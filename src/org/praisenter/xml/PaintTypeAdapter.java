package org.praisenter.xml;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Paint type adapter for xml output.
 * <p>
 * This class only handles Color, LinearGradientPaint, and RadialGradientPaint types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO support texture paint
public class PaintTypeAdapter extends AbstractTypeAdapter<Paint> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(PaintTypeAdapter.class);
	
	/** The Color type name */
	private static final String COLOR_TYPE_NAME = "Color";
	
	/** The LinearGradientPaint name */
	private static final String LINEAR_GRADIENT_PAINT_TYPE_NAME = "LinearGradientPaint";
	
	/** The RadialGradientPaint name */
	private static final String RADIAL_GRADIENT_PAINT_TYPE_NAME = "RadialGradientPaint";
	
	/** The type/data delimiter */
	private static final String TYPE_NAME_DELIMITER = ":";
	
	/** The color components delimiter */
	private static final String COLOR_COMPONENT_DELIMITER = "-";
	
	/** The gradient data delimiter */
	private static final String GRADIENT_DATA_DELIMITER = "|";
	
	/** The gradient sub-data delimiter */
	private static final String GRADIENT_SUB_DATA_DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Paint v) throws Exception {
		StringBuilder sb = new StringBuilder();
		// all output begins with a type string
		if (v instanceof Color) {
			Color color = (Color)v;
			sb.append(COLOR_TYPE_NAME).append(TYPE_NAME_DELIMITER);
			// begin data (RGBA)
			sb.append(getStringFromColor(color, COLOR_COMPONENT_DELIMITER));
		} else if (v instanceof LinearGradientPaint) {
			LinearGradientPaint paint = (LinearGradientPaint)v;
			sb.append(LINEAR_GRADIENT_PAINT_TYPE_NAME).append(TYPE_NAME_DELIMITER);
			// begin data
			// start point
			sb.append(getStringFromPoint2D(paint.getStartPoint(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// end point
			sb.append(getStringFromPoint2D(paint.getEndPoint(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// fractions
			sb.append(getStringFromFloatArray(paint.getFractions(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// colors
			sb.append(getStringFromColorArray(paint.getColors(), GRADIENT_SUB_DATA_DELIMITER, COLOR_COMPONENT_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// the cycle method
			sb.append(paint.getCycleMethod());
		} else if (v instanceof RadialGradientPaint) {
			RadialGradientPaint paint = (RadialGradientPaint)v;
			sb.append(RADIAL_GRADIENT_PAINT_TYPE_NAME).append(TYPE_NAME_DELIMITER);
			// begin data
			// center point
			sb.append(getStringFromPoint2D(paint.getCenterPoint(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// radius
			sb.append(paint.getRadius())
			  .append(GRADIENT_DATA_DELIMITER);
			// focus point
			sb.append(getStringFromPoint2D(paint.getFocusPoint(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// fractions
			sb.append(getStringFromFloatArray(paint.getFractions(), GRADIENT_SUB_DATA_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// colors
			sb.append(getStringFromColorArray(paint.getColors(), GRADIENT_SUB_DATA_DELIMITER, COLOR_COMPONENT_DELIMITER))
			  .append(GRADIENT_DATA_DELIMITER);
			// the cycle method
			sb.append(paint.getCycleMethod());
		} else {
			// if we dont handle it then just return empty string
			LOGGER.warn("Paint type [" + v.getClass().getName() + "] is not supported.");
			return "";
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Paint unmarshal(String v) throws Exception {
		String[] typeData = v.split(TYPE_NAME_DELIMITER);
		if (typeData.length == 2) {
			String type = typeData[0];
			if (COLOR_TYPE_NAME.equals(type)) {
				return getColorFromString(typeData[1], COLOR_COMPONENT_DELIMITER);
			} else if (LINEAR_GRADIENT_PAINT_TYPE_NAME.equals(type)) {
				return getLinearGradientPaintFromString(typeData[1]);
			} else if (RADIAL_GRADIENT_PAINT_TYPE_NAME.equals(type)) {
				return getRadialGradientPaintFromString(typeData[1]);
			} else {
				LOGGER.warn("Unknown paint type [" + v + "].");
			}
		} else {
			LOGGER.warn("Data not in the correct format [" + v + "].");
		}
		return null;
	}
	
	/**
	 * Returns a string containing the data for the given color delimited by the
	 * given delimiter.
	 * @param color the color
	 * @param delimiter the data delimiter
	 * @return String
	 */
	private static final String getStringFromColor(Color color, String delimiter) {
		StringBuilder sb = new StringBuilder();
		sb.append(color.getRed()).append(delimiter)
		  .append(color.getGreen()).append(delimiter)
		  .append(color.getBlue()).append(delimiter)
		  .append(color.getAlpha());
		return sb.toString();
	}
	
	/**
	 * Parses the given data string and creates a color.
	 * <p>
	 * This method will always return a color even if the color values are missing
	 * or are not in a valid format.
	 * @param dataString the data string
	 * @param delimiter the data delimiter
	 * @return Color
	 */
	private static final Color getColorFromString(String dataString, String delimiter) {
		// parse the data portion
		String[] components = dataString.split(delimiter);
		// attempt to get whatever components are there
		int length = components.length;
		int r = 0, g = 0, b = 0, a = 0;
		if (length > 0) {
			try {
				r = Integer.parseInt(components[0]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse red color component [" + components[0] + "]: ", e);
			}
		}
		if (length > 1) {
			try {
				g = Integer.parseInt(components[1]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse green color component [" + components[1] + "]: ", e);
			}
		}
		if (length > 2) {
			try {
				b = Integer.parseInt(components[2]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse blue color component [" + components[2] + "]: ", e);
			}
		}
		if (length > 3) {
			try {
				a = Integer.parseInt(components[3]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse alpha color component [" + components[3] + "]: ", e);
			}
		}
		return new Color(r, g, b, a);
	}
	
	/**
	 * Returns a string for the given Color array.
	 * @param array the Color array
	 * @param elementDelimiter the delimiter for the colors
	 * @param componentDelimiter the delimiter for the color components
	 * @return String
	 */
	private static final String getStringFromColorArray(Color[] array, String elementDelimiter, String componentDelimiter) {
		if (array != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i != 0) {
					sb.append(elementDelimiter);
				}
				sb.append(getStringFromColor(array[i], componentDelimiter));
			}
			return sb.toString();
		}
		return "";
	}
	
	/**
	 * Returns a Color array from the given string.
	 * <p>
	 * Returns a Color array with one BLACK entry if the dataString has no data.
	 * @param dataString the data string
	 * @param elementDelimiter the delimiter for the colors
	 * @param componentDelimiter the delimiter for the color components
	 * @return Color[]
	 */
	private static final Color[] getColorArrayFromString(String dataString, String elementDelimiter, String componentDelimiter) {
		String[] data = dataString.split(elementDelimiter);
		int length = data.length;
		if (length > 0) {
			Color[] colors = new Color[length];
			Arrays.fill(colors, Color.BLACK);
			for (int i = 0; i < length; i++) {
				colors[i] = getColorFromString(data[i], componentDelimiter);
			}
			return colors;
		}
		return new Color[] { Color.BLACK };
	}
	
	/**
	 * Parses the given data string and creates a LinearGradientPaint.
	 * <p>
	 * This method will always return a paint even if values are missing
	 * or are not in a valid format.
	 * @param dataString the data string
	 * @return LinearGradientPaint
	 */
	private static final LinearGradientPaint getLinearGradientPaintFromString(String dataString) {
		// parse the data string (startPoint|endPoint|fractions|colors|cycleMethod)
		String[] data = dataString.split("\\" + GRADIENT_DATA_DELIMITER);
		// attempt to get whatever data is there
		int length = data.length;
		// defaults
		Point2D start = null;
		Point2D end = null;
		float[] fractions = null;
		Color[] colors = null;
		CycleMethod method = CycleMethod.NO_CYCLE;
		// parse away
		if (length > 0) {
			// then we have the start point
			start = getPoint2DFromString(data[0], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 1) {
			// then we have the end point
			end = getPoint2DFromString(data[1], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 2) {
			// then we have the fractions array
			fractions = getFloatArrayFromString(data[2], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 3) {
			// then we have the fractions array
			colors = getColorArrayFromString(data[3], GRADIENT_SUB_DATA_DELIMITER, COLOR_COMPONENT_DELIMITER);
		}
		if (length > 4) {
			try {
				method = CycleMethod.valueOf(data[4]);
			} catch (Exception e) {
				LOGGER.warn("The CycleMethod is unknown [" + data[4] + "]: ", e);
			}
		}
		
		return new LinearGradientPaint(start, end, fractions, colors, method);
	}
	
	/**
	 * Parses the given data string and creates a RadialGradientPaint.
	 * <p>
	 * This method will always return a paint even if values are missing
	 * or are not in a valid format.
	 * @param dataString the data string
	 * @return RadialGradientPaint
	 */
	private static final RadialGradientPaint getRadialGradientPaintFromString(String dataString) {
		// parse the data string (centerPoint|radius|focusPoint|fractions|colors|cycleMethod)
		String[] data = dataString.split("\\" + GRADIENT_DATA_DELIMITER);
		// attempt to get whatever data is there
		int length = data.length;
		// defaults
		Point2D center = null;
		float radius = 25.0f;
		Point2D focus = null;
		float[] fractions = null;
		Color[] colors = null;
		CycleMethod method = CycleMethod.NO_CYCLE;
		// parse away
		if (length > 0) {
			// then we have the start point
			center = getPoint2DFromString(data[0], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 1) {
			// then we have the radius
			try {
				radius = Float.parseFloat(data[1]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse radius [" + data[1] + "]: ", e);
			}
		}
		if (length > 2) {
			// then we have the end point
			focus = getPoint2DFromString(data[2], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 3) {
			// then we have the fractions array
			fractions = getFloatArrayFromString(data[3], GRADIENT_SUB_DATA_DELIMITER);
		}
		if (length > 4) {
			// then we have the fractions array
			colors = getColorArrayFromString(data[4], GRADIENT_SUB_DATA_DELIMITER, COLOR_COMPONENT_DELIMITER);
		}
		if (length > 5) {
			try {
				method = CycleMethod.valueOf(data[5]);
			} catch (Exception e) {
				LOGGER.warn("The CycleMethod is unknown [" + data[5] + "]: ", e);
			}
		}
		
		return new RadialGradientPaint(center, radius, focus, fractions, colors, method);
	}
}
