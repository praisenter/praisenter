package org.praisenter.slide;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.praisenter.slide.graphics.AbstractSlidePaint;
import org.praisenter.slide.graphics.SlidePaint;

public final class SlideXmlAdapter extends XmlAdapter<BasicSlide, Slide> {
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public BasicSlide marshal(Slide slide) throws Exception {
		return (BasicSlide)slide;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Slide unmarshal(BasicSlide slide) throws Exception {
		return slide;
	}
}