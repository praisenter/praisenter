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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.resources.OpenIconic;
import org.praisenter.resources.translations.Translations;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

// FIXME fix the manifest
// FIXME explore deployment options
// FIXME what happens when two of the same movie is played with audio.... For example if displayed on multiple screens
// FIXME testing on High DPI screens

// FEATURE Use Apache POI to read powerpoint files
// FEATURE Evaluate alternate JavaFX styles here https://github.com/JFXtras/jfxtras-styles

// JAVABUG 09/28/16 High DPI - https://bugs.openjdk.java.net/browse/JDK-8090569

/**
 * This is the entry point for the application.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Praisenter extends Application {
	/** The class-level logger */
	private static final Logger LOGGER;
	
	/** The application configuration properties */
	private static final Configuration CONFIG;
	
	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		// set the log4j configuration file path
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
		
		// set the derby log file path
		System.setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
		
		// create a logger for this class after the log4j has been initialized
		LOGGER = LogManager.getLogger();
		
		// load configuration properties
		LOGGER.info("Loading configuration.");
		Configuration config = Configuration.load();
		if (config == null) {
			// either an exception occurred loading the configuration
			// or the configuration hasn't been saved, in either case
			// we need to create a default configuration
			LOGGER.info("No configuration found or unable to load configuration. Using default configuration.");
			config = Configuration.createDefaultConfiguration();
		} else {
			// set the language if in the config
			if (config.getLanguage() != null) {
				LOGGER.info("Setting the default language to '" + config.getLanguage() + "'.");
				Locale.setDefault(config.getLanguage());
			}
		}
		
		CONFIG = config;
	}

	// FEATURE We should look at making some of the Java FX features "optional" instead of required
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
	private static final int WIDTH = 1200;
	
	/** The default height */
	private static final int HEIGHT = 700;
	
	/**
	 * The entry point method.
	 * @param args any arguments
	 */
    public static void main(String[] args) {
        launch(args);
    }
    
    /** The praisenter context */
    private PraisenterContext context;
    
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {
    	// log supported features
    	LOGGER.info("Supported Java FX Features:");
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
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon16x16.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon32x32.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon48x48.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon64x64.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon96x96.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon128x128.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon256x256.png"));
    	stage.getIcons().add(new Image("org/praisenter/resources/logo/icon512x512.png"));
    	
		// load fonts
    	LOGGER.info("Loading glyph fonts.");
    	GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/resources/fontawesome-webfont.ttf")));
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/resources/open-iconic.ttf")));
		
    	// we'll have a stack of the main pane and the loading pane
    	StackPane stack = new StackPane();
    	
    	// create the loading scene
    	LoadingPane loading = new LoadingPane(WIDTH, HEIGHT, new JavaFXContext(this, stage), CONFIG);
    	loading.setOnComplete((e) -> {
    		long t0 = 0;
    		long t1 = 0;
    		
    		// get the context
    		PraisenterContext context = e.data;
    		
    		LOGGER.info("Creating the UI.");
    		t0 = System.nanoTime();
    		// create the main pane and add it to the stack
    		MainPane main = new MainPane(context);
    		t1 = System.nanoTime();
    		LOGGER.info("UI created in {} seconds.", (t1 - t0) / 1e9);
    		
    		stack.getChildren().add(0, main);
    		
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
    	scene.getStylesheets().add(CONFIG.getThemeCss());
    	stage.setScene(scene);
    	stage.setOnCloseRequest((e) -> {
    		LOGGER.info("Checking for background threads that have not completed yet.");
    		// this stuff could be null if we blow up before its created
    		MonitoredThreadPoolExecutor executor = context != null ? context.getExecutorService() : null;
    		if (executor != null) {
    			
    			// for testing shutdown waiting
//        		context.getWorkers().submit(() -> { 
//        			try {
//    					Thread.sleep(5 * 1000);
//    				} catch (Exception e1) {
//    					e1.printStackTrace();
//    				} 
//        		});
    			
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
    	});
    	stage.show();
    	
    	// start the loading
    	LOGGER.info("Starting load");
    	loading.start();
    }
}
