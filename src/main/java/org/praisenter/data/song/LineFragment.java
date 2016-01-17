package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlRootElement
@XmlSeeAlso(value = { Tag.class, Chord.class, Br.class, LineComment.class })
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class LineFragment {
	
	String text = null;

	@Override
	public String toString() {
		return text;
	}
	
	public String getText() {
		return text;
	}
	@XmlValue
	public void setText(String text) {
		this.text = text;
	}
}
