/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.PositionedComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.xml.MediaTypeAdapter;

/**
 * Component for showing videos from the media library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "VideoMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class VideoMediaComponent extends GenericComponent implements SlideComponent, RenderableComponent, PositionedComponent, MediaComponent<AbstractVideoMedia>, PlayableMediaComponent<AbstractVideoMedia>, MediaPlayerListener, VideoMediaPlayerListener {
	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected AbstractVideoMedia media;

	/** The video scale type */
	@XmlAttribute(name = "ScaleType", required = false)
	protected ScaleType scaleType;
	
	/** True if looping is enabled */
	@XmlAttribute(name = "LoopEnabled", required = true)
	protected boolean loopEnabled;
	
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
	/** True if the video is visible */
	@XmlElement(name = "VideoVisible", required = false, nillable = true)
	protected boolean videoVisible;
	
	/** The current frame */
	protected BufferedImage currentFrame;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected VideoMediaComponent() {
		this(null, null, 0, 0, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param media the video media
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(String name, AbstractVideoMedia media, int width, int height) {
		this(name, media, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param media the video media
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(String name, AbstractVideoMedia media, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		this.media = media;
		this.scaleType = ScaleType.NONUNIFORM;
		this.loopEnabled = false;
		this.audioMuted = false;
		this.videoVisible = true;
		this.currentFrame = null;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public VideoMediaComponent(VideoMediaComponent component) {
		super(component);
		this.media = component.media;
		this.scaleType = component.scaleType;
		this.loopEnabled = component.loopEnabled;
		this.audioMuted = component.audioMuted;
		this.videoVisible = component.videoVisible;
		this.currentFrame = component.currentFrame;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericComponent#copy()
	 */
	@Override
	public VideoMediaComponent copy() {
		return new VideoMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericComponent#isTransitionRequired(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public boolean isTransitionRequired(RenderableComponent component) {
		if (component == null) return true;
		if (this == component) return false;
		
		// check the type
		if (component instanceof VideoMediaComponent) {
			VideoMediaComponent other = (VideoMediaComponent)component;
			// the media must be the same
			if (this.videoVisible && other.isVideoVisible()) {
				if (this.media != null && other.getMedia() != null) {
					if (!this.media.equals(other.getMedia())) {
						// the media items are not the same so we have to transition
						return true;
					}
				} else if (this.media != null || other.getMedia() != null) {
					// one is not null
					return true;
				}
			} else if (this.videoVisible || other.isVideoVisible()) {
				// one is visible and the other isn't, so we have to transition
				return true;
			}
			
			// test the scaling type
			if (this.scaleType != other.getScaleType()) {
				return true;
			}
			
			// the audio muting and loop enabled will be copied in a transition
			// and do not need to be checked here
		} else {
			// not the same type, so we must transition
			return true;
		}
		
		// else, pass on the test to the super class
		return super.isTransitionRequired(component);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#setMedia(org.praisenter.media.Media)
	 */
	@Override
	public void setMedia(AbstractVideoMedia media) {
		this.media = media;
		this.currentFrame = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	@Override
	public AbstractVideoMedia getMedia() {
		return this.media;
	}

	/**
	 * Returns the first frame of this video component.
	 * @return BufferedImage
	 */
	public BufferedImage getFirstFrame() {
		if (this.media != null) {
			return this.media.getFirstFrame();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the current frame of this video component or
	 * the first frame if the current frame is null.
	 * <p>
	 * The current frame will be updated if this component
	 * is added as a {@link VideoMediaPlayerListener} to a 
	 * {@link MediaPlayer}.
	 * @return BufferedImage
	 */
	public BufferedImage getCurrentFrame() {
		BufferedImage image = this.currentFrame;
		if (image == null) {
			return this.getFirstFrame();
		}
		return image;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayerListener#onVideoImage(java.awt.image.BufferedImage)
	 */
	@Override
	public void onVideoImage(BufferedImage image) {
		this.currentFrame = image;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// render the background
		if (this.backgroundVisible) {
			this.renderBackground(g, this.x, this.y);
		}
		// render the image
		if (this.videoVisible) {
			this.renderScaledFrame(g, this.getCurrentFrame());
		}
		// the border needs to be rendered last on top of the other renderings
		if (this.borderVisible) {
			this.renderBorder(g);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// render the background
		if (this.backgroundVisible) {
			this.renderBackground(g, this.x, this.y);
		}
		// render the image
		if (this.videoVisible) {
			this.renderScaledFrame(g, this.getFirstFrame());
		}
		// the border needs to be rendered last on top of the other renderings
		if (this.borderVisible) {
			this.renderBorder(g);
		}
	}
	
	/**
	 * Renders the scaled image to the given graphics object.
	 * @param g the graphics object to render to
	 * @param image the image to render scaled
	 */
	protected void renderScaledFrame(Graphics2D g, BufferedImage image) {
		if (image != null) {
			// setup the clip for this component
			Shape oClip = g.getClip();
			g.clipRect(this.x, this.y, this.width, this.height);

			// compute the image dimensions
			int iw = image.getWidth();
			int ih = image.getHeight();
			
			if (iw != this.width || ih != this.height) {
				double sw = (double)this.width / (double)iw;
				double sh = (double)this.height / (double)ih;
				if (this.scaleType == ScaleType.UNIFORM) {
					if (sw < sh) {
						iw = this.width;
						ih = (int)Math.ceil(sw * ih);
					} else {
						iw = (int)Math.ceil(sh * iw);
						ih = this.height;
					}
				} else if (this.scaleType == ScaleType.NONUNIFORM) {
					iw = this.width;
					ih = this.height;
				}
				// center the image
				int x = (this.width - iw) / 2;
				int y = (this.height - ih) / 2;
				g.drawImage(image, this.x + x, this.y + y, iw, ih, null);
			} else {
				g.drawImage(image, this.x, this.y, null);
			}
			
			g.setClip(oClip);
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setLoopEnabled(boolean)
	 */
	@Override
	public void setLoopEnabled(boolean loopEnabled) {
		this.loopEnabled = loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isLoopEnabled()
	 */
	@Override
	public boolean isLoopEnabled() {
		return this.loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isAudioMuted()
	 */
	public boolean isAudioMuted() {
		return this.audioMuted;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setAudioMuted(boolean)
	 */
	public void setAudioMuted(boolean audioMuted) {
		this.audioMuted = audioMuted;
	}

	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getScaleType() {
		return this.scaleType;
	}

	/**
	 * Sets the image scale type.
	 * @param scaleType the scale type
	 */
	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}
	
	/**
	 * Returns true if the video is visible.
	 * @return boolean
	 */
	public boolean isVideoVisible() {
		return this.videoVisible;
	}

	/**
	 * Toggles the visibility of the video.
	 * @param visible true if the video should be visible
	 */
	public void setVideoVisible(boolean visible) {
		this.videoVisible = visible;
	}
}
