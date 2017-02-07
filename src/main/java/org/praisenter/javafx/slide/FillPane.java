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
package org.praisenter.javafx.slide;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.object.MediaObject;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * A custom pane that can show an color, gradient, image, video, etc.
 * @author William Bittle
 * @version 3.0.0
 */
final class FillPane extends StackPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// data
	
	/** The praisenter context */
	private final PraisenterContext context;
	
	/** The slide mode */
	private final SlideMode mode;
	
	// nodes
	
	/** The media view for audio/video */
	private final MediaView mediaView;
	
	/** The paint view for color/gradient/image */
	private final VBox paintView;
	
	// current state
	
	/** The media */
	private Media media;
	
	/** The scaling type */
	private ScaleType scaleType;
	
	/** The image to use when not presenting */
	private Image image;
	
	/** The target width */
	private double width;
	
	/** The target height */
	private double height;
	
	/** The border radius */
	private double borderRadius;
	
	public FillPane(PraisenterContext context, SlideMode mode) {
		this.context = context;
		this.mode = mode;
		this.mediaView = new MediaView();
		this.paintView = new VBox();
		
		this.mediaView.setMouseTransparent(true);
		this.paintView.setMouseTransparent(true);
		
		this.getChildren().addAll(this.paintView, this.mediaView);
	}
	
	public void setSize(double w, double h) {
		this.setPrefSize(w, h);
		this.setMinSize(w, h);
		this.setMaxSize(w, h);
		this.width = w;
		this.height = h;
		this.setPaintViewSize();
		this.setMediaViewSize();
	}
	
	public void setBorderRadius(double r) {
		this.borderRadius = r;
		this.setPaintViewSize();
		this.setMediaViewSize();
	}
	
	public void setPaint(SlidePaint paint) {
		if (paint == null) {
			removePaint();
			return;
		}
		
		// what's the paint type
		if (paint instanceof MediaObject) {
			setMediaObject((MediaObject)paint);
		} else if (paint instanceof SlideColor ||
				   paint instanceof SlideLinearGradient ||
				   paint instanceof SlideRadialGradient) {
			setBackgroundPaint(paint);
		} else {
			LOGGER.warn("Unknown paint type " + paint.getClass().getName());
		}
	}

	private void removePaint() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			player.stop();
			player.dispose();
		}
		this.mediaView.setMediaPlayer(null);
		this.paintView.setBackground(null);
		this.image = null;
		this.media = null;
		this.scaleType = null;
	}
	
	private void setPaintViewSize() {
		Fx.setSize(this.paintView, this.width, this.height);
		Rectangle r = new Rectangle(0, 0, this.width, this.height);
		if (this.borderRadius > 0) {
			r.setArcHeight(this.borderRadius * 2);
			r.setArcWidth(this.borderRadius * 2);
		}
		this.paintView.setClip(r);
	}
	
	private void setMediaViewSize() {
		double w = this.width;
		double h = this.height;
		double br = this.borderRadius;
		
		double mw = 0.0;
		double mh = 0.0;
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			mw = player.getMedia().getWidth();
			mh = player.getMedia().getHeight();
		}
		
		Rectangle clip = new Rectangle(0, 0, w, h);
		if (br > 0) {
			clip.setArcHeight(br * 2);
			clip.setArcWidth(br * 2);
		}
		
		// reset
		this.mediaView.setFitWidth(0);
		this.mediaView.setFitHeight(0);
		this.mediaView.setPreserveRatio(true);
		
		if (this.scaleType == ScaleType.NONUNIFORM) { 
			this.mediaView.setFitWidth(w);
			this.mediaView.setFitHeight(h);
			this.mediaView.setPreserveRatio(false);
		} else if (this.scaleType == ScaleType.UNIFORM) {
			// set the fit w/h based on the min
			if (w < h) {
				this.mediaView.setFitWidth(w);
			} else {
				this.mediaView.setFitHeight(h);
			}
		} else {
			// then center it
			this.mediaView.setLayoutX((w - mw) * 0.5);
			this.mediaView.setLayoutY((h - mh) * 0.5);
			// need to set a clip if its bigger than the component
			clip.setX(-(w - mw) * 0.5);
			clip.setY(-(h - mh) * 0.5);
		}
		this.mediaView.setClip(clip);
	}
	
	private void setBackgroundPaint(SlidePaint paint) {
		this.removePaint();
		
		Paint bgPaint = JavaFXTypeConverter.toJavaFX(paint);
		Background background = new Background(new BackgroundFill(bgPaint, new CornerRadii(this.borderRadius), null));
		this.paintView.setBackground(background);
	}
	
	private void setMediaObject(MediaObject mo) {
		// get the media
		Media media = null;
		UUID id = mo.getId();
		if (id != null) {
			media = this.context.getMediaLibrary().get(id);
		}
		
		if (media == null) {
			// this could happen if the media is moved or deleted
			this.removePaint();
		} else {
			MediaType type = media.getType();
			// they are the same media item
			this.scaleType = mo.getScaling();
			
			// did the media change
			if (!media.equals(this.media)) {
				// if so, we need to just start from scratch
				this.removePaint();
				
				// set data
				this.media = media;
				
				// create new image if we are in edit mode or if the media type is image
				if (this.mode == SlideMode.EDIT ||
					this.mode == SlideMode.SNAPSHOT ||
					this.mode == SlideMode.PREVIEW ||
					type == MediaType.IMAGE) {
					this.image = JavaFXTypeConverter.toJavaFXImage(this.context.getMediaLibrary(), this.context.getImageCache(), media);
					Background background = new Background(new BackgroundImage(
							this.image, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, 
							JavaFXTypeConverter.toJavaFX(mo.getScaling())));
					this.paintView.setBackground(background);
				} else {
					// otherwise create a media player
					MediaPlayer player = JavaFXTypeConverter.toJavaFXMediaPlayer(media, mo.isLoop(), mo.isMute());
					this.mediaView.setMediaPlayer(player);
					setMediaViewSize();
				}
			} else {
				// set player settings based on the given media
				MediaPlayer player = this.mediaView.getMediaPlayer();
				if (player != null) {
					player.setCycleCount(mo.isLoop() ? MediaPlayer.INDEFINITE : 1);
					player.setMute(mo.isMute());
				}
				setMediaViewSize();
				
				// the scale type may have changed
				if (this.image != null) {
					Background background = new Background(new BackgroundImage(
							this.image, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, 
							JavaFXTypeConverter.toJavaFX(mo.getScaling())));
					this.paintView.setBackground(background);
				}
			}
		}
	}

	// playable stuff
	
	public void play() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			player.play();
		}
	}
	
	public void stop() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			player.stop();
		}
	}
	
	public void dispose() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			player.dispose();
		}
	}
}
