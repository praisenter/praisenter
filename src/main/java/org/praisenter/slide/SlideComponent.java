package org.praisenter.slide;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.SongTextComponent;

@XmlSeeAlso({
	MediaComponent.class,
	BasicTextComponent.class,
	DateTimeComponent.class,
	SongTextComponent.class
})
public interface SlideComponent extends SlideRegion, Comparable<SlideComponent> {
	// properties
	public abstract int getOrder();
	public abstract void setOrder(int order);
}
