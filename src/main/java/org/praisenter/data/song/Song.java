package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public class Song implements Comparable<Song> {
	/** The version of openlyrics the song classes conform to */
	public static final String VERSION = "0.8";
	
	/** The id assigned to new songs */
	protected static final int NEW_SONG_ID = -1;
	
	int id;
	
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
			}
		}
		return title;
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
