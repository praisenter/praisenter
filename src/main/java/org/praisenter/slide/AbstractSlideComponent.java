package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractSlideComponent extends AbstractSlideRegion implements SlideRegion, SlideComponent {
	@XmlAttribute(name = "order", required = false)
	int order;
	
	@Override
	public int compareTo(SlideComponent o) {
		return this.order - o.getOrder();
	}
	
	@Override
	public int getOrder() {
		return this.order;
	}
	
	@Override
	public void setOrder(int order) {
		this.order = order;
	}
}
