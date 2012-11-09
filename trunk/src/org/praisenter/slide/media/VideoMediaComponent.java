package org.praisenter.slide.media;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Component for showing videos from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "VideoMediaComponent")
public class VideoMediaComponent extends AbstractImageMediaComponent<AbstractVideoMedia> implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<AbstractVideoMedia>, TimedMediaComponent<AbstractVideoMedia> {
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
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
	 */
	public VideoMediaComponent(VideoMediaComponent component) {
		super(component);
		this.audioMuted = component.audioMuted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public VideoMediaComponent copy() {
		return new VideoMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getPreviewImage()
	 */
	@Override
	public BufferedImage getPreviewImage() {
		return this.media.getFirstFrame();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		return this.media.getCurrentFrame();
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
