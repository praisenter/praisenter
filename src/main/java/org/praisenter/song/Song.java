package org.praisenter.song;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public class Song {
	/** For string comparison */
	static final Collator COLLATOR = Collator.getInstance();

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
	@XmlElementWrapper(name = "titles")
	List<Title> titles;
	
	@XmlElement(name = "author", required = false)
	@XmlElementWrapper(name = "authors")
	List<Author> authors;

	@XmlElement(name = "theme", required = false)
	@XmlElementWrapper(name = "themes")
	List<Theme> themes;
	
	@XmlElement(name = "songbook", required = false)
	@XmlElementWrapper(name = "songbooks")
	List<Songbook> songbooks;
	
	@XmlElement(name = "lyrics", required = false)
	@XmlElementWrapper(name = "lyricsets")
	List<Lyrics> lyrics;
	
	public Song() {
		this.titles = new ArrayList<>();
		this.authors = new ArrayList<>();
		this.themes = new ArrayList<>();
		this.songbooks = new ArrayList<>();
		this.lyrics = new ArrayList<>();
	}
	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		Title title = this.getDefaultTitle();
//		if (title == null) {
//			return "Untitled";
//		}
//		return title.text;
//	}
//	
//	/* (non-Javadoc)
//	 * @see java.lang.Comparable#compareTo(java.lang.Object)
//	 */
//	@Override
//	public int compareTo(Song o) {
//		// sort by title first
//		Title t0 = this.getDefaultTitle();
//		Title t1 = o.getDefaultTitle();
//		String s0 = t0 == null ? "" : t0.text;
//		String s1 = t1 == null ? "" : t1.text;
//		int diff = COLLATOR.compare(s0, s1);
//		if (diff == 0) {
//			// then sort by variant
//			diff = COLLATOR.compare(this.properties.variant, o.properties.variant);
//			if (diff == 0) {
//				// then sort by author
//				Author a0 = this.getDefaultAuthor();
//				Author a1 = o.getDefaultAuthor();
//				s0 = a0 == null ? "" : a0.name;
//				s1 = a1 == null ? "" : a1.name;
//				diff = COLLATOR.compare(s0, s1);
//				if (diff == 0) {
//					// then by added date
//					diff = this.metadata.dateAdded.compareTo(o.metadata.dateAdded);
//				}
//			}
//		}
//		return diff;
//	}
//	
//	public List<Verse> getVerses(String name) {
//		List<Verse> verses = new ArrayList<Verse>();
//		for (Verse verse : this.verses) {
//			if (verse.name.equals(name)) {
//				verses.add(verse);
//			}
//		}
//		return verses;
//	}
	
	public Author getDefaultAuthor() {
		Author author = null;
		if (this.authors.size() > 0) {
			// default to the first one
			author = this.authors.get(0);
			// try to find the best one
			for (Author auth : this.authors) {
				// don't choose an empty one
				if (auth.name == null || auth.name.length() <= 0) {
					continue;
				}
				// otherwise its the one without a language setting
				if (auth.language == null || auth.language.isEmpty()) {
					author = auth;
				}
				// or the first if we don't find any of the above
			}
		}
		return author;
	}
	
	public Title getDefaultTitle() {
		Title title = null;
		if (this.titles.size() > 0) {
			// default to the first one
			title = this.titles.get(0);
			// try to find the best one
			for (Title ttl : this.titles) {
				// don't choose an empty one
				if (ttl.text == null || ttl.text.isEmpty()) {
					continue;
				}
				// the original trumps all
				if (ttl.isOriginal()) {
					return title;
				// otherwise its the one without a language setting
				} else if (ttl.language == null || ttl.language.isEmpty()) {
					title = ttl;
				}
				// or the first if we don't find any of the above
			}
		}
		return title;
	}
	
	public Lyrics getDefaultLyrics() {
		Lyrics lyrics = null;
		if (this.lyrics.size() > 0) {
			// default to the first one
			lyrics = this.lyrics.get(0);
			// try to find the best one
			for (Lyrics lrcs : this.lyrics) {
				// don't choose an empty one
				if (lrcs.verses.size() == 0) {
					continue;
				}
				// otherwise its the one without a language setting
				if (lrcs.language == null || lrcs.language.isEmpty()) {
					lyrics = lrcs;
				}
				// or the first if we don't find any of the above
			}
		}
		return lyrics;
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
