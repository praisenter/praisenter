package org.praisenter.media.ui;

import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.MidiAudioMedia;
import org.praisenter.media.NoMediaLoaderException;

public class AudioPlayerTest {
	public static void main(String[] args) throws NoMediaLoaderException, MediaException {
		MidiAudioMedia media = (MidiAudioMedia)MediaLibrary.getMedia("media\\audio\\costa.mid");
		MediaPlayerFactory<?> factory = MediaLibrary.getMediaPlayerFactory(MidiAudioMedia.class);
		MediaPlayer player = factory.createMediaPlayer();
		player.setMedia(media);
		player.play();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.stop();
		player.release();
	}
}
