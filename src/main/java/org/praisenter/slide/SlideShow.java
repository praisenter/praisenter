package org.praisenter.slide;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlSeeAlso({
	BasicSlide.class,
	SongSlide.class,
	BibleSlide.class
})
@XmlRootElement(name = "slideShow")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideShow {
	@XmlElementWrapper(name = "slides")
	@XmlElement(name = "slide")
	@XmlJavaTypeAdapter(value = SlideXmlAdapter.class)
	List<Slide> slides;

	public SlideShow() {
		this.slides = new ArrayList<Slide>();
	}
	
	public List<Slide> getSlides() {
		return slides;
	}

	public void setSlides(List<Slide> slides) {
		this.slides = slides;
	}
}
