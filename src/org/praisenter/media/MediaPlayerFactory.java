package org.praisenter.media;

/**
 * Interface representing a class that can create {@link MediaPlayer}s.
 * @param <E> the {@link PlayableMedia} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MediaPlayerFactory<E extends PlayableMedia> {
	/**
	 * Returns true if the given media class is supported by this {@link MediaPlayer}.
	 * @param clazz the type
	 * @return boolean
	 */
	public abstract <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz);
	
	/**
	 * Creates and returns a new {@link MediaPlayer}.
	 * @return {@link MediaPlayer}
	 */
	public MediaPlayer<E> createMediaPlayer();
}
