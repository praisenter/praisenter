package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({
	SlideColor.class,
	SlideLinearGradient.class,
	SlideRadialGradient.class
})
public interface SlidePaint {
}
