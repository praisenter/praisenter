package org.praisenter.slide;

public interface SlideComponent extends SlideRegion, Comparable<SlideComponent> {
	// properties
	public abstract int getOrder();
	public abstract void setOrder(int order);
}
