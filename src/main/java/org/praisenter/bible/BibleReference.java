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
 * Represents a reference to a bible verse.
 * <p>
 * This class is intended for storage and transfer.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "bibleReference")
@XmlAccessorType(XmlAccessType.NONE)
public final class BibleReference {
	/** The not set flag */
	public static final short NOT_SET = -1;
	
	/** The id of the bible */
	@XmlAttribute(name = "bibleId", required = false)
	private final UUID bibleId;
	
	@XmlAttribute(name = "bookName", required = false)
	private final String bookName;
	
	/** The book number */
	@XmlAttribute(name = "bookNumber", required = false)
	private final short bookNumber;
	
	/** The chapter number */
	@XmlAttribute(name = "chapterNumber", required = false)
	private final short chapterNumber;
	
	/** The verse number */
	@XmlAttribute(name = "verseNumber", required = false)
	private final short verseNumber;
	
	/** The verse text */
	@XmlElement(name = "text", required = false)
	private final String text;
	
	/**
	 * For JAXB only.
	 */
	BibleReference() {
		// for jaxb
		this.bibleId = null;
		this.bookName = null;
		this.bookNumber = NOT_SET;
		this.chapterNumber = NOT_SET;
		this.verseNumber = NOT_SET;
		this.text = null;
	}
	
	/**
	 * Full constructor.
	 * @param bibleId the id of the bible
	 * @param bookName the book name
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @param text the verse text
	 */
	public BibleReference(UUID bibleId, String bookName, short bookNumber, short chapterNumber, short verseNumber, String text) {
		this.bibleId = bibleId;
		this.bookName = bookName;
		this.bookNumber = bookNumber;
		this.chapterNumber = chapterNumber;
		this.verseNumber = verseNumber;
		this.text = text;
	}

	/**
	 * Returns the bible id.
	 * @return UUID
	 */
	public UUID getBibleId() {
		return this.bibleId;
	}
	
	/**
	 * Returns the book name.
	 * @return String
	 */
	public String getBookName() {
		return this.bookName;
	}

	/**
	 * Returns the book number.
	 * @return short
	 */
	public short getBookNumber() {
		return this.bookNumber;
	}

	/**
	 * Returns the chapter number.
	 * @return short
	 */
	public short getChapterNumber() {
		return this.chapterNumber;
	}

	/**
	 * Returns the verse number.
	 * @return short
	 */
	public short getVerseNumber() {
		return this.verseNumber;
	}
	
	/**
	 * Returns the verse text.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
}
