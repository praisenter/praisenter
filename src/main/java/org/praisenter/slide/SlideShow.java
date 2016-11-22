package org.praisenter.slide;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "slideShow")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideShow {
	
	Path path;
	
	String name;
	
	@XmlElementWrapper(name = "slides")
	@XmlElement(name = "slide")
	List<UUID> slides;

	@XmlElement(name = "loop", required = false)
	boolean loop;
	
	public SlideShow() {
		this.slides = new ArrayList<UUID>();
		this.loop = false;
	}
	
	public List<UUID> getSlides() {
		return slides;
	}

	public void setSlides(List<UUID> slides) {
		this.slides = slides;
	}
}
