/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.ui;

import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.configuration.ConfigurationPersistAdapter;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.fonts.OpenIconic;
import org.praisenter.ui.themes.Theme;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.RuntimeProperties;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

// FIXME fix the manifest
// FIXME explore deployment options
// FIXME testing on High DPI screens
// FIXME fix dark theme

// FEATURE (L-M) Evaluate detecting text language for better indexing (a field per language) in lucene; Apache Tika or LangDetect; This would also be used in the searches to know what indexed fields to use
// FEATURE (L-M) Use Apache POI to read powerpoint files
// FEATURE (M-L) Evaluate alternate JavaFX styles here https://github.com/JFXtras/jfxtras-styles
// FEATURE (H-L) Quick send to display - any place in the app when the context contains something that could be displayed offer a Quick Display button to allow the user to quickly get it shown - with configurable settings
// FEATURE (M-L) From selected media items, generate slides or slide show
// FEATURE (H-H) Auto-update feature

// a. Generate a self-signed public/private key pair
// b. Generate a signature for the version-check.json and install.jar files

// Upon start up
// 0. Check for install.jar and install.jar.sig
// 0a. If present perform verification again
// 0b. If verified execute install.jar
// 0c. Either way delete all update files
// 0c. When complete continue startup

// After startup and in the background
// 1. Download {url}/v/{current-version}/version-check.json
// 2. Download {url}/v/{current-version}/version-check.json.sig https://docs.oracle.com/javase/tutorial/security/apisign/step3.html
// 3. Verify the signature of the version-check.json with the version-check.json.sig and the public key
// 4. If it does not pass
//		a. Ignore update and notify user
//		b. Clean up files
// 5. If it passes
//		a. Check for a new version
//			i.  If we are on the current version then stop
//			ii. Otherwise
// 				1. Download {url}/v/{current-version}/install.jar
// 				2. Download {url}/v/{current-version}/install.jar.sig
// 				3. Verify the signature of the install.jar with the install.jar.sig and the public key
//				4. If it does not pass
//					a. Ignore update and notify user
//					b. clean up files
//				5. If it passes
//					a. Notify the user of the update with option to restart

// JAVABUG (M) 09/28/16 [fixed-9] High DPI https://bugs.openjdk.java.net/browse/JDK-8091832
// JAVABUG (M) 11/03/16 [fixed-9] Editable ComboBox and Spinner auto commit - https://bugs.openjdk.java.net/browse/JDK-8150946
// JAVABUG (L) 05/31/17 Java FX just chooses the last image in the set of stage icons rather than choosing the best bugs.openjdk.java.net/browse/JDK-8091186, bugs.openjdk.java.net/browse/JDK-8087459

/**
 * This is the entry point for the application.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Praisenter extends Application {
	/** The class-level logger */
	private static final Logger LOGGER;
	
	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		// set the log4j configuration file path
		// TODO this will need to change so we can write the file on first start up
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
		
		// create a logger for this class after the log4j has been initialized
		LOGGER = LogManager.getLogger();
    	
		// log some system info
    	LOGGER.info(Constants.NAME + " " + Constants.VERSION);
    	LOGGER.info("OS:        " + (RuntimeProperties.IS_WINDOWS_OS ? "[Windows] " : RuntimeProperties.IS_MAC_OS ? "[MacOS] " : RuntimeProperties.IS_LINUX_OS ? "[Linux] " : "[Other] ") + RuntimeProperties.OPERATING_SYSTEM + " " + RuntimeProperties.ARCHITECTURE);
    	LOGGER.info("Java:      " + RuntimeProperties.JAVA_VERSION + " " + RuntimeProperties.JAVA_VENDOR);
    	LOGGER.info("JVM Args:  " + RuntimeProperties.JVM_ARGUMENTS);
    	LOGGER.info("Java Home: " + RuntimeProperties.JAVA_HOME);
    	LOGGER.info("User Home: " + RuntimeProperties.USER_HOME);
    	LOGGER.info("Locale:    " + Locale.getDefault().toLanguageTag());
	}

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
	
	/** The default width */
	private static final int MIN_WIDTH = 1000;
	
	/** The default height */
	private static final int MIN_HEIGHT = 700;
	
	/**
	 * The entry point method.
	 * @param args any arguments
	 */
    public static void main(String[] args) {
        launch(args);
    }
    
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {
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
    	stage.setTitle(Constants.NAME + " " + Constants.VERSION);
    	
    	// icons
    	stage.getIcons().add(new Image("org/praisenter/logo/icon16x16alt.png", 16, 16, true, true));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon32x32.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon48x48.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon64x64.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon96x96.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon128x128.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon256x256.png"));
    	stage.getIcons().add(new Image("org/praisenter/logo/icon512x512.png"));
    	
		// load fonts
    	LOGGER.info("Loading glyph fonts.");
    	GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/fontawesome-webfont.ttf")));
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/open-iconic.ttf")));

    	// initialize lucene
		final FSDirectory directory = FSDirectory.open(Paths.get(Constants.SEARCH_INDEX_ABSOLUTE_PATH));
		final Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		
		// initialize the data manager
    	final SearchIndex index = new SearchIndex(directory, analyzer);
    	DataManager dataManager = new DataManager(index);
    	
    	// Step 1: load configuration or default it
    	this.loadConfiguration(dataManager).thenApply((configuration) -> {
    		// finally build the context
    		return new GlobalContext(
    				this, 
    				stage, 
    				dataManager, 
    				configuration);
    	}).thenApply((context) -> {
    		// set the language if in the config
        	String languageTag = context.configuration.getLanguageTag();
        	if (languageTag != null) {
        		Locale locale = Locale.forLanguageTag(languageTag);
        		if (locale != null) {
        			LOGGER.info("Setting the default language to '" + locale + "'.");
    				Locale.setDefault(locale);
        		}
        	}
        	
    		// check for debug mode
    		if (context.configuration.isDebugModeEnabled()) {
    			// if enabled, change the log level to TRACE
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
    			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
    			loggerConfig.setLevel(Level.TRACE);
    			ctx.updateLoggers();
    		}
    		
    		return context;
    	}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((context) -> {    		
    		// set the widow size and position
    		// set minimum size
        	stage.setMinWidth(MIN_WIDTH);
        	stage.setMinHeight(MIN_HEIGHT);
        	
        	// read stored position/size
        	double x = context.configuration.getApplicationX();
    		double y = context.configuration.getApplicationY();
    		double w = context.configuration.getApplicationWidth();
    		double h = context.configuration.getApplicationHeight();
        	
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
    		if (context.configuration.isApplicationMaximized()) {
    			stage.setMaximized(true);
    		}
    		
    		stage.setOnCloseRequest((e) -> {
    			LOGGER.debug("Request to close the stage received.");
    			e.consume();
    			LOGGER.debug("Attempt to save configuration before exit.");
    			this.onCloseRequest(context).thenRun(() -> {
    				LOGGER.debug("Closing application");
    				Platform.runLater(() -> {
    					stage.close();
    				});
    			});
    		});
    		stage.setOnHiding((e) -> {
    			Platform.exit();
    		});
    		
    		// add bindings
    		context.configuration.applicationXProperty().bind(stage.xProperty());
    		context.configuration.applicationYProperty().bind(stage.yProperty());
    		context.configuration.applicationWidthProperty().bind(stage.widthProperty());
    		context.configuration.applicationHeightProperty().bind(stage.heightProperty());
    		context.configuration.applicationMaximizedProperty().bind(stage.maximizedProperty());
    		
    		// when debug mode changes, update the log level
    		context.configuration.debugModeEnabledProperty().addListener((obs, ov, nv) -> {
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
    			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    			if (nv) {
	    			loggerConfig.setLevel(Level.TRACE);
				} else {
					loggerConfig.setLevel(Level.INFO);
				}
    			ctx.updateLoggers();
    		});
    		    		
    		// build the loading UI
    		LoadingPane loadingPane = new LoadingPane(context);
    		StackPane layout = new StackPane(loadingPane);
    		
    		Scene scene = new Scene(layout);
    		scene.getStylesheets().add(Theme.getTheme(context.configuration.getThemeName()).getCss());
    		stage.setScene(scene);
    		stage.show();
    		
    		LOGGER.info("Starting load");
    		loadingPane.start().thenRun(() -> {
    			Platform.runLater(() -> {
    				// TODO fix missing slide thumbnails?
    				// TODO setup display manager
//    				// generate any missing slide thumbnails
//    				LOGGER.info("Generating missing slide thumbnails.");
//    				context.getSlideLibrary().generateMissingThumbnails();
//    				
//    				// setup the screen manager
//    				LOGGER.info("Initializing the screen manager.");
//    				context.getDisplayManager().initialize(context.getJavaFXContext().getStage().getScene());
    				
    				// load fonts
    				LOGGER.info("Loading fonts.");
    				List<String> families = Font.getFamilies();
    				Font.getFontNames();
    				// to improve performance of font pickers, we need to preload
    				// the fonts by creating a font for each one
    				for (String family : families) {
    					Font.font(family);
    				}
    				LOGGER.info("Fonts loaded.");
    				
    				LOGGER.info("Load complete");
    				
    				LOGGER.info("Building UI");
//    	    		Label label = new Label();
//    	    		label.textProperty().bind(Bindings.createStringBinding(() -> {
//    	    			StringBuilder sb = new StringBuilder();
//    	    			sb.append("(").append(context.configuration.getApplicationX()).append(", ").append(context.configuration.getApplicationY()).append(") ")
//    	    			  .append(context.configuration.getApplicationWidth()).append("x").append(context.configuration.getApplicationHeight());
//    	    			return sb.toString();
//    	    		}, context.configuration.applicationXProperty(),
//    					context.configuration.applicationYProperty(),
//    					context.configuration.applicationWidthProperty(),
//    					context.configuration.applicationHeightProperty()));
    	    		
    	    		PraisenterPane main = new PraisenterPane(context);
    	    		
    	    		// TODO replace with main UI
    	    		layout.getChildren().add(0, main);
    	    		
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
    						long endTime = System.nanoTime();
//    						LOGGER.info("Loading took {} seconds.", (endTime - START_TIME) / 1e9);
    					}
    				});
    				
    				// play the fade out
    				seq.play();
    			});
    		}).exceptionally((ex) -> {
    			this.showExecptionAlertThenExit(ex, stage);
    			return null;
    		});
    	})).exceptionally((ex) -> {
    		this.showExecptionAlertThenExit(ex, stage);
    		return null;
    	});
    }
    
    private void showExecptionAlertThenExit(Throwable ex, Window owner) {
		Platform.runLater(() -> {
			// and show the error
			Alert a = Alerts.exception(
					owner,
					Translations.get("init.error.title"), 
					Translations.get("init.error.header"), 
					ex.getMessage(), 
					ex);
			a.showAndWait();
			
			LOGGER.info("User closed exception dialog. Exiting application.");
			
			// then exit the app
			Platform.exit();
		});
	}
    
    private CompletableFuture<Configuration> loadConfiguration(DataManager dataManager) {
    	return dataManager.registerPersistAdapter(Configuration.class, new ConfigurationPersistAdapter(Paths.get(Constants.CONFIG_ABSOLUTE_PATH))).exceptionally((ex) -> {
    		LOGGER.error("Failed to load configuration: ", ex);
    		return null;
    	}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((a) -> {
    		LOGGER.info("Loading configuration.");
    		List<Configuration> configs = dataManager.getItems(Configuration.class);
    		if (configs.isEmpty()) {
    			return null;
    		}
    		return configs.get(0);
    	})).exceptionally((ex) -> {
    		LOGGER.error("", ex);
    		return null;
    	}).thenCompose((configuration) -> {
    		// just return the config if it's not null
        	CompletableFuture<Configuration> future = CompletableFuture.completedFuture(configuration);
        	if (configuration == null) {
        		// if it is null, then create a new default configuration
        		LOGGER.info("Configuration not found, creating a new one with default settings.");
        		final Configuration newConfiguration = new Configuration();
        		future = dataManager.create(newConfiguration).thenApply((o) -> newConfiguration);
        	}
        	
        	LOGGER.info("Saving configuration.");
        	return future;
    	});
    }

    /**
     * Returns true if the given rectangle is within one of the current screens.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     * @return
     */
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
    
    // TODO handle still running tasks
//    private void waitForIncompleteTasks(Stage stage) {
//    	LOGGER.info("Checking for background threads that have not completed yet.");
//		// this stuff could be null if we blow up before its created
//		AsyncTaskExecutor executor = context != null ? context.getExecutorService() : null;
//		if (executor != null) {
//			// for testing shutdown waiting
////			executor.submit(() -> { 
////				try {
////					Thread.sleep(5 * 1000);
////				} catch (Exception e1) {
////					e1.printStackTrace();
////				} 
////			});
//			
//			executor.shutdown();
//    		int activeThreads = executor.getActiveCount();
//    		if (activeThreads > 0) {
//    			LOGGER.info("{} background threads awaiting completion.", activeThreads);
//    			
//    			ShutdownDialog shutdown = new ShutdownDialog(stage, executor);
//    			shutdown.completeProperty().addListener((obs, ov, nv) -> {
//    				if (nv) {
//    					LOGGER.info("Background threads complete. Closing main window.");
//    					stage.close();
//    				}
//    			});
//    			shutdown.show();
//    		} else {
//    			LOGGER.info("No background threads awaiting completion. Closing main window.");
//    			stage.close();
//    		}
//		}
//    }
    
    private CompletableFuture<Void> onCloseRequest(GlobalContext context) {
    	// close the presentation screens
		//this.context.getDisplayManager().release();
		
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
