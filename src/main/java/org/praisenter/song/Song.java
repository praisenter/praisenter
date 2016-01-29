package org.praisenter.song;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.praisenter.Constants;
import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public class Song implements SongOutput, Comparable<Song> {
	/** For string comparison */
	static final Collator COLLATOR = Collator.getInstance();

	@XmlAttribute(name = "id", required = false)
	UUID id;
	
	@XmlAttribute(name = "createdDate", required = false)
	Date createdDate;
	
	@XmlAttribute(name = "createdIn", required = false)
	String createdIn;
	
	@XmlAttribute(name = "lastModifiedIn", required = false)
	String lastModifiedIn;

	@XmlAttribute(name = "lastModifiedDate", required = false)
	Date lastModifiedDate;

	@XmlAttribute(name = "copyright", required = false)
	String copyright;
	
	@XmlAttribute(name = "ccli", required = false)
	int ccli;
	
	@XmlAttribute(name = "released", required = false)
	String released;
	
	@XmlAttribute(name = "transposition", required = false)
	int transposition;
	
	@XmlAttribute(name = "tempo", required = false)
	String tempo;
	
	@XmlAttribute(name = "key", required = false)
	String key;
	
	@XmlAttribute(name = "variant", required = false)
	String variant;
	
	@XmlAttribute(name = "publisher", required = false)
	String publisher;
	
	@XmlAttribute(name = "version", required = false)
	String version;
	
	@XmlElement(name = "keywords", required = false)
	String keywords;

	@XmlElement(name = "sequence", required = false)
	String sequence;

	@XmlElement(name = "comments", required = false)
	String comments;

	// lists
	
	@XmlElement(name = "title", required = false)
	@XmlElementWrapper(name = "titles", required = false)
	List<Title> titles;
	
	@XmlElement(name = "author", required = false)
	@XmlElementWrapper(name = "authors", required = false)
	List<Author> authors;

	@XmlElement(name = "theme", required = false)
	@XmlElementWrapper(name = "themes", required = false)
	List<Theme> themes;
	
	@XmlElement(name = "songbook", required = false)
	@XmlElementWrapper(name = "songbooks", required = false)
	List<Songbook> songbooks;
	
	@XmlElement(name = "lyrics", required = false)
	@XmlElementWrapper(name = "lyricsets", required = false)
	List<Lyrics> lyrics;
	
	public Song() {
		this.id = UUID.randomUUID();
		this.ccli = -1;
		this.transposition = 0;
		this.titles = new ArrayList<>();
		this.authors = new ArrayList<>();
		this.themes = new ArrayList<>();
		this.songbooks = new ArrayList<>();
		this.lyrics = new ArrayList<>();
	}
	
	// BCP 47
	// ISO-639 language code
	// ISO-3166-1 country code
	// format xx-YY
	// at this time variants and other designations not supported
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

	public Lyrics getLyrics(String language, String transliteration) {
		for (Lyrics lyrics : this.lyrics) {
			if (StringUtils.equalsIgnoreCase(lyrics.language, language) &&
				StringUtils.equalsIgnoreCase(lyrics.transliteration, transliteration)) {
				return lyrics;
			}
		}
		return null;
	}
	
	public UUID getId() {
		return id;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedIn() {
		return createdIn;
	}

	public void setCreatedIn(String createdIn) {
		this.createdIn = createdIn;
	}

	public String getLastModifiedIn() {
		return lastModifiedIn;
	}

	public void setLastModifiedIn(String lastModifiedIn) {
		this.lastModifiedIn = lastModifiedIn;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public int getCcli() {
		return ccli;
	}

	public void setCcli(int ccli) {
		this.ccli = ccli;
	}

	public String getReleased() {
		return released;
	}

	public void setReleased(String released) {
		this.released = released;
	}

	public int getTransposition() {
		return transposition;
	}

	public void setTransposition(int transposition) {
		this.transposition = transposition;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Title> getTitles() {
		return titles;
	}

	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<Theme> getThemes() {
		return themes;
	}

	public void setThemes(List<Theme> themes) {
		this.themes = themes;
	}

	public List<Songbook> getSongbooks() {
		return songbooks;
	}

	public void setSongbooks(List<Songbook> songbooks) {
		this.songbooks = songbooks;
	}

	public List<Lyrics> getLyrics() {
		return lyrics;
	}

	public void setLyrics(List<Lyrics> lyrics) {
		this.lyrics = lyrics;
	}
	
//	public void prepare() {
//		for (Verse verse : this.verses) {
//			for (OpenLyricsLine line : verse.lines) {
//				line.prepare();
//			}
//		}
//	}
//	
//	@Override
//	public String getDisplayText(DisplayType type) {
//		StringBuilder sb = new StringBuilder();
//		for (Verse verse : this.verses) {
//			if (type == DisplayType.EDIT) {
//				sb.append(verse.getName()).append(RuntimeProperties.NEW_LINE_SEPARATOR);
//			}
//			sb.append(verse.getDisplayText(type))
//			  .append(RuntimeProperties.NEW_LINE_SEPARATOR);
//		}
//		return sb.toString();
//	}

}
