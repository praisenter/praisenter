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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.slide.JavaFXSlideThumbnailGenerator;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.RuntimeProperties;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
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

/**
 * Pane for showing a loading indicator and other animations while building
 * a {@link PraisenterContext}
 * @author William Bittle
 * @version 3.0.0
 */
final class LoadingPane extends Pane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// constants
	
	/** The offset from the left side of the window */
	private static final double BAR_X_OFFSET = 75.0;
	
	/** The offset from the bottom of the window */
	private static final double BAR_Y_OFFSET = 50.0;
	
	/** The radius of the circle */
	private static final double CIRCLE_RADIUS = 50.0;
	
	/** The line width of the shapes */
	private static final double LINE_WIDTH = 4.0;
	
	// members
	
	/** The loading task */
	private final LoadingTask loading;
	
	/** The loading thread */
	private final Thread loadingThread;
	
	/** The circle animation */
	private final RotateTransition circleAnimation;
	
	/** The current bar animation */
	private Timeline barAnimation;
	
	/** The on complete handler */
	private EventHandler<CompleteEvent<PraisenterContext>> onComplete;
	
	/**
	 * Full constructor
	 * @param width the initial width
	 * @param height the initial height
	 * @param javaFXContext the JavaFX context
	 * @param configuration the application configuration
	 */
	public LoadingPane(final double width, final double height, JavaFXContext javaFXContext, Configuration configuration) {
		this.setPrefWidth(width);
		this.setPrefHeight(height);
		
		// create the loading task
		this.loading = new LoadingTask(javaFXContext, configuration);
		this.loadingThread = new Thread(this.loading);
		
		// FEATURE Replace the current loading background image
		// FIXME Add "Praisenter" text or logo to the loading pane
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
    	// TODO test fonts on various platforms
    	final Text loadingText = new Text(Translations.get("loading"));
    	loadingText.setFont(Font.font(
    			RuntimeProperties.IS_WINDOWS_OS 
    			? "Segoe UI Light" //"Sans Serif" //"Lucida Grande" //"Segoe UI Light" 
    			: RuntimeProperties.IS_MAC_OS 
    				? "Lucida Grande"
    				: "Sans Serif", 80));
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
    	path.setStrokeLineCap(StrokeLineCap.BUTT);
    	
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
						
						// notify that loading is complete
						if (this.onComplete != null) {
							try {
								PraisenterContext context = loading.get();
								onPraisenterContextCreated(context);
							} catch (Exception ex) {
								LOGGER.error("Failed to get PraisenterContext due to the following.", ex);
								showExecptionAlertThenExit(ex);
							}
						} else {
							LOGGER.warn("Loading has completed, but there's no event handler set to call.");
						}
					}
				});
			}
			
			// play the new animation
			barAnimation.play();
		});
    	
    	// watch for errors
    	this.loading.exceptionProperty().addListener((exception, oldException, newException) -> {
    		if (newException != null) {
	    		LOGGER.fatal("Failed to initialize Praisenter context.", newException);
	    		
	    		// if an error occurs just stop the animations
	    		if (barAnimation != null) {
					barAnimation.stop();
				}
	    		circleAnimation.stop();
	    		
	    		showExecptionAlertThenExit(newException);
    		}
    	});
    	
    	// finally add all the nodes to this pane
    	this.getChildren().addAll(barbg, barfg, path, loadingText, currentAction);
	}
	
	/**
	 * Additional initialization that must be done on the UI thread.
	 * @param context the context
	 */
	private void onPraisenterContextCreated(PraisenterContext context) {
		// generate any missing slide thumbnails
		LOGGER.info("Generating missing slide thumbnails.");
		context.getSlideLibrary().generateMissingThumbnails(new JavaFXSlideThumbnailGenerator(100, 100, context));
		
		// setup the screen manager
		LOGGER.info("Initializing the screen manager.");
		context.getScreenManager().initialize();
		
		// load fonts
		LOGGER.info("Loading fonts.");
		Font.getFamilies();
		Font.getFontNames();

		// notify any listeners
		this.onComplete.handle(new CompleteEvent<PraisenterContext>(LoadingPane.this, LoadingPane.this, context));
	}
	
	/**
	 * Shows an exception alert and then exist the application after the user closes it.
	 * @param ex the exception
	 */
	private void showExecptionAlertThenExit(Throwable ex) {
		Platform.runLater(() -> {
			// and show the error
			Alert a = Alerts.exception(
					getScene().getWindow(),
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
	
	/**
	 * Starts the loading process on another thread.
	 * @throws IllegalStateException if start has already been called
	 */
	public void start() {
		this.loadingThread.start();
	}
	
	/**
	 * Sets the handler called when the loading is completed.
	 * @param handler the handler
	 */
	public void setOnComplete(EventHandler<CompleteEvent<PraisenterContext>> handler) {
		this.onComplete = handler;
	}
	
	/**
	 * Returns the on complete handler.
	 * @return EventHandler&lt;CompleteEvent&lt;LoadingResult&gt;&gt;
	 */
	public EventHandler<CompleteEvent<PraisenterContext>> getOnComplete() {
		return this.onComplete;
	}
}
