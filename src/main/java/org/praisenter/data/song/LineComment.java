package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
public final class LineComment extends LineFragment {
	int songId;
	
	@Override
	public String toString() {
		return "<comment>" + text + "</comment>";
	}
	
	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}
}
