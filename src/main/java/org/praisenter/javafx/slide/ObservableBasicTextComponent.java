package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.TextComponent;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableBasicTextComponent<T extends BasicTextComponent> extends ObservableTextComponent<T> implements SlideRegion, SlideComponent, TextComponent {
	
	final StringProperty text = new SimpleStringProperty();
	
	public ObservableBasicTextComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.text.set(component.getText());
		
		// listen for changes
		this.text.addListener((obs, ov, nv) -> { 
			this.region.setText(nv); 
			updateText();
		});
		
		this.build();
	}
	
	void build() {
		super.build();
		
		updateText();
	}
	
	private void updateText() {
		this.textNode.setText(this.text.get());
	}
	
	@Override
	public ObservableBasicTextComponent copy() {
		throw new UnsupportedOperationException();
	}
	
	// text
	
	@Override
	public String getText() {
		return this.text.get();
	}
	
	@Override
	public void setText(String text) {
		this.text.set(text);
	}
	
	public StringProperty textProperty() {
		return this.text;
	}
}
