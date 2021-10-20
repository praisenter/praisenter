package org.praisenter.ui.controls;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.util.StringConverter;

public final class SimpleDateFormatConverter extends StringConverter<SimpleDateFormat> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public String toString(SimpleDateFormat format) {
		if (format == null) return null;
		return format.toPattern();
	}

	@Override
	public SimpleDateFormat fromString(String format) {
		if (format == null || format.trim().isEmpty()) {
			return null;
		}
		try {
			return new SimpleDateFormat(format);
		} catch (IllegalArgumentException ex) {
			LOGGER.warn("The user supplied format '" + format + "' was not accepted by the SimpleDateFormat constructor.", ex);
		}
		return null;
	}

}
