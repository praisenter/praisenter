package org.praisenter.song;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.Constants;
import org.praisenter.Localized;
import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "lyrics")
@XmlAccessorType(XmlAccessType.NONE)
public final class Lyrics implements SongOutput, Localized {
	private static final String VERSE_EDIT_FORMAT = "<verse name=\"{0}\" />";
	
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
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return Song.getLocale(this.language);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getOutput(SongOutputType type) {
		StringBuilder sb = new StringBuilder();
		int size = this.verses.size();
		for (int i = 0; i < size; i++) {
			Verse verse = this.verses.get(i);
			
			if (i != 0) {
				sb.append(Constants.NEW_LINE)
				  .append(Constants.NEW_LINE);
			}
			
			if (type == SongOutputType.EDIT) {
				sb.append(MessageFormat.format(VERSE_EDIT_FORMAT, verse.name))
				  .append(Constants.NEW_LINE);
			}
			
			sb.append(verse.getOutput(type));
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
