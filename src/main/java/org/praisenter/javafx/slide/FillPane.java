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
import org.praisenter.MediaType;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.converters.EffectConverter;
import org.praisenter.javafx.slide.converters.MediaConverter;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.media.MediaObject;

import javafx.scene.effect.Effect;
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
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * A custom pane that can show an color, gradient, image, video, etc.
 * @author William Bittle
 * @version 3.0.0
 */
final class FillPane extends StackPane implements Playable {
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
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 * @param mode the slide mode
	 */
	public FillPane(PraisenterContext context, SlideMode mode) {
		this.context = context;
		this.mode = mode;
		this.mediaView = new MediaView();
		this.paintView = new VBox();
		
		this.mediaView.setMouseTransparent(true);
		this.paintView.setMouseTransparent(true);
		
		this.getChildren().addAll(this.paintView, this.mediaView);
	}
	
	/**
	 * Sets the size of the pane to the desired width and height.
	 * @param width the width
	 * @param height the height
	 */
	public void setSize(double width, double height) {
		this.setPrefSize(width, height);
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		this.width = width;
		this.height = height;
		this.setPaintViewSize();
		this.setMediaViewSize();
	}
	
	/**
	 * Sets the border radius of the pane.
	 * @param radius the radius
	 */
	public void setBorderRadius(double radius) {
		this.borderRadius = radius;
		this.setPaintViewSize();
		this.setMediaViewSize();
	}
	
	/**
	 * Sets the paint of the pane, which could be a {@link SlideColor},
	 * {@link SlideGradient}, or {@link MediaObject}.
	 * @param paint the paint
	 */
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

	/**
	 * Removes both the paint and the media and clears
	 * any other values for the current display.
	 */
	private void removePaint() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			player.stop();
			player.dispose();
		}
		this.mediaView.setMediaPlayer(null);
		this.mediaView.setEffect(null);
		this.paintView.setBackground(null);
		this.paintView.setEffect(null);
		this.image = null;
		this.media = null;
		this.scaleType = null;
	}
	
	/**
	 * Sets the paint view's size.
	 */
	private void setPaintViewSize() {
		Fx.setSize(this.paintView, this.width, this.height);
		Rectangle r = new Rectangle(0, 0, this.width, this.height);
		if (this.borderRadius > 0) {
			r.setArcHeight(this.borderRadius * 2);
			r.setArcWidth(this.borderRadius * 2);
		}
		this.paintView.setClip(r);
	}
	
	/**
	 * Sets the MediaView's size based on the desired height, width and
	 * scale type.
	 */
	private void setMediaViewSize() {
		// get the viewport dimensions
		double w = this.width;
		double h = this.height;
		double br = this.borderRadius;
		
		// build a clip based on the border radius
		Rectangle clip = new Rectangle(0, 0, w, h);
		if (br > 0) {
			clip.setArcHeight(br * 2);
			clip.setArcWidth(br * 2);
		}
		
		// get the media dimensions
		double mw = 0.0;
		double mh = 0.0;
		if (this.media != null) {
			mw = this.media.getWidth();
			mh = this.media.getHeight();
		}

		// reset
		this.mediaView.setFitWidth(0);
		this.mediaView.setFitHeight(0);
		this.mediaView.setPreserveRatio(true);
		
		// determine how to position and present the video
		// based on the scale type
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
	
	/**
	 * Sets the paint for this pane - which will override any media.
	 * @param paint the paint
	 */
	private void setBackgroundPaint(SlidePaint paint) {
		this.removePaint();
		
		Paint bgPaint = PaintConverter.toJavaFX(paint);
		Background background = new Background(new BackgroundFill(bgPaint, new CornerRadii(this.borderRadius), null));
		this.paintView.setBackground(background);
	}
	
	/**
	 * Sets the media for this pane - which will override any paint.
	 * @param mo the media object
	 */
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
			// did the media change
			if (!media.equals(this.media)) {
				// if so, we need to just start from scratch
				this.removePaint();
			
				MediaType type = media.getType();
				this.scaleType = mo.getScaling();
				
				// set data
				this.media = media;
				
				// create new image if we are in edit mode or if the media type is image
				if (this.mode == SlideMode.EDIT ||
					this.mode == SlideMode.SNAPSHOT ||
					this.mode == SlideMode.PREVIEW ||
					type == MediaType.IMAGE) {
					this.image = MediaConverter.toJavaFXImage(this.context.getImageCache(), media);
					Background background = new Background(new BackgroundImage(
							this.image, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, 
							MediaConverter.toJavaFX(mo.getScaling())));
					this.paintView.setBackground(background);
				} else {
					// otherwise create a media player
					MediaPlayer player = MediaConverter.toJavaFXMediaPlayer(
							media, 
							mo.isLoop(), 
							this.mode == SlideMode.PREVIEW_NO_AUDIO ? true : mo.isMute());
					this.mediaView.setMediaPlayer(player);
					setMediaViewSize();
				}
			} else {
				this.scaleType = mo.getScaling();
				
				// set player settings based on the given media
				MediaPlayer player = this.mediaView.getMediaPlayer();
				if (player != null) {
					player.setCycleCount(mo.isLoop() ? MediaPlayer.INDEFINITE : 1);
					player.setMute(this.mode == SlideMode.PREVIEW_NO_AUDIO ? true : mo.isMute());
				}
				setMediaViewSize();
				
				// the scale type may have changed
				if (this.image != null) {
					Background background = new Background(new BackgroundImage(
							this.image, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundRepeat.NO_REPEAT, 
							BackgroundPosition.CENTER, 
							MediaConverter.toJavaFX(mo.getScaling())));
					this.paintView.setBackground(background);
				}
			}
			
			// either way lets update the color adjust effect
			Effect effect = EffectConverter.toJavaFX(mo.getColorAdjust());
			this.paintView.setEffect(effect);
			this.mediaView.setEffect(effect);
		}
	}

	// playable stuff
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#play()
	 */
	public void play() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			if (player.getStatus() == Status.PLAYING || 
				player.getStatus() == Status.PAUSED ||
				player.getStatus() == Status.STALLED) {
				try {
					player.stop();
				} catch (Exception ex) {
					LOGGER.error("Failed to stop playing media " + this.media.getName() + " at " + this.media.getFileName() + " on request to play.", ex);
				}
			}
			try {
				player.play();
			} catch (Exception ex) {
				LOGGER.error("Failed to play media " + this.media.getName() + " at " + this.media.getFileName(), ex);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#stop()
	 */
	public void stop() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			try {
				player.stop();
			} catch (Exception ex) {
				LOGGER.error("Failed to stop media " + this.media.getName() + " at " + this.media.getFileName(), ex);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#dispose()
	 */
	public void dispose() {
		MediaPlayer player = this.mediaView.getMediaPlayer();
		if (player != null) {
			try {
				player.dispose();
				this.mediaView.setMediaPlayer(null);
			} catch (Exception ex) {
				LOGGER.error("Failed to dispose media " + this.media.getName() + " at " + this.media.getFileName(), ex);
			}
		}
	}
}
