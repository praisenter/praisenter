package org.praisenter.song.openlyrics;

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

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;
import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "song")
@XmlAccessorType(XmlAccessType.NONE)
public class OpenLyricsSong implements DisplayText {
	/** A regex to match new lines */
	static final String NEW_LINE_REGEX = "\\n|\\r|\\n\\r|\\r\\n";
	
	/** A regex to match whitespace */
	static final String NEW_LINE_WHITESPACE = "\\s+";
	
	/** For string comparison */
	static final Collator COLLATOR = Collator.getInstance();
	
	/** The version of openlyrics the song classes conform to */
	public static final String VERSION = "0.8";
	
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
	
	@XmlElement(name = "properties", required = false, nillable = true)
	OpenLyricsProperties properties;
	
	@XmlElementWrapper(name = "lyrics")
	@XmlElement(name = "verse")
	List<OpenLyricsVerse> verses;
	
	public OpenLyricsSong() {
		this.properties = new OpenLyricsProperties();
		this.verses = new ArrayList<OpenLyricsVerse>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		OpenLyricsTitle title = this.getDefaultTitle();
		if (title == null) {
			return "Untitled";
		}
		return title.text;
	}
	
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
	
	public List<OpenLyricsVerse> getVerses(String name) {
		List<OpenLyricsVerse> verses = new ArrayList<OpenLyricsVerse>();
		for (OpenLyricsVerse verse : this.verses) {
			if (verse.name.equals(name)) {
				verses.add(verse);
			}
		}
		return verses;
	}
	
	public OpenLyricsAuthor getDefaultAuthor() {
		OpenLyricsAuthor author = null;
		if (this.properties.authors.size() > 0) {
			// default to the first one
			author = this.properties.authors.get(0);
			// try to find the best one
			for (OpenLyricsAuthor auth : this.properties.authors) {
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
	
	public OpenLyricsTitle getDefaultTitle() {
		OpenLyricsTitle title = null;
		if (this.properties.titles.size() > 0) {
			// default to the first one
			title = this.properties.titles.get(0);
			// try to find the best one
			for (OpenLyricsTitle ttl : this.properties.titles) {
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
	
	public void prepare() {
		for (OpenLyricsVerse verse : this.verses) {
			for (OpenLyricsLine line : verse.lines) {
				line.prepare();
			}
		}
	}
	
	@Override
	public String getDisplayText(DisplayType type) {
		StringBuilder sb = new StringBuilder();
		for (OpenLyricsVerse verse : this.verses) {
			if (type == DisplayType.EDIT) {
				sb.append(verse.getName()).append(RuntimeProperties.NEW_LINE_SEPARATOR);
			}
			sb.append(verse.getDisplayText(type))
			  .append(RuntimeProperties.NEW_LINE_SEPARATOR);
		}
		return sb.toString();
	}
	
	public OpenLyricsProperties getProperties() {
		return properties;
	}

	public void setProperties(OpenLyricsProperties properties) {
		this.properties = properties;
	}

	public List<OpenLyricsVerse> getVerses() {
		return verses;
	}

	public void setVerses(List<OpenLyricsVerse> verses) {
		this.verses = verses;
	}
}
