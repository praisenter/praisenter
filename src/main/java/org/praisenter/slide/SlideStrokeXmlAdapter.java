package org.praisenter.slide;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.praisenter.slide.graphics.AbstractSlideStroke;
import org.praisenter.slide.graphics.SlideStroke;

public class SlideStrokeXmlAdapter extends XmlAdapter<AbstractSlideStroke, SlideStroke> {

	@Override
	public AbstractSlideStroke marshal(SlideStroke stroke) throws Exception {
		return (AbstractSlideStroke)stroke;
	}

	@Override
	public SlideStroke unmarshal(AbstractSlideStroke stroke) throws Exception {
		return stroke;
	}
}