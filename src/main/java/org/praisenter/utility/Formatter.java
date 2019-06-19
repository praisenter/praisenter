/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Class used to format various data for readability.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Formatter {
	/** The time format */
	private static final NumberFormat TIME_FORMAT = new DecimalFormat("00");

	/** Only shows one decimal place */
	private static final NumberFormat ONE_DECIMAL_PLACE = new DecimalFormat("0.0");
	
	/** Hidden default constructor */
	private Formatter() {}
	
	/** Simple scale for bytes */
	private static final long[] BYTE_SCALE = new long[] {
		1024, // 1 kilobyte
		1024 * 1024, // 1 megabyte
		1024 * 1024 * 1024, // 1 gigabyte
	};
	
	/**
	 * Returns a formatted time string for the given length.
	 * @param length the length in seconds
	 * @return String
	 */
	public static final String getSecondsFormattedString(long length) {
		long hours = length / 3600;
		long minutes = (length % 3600) / 60;
		long seconds = (length % 3600) % 60;
		return TIME_FORMAT.format(hours) + ":" + TIME_FORMAT.format(minutes) + ":" + TIME_FORMAT.format(seconds);
	}
	
	/**
	 * Returns a formatted time string for the given length.
	 * @param length the length in milliseconds
	 * @return String
	 */
	public static final String getTimeMillisecondsFormattedString(long length) {
		long minutes = length / 60000;
		long seconds = (length % 60000) / 1000;
		long milliseconds = (length % 60000) % 1000;
		return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
	}
	
	/**
	 * Returns a formatted time string for the given length.
	 * @param length the length in milliseconds
	 * @return String
	 */
	public static final String getMillisecondsFormattedString(long length) {
		long minutes = length / 60000;
		long seconds = (length % 60000) / 1000;
		long milliseconds = (length % 60000) % 1000;
		if (minutes > 0) {
			if (milliseconds > 0) {
				return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
			} else {
				return String.format("%d:%02d", minutes, seconds);
			}
		} else if (seconds > 0) {
			if (length % 1000  > 0) {
				return String.format("%.1fs", length / 1000.0);
			} else {
				return String.format("%ds", length / 1000);
			}
		} else if (milliseconds > 0) {
			return String.format("%dms", milliseconds);
		} else {
			return "0";
		}
	}
	
	/**
	 * Returns a formatted size string for the given size.
	 * @param size the size in bytes
	 * @return String
	 */
	public static final String getSizeFormattedString(long size) {
		if (size < BYTE_SCALE[0]) {
			return size + " B";
		} else if (size < BYTE_SCALE[1]) {
			double ds = size;
			double dc = BYTE_SCALE[0];
			double r = ds / dc;
			return ONE_DECIMAL_PLACE.format(r) + " KB";
		} else if (size < BYTE_SCALE[2]) {
			double ds = size;
			double dc = BYTE_SCALE[1];
			double r = ds / dc;
			return ONE_DECIMAL_PLACE.format(r) + " MB";
		} else if (size < BYTE_SCALE[3]) {
			double ds = size;
			double dc = BYTE_SCALE[2];
			double r = ds / dc;
			return ONE_DECIMAL_PLACE.format(r) + " GB";
		} else {
			return String.valueOf(size);
		}
	}
}