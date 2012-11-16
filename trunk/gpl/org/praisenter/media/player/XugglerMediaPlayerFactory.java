package org.praisenter.media.player;

import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.PlayableMedia;
import org.praisenter.media.XugglerPlayableMedia;

/**
 * Factory class for creating media player's for {@link XugglerPlayableMedia} media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerMediaPlayerFactory implements MediaPlayerFactory<XugglerPlayableMedia> {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#isTypeSupported(java.lang.Class)
	 */
	@Override
	public <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz) {
		if (XugglerPlayableMedia.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayerFactory#createMediaPlayer()
	 */
	@Override
	public MediaPlayer<XugglerPlayableMedia> createMediaPlayer() {
		return new XugglerMediaPlayer();
	}
}
