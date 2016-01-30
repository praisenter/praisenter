package org.praisenter.slide;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.graphics.ScaleType;

@XmlRootElement(name = "media")
@XmlAccessorType(XmlAccessType.NONE)
public class MediaComponent extends AbstractSlideComponent implements SlideRegion, SlideComponent {
	@XmlAttribute(name = "id", required = false)
	UUID id;
	
	@XmlAttribute(name = "scaling", required = false)
	ScaleType scaling;
	
	@XmlAttribute(name = "loop", required = false)
	boolean loop;
	
	@XmlAttribute(name = "mute", required = false)
	boolean mute;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ScaleType getScaling() {
		return scaling;
	}

	public void setScaling(ScaleType scaling) {
		this.scaling = scaling;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
}
