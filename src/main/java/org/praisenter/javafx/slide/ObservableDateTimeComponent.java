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
			setText(region.getText());
		}
	};
	
	private final ObjectProperty<SimpleDateFormat> dateTimeFormat = new SimpleObjectProperty<SimpleDateFormat>();
	
	public ObservableDateTimeComponent(DateTimeComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.dateTimeFormat.set(component.getDateTimeFormat());
		
		// listen for changes
		this.dateTimeFormat.addListener((obs, ov, nv) -> { 
			this.region.setDateTimeFormat(nv);
			String text = this.region.getText();
			this.setText(text);
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
	
	public SimpleDateFormat getDateTimeFormat() {
		return this.dateTimeFormat.get();
	}
	
	public void setDateTimeFormat(SimpleDateFormat format) {
		this.dateTimeFormat.set(format);
	}
	
	public ObjectProperty<SimpleDateFormat> dateTimeFormatProperty() {
		return this.dateTimeFormat;
	}
}
