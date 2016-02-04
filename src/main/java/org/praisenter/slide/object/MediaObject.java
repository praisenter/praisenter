package org.praisenter.slide.object;

import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.graphics.AbstractSlidePaint;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlidePaint;

// TODO note: the usefulness of this class is limited:
//	1. An audio paint is only compatible with a MediaComponent since it has no "visible" aspect.
//	2. A video paint is only compatible with backgrounds and the MediaComponent (i.e. not borders, text, etc.).
//	3. An image paint is compatible with everything backgrounds, borders, etc.
@XmlRootElement(name = "media")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaObject extends AbstractSlidePaint implements SlidePaint {
	@XmlAttribute(name = "id", required = false)
	UUID id;

	@XmlAttribute(name = "scaling", required = false)
	ScaleType scaling;
	
	@XmlAttribute(name = "loop", required = false)
	boolean loop;
	
	@XmlAttribute(name = "mute", required = false)
	boolean mute;

	public MediaObject() {
		this.scaling = ScaleType.UNIFORM;
		this.loop = false;
		this.mute = false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaObject) {
			MediaObject m = (MediaObject)obj;
			return Objects.equals(id, m.id) &&
				   scaling == m.scaling &&
				   loop == m.loop &&
				   mute == m.mute;
		}
		return false;
	}
	
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
