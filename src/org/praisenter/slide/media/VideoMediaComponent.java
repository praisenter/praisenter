package org.praisenter.slide.media;

import java.awt.image.BufferedImage;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Component for showing videos from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class VideoMediaComponent extends AbstractImageMediaComponent<AbstractVideoMedia> implements SlideComponent, PositionedSlideComponent, MediaComponent<AbstractVideoMedia>, TimedMediaComponent<AbstractVideoMedia> {
	/** True if the audio should be muted */
	protected boolean audioMuted;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(int width, int height) {
		this(0, 0, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public VideoMediaComponent(int x, int y, int width, int height) {
		this(x, y, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param media the video media
	 */
	public VideoMediaComponent(int x, int y, int width, int height, AbstractVideoMedia media) {
		super(x, y, width, height);
		this.media = media;
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
