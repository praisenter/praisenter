package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.NONE)
public final class Tag extends LineFragment {
	@XmlAttribute(name = "name")
	String name;
	
	@Override
	public String toString() {
		return "<tag name='" + name + "'>" + text + "</tag>";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
