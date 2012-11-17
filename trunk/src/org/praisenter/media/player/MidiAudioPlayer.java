/*
 * Copyright (c) 2009, William Bittle
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
 *   * Neither the name of William Bittle nor the names of its contributors may be used to endorse or 
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
package org.praisenter.media.player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import org.apache.log4j.Logger;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaPlayer;
import org.praisenter.media.MidiAudioMedia;

/**
 * Represents an audio player for Midi audio.
 * <p>
 * Some implementations/distributions of the JRE do not come with a sound bank.  A sound bank
 * is needed for midi playback.  This class will first attempt to use the sound bank supplied
 * in the constructor.  If the given sound bank is not supported, the class will fall back to the
 * sound bank distributed with the JRE.  If it doesnt exist or isnt supported then the class 
 * will fall back to the hardware sound bank.
 * <p>
 * Setting the volume for a midi must be done after the midi audio has started.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MidiAudioPlayer extends AbstractAudioPlayer<MidiAudioMedia> implements MediaPlayer<MidiAudioMedia>, MetaEventListener {
	/** Logger for the class */
	private static final Logger LOGGER = Logger.getLogger(MidiAudioPlayer.class);

	/** Midi max volume {@value #MIDI_MAX_VOLUME} */
    protected static final double MIDI_MAX_VOLUME = 127.0d;
    
    /** The volume controller {@value #VOLUME_CONTROLLER} */
    protected static final int VOLUME_CONTROLLER = 7;
    
    /** The maximum number of channels {@value #MAX_NUMBER_OF_CHANNELS} */
    protected static final int MAX_NUMBER_OF_CHANNELS = 16;
	
	/** The end of track event message = {@value #END_OF_TRACK_MESSAGE} */
    protected static final int END_OF_TRACK_MESSAGE = 47;

    /** The midi sequencer */
    protected Sequencer sequencer = null;
    
    /** The synthesizer */
    protected Synthesizer synthesizer = null;
    
    /** The receiver to send messages to */
    protected Receiver receiver = null;
	
	/** The midi sequence */
	protected Sequence sequence = null;

	/** The current position in the midi sequence when paused */
	protected long microsecondPosition = 0;

	/**
	 * Default constructor.
	 */
	public MidiAudioPlayer() {}

	@Override
	public synchronized boolean setMedia(MidiAudioMedia media) {
		// assign the media
		super.setMedia(media);
		try {
			this.initializeMedia();
			return true;
		} catch (MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized void initializeMedia() throws MediaException {
		// attempt to load the sequence
		try {
			// use the class loader that loaded this class to load the resource
			InputStream inputStream = new FileInputStream(new File(media.getFile().getPath()));
	    	// if the stream does not support marking then use a 
	    	// buffered input stream
	        if (!inputStream.markSupported()) {
	            inputStream = new BufferedInputStream(inputStream);
	        }
	        // get the sequence using the default midi system
	        this.sequence = MidiSystem.getSequence(inputStream);
	        // close the input stream
	        inputStream.close();
		} catch (FileNotFoundException e) {
			
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
	    	// get the default system sequencer, initially not connected
	        this.sequencer = MidiSystem.getSequencer(false);
	        // attempt to get the default system synthesizer
	        this.synthesizer = MidiSystem.getSynthesizer();
	        // setup the midi sound bank
	        boolean setup = false;
	        // attempt to use the passed in sound bank
//	        if (this.soundbank != null && this.synthesizer.isSoundbankSupported(this.soundbank)) {
//	    		this.synthesizer.loadAllInstruments(this.soundbank);
//	    		setup = true;
//	        }
	        // attempt the default sound bank
	        if (!setup && this.synthesizer.getDefaultSoundbank() != null) {
	        	LOGGER.info("");
//	        	this.soundbank = this.synthesizer.getDefaultSoundbank();
	        	setup = true;
	        }
	        // attempt the hardware sound bank
			if (!setup) {
				LOGGER.warn("");
				// if the packaged one didnt work try the hardware one
	            this.synthesizer = null;
	        	// if it does have a sound bank then use the default synthesizer
	        	this.receiver = MidiSystem.getReceiver();
			}
			// if the hardware soundbank is not being used then open the synthesizer and
			// get the default receiver from it
			if (this.synthesizer != null) {
	    		// open the synthesizer
	    		this.synthesizer.open();
	    		// get the reciever for later use
	    		this.receiver = this.synthesizer.getReceiver();
			}
	        // open the sequencer
	        this.sequencer.open();
	        // link the sequencer to the synthesizer
	        this.sequencer.getTransmitter().setReceiver(this.receiver);
	        // set this class to list for meta events
	        // (end of track event in particular)
	        this.sequencer.addMetaEventListener(this);
	        // set the state to ready
	        this.state = State.STOPPED;
	        // log that the audio is ready
	        LOGGER.debug("");
		} catch (MidiUnavailableException e) {
			LOGGER.error(e);
			throw new MediaException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#stop()
	 */
	@Override
	public synchronized void stop() {
		// make sure its in the playing or paused state
		if (this.state == State.PLAYING || this.state == State.PAUSED) {
			// set the audio to the open state
			this.state = State.STOPPED;
			// stop the sequencer
			this.sequencer.stop();
			// reset the audio
			this.microsecondPosition = 0;
			// log that the audio has been stopped
	        LOGGER.debug("");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#release()
	 */
	@Override
	public synchronized void release() {
    	// close the sequencer, synthesizer, and receiver
    	if (this.sequencer != null && this.sequencer.isOpen()) {
	    	 this.sequencer.close();
	    }
	    if (this.synthesizer != null && this.synthesizer.isOpen()) {
	    	this.synthesizer.close();
	    }
	    if (this.receiver != null) {
	    	this.receiver.close();
	    }
	    // set the state to closed
	    this.state = State.ENDED;
	    
		this.sequencer = null;
		this.synthesizer = null;
		this.receiver = null;
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#pause()
	 */
	@Override
	public synchronized void pause() {
    	// make sure something is playing
		// (we shouldnt pass the state check if the sequencer is null)
    	if (this.state == State.PLAYING && this.sequencer.isRunning()) {
	    	// set the state to paused
    		this.state = State.PAUSED;
    		// stop the sequencer
			this.sequencer.stop();
	    	// save the microsecond position
	    	this.microsecondPosition = this.sequencer.getMicrosecondPosition();
    	}
		// log that the audio has been paused
        LOGGER.debug("");
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#resume()
	 */
	@Override
	public synchronized void resume() {
    	// make sure we are paused before we do anything
		// (we shouldnt pass the state check if the sequencer is null)
    	if (this.state == State.PAUSED) {
    		// set the state to playing
    		this.state = State.PLAYING;
    		// start the sequencer
			this.sequencer.start();
			// set the microsecond position back to zero
			this.microsecondPosition = 0;
    	}
		// log that the audio has been resumed
        LOGGER.debug("");
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#play()
	 */
	@Override
	public synchronized void play() {
		try {
			if (this.state == State.PAUSED) {
	    		// set the state to playing
	    		this.state = State.PLAYING;
	    		// start the sequencer
				this.sequencer.start();
				// set the microsecond position back to zero
				this.microsecondPosition = 0;
	    	} else if (this.state == State.STOPPED) {
	        	// give the sequencer the sequence
				this.sequencer.setSequence(this.sequence);
	        	// set the microsecond position
	        	this.sequencer.setMicrosecondPosition(this.microsecondPosition);
	    		// start the sequencer
				this.sequencer.start();
				// the player is now playing
				this.state = State.PLAYING;
				// set the volume
				this.setVolume(this.volume);
	    	}
		} catch (InvalidMidiDataException e) {
			LOGGER.error(e);
			// close the audio
			this.stop();
		}
		// log that the audio has been started
        LOGGER.debug("");
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaPlayer#seek(long)
	 */
	@Override
	public synchronized void seek(long position) {
		this.sequencer.setMicrosecondPosition(position);
	}
	
	protected void loop() {
    	this.sequencer.setTickPosition(0);
    	this.sequencer.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.sound.midi.MetaEventListener#meta(javax.sound.midi.MetaMessage)
	 */
	@Override
	public void meta(MetaMessage event) {
    	// we are only worried about the end of track meta message
        if (event.getType() == MidiAudioPlayer.END_OF_TRACK_MESSAGE) {
        	// make sure the audio should loop
            if (this.getConfiguration().isLoopEnabled()) {
            	this.loop();
            	// log that the audio is being looped
    	        LOGGER.debug("");
            } else {
            	this.stop();
            }
        	// set the volume again
        	this.setVolume(this.volume);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#setVolume(double)
	 */
	@Override
	public synchronized void setVolume(double volume) {
		int midiVolume = 0;
		if (volume >= AbstractAudioPlayer.MIN_VOLUME && volume <= AbstractAudioPlayer.MAX_VOLUME) {
			super.setVolume(volume);
			midiVolume = (int) (MidiAudioPlayer.MIDI_MAX_VOLUME * (volume / AbstractAudioPlayer.MAX_VOLUME));
		} else {
			// if its not in range log a message and return
//			LOGGER.log(Level.INFO, "org.codezealot.game.audio.volumeOutOfRange", new double[] {volume, AbstractAudioPlayer.MIN_VOLUME, AbstractAudioPlayer.MIN_VOLUME});
			
			return;
		}
		// make sure the player is not closed
		if (this.state != State.STOPPED) {
			// make sure the synthesizer is not null
			if (this.synthesizer != null) {
				// if its not null, then we know we can use the synthesizer to 
				// change the volume
				MidiChannel[] channels = this.synthesizer.getChannels();
				// set the master volume for each channel
				for (int i = 0; i < channels.length; i++) {
					// change the percent value to a respective gain value
					channels[i].controlChange(MidiAudioPlayer.VOLUME_CONTROLLER, midiVolume);
				}
				// log that the audio volume has been changed
//    	        LOGGER.log(Level.FINEST, "org.codezealot.game.audio.volumeChanged", volume);
			} else if (this.receiver != null) {
				// if the sequencer is null, then we are using the receiver
				// create a short message
				ShortMessage volMessage = new ShortMessage();
				// loop through the channels
				for (int i = 0; i < MidiAudioPlayer.MAX_NUMBER_OF_CHANNELS; i++) {
					try {
						// set the message to a control change event to 
						// change the volume
						volMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, MidiAudioPlayer.VOLUME_CONTROLLER, midiVolume);
					} catch (InvalidMidiDataException e) {
						// if an error occurs, just log it and continue
						// this could be caused by less than 16 channels
//						LOGGER.log(Level.INFO, "org.codezealot.game.audio.midi.controlChange.invalidData", new int[] {MidiAudioPlayer.VOLUME_CONTROLLER, i, midiVolume});
					}
					// send the message
					this.receiver.send(volMessage, -1);
				}
				// log that the audio volume has been changed
//    	        LOGGER.log(Level.FINEST, "org.codezealot.game.audio.volumeChanged", volume);
			} else {
				// should never happen, but just in case, log a message
				LOGGER.info("");
			}
		}
	}

	/**
	 * Sets the microsecond position.
	 * <p>
	 * If this method is called while the audio is playing the microsecond position will
	 * be overridden.  To set the microsecond position without this happening, one must
	 * call the {@link #pause()} method, then this method, then the {@link #resume()} method.
	 * @param microsecondPosition the microsecond position in the midi
	 */
	public synchronized void setMicrosecondPosition(long microsecondPosition) {
		this.microsecondPosition = microsecondPosition;
	}
	
	/**
	 * This will return the microsecond position of the sequencer.
	 * @return long
	 */
	public long getMicrosecondPosition() {
		if (this.state == AbstractAudioPlayer.State.PLAYING) {
			return this.sequencer.getMicrosecondPosition();
		} else {
			return this.microsecondPosition;
		}
	}
}
