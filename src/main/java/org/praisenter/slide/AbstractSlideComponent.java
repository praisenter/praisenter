package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import javafx.scene.control.Slider;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractSlideComponent extends AbstractSlideRegion implements SlideRegion, SlideComponent {
	@XmlAttribute(name = "order", required = false)
	int order;
	
	@Override
	public int compareTo(SlideComponent o) {
		return this.order - o.getOrder();
	}
	
	/**
	 * Copies over the values of this component to the given component.
	 * @param to the component to copy to
	 */
	protected void copy(SlideComponent to) {
		// shouldn't need a deep copy of any of these
		to.setOrder(this.order);
		this.copy((SlideRegion)to);
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
