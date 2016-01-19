package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;
import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public class Song implements Comparable<Song>, DisplayText {
	/** The version of openlyrics the song classes conform to */
	public static final String VERSION = "0.8";
	
	/** The id assigned to new songs */
	protected static final int NEW_SONG_ID = -1;
	
	int id;
	
	// metadata used only for openlyrics format
	
	/** The openlyrics format version */
	@XmlAttribute(name = "version")
	final String version = VERSION;
	
	/** The original created in application */
	@XmlAttribute(name = "createdIn")
	String createdIn;
	
	/** The last modified in application */
	@XmlAttribute(name = "modifiedIn")
	String modifiedIn;
	
	/** The last modified date */
	@XmlAttribute(name = "modifiedDate")
	Date modifiedDate;
	
	// data
	
	Date dateAdded;
	
	@XmlElement(name = "properties", required = false, nillable = true)
	Properties properties;
	
	@XmlElementWrapper(name = "lyrics")
	@XmlElement(name = "verse")
	List<Verse> verses;
	
	public Song() {
		this.id = NEW_SONG_ID;
		this.properties = new Properties();
		this.verses = new ArrayList<Verse>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Song) {
			Song song = (Song)obj;
			if (song.id == this.id) {
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
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Song[Id=").append(this.id)
		  .append("|DateAdded=").append(this.dateAdded)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Song o) {
		// sort by title first
		int diff = 0; // FIXME this.title.compareToIgnoreCase(o.title);
		if (diff == 0) {
			// then sort by date added
			diff = this.dateAdded.compareTo(o.dateAdded);
			if (diff == 0) {
				// finally sort by id
				diff = this.id - o.id;
			}
		}
		return diff;
	}
	
	/**
	 * Returns true if this song is a new song (that has not been saved).
	 * @return boolean
	 */
	public boolean isNew() {
		return this.id == Song.NEW_SONG_ID;
	}
	
	public List<Verse> getVerses(String name) {
		List<Verse> verses = new ArrayList<Verse>();
		for (Verse verse : this.verses) {
			if (verse.name.equals(name)) {
				verses.add(verse);
			}
		}
		return verses;
	}
	
	public Title getDefaultTitle() {
		Title title = null;
		if (this.properties.titles.size() > 0) {
			// default to the first one
			title = this.properties.titles.get(0);
			// try to find the best one
			for (Title ttl : this.properties.titles) {
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
	
	public void removeTags() {
		for (Verse verse : this.verses) {
			for (Line line : verse.lines) {
				line.removeTags();
			}
		}
	}
	
	@Override
	public String getDisplayText(DisplayType type) {
		StringBuilder sb = new StringBuilder();
		for (Verse verse : this.verses) {
			if (type == DisplayType.EDIT) {
				sb.append(verse.getName()).append(RuntimeProperties.NEW_LINE_SEPARATOR);
			}
			sb.append(verse.getDisplayText(type))
			  .append(RuntimeProperties.NEW_LINE_SEPARATOR);
		}
		return sb.toString();
	}
	
	public List<SearchableText> getSearchableText() {
		// FIXME include titles, song text, what else?
		List<SearchableText> txt = new ArrayList<SearchableText>();
		for (Verse verse : this.verses) {
			SearchableText st = new SearchableText();
			st.songId = this.id;
			st.part = verse.part;
			st.text = verse.getDisplayText(DisplayType.MAIN).toUpperCase();
			st.language = verse.language;
			st.translit = verse.transliteration;
			txt.add(st);
		}
		for (Title title : this.properties.titles) {
			SearchableText st = new SearchableText();
			st.songId = this.id;
			st.part = "title";
			st.text = title.text.toUpperCase();
			st.language = title.language;
			st.translit = title.transliteration;
			txt.add(st);
		}
		return txt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<Verse> getVerses() {
		return verses;
	}

	public void setVerses(List<Verse> verses) {
		this.verses = verses;
	}
}
