package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;

@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.NONE)
public final class LineComment implements DisplayText {
	int songId;
	
	/** The comment text */
	@XmlValue
	String comment;

	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getDisplayText(DisplayType type) {
		if (type == DisplayType.EDIT) {
			return "<comment>" + this.comment + "</comment>";
		} else {
			return "";
		}
	}
	
	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
