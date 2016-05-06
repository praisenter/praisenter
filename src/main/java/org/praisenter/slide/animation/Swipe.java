package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "swipe")
@XmlAccessorType(XmlAccessType.NONE)
public final class Swipe extends SlideAnimation {
	@XmlElement(name = "direction", required = false)
	Direction direction;
	
	public Swipe() {
		this.direction = Direction.UP;
	}
	
	@Override
	public Swipe copy(UUID id) {
		Swipe animation = new Swipe();
		copy(animation, id);
		animation.direction = this.direction;
		return animation;
	}
	
	public Direction getDirection() {
		return this.direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
