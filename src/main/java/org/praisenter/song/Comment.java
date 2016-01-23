package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
public final class Comment {
	int songId;
	
	@XmlValue
	String text;
	
	@Override
	public String toString() {
		return text;
	}
	
	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
