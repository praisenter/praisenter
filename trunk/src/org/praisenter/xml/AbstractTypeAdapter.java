package org.praisenter.xml;

import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;

/**
 * Abstract type adapter that contains common types to marshal and unmarshal.
 * @param <E> the type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractTypeAdapter<E> extends XmlAdapter<String, E> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(AbstractTypeAdapter.class);
	
	/**
	 * Returns a string of the given point delimited by the given delimiter.
	 * @param point the point
	 * @param delimiter the data delimiter
	 * @return String
	 */
	protected static final String getStringFromPoint2D(Point2D point, String delimiter) {
		StringBuilder sb = new StringBuilder();
		sb.append(point.getX()).append(delimiter)
		  .append(point.getY());
		return sb.toString();
	}
	
	/**
	 * Returns a Point2D for the given string and delimiter.
	 * @param dataString the delimited data string
	 * @param delimiter the delimiter
	 * @return Point2D
	 */
	protected static final Point2D getPoint2DFromString(String dataString, String delimiter) {
		String[] data = dataString.split(delimiter);
		int subLength = data.length;
		double x = 0.0;
		double y = 0.0;
		if (subLength > 0) {
			try {
				x = Double.parseDouble(data[0]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse x coordinate of the point [" + data[0] + "]: ", e);
			}
		}
		if (subLength > 1) {
			try {
				y = Double.parseDouble(data[1]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse y coordinate of the point [" + data[1] + "]: ", e);
			}
		}
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Returns a string for the given float array.
	 * @param array the float array
	 * @param delimiter the data delimiter
	 * @return String
	 */
	protected static final String getStringFromFloatArray(float[] array, String delimiter) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (i != 0) {
					sb.append(delimiter);
				}
				sb.append(array[i]);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns a float array from the given string.
	 * <p>
	 * Returns a float array with one zero entry if the dataString has no data.
	 * @param dataString the data string
	 * @param delimiter the delimiter
	 * @return float[]
	 */
	protected static final float[] getFloatArrayFromString(String dataString, String delimiter) {
		if (dataString == null || dataString.trim().length() == 0) {
			return null;
		}
		String[] data = dataString.split(delimiter);
		int length = data.length;
		if (length > 0) {
			float[] floats = new float[length];
			Arrays.fill(floats, 0.0f);
			for (int i = 0; i < length; i++) {
				try {
					floats[i] = Float.parseFloat(data[i]);
					// silently eat the exception
				} catch (NumberFormatException e) {
					LOGGER.warn("Unable to parse float array element [" + data[i] + "]: ", e);
				}
			}
			
			return floats;
		}
		return new float[] { 0.0f };
	}
}
