package org.praisenter.song;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chord")
@XmlAccessorType(XmlAccessType.NONE)
public final class Chord implements VerseFragment, SongOutput {
	private static final String EDIT_FORMAT = "<chord name=\"{0}\"/>";
	
	/** The chord */
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public String getOutput(SongOutputType type) {
		if (type == SongOutputType.TEXT) {
			return "";
		} else {
			return MessageFormat.format(EDIT_FORMAT, this.name);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
