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
package org.praisenter.data.json;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.Constants;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * Class used to store format information for the Praisenter JSON format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterFormat {
	/** The class type specified by JsonSubTypes and Type Jackson annotations */
	private final String type;
	
	/** The format; should always be {@link Constants#FORMAT_NAME} */
	private final String format;
	
	/** The version; could be null */
	private final String version;
	
	/**
	 * Full constructor.
	 * @param type the type
	 * @param format the format
	 * @param version the version
	 */
	public PraisenterFormat(String type, String format, String version) {
		this.type = type;
		this.format = format;
		this.version = version;
	}

	/**
	 * Returns true if this format detail matches the given class.
	 * @param clazz the class
	 * @return boolean
	 */
	public boolean is(Class<?> clazz) {
		JsonSubTypes[] annotations = clazz.getAnnotationsByType(JsonSubTypes.class);
		List<String> typeNames = new ArrayList<String>();
		if (annotations != null) {
			for (JsonSubTypes subTypes : annotations) {
				Type[] types = subTypes.value();
				if (types != null) {
					for (Type type : types) {
						if (clazz.isAssignableFrom(type.value())) {
							typeNames.add(type.name());
						}
					}
				}
			}
		}
		
		if (typeNames.isEmpty()) {
			typeNames.add(clazz.getSimpleName());
		}
		
		return typeNames.contains(this.type) &&
			   this.format.equalsIgnoreCase(Constants.FORMAT_NAME);
	}
	
	/**
	 * Returns the type as specified by JsonSubTypes and Type Jackson annotations.
	 * @return String
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns the format, which should always be {@link Constants#FORMAT_NAME}.
	 * @return String
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Returns the version. Can be null or empty.
	 * @return String
	 */
	public String getVersion() {
		return this.version;
	}
}
