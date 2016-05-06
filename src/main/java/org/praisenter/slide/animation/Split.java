package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "split")
@XmlAccessorType(XmlAccessType.NONE)
public final class Split extends SlideAnimation {
	@XmlElement(name = "orientation", required = false)
	Orientation orientation;
	
	@XmlElement(name = "operation", required = false)
	Operation operation;
	
	public Split() {
		this.orientation = Orientation.HORIZONTAL;
		this.operation = Operation.COLLAPSE;
	}
	
	@Override
	public Split copy(UUID id) {
		Split animation = new Split();
		copy(animation, id);
		animation.orientation = this.orientation;
		animation.operation = this.operation;
		return animation;
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
}
