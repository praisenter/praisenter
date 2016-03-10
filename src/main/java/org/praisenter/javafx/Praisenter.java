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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.Constants;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

// FEATURE use Apache POI to read powerpoint files

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
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
		
		// set the derby log file path
		System.setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
		
		// create a logger for this class after the log4j has been initialized
		LOGGER = LogManager.getLogger();
		
		// load configuration properties
		Properties conf = new Properties();
		try {
			Path config = Paths.get(Constants.ROOT_PATH + "conf.properties");
			if (Files.exists(config)) {
				try (InputStream stream = Files.newInputStream(config)) {
					conf.load(stream);
				} catch (Exception ex) {
					LOGGER.error("Failed to read conf.properties file.", ex);
				}
			}
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		
		// set the language if in the config
		if (conf.containsKey("ui.language")) {
			String lang = conf.getProperty("ui.language");
			Locale locale = Locale.forLanguageTag(lang);
			Locale.setDefault(locale);
		}
	}

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
    
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {
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
    	
    	StackPane stack = new StackPane();
    	// TODO we may want to defer this until the loading is complete so that we can pass it the PraisenterContext object.
    	MainPane main = new MainPane();
    	
    	// create the loading scene
    	LoadingPane loading = new LoadingPane(WIDTH, HEIGHT);
    	loading.setOnComplete((e) -> {
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
					stack.getChildren().remove(loading);
				}
			});
			
			// play the fade out
			seq.play();
    	});
    	
    	stack.getChildren().addAll(main, loading);
    	
    	// show the stage
    	stage.setScene(new Scene(stack));
    	stage.show();
    	
    	// start the loading
    	loading.start();
    }
}
