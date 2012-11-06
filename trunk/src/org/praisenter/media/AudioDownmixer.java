package org.praisenter.media;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a helper class for JavaSound.  JavaSound may not find an output line that
 * can handle certain formats (specifically multi-channel output above stereo) so this class
 * is provided to allow java sound to play the audio, but with loss of quality.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class AudioDownmixer {
	/** The buffer size in bytes (made to be large enough for 18 channels at 24-bit (3 bytes) samples) */
	private static final int BUFFER_SIZE = 3 * 18;
	
	/** The thread local buffer */
	private static final ThreadLocal<ByteBuffer> LOCAL_BUFFER = new ThreadLocal<ByteBuffer>();
	
	/**
	 * Down mixes the given samples to stereo using the same bit depth.
	 * <p>
	 * This method currently only handles up to 6 channels.
	 * <p>
	 * If the data is already stereo or mono, this method just returns the data.
	 * @param data the data to down mix
	 * @param bitDepth the bit depth of the data (8-bit, 16-bit, 24-bit, etc.)
	 * @param channels the number of channels (4, 6, etc)
	 * @param order the byte order of the data
	 * @return byte[]
	 */
	public static byte[] downmixToStereo(byte[] data, int bitDepth, int channels, ByteOrder order) {
		// make sure the byte buffer exists
		ByteBuffer buffer = LOCAL_BUFFER.get();
		if (buffer == null) {
			// create the buffer
			buffer = ByteBuffer.allocate(BUFFER_SIZE);
			LOCAL_BUFFER.set(buffer);
		}
		// make sure the buffer is ordered properly
		buffer.order(order);
		// no need to down mix if audio is already 2 or less channels
		if (channels <= 2) return data;
		// compute the number of bytes per channel
		int depthInBytes = bitDepth / 8;
		// compute the number of bytes per sample
		int channelLength = depthInBytes * channels;
		// create the output byte array (the original length / channels * 2 channels)
		byte[] output = new byte[data.length / channels * 2];
		int k = 0;
    	for (int i = 0; i < data.length; i += channelLength) {
    		int j = 0;
    		// read 1 samples data
    		buffer.put(data, i, channelLength);
    		
    		// get the front left
    		int frontLeft = getData(buffer, j, depthInBytes);
    		// get the front right
    		int frontRight = getData(buffer, j += depthInBytes, depthInBytes);
    		
    		int rearLeft = 0, rearRight = 0, centerFront = 0, lfe = 0;
    		// get the rear left/right
    		if (channels > 2) {
    			rearLeft = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		if (channels > 3) {
    			rearRight = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		
    		// get the center front
    		if (channels > 4) {
    			centerFront = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		
    		// get the sub (low-frequency-effects)
    		if (channels > 5) {
    			lfe = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		
    		// down mix
    		short l = (short)Math.floor((rearLeft  - (rearRight * 0.5) + (centerFront * 0.5) + frontLeft  - (lfe * 0.5)) * 0.5);
    		short r = (short)Math.floor((rearRight - (rearLeft * 0.5)  + (centerFront * 0.5) + frontRight - (lfe * 0.5)) * 0.5);
    		
    		// clear the buffer
    		buffer.clear();
    		
    		// put the stereo sound in the buffer
    		buffer.putShort(l);
    		buffer.putShort(r);
    		
    		// get it back out as ordered bytes
    		output[k++] = buffer.get(0);
    		output[k++] = buffer.get(1);
    		output[k++] = buffer.get(2);
    		output[k++] = buffer.get(3);
    		
    		// clear the buffer once more
    		buffer.clear();
    	}
    	
    	// return the stereo output
    	return output;
	}
	
	/**
	 * Get the int value of the given channel sample using the given size.
	 * <p>
	 * Returns zero if the size is less than 1 or greater than 3.
	 * @param buf the buffer
	 * @param index the index in the buffer
	 * @param size the size of the data (1, 2, or 3 byte)
	 * @return int
	 */
	private static final int getData(ByteBuffer buf, int index, int size) {
		if (size == 1) {
			return buf.get(index) & 0xFFFFFFFF;
		} else if (size == 2) {
			return buf.getShort(index) & 0xFFFFFFFF;
		} else if (size == 3) {
			return buf.getInt(index);
		} else {
			return 0;
		}
	}
}
