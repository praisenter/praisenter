package org.praisenter.data.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "br")
@XmlAccessorType(XmlAccessType.NONE)
public final class Br extends LineFragment {
	@Override
	public String toString() {
		return "<br/>";
	}
	
	public String getText() {
		return null;
	}
}
