package org.praisenter.data.song;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.StringBinding;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.utility.RuntimeProperties;
import org.praisenter.xml.XmlIO;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class Verse {
	int songId;
	
	String type;
	int number;
	String part;
	int fontSize;
	
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@XmlAttribute(name = "lang", required = false)
	String language;
	
	@XmlAttribute(name = "translit", required = false)
	String transliteration;
	
	@XmlElement(name = "lines")
	List<Line> lines;
	
	String text;
	
	public Verse() {
		this.type = "c";
		this.number = 1;
		this.fontSize = 60;
		this.name = "c1";
		this.lines = new ArrayList<Line>();
	}
	
	private void setName() {
		this.name = type + String.valueOf(number) + (part == null ? "" : part);
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

	public String getName() {
		return name;
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

	public String getDisplayText() {
		StringBuilder sb = new StringBuilder();
		for (Line line : this.lines) {
			for (Object o : line.text) {
				if (o instanceof String) {
					sb.append(o.toString());
				} else if (o instanceof Br) {
					sb.append(RuntimeProperties.NEW_LINE_SEPARATOR);
				} else if (o instanceof Tag) {
					sb.append(((Tag)o).text);
				}
			}
		}
		return sb.toString();
	}
	
	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (Line line : this.lines) {
			for (Object o : line.text) {
				sb.append(o.toString());
			}
		}
		return sb.toString();
	}

	public void setText(String text) {
		this.text = text;
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

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
