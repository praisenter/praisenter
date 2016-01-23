package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class VerseMetadata {
	public static final int DEFAULT_FONT_SIZE = 60;
	
	@XmlAttribute(name = "name")
	final String name;
	
	@XmlAttribute(name = "fontSize")
	int fontSize;
	
	VerseMetadata() {
		// for jaxb only
		this.name = null;
		this.fontSize = -1;
	}
	
	public VerseMetadata(String name) {
		this(name, DEFAULT_FONT_SIZE);
	}
	
	public VerseMetadata(String name, int fontSize) {
		this.name = name;
		this.fontSize = fontSize > 0 ? fontSize : DEFAULT_FONT_SIZE;
	}

	public String getName() {
		return name;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
