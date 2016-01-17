package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "lines")
@XmlAccessorType(XmlAccessType.NONE)
public final class Line {
	@XmlAttribute(name = "part", required = false)
	String part;
	
	//@XmlAnyElement
	@XmlElementRef(name = "lines", type = LineFragment.class)
	@XmlMixed
	List<Object> text;

	public Line() {
		this.text = new ArrayList<Object>();
	}
	
	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
}
