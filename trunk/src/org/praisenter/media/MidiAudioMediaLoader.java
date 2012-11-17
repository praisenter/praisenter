package org.praisenter.media;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

/**
 * Midi loader using JavaSound.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MidiAudioMediaLoader implements MediaLoader<AbstractAudioMedia>, AudioMediaLoader {
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<? extends Media> getMediaType() {
		return MidiAudioMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		if ("audio/midi".equalsIgnoreCase(mimeType)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String)
	 */
	@Override
	public AbstractAudioMedia load(String filePath) throws MediaException {
		try {
			// use the class loader that loaded this class to load the resource
			InputStream inputStream = new FileInputStream(new File(filePath));
	    	// if the stream does not support marking then use a 
	    	// buffered input stream
	        if (!inputStream.markSupported()) {
	            inputStream = new BufferedInputStream(inputStream);
	        }
	        // get the sequence using the default midi system
	        Sequence sequence = MidiSystem.getSequence(inputStream);
	        // close the input stream
	        inputStream.close();
	        // get the media file properties
	        AudioMediaFile file = new AudioMediaFile(filePath, "midi", sequence.getMicrosecondLength() / 1000 / 1000);
	        // create the media
	        return new MidiAudioMedia(file);
	        // FIXME translated messages
		} catch (FileNotFoundException e) {
			throw new MediaException(e);
		} catch (InvalidMidiDataException e) {
			throw new MediaException(e);
		} catch (IOException e) {
			throw new MediaException(e);
		}
	}
}
