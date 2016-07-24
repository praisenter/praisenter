package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ObservableTextPlaceholderComponent extends ObservableTextComponent<TextPlaceholderComponent> {
	
	final ObjectProperty<PlaceholderType> placeholderType = new SimpleObjectProperty<PlaceholderType>();
	final ObservableList<PlaceholderVariant> variants = FXCollections.observableArrayList();
	
	public ObservableTextPlaceholderComponent(TextPlaceholderComponent component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.placeholderType.set(component.getType());
		this.variants.addAll(component.getVariants());
		
		if (this.mode == SlideMode.EDIT ||
			this.mode == SlideMode.PREVIEW ||
			this.mode == SlideMode.SNAPSHOT) {
			this.text.set(this.getText());
		}
		
		// TODO this will need to be replaced with the appropriate text at display time
		this.placeholderType.addListener((obs, ov, nv) -> { 
			this.region.setType(nv); 
			this.text.set(this.getText());
		});
		
		this.variants.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				text.set(getText());
			}
		});
		
		this.build();
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

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (PlaceholderVariant variant : this.variants) {
			if (sb.length() > 0) sb.append("\n\n");
			sb.append(this.getTextFor(this.placeholderType.get(), variant));
		}
		return sb.toString();
	}
	
	private String getTextFor(PlaceholderType type, PlaceholderVariant variant) {
		// TODO translate
		if (type == PlaceholderType.TITLE) {
			if (variant == PlaceholderVariant.PRIMARY) {
				return "The Primary Title";
			} else {
				return "The Secondary Title";
			}
		} else {
			if (variant == PlaceholderVariant.PRIMARY) {
				return "The primary text.";
			} else {
				return "The secondary text.";
			}
		}
	}
}
