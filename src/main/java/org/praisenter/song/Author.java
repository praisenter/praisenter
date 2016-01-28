package org.praisenter.song;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.praisenter.Localized;

@XmlRootElement(name = "author")
@XmlAccessorType(XmlAccessType.NONE)
public final class Author implements Localized {
	public static final String TYPE_WORDS = "words";
	public static final String TYPE_MUSIC = "music";
	public static final String TYPE_TRANSLATION = "translation";

	@XmlAttribute(name = "type", required = false)
	String type;
	
	@XmlAttribute(name = "language", required = false)
	String language;
	
	@XmlValue
	String name;

	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
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
