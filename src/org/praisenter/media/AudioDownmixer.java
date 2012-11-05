package org.praisenter.media;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioDownmixer {
	// 3 bytes (24-bit) per channel (18 channel max)
	private static final int MAXIMUM_BUFFER_SIZE = 3 * 18;
	private static final ThreadLocal<ByteBuffer> LOCAL_BUFFER = new ThreadLocal<>();
	
	public static byte[] downmixToStereo(byte[] data, int bitDepth, int channels) {
		// make sure the byte buffer exists
		ByteBuffer buffer = LOCAL_BUFFER.get();
		if (buffer == null) {
			buffer = ByteBuffer.allocate(MAXIMUM_BUFFER_SIZE);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			LOCAL_BUFFER.set(buffer);
		}
		buffer.clear();
		// no need to down mix if audio is already 2 or less channels
		if (channels <= 2) return data;
		// compute the number of bytes per channel
		int depthInBytes = bitDepth / 8;
		// compute the number of bytes per sample
		int channelLength = depthInBytes * channels;
		// create the output byte array (the original length / channels * 2 channels)
		byte[] output = new byte[data.length / channels * 2];
//    	ByteBuffer buf = ByteBuffer.allocate();
//    	buf.order(ByteOrder.LITTLE_ENDIAN);
//    	ByteBuffer tmp = ByteBuffer.allocate(channelLength);
//    	tmp.order(ByteOrder.LITTLE_ENDIAN);
		int k = 0;
    	for (int i = 0; i < data.length; i += channelLength) {
    		int j = 0;
    		// read 1 samples data
    		buffer.put(data, i, channelLength);
    		
    		// get the front left/right
    		int lf = getData(buffer, j, depthInBytes); //tmp.getShort(0) & 0xFFFFFFFF;
    		int rf = getData(buffer, j += depthInBytes, depthInBytes); //tmp.getShort(2) & 0xFFFFFFFF;
    		
    		int lr = 0, rr = 0, cf = 0, su = 0;
    		// get the rear left/right
    		if (channels > 2) {
    			lr = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		if (channels > 3) {
    			rr = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		
    		// get the center front
    		if (channels > 4) {
    			cf = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    		
    		// get the sub
    		if (channels > 5) {
    			su = getData(buffer, j += depthInBytes, depthInBytes);
    		}
    				
//    		short l = (short)(lf + 0.71 * cf + 0.71 * su - lr);
//    		short r = (short)(rf + 0.71 * cf + 0.71 * su - rr);
    		
    		short l = (short)((lr - (rr * 0.5) + (cf * 0.5) + lf - (su * 0.5)) * 0.5);
    		short r = (short)((rr - (lr * 0.5) + (cf * 0.5) + rf - (su * 0.5)) * 0.5);
    		
//    		buf.putShort(l);
//    		buf.putShort(r);
    		buffer.clear();
    		
    		buffer.putShort(l);
    		buffer.putShort(r);
    		output[k++] = buffer.get(0);
    		output[k++] = buffer.get(1);
    		output[k++] = buffer.get(2);
    		output[k++] = buffer.get(3);
    		
    		buffer.clear();
    	}
    	
    	return output;
	}
	
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
