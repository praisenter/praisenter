package org.praisenter.song;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
public final class Comment implements VerseFragment, SongOutput {
	private static final String FORMAT = "<comment>{0}</comment>";
	
	/** The comment text */
	@XmlValue
	String text;

	@Override
	public String toString() {
		return this.text;
	}
	
	@Override
	public String getOutput(SongOutputType type) {
		if (type == SongOutputType.TEXT) {
			return "";
		} else {
			return MessageFormat.format(FORMAT, this.text);
		}
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
