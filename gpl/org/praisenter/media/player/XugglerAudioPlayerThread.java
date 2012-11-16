package org.praisenter.media.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;
import org.praisenter.thread.PausableThread;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Represents a thread used to play audio using JavaSound.
 * <p>
 * This class is not designed to be used separately from the {@link XugglerMediaPlayer} class.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerAudioPlayerThread extends PausableThread {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(XugglerAudioPlayerThread.class);
	
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
		// clean up the line
		if (this.line != null) {
			this.line.close();
		}
		// since we could be blocking on the queue
		// we need to interrupt it to notify of the state change
		this.interrupt();
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
