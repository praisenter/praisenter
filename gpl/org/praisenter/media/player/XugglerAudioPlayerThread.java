/*
 * Praisenter: A free open source church presentation software.
 * Copyright (C) 2012-2013  William Bittle  http://www.praisenter.org/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.praisenter.media.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.praisenter.common.threading.PausableThread;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Represents a thread used to play audio using JavaSound.
 * <p>
 * This class is not designed to be used separately from the {@link XugglerMediaPlayer} class.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class XugglerAudioPlayerThread extends PausableThread {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerAudioPlayerThread.class);
	
	/** The max volume for the audio = {@value #MAX_VOLUME} */
	public static final double MAX_VOLUME = 100.0;
	
	/** The min volume for the audio = {@value #MIN_VOLUME} */
	public static final double MIN_VOLUME = 0.0;
	
	/** The JavaSound line to play the samples */
	protected SourceDataLine line;
	
	/** A blocking queue to play the samples */
	protected BlockingDeque<byte[]> queue;
	
	/**
	 * Default constructor.
	 */
	public XugglerAudioPlayerThread() {
		super("XugglerAudioPlayerThread");
		// unbounded blocking queue
		this.queue = new LinkedBlockingDeque<byte[]>();
	}
	
	/**
	 * Initializes this audio player thread with the given audio coder information.
	 * <p>
	 * Returns true if the no downmixing is necessary.
	 * @param audioCoder the audio coder
	 * @return boolean
	 */
	public boolean initialize(IStreamCoder audioCoder) {
		if (this.line != null) {
			this.line.close();
			this.line = null;
		}
		
		// make sure the given audio coder is not null
		// this can happen with videos that are just video
		if (audioCoder != null) {
			boolean downMixingRequired = false;
			// attempt to use the media's audio format
			AudioFormat format = new AudioFormat(
					audioCoder.getSampleRate(), 
					(int)IAudioSamples.findSampleBitDepth(audioCoder.getSampleFormat()),
					audioCoder.getChannels(), 
					true, 
					false);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			
			// see if its supported
			if (!AudioSystem.isLineSupported(info)) {
				// if its not supported, this is typically due to the number of playback channels
				// lets try the same format with just 2 channels (stereo)
				format = new AudioFormat(
						audioCoder.getSampleRate(), 
						(int)IAudioSamples.findSampleBitDepth(audioCoder.getSampleFormat()),
						2, 
						true, 
						false);
				info = new DataLine.Info(SourceDataLine.class, format);
				if (AudioSystem.isLineSupported(info)) {
					downMixingRequired = true;
				} else {
					// format just isn't supported so log it and dont play the audio
					LOGGER.warn("The audio format is not supported by JavaSound: " + format);
					this.line = null;
					return false;
				}
			}
			
			try {
				// create and open JavaSound
				this.line = (SourceDataLine)AudioSystem.getLine(info);
				this.line.open(format);
				this.line.start();
				return downMixingRequired;
			} catch (LineUnavailableException e) {
				// if a line isn't available then just dont play any sound
				// and just continue normally
				LOGGER.error("Line not available for audio playback: ", e);
				this.line = null;
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets this audio player to muted.
	 * @param flag true to mute the audio
	 */
	public void setMuted(boolean flag) {
		if (this.line != null) {
			if (this.line.isControlSupported(BooleanControl.Type.MUTE)) {
				BooleanControl control = (BooleanControl)this.line.getControl(BooleanControl.Type.MUTE);
				control.setValue(flag);
			}
		}
	}
	
	/**
	 * Returns true if the audio player is muted.
	 * <p>
	 * It's possible that JavaSound doesn't support the mute control.  In this
	 * case false will always be returned.
	 * @return boolean
	 */
	public boolean isMuted() {
		if (this.line != null) {
			if (this.line.isControlSupported(BooleanControl.Type.MUTE)) {
				BooleanControl control = (BooleanControl)this.line.getControl(BooleanControl.Type.MUTE);
				return control.getValue();
			}
		}
		// otherwise return false
		return false;
	}

	/**
	 * Sets the volume of the line.
	 * @param volume the volume
	 */
	public void setVolume(double volume) {
		if (volume < MIN_VOLUME || volume > MAX_VOLUME) {
			// if its not in range log a message and return
			LOGGER.warn("Desired volume is out of range [" + volume + "]. Volume clamped.");
			// clamp the value
			if (volume < MIN_VOLUME) {
				volume = MIN_VOLUME;
			}
			if (volume > MAX_VOLUME) {
				volume = MAX_VOLUME;
			}
		}
		// make sure the audio is still open
		if (this.line != null) {
    		// calculate the new volume
    		double volumePercent = volume / MAX_VOLUME;
			// determine what type of volume control to use
			if (this.line.isControlSupported(FloatControl.Type.VOLUME)) {
				// create the volume control
				FloatControl volumeControl = (FloatControl) this.line.getControl(FloatControl.Type.VOLUME);
				// only update the volume if it has changed
				if (volumeControl.getValue() != volumePercent) {
					volumeControl.setValue((float) volumePercent);
				}
				// log that the audio volume has been changed
				LOGGER.debug("Volume changed using VOLUME contorl.");
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
				LOGGER.debug("Volume changed using MASTER_GAIN contorl.");
			} else {
				LOGGER.warn("Volume control not supported.");
			}
    	}
	}
	
	/**
	 * Queues the given audio samples for playback.
	 * <p>
	 * This method does not block.
	 * @param samples the samples
	 */
	public void queue(byte[] samples) {
		// if the line is null, then dont bother queuing
		// data up since we can't play it anyway
		if (this.line != null) {
			// add the item to the queue
			// this will notify this thread if its
			// waiting for an item from the queue
			this.queue.offer(samples);
		}
	}
	
	/**
	 * Drains the remaining data in the buffer.
	 * <p>
	 * This method will block until all data has been written and
	 * drained from the line.
	 */
	public void drain() {
		// make sure the line is available
		if (this.line != null) {
			// copy all the data from the queue
			List<byte[]> sink = new ArrayList<byte[]>();
			this.queue.drainTo(sink);
			// write all the data to the line and drain it
			if (sink.size() > 0) {
				for (byte[] data : sink) {
					this.line.write(data, 0, data.length);
				}
				this.line.drain();
			}
		} else {
			// otherwise just clear the queue
			this.queue.clear();
		}
	}
	
	/**
	 * Clears the samples queue even if all samples have
	 * not been played.
	 * <p>
	 * This method does not block.
	 */
	public void flush() {
		this.queue.clear();
		if (this.line != null) {
			this.line.flush();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#setPaused(boolean)
	 */
	@Override
	public void setPaused(boolean flag) {
		super.setPaused(flag);
		// since we could be blocking on the queue
		// we need to interrupt it to notify of the state change
		this.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#end()
	 */
	@Override
	public void end() {
		// normal end stuff
		super.end();
		// since we could be blocking on the queue
		// we need to interrupt it to notify of the state change
		this.interrupt();
	}
	
	@Override
	protected void onThreadStopped() {
		super.onThreadStopped();
		
		// clean up the line
		if (this.line != null) {
			this.line.close();
		}
		this.line = null;
		this.queue.clear();
		this.queue = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#executeTask()
	 */
	@Override
	protected void executeTask() {
		try {
			byte[] data = this.queue.take();
			// make sure we have data and a line to play the data
			if (data != null && this.line != null) {
				this.line.write(data, 0, data.length);
			}
		} catch (InterruptedException e) {
			// if we get interrupted just continue
			LOGGER.debug("Playback interrupted.");
		}
	}
}
