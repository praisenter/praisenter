package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "songbook")
@XmlAccessorType(XmlAccessType.NONE)
public final class Songbook {
	@XmlAttribute(name = "name", required = false)
	String name;
	
	@XmlAttribute(name = "entry", required = false)
	String entry;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
}
