package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.display.ScaleType;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.VideoMediaPlayerListener;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideComponentCopyException;

/**
 * Component for showing videos from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "VideoMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class VideoMediaComponent extends AbstractImageMediaComponent<AbstractVideoMedia> implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<AbstractVideoMedia>, PlayableMediaComponent<AbstractVideoMedia>, MediaPlayerListener, VideoMediaPlayerListener {
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
	/** The current frame */
	protected BufferedImage currentFrame;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected VideoMediaComponent() {
		super(null, 0, 0, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param media the video media
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(AbstractVideoMedia media, int width, int height) {
		this(media, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param media the video media
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(AbstractVideoMedia media, int x, int y, int width, int height) {
		super(media, x, y, width, height);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 * @throws SlideComponentCopyException thrown if the media could not be copied
	 */
	public VideoMediaComponent(VideoMediaComponent component) throws SlideComponentCopyException {
		super(component);
		this.audioMuted = component.audioMuted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public VideoMediaComponent copy() throws SlideComponentCopyException {
		return new VideoMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		BufferedImage image = this.currentFrame;
		if (image == null) {
			image = this.media.getFirstFrame();
		}
		return image;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getPreviewImage()
	 */
	@Override
	public BufferedImage getPreviewImage() {
		if (this.media != null) {
			return this.media.getFirstFrame();
		} else {
			return null;
		}
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
		// since video is opaque don't bother rendering the background
		this.renderScaledFrame(g);
		this.renderBorder(g);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// since video is opaque don't bother rendering the background
		this.renderScaledFrame(g);
		this.renderBorder(g);
	}
	
	/**
	 * Renders the scaled image to the given graphics object.
	 * @param g the graphics object to render to
	 */
	protected void renderScaledFrame(Graphics2D g) {
		if (this.currentFrame == null && this.media != null) {
			this.currentFrame = this.media.getFirstFrame();
		}
		if (this.currentFrame != null) {
			// setup the clip for this component
			Shape oClip = g.getClip();
			g.setClip(this.x, this.y, this.width, this.height);

			// compute the image dimensions
			int iw = this.currentFrame.getWidth();
			int ih = this.currentFrame.getHeight();
			
			if (iw != this.width || ih != this.height) {
				double sw = (double)this.width / (double)this.currentFrame.getWidth();
				double sh = (double)this.height / (double)this.currentFrame.getHeight();
				if (this.scaleType == ScaleType.UNIFORM) {
					if (sw < sh) {
						iw = this.width;
						ih = (int)Math.round(sw * this.currentFrame.getHeight());
					} else {
						iw = (int)Math.round(sh * this.currentFrame.getWidth());
						ih = this.height;
					}
				} else if (this.scaleType == ScaleType.NONUNIFORM) {
					iw = this.width;
					ih = this.height;
				}
				// center the image
				int x = (this.width - iw) / 2;
				int y = (this.height - ih) / 2;
				g.drawImage(this.currentFrame, this.x + x, this.y + y, iw, ih, null);
			} else {
				g.drawImage(this.currentFrame, this.x, this.y, null);
			}
			
			g.setClip(oClip);
		}
	}
	
	/**
	 * Returns true if the audio is muted.
	 * @return boolean
	 */
	public boolean isAudioMuted() {
		return this.audioMuted;
	}

	/**
	 * Sets the audio to muted or not.
	 * @param audioMuted true if the audio should be muted
	 */
	public void setAudioMuted(boolean audioMuted) {
		this.audioMuted = audioMuted;
	}
}
