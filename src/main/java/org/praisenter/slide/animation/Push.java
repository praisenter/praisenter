package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "push")
@XmlAccessorType(XmlAccessType.NONE)
public final class Push extends SlideAnimation {
	@XmlAttribute(name = "direction", required = false)
	Direction direction;
	
	public Push() {
		this.direction = Direction.UP;
	}
	
	@Override
	public Push copy(UUID id) {
		Push animation = new Push();
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
