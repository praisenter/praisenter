package org.praisenter.xml.adapters;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleDateFormatTypeAdapter extends XmlAdapter<String, SimpleDateFormat> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(SimpleDateFormat v) throws Exception {
		return v.toPattern();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public SimpleDateFormat unmarshal(String v) throws Exception {
		// check for null or empty
		if (v != null && v.trim().length() > 0) {
			try {
				return new SimpleDateFormat(v);
			} catch (Exception e) {
				LOGGER.warn("The date/time format [" + v + "] is not valid.");
			}
		}
		// return the default format
		return new SimpleDateFormat("EEEE MMMM, d yyyy");
	}
}