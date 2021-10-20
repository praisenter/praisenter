package org.praisenter.utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class Formatter {
	private static final NumberFormat TIME_FORMAT = new DecimalFormat("00");
	private static final NumberFormat ONE_DECIMAL_PLACE = new DecimalFormat("0.0");
	private static final long[] BYTE_SCALE = new long[] {
			1024, // 1 kilobyte
			1024 * 1024, // 1 megabyte
			1024 * 1024 * 1024, // 1 gigabyte
		};
	
	private Formatter() {}

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
