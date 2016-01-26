package org.praisenter.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "lyrics")
@XmlAccessorType(XmlAccessType.NONE)
public final class Lyrics implements SongOutput {
	@XmlAttribute(name = "language", required = false)
	String language;
	
	@XmlAttribute(name = "transliteration", required = false)
	String transliteration;

	@XmlElement(name = "verse", required = false)
	@XmlElementWrapper(name = "verses")
	List<Verse> verses;

	public Lyrics() {
		this.verses = new ArrayList<>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getOutput(SongOutputType type) {
		StringBuilder sb = new StringBuilder();
		for (Verse verse : this.verses) {
			if (type == SongOutputType.EDIT) {
				sb.append(verse.getName())
				  .append(RuntimeProperties.NEW_LINE_SEPARATOR);
			}
			sb.append(verse.getOutput(type))
			  .append(RuntimeProperties.NEW_LINE_SEPARATOR)
			  .append(RuntimeProperties.NEW_LINE_SEPARATOR);
		}
		return sb.toString();
	}
	
	public Verse getVerse(String name) {
		for (Verse verse : this.verses) {
			if (name.equalsIgnoreCase(verse.name)) {
				return verse;
			}
		}
		return null;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTransliteration() {
		return transliteration;
	}

	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
	}

	public List<Verse> getVerses() {
		return verses;
	}

	public void setVerses(List<Verse> verses) {
		this.verses = verses;
	}
}
