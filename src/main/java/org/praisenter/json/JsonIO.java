package org.praisenter.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonIO {
	private static final ObjectMapper MAPPER = createObjectMapper();
	
	private static final ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		return mapper;
	}
	
	public static final <T> T read(String string, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(string);
	}
	
	public static final <T> T read(InputStream stream, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(stream);
	}

	public static final <T> T read(Path path, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(path.toFile());
	}
	
	public static final String write(Object object) throws JsonProcessingException {
		return MAPPER.writerFor(object.getClass()).writeValueAsString(object);
	}
	
	public static final void write(OutputStream stream, Object object) throws JsonGenerationException, JsonMappingException, IOException {
		MAPPER.writerFor(object.getClass()).writeValue(stream, object);
	}
	
	public static final void write(Path path, Object object) throws JsonGenerationException, JsonMappingException, IOException {
		MAPPER.writerFor(object.getClass()).writeValue(path.toFile(), object);
	}
}
