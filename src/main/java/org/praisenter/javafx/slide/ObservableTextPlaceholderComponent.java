package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservableTextPlaceholderComponent extends ObservableTextComponent<TextPlaceholderComponent> implements SlideRegion, SlideComponent, TextComponent {
	
	final ObjectProperty<PlaceholderType> placeholderType = new SimpleObjectProperty<PlaceholderType>();
	final ObservableList<PlaceholderVariant> variants = FXCollections.observableArrayList();
	
	public ObservableTextPlaceholderComponent(TextPlaceholderComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.placeholderType.set(component.getType());
		this.variants.addAll(component.getVariants());
		
		// TODO variants
		this.placeholderType.addListener((obs, ov, nv) -> { 
			this.region.setType(nv); 
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
	public ObservableTextPlaceholderComponent copy() {
		throw new UnsupportedOperationException();
	}
	
	// placeholder type
	
	public PlaceholderType getPlaceholderType() {
		return this.placeholderType.get();
	}
	
	public void setPlaceholderType(PlaceholderType type) {
		this.placeholderType.set(type);
	}
	
	public ObjectProperty<PlaceholderType> placeholderTypeProperty() {
		return this.placeholderType;
	}
	
	// variants
	
	public ObservableList<PlaceholderVariant> getVariants() {
		return this.variants;
	}

	@Override
	public String getText() {
		// TODO translate
		if (this.placeholderType.get() == PlaceholderType.TITLE) {
			return "This is where the title will go";
		} else {
			return "This is where text will go";
		}
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}
}
