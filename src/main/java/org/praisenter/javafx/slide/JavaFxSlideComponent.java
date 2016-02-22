package org.praisenter.javafx.slide;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

// TODO the thought here is that this will be what we bind the edit UI to
// TODO we'll have setters that set the values in the components and in the appropriate JavaFX object.
public final class JavaFxSlideComponent<T extends SlideComponent> {
	final PraisenterContext context;
	final T component;
	final Region region;

	final IntegerProperty width;
	final IntegerProperty height;
	final IntegerProperty x;
	final IntegerProperty y;
	final DoubleProperty padding;
	final ObjectProperty<SlidePaint> background;
	final ObjectProperty<SlideStroke> border;
	
	public JavaFxSlideComponent(T component, PraisenterContext context) {
		this.context = context;
		this.component = component;
		this.region = null;// FIXME build it using the converter
		
		
		this.width.addListener((obs, ov, nv) -> {
			
		});
		
	}
	
	public void setWidth(int width) {
		this.width.set(width);
		this.component.setWidth(width);
	}
}
