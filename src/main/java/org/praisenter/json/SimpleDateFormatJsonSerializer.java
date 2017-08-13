package org.praisenter.json;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SimpleDateFormatJsonSerializer extends JsonSerializer<SimpleDateFormat> {
	@Override
	public void serialize(SimpleDateFormat value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		generator.writeString(value.toPattern());
	}
}
