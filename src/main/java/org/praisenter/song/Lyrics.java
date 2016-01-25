package org.praisenter.song;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lyrics")
@XmlAccessorType(XmlAccessType.NONE)
public final class Lyrics {
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String translit;

	@XmlElement(name = "verse", required = false)
	@XmlElementWrapper(name = "verses")
	List<Verse> verses;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTranslit() {
		return translit;
	}

	public void setTranslit(String translit) {
		this.translit = translit;
	}

	public List<Verse> getVerses() {
		return verses;
	}

	public void setVerses(List<Verse> verses) {
		this.verses = verses;
	}
}
