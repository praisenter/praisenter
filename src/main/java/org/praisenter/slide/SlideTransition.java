package org.praisenter.slide;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

// TODO the thought here is that you could transition individual components
@XmlRootElement(name = "transition")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideTransition {
	@XmlAttribute(name = "id", required = false)
	UUID id;
	
	@XmlAttribute(name = "transitionId", required = false)
	int transitionId;
	
	@XmlAttribute(name = "easingId", required = false)
	int easingId;
	
	// ignored for slides
	@XmlAttribute(name = "delay", required = false)
	long delay;
	
	@XmlAttribute(name = "duration", required = false)
	long duration;
	
	public SlideTransition copy() {
		SlideTransition st = new SlideTransition();
		st.id = id;
		st.transitionId = transitionId;
		st.easingId = easingId;
		st.delay = delay;
		st.duration = duration;
		return st;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getTransitionId() {
		return transitionId;
	}
	
	public void setTransitionId(int transitionId) {
		this.transitionId = transitionId;
	}
	
	public int getEasingId() {
		return easingId;
	}
	
	public void setEasingId(int easingId) {
		this.easingId = easingId;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
}
