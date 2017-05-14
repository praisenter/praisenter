package org.praisenter.javafx.slide;

import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableTextPlaceholderComponent extends ObservableTextComponent<TextPlaceholderComponent> {
	
	private final ObjectProperty<TextType> placeholderType = new SimpleObjectProperty<TextType>();
	private final ObjectProperty<TextVariant> placeholderVariant = new SimpleObjectProperty<TextVariant>();
	
	public ObservableTextPlaceholderComponent(TextPlaceholderComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.placeholderType.set(component.getPlaceholderType());
		this.placeholderVariant.set(component.getPlaceholderVariant());
		
		if (this.mode == SlideMode.EDIT ||
			this.mode == SlideMode.PREVIEW ||
			this.mode == SlideMode.PREVIEW_NO_AUDIO ||
			this.mode == SlideMode.SNAPSHOT) {
			this.setText(this.getText());
		}
		
		this.placeholderType.addListener((obs, ov, nv) -> { 
			this.region.setPlaceholderType(nv); 
			this.setText(this.getText());
		});
		
		this.placeholderVariant.addListener((obs, ov, nv) -> {
			this.region.setPlaceholderVariant(nv); 
			this.setText(this.getText());
		});

		this.build();
	}
	
	// placeholder type
	
	public TextType getPlaceholderType() {
		return this.placeholderType.get();
	}
	
	public void setPlaceholderType(TextType type) {
		this.placeholderType.set(type);
	}
	
	public ObjectProperty<TextType> placeholderTypeProperty() {
		return this.placeholderType;
	}
	
	// placeholder variant
	
	public TextVariant getPlaceholderVariant() {
		return this.placeholderVariant.get();
	}
	
	public void setPlaceholderVariant(TextVariant variant) {
		this.placeholderVariant.set(variant);
	}
	
	public ObjectProperty<TextVariant> placeholderVariantProperty() {
		return this.placeholderVariant;
	}
	
	public String getText() {
		String text = this.region.getText();
		if (this.mode == SlideMode.EDIT && (text == null || text.length() == 0)) {
			text = this.getTextFor(this.placeholderType.get(), this.placeholderVariant.get());
		}
		return text;
	}
	
	private String getTextFor(TextType type, TextVariant variant) {
		if (type == TextType.TITLE) {
			if (variant == TextVariant.PRIMARY) {
				return Translations.get("slide.placeholder.type.title.primary");
			} else {
				return Translations.get("slide.placeholder.type.title.secondary");
			}
		} else {
			if (variant == TextVariant.PRIMARY) {
				return Translations.get("slide.placeholder.type.text.primary");
			} else {
				return Translations.get("slide.placeholder.type.text.secondary");
			}
		}
	}
}
