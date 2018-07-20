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
package org.praisenter.data.media;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A media codec.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaCodec {
	/** The codec short name */
	@JsonProperty
	final String name;
	
	/** The codec long name */
	@JsonProperty
	final String description;
	
	/** The codec type */
	@JsonProperty
	final CodecType type;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private MediaCodec() {
		// for jaxb
		this(null, null, null);
	}
	
	/**
	 * Minimal constructor.
	 * @param type the codec type
	 * @param name the codec short name
	 * @param description the coded long name
	 */
	public MediaCodec(CodecType type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Returns the short name of this codec.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the long name of this codec.
	 * @return String
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns the type of this codec.
	 * @return {@link CodecType}
	 */
	public CodecType getType() {
		return this.type;
	}
}
