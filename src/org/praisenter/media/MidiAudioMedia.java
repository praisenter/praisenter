package org.praisenter.media;

import java.awt.Dimension;

import org.praisenter.images.Images;

/**
 * Concrete class for Midi audio media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MidiAudioMedia extends AbstractAudioMedia {
	/**
	 * Full constructor.
	 * @param file the file information
	 */
	public MidiAudioMedia(AudioMediaFile file) {
		super(file);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		return new MediaThumbnail(file, Images.MIDI_AUDIO, MediaType.AUDIO);
	}
}
