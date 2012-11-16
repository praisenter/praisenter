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
public interface PlayableMediaComponent<E extends PlayableMedia> extends SlideComponent, MediaComponent<E>, MediaPlayerListener {}
