package org.praisenter.json;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SimpleDateFormatJsonDeserializer extends JsonDeserializer<SimpleDateFormat> {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public SimpleDateFormat deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String data = parser.readValueAs(String.class);
		// check for null or empty
		if (data != null && data.trim().length() > 0) {
			try {
				return new SimpleDateFormat(data);
			} catch (Exception e) {
				LOGGER.warn("The date/time format '" + data + "' is not valid.");
			}
		}
		// return the default format
		return new SimpleDateFormat("EEEE MMMM, d yyyy");
	}
}
