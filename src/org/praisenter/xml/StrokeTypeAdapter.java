package org.praisenter.xml;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.log4j.Logger;

/**
 * Stroke type adapter for xml output.
 * <p>
 * This class only handles BasicStroke instances.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StrokeTypeAdapter extends AbstractTypeAdapter<Stroke> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(StrokeTypeAdapter.class);
	
	/** The BasicStroke type name */
	private static final String BASIC_STROKE_TYPE_NAME = "BasicStroke";
	
	/** The type/data delimiter */
	private static final String TYPE_NAME_DELIMITER = ":";
	
	/** The data delimiter */
	private static final String STROKE_DATA_DELIMITER = "|";
	
	/** The sub-data delimiter */
	private static final String STROKE_SUB_DATA_DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Stroke v) throws Exception {
		StringBuilder sb = new StringBuilder();
		// all output begins with a type string
		if (v instanceof BasicStroke) {
			BasicStroke stroke = (BasicStroke)v;
			sb.append(BASIC_STROKE_TYPE_NAME).append(TYPE_NAME_DELIMITER);
			// begin data
			// line width
			sb.append(stroke.getLineWidth())
			  .append(STROKE_DATA_DELIMITER);
			// cap type
			sb.append(stroke.getEndCap())
			  .append(STROKE_DATA_DELIMITER);
			// join type
			sb.append(stroke.getLineJoin())
			  .append(STROKE_DATA_DELIMITER);
			// miterLimit
			sb.append(stroke.getMiterLimit())
			  .append(STROKE_DATA_DELIMITER);
			// dash array
			sb.append(getStringFromFloatArray(stroke.getDashArray(), STROKE_SUB_DATA_DELIMITER))
			  .append(STROKE_DATA_DELIMITER);
			// dash phase
			sb.append(stroke.getDashPhase());
		} else {
			// if we dont handle it then just return empty string
			LOGGER.warn("Stroke type [" + v.getClass().getName() + "] is not supported.");
			return "";
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Stroke unmarshal(String v) throws Exception {
		String[] typeData = v.split(TYPE_NAME_DELIMITER);
		if (typeData.length == 2) {
			String type = typeData[0];
			if (BASIC_STROKE_TYPE_NAME.equals(type)) {
				return getBasicStrokeFromString(typeData[1]);
			} else {
				LOGGER.warn("Unknown stroke type [" + v + "].");
			}
		} else {
			LOGGER.warn("Data not in the correct format [" + v + "].");
		}
		return null;
	}
	
	/**
	 * Parses the given data string and creates a BasicStroke.
	 * <p>
	 * This method will always return a stroke even if values are missing
	 * or are not in a valid format.
	 * @param dataString the data string
	 * @return BasicStroke
	 */
	private static final BasicStroke getBasicStrokeFromString(String dataString) {
		// parse the data string (width|cap|join|miterLimit|dash|dashPhase)
		String[] data = dataString.split(STROKE_DATA_DELIMITER);
		// attempt to get whatever data is there
		int length = data.length;
		// defaults
		float width = 1.0f;
		int cap = BasicStroke.CAP_ROUND;
		int join = BasicStroke.JOIN_ROUND;
		float limit = 1.0f;
		float[] dash = null;
		float phase = -1.0f;
		// parse away
		if (length > 0) {
			// then we have the line width
			try {
				width = Float.parseFloat(data[0]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse line width [" + data[0] + "]: ", e);
			}
		}
		if (length > 1) {
			// then we have the cap type
			try {
				cap = Integer.parseInt(data[1]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse cap type [" + data[1] + "]: ", e);
			}
		}
		if (length > 2) {
			// then we have the join type
			try {
				join = Integer.parseInt(data[2]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse join type [" + data[2] + "]: ", e);
			}
		}
		if (length > 3) {
			// then we have the miter limit
			try {
				limit = Float.parseFloat(data[3]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse miter limit [" + data[3] + "]: ", e);
			}
		}
		if (length > 4) {
			// then we have a dash array
			if (data[4].contains(STROKE_SUB_DATA_DELIMITER)) {
				dash = getFloatArrayFromString(data[4], STROKE_SUB_DATA_DELIMITER);
			}
		}
		if (length > 5) {
			// then we have the dash phase
			try {
				phase = Float.parseFloat(data[5]);
				// silently eat the exception
			} catch (NumberFormatException e) {
				LOGGER.warn("Unable to parse dash phase [" + data[5] + "]: ", e);
			}
		}
		
		return new BasicStroke(width, cap, join, limit, dash, phase);
	}
}
