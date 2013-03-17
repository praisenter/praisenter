/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
 * @version 2.0.0
 * @since 2.0.0
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
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String, java.lang.String)
	 */
	@Override
	public AbstractAudioMedia load(String basePath, String filePath) throws MediaException {
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
	        AudioMediaFile file = new AudioMediaFile(
	        		basePath,
	        		filePath, 
	        		"midi", 
	        		sequence.getMicrosecondLength() / 1000 / 1000);
	        // create the media
	        return new MidiAudioMedia(file);
		} catch (FileNotFoundException e) {
			throw new MediaException(e);
		} catch (InvalidMidiDataException e) {
			throw new MediaException(e);
		} catch (IOException e) {
			throw new MediaException(e);
		}
	}
}
