package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "author")
@XmlAccessorType(XmlAccessType.NONE)
public final class Author {
	public static final String TYPE_WORDS = "words";
	public static final String TYPE_MUSIC = "music";
	public static final String TYPE_TRANSLATION = "translation";
	
	int songId;
	
	@XmlAttribute(name = "type", required = false)
	String type;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlValue
	String name;

	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
