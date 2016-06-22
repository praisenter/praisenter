package org.praisenter.javafx.slide;

import java.time.LocalTime;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservableCountdownComponent extends ObservableTextComponent<CountdownComponent> implements SlideRegion, SlideComponent, TextComponent {
	
	final ObjectProperty<LocalTime> target = new SimpleObjectProperty<LocalTime>();
	final StringProperty format = new SimpleStringProperty();
	
	public ObservableCountdownComponent(CountdownComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.target.set(component.getTarget());
		this.format.set(component.getFormat());
		
		this.target.addListener((obs, ov, nv) -> { 
			this.region.setTarget(nv); 
			updateText();
		});
		this.format.addListener((obs, ov, nv) -> { 
			this.region.setFormat(nv); 
			updateText();
		});
				
		this.build();
	}
	
	void build() {
		updateText();
		super.build();
	}
	
	private void updateText() {
		this.textNode.setText(this.getText());
	}
	
	@Override
	public ObservableCountdownComponent copy() {
		throw new UnsupportedOperationException();
	}
	
	// target
	
	public LocalTime getTarget() {
		return this.target.get();
	}
	
	public void setTarget(LocalTime target) {
		this.target.set(target);
	}
	
	public ObjectProperty<LocalTime> targetProperty() {
		return this.target;
	}
	
	// format
	
	public String getFormat() {
		return this.format.get();
	}
	
	public void setFormat(String format) {
		this.format.set(format);
	}
	
	public StringProperty formatProperty() {
		return this.format;
	}

	// overrides
	
	@Override
	public String getText() {
		return this.region.getText();
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}
}
