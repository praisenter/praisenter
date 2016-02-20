package org.praisenter.javafx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

final class LoadingPane extends Pane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final double BAR_X_OFFSET = 75.0;
	private static final double BAR_Y_OFFSET = 50.0;
	private static final double CIRCLE_RADIUS = 50.0;
	private static final double LINE_WIDTH = 4.0;
	
	/** The loading task */
	private final ContextLoadingTask loading;
	
	/** The circle animation */
	private final RotateTransition circleAnimation;
	
	/** The current bar animation */
	private Timeline barAnimation;
	
	/**
	 * Full constructor
	 * @param width the initial width
	 * @param height the initial height
	 */
	public LoadingPane(final double width, final double height) {
		this.setPrefWidth(width);
		this.setPrefHeight(height);
		
		// create the loading task
		this.loading = new ContextLoadingTask();
		
		// TODO need better splash screen image
		// set the background image
    	setBackground(new Background(
    			new BackgroundImage(
    					new Image("org/praisenter/resources/splash.jpg"), 
    					BackgroundRepeat.NO_REPEAT, 
    					BackgroundRepeat.NO_REPEAT, 
    					null, 
    					new BackgroundSize(1, 1, true, true, false, true))));
    	
    	// loading bar background
    	final Line barbg = new Line(BAR_X_OFFSET, height - BAR_Y_OFFSET, width - BAR_X_OFFSET, height - BAR_Y_OFFSET);
    	barbg.setStroke(new Color(0, 0, 0, 0.3));
    	barbg.setStrokeWidth(LINE_WIDTH);
    	
    	// the loading bar
    	final Line barfg = new Line();
    	barfg.setStroke(new Color(1, 1, 1, 1));
    	barfg.setStrokeWidth(LINE_WIDTH);
    	barfg.setStartX(BAR_X_OFFSET);
    	barfg.setStartY(height - BAR_Y_OFFSET);
    	barfg.setEndX(BAR_X_OFFSET + 1);
    	barfg.setEndY(height - BAR_Y_OFFSET);
    	
    	// loading... text
    	// TODO choose font based on OS
    	final Text loadingText = new Text("Loading...");
    	loadingText.setFont(Font.font("Segoe UI Light", 80));
    	loadingText.setFill(Color.WHITE);
    	loadingText.setX(BAR_X_OFFSET - 5);
    	loadingText.setY(height - BAR_Y_OFFSET - CIRCLE_RADIUS * 0.75);
    	
    	// the current loading action
    	final Text currentAction = new Text("");
    	currentAction.setFont(Font.font("Segoe UI Light", 15));
    	currentAction.setFill(Color.WHITE);
    	currentAction.setX(BAR_X_OFFSET);
    	currentAction.setY(height - BAR_Y_OFFSET - LINE_WIDTH - CIRCLE_RADIUS * 0.25);
    	
    	// loading circle
    	final double cx = width - BAR_X_OFFSET - CIRCLE_RADIUS;
    	final double cy = height - BAR_Y_OFFSET - LINE_WIDTH - CIRCLE_RADIUS * 0.5 - CIRCLE_RADIUS;
    	final Path path = new Path(
    			new MoveTo(									cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(0)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(0))),
    			new ArcTo(CIRCLE_RADIUS, CIRCLE_RADIUS, 0, 	cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(100)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(100)), false, true),
    			new MoveTo(									cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(120)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(120))),
    			new ArcTo(CIRCLE_RADIUS, CIRCLE_RADIUS, 0, 	cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(220)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(220)), false, true),
    			new MoveTo(									cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(240)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(240))),
    			new ArcTo(CIRCLE_RADIUS, CIRCLE_RADIUS, 0, 	cx + CIRCLE_RADIUS * Math.cos(Math.toRadians(340)), cy + CIRCLE_RADIUS * Math.sin(Math.toRadians(340)), false, true));
    	path.setFill(null);
    	path.setStroke(Color.WHITE);
    	path.setStrokeWidth(LINE_WIDTH);
    	path.setStrokeLineCap(StrokeLineCap.ROUND);
    	
    	// the circle animation
    	this.circleAnimation = new RotateTransition(Duration.millis(2000), path);
    	this.circleAnimation.setByAngle(-360);
    	this.circleAnimation.setInterpolator(Interpolator.LINEAR);
    	this.circleAnimation.setCycleCount(Animation.INDEFINITE);
    	this.circleAnimation.play();
    	
    	// handle resizing
    	this.widthProperty().addListener((obs, oValue, nValue) -> {
			// make sure everything is positioned correctly
    		double w = nValue.doubleValue();
			barbg.setEndX(w - BAR_X_OFFSET);
			barfg.setEndX(BAR_X_OFFSET + Math.max(0, loading.getProgress()) * (w - BAR_X_OFFSET * 2.0));
			double diff = width - w;
			path.setTranslateX(-diff);
    	});
    	this.heightProperty().addListener((obs, oValue, nValue) -> {
			// make sure everything is positioned correctly
    		double h = nValue.doubleValue();
    		barbg.setStartY(h - BAR_Y_OFFSET);
			barbg.setEndY(h - BAR_Y_OFFSET);
			barfg.setStartY(h - BAR_Y_OFFSET);
			barfg.setEndY(h - BAR_Y_OFFSET);
			double diff = height - h;
			path.setTranslateY(-diff);
			loadingText.setY(h - BAR_Y_OFFSET  - LINE_WIDTH - CIRCLE_RADIUS * 0.75);
			currentAction.setY(h - BAR_Y_OFFSET  - LINE_WIDTH - CIRCLE_RADIUS * 0.25);
    	});
    	
    	// update the message
    	this.loading.messageProperty().addListener((message, oldAction, newAction) -> {
    		currentAction.setText(newAction);
    	});
    	
    	// update the progress
    	this.loading.progressProperty().addListener((progress, oldProgress, newProgress) -> {
			double pc = newProgress.doubleValue();

			// when the progress changes, we could just go ahead and set the new
			// end x of the loading bar, but it looks choppy.
			
			// instead we will animate the end x of the bar to the correct position
			// using a timeline.
			
			// if the current timeline isn't finished by the time a new target end
			// x is reached, it is stopped wherever it is and a new timeline animates
			// it to the new end x.
			
			// create a new timeline to animate the end x property of the loading bar
			Timeline tn = new Timeline(
					new KeyFrame(
							Duration.millis(300), 
							new KeyValue(
									barfg.endXProperty(), 
									BAR_X_OFFSET + pc * (LoadingPane.this.getWidth() - BAR_X_OFFSET * 2), 
									Interpolator.EASE_IN)));
			
			// stop the current one (if needed)
			if (barAnimation != null) {
				barAnimation.stop();
			}
			
			// assign the new animation
			barAnimation = tn;
			
			// check if we have completed
			if (pc >= 1.0) {
				// if so, lets listen for the bar animation to complete
				// so we can stop the circle animation and fade out this
				// pane
				barAnimation.statusProperty().addListener((barStatus, oldBarStatus, newBarStatus) -> {
					if (newBarStatus == Status.STOPPED) {
						// stop the circle animation
						circleAnimation.stop();
						
						// TODO do this from Praisenter class so we can remove the pane and make sure the main application is ready
						// we only want to fade out the loading screen if the loading
						// was successful
						if (loading.getException() == null) {
							// fade out the loader
							FadeTransition fade = new FadeTransition(Duration.millis(600), LoadingPane.this);
							fade.setFromValue(1.0);
							fade.setToValue(0.0);
							
							// we'll pause for a moment to let the user see it completed
							SequentialTransition seq = new SequentialTransition(new PauseTransition(Duration.millis(1500)), fade);
							seq.setAutoReverse(false);
							seq.setCycleCount(1);
							
							// when the fade out is complete
							seq.statusProperty().addListener((fadeStatus, oldFadeStatus, newFadeStatus) -> {
								// TODO remove the pane
								System.out.println("done");
							});
							
							// play the fade out
							seq.play();
						}
					}
				});
			}
			
			// play the new animation
			barAnimation.play();
		});
    	
    	// watch for errors
    	this.loading.exceptionProperty().addListener((exception, oldException, newException) -> {
    		LOGGER.fatal("Failed to initialize Praisenter context.", newException);
    		
    		// if an error occurs just stop the animations
    		if (barAnimation != null) {
				barAnimation.stop();
			}
    		circleAnimation.stop();
    		
    		// and show the error
    		Alert a = Alerts.exception("test", "test2", "test3", newException);
    		a.showAndWait();
    		
    		LOGGER.info("User closed exception dialog. Exiting application.");
    		
    		// then exit the app
    		Platform.exit();
    	});
    	
    	// finally add all the nodes to this pane
    	this.getChildren().addAll(barbg, barfg, path, loadingText, currentAction);
	}
	
	/**
	 * Returns the loading task to be used in a thread.
	 * @return Task&lt;{@link PraisenterContext}&gt;
	 */
	public Task<PraisenterContext> getLoadingTask() {
		return this.loading;
	}
}
