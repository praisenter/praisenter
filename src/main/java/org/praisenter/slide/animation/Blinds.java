package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

// FEATURE allow the number of blinds to be configured

@XmlRootElement(name = "blinds")
@XmlAccessorType(XmlAccessType.NONE)
public final class Blinds extends SlideAnimation {
	@XmlAttribute(name = "orientation", required = false)
	Orientation orientation;
	
	@Override
	public Blinds copy(UUID id) {
		Blinds animation = new Blinds();
		copy(animation, id);
		animation.orientation = this.orientation;
		return animation;
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
}
