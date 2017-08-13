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
package org.praisenter.javafx;

import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.javafx.async.AsyncTaskExecutor;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.controls.Alerts;
import org.praisenter.resources.OpenIconic;
import org.praisenter.resources.translations.Translations;
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
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

// FIXME fix the manifest
// FIXME explore deployment options
// FIXME testing on High DPI screens
// FIXME fix dark theme

// FEATURE (L) Evaluate detecting text language for better indexing (a field per language) in lucene; Apache Tika or LangDetect; This would also be used in the searches to know what indexed fields to use
// FEATURE (L) Use Apache POI to read powerpoint files
// FEATURE (M) Evaluate alternate JavaFX styles here https://github.com/JFXtras/jfxtras-styles
// FEATURE (H) Quick send to display - any place in the app when the context contains something that could be displayed offer a Quick Display button to allow the user to quickly get it shown - with configurable settings
// FEATURE (L) Quick send any image/video from file system
// FEATURE (M) From selected media items, generate slides or slide show
// FEATURE (H) Auto-update feature

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
	
	/** The configuration */
	private static final Configuration CONFIGURATION;
	
	/** The start time */
	private static final long START_TIME = System.nanoTime();
	
	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		// set the log4j configuration file path
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
		
		// create a logger for this class after the log4j has been initialized
		LOGGER = LogManager.getLogger();
    	
		// log some system info
    	LOGGER.info(Constants.NAME + " " + Constants.VERSION);
    	LOGGER.info("OS:        " + (RuntimeProperties.IS_WINDOWS_OS ? "[W] " : RuntimeProperties.IS_MAC_OS ? "[M] " : RuntimeProperties.IS_LINUX_OS ? "[L] " : "[O] ") + RuntimeProperties.OPERATING_SYSTEM + " " + RuntimeProperties.ARCHITECTURE);
    	LOGGER.info("Java:      " + RuntimeProperties.JAVA_VERSION + " " + RuntimeProperties.JAVA_VENDOR);
    	LOGGER.info("JVM Args:  " + RuntimeProperties.JVM_ARGUMENTS);
    	LOGGER.info("Java Home: " + RuntimeProperties.JAVA_HOME);
    	LOGGER.info("User Home: " + RuntimeProperties.USER_HOME);
    	LOGGER.info("Locale:    " + Locale.getDefault().toLanguageTag());
    	
		// load (or create) configuration
		LOGGER.info("Loading configuration.");
		CONFIGURATION = Configuration.load();
		// set the language if in the config
		String lang = CONFIGURATION.getString(Setting.APP_LANGUAGE, null);
		if (lang != null) {
			Locale locale = Locale.forLanguageTag(lang);
			if (locale != null) {
				LOGGER.info("Setting the default language to '" + locale + "'.");
				Locale.setDefault(locale);
			}
		}
		
		// check for debug mode
		if (CONFIGURATION.isSet(Setting.APP_DEBUG_MODE)) {
			// if enable, change the log level to DEBUG
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
			loggerConfig.setLevel(Level.TRACE);
			ctx.updateLoggers();
		}
	}

	// FEATURE (L) We should look at making some of the Java FX features "optional" instead of required
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
    
    /** The praisenter context */
    private PraisenterContext context;
    
    /**
     * Returns true if the given rectangle is within one of the current screens.
     * @param x the x coorindate
     * @param y the y coorindate
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
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon16x16alt.png", 16, 16, true, true));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon32x32.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon48x48.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon64x64.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon96x96.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon128x128.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon256x256.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon512x512.png"));
    	
    	// set minimum size
    	stage.setMinWidth(MIN_WIDTH);
    	stage.setMinHeight(MIN_HEIGHT);
    	
    	// read stored position/size
    	double x = CONFIGURATION.getDouble(Setting.APP_X, stage.getX());
		double y = CONFIGURATION.getDouble(Setting.APP_Y, stage.getY());
		double w = CONFIGURATION.getDouble(Setting.APP_WIDTH, MIN_WIDTH);
		double h = CONFIGURATION.getDouble(Setting.APP_HEIGHT, MIN_HEIGHT);
    	
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
		if (CONFIGURATION.getBoolean(Setting.APP_MAXIMIZED, false)) {
			stage.setMaximized(true);
		}
		
		// load fonts
    	LOGGER.info("Loading glyph fonts.");
    	GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/resources/fontawesome-webfont.ttf")));
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/resources/open-iconic.ttf")));

		// create the observable configuration
		ObservableConfiguration configuration = new ObservableConfiguration(CONFIGURATION);
		
    	// we'll have a stack of the main pane and the loading pane
    	StackPane stack = new StackPane();
    	
    	// create the loading scene
    	LoadingPane loading = new LoadingPane(MIN_WIDTH, MIN_HEIGHT, new JavaFXContext(this, stage), configuration);
    	loading.setOnComplete((e) -> {
    		long t0 = 0;
    		long t1 = 0;
    		
    		// get the context
    		this.context = e.data;
    		
    		LOGGER.info("Creating the UI.");
    		t0 = System.nanoTime();
    		// create the main pane and add it to the stack
    		MainPane main = new MainPane(this.context);
    		t1 = System.nanoTime();
    		LOGGER.info("UI created in {} seconds.", (t1 - t0) / 1e9);
    		
    		stack.getChildren().add(0, main);
    		
    		// set what to do when the app is closed
    		stage.setOnCloseRequest(we -> {
    			// check for unsaved changes
	    		ButtonType result = main.checkForUnsavedChanges();
	    		if (result == ButtonType.CANCEL) {
	    			we.consume();
	    		}
    		});
    		stage.setOnHiding(this::onHiding);
    		
			// fade out the loader
			FadeTransition fade = new FadeTransition(Duration.millis(600), loading);
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
					stack.getChildren().remove(loading);
					LOGGER.info("Loading scene removed from the scene graph. {} node(s) remaining.", stack.getChildren().size());
					long endTime = System.nanoTime();
					LOGGER.info("Loading took {} seconds.", (endTime - START_TIME) / 1e9);
				}
			});
			
			// play the fade out
			seq.play();
    	});
    	
    	// add the loading scene to the stack
    	stack.getChildren().add(loading);
    	
    	// show the stage
    	Scene scene = new Scene(stack);
    	// set the application look and feel
    	// NOTE: this should be the only place where this is done, every other window needs to inherit the css from the parent
    	scene.getStylesheets().add(configuration.getTheme().getCss());
    	stage.setScene(scene);
//    	stage.setOnHiding((e) -> {
//			// close the presentation screens
//			this.context.getDisplayManager().release();
//			
//    		// save some application info
//    		LOGGER.debug("Saving application location and size: ({}, {}) {}x{}", stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
//    		if (!stage.isMaximized()) {
//	    		CONFIGURATION.setDouble(Setting.APP_X, stage.getX());
//	    		CONFIGURATION.setDouble(Setting.APP_Y, stage.getY());
//	    		CONFIGURATION.setDouble(Setting.APP_WIDTH, stage.getWidth());
//	    		CONFIGURATION.setDouble(Setting.APP_HEIGHT, stage.getHeight());
//    		}
//    		CONFIGURATION.setBoolean(Setting.APP_MAXIMIZED, stage.isMaximized());
//    		
//    		try {
//    			// save the configuration synchronously
//    			CONFIGURATION.save();
//    		} catch (Exception ex) {
//    			LOGGER.warn("Failed to save application x,y and width,height when the application closed.", ex);
//    		}
//    		
//    		LOGGER.info("Checking for background threads that have not completed yet.");
//    		// this stuff could be null if we blow up before its created
//    		AsyncTaskExecutor executor = context != null ? context.getExecutorService() : null;
//    		if (executor != null) {
//    			
//    			// for testing shutdown waiting
////        		context.getWorkers().submit(() -> { 
////        			try {
////    					Thread.sleep(5 * 1000);
////    				} catch (Exception e1) {
////    					e1.printStackTrace();
////    				} 
////        		});
//    			
//    			executor.shutdown();
//	    		int activeThreads = executor.getActiveCount();
//	    		if (activeThreads > 0) {
//	    			LOGGER.info("{} background threads awaiting completion.", activeThreads);
//	    			e.consume();
//	    			
//	    			ShutdownDialog shutdown = new ShutdownDialog(stage, executor);
//	    			shutdown.completeProperty().addListener((obs, ov, nv) -> {
//	    				if (nv) {
//	    					LOGGER.info("Shutdown complete. Exiting the platform.");
//	    					Platform.exit();
//	    				}
//	    			});
//	    			shutdown.show();
//	    		} else {
//	    			LOGGER.info("No active background threads running. Exiting the platform.");
//		    		Platform.exit();
//	    		}
//    		} else {
//    			LOGGER.info("No active background threads running. Exiting the platform.");
//	    		Platform.exit();
//    		}
//    	});
    	stage.show();
    	
    	// start the loading
    	LOGGER.info("Starting load");
    	loading.start();
    }
    
    private void onHiding(WindowEvent e) {
    	Stage stage = this.context.getJavaFXContext().getStage();
    	
    	// close the presentation screens
		this.context.getDisplayManager().release();
		
		// save some application info
		LOGGER.debug("Saving application location and size: ({}, {}) {}x{}", stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
		if (!stage.isMaximized()) {
    		CONFIGURATION.setDouble(Setting.APP_X, stage.getX());
    		CONFIGURATION.setDouble(Setting.APP_Y, stage.getY());
    		CONFIGURATION.setDouble(Setting.APP_WIDTH, stage.getWidth());
    		CONFIGURATION.setDouble(Setting.APP_HEIGHT, stage.getHeight());
		}
		CONFIGURATION.setBoolean(Setting.APP_MAXIMIZED, stage.isMaximized());
		
		try {
			// save the configuration synchronously
			CONFIGURATION.save();
		} catch (Exception ex) {
			LOGGER.warn("Failed to save application x,y and width,height when the application closed.", ex);
		}
		
		LOGGER.info("Checking for background threads that have not completed yet.");
		// this stuff could be null if we blow up before its created
		AsyncTaskExecutor executor = context != null ? context.getExecutorService() : null;
		if (executor != null) {
			
			// for testing shutdown waiting
//    	        		context.getWorkers().submit(() -> { 
//    	        			try {
//    	    					Thread.sleep(5 * 1000);
//    	    				} catch (Exception e1) {
//    	    					e1.printStackTrace();
//    	    				} 
//    	        		});
			
			executor.shutdown();
    		int activeThreads = executor.getActiveCount();
    		if (activeThreads > 0) {
    			LOGGER.info("{} background threads awaiting completion.", activeThreads);
    			e.consume();
    			
    			ShutdownDialog shutdown = new ShutdownDialog(stage, executor);
    			shutdown.completeProperty().addListener((obs, ov, nv) -> {
    				if (nv) {
    					LOGGER.info("Shutdown complete. Exiting the platform.");
    					Platform.exit();
    				}
    			});
    			shutdown.show();
    		} else {
    			LOGGER.info("No active background threads running. Exiting the platform.");
	    		Platform.exit();
    		}
		} else {
			LOGGER.info("No active background threads running. Exiting the platform.");
    		Platform.exit();
		}
    }
}
