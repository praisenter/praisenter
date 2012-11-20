package org.praisenter.xml;

import java.awt.Dimension;

import org.apache.log4j.Logger;

/**
 * Dimension type adapter for xml output.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DimensionTypeAdapter extends AbstractTypeAdapter<Dimension> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(DimensionTypeAdapter.class);
	
	/** The color components delimiter */
	private static final String DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Dimension v) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(v.width).append(DELIMITER).append(v.height);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Dimension unmarshal(String v) throws Exception {
		String[] typeData = v.split(DELIMITER);
		if (typeData.length == 2) {
			try {
			return new Dimension(
					Integer.parseInt(typeData[0]),
					Integer.parseInt(typeData[1]));
			} catch (Exception e) {
				LOGGER.error("Unable to parse dimension value [" + v + "]: ", e);
			}
		} else {
			LOGGER.warn("Data not in the correct format [" + v + "].");
		}
		return null;
	}
}
