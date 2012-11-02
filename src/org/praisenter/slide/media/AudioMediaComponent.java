package org.praisenter.slide.media;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;

// FIXME AUDIO we will worry about later
public class AudioMediaComponent extends AbstractMediaComponent<AbstractAudioMedia> implements SlideComponent, PositionedSlideComponent, MediaComponent<AbstractAudioMedia>, TimedMediaComponent<AbstractAudioMedia> {
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AudioMediaComponent(int width, int height) {
		this(0, 0, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AudioMediaComponent(int x, int y, int width, int height) {
		this(x, y, width, height, null);
	}

	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param media the image media
	 */
	public AudioMediaComponent(int x, int y, int width, int height, AbstractAudioMedia media) {
		super(x, y, width, height);
		this.media = media;
	}

}
