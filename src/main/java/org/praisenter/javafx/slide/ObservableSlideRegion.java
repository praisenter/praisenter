package org.praisenter.javafx.slide;

import java.util.UUID;

import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class ObservableSlideRegion<T extends SlideRegion> implements SlideRegion {
	final T region;
	
	final IntegerProperty x = new SimpleIntegerProperty();
	final IntegerProperty y = new SimpleIntegerProperty();
	final IntegerProperty width = new SimpleIntegerProperty();
	final IntegerProperty height = new SimpleIntegerProperty();
	
	final ObjectProperty<SlidePaint> background = new SimpleObjectProperty<SlidePaint>();
	final ObjectProperty<SlideStroke> border = new SimpleObjectProperty<SlideStroke>();
	
	public ObservableSlideRegion(T region) {
		this.region = region;
		
		// set initial values
		this.x.set(region.getX());
		this.y.set(region.getY());
		this.width.set(region.getWidth());
		this.height.set(region.getHeight());
		this.background.set(region.getBackground());
		this.border.set(region.getBorder());
		
		// listen for changes
		this.x.addListener((obs, ov, nv) -> { this.region.setX(nv.intValue()); });
		this.y.addListener((obs, ov, nv) -> { this.region.setY(nv.intValue()); });
		this.width.addListener((obs, ov, nv) -> { this.region.setWidth(nv.intValue()); });
		this.height.addListener((obs, ov, nv) -> { this.region.setHeight(nv.intValue()); });
		this.background.addListener((obs, ov, nv) -> { this.region.setBackground(nv); });
		this.border.addListener((obs, ov, nv) -> { this.region.setBorder(nv); });
	}
	
	@Override
	public UUID getId() {
		return this.region.getId();
	}

	@Override
	public boolean isBackgroundTransitionRequired(SlideRegion region) {
		return this.region.isBackgroundTransitionRequired(region);
	}
	
	@Override
	public void adjust(double pw, double ph) {
		this.region.adjust(pw, ph);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
	}
	
	@Override
	public Rectangle resize(int dw, int dh) {
		Rectangle r = this.region.resize(dw, dh);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
		return r;
	}
	
	@Override
	public void translate(int dx, int dy) {
		this.region.translate(dx, dy);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
	}

	// x
	
	@Override
	public int getX() {
		return this.x.get();
	}
	
	@Override
	public void setX(int x) {
		this.x.set(x);
	}
	
	public IntegerProperty xProperty() {
		return this.x;
	}
	
	// y
	
	@Override
	public int getY() {
		return this.y.get();
	}
	
	@Override
	public void setY(int y) {
		this.y.set(y);
	}
	
	public IntegerProperty yProperty() {
		return this.y;
	}
	
	// width

	@Override
	public int getWidth() {
		return this.width.get();
	}
	
	@Override
	public void setWidth(int width) {
		this.width.set(width);
	}
	
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	// height

	@Override
	public int getHeight() {
		return this.height.get();
	}
	
	@Override
	public void setHeight(int height) {
		this.height.set(height);
	}
	
	public IntegerProperty heightProperty() {
		return this.height;
	}
	
	// background
	
	@Override
	public void setBackground(SlidePaint background) {
		this.background.set(background);
	}
	
	@Override
	public SlidePaint getBackground() {
		return this.background.get();
	}
	
	public ObjectProperty<SlidePaint> backgroundProperty() {
		return this.background;
	}
	
	// border
	
	@Override
	public void setBorder(SlideStroke border) {
		this.border.set(border);
	}
	
	@Override
	public SlideStroke getBorder() {
		return this.border.get();
	}
	
	public ObjectProperty<SlideStroke> borderProperty() {
		return this.border;
	}
}
