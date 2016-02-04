package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.object.MediaObject;

@XmlSeeAlso({
	SlideColor.class,
	SlideLinearGradient.class,
	SlideRadialGradient.class,
	MediaObject.class
})
public interface SlidePaint {
}
