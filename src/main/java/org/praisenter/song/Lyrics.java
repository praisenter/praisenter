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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.Constants;
import org.praisenter.Localized;

/**
 * Represents a set of lyrics.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "lyrics")
@XmlAccessorType(XmlAccessType.NONE)
public final class Lyrics implements SongOutput, Localized {
	/** The edit format */
	private static final String EDIT_FORMAT = "<verse name=\"{0}\" />";
	
	// TODO auto-complete text box
	/** The language */
	@XmlAttribute(name = "language", required = false)
	String language;

	// TODO auto-complete text box
	/** The transliteration */
	@XmlAttribute(name = "transliteration", required = false)
	String transliteration;

	/** The verses */
	@XmlElement(name = "verse", required = false)
	@XmlElementWrapper(name = "verses", required = false)
	List<Verse> verses;

	/**
	 * Default constructor.
	 */
	public Lyrics() {
		this.verses = new ArrayList<>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getOutput(SongOutputType type) {
		StringBuilder sb = new StringBuilder();
		int size = this.verses.size();
		for (int i = 0; i < size; i++) {
			Verse verse = this.verses.get(i);
			
			if (i != 0) {
				sb.append(Constants.NEW_LINE)
				  .append(Constants.NEW_LINE);
			}
			
			if (type == SongOutputType.EDIT) {
				sb.append(MessageFormat.format(EDIT_FORMAT, verse.name))
				  .append(Constants.NEW_LINE);
			}
			
			sb.append(verse.getOutput(type));
		}
		return sb.toString();
	}
	
	/**
	 * Returns the verse for the given name.
	 * @param name the verse name
	 * @return {@link Verse}
	 */
	public Verse getVerse(String name) {
		for (Verse verse : this.verses) {
			if (name.equalsIgnoreCase(verse.name)) {
				return verse;
			}
		}
		return null;
	}
	
	/**
	 * Returns the language.
	 * @return String
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Sets the language.
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

	/**
	 * Returns the verses.
	 * @return List&lt;{@link Verse}&gt;
	 */
	public List<Verse> getVerses() {
		return this.verses;
	}

	/**
	 * Sets the verses.
	 * @param verses the verses
	 */
	public void setVerses(List<Verse> verses) {
		this.verses = verses;
	}
}
