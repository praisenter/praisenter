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
package org.praisenter.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A media format.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "format")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaFormat {
	/** The format short name */
	@XmlAttribute
	final String name;
	
	/** The format long name */
	@XmlAttribute
	final String description;
	
	/** The media's contained codecs */
	@XmlElementWrapper
	@XmlElement(name = "codec")
	final List<MediaCodec> codecs;
	
	/**
	 * Default constructor.
	 * Only for JAXB.
	 */
	@SuppressWarnings("unused")
	private MediaFormat() {
		this(null, null);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the format's short name 
	 * @param description the format's long name
	 */
	MediaFormat(String name, String description) {
		this.name = name;
		this.description = description;
		this.codecs = new ArrayList<MediaCodec>();
	}
	
	/**
	 * Optional constructor.
	 * @param name the format's short name 
	 * @param description the format's long name
	 * @param codecs a list of codecs
	 */
	MediaFormat(String name, String description, MediaCodec... codecs) {
		this.name = name;
		this.description = description;
		this.codecs = Arrays.asList(codecs);
	}
	
	/**
	 * Optional constructor.
	 * @param name the format's short name 
	 * @param description the format's long name
	 * @param codecs a list of codecs
	 */
	MediaFormat(String name, String description, List<MediaCodec> codecs) {
		this.name = name;
		this.description = description;
		this.codecs = codecs;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		if (this.codecs.size() > 0) {
			sb.append(" (");
			for (int i = 0; i < this.codecs.size(); i++) {
				MediaCodec codec = this.codecs.get(i);
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(codec.getName());
			}
			sb.append(")");
		}
		return sb.toString();
	}
	
	/**
	 * Returns a string with all the codec concatenated together with a ',' for
	 * display.
	 * @return String
	 */
	public String getCodecNames() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.codecs.size(); i++) {
			MediaCodec codec = this.codecs.get(i);
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(codec.getName());
		}
		return sb.toString();
	}
	
	/**
	 * Returns the short name of the format.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the long name of the format.
	 * @return String
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Returns an unmodifiable list of codecs for this media.
	 * @return List&lt;{@link MediaCodec}&gt;
	 */
	public List<MediaCodec> getCodecs() {
		return Collections.unmodifiableList(this.codecs);
	}
}
