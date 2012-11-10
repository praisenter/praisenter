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

import org.praisenter.thread.PausableThread;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;

public class XugglerAudioPlayerThread extends PausableThread {
	protected SourceDataLine line;
	protected BlockingDeque<byte[]> queue;
	
	public XugglerAudioPlayerThread() {
		super("XugglerAudioPlayerThread");
		// unbound blocking queue
		this.queue = new LinkedBlockingDeque<>();
	}
	
	public void initialize(IStreamCoder audioCoder) {
		if (this.line != null) {
			this.line.close();
		}
		
		boolean formatSupported = false;
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
				formatSupported = true;
			}
		} else {
			formatSupported = true;
		}
		
		// verify the format is supported
		if (formatSupported) {
			try{
				// create and open JavaSound
				this.line = (SourceDataLine)AudioSystem.getLine(info);
				this.line.open(format);
				this.line.start();
			} catch (LineUnavailableException e) {
//				LOGGER.warn("Audio line not available: ", e);
				this.line = null;
			}
		} else {
			this.line = null;
		}
	}
	
	public void queue(byte[] samples) {
		// add the item to the queue
		// this will notify this thread if its
		// waiting for an item from the queue
		this.queue.offer(samples);
	}
	
	/**
	 * Plays any remaining data from the queue.
	 * <p>
	 * This method will block until all the audio has been played.
	 */
	public void drain() {
		if (this.queue.size() > 0) {
			if (this.line != null) {
				List<byte[]> sink = new ArrayList<>();
				this.queue.drainTo(sink);
				for (byte[] data : sink) {
					this.line.write(data, 0, data.length);
				}
				this.line.drain();
			} else {
				this.flush();
			}
		}
	}
	
	/**
	 * Clears the samples queue even if all samples have
	 * not been played.
	 */
	public void flush() {
		this.queue.clear();
		if (this.line != null) {
			this.line.flush();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.thread.PausableThread#end()
	 */
	@Override
	public void end() {
		// TODO Auto-generated method stub
		super.end();
		
		if (this.line != null) {
			this.line.close();
		}
	}
	
	@Override
	protected void executeTask() {
		try {
			byte[] data = this.queue.take();
			// make sure we have data and a line to play the data
			if (data != null && this.line != null) {
				this.line.write(data, 0, data.length);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
