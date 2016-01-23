package org.praisenter.data.song;

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
public final class Verse implements DisplayText {
	int songId;
	
	/** The type (c, v, p, b, e...) */
	String type;
	
	/** The number (1, 2, 3...) */
	int number;
	
	/** The part (a, b, c...) */
	String part;
	
	/** The verse name */
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String transliteration;
	
	/** The verse lines */
	@XmlElement(name = "lines")
	List<Line> lines;
	
	public Verse() {
		this.type = "c";
		this.number = 1;
		this.name = "c1";
		this.lines = new ArrayList<Line>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getDisplayText(DisplayType.MAIN);
	}
	
	/**
	 * Generates the verse name based on the type, number and part.
	 */
	private void setName() {
		this.name = this.type + String.valueOf(this.number) + (this.part == null ? "" : this.part);
	}

	public String getName() {
		return name;
	}

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
		setName();
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		setName();
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
		setName();
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
		for (Line line : this.lines) {
			sb.append(line.getDisplayText(type)).append(RuntimeProperties.NEW_LINE_SEPARATOR);
		}
		return sb.toString();
	}
	
	public void setText(String text) {
		if (this.lines == null) {
			this.lines = new ArrayList<Line>();
		}
		this.lines.clear();
		
		try {
			InputStream stream = new ByteArrayInputStream(("<lines xmlns=\"http://openlyrics.info/namespace/2009/song\">" + text + "</lines>").getBytes("UTF-8"));
			Line line = XmlIO.read(stream, Line.class);
			this.lines.add(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
