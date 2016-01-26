package org.praisenter.song.openlyrics;

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
public final class OpenLyricsSong {
	/** A regex to match new lines */
	static final String NEW_LINE_REGEX = "\\n|\\r|\\n\\r|\\r\\n";
	
	/** A regex to match whitespace */
	static final String NEW_LINE_WHITESPACE = "\\s+";
	
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

	public void prepare() {
		for (OpenLyricsVerse verse : this.verses) {
			for (OpenLyricsLine line : verse.lines) {
				line.prepare();
			}
		}
	}
	
	public String getCreatedIn() {
		return createdIn;
	}

	public void setCreatedIn(String createdIn) {
		this.createdIn = createdIn;
	}

	public String getModifiedIn() {
		return modifiedIn;
	}

	public void setModifiedIn(String modifiedIn) {
		this.modifiedIn = modifiedIn;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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

	public String getVersion() {
		return version;
	}
}
