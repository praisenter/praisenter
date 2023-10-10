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
package org.praisenter.ui.controls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Playable;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
public final class MediaPreview extends BorderPane implements Playable {
	private static final String MEDIA_PREVIEW_CSS = "p-media-preview";
	private static final String MEDIA_PREVIEW_CONTROLS_CSS = "p-media-preview-controls";
	
	private static final Logger LOGGER = LogManager.getLogger();
	  
    private final ObjectProperty<MediaPlayer> player;
    private final ObjectProperty<Duration> duration;
    
    // glyphs
    // since we are toggling them back and forth, lets use the
    // same glyph nodes for better memory usage and performance
    
    private final Node play = Icons.getIcon(Icons.PLAY);
    private final Node pause = Icons.getIcon(Icons.PAUSE);
    private final Node mute = Icons.getIcon(Icons.MUTE);
    private final Node control = Icons.getIcon(Icons.VOLUME);
    
    /**
     * Default constructor.
     */
    public MediaPreview() {
    	this.getStyleClass().add(MEDIA_PREVIEW_CSS);
    	
    	MediaView mediaView = new MediaView();
        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(this.widthProperty());
        
        this.player = mediaView.mediaPlayerProperty();
        this.duration = new SimpleObjectProperty<>(Duration.ZERO);
        
        this.setCenter(mediaView);

        HBox controlsBar = new HBox();
        controlsBar.getStyleClass().add(MEDIA_PREVIEW_CONTROLS_CSS);
        BorderPane.setAlignment(controlsBar, Pos.CENTER);

        // play/pause button
        Button btnPlay = new Button("", this.play);
        btnPlay.setOnAction(e -> {
        	this.play(true);
        });

        // time slider
        Slider sldTime = new Slider();
        sldTime.getStyleClass().add(Styles.SMALL);
        sldTime.setSkin(new ProgressSliderSkin(sldTime));
        sldTime.setMin(0);
        sldTime.setMax(100);
        sldTime.setValue(0);
        sldTime.setMinWidth(20);
        sldTime.setOnMouseReleased(e -> {
        	MediaPlayer player = this.player.get();
        	Duration duration = this.duration.get();
            if (player != null && duration != null && !duration.isIndefinite() && !duration.isUnknown()) {
            	Number nv = sldTime.getValue();
            	Duration current = player.getCurrentTime();
            	Duration newPosition = duration.multiply(nv.doubleValue() / 100.0);
        		LOGGER.debug("Seeking from " + current + " to " + newPosition);
        		// multiply duration by percentage calculated by slider position
        		player.seek(newPosition);
            }
        });
        HBox.setHgrow(sldTime, Priority.ALWAYS);

        // time label
        Label lblTime = new Label();
        lblTime.setMinWidth(Label.USE_PREF_SIZE);

        // mute button
        Button btnMute = new Button("", this.control);
        btnMute.setOnAction((e) -> {
        	MediaPlayer player = mediaView.getMediaPlayer();
        	if (player == null) return;
        	// toggle mute state
        	if (player.isMute()) {
        		player.setMute(false);
        	} else {
        		player.setMute(true);
        	}
        });
        
        // volume slider
        Slider sldVolume = new Slider();
        sldVolume.getStyleClass().add(Styles.SMALL);
        sldVolume.setSkin(new ProgressSliderSkin(sldVolume));
        sldVolume.setMin(0.0);
        sldVolume.setMax(1.0);
        sldVolume.setValue(0.5);
        sldVolume.setPrefWidth(70);
        sldVolume.setMaxWidth(Region.USE_PREF_SIZE);
        sldVolume.setMinWidth(50);
        sldVolume.valueProperty().addListener((obs) -> {
        	MediaPlayer player = mediaView.getMediaPlayer();
            if (player != null && sldVolume.isValueChanging()) {
        		player.setVolume(sldVolume.getValue());
            }
        });
        
        controlsBar.getChildren().addAll(btnPlay, sldTime, lblTime, btnMute, sldVolume);
        controlsBar.setDisable(true);
		
        this.setBottom(controlsBar);
        
        ChangeListener<Duration> timeListener = (obs, ov, nv) -> {
        	Duration duration = this.duration.get();
        	if (duration == null || duration.isIndefinite() || duration.isUnknown()) return;
        	double value = nv.toMillis() / duration.toMillis() * 100.0;
        	if (!sldTime.isPressed() && !sldTime.isValueChanging()) {
        		sldTime.setValue(value);
        	}
        };
        
        Runnable onEndOfMedia = () -> {
        	MediaPlayer player = this.player.get();
        	if (player != null) {
        		player.stop();
        		player.seek(player.getStartTime());
        	}
        };
        
        this.player.addListener((obs, ov, nv) -> {
        	// clean up
        	if (ov != null) {
        		ov.volumeProperty().unbindBidirectional(sldVolume.valueProperty());
        		ov.currentTimeProperty().removeListener(timeListener);
        		ov.setOnEndOfMedia(null);
        		ov.dispose();
        	}
        	
        	this.duration.unbind();
        	controlsBar.disableProperty().unbind();
        	btnPlay.graphicProperty().unbind();
        	lblTime.textProperty().unbind();
        	btnMute.graphicProperty().unbind();
        	
        	this.duration.set(Duration.ZERO);
        	controlsBar.setDisable(true);
        	btnPlay.setGraphic(this.play);
        	lblTime.setText(this.formatTime(Duration.ZERO));
        	sldTime.setValue(0);
        	btnMute.setGraphic(this.control);
        	sldVolume.setValue(0.5);
        	
        	if (nv != null) {
        		this.duration.bind(nv.cycleDurationProperty());
        		
        		controlsBar.disableProperty().bind(Bindings.createBooleanBinding(() -> {
        			Status status = nv.getStatus();
        			return status == Status.DISPOSED || status == Status.HALTED || status == Status.UNKNOWN;
        		}, nv.statusProperty()));
        		
        		btnPlay.graphicProperty().bind(Bindings.createObjectBinding(() -> {
        			Status status = nv.getStatus();
        			if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
        				// then show the play symbol
        				return this.play;
        			} else {
        				return this.pause;
        			}
        		}, nv.statusProperty()));

        		lblTime.textProperty().bind(Bindings.createObjectBinding(() -> {
        			Duration currentTime = nv.getCurrentTime();
        			return this.formatTime(currentTime);
        		}, nv.currentTimeProperty(), nv.cycleDurationProperty()));
        		
        		btnMute.graphicProperty().bind(Bindings.createObjectBinding(() -> {
        			boolean isMuted = nv.isMute();
        			return isMuted ? this.mute : this.control;
        		}, nv.muteProperty()));
        		
        		nv.volumeProperty().bindBidirectional(sldVolume.valueProperty());
        		nv.setOnEndOfMedia(onEndOfMedia);
        		nv.currentTimeProperty().addListener(timeListener);
        	}
        });
    }
   
    public MediaPlayer getMediaPlayer() {
    	return this.player.get();
    }
    
    public void setMediaPlayer(MediaPlayer player) {
    	this.player.set(player);
    }
    
    public ObjectProperty<MediaPlayer> mediaPlayerProperty() {
    	return this.player;
    }
    
    /**
     * Formats a time/duration string.
     * @param elapsed the elapsed time
     * @param duration the total time
     * @return String
     */
    private String formatTime(Duration elapsed) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        Duration duration = this.duration.get();
        if (duration == null || duration == Duration.INDEFINITE || duration == Duration.UNKNOWN) {
        	duration = Duration.ZERO;
        }
        
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
    
    @Override
    public void play() {
    	this.play(false);
    }
    
    private void play(boolean toggle) {
    	MediaPlayer player = this.player.get();
    	
    	if (player == null) return;
    	
        Status status = player.getStatus();

        if (status == Status.DISPOSED) {
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

        if (!toggle) {
        	player.play();
        	return;
        }
        
        if (status == Status.PAUSED
         || status == Status.READY
         || status == Status.STOPPED) {
            // then resume playing
            player.play();
        } else {
            player.pause();
        }
    }
    
    @Override
    public void pause() {
    	MediaPlayer player = this.player.get();
    	if (player != null) {
    		player.pause();
    	}
    }
    
    @Override
    public void stop() {
    	MediaPlayer player = this.player.get();
    	if (player != null) {
    		player.stop();
    	}
    }
    
    @Override
    public void dispose() {
    	MediaPlayer player = this.player.get();
    	this.player.set(null);
    	if (player != null) {
    		player.dispose();
    	}
    }
}
