package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;
import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "br")
@XmlAccessorType(XmlAccessType.NONE)
public final class Br implements DisplayText {
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getDisplayText(DisplayType type) {
		if (type == DisplayType.EDIT) {
			return "<br/>";
		}
		return RuntimeProperties.NEW_LINE_SEPARATOR;
	}
}
