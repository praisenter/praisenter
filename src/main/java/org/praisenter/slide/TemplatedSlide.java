package org.praisenter.slide;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

// TODO a slide created from a basic slide, we want to track the basic slide it came from (maybe to do updates to this slide if the user updates the basic slide)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class TemplatedSlide extends BasicSlide implements Slide, SlideRegion  {
	/** The id of the slide this slide was based on */
	@XmlAttribute(name = "templateId", required = false)
	final UUID templateId;

	public TemplatedSlide(UUID templateId) {
		this.templateId = templateId;
	}
	
	TemplatedSlide(UUID id, UUID templateId) {
		super(id);
		this.templateId = templateId;
	}
	
	protected static final UUID getRootTemplateId(Slide slide) {
		if (slide == null) return null;
		if (slide instanceof TemplatedSlide) { 
			return ((TemplatedSlide)slide).getTemplateId();
		}
		return slide.getId();
	}

	// could be null
	public UUID getTemplateId() {
		return templateId;
	}
}
