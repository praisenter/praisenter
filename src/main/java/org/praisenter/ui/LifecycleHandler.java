package org.praisenter.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.SingleFileManager;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.data.workspace.Workspaces;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.themes.StyleSheets;
import org.praisenter.ui.themes.Theming;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.upgrade.InstallUpgradeHandler;
import org.praisenter.ui.upgrade.UpgradeChecker;
import org.praisenter.utility.RuntimeProperties;

import atlantafx.base.theme.PrimerDark;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public final class LifecycleHandler {
	// FEATURE (L-H) We should look at making some of the Java FX features "optional" instead of required
	/** The array of Java FX features that Praisenter uses */
	private static final ConditionalFeature[] REQUIRED_JAVAFX_FEATURES = new ConditionalFeature[] {
		ConditionalFeature.TRANSPARENT_WINDOW,
		ConditionalFeature.GRAPHICS,
		ConditionalFeature.SHAPE_CLIP,
		ConditionalFeature.CONTROLS,
		ConditionalFeature.MEDIA,
		ConditionalFeature.EFFECT
	};
	
	private static final int MIN_WIDTH = 1200;
	private static final int MIN_HEIGHT = 800;
	
	public void restart(GlobalContext context) {
		this.restart(context, null);
	}
	
	public void restart(GlobalContext context, Path workspacePath) {
		Stage stage = context.stage;
		Application application = context.application;
		
		// we can use the current log configuration
		final Logger LOGGER = LogManager.getLogger();

		// go though all the context clean process
		this.cleanUp(LOGGER, context).thenCompose(AsyncHelper.onJavaFXThreadAndWait((shouldContinue) -> {
			if (shouldContinue) {
				// clean up the stage
				LOGGER.debug("Resetting stage.");
				stage.setOnCloseRequest(null);
				stage.setOnHidden(null);
	        	stage.setMinWidth(0);
	        	stage.setMinHeight(0);
				stage.setTitle("");
				stage.getIcons().clear();
				
				// start the app
				LOGGER.info("Starting the application.");
				this.start(application, stage, workspacePath);
			}
		})).exceptionally((e) -> {
			// log it
			LOGGER.error("Failed to restart the application: " + e.getMessage(), e);
			
			// if we fail, just exit the application
			Platform.exit();
			return null;
		});
	}
	
	public void start(Application application, Stage stage) throws IOException {
		this.start(application, stage, null);
	}
	
	public void start(Application application, Stage stage, Path workspacePath) throws IOException {
		long startTime = System.nanoTime();

    	// the very first step is to do any install initialization or 
    	// upgrade operations
    	InstallUpgradeHandler installer = new InstallUpgradeHandler();
    	installer.initialize();
    	
    	// set system props for log4j
    	System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
    	System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, Paths.get(Constants.ROOT_PATH, Constants.LOGS_CONFIGURATION_FILENAME).toAbsolutePath().toString());
    	LoggerContext.getContext(false).reconfigure();
		
    	// create a logger for this class after the log4j has been initialized
    	final Logger LOGGER = LogManager.getLogger();
    	
		// log some system info
    	LOGGER.info(Constants.NAME + " v" + Version.STRING);
    	LOGGER.info("OS:        " + (RuntimeProperties.IS_WINDOWS_OS ? "[Windows] " : RuntimeProperties.IS_MAC_OS ? "[MacOS] " : RuntimeProperties.IS_LINUX_OS ? "[Linux] " : "[Other] ") + RuntimeProperties.OPERATING_SYSTEM + " " + RuntimeProperties.ARCHITECTURE);
    	LOGGER.info("Java:      " + RuntimeProperties.JAVA_VERSION + " " + RuntimeProperties.JAVA_VENDOR);
    	LOGGER.info("JVM Args:  " + RuntimeProperties.JVM_ARGUMENTS);
    	LOGGER.info("Java Home: " + RuntimeProperties.JAVA_HOME);
    	LOGGER.info("User Home: " + RuntimeProperties.USER_HOME);
    	LOGGER.info("Locale:    " + Locale.getDefault().toLanguageTag());
    	
    	// log supported features
    	LOGGER.info("Java FX Feature Support:");
    	for (ConditionalFeature feature : ConditionalFeature.values()) {
    		LOGGER.info(feature.name() + "=" + Platform.isSupported(feature));
    	}
    	
    	// verify features
    	LOGGER.info("Verifying required Java FX features.");
    	for (ConditionalFeature feature : REQUIRED_JAVAFX_FEATURES) {
    		if (!Platform.isSupported(feature)) {
    			// not supported, attempt to show the user an error message
    			Alert a = Dialogs.exception(
    					stage.getOwner(),
    					Translations.get("init.feature.missing.title"), 
    					Translations.get("init.feature.missing.header"), 
    					feature.name());
    			a.showAndWait();
    			LOGGER.info("User closed exception dialog. Exiting application.");
    			Platform.exit();
    		}
    	}
    	LOGGER.info("Required features present.");
    	
    	// title
    	stage.setTitle(Constants.NAME + " " + Version.STRING);
    	
    	// icons
    	WindowHelper.setIcons(stage);

		// load the workspaces file
		LOGGER.info("Reading workspaces file.");
		Path path = Paths.get(Constants.ROOT_PATH, "workspaces.json");
		SingleFileManager<Workspaces> fm = SingleFileManager.open(path, Workspaces.class, new Workspaces());
		
		CompletableFuture<Optional<Path>> workspacePathFuture = null;
		if (workspacePath == null) {
			LOGGER.info("Prompting for workspace selection.");
			WorkspaceSelectionPane wss = new WorkspaceSelectionPane(fm);
			
			// NOTE: we're using the main stage here for workspace selection because
			// using anything else doesn't block the application from closing
			Scene scene = new Scene(wss);
			
			// use the styles
			StyleSheets.apply(scene);
			
			// setup the scene
			stage.setScene(scene);
			stage.sizeToScene();
			stage.show();
			stage.centerOnScreen();
			
			workspacePathFuture = wss.getSelectedWorkspace();
		} else {
			LOGGER.info("Workspace path already given - no prompt necessary");
			workspacePathFuture = CompletableFuture.completedFuture(Optional.of(workspacePath));
		}
		
		workspacePathFuture.thenApplyAsync((owp) -> {
			// get the workspace path
			LOGGER.info("Workspace selection complete.");
			
			// if they don't choose anything, then just exit
			if (owp.isEmpty()) {
				LOGGER.info("No workspace was chosen, exiting...");
				// exit the application
				throw new CompletionException(new NoWorkspaceSelectedException());
			}
			
	    	final Path wsp = owp.get();
	    	LOGGER.info("Workspace '" + wsp.toAbsolutePath() + "' was selected.  Opening...");
	    	return wsp;
		}).thenApplyAsync((wsp) -> {
			// open the workspace
    		try {
    			// get all the other workspaces
    			Set<Path> otherWorkspaces = fm.getData().getWorkspaces()
    					.stream()
    					.map(s -> Paths.get(s))
    					.filter(s -> !s.equals(wsp))
    					.collect(Collectors.toSet());
    			
    			// build the workspace manager
    			WorkspaceManager wsm = WorkspaceManager.open(wsp, otherWorkspaces);
    			LOGGER.info("Workspace '" + wsp.toAbsolutePath()+ "' was opened successfully.");
    			return wsm;
    		} catch (Exception ex) {
    			LOGGER.error("Failed to open the workspace at '" + wsp.toAbsolutePath() + "': " + ex.getMessage(), ex);
    			throw new CompletionException(ex);
    		}
    	}).thenCompose((workspaceManager) -> {
    		LOGGER.info("Saving workspaces configuration file.");
    		// save the workspaces config file with the new
    		// last workspace opened and if there's a new workspace
    		String wsp = workspaceManager.getWorkspacePathResolver().getBasePath().toAbsolutePath().toString();
    		fm.getData().setLastSelectedWorkspace(wsp);
    		fm.getData().getWorkspaces().add(wsp);
    		return fm.saveData().thenApply((v) -> {
    			return workspaceManager;
    		});
    	}).thenApply((workspaceManager) -> {
    		LOGGER.info("Building the global context.");
    		// finally build the context
    		return new GlobalContext(
    				application, 
    				stage, 
    				workspaceManager);
    	}).thenApply((context) -> {
    		LOGGER.info("Asynchonously checking for latest Praisenter version.");
    		// NOTE: don't wait on this - it can complete asynchronously
    		UpgradeChecker checker = new UpgradeChecker();
    		checker.getLatestReleaseVersion().thenCompose(AsyncHelper.onJavaFXThreadAndWait((nv) -> {
    			LOGGER.info("Latest version of praisenter retrieved (trigged by startup): " + nv);
    			context.setLatestVersion(nv);
    		})).exceptionally((t) -> {
    			LOGGER.warn("failed to check for the latest version: " + t.getMessage(), t);
    			return null;
    		});
    		
    		return context;
    	}).thenApply((context) -> {
    		// configure the log4j logger to go to the workspace logs folder
			Path logsPath = context.getWorkspaceManager().getWorkspacePathResolver().getLogsPath();
			LOGGER.info("Configuring log4j to point to '" + logsPath + "'");
			
        	// https://stackoverflow.com/questions/58973751/how-to-reconfigure-log4j2-at-runtime-with-new-xml-file
        	System.setProperty("praisenter.logs.dir", logsPath.toAbsolutePath().toString());
        	LoggerContext.getContext(false).reconfigure();
			
    		WorkspaceConfiguration configuration = context.getWorkspaceConfiguration();
    		
    		// set the language if in the config
        	String languageTag = configuration.getLanguageTag();
        	if (languageTag != null) {
        		Locale locale = Locale.forLanguageTag(languageTag);
        		if (locale != null) {
        			LOGGER.info("Setting the default language to '" + locale + "'.");
    				Locale.setDefault(locale);
        		}
        	}
        	
    		// check for debug mode
    		if (configuration.isDebugModeEnabled()) {
    			// if enabled, change the log level to TRACE
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
    			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
    			loggerConfig.setLevel(Level.TRACE);
    			ctx.updateLoggers();
    		}
    		
    		return context;
    	}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((context) -> {
    		WorkspaceConfiguration configuration = context.getWorkspaceConfiguration();
    		
    		stage.hide();
    		
    		// set the widow size and position
    		// set minimum size
        	stage.setMinWidth(MIN_WIDTH);
        	stage.setMinHeight(MIN_HEIGHT);
        	
        	// read stored position/size
        	double x = configuration.getApplicationX();
    		double y = configuration.getApplicationY();
    		double w = configuration.getApplicationWidth();
    		double h = configuration.getApplicationHeight();
        	
    		// clamp the size
    		if (w < MIN_WIDTH) w = MIN_WIDTH;
    		if (h < MIN_HEIGHT) h = MIN_HEIGHT;
    		
    		// set the size
    		stage.setWidth(w);
        	stage.setHeight(h);
    		
    		// set position, but only if
    		// we are in the bounds of a screen
    		// if not, we'll rely on Java FX to place
    		// the window a the default location
    		if (this.isInScreenBounds(x, y, w, h)) {
    			stage.setX(x);
    			stage.setY(y);
    		}
    		
    		// check for maximized
    		if (configuration.isApplicationMaximized()) {
    			stage.setMaximized(true);
    		}

    		// bind the maximized property
    		configuration.applicationMaximizedProperty().bind(stage.maximizedProperty());
    		
    		// bind the x, y, width, height - but only if we're not maximized - this allows
    		// us to "remember" the size/location of the window before they maximized it and
    		// go back to that after a restart
    		configuration.applicationXProperty().bind(Bindings.when(stage.maximizedProperty().not()).then(stage.xProperty()).otherwise(configuration.getApplicationX()));
			configuration.applicationYProperty().bind(Bindings.when(stage.maximizedProperty().not()).then(stage.yProperty()).otherwise(configuration.getApplicationY()));
			configuration.applicationWidthProperty().bind(Bindings.when(stage.maximizedProperty().not()).then(stage.widthProperty()).otherwise(configuration.getApplicationWidth()));
			configuration.applicationHeightProperty().bind(Bindings.when(stage.maximizedProperty().not()).then(stage.heightProperty()).otherwise(configuration.getApplicationHeight()));
    		
    		stage.setOnCloseRequest((e) -> {
    			LOGGER.debug("Request to close the stage received.");
    			e.consume();
    			this.cleanUp(LOGGER, context).thenAccept((shouldContinue) -> {
    				if (shouldContinue) {
    					Platform.runLater(() -> {
    						// the application should exit at this point since the
    						// stage has an onhiding event to call Platform.exit()
    						// that said, Java FX should shutdown anyway when the 
    						// last stage has closed
    						context.stage.close();
    					});
    				}
    			}).exceptionally((t) -> {
    				Platform.runLater(() -> {
    					Alert alert = Dialogs.exception(context.stage, t);
    					alert.show();
    				});
    				return null;
    			});
    		});
    		stage.setOnHidden((e) -> {
    			Platform.exit();
    		});
    		
    		// build the loading UI
    		LoadingPane loadingPane = new LoadingPane(context, installer);
    		StackPane layout = new StackPane(loadingPane);
    		Scene mainScene = new Scene(layout);
    		
    		// default the theme
    		LOGGER.info("Setting up the current theme");
    		var theme = Theming.getTheme(configuration.getThemeName());
    		if (theme != null) {
    			LOGGER.info("Setting theme to '" + theme.getTheme().getName() + "'");
    			Application.setUserAgentStylesheet(theme.getTheme().getUserAgentStylesheet());
    		} else {
    			// default to PrimerDark
    			Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
    		}

    		// use the styles
    		LOGGER.info("Loading custom stylesheets");
			StyleSheets.apply(mainScene);
			
    		// set the accent color
			LOGGER.info("Setting up the current accent color");
    		var accent = Theming.getAccent(configuration.getAccentName());
    		if (accent != null && !accent.getPseudoClass().getPseudoClassName().startsWith("p-color-accent-default")) {
    			LOGGER.info("Setting accent colors to '" + accent.getPseudoClass().getPseudoClassName() + "'");
    			layout.pseudoClassStateChanged(accent.getPseudoClass(), true);
    		}
    		
    		stage.setScene(mainScene);
    		stage.show();
    		
    		LOGGER.info("Starting load");
    		loadingPane.start().thenAccept((ui) -> {
    			Platform.runLater(() -> {
    				try {
	    	    		layout.getChildren().add(0, ui);
	    	    		
	    				// fade out the loader
	    				FadeTransition fade = new FadeTransition(Duration.millis(600), loadingPane);
	    				fade.setFromValue(1.0);
	    				fade.setToValue(0.0);
	    				
	    				// we'll pause for a moment to let the user see it completed
	    				SequentialTransition seq = new SequentialTransition(new PauseTransition(Duration.millis(1500)), fade);
	    				seq.setAutoReverse(false);
	    				seq.setCycleCount(1);
	    				
	    				// when the fade out is complete
	    				seq.statusProperty().addListener((fadeStatus, oldFadeStatus, newFadeStatus) -> {
	    					if (newFadeStatus == Animation.Status.STOPPED) {
	    						LOGGER.info("Fade out of loading screen complete.");
	    						layout.getChildren().remove(loadingPane);
	    						LOGGER.info("Loading scene removed from the scene graph. {} node(s) remaining.", layout.getChildren().size());
	    						final long endTime = System.nanoTime();
	    						LOGGER.info("Loading completed in {} seconds.", (endTime - startTime) / 1e9);
	    					}
	    				});
	    				
	    				// play the fade out
	    				seq.play();
    				} catch (Exception ex) {
    					LOGGER.error(ex.getMessage(), ex);
    					this.showExecptionAlertThenExit(LOGGER, ex, stage);
    				}
    			});
    		}).exceptionally((ex) -> {
    			this.showExecptionAlertThenExit(LOGGER, ex, stage);
    			return null;
    		});
    	})).exceptionally((ex) -> {
    		if (ex.getCause() instanceof NoWorkspaceSelectedException) {
    			Platform.exit();
    		} else {
    			this.showExecptionAlertThenExit(LOGGER, ex, stage);
    		}
    		return null;
    	});
	}
	
	public void stop(GlobalContext context) {
		// we can use the current log configuration
		final Logger LOGGER = LogManager.getLogger();
		
		// go through the shutdown proceedure
		this.cleanUp(LOGGER, context).thenAccept((shouldContinue) -> {
			if (shouldContinue) {
				Platform.runLater(() -> {
					// the application should exit at this point since the
					// stage has an onhiding event to call Platform.exit()
					// that said, Java FX should shutdown anyway when the 
					// last stage has closed
					context.stage.close();
				});
			}
		}).exceptionally((t) -> {
			Platform.runLater(() -> {
				Alert alert = Dialogs.exception(context.stage, t);
				alert.show();
			});
			return null;
		});;
	}
	
    private void showExecptionAlertThenExit(Logger LOGGER, Throwable ex, Window owner) {
		Platform.runLater(() -> {
			// and show the error
			Alert a = Dialogs.exception(owner, ex);
			a.showAndWait();
			
			LOGGER.info("User closed exception dialog. Exiting application.");
			
			// then exit the app
			Platform.exit();
		});
	}

    private boolean isInScreenBounds(double x, double y, double w, double h) {
    	Rectangle2D bounds = new Rectangle2D(x, y, w, h);
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D sb = screen.getBounds();
            if (bounds.intersects(sb)) {
            	return true;
            }
        }
        
        return false;
    }
    
    private CompletableFuture<Boolean> promptUnsavedChanges(Logger LOGGER, GlobalContext context) {
    	LOGGER.info("Checking for unsaved changes.");
		Optional<Boolean> unsavedChanges = context.getOpenDocumentsUnmodifiable().stream().map(d -> d.hasUnsavedChanges()).reduce((a, b) -> a || b); 
		if (unsavedChanges.isPresent() && unsavedChanges.get()) {
			LOGGER.info("Unsaved documents are present. Prompting for next step.");
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(Translations.get("workspace.close.unsaved.title"));
            alert.setHeaderText(Translations.get("workspace.close.unsaved.header"));
            alert.setContentText(Translations.get("workspace.close.unsaved.content"));
            ButtonType save = new ButtonType(Translations.get("workspace.close.unsaved.save"), ButtonData.YES);
            ButtonType discard = new ButtonType(Translations.get("workspace.close.unsaved.discard"), ButtonData.NO);
            ButtonType cancel = new ButtonType(Translations.get("cancel"), ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(save, discard, cancel);
            alert.initOwner(context.stage);

            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isEmpty()) {
            	// don't continue, they closed the window
            	LOGGER.info("User closed prompt, cancelling the close request / workspace switch");
            	return CompletableFuture.completedFuture(false);
            } else if (result.get() == save){
            	LOGGER.info("Saving unsaved documents");
            	return context.saveAll().thenApply(v -> true);
            } else if (result.get() == discard) {
            	LOGGER.info("User chose to discard all changes");
                return CompletableFuture.completedFuture(true);
            } else {
            	// they cancelled the window
            	LOGGER.info("User cancelled the operation, cancelling the close request / workspace switch");
            	return CompletableFuture.completedFuture(false);
            }
		}
    	
		LOGGER.info("No unsaved changes detected.");
		return CompletableFuture.completedFuture(true);
    }
    
    private CompletableFuture<Boolean> waitForAsyncTaskCompletion(Logger LOGGER) {
    	LOGGER.info("Waiting for any pending async tasks to complete");
    	// wait for any pending async tasks
    	// NOTE: the assumption here is that all asynchronous processing is being performed on the ForkJoinPool commonPool
    	// (the default for CompletableFuture)
    	if (!ForkJoinPool.commonPool().awaitQuiescence(60, TimeUnit.SECONDS)) {
    		LOGGER.warn("Waited 60 seconds for tasks to complete, but they didn't.");
    		// TODO need to prompt user to wait longer or just exit
    	}
    	
    	return CompletableFuture.completedFuture(true);
    }
    
    private CompletableFuture<Boolean> saveWorkspaceConfiguration(Logger LOGGER, GlobalContext context) {
    	// save some application info
		LOGGER.debug("Saving application location and size: ({}, {}) {}x{} isMaximized={}", 
				context.getWorkspaceConfiguration().getApplicationX(), 
				context.getWorkspaceConfiguration().getApplicationY(), 
				context.getWorkspaceConfiguration().getApplicationWidth(), 
				context.getWorkspaceConfiguration().getApplicationHeight(),
				context.getWorkspaceConfiguration().isApplicationMaximized());
		
		return context.saveConfiguration().thenApply((v) -> true);
    }
    
    private CompletableFuture<Boolean> disposeContext(Logger LOGGER, GlobalContext context) {
    	return AsyncHelper.onJavaFXThreadAndWait((v) -> {
    		LOGGER.info("Disposing of the context");
    		context.dispose();
    	}).apply(null).exceptionally((e) -> {
    		LOGGER.warn("Failed to dispose of the context: " + e.getMessage(), e);
    		return null;
    	}).thenApply((v) -> true);
    }
    
    private CompletableFuture<Boolean> cleanUp(Logger LOGGER, GlobalContext context) {
    	// prompt for unsaved changes
		return promptUnsavedChanges(LOGGER, context).exceptionally((t) -> {
			// show an error and don't continue
			Platform.runLater(() -> {
				Alert alert = Dialogs.exception(context.stage, t);
				alert.show();
			});
			return false;
		}).thenCompose((shouldContinue) -> {
			// check if we should continue or not
			if (shouldContinue) {
				return this.waitForAsyncTaskCompletion(LOGGER);
			}
			return CompletableFuture.completedStage(false);
		}).thenCompose((shouldContinue) -> {
			if (shouldContinue) {
				return this.saveWorkspaceConfiguration(LOGGER, context);
			}
			return CompletableFuture.completedStage(false);
		}).thenCompose((shouldContinue) -> {
			if (shouldContinue) {
				return this.disposeContext(LOGGER, context);
			}
			return CompletableFuture.completedStage(false);
		});
    }
}
