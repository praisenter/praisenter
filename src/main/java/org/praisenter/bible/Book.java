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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a book in the Bible.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.NONE)
public final class Book implements Comparable<Book> {
	/** The book code */
	@XmlAttribute(name = "code", required = false)
	String code;
	
	/** The book name */
	@XmlElement(name = "name", required = false)
	String name;

	/** The book order */
	@XmlAttribute(name = "order", required = false)
	int order;
	
	/** The verses in this book */
	@XmlElement(name = "verse", required = false)
	@XmlElementWrapper(name = "verses", required = false)
	final List<Verse> verses;
	
	/**
	 * Default constructor.
	 */
	public Book() {
		this.code = null;
		this.name = null;
		this.order = 0;
		this.verses = new ArrayList<Verse>();
	}
	
	/**
	 * Optional constructor.
	 * @param code the book code (id)
	 * @param name the book name
	 * @param order the book order
	 */
	public Book(String code, String name, int order) {
		this( code, name, order, null);
	}
	
	/**
	 * Full constructor.
	 * @param code the book code (id)
	 * @param name the book name
	 * @param order the book order
	 * @param verses the list of verses; or null
	 */
	public Book(String code, String name, int order, List<Verse> verses) {
		this.code = code;
		this.name = name;
		this.order = order;
		this.verses = verses != null ? verses : new ArrayList<Verse>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Book o) {
		if (o == null) return 1;
		return this.order - o.order;
	}
	
	/**
	 * Returns the book code.
	 * @return String
	 */
	public String getCode() {
		return this.code;
	}
	
	/**
	 * Sets the book code.
	 * @param code the code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * Returns the book name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the book name.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the order.
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Sets the order.
	 * @param order the position
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * Returns the list of verses for this book.
	 * @return List&lt;{@link Verse}&gt;
	 */
	public List<Verse> getVerses() {
		return this.verses;
	}
}
