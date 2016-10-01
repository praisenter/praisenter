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

import java.util.UUID;

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
public final class Verse implements Comparable<Verse> {
	/** The verse id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;
	
	/** The chapter number */
	@XmlAttribute(name = "chapter", required = false)
	int chapter;
	
	/** The verse number */
	@XmlAttribute(name = "verse", required = false)
	int verse;
	
	/** The sub verse number */
	@XmlAttribute(name = "subVerse", required = false)
	int subVerse;
	
	/** The verse order */
	@XmlAttribute(name = "order", required = false)
	int order;
	
	/** The verse text */
	@XmlElement(name = "text", required = false)
	String text;
	
	Verse()  {
		// for JAXB
		this.id = null;
	}
	
	/**
	 * Full constructor.
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @param subVerse the sub verse number
	 * @param order the verse order
	 * @param text the verse text
	 */
	public Verse(int chapter, int verse, int subVerse, int order, String text) {
		this(null, chapter, verse, subVerse, order, text);
	}
	
	/**
	 * Full constructor.
	 * @param id the verse id
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @param subVerse the sub verse number
	 * @param order the verse order
	 * @param text the verse text
	 */
	public Verse(UUID id, int chapter, int verse, int subVerse, int order, String text) {
		this.id = id == null ? UUID.randomUUID() : id;
		this.chapter = chapter;
		this.verse = verse;
		this.subVerse = subVerse;
		this.order = order;
		this.text = text;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Verse) {
			Verse other = (Verse)obj;
			if (other.id == this.id) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Verse[Id=").append(this.id)
		  .append("|Chapter=").append(this.chapter)
		  .append("|Verse=").append(this.verse)
		  .append("|SubVerse=").append(this.subVerse)
		  .append("|Order=").append(this.order)
		  .append("|Text=").append(this.text)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Verse o) {
		if (o == null) return 1;
		return this.order - o.order;
	}
	
	/**
	 * Returns the verse id for this {@link Verse}.
	 * @return int
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the chapter number this {@link Verse} is contained in.
	 * @return int
	 */
	public int getChapter() {
		return this.chapter;
	}
	
	/**
	 * Returns the verse number of this {@link Verse}.
	 * @return int
	 */
	public int getVerse() {
		return this.verse;
	}
	
	/**
	 * Returns the sub verse number of this {@link Verse}.
	 * @return int
	 */
	public int getSubVerse() {
		return this.subVerse;
	}
	
	/**
	 * Returns the verse order.
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Returns the text of this verse.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
}
