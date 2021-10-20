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
package org.praisenter.javafx.slide.converters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.MediaType;
import org.praisenter.javafx.ImageCache;
import org.praisenter.media.Media;
import org.praisenter.slide.graphics.ScaleType;

import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.MediaPlayer;

/**
 * Class with a collection of media related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaConverter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** private constructor */
	private MediaConverter() {}
	
	/**
	 * Returns a BackgroundSize object for the given {@link ScaleType}.
	 * @param scaling the scale type
	 * @return BackgroundSize
	 */
	public static BackgroundSize toJavaFX(ScaleType scaling) {
		BackgroundSize size = BackgroundSize.DEFAULT;
		if (scaling == ScaleType.NONUNIFORM) {
			size = new BackgroundSize(1.0, 1.0, true, true, false, false);
		} else if (scaling == ScaleType.UNIFORM) {
			size = new BackgroundSize(0.0, 0.0, false, false, true, false);
		}
		return size;
	}
	
	/**
	 * Returns a {@link ScaleType} for the given BackgroundSize.
	 * @param size the size
	 * @return {@link ScaleType}
	 */
	public static ScaleType fromJavaFX(BackgroundSize size) {
		if (size == null || size == BackgroundSize.DEFAULT) {
			return ScaleType.NONE;
		} else if (!size.isContain() && !size.isCover() && size.isWidthAsPercentage() && size.isHeightAsPercentage()) {
			return ScaleType.NONUNIFORM;
		} else if (size.isContain() && !size.isCover() && !size.isWidthAsPercentage() && !size.isHeightAsPercentage()) {
			return ScaleType.UNIFORM;
		}
		return ScaleType.NONE;
	}
	
	/**
	 * Returns a Java FX MediaPlayer for the given media and settings.
	 * <p>
	 * This method is only for audio or video media and will return null if it's any other
	 * type of media.
	 * @param media the media
	 * @param loop true if the media should loop continuously
	 * @param mute true if the media should be muted
	 * @return MediaPlayer
	 */
	public static MediaPlayer toJavaFXMediaPlayer(Media media, boolean loop, boolean mute) {
		// check for missing media
		if (media == null) {
			return null;
		}
		// check the type
		if (media.getType() != MediaType.AUDIO && media.getType() != MediaType.VIDEO) {
			return null;
		}
		try {
			// attempt to open the media
			javafx.scene.media.Media m = new javafx.scene.media.Media(media.getPath().toUri().toString());
			m.setOnError(() -> { LOGGER.error(m.getError()); });
			
			// create a player
			MediaPlayer player = new MediaPlayer(m);
			// set the player attributes
			player.setMute(mute);
			player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 0);
			player.setOnError(() -> { LOGGER.error(player.getError()); });
			
			return player;
		} catch (Exception ex) {
			// if it blows up, then just log the error
			LOGGER.error("Failed to create media or media player.", ex);
		}
		
		return null;
	}
	
	/**
	 * Returns a Java FX Image for the given media.
	 * <p>
	 * Returns null if the media is null, not found, or could not be converted to a Java FX image.
	 * <p>
	 * This method works for all media types.
	 * @param imageCache the image cache
	 * @param media the media
	 * @return Image
	 */
	public static Image toJavaFXImage(ImageCache imageCache, Media media) {
		// check for missing media
		if (media == null) {
			return null;
		}
		
		try {
			// check the media type
			if (media.getType() == MediaType.VIDEO) {
				// for video's we just need to show a single frame
				return imageCache.getOrLoadVideoMediaFrame(media.getId(), media.getFramePath());
			} else if (media.getType() == MediaType.IMAGE) {
				// image itself
				return imageCache.getOrLoadImageMediaImage(media.getId(), media.getPath());
			} else if (media.getType() == MediaType.AUDIO) {
				// a default image of sorts
				return imageCache.getOrLoadApplicationImage("/org/praisenter/resources/music-default-thumbnail.png");
			} else {
				LOGGER.error("Unknown media type " + media.getType());
			}
		} catch (Exception ex) {
			LOGGER.error("Failed to load image for media '" + media.getId() + "' '" + media.getPath() + "'", ex);
		}
		
		return null;
	}
}
