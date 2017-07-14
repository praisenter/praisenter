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

import java.nio.file.Path;
import java.text.Collator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.praisenter.Constants;
import org.praisenter.Tag;
import org.praisenter.xml.adapters.InstantXmlAdapter;

/**
 * Represents a song.
 * <p>
 * A song is typically broken into various verses like verse 1, chorus, etc.  This collection
 * of verses are called lyrics.  Some songs are translated or transliterated for other language
 * speakers.  Each translation-transliteration combination is a separate set of lyrics in 
 * {@link #getLyrics()}.  The lyrics without a translation/transliteration are typically the
 * default lyrics or the lyrics the song was in originally, althought this may not always be
 * the case.  Use the {@link #getDefaultLyrics()} method to get the default lyrics of the song.
 * This method may not return the correct set, but does a best effort based on the following
 * priority:
 * <br>
 * The first set of non-empty lyrics to match:
 * <ol>
 * <li>No language or transliteration</li>
 * <li>The language/country that matches the current locale</li>
 * <li>The language that matches the current locale</li>
 * <li>The lyrics with the most verses</li>
 * </ol>
 * Likewise, a song my also have it's title translated or transliterated.  You can get the default
 * title by calling the {@link #getDefaultTitle()} method.  This method does a best effort based
 * on the following priority:
 * <br>
 * The first non-empty title to match:
 * <ol>
 * <li>No language or transliteration</li>
 * <li>The language/country that matches the current locale</li>
 * <li>The language that matches the current locale</li>
 * <li>The first title in the list</li>
 * </ol>
 * To get a list of all the locales for the titles and lyrics use the {@link #getLocales()} method.
 * <p>
 * In addition, a song may have many authors.  Each author may have authored a different part
 * of the song (words, music, translation, etc).  The {@link #getDefaultAuthor()} method will
 * return the main author using the following priority:
 * <br>
 * The first non-empty author to match:
 * <ol>
 * <li>No type attribute</li>
 * <li>The type == words</li>
 * <li>The type == music</li>
 * <li>The first author in the list</li>
 * </ol>
 * Apart from these, the song will contain a number of metadata that can assist with searching
 * and cataloging, {@link #getTags()} in particular.
 * <p>
 * Creating a new song defaults the created and modified properties to be created in Praisenter
 * and today.
 * <p>
 * Songs implement the {@link SongOutput} interface to provide a way to store the raw version
 * of the song and also the viewable version.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public final class Song implements SongOutput, Comparable<Song> {
	/** For string comparison (current locale) */
	static final Collator COLLATOR = Collator.getInstance();

	/** The current version number */
	public static final int CURRENT_VERSION = 3;

	// final
	
	/** The format (for format identification only) */
	@XmlAttribute(name = "format", required = false)
	private final String format = Constants.FORMAT_NAME;
	
	/** The version number */
	@XmlAttribute(name = "version", required = false)
	private final int version = CURRENT_VERSION;
	
	// for internal use really
	/** The file path */
	Path path;
	
	/** The song unique id */
	@XmlElement(name = "id", required = false)
	private UUID id;
	
	/** The created on date */
	@XmlElement(name = "createdDate", required = false)
	@XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
	Instant createdDate;
	
	/** The last modified date */
	@XmlElement(name = "modifiedData", required = false)
	@XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
	Instant modifiedDate;

	/** The created in application */
	@XmlElement(name = "source", required = false)
	String source;

	/** The song's copyright information */
	@XmlElement(name = "copyright", required = false)
	String copyright;
	
	/** The song's CCLI number */
	@XmlElement(name = "ccli", required = false)
	int ccli;
	
	/** The song's release date */
	@XmlElement(name = "released", required = false)
	String released;
	
	/** The song's transposition */
	@XmlElement(name = "transposition", required = false)
	int transposition;
	
	/** The song's tempo; typically in bpm */
	@XmlElement(name = "tempo", required = false)
	String tempo;
	
	/** The song's key */
	@XmlElement(name = "key", required = false)
	String key;
	
	/** The variant name for this song */
	@XmlElement(name = "variant", required = false)
	String variant;
	
	/** The publisher */
	@XmlElement(name = "publisher", required = false)
	String publisher;
	
	/** The comments */
	@XmlElement(name = "comments", required = false)
	String comments;

	/** The keywords to aid in searching */
	@XmlElement(name = "keywords", required = false)
	String keywords;
	
	/** The comments */
	@XmlElement(name = "primaryLyrics", required = false)
	UUID primaryLyrics;

	// lists
	
	/** The lyrics */
	@XmlElement(name = "lyrics", required = false)
	@XmlElementWrapper(name = "lyricsets", required = false)
	List<Lyrics> lyrics;

	/** The sequence of verses space separated */
	@XmlElement(name = "name", required = false)
	@XmlElementWrapper(name = "sequence", required = false)
	List<String> sequence;

	/** The tags; useful for searching or grouping */
	@XmlElement(name = "tag", required = false)
	@XmlElementWrapper(name = "tags", required = false)
	Set<Tag> tags;
	
	/**
	 * Default constructor.
	 */
	public Song() {
		this.path = null;
		this.id = UUID.randomUUID();
		this.createdDate = Instant.now();
		this.source = Constants.NAME + " " + Constants.VERSION;
		this.modifiedDate = this.createdDate;
		this.ccli = -1;
		this.transposition = 0;
		this.primaryLyrics = null;
		
		this.lyrics = new ArrayList<>();
		this.sequence = new ArrayList<>();
		this.tags = new TreeSet<>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.song.SongOutput#getOutput(org.praisenter.song.SongOutputType)
	 */
	@Override
	public String getOutput(SongOutputType type) {
		StringBuilder sb = new StringBuilder();
		int size = this.lyrics.size();
		for (int i = 0; i < size; i++) {
			Lyrics lyrics = this.lyrics.get(i);
			if (i != 0) {
				sb.append(Constants.NEW_LINE)
				  .append(Constants.NEW_LINE);
			}
			
			if (type == SongOutputType.EDIT) {
				if (lyrics.language != null && lyrics.language.length() > 0) {
					// show language
					sb.append(lyrics.language);
					// if language is there, there may be a translit
					if (lyrics.transliteration != null && lyrics.transliteration.length() > 0) {
						sb.append(Constants.NEW_LINE).append(lyrics.transliteration);
					}
					sb.append(Constants.NEW_LINE);
				}
			}
			
			sb.append(lyrics.getOutput(type));
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String title = this.getDefaultTitle();
		if (title == null) {
			return "Untitled";
		}
		return title;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Song o) {
		// sort by title first
		String t0 = this.getDefaultTitle();
		String t1 = o.getDefaultTitle();
		int diff = COLLATOR.compare(t0, t1);
		if (diff == 0) {
			// then sort by variant
			diff = COLLATOR.compare(this.variant, o.variant);
			if (diff == 0) {
				// then sort by author
				Author a0 = this.getDefaultAuthor();
				Author a1 = o.getDefaultAuthor();
				String s0 = a0 == null ? "" : a0.name;
				String s1 = a1 == null ? "" : a1.name;
				diff = COLLATOR.compare(s0, s1);
				if (diff == 0) {
					// then by added date
					diff = this.createdDate.compareTo(o.createdDate);
				}
			}
		}
		return diff;
	}
	
	/**
	 * Returns the default author for the default lyrics.
	 * @return {@link Author}
	 * @see Lyrics#getDefaultAuthor()
	 */
	public Author getDefaultAuthor() {
		Lyrics lyrics = this.getDefaultLyrics();
		if (lyrics != null) {
			lyrics.getDefaultAuthor();
		}
		return null;
	}
	
	/**
	 * Returns the title for the default lyrics.
	 * @return String
	 * @see #getDefaultLyrics()
	 */
	public String getDefaultTitle() {
		Lyrics lyrics = this.getDefaultLyrics();
		if (lyrics != null) {
			return lyrics.title;
		}
		return null;
	}
	
	/**
	 * Returns the default set of lyrics using the following criteria:
	 * <br>
	 * The first non-empty set of lyrics that matches:
	 * <ol>
	 * <li>The primary flag equals true</li>
	 * <li>The original flag equals true</li>
	 * <li>The language is empty</li>
	 * <li>The language and country equals the current locale</li>
	 * <li>The language equals the current locale</li>
	 * <li>The one with the most verses</li>
	 * </ol>
	 * @return {@link Lyrics}
	 */
	public Lyrics getDefaultLyrics() {
		Locale locale = Locale.getDefault();
		Lyrics lyrics = null;
		int matchLevel = 0;
		int verseCount = 0;
		if (this.lyrics.size() > 0) {
			// default to the first one
			lyrics = this.lyrics.get(0);
			// try to find the best one
			for (Lyrics lrcs : this.lyrics) {
				Locale sl = lrcs.getLocale();
				int vc = lrcs.getVerses().size();
				// don't choose an empty one
				if (vc == 0) {
					continue;
				}
				// see if it's the primary set (set by the user)
				if (this.primaryLyrics != null && this.primaryLyrics.equals(lrcs.id)) {
					return lrcs;
				// see if it's the original set of lyrics
				} else if (lrcs.original) {
					return lrcs;
				// otherwise its the one without a language setting
				} else if (lrcs.language == null || lrcs.language.isEmpty()) {
					return lrcs;
				// otherwise its the first one that matches the current locale
				// check the current match level to make sure we get the last one 
				// (we want the first)
				} else if (sl != null &&
						   locale.getLanguage().equals(sl.getLanguage()) &&
						   locale.getCountry().equals(sl.getCountry()) &&
						   matchLevel < 2) {
					lyrics = lrcs;
					matchLevel = 2;
				// otherwise its the first one that matches the current language
				// check the current match level to make sure we don't replace one
			    // that is more locale specific or get the last one (we want the first)
				} else if (sl != null &&
						   locale.getLanguage().equals(sl.getLanguage()) &&
						   matchLevel < 1) {
					lyrics = lrcs;
					matchLevel = 1;
				// otherwise choose the one with the most verses
				} else if (verseCount < vc &&
						   matchLevel < 1) {
					lyrics = lrcs;
					verseCount = vc;
				}
			}
		}
		return lyrics;
	}

	/**
	 * Returns the lyrics for the given language and, optionally, transliteration.
	 * @param language the language; can be null
	 * @param transliteration the transliteration; can be null
	 * @return {@link Lyrics}
	 */
	public Lyrics getLyrics(String language, String transliteration) {
		for (Lyrics lyrics : this.lyrics) {
			if (StringUtils.equalsIgnoreCase(lyrics.language, language) &&
				StringUtils.equalsIgnoreCase(lyrics.transliteration, transliteration)) {
				return lyrics;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of locales in this song's lyrics.
	 * @return List&lt;Locale&gt;
	 */
	public Set<Locale> getLocales() {
		Set<Locale> locales = new TreeSet<Locale>();
		// loop over the lyrics
		for (Lyrics lyrics : this.lyrics) {
			locales.add(lyrics.getLocale());
		}
		return locales;
	}
	
	/**
	 * Returns the unique identifier.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the created on date.
	 * @return Date
	 */
	public Instant getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Returns the source.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the source.
	 * @param source the source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the last modified date.
	 * @return Instant
	 */
	public Instant getModifiedDate() {
		return this.modifiedDate;
	}

	/**
	 * Sets the last modified date.
	 * @param modifiedDate the last modified date
	 */
	public void setModifiedDate(Instant modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Returns the copyright.
	 * @return String
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * Sets the copyright.
	 * @param copyright the copyright
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * Returns the CCLI number.
	 * @return int
	 */
	public int getCcli() {
		return this.ccli;
	}

	/**
	 * Sets the CCLI number.
	 * @param ccli the ccli number
	 */
	public void setCcli(int ccli) {
		this.ccli = ccli;
	}

	/**
	 * Returns the release date.
	 * @return String
	 */
	public String getReleased() {
		return this.released;
	}

	/**
	 * Sets the release date.
	 * @param released the release date
	 */
	public void setReleased(String released) {
		this.released = released;
	}

	/**
	 * Returns the transposition.
	 * @return int
	 */
	public int getTransposition() {
		return this.transposition;
	}

	/**
	 * Sets the transposition.
	 * @param transposition the transposition
	 */
	public void setTransposition(int transposition) {
		this.transposition = transposition;
	}

	/**
	 * Returns the tempo.
	 * @return String
	 */
	public String getTempo() {
		return this.tempo;
	}

	/**
	 * Sets the tempo.
	 * @param tempo the tempo
	 */
	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	/**
	 * Returns the key (Eb for example).
	 * @return String
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Sets the key.
	 * @param key the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the variant.
	 * @return String
	 */
	public String getVariant() {
		return this.variant;
	}

	/**
	 * Sets the variant.
	 * @param variant the variant
	 */
	public void setVariant(String variant) {
		this.variant = variant;
	}

	/**
	 * Returns the publisher
	 * @return String
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * Sets the publisher
	 * @param publisher the publisher
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * Returns the comments.
	 * @return String
	 */
	public String getComments() {
		return this.comments;
	}

	/**
	 * Sets the comments.
	 * @param comments the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * Returns the keywords.
	 * @return String
	 */
	public String getKeywords() {
		return this.keywords;
	}
	
	/**
	 * Sets the keywords.
	 * @param keywords the keywords
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Returns the primary lyrics id.
	 * @return UUID
	 */
	public UUID getPrimaryLyrics() {
		return this.primaryLyrics;
	}
	
	/**
	 * Sets the primary lyrics id.
	 * @param primaryLyrics the primary lyrics id
	 */
	public void setPrimaryLyrics(UUID primaryLyrics) {
		this.primaryLyrics = primaryLyrics;
	}
	
	/**
	 * Returns the lyrics.
	 * @return List&lt;{@link Lyrics}&gt;
	 */
	public List<Lyrics> getLyrics() {
		return this.lyrics;
	}

	/**
	 * Returns the verse sequence.
	 * @return List&lt;String&gt;
	 */
	public List<String> getSequence() {
		return this.sequence;
	}

	/**
	 * Returns the tags.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		return this.tags;
	}
}
