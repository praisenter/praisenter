package org.praisenter.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Utility class for common formatting operations.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class Formatter {
	/** The time format */
	private static final NumberFormat TIME_FORMAT = new DecimalFormat("00");

	/** Only shows one decimal place */
	private static final NumberFormat ONE_DECIMAL_PLACE = new DecimalFormat("0.0");
	
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
	public static final String getLengthFormattedString(long length) {
		long hours = length / 3600;
		long minutes = (length % 3600) / 60;
		long seconds = (length % 3600) % 60;
		return TIME_FORMAT.format(hours) + ":" + TIME_FORMAT.format(minutes) + ":" + TIME_FORMAT.format(seconds);
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
