package org.praisenter.javafx.slide;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextComponent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableDateTimeComponent extends ObservableTextComponent<DateTimeComponent> implements SlideRegion, SlideComponent, TextComponent {

	final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			// TODO update text node
			SimpleDateFormat f = format.get();
			if (f == null) {
				textNode.setText(new Date().toString());
			} else {
				textNode.setText(f.format(new Date()));
			}
		}
	};
	
	final ObjectProperty<SimpleDateFormat> format = new SimpleObjectProperty<SimpleDateFormat>();
	
	public ObservableDateTimeComponent(DateTimeComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.format.set(component.getFormat());
		
		// listen for changes
		this.format.addListener((obs, ov, nv) -> { 
			this.region.setFormat(nv); 
			timer.handle(0);
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
	
	@Override
	public TextComponent copy() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getText() {
		return this.region.getText();
	}
	
	@Override
	public void setText(String text) {
		this.region.setText(text);
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
