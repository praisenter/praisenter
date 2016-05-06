package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "blinds")
@XmlAccessorType(XmlAccessType.NONE)
public final class Blinds extends SlideAnimation {
	@XmlElement(name = "orientation", required = false)
	Orientation orientation;
	
	@XmlElement(name = "blindCount", required = false)
	int blindCount;
	
	public Blinds() {
		this.orientation = Orientation.HORIZONTAL;
		this.blindCount = 12;
	}
	
	@Override
	public Blinds copy(UUID id) {
		Blinds animation = new Blinds();
		copy(animation, id);
		animation.orientation = this.orientation;
		animation.blindCount = this.blindCount;
		return animation;
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public int getBlindCount() {
		return this.blindCount;
	}

	public void setBlindCount(int blindCount) {
		this.blindCount = blindCount;
	}
}
