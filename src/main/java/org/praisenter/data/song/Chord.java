package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chord")
@XmlAccessorType(XmlAccessType.NONE)
public final class Chord extends LineFragment {
	@XmlAttribute(name = "name")
	String name;
	
	@Override
	public String toString() {
		return "<chord name=\"" + name + "\"/>";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getText() {
		return null;
	}
}
