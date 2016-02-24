package org.praisenter.javafx.song;

import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.song.SongLibrary;

public class SongLibraryPane extends Application {
	static {
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
	}
	
	private static final Logger LOGGER = LogManager.getLogger(SongLibraryPane.class);
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	primaryStage.setTitle("Song Library");
    	
    	// TODO should be done elsewhere
    	SongLibrary sl = SongLibrary.open(Paths.get("C:\\Users\\William\\Desktop\\test\\songs"));
    	
    	
    	
    	
    	primaryStage.setScene(new Scene(null, 500, 500));
        primaryStage.show();
    }
}
