package org.praisenter.xml;

import java.awt.Font;

/**
 * Font type adapter for xml output.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FontTypeAdapter extends AbstractTypeAdapter<Font> {
	/** The data delimiter */
	private static final String DATA_DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Font v) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(v.getFamily()).append(DATA_DELIMITER)
		  .append(v.getStyle()).append(DATA_DELIMITER)
		  .append(v.getSize());
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Font unmarshal(String v) throws Exception {
		String[] components = v.split(DATA_DELIMITER);
		// return the font
		return new Font(
				components[0],
				Integer.parseInt(components[1]),
				Integer.parseInt(components[2]));
	}

}
