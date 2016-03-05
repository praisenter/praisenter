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
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.commons.lang3.StringUtils;
import org.praisenter.Constants;
import org.praisenter.Tag;

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
 * and cataloging, {@link #getKeywords()} and {@link #getTags()} in particular.
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

	// for internal use really
	/** The file path */
	Path path;
	
	/** The format version */
	@XmlAttribute(name = "format", required = false)
	final String format = Constants.VERSION;
	
	/** The song unique id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;
	
	/** The created on date */
	@XmlAttribute(name = "createdDate", required = false)
	Date createdDate;
	
	/** The created in application */
	@XmlAttribute(name = "createdIn", required = false)
	String createdIn;
	
	/** The last modified in application */
	@XmlAttribute(name = "lastModifiedIn", required = false)
	String lastModifiedIn;

	/** The last modified date */
	@XmlAttribute(name = "lastModifiedDate", required = false)
	Date lastModifiedDate;

	/** The song's copyright information */
	@XmlAttribute(name = "copyright", required = false)
	String copyright;
	
	/** The song's CCLI number */
	@XmlAttribute(name = "ccli", required = false)
	int ccli;
	
	/** The song's release date */
	@XmlAttribute(name = "released", required = false)
	String released;
	
	/** The song's transposition */
	@XmlAttribute(name = "transposition", required = false)
	int transposition;
	
	/** The song's tempo; typically in bpm */
	@XmlAttribute(name = "tempo", required = false)
	String tempo;
	
	/** The song's key */
	@XmlAttribute(name = "key", required = false)
	String key;
	
	/** The variant name for this song */
	@XmlAttribute(name = "variant", required = false)
	String variant;
	
	/** The publisher */
	@XmlAttribute(name = "publisher", required = false)
	String publisher;
	
	/** The version of this song */
	@XmlAttribute(name = "version", required = false)
	String version;
	
	/** A space separated list of keywords for searching */
	@XmlElement(name = "keywords", required = false)
	String keywords;

	/** The sequence of verses space separated */
	@XmlElement(name = "sequence", required = false)
	String sequence;

	/** The comments */
	@XmlElement(name = "comments", required = false)
	String comments;

	// lists
	
	/** The titles */
	@XmlElement(name = "title", required = false)
	@XmlElementWrapper(name = "titles", required = false)
	List<Title> titles;
	
	/** The authors */
	@XmlElement(name = "author", required = false)
	@XmlElementWrapper(name = "authors", required = false)
	List<Author> authors;

	/** The tags; useful for searching or grouping */
	@XmlElement(name = "tag", required = false)
	@XmlElementWrapper(name = "tags", required = false)
	List<Tag> tags;
	
	/** The song books that this song is in */
	@XmlElement(name = "songbook", required = false)
	@XmlElementWrapper(name = "songbooks", required = false)
	List<Songbook> songbooks;
	
	/** The lyrics */
	@XmlElement(name = "lyrics", required = false)
	@XmlElementWrapper(name = "lyricsets", required = false)
	List<Lyrics> lyrics;
	
	/**
	 * Default constructor.
	 */
	public Song() {
		this.path = null;
		this.id = UUID.randomUUID();
		this.createdDate = new Date();
		this.createdIn = Constants.NAME + " " + Constants.VERSION;
		this.lastModifiedDate = this.createdDate;
		this.lastModifiedIn = this.createdIn;
		this.ccli = -1;
		this.transposition = 0;
		this.titles = new ArrayList<>();
		this.authors = new ArrayList<>();
		this.tags = new ArrayList<>();
		this.songbooks = new ArrayList<>();
		this.lyrics = new ArrayList<>();
	}
	
	/**
	 * Returns the locale for the given language.
	 * <p>
	 * The language should be in the BCP 47 format (xx-YY) using
	 * ISO-639 language codes and ISO-3166-1 country codes.
	 * <p>
	 * At this time variants and other designations not supported.
	 * @param language the language
	 * @return Locale
	 */
	static final Locale getLocale(String language) {
		// converts the language to a locale
		if (language != null && language.length() > 0) {
			String[] parts = language.split("[-]");
			if (parts.length == 1) {
				return new Locale(parts[0]);
			} else if (parts.length == 2) {
				return new Locale(parts[0], parts[1]);
			} else {
				return null;
			}
		}
		return null;
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
		Title title = this.getDefaultTitle();
		if (title == null) {
			return "Untitled";
		}
		return title.text;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Song o) {
		// sort by title first
		Title t0 = this.getDefaultTitle();
		Title t1 = o.getDefaultTitle();
		String s0 = t0 == null ? "" : t0.text;
		String s1 = t1 == null ? "" : t1.text;
		int diff = COLLATOR.compare(s0, s1);
		if (diff == 0) {
			// then sort by variant
			diff = COLLATOR.compare(this.variant, o.variant);
			if (diff == 0) {
				// then sort by author
				Author a0 = this.getDefaultAuthor();
				Author a1 = o.getDefaultAuthor();
				s0 = a0 == null ? "" : a0.name;
				s1 = a1 == null ? "" : a1.name;
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
	 * Returns the default author based on the following criteria:
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
				} else if (Author.TYPE_WORDS.equals(auth.type) && matchType < 1) {
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
	 * Returns the default title for the song using the following criteria:
	 * <br>
	 * The first non-empty title to match:
	 * <ol>
	 * <li>No language or transliteration</li>
	 * <li>The language/country that matches the current locale</li>
	 * <li>The language that matches the current locale</li>
	 * <li>The first title in the list</li>
	 * </ol>
	 * @return {@link Title}
	 */
	public Title getDefaultTitle() {
		Locale locale = Locale.getDefault();
		Title title = null;
		int matchLevel = 0;
		if (this.titles.size() > 0) {
			// default to the first one
			title = this.titles.get(0);
			// try to find the best one
			for (Title ttl : this.titles) {
				Locale tl = ttl.getLocale();
				// don't choose an empty one
				if (ttl.text == null || ttl.text.isEmpty()) {
					continue;
				}
				// the original trumps all
				if (ttl.isOriginal()) {
					return ttl;
				// otherwise its the first one without a language setting
				} else if (ttl.language == null || ttl.language.isEmpty()) {
					return ttl;
				// otherwise its the first one that matches the current locale
				// check the current match level to make sure we get the last one 
				// (we want the first)
				} else if (tl != null && 
						   locale.getLanguage().equals(tl.getLanguage()) &&
						   locale.getCountry().equals(tl.getCountry()) &&
						   matchLevel < 2) {
					title = ttl;
					matchLevel = 2;
				// otherwise its the first one that matches the current language
				// check the current match level to make sure we don't replace one
			    // that is more locale specific or get the last one (we want the first)
				} else if (tl != null && 
						   locale.getLanguage().equals(tl.getLanguage()) &&
						   matchLevel < 1) {
					title = ttl;
					matchLevel = 1;
				}
				// or the first if we don't find any of the above
			}
		}
		return title;
	}
	
	/**
	 * Returns the default set of lyrics using the following criteria:
	 * <br>
	 * The first non-empty author to match:
	 * <ol>
	 * <li>No type attribute</li>
	 * <li>The type == words</li>
	 * <li>The type == music</li>
	 * <li>The first author in the list</li>
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
				// otherwise its the one without a language setting
				if (lrcs.language == null || lrcs.language.isEmpty()) {
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
	 * Returns a list of locales in this song's lyrics and titles.
	 * @return List&lt;Locale&gt;
	 */
	public Set<Locale> getLocales() {
		Set<Locale> locales = new TreeSet<Locale>();
		// loop over the titles
		for (Title title : this.titles) {
			locales.add(title.getLocale());
		}
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
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Sets the created date.
	 * @param createdDate the created date
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Returns the created in application.
	 * @return String
	 */
	public String getCreatedIn() {
		return this.createdIn;
	}

	/**
	 * Sets the created in application.
	 * @param createdIn the created in application
	 */
	public void setCreatedIn(String createdIn) {
		this.createdIn = createdIn;
	}

	/**
	 * Returns the last modified in application.
	 * @return String
	 */
	public String getLastModifiedIn() {
		return this.lastModifiedIn;
	}

	/**
	 * Sets the last modified in application.
	 * @param lastModifiedIn the last modified in application
	 */
	public void setLastModifiedIn(String lastModifiedIn) {
		this.lastModifiedIn = lastModifiedIn;
	}

	/**
	 * Returns the last modified date.
	 * @return Date
	 */
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Sets the last modified date.
	 * @param lastModifiedDate the last modified date
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	 * Returns the version.
	 * @return String
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Sets the version.
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the keywords in a space separated string.
	 * @return String
	 */
	public String getKeywords() {
		return this.keywords;
	}

	/**
	 * Sets the keywords.
	 * @param keywords the keywords in a space separated string
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * Returns the verse sequence space separated.
	 * @return String
	 */
	public String getSequence() {
		return this.sequence;
	}

	/**
	 * Sets the verse sequence.
	 * @param sequence the verse sequence space separated
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
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
	 * Returns the titles.
	 * @return List&lt;{@link Title}&gtl;
	 */
	public List<Title> getTitles() {
		return this.titles;
	}

	/**
	 * Sets the titles.
	 * @param titles the titles
	 */
	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}

	/**
	 * Returns the authors.
	 * @return List&lt;{@link Author}&gt;
	 */
	public List<Author> getAuthors() {
		return this.authors;
	}

	/**
	 * Sets the authors.
	 * @param authors the authors
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	/**
	 * Returns the tags.
	 * @return List&lt;{@link Tag}&gt;
	 */
	public List<Tag> getTags() {
		return this.tags;
	}

	/**
	 * Sets the tags.
	 * @param tags the tags
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Returns the song books.
	 * @return List&lt;{@link Songbook}&gt;
	 */
	public List<Songbook> getSongbooks() {
		return this.songbooks;
	}

	/**
	 * Sets the song books.
	 * @param songbooks the song books
	 */
	public void setSongbooks(List<Songbook> songbooks) {
		this.songbooks = songbooks;
	}

	/**
	 * Returns the lyrics.
	 * @return List&lt;{@link Lyrics}&gt;
	 */
	public List<Lyrics> getLyrics() {
		return this.lyrics;
	}

	/**
	 * Sets the lyrics.
	 * @param lyrics the lyrics
	 */
	public void setLyrics(List<Lyrics> lyrics) {
		this.lyrics = lyrics;
	}
}
