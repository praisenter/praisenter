package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class ObservableSlideComponent<T extends SlideComponent> extends ObservableSlideRegion<T> implements SlideRegion, SlideComponent {

	final IntegerProperty order = new SimpleIntegerProperty();
	
	public ObservableSlideComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.order.set(component.getOrder());
		
		// listen for changes
		this.order.addListener((obs, ov, nv) -> { this.region.setOrder(nv.intValue()); });
	}
	
	@Override
	public int compareTo(SlideComponent o) {
		return this.region.compareTo(o);
	}

	// order
	
	@Override
	public int getOrder() {
		return this.order.get();
	}
	
	@Override
	public void setOrder(int order) {
		this.order.set(order);
	}
	
	public IntegerProperty orderProperty() {
		return this.order;
	}
}
