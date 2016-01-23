package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;

@XmlRootElement(name = "chord")
@XmlAccessorType(XmlAccessType.NONE)
public final class Chord implements DisplayText {
	/** The chord */
	@XmlAttribute(name = "name")
	String name;
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getDisplayText(DisplayType type) {
		if (type == DisplayType.EDIT || type == DisplayType.MUSICIAN) {
			return "<chord name=\"" + name + "\"/>";
		} else {
			return "";
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
