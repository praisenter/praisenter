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
package org.praisenter.bible;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a {@link Verse} of the {@link Bible}.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class Verse implements Comparable<Verse>, Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 1013677325712981189L;

	/** The verse number */
	@XmlAttribute(name = "number", required = false)
	short number;
	
	/** The verse text */
	@XmlElement(name = "text", required = false)
	String text;
	
	/**
	 * Default constructor.
	 */
	public Verse()  {
		this.number = 0;
		this.text = null;
	}
	
	/**
	 * Full constructor.
	 * @param number the verse number
	 * @param text the verse text
	 */
	public Verse(short number, String text) {
		this.number = number;
		this.text = text;
	}
	
	/**
	 * Copy constructor.
	 * @param verse the verse to copy
	 */
	public Verse(Verse verse) {
		this.number = verse.number;
		this.text = verse.text;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Verse o) {
		if (o == null) return 1;
		return this.number - o.number;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.text;
	}
	
	/**
	 * Performs a deep copy of this verse.
	 * @return {@link Verse}
	 */
	public Verse copy() {
		return new Verse(this);
	}
	
	/**
	 * Returns the verse number.
	 * @return short
	 */
	public short getNumber() {
		return this.number;
	}
	
	/**
	 * Sets the verse number.
	 * @param number the verse number
	 */
	public void setNumber(short number) {
		this.number = number;
	}
	
	/**
	 * Returns the text of this verse.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the verse text.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
	}
}
