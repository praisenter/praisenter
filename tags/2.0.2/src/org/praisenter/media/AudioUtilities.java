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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a helper class for JavaSound.  JavaSound may not find an output line that
 * can handle certain formats (specifically multi-channel output above stereo) so this class
 * is provided to allow java sound to play the audio, but with loss of quality.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public final class AudioUtilities {
	/** The buffer size in bytes (made to be large enough for 18 channels at 64-bit (8 bytes) samples) */
	private static final int BUFFER_SIZE = 8 * 18;
	
	/** The thread local buffer */
	private static final ThreadLocal<ByteBuffer> LOCAL_BUFFER = new ThreadLocal<ByteBuffer>();
	
	/** The maximum 24 bit signed integer value */
	private static final int SIGNED_INT_24_MAX_VALUE = (int)Math.pow(2, 23) - 1;
	
	/** The maximum 8 bit unsigned integer value */
	private static final int UNSIGNED_INT_8_MAX_VALUE = (int)Math.pow(2, 8);
	
	/** Hidden default constructor */
	private AudioUtilities() {}
	
	/**
	 * Converts the given audio data from its source format to 16bit integer bit depth.
	 * @param source the source audio data
	 * @param bitDepth the source bit depth
	 * @param channels the source number of channels
	 * @param order the source byte order
	 * @param format the source format
	 * @return byte[]
	 */
	public static final byte[] convertTo16Bit(byte[] source, int bitDepth, int channels, ByteOrder order, Class<? extends Number> format) {
		if (bitDepth == 16) return source;
		
		// make sure the byte buffer exists
		ByteBuffer buffer = LOCAL_BUFFER.get();
		if (buffer == null) {
			// create the buffer
			buffer = ByteBuffer.allocate(BUFFER_SIZE);
			LOCAL_BUFFER.set(buffer);
		}
		// make sure the buffer is ordered properly
		buffer.order(order);
		
		int depthInBytes = bitDepth / 8;
		int size = source.length / depthInBytes * 2;
		
		byte[] result = new byte[size];
		
		// go byte by byte
		int k = 0;
		for (int i = 0; i < source.length; i += depthInBytes) {
			// put the bytes in there that represent one sample on one channel
			buffer.put(source, i, depthInBytes);
			
			// interpret the bytes
			// get the sample percentage of the maximum value
			double sp = 0;
			if (format == Double.class) {
				sp = buffer.getDouble(0);
			} else if (format == Float.class) {
				sp = buffer.getFloat(0);
			} else if (depthInBytes == 4) {
				sp = (double)buffer.getInt(0) / (double)Integer.MAX_VALUE;
			} else if (depthInBytes == 3) {
				sp = (double)get24BitInt(buffer.get(0), buffer.get(1), buffer.get(2), buffer.order()) / (double)SIGNED_INT_24_MAX_VALUE;
			} else if (depthInBytes == 2) {
				sp = (double)buffer.getShort(0) / (double)Short.MAX_VALUE;
			} else {
				sp = (double)buffer.get(0) / (double)UNSIGNED_INT_8_MAX_VALUE;
			}
			
			// convert to 16 bit integer
			double sample = sp * (double)Short.MAX_VALUE;
			// clamp it
			sample = clamp(sample, -Short.MAX_VALUE, Short.MAX_VALUE);
			
			buffer.clear();
			buffer.putShort((short)sample);
			
			result[k++] = buffer.get(0);
			result[k++] = buffer.get(1);
			
			buffer.clear();
		}
		
		return result;
	}
	
	/**
	 * Down mixes the given samples to stereo using the same bit depth.
	 * <p>
	 * This method currently only handles up to 6 channels.
	 * <p>
	 * If the data is already stereo or mono, this method just returns the data.
	 * <p>
	 * This method only handles non-planar channels.
	 * @param data the data to down mix
	 * @param bitDepth the bit depth of the data (8-bit, 16-bit, 24-bit, etc.)
	 * @param channels the number of channels (4, 6, etc)
	 * @param order the byte order of the data
	 * @param format the format of the samples as a Java type
	 * @return byte[]
	 */
	public static byte[] downmixToStereo(byte[] data, int bitDepth, int channels, ByteOrder order, Class<? extends Number> format) {
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
    		
    		// check the format type
    		if (format == Float.class) {
    			// get the front left
	    		float frontLeft = buffer.getFloat(j);
	    		// get the front right
	    		float frontRight = buffer.getFloat(j += depthInBytes);
	    		
	    		float rearLeft = 0, rearRight = 0, centerFront = 0, lfe = 0;
	    		// get the rear left/right
	    		if (channels > 2) {
	    			rearLeft = buffer.getFloat(j += depthInBytes);
	    		}
	    		if (channels > 3) {
	    			rearRight = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// get the center front
	    		if (channels > 4) {
	    			centerFront = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// get the sub (low-frequency-effects)
	    		if (channels > 5) {
	    			lfe = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// down mix
	    		float l = (float)Math.floor((rearLeft  - (rearRight * 0.5) + (centerFront * 0.5) + frontLeft  - (lfe * 0.5)) * 0.5);
	    		float r = (float)Math.floor((rearRight - (rearLeft * 0.5)  + (centerFront * 0.5) + frontRight - (lfe * 0.5)) * 0.5);
	    		
	    		// clear the buffer
	    		buffer.clear();
	    		
	    		// put the stereo sound in the buffer
	    		buffer.putFloat(l);
	    		buffer.putFloat(r);
    		} else if (format == Double.class) {
    			// get the front left
	    		double frontLeft = buffer.getFloat(j);
	    		// get the front right
	    		double frontRight = buffer.getFloat(j += depthInBytes);
	    		
	    		double rearLeft = 0, rearRight = 0, centerFront = 0, lfe = 0;
	    		// get the rear left/right
	    		if (channels > 2) {
	    			rearLeft = buffer.getFloat(j += depthInBytes);
	    		}
	    		if (channels > 3) {
	    			rearRight = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// get the center front
	    		if (channels > 4) {
	    			centerFront = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// get the sub (low-frequency-effects)
	    		if (channels > 5) {
	    			lfe = buffer.getFloat(j += depthInBytes);
	    		}
	    		
	    		// down mix
	    		double l = Math.floor((rearLeft  - (rearRight * 0.5) + (centerFront * 0.5) + frontLeft  - (lfe * 0.5)) * 0.5);
	    		double r = Math.floor((rearRight - (rearLeft * 0.5)  + (centerFront * 0.5) + frontRight - (lfe * 0.5)) * 0.5);
	    		
	    		// clear the buffer
	    		buffer.clear();
	    		
	    		// put the stereo sound in the buffer
	    		buffer.putDouble(l);
	    		buffer.putDouble(r);
    		} else {
    			// then the format is either Byte, Short, or Integer (24 or 32)
	    		// get the front left
	    		double frontLeft = getData(buffer, j, depthInBytes);
	    		// get the front right
	    		double frontRight = getData(buffer, j += depthInBytes, depthInBytes);
	    		
	    		double rearLeft = 0, rearRight = 0, centerFront = 0, lfe = 0;
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
	    		double l = Math.floor((rearLeft  - (rearRight * 0.5) + (centerFront * 0.5) + frontLeft  - (lfe * 0.5)) * 0.5);
	    		double r = Math.floor((rearRight - (rearLeft * 0.5)  + (centerFront * 0.5) + frontRight - (lfe * 0.5)) * 0.5);
	    		
	    		// clear the buffer
	    		buffer.clear();
	    		
	    		// put the stereo sound in the buffer
	    		// take bit depth into account
	    		if (depthInBytes == 1) {
	    			// 8bit
	    			buffer.put((byte)l);
		    		buffer.put((byte)r);
	    		} else if (depthInBytes == 2) {
	    			// 16bit
		    		buffer.putShort((short)l);
		    		buffer.putShort((short)r);
	    		} else if (depthInBytes == 3) {
	    			// 24bit
	    			// special handling for 24 bit
	    			set24BitInt(buffer, (int)l);
	    			set24BitInt(buffer, (int)r);
	    		} else {
	    			// 32bit
	    			buffer.putInt((int)l);
		    		buffer.putInt((int)r);
	    		}
    		}
    		
    		// get it back out as ordered bytes
    		for (int m = 0; m < depthInBytes * 2; m++) {
    			output[k++] = buffer.get(m);
    		}
    		
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
	 * @param size the size of the data (1, 2, 3, or 4 byte)
	 * @return int
	 */
	private static final int getData(ByteBuffer buf, int index, int size) {
		if (size == 1) {
			// unsigned 8bit
			return buf.get(index) & 0xFFFFFFFF;
		} else if (size == 2) {
			// signed 16bit
			return buf.getShort(index);
		} else if (size == 3) {
			// signed 24bit
			return get24BitInt(buf.get(index), buf.get(index + 1), buf.get(index + 2), buf.order());
		} else {
			// signed 32bit
			return buf.getInt(index);
		}
	}
	
	/**
	 * Returns an integer for the given 3 bytes.
	 * @param b1 top byte
	 * @param b2 mid byte
	 * @param b3 low byte
	 * @param order the byte order
	 * @return int
	 */
	private static final int get24BitInt(byte b1, byte b2, byte b3, ByteOrder order) {
		if (order == ByteOrder.BIG_ENDIAN) {
			return ((b1 << 16) | ((b2 & 0xFF) << 8) | (b3 & 0xFF));
		} else {
			return ((b3 << 16) | ((b2 & 0xFF) << 8) | (b1 & 0xFF));
		}
	}
	
	/**
	 * Places the given value into the given buffer as a 3 byte integer.
	 * @param buffer the buffer
	 * @param value the value
	 */
	private static final void set24BitInt(ByteBuffer buffer, int value) {
		// check the byte order
		if (buffer.order() == ByteOrder.BIG_ENDIAN) {
			buffer.put((byte)(value >> 16));
    		buffer.put((byte)((value >>> 8) & 0xFF));
			buffer.put((byte)(value & 0xFF));
		} else {
			buffer.put((byte)(value & 0xFF));
    		buffer.put((byte)((value >>> 8) & 0xFF));
			buffer.put((byte)(value >> 16));
		}
	}
	
	/**
	 * Clamps the given value between the given min and max.
	 * @param value the value to clamp
	 * @param min the minimum
	 * @param max the maximum
	 * @return double
	 */
	private static final double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} 
		return value;
	}
}
