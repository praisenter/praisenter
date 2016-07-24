package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class ObservableSlideComponent<T extends SlideComponent> extends ObservableSlideRegion<T> implements Comparable<ObservableSlideComponent<?>> {

	final IntegerProperty order = new SimpleIntegerProperty();
	
	public ObservableSlideComponent(T component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.order.set(component.getOrder());
		
		// listen for changes
		this.order.addListener((obs, ov, nv) -> { this.region.setOrder(nv.intValue()); });
	}
	
	public int compareTo(ObservableSlideComponent<?> o) {
		return this.region.compareTo(o.region);
	}

	// order
	
	public int getOrder() {
		return this.order.get();
	}
	
	public void setOrder(int order) {
		this.order.set(order);
	}
	
	public IntegerProperty orderProperty() {
		return this.order;
	}
}
