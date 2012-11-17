package org.praisenter.slide.media;

import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.PlayableMedia;
import org.praisenter.slide.SlideComponent;

/**
 * Interface representing a media component that must run while being displayed (video, audio, etc).
 * @param <E> the {@link PlayableMedia} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PlayableMediaComponent<E extends PlayableMedia> extends SlideComponent, MediaComponent<E>, MediaPlayerListener {
	/**
	 * Returns true if looping of the media is enabled.
	 * @return boolean
	 */
	public abstract boolean isLoopEnabled();
	
	/**
	 * Sets looping of the media to true or false.
	 * @param loopEnabled true if looping should be enabled
	 */
	public abstract void setLoopEnabled(boolean loopEnabled);
	
	/**
	 * Returns true if the audio is muted.
	 * @return boolean
	 */
	public boolean isAudioMuted();

	/**
	 * Sets the audio to muted or not.
	 * @param audioMuted true if the audio should be muted
	 */
	public void setAudioMuted(boolean audioMuted);
}
