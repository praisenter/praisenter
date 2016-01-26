package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "text")
@XmlAccessorType(XmlAccessType.NONE)
public final class TextFragment implements VerseFragment, SongOutput {
	@XmlValue
	String text;

	@Override
	public String toString() {
		return this.text;
	}
	
	@Override
	public String getOutput(SongOutputType type) {
		return this.text;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
