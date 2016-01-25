package org.praisenter.song.openlyrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;
import org.praisenter.utility.RuntimeProperties;
import org.praisenter.xml.XmlIO;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsVerse implements DisplayText {
	/** The verse name */
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String transliteration;
	
	/** The verse lines */
	@XmlElement(name = "lines")
	List<OpenLyricsLine> lines;
	
	public OpenLyricsVerse() {
		this.name = "c1";
		this.lines = new ArrayList<OpenLyricsLine>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getDisplayText(DisplayType.MAIN);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String getDisplayText(DisplayType type) {
		StringBuilder sb = new StringBuilder();
		for (OpenLyricsLine line : this.lines) {
			sb.append(line.getDisplayText(type)).append(RuntimeProperties.NEW_LINE_SEPARATOR);
		}
		return sb.toString();
	}
	
	public void setText(String text) {
		if (this.lines == null) {
			this.lines = new ArrayList<OpenLyricsLine>();
		}
		this.lines.clear();
		
		try {
			InputStream stream = new ByteArrayInputStream(("<lines xmlns=\"http://openlyrics.info/namespace/2009/song\">" + text + "</lines>").getBytes("UTF-8"));
			OpenLyricsLine line = XmlIO.read(stream, OpenLyricsLine.class);
			this.lines.add(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
