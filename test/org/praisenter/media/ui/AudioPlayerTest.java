package org.praisenter.media.ui;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MediaPlayerFactory;
import org.praisenter.media.MidiAudioMedia;
import org.praisenter.media.NoMediaLoaderException;

public class AudioPlayerTest {
	public static void main(String[] args) throws NoMediaLoaderException, MediaException {
		MediaLibrary.loadMediaLibrary();
		
		// test the midi player
		MidiAudioMedia media = (MidiAudioMedia)MediaLibrary.getMedia("media\\audio\\green_day-the_forgotten.mid");
		MediaPlayerFactory<?> factory = MediaLibrary.getMediaPlayerFactory(MidiAudioMedia.class);
		MediaPlayer player = factory.createMediaPlayer();
		player.setMedia(media);
		player.play();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.stop();
		player.release();
		
		// test the xuggler sampled audio player
		AbstractAudioMedia aMedia = (AbstractAudioMedia)MediaLibrary.getMedia("media\\audio\\04 This Is the Life.wma");
		factory = MediaLibrary.getMediaPlayerFactory(aMedia.getClass());
		player = factory.createMediaPlayer();
		player.setMedia(aMedia);
		player.play();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		player.stop();
		player.release();
	}
}
