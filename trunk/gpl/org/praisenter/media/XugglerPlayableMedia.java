package org.praisenter.media;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Represents playable Xuggler media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface XugglerPlayableMedia extends Media, PlayableMedia {
	/**
	 * Returns the media container format object.
	 * @return IContainer
	 */
	public abstract IContainer getContainer();
	
	/**
	 * Returns the stream coder for the audio stream.
	 * <p>
	 * Returns null if no stream available.
	 * @return IStreamCoder
	 */
	public abstract IStreamCoder getAudioCoder();
}
