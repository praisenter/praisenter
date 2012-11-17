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
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class represents a sampled audio object.
 * @author William Bittle
 * @version $Revision: 358 $
 */
public class Sampled extends AbstractAudioPlayer implements Runnable {
	/** The logger for the class */
	private static final Logger LOGGER = Logger.getLogger(Sampled.class.getPackage().getName(), "messages");
	
	/** The default executor service */
	public static final ExecutorService DEFAULT_EXECUTOR_SERVICE = new ThreadPoolExecutor(32, 32, 0, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
	
	/** The original audio stream */
	protected AudioInputStream audioInputStream = null;
	
	/** The input stream to use for sound play back */
	protected InputStream inputStream = null;

    /** The line to feed sound data to */
	protected SourceDataLine line = null;
    
    /** The buffer containing sound data */
	protected byte[] buffer = null;
	
	/** The thread pool that this audio will use to play itself */
	protected ExecutorService executorService = null;
	
	/**
	 * Full constructor.
	 * @param audioInputStream the audio stream
	 */
	public Sampled(AudioInputStream audioInputStream) {
		super();
		this.audioInputStream = audioInputStream;
		this.executorService = Sampled.DEFAULT_EXECUTOR_SERVICE;
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#open()
	 */
	@Override
	public synchronized void open() throws ResourceUnavailableException {
		// only open if the audio is in the closed state
		if (this.state == AbstractAudioPlayer.State.CLOSED) {
			// get the audio format
			AudioFormat format = this.audioInputStream.getFormat();
	    	// calculate the buffer size
	        int bufferSize = format.getFrameSize() * Math.round(format.getSampleRate() / 10);
	        // create the line
	        SourceDataLine line = null;
	        // create the line info
	        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
	        try {
	        	// open the line
	            line = (SourceDataLine) AudioSystem.getLine(lineInfo);
	            line.open(format, bufferSize);
		        // start the line
		        line.start();
		        // set the line and buffer
		        this.line = line;
		        this.buffer = new byte[bufferSize];
		        // attempt to create the input stream for playback
		        this.inputStream = this.getInputStream();
		        // set the state to ready
				this.state = AbstractAudioPlayer.State.OPEN;
				// log that the audio is ready
		        LOGGER.finest("org.codezealot.game.audio.ready");
	        } catch (LineUnavailableException e) {
				this.release();
				LOGGER.warning("org.codezealot.game.audio.sampled.open.resourcesUnavailable");
				LOGGER.throwing(this.getClass().getName(), "open", e);
				throw new ResourceUnavailableException(e);
	        }
		}
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#close()
	 */
	@Override
	public synchronized void close() {
		// make sure its not already closed
		if (this.state != AbstractAudioPlayer.State.CLOSED) {
			// change it
			this.state = AbstractAudioPlayer.State.CLOSED;
			// drain the line and close it
        	this.line.drain();
        	this.line.close();
        	// close the input stream
        	try {
				this.inputStream.close();
			} catch (IOException e) {
				LOGGER.warning("org.codezealot.game.audio.sampled.close.ioException");
				LOGGER.throwing(this.getClass().getName(), "close", e);
			}
        	this.release();
    		// notify all threads waiting on this lock that its available
    		this.notify();
        	// log that the audio has been closed
	        LOGGER.finest("org.codezealot.game.audio.closed");
		}
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#release()
	 */
	@Override
	protected synchronized void release() {
		this.inputStream = null;
		this.line = null;
		this.buffer = null;
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#pause()
	 */
	@Override
	public synchronized void pause() {
		// dont do anything if the state is not changing
        if (this.state == AbstractAudioPlayer.State.PLAYING) {
			// once we have it, change it
			this.state = AbstractAudioPlayer.State.PAUSED;
			// log that the audio has been paused
	        LOGGER.finest("org.codezealot.game.audio.paused");
		}
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#play()
	 */
	@Override
	public synchronized void play() {
		// check the state
		if (this.state == AbstractAudioPlayer.State.PLAYING) {
			// if its already playing then reset it
			this.stop();
			this.play();
		} else if (this.state == AbstractAudioPlayer.State.PAUSED) {
			// if its paused then resume it
			this.state = AbstractAudioPlayer.State.PLAYING;
			// notify a thread waiting on this lock that its available
			this.notify();
		} else if (this.state == AbstractAudioPlayer.State.OPEN) {
			// if its paused then resume it
			this.state = AbstractAudioPlayer.State.PLAYING;
			// if its in the ready state then play it normally
			this.executorService.execute(this);
		}
		// log that the audio has been started
        LOGGER.finest("org.codezealot.game.audio.started");
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#resume()
	 */
	@Override
	public synchronized void resume() {
		// dont do anything if the state is not changing
		if (this.isPaused()) {
			// once we have it, change it
			this.state = AbstractAudioPlayer.State.PLAYING;
			// notify a thread waiting on this lock that its available
			this.notify();
			// log that the audio has been resumed
	        LOGGER.finest("org.codezealot.game.audio.resumed");
		}
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#stop()
	 */
	@Override
	public synchronized void stop() {
		// make sure its playing or paused
		if (this.state == AbstractAudioPlayer.State.PLAYING || this.state == AbstractAudioPlayer.State.PAUSED) {
			// set the state to ready
			this.state = AbstractAudioPlayer.State.OPEN;
			// reset the loops
			this.reset();
			// log that the audio has been stopped
	        LOGGER.finest("org.codezealot.game.audio.stopped");
		}
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#reset()
	 */
	@Override
	protected synchronized void reset() {
		try {
			// attempt to reset the input stream
			this.inputStream.reset();
		} catch (IOException e) {
			LOGGER.warning("org.codezealot.game.audio.sampled.reset.ioException");
			LOGGER.throwing(this.getClass().getName(), "reset", e);
		}
		this.completedLoops = 0;
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#setVolume(double)
	 */
	@Override
	public synchronized void setVolume(double volume) {
		if (volume >= AbstractAudioPlayer.MIN_VOLUME && volume <= AbstractAudioPlayer.MAX_VOLUME) {
			super.setVolume(volume);
		} else {
			// if its not in range log a message and return
			LOGGER.log(Level.INFO, "org.codezealot.game.audio.volumeOutOfRange", new double[] {volume, AbstractAudioPlayer.MIN_VOLUME, AbstractAudioPlayer.MIN_VOLUME});
			return;
		}
		// make sure the audio is still open
		if (!this.isClosed()) {
    		// calculate the new volume
    		double volumePercent = getVolume() / AbstractAudioPlayer.MAX_VOLUME;
			// determine what type of volume control to use
			if (this.line.isControlSupported(FloatControl.Type.VOLUME)) {
				// create the volume control
				FloatControl volumeControl = (FloatControl) this.line.getControl(FloatControl.Type.VOLUME);
				// only update the volume if it has changed
				if (volumeControl.getValue() != volumePercent) {
					volumeControl.setValue((float) volumePercent);
				}
				// log that the audio volume has been changed
    	        LOGGER.log(Level.FINEST, "org.codezealot.game.audio.volumeChanged", volume);
			} else if (this.line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				// create the volume control
				FloatControl gainControl = (FloatControl) this.line.getControl(FloatControl.Type.MASTER_GAIN);
				// calculate the decible value
				float dB = (float) (Math.log(volumePercent == 0.0 ? 0.0001 : volumePercent) / Math.log(10.0) * 20.0);
				// only update the volume if it has changed
				if (dB != gainControl.getValue()) {
					gainControl.setValue(dB);
				}
				// log that the audio volume has been changed
    	        LOGGER.log(Level.FINEST, "org.codezealot.game.audio.volumeChanged", volume);
			} else {
				LOGGER.info("org.codezealot.game.audio.volumeControlNotSupported");
			}
    	}
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.audio.Audio#loop()
	 */
	@Override
	protected synchronized void loop() {
		super.loop();
		try {
			this.inputStream.reset();
		} catch (IOException e) {
			LOGGER.warning("org.codezealot.game.audio.sampled.reset.ioException");
			LOGGER.throwing(this.getClass().getName(), "loop", e);
		}
	}
	
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
	@Override
    public void run() {
        try {
            // copy data to the line
            int numBytesRead = 0;
            // read the stream to play the sound
            while (numBytesRead != -1) {
            	// obtain the lock on the monitor
            	synchronized (this) {
					if (this.state == AbstractAudioPlayer.State.PAUSED) {
						// flush the stale data out
						this.line.flush();
						// if it has then wait for it to be notified to start again
						this.wait();
					} else if (this.state == AbstractAudioPlayer.State.OPEN || this.state == AbstractAudioPlayer.State.CLOSED) {
						// if it was put in either the ready or closed state then
						// break from the loop
						break;
					}
	            	// set the volume
	            	this.setVolume(this.volume);
	            	// copy data to the buffer
	                numBytesRead = this.inputStream.read(this.buffer, 0, this.buffer.length);
	                // write the data to the line
	                if (numBytesRead != -1) {
	                	this.line.write(this.buffer, 0, numBytesRead);
	                } else {
	                	// if we hit the end of the stream see if we need to loop
	                	if (this.canLoop()) {
	                		this.loop();
	                    	numBytesRead = 0;
	                		// log that the audio is being looped
	            	        LOGGER.finest("org.codezealot.game.audio.loop");
	                	}
	                }
				}
            	Thread.yield();
            }
        } catch (IOException e) {
        	// log the error
        	LOGGER.warning("org.codezealot.game.audio.sampled.play.ioException");
        	LOGGER.throwing(this.getClass().getName(), "run", e);
        } catch (InterruptedException e) {
        	// log the error
        	LOGGER.warning("org.codezealot.game.audio.sampled.play.interruptedException");
        	LOGGER.throwing(this.getClass().getName(), "run", e);
        }
        
        this.stop();
    }
    
	/**
	 * Creates samples given the format.
	 */
	private byte[] getSamples() {
		AudioInputStream ais = this.audioInputStream;
        // get the number of bytes to read
        int length = (int)(ais.getFrameLength() * ais.getFormat().getFrameSize());
        // mark the stream at the very beginning
        ais.mark(length);
        // create the samples byte array
        byte[] samples = new byte[length];
        int totalBytesRead = 0;
        try {
	        // loop through the stream reading all the bytes
	        while (totalBytesRead < length) {
	        	// read a frame from the stream and increment the number of bytes read
	        	totalBytesRead += this.audioInputStream.read(samples, totalBytesRead, ais.getFormat().getFrameSize());
	        }
	        // reset the stream so that the audio can be converted into a different format
	        ais.reset();
        } catch (IOException e) {
        	// if an exception occurs attempt to continue, skipping the rest of the data
        	LOGGER.warning("org.codezealot.game.audio.sampled.open.ioException");
        	LOGGER.throwing(this.getClass().getName(), "getSamples", e);
        }
        // create the input stream
        return samples;
	}
	
	/**
	 * Returns the input stream for this sampled audio object.
	 * @return InputStream
	 */
	protected InputStream getInputStream() {
        return new ByteArrayInputStream(this.getSamples());
	}
	
	/**
	 * Reads in the audio from the given file name and path.
	 * @param file the file name and path of the audio
	 * @return AudioInputStream
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException if an IO error occurs
	 * @throws UnsupportedAudioFileException if the audio file type is not supported
	 */
	public static AudioInputStream loadAudioInputStream(String file) throws FileNotFoundException, IOException, UnsupportedAudioFileException {
		InputStream inputStream = Sampled.class.getResource(file).openStream();
    	// if the stream does not support marking then use a buffered
    	// input stream
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        // get the audio stream
        return AudioSystem.getAudioInputStream(inputStream);
	}
}
