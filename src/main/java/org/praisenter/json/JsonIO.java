/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.praisenter.Constants;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Helper class for serializing and deserializing between Java objects and JSON.
 * @author William Bittle
 * @version 3.0.0
 */
public final class JsonIO {
	/** The mapper */
	private static final ObjectMapper MAPPER = createObjectMapper();
	
	/**
	 * Builds the object mapper and sets some default settings.
	 * @return ObjectMapper
	 */
	private static final ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// pretty print
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// just skip unknown properties
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// make sure we have explicitly mark the properties we want serialized/deserialized
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		// don't include empty properties
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		// make sure that the json generator doesn't close streams (we'll take care of it)
		mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		return mapper;
	}
	
	// read
	
	/**
	 * Deserializes the given string into the given class.
	 * @param string the JSON string
	 * @param clazz the class
	 * @return T
	 * @throws JsonProcessingException if a JSON deserialization error occurs
	 * @throws IOException if an IO error occurs
	 */
	public static final <T> T read(String string, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(string);
	}
	
	/**
	 * Deserializes the given stream into the given class.
	 * @param stream the JSON input stream
	 * @param clazz the class
	 * @return T
	 * @throws JsonProcessingException if a JSON deserialization error occurs
	 * @throws IOException if an IO error occurs
	 */
	public static final <T> T read(InputStream stream, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(stream);
	}

	/**
	 * Deserializes the given file into the given class.
	 * @param path the path to a JSON file
	 * @param clazz the class
	 * @return T
	 * @throws JsonProcessingException if a JSON deserialization error occurs
	 * @throws IOException if an IO error occurs
	 */
	public static final <T> T read(Path path, Class<T> clazz) throws JsonProcessingException, IOException {
		return MAPPER.readerFor(clazz).readValue(path.toFile());
	}
	
	// write
	
	/**
	 * Serializes the given object to a JSON string.
	 * @param object the object to serialize
	 * @return String
	 * @throws JsonProcessingException if a JSON serialization error occurs
	 */
	public static final String write(Object object) throws JsonProcessingException {
		return MAPPER.writerFor(object.getClass()).writeValueAsString(object);
	}
	
	/**
	 * Serializes the given object to the given stream.
	 * @param stream the stream to write the JSON to
	 * @param object the object to serialize
	 * @throws IOException if an IO error occurs
	 * @throws JsonMappingException if a JSON mapping error occurs
	 * @throws JsonGenerationException if a JSON writing error occurs
	 */
	public static final void write(OutputStream stream, Object object) throws JsonGenerationException, JsonMappingException, IOException {
		MAPPER.writerFor(object.getClass()).writeValue(stream, object);
	}
	
	/**
	 * Serializes the given object to the given file.
	 * @param path the file to write the JSON to
	 * @param object the object to serialize
	 * @throws IOException if an IO error occurs
	 * @throws JsonMappingException if a JSON mapping error occurs
	 * @throws JsonGenerationException if a JSON writing error occurs
	 */
	public static final void write(Path path, Object object) throws JsonGenerationException, JsonMappingException, IOException {
		MAPPER.writerFor(object.getClass()).writeValue(path.toFile(), object);
	}
	
	// identify
	
	/**
	 * Returns a {@link PraisenterFormat} object for the given JSON or null if it's not a 
	 * Praisenter file format.
	 * @param json the json string
	 * @return boolean
	 * @throws JsonProcessingException if an error occurs while interpreting the json string
	 * @throws IOException if and IO error occurs
	 */
	public static final PraisenterFormat getPraisenterFormat(String json) throws JsonProcessingException, IOException {
		return getPraisenterFormat(MAPPER.readTree(json));
	}

	/**
	 * Returns a {@link PraisenterFormat} object for the given JSON or null if it's not a 
	 * Praisenter file format.
	 * @param stream the stream
	 * @return boolean
	 * @throws JsonProcessingException if an error occurs while interpreting the stream as JSON
	 * @throws IOException if and IO error occurs
	 */
	public static final PraisenterFormat getPraisenterFormat(InputStream stream) throws JsonProcessingException, IOException {
		return getPraisenterFormat(MAPPER.readTree(stream));
	}
	
	/**
	 * Returns a {@link PraisenterFormat} object for the given JSON or null if it's not a 
	 * Praisenter file format.
	 * @param path the path
	 * @return boolean
	 * @throws JsonProcessingException if an error occurs while interpreting the file as JSON
	 * @throws IOException if and IO error occurs
	 */
	public static final PraisenterFormat getPraisenterFormat(Path path) throws JsonProcessingException, IOException {
		return getPraisenterFormat(MAPPER.readTree(path.toFile()));
	}
	
	/**
	 * Returns a {@link PraisenterFormat} object for the given JSON.
	 * @param node the JSON
	 * @return {@link PraisenterFormat}
	 */
	private static final PraisenterFormat getPraisenterFormat(JsonNode node) {
		if (node == null) return null;
		
		// root level there should be a @type, @format, and @version
		JsonNode tn = node.get("@type");
		JsonNode fn = node.get(Constants.FORMAT_PROPERTY_NAME);
		JsonNode vn = node.get(Constants.VERSION_PROPERTY_NAME);
		
		// if we don't find @type and format then we don't think
		// it's praisenter
		if (tn == null || fn == null) {
			return null;
		}
		
		// the version is optional, but should be provided
		return new PraisenterFormat(
				tn.asText(), 
				fn.asText(), 
				vn != null ? vn.asText() : null);
	}
}
