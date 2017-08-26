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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.Localized;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a set of lyrics.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Lyrics implements Localized {
	// for internal use
	/** The id of the lyrics */
	@JsonProperty
	final UUID id;
	
	/** True if this set of lyrics are the original set */
	@JsonProperty
	boolean original;
	
	/** The language */
	@JsonProperty
	String language;

	/** The transliteration */
	@JsonProperty
	String transliteration;

	/** The title */
	@JsonProperty
	String title;
	
	/** The authors */
	@JsonProperty
	final List<Author> authors;

	/** The song books that contain these lyrics */
	@JsonProperty
	final List<Songbook> songbooks;
	
	/** The verses */
	@JsonProperty
	final List<Verse> verses;

	/**
	 * Default constructor.
	 */
	public Lyrics() {
		this.id = UUID.randomUUID();
		this.original = false;
		this.authors = new ArrayList<Author>();
		this.songbooks = new ArrayList<Songbook>();
		this.verses = new ArrayList<>();
	}
	
	/**
	 * Copy constructor.
	 * @param lyrics the lyrics to copy
	 * @param exact true if an exact copy should be made (same ids)
	 */
	public Lyrics(Lyrics lyrics, boolean exact) {
		this.id = exact ? lyrics.id : UUID.randomUUID();
		this.language = lyrics.language;
		this.original = lyrics.original;
		this.title = lyrics.title;
		this.transliteration = lyrics.transliteration;
		this.authors = new ArrayList<Author>();
		this.songbooks = new ArrayList<Songbook>();
		this.verses = new ArrayList<Verse>();
		
		for (Author author : lyrics.authors) {
			this.authors.add(author.copy());
		}
		for (Songbook songbook : lyrics.songbooks) {
			this.songbooks.add(songbook.copy());
		}
		for (Verse verse : lyrics.verses) {
			this.verses.add(verse.copy());
		}
	}
	
	/**
	 * Returns a deep copy of this lyrics.
	 * @return {@link Lyrics}
	 */
	public Lyrics copy() {
		return new Lyrics(this, false);
	}
	
	/**
	 * Returns a deep copy of this lyrics.
	 * @param exact true if an exact copy should be made (same ids)
	 * @return {@link Lyrics}
	 */
	public Lyrics copy(boolean exact) {
		return new Lyrics(this, exact);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		if (this.language != null) {
			return Locale.forLanguageTag(this.language);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Verse verse : this.verses) {
			String text = verse.getText();
			if (text != null) {
				sb.append(text).append(Constants.NEW_LINE);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns the default author for the lyrics.
	 * <br>
	 * The first non-empty author to match:
	 * <ol>
	 * <li>No type attribute</li>
	 * <li>The type == words</li>
	 * <li>The type == music</li>
	 * <li>The first author in the list</li>
	 * </ol>
	 * @return {@link Author}
	 */
	public Author getDefaultAuthor() {
		Author author = null;
		int matchType = 0;
		if (this.authors.size() > 0) {
			// default to the first one
			author = this.authors.get(0);
			// try to find the best one
			for (Author auth : this.authors) {
				// don't choose an empty one
				if (auth.name == null || auth.name.length() <= 0) {
					continue;
				}
				// otherwise its the first one without a type setting
				if (auth.type == null || auth.type.length() == 0) {
					return auth;
				// otherwise its the first with type words
				} else if (Author.TYPE_LYRICS.equals(auth.type) && matchType < 1) {
					auth = author;
					matchType = 1;
				// otherwise its the first with type music
				} else if (Author.TYPE_MUSIC.equals(auth.type) && matchType < 2) {
					auth = author;
					matchType = 2;
				}
				// otherwise its the first
			}
		}
		return author;
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
	 * Returns the id for this lyric set.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns true if this set of lyrics is the original set.
	 * @return boolean
	 */
	public boolean isOriginal() {
		return this.original;
	}
	
	/**
	 * Sets this lyrics to the original if given true.
	 * @param original true if this set of lyrics is the original
	 */
	public void setOriginal(boolean original) {
		this.original = original;
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
	 * Returns the title.
	 * @return String
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title.
	 * @param title the title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the set of authors for this set of lyrics.
	 * @return List&lt;{@link Author}&gt;
	 */
	public List<Author> getAuthors() {
		return this.authors;
	}
	
	/**
	 * Returns the set of songbooks this set of lyrics are in.
	 * @return List&lt;{@link Songbook}&gt;
	 */
	public List<Songbook> getSongbooks() {
		return this.songbooks;
	}
	
	/**
	 * Returns the verses.
	 * @return List&lt;{@link Verse}&gt;
	 */
	public List<Verse> getVerses() {
		return this.verses;
	}
}
