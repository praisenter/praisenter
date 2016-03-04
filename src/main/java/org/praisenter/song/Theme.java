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
package org.praisenter.song;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.praisenter.Localized;

// FIXME replace with Tag class
/**
 * Represents a theme of a song (similar to a tag).
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "theme")
@XmlAccessorType(XmlAccessType.NONE)
public final class Theme implements Localized {
	/** The theme text */
	@XmlValue
	String text;
	
	/** The language */
	@XmlAttribute(name = "language", required = false)
	String language;
	
	/** The transliteration */
	@XmlAttribute(name = "transliteration", required = false)
	String transliteration;
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
	}
	
	/**
	 * Returns the theme text.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the theme text.
	 * @param text the theme text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns the language.
	 * @return String
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Sets the langauge.
	 * @param language the language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns the transliteration.
	 * @return String
	 */
	public String getTransliteration() {
		return this.transliteration;
	}

	/**
	 * Sets the transliteration.
	 * @param transliteration the transliteration
	 */
	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
	}
}
