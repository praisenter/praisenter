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
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.SingleFileManager;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.data.workspace.Workspaces;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.fonts.OpenIconic;
import org.praisenter.ui.themes.Theme;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.upgrade.InstallUpgradeHandler;
import org.praisenter.utility.RuntimeProperties;

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
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public final class StartupHandler {
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
	
	private static final int MIN_WIDTH = 1000;
	private static final int MIN_HEIGHT = 700;
	
	public void restart(GlobalContext context) throws IOException {
		this.restart(context, null);
	}
	
	public void restart(GlobalContext context, Path workspacePath) throws IOException {
		Stage stage = context.stage;
		Application application = context.application;
		
		// we can use the current log configuration
		final Logger LOGGER = LogManager.getLogger();

		WorkspaceConfiguration workspaceConfiguration = context.getConfiguration();
		
		// remove window tracking bindings
		workspaceConfiguration.applicationMaximizedProperty().unbind();
		workspaceConfiguration.applicationXProperty().unbind();
		workspaceConfiguration.applicationYProperty().unbind();
		workspaceConfiguration.applicationWidthProperty().unbind();
		workspaceConfiguration.applicationHeightProperty().unbind();
		
		// try to save the current configuration
		LOGGER.info("Restarting the application.");
		LOGGER.info("Saving the configuration.");
		context.workspaceManager.saveWorkspaceConfiguration().exceptionally((e) -> {
			// if it fails to save, just move on
			LOGGER.warn("Failed to save configuration: " + e.getMessage() + ". Ignoring and continuing.", e);
			return null;
		}).thenRun(() -> {
			// then wait for any in-process stuff to complete
			LOGGER.info("Waiting for any unfinished jobs.");
			if (!ForkJoinPool.commonPool().awaitQuiescence(60, TimeUnit.SECONDS)) {
				LOGGER.warn("All jobs didn't complete in time. Ignoring and continuing.");
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			LOGGER.debug("Cleaning up current context.");
			
			// dispose the context - this will do all sorts of things like
			// removing resources, unbinding, listener removal, etc.
			context.dispose();
			
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
    			Alert a = Alerts.exception(
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
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon16x16alt.png"), 16, 16, true, true));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon32x32.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon48x48.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon64x64.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon96x96.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon128x128.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon256x256.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon512x512.png")));

		// load fonts
    	LOGGER.info("Loading glyph fonts.");
    	GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/fontawesome-webfont.ttf")));
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/open-iconic.ttf")));

		// load the workspaces file
		LOGGER.info("Reading workspaces file.");
		Path path = Paths.get(Constants.ROOT_PATH, "workspaces.json");
		SingleFileManager<Workspaces> fm = SingleFileManager.open(path, Workspaces.class, new Workspaces());
		
		CompletableFuture<Optional<Path>> workspacePathFuture = null;
		if (workspacePath == null) {
			LOGGER.info("Prompting for workspace selection.");
			WorkspaceSelectorPane wss = new WorkspaceSelectorPane(fm);
			
			// NOTE: we're using the main stage here for workspace selection because
			// using anything else doesn't block the application from closing
			Scene scene = new Scene(wss);
			
			// just use the default theme
			scene.getStylesheets().add(Theme.getAvailableThemes().get(0).getCss());
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
	    	LOGGER.info("Workspace '" + wsp.toAbsolutePath()+ "' was selected.  Opening...");
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
    		// save the workspaces config file with the new
    		// last workspace opened and if there's a new workspace
    		String wsp = workspaceManager.getWorkspacePathResolver().getBasePath().toAbsolutePath().toString();
    		fm.getData().setLastSelectedWorkspace(wsp);
    		fm.getData().getWorkspaces().add(wsp);
    		return fm.saveData().thenApply((v) -> {
    			return workspaceManager;
    		});
    	}).thenApply((workspaceManager) -> {
    		// finally build the context
    		return new GlobalContext(
    				application, 
    				stage, 
    				workspaceManager);
    	}).thenApply((context) -> {
    		// configure the log4j logger to go to the workspace logs folder
			Path logsPath = context.getWorkspaceManager().getWorkspacePathResolver().getLogsPath();
			LOGGER.info("Configuring log4j to point to '" + logsPath + "'");
			
        	// https://stackoverflow.com/questions/58973751/how-to-reconfigure-log4j2-at-runtime-with-new-xml-file
        	System.setProperty("praisenter.logs.dir", logsPath.toAbsolutePath().toString());
        	LoggerContext.getContext(false).reconfigure();
			
    		WorkspaceConfiguration configuration = context.getConfiguration();
    		
    		// TODO this should work, but needs to be tested
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
    		WorkspaceConfiguration configuration = context.getConfiguration();
    		
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
    			LOGGER.debug("Attempt to save configuration before exit.");
    			this.onCloseRequest(LOGGER, context).thenRun(() -> {
    				LOGGER.debug("Closing application");
    				Platform.runLater(() -> {
    					stage.close();
    				});
    			});
    		});
    		stage.setOnHidden((e) -> {
    			Platform.exit();
    		});
    		
    		// build the loading UI
    		LoadingPane loadingPane = new LoadingPane(context, installer);
    		StackPane layout = new StackPane(loadingPane);
    		
    		Scene mainScene = new Scene(layout);
    		mainScene.getStylesheets().add(Theme.getTheme(configuration.getThemeName()).getCss());
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
	
    private void showExecptionAlertThenExit(Logger LOGGER, Throwable ex, Window owner) {
		Platform.runLater(() -> {
			// and show the error
			Alert a = Alerts.exception(owner, ex);
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
    
    private CompletableFuture<Void> onCloseRequest(Logger LOGGER, GlobalContext context) {
    	// close the presentation screens
		context.getDisplayManager().dispose();
		
    	// wait for any pending async tasks
    	// NOTE: the assumption here is that all asynchronous processing is being performed on the ForkJoinPool commonPool
    	// (the default for CompletableFuture)
    	if (!ForkJoinPool.commonPool().awaitQuiescence(60, TimeUnit.SECONDS)) {
    		LOGGER.warn("Waited 60 seconds for tasks to complete, but they didn't.");
    		// TODO need to prompt user to wait longer or just exit
    	}
    	
		// save some application info
		LOGGER.debug("Saving application location and size: ({}, {}) {}x{} isMaximized={}", 
				context.getConfiguration().getApplicationX(), 
				context.getConfiguration().getApplicationY(), 
				context.getConfiguration().getApplicationWidth(), 
				context.getConfiguration().getApplicationHeight(),
				context.getConfiguration().isApplicationMaximized());
		
		return context.saveConfiguration().exceptionally((ex) -> {
			LOGGER.warn("Failed to save application x,y and width,height when the application closed.", ex);
			return null;
		});
    }
}
