package org.praisenter.javafx.slide;

import java.text.SimpleDateFormat;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableDateTimeComponent extends ObservableTextComponent<DateTimeComponent> implements SlideRegion, SlideComponent, TextComponent {

	final ObjectProperty<SimpleDateFormat> format = new SimpleObjectProperty<SimpleDateFormat>();
	
	public ObservableDateTimeComponent(DateTimeComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.format.set(component.getFormat());
		
		// listen for changes
		this.format.addListener((obs, ov, nv) -> { this.region.setFormat(nv); });
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
