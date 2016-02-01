package org.praisenter.slide;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.praisenter.slide.graphics.AbstractSlidePaint;
import org.praisenter.slide.graphics.SlidePaint;

public class SlidePaintXmlAdapter extends XmlAdapter<AbstractSlidePaint, SlidePaint> {

	@Override
	public AbstractSlidePaint marshal(SlidePaint paint) throws Exception {
		return (AbstractSlidePaint)paint;
	}

	@Override
	public SlidePaint unmarshal(AbstractSlidePaint paint) throws Exception {
		return paint;
	}
}