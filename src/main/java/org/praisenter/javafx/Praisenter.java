package org.praisenter.javafx;

import java.nio.file.Files;
import java.nio.file.Paths;



































import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;


import javafx.util.Duration;






import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.Constants;
import org.praisenter.javafx.easing.EasingType;
import org.praisenter.javafx.easing.Easings;

public final class Praisenter extends Application {
	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		// set the log4j configuration file path
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
		
		// set the derby log file path
		System.setProperty("derby.stream.error.file", Constants.DATABASE_LOG_FILE_PATH);
	}
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 700;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
    public static void main(String[] args) {
        launch(args);
    }
    
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
    	
    	// create the loading scene
    	LoadingPane loading = new LoadingPane(WIDTH, HEIGHT);
    	
    	stage.setScene(new Scene(loading));
    	
    	stage.show();
    	
    	Thread t = new Thread(loading.getLoadingTask());
    	t.start();
    }
}
