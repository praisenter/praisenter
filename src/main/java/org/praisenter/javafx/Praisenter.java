package org.praisenter.javafx;

import java.nio.file.Files;
import java.nio.file.Paths;
































import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
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
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 700;
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ContextLoadingTask loading = new ContextLoadingTask();
	private Timeline barAnimation = null;
	private Animation circleAnimation = null;
	
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
    	Parent root = createLoadingNode();
    	
    	stage.setScene(new Scene(root));
    	
    	stage.show();
    	
    	Thread t = new Thread(loading);
    	t.start();
    }
    
    private Parent createLoadingNode() {
    	Pane pane = new Pane();
    	pane.setPrefWidth(WIDTH);
    	pane.setPrefHeight(HEIGHT);
    	pane.setBackground(new Background(
    			new BackgroundImage(
    					// TODO need better splash screen
    					new Image("org/praisenter/resources/splash.jpg"), 
    					BackgroundRepeat.NO_REPEAT, 
    					BackgroundRepeat.NO_REPEAT, 
    					null, 
    					new BackgroundSize(1, 1, true, true, false, true))));
    	
    	// loading bar
    	final double barXOffset = 75;
    	final double barYOffset = 50;
    	Line barbg = new Line(barXOffset, HEIGHT - barYOffset, WIDTH - barXOffset, HEIGHT - barYOffset);
    	barbg.setStroke(new Color(0, 0, 0, 0.3));
    	barbg.setStrokeWidth(4);
    	
    	Line barfg = new Line();
    	barfg.setStroke(new Color(1, 1, 1, 1));
    	barfg.setStrokeWidth(4);
    	barfg.setStartX(barXOffset);
    	barfg.setStartY(HEIGHT - barYOffset);
    	barfg.setEndX(barXOffset+1);
    	barfg.setEndY(HEIGHT - barYOffset);
    	
    	// loading... text
    	// TODO choose font based on OS
    	Text loadingText = new Text("Loading...");
    	loadingText.setFont(Font.font("Segoe UI Light", 80));
    	loadingText.setFill(Color.WHITE);
    	loadingText.setX(barXOffset - 5);
    	loadingText.setY(HEIGHT - barYOffset * 2);
    	
    	Text currentAction = new Text("");
    	currentAction.setFont(Font.font("Segoe UI Light", 15));
    	currentAction.setFill(Color.WHITE);
    	currentAction.setX(barXOffset);
    	currentAction.setY(HEIGHT - barYOffset * 1.4);
    	
    	// loading circle
    	final double r = 50;
    	final double cx = WIDTH - barXOffset - r;
    	final double cy = HEIGHT - barYOffset * 1.5 - r;
    	Path path = new Path(
    			new MoveTo(			cx + r * Math.cos(Math.toRadians(0)), cy + r * Math.sin(Math.toRadians(0))),
    			new ArcTo(r, r, 0, 	cx + r * Math.cos(Math.toRadians(100)), cy + r * Math.sin(Math.toRadians(100)), false, true),
    			new MoveTo(			cx + r * Math.cos(Math.toRadians(120)), cy + r * Math.sin(Math.toRadians(120))),
    			new ArcTo(r, r, 0, 	cx + r * Math.cos(Math.toRadians(220)), cy + r * Math.sin(Math.toRadians(220)), false, true),
    			new MoveTo(			cx + r * Math.cos(Math.toRadians(240)), cy + r * Math.sin(Math.toRadians(240))),
    			new ArcTo(r, r, 0, 	cx + r * Math.cos(Math.toRadians(340)), cy + r * Math.sin(Math.toRadians(340)), false, true));
    	path.setFill(null);
    	path.setStroke(Color.WHITE);
    	path.setStrokeWidth(4);
    	path.setStrokeLineCap(StrokeLineCap.ROUND);
    	
    	RotateTransition rotate = new RotateTransition(Duration.millis(2000), path);
    	rotate.setByAngle(-360);
    	rotate.setInterpolator(Interpolator.LINEAR);
    	rotate.setCycleCount(Animation.INDEFINITE);
    	rotate.play();
    	
    	circleAnimation = rotate;
    	
    	// handle resizing
    	pane.widthProperty().addListener((obs, oValue, nValue) -> {
			// make sure everything is positioned correctly
    		double w = nValue.doubleValue();
			barbg.setEndX(w - barXOffset);
			barfg.setEndX(barXOffset + loading.getProgress() * (w - barXOffset * 2.0));
			double diff = WIDTH - w;
			path.setTranslateX(-diff);
    	});
    	pane.heightProperty().addListener((obs, oValue, nValue) -> {
			// make sure everything is positioned correctly
    		double h = nValue.doubleValue();
    		barbg.setStartY(h - barYOffset);
			barbg.setEndY(h - barYOffset);
			barfg.setStartY(h - barYOffset);
			barfg.setEndY(h - barYOffset);
			double diff = HEIGHT - h;
			path.setTranslateY(-diff);
			loadingText.setY(h - barYOffset * 2);
			currentAction.setY(h - barYOffset * 1.4);
    	});
    	
    	loading.messageProperty().addListener((obs, oValue, nValue) -> {
    		currentAction.setText(nValue);
    	});
    	loading.progressProperty().addListener((obs, oValue, nValue) -> {
			double progress = nValue.doubleValue();

			// create a new animation
			Timeline tn = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(barfg.endXProperty(), barXOffset + progress * (pane.getWidth() - barXOffset * 2), Easings.getQuadratic(EasingType.IN))));
			
			// stop the current one (if needed)
			if (barAnimation != null) {
				barAnimation.stop();
			}
			barAnimation = tn;
			if (progress >= 1.0) {
				barAnimation.statusProperty().addListener((obs2, oValue2, nValue2) -> {
					if (nValue2 == Status.STOPPED)
						circleAnimation.stop();
				});
			}
			barAnimation.play();
		});
    	loading.exceptionProperty().addListener((obs, oValue, nValue) -> {
    		if (barAnimation != null) {
				barAnimation.stop();
			}
    		circleAnimation.stop();
    		
    		Alert a = Alerts.exception("test", "test2", "test3", nValue);
    		a.showAndWait();
    		Platform.exit();
    	});
    	
    	pane.getChildren().addAll(barbg, barfg, path, loadingText, currentAction);
    	
    	return pane;
    }
}
