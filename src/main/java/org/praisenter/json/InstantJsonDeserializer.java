package org.praisenter.json;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InstantJsonDeserializer extends JsonDeserializer<Instant> {
	@Override
	public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String data = parser.readValueAs(String.class);
		if (data != null) {
			try {
				return Instant.parse(data);
			} catch (Exception ex) {
				// TODO handle
			}
		}
		return null;
	}
}
