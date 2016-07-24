package org.praisenter.javafx.slide;

import java.text.SimpleDateFormat;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.DateTimeComponent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableDateTimeComponent extends ObservableTextComponent<DateTimeComponent> {

	final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			text.set(region.getText());
		}
	};
	
	final ObjectProperty<SimpleDateFormat> format = new SimpleObjectProperty<SimpleDateFormat>();
	
	public ObservableDateTimeComponent(DateTimeComponent component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.format.set(component.getFormat());
		
		// listen for changes
		this.format.addListener((obs, ov, nv) -> { 
			this.region.setFormat(nv); 
			this.text.set(this.region.getText());
		});
		
		this.build();
	}
	
	// playable stuff
	
	public void play() {
		super.play();
		this.timer.start();
	}
	
	public void stop() {
		super.stop();
		this.timer.stop();
	}
	
	// format
	
	public SimpleDateFormat getFormat() {
		return this.format.get();
	}
	
	public void setFormat(SimpleDateFormat format) {
		this.format.set(format);
	}
	
	public ObjectProperty<SimpleDateFormat> formatProperty() {
		return this.format;
	}
}
