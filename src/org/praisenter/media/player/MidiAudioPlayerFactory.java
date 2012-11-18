package org.praisenter.media.player;

import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.MidiAudioMedia;
import org.praisenter.media.PlayableMedia;

/**
 * Factory class for creating {@link MidiAudioPlayer}s.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MidiAudioPlayerFactory implements MediaPlayerFactory<MidiAudioMedia> {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayerFactory#isTypeSupported(java.lang.Class)
	 */
	@Override
	public <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz) {
		if (MidiAudioMedia.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayerFactory#createMediaPlayer()
	 */
	@Override
	public MediaPlayer<MidiAudioMedia> createMediaPlayer() {
		return new MidiAudioPlayer();
	}
}
