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
package org.praisenter.javafx.media;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.Glyph;
import org.praisenter.data.media.MediaType;
import org.praisenter.javafx.ApplicationGlyphs;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * Represents a media player with play/pause and volume controls.
 * @author William Bittle
 * @version 3.0.0
 * @see <a href="http://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm#sthref18">Controlling Media Playback</a>
 */
public final class MediaPlayerPane extends BorderPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
    /** The current media duration */
    private Duration duration;
    
    // controls
    
    /** The view to send playing media */
    private final MediaView mediaView;
    
    /** The button for playing or pausing playback */
    private final Button btnPlay;
    
    /** The slider for the current position in the media */
    private final Slider sldTime;
    
    /** The label for showing the current position in the media */
    private final Label lblTime;
    
    /** The button for muting audio */
    private final Button btnMute;
    
    /** The slider for the current volume */
    private final Slider sldVolume;
    
    /** The container for all the media playback controls */
    private final HBox controlsBar;

    // glyphs
    // since we are toggling them back and forth, lets use the
    // same glyph nodes for better memory usage and performance
    
    /** A glyph for play */
    private final Glyph play = ApplicationGlyphs.PLAYER_PLAY.duplicate();
    
    /** A glyph for pause */
    private final Glyph pause = ApplicationGlyphs.PLAYER_PAUSE.duplicate();
    
    /** A glyph for mute */
    private final Glyph mute = ApplicationGlyphs.PLAYER_VOLUME_MUTE.duplicate();
    
    /** A glyph for the volume control */
    private final Glyph control = ApplicationGlyphs.PLAYER_VOLUME_CONTROL.duplicate();
    
    /**
     * Default constructor.
     */
    public MediaPlayerPane() {
    	this.getStyleClass().add("media-player-pane");
        this.mediaView = new MediaView();
        this.mediaView.setPreserveRatio(true);
        
        this.setCenter(this.mediaView);

        this.controlsBar = new HBox();
        this.controlsBar.setAlignment(Pos.CENTER);
        this.controlsBar.setPadding(new Insets(5, 10, 5, 10));
        this.controlsBar.setSpacing(5);
        BorderPane.setAlignment(this.controlsBar, Pos.CENTER);

        // play/pause button
        this.btnPlay = new Button("", play);
        this.btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	MediaPlayer player = mediaView.getMediaPlayer();
            	if (player == null) return;
            	
                Status status = player.getStatus();

                if (status == Status.UNKNOWN || 
            		status == Status.DISPOSED) {
                	// check the error
                	MediaException mex = player.getError();
                	if (mex != null) {
                		LOGGER.error("Failed to play " + player.getMedia().getSource() + " due to " + mex.getMessage(), mex);
                	}
                    // don't do anything in these states
                    return;
                }
                
                if (status == Status.HALTED) {
                	// just log the error
                	LOGGER.error("Media " + player.getMedia().getSource() + " halted.", player.getError());
                	return;
                }

                if (status == Status.PAUSED
                 || status == Status.READY
                 || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    player.play();
                } else {
                    player.pause();
                }
            }
        });

        // time slider
        this.sldTime = new Slider();
        this.sldTime.setMin(0);
        this.sldTime.setMax(100);
        this.sldTime.setValue(0);
        this.sldTime.setMinWidth(20);
        this.sldTime.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
            	MediaPlayer player = mediaView.getMediaPlayer();
                if (player != null && sldTime.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    player.seek(duration.multiply(sldTime.getValue() / 100.0));
                }
            }
        });
        HBox.setHgrow(this.sldTime, Priority.ALWAYS);

        // time label
        this.lblTime = new Label();
        this.lblTime.setMinWidth(Label.USE_PREF_SIZE);

        // mute button
        this.btnMute = new Button("", control);
        this.btnMute.setOnAction((e) -> {
        	MediaPlayer player = mediaView.getMediaPlayer();
        	if (player == null) return;
        	// toggle mute state
        	if (player.isMute()) {
        		player.setMute(false);
        		btnMute.setGraphic(control);
        	} else {
        		player.setMute(true);
        		btnMute.setGraphic(mute);
        	}
        });
        
        // volume slider
        this.sldVolume = new Slider();
        this.sldVolume.setMin(0);
        this.sldVolume.setMax(100);
        this.sldVolume.setValue(100);
        this.sldVolume.setPrefWidth(70);
        this.sldVolume.setMaxWidth(Region.USE_PREF_SIZE);
        this.sldVolume.setMinWidth(50);
        this.sldVolume.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
            	MediaPlayer player = mediaView.getMediaPlayer();
                if (player != null && sldVolume.isValueChanging()) {
                    player.setVolume(sldVolume.getValue() / 100.0);
                }
            }
        });
        
        this.controlsBar.getChildren().addAll(this.btnPlay, this.sldTime, this.lblTime, this.btnMute, this.sldVolume);
        this.controlsBar.setDisable(true);
		
        this.setBottom(this.controlsBar);
    }
    
    /**
     * The fitWidth property for the media.
     * @return DoubleProperty
     */
    public DoubleProperty mediaFitWidthProperty() {
    	return this.mediaView.fitWidthProperty();
    }
    
    /**
     * Sets the current media player.
     * @param player the new player
     * @param type the media type
     */
    public void setMediaPlayer(MediaPlayer player, MediaType type) {
    	// if the current player isn't null
    	MediaPlayer old = this.mediaView.getMediaPlayer();
    	
		// set the new player
    	this.mediaView.setMediaPlayer(player);
    	
    	// then make sure we clean up the
    	// current one right away
    	if (old != null) {
    		old.dispose();
    	}
    	
    	// reset the controls bar
    	this.btnPlay.setGraphic(play);
		this.sldTime.setValue(0);
		
    	// perform more setup
    	if (player != null) {
    		// enable the controls
    		this.controlsBar.setDisable(false);
    		
    		// wire up events
    		player.setCycleCount(1);
        	player.currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    updateValues();
                }
            });
            player.setOnPlaying(new Runnable() {
                public void run() {
                	btnPlay.setGraphic(pause);
                }
            });
            player.setOnPaused(new Runnable() {
                public void run() {
                    btnPlay.setGraphic(play);
                }
            });
            player.setOnStopped(new Runnable() {
                public void run() {
                    btnPlay.setGraphic(play);
                }
            });
            player.setOnReady(new Runnable() {
                public void run() {
                    duration = player.getMedia().getDuration();
                    updateValues();
                }
            });
            player.setOnEndOfMedia(new Runnable() {
                public void run() {
                	Platform.runLater(() -> {
                		// for some reason, this was the only consistent way to 
                		// get the controls and everything to stay linked
                		setMediaPlayer(new MediaPlayer(player.getMedia()), type);
                	});
                }
            });
            
        	this.setCenter(this.mediaView);
    	} else {
    		// disable the controls
    		this.lblTime.setText(formatTime(Duration.ZERO, Duration.ZERO));
    		this.controlsBar.setDisable(true);
    	}
    }
    
    /**
     * Updates the controls based on the media player's current status.
     */
    private void updateValues() {
    	final MediaPlayer player = this.mediaView.getMediaPlayer();
        if (player != null) {
            Platform.runLater(() -> {
                Duration currentTime = player.getCurrentTime();
                lblTime.setText(formatTime(currentTime, duration));
                sldTime.setDisable(duration.isUnknown());
                if (!sldTime.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !sldTime.isValueChanging()) {
                    sldTime.setValue(currentTime.divide(duration.toMillis()).toMillis()
                            * 100.0);
                }
                if (!sldVolume.isValueChanging()) {
                    sldVolume.setValue((int) Math.round(player.getVolume()
                            * 100));
                }
            });
        }
    }

    /**
     * Formats a time/duration string.
     * @param elapsed the elapsed time
     * @param duration the total time
     * @return String
     */
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }
    
    /**
     * Stops the player.
     */
    public void stop() {
    	MediaPlayer player = this.mediaView.getMediaPlayer();
    	if (player != null) {
    		player.stop();
    	}
    }
}
