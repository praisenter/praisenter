package org.praisenter.javafx.slide;

import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public final class ObservableTextPlaceholderComponent extends ObservableTextComponent<TextPlaceholderComponent> {
	
	final ObjectProperty<TextType> placeholderType = new SimpleObjectProperty<TextType>();
	final ObservableSet<TextVariant> placeholderVariants = FXCollections.observableSet();
	
	public ObservableTextPlaceholderComponent(TextPlaceholderComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.placeholderType.set(component.getPlaceholderType());
		this.placeholderVariants.addAll(component.getPlaceholderVariants());
		
		if (this.mode == SlideMode.EDIT ||
			this.mode == SlideMode.PREVIEW ||
			this.mode == SlideMode.SNAPSHOT) {
			this.text.set(this.getText());
		}
		
		// TODO this will need to be replaced with the appropriate text at display time
		this.placeholderType.addListener((obs, ov, nv) -> { 
			this.region.setPlaceholderType(nv); 
			this.text.set(this.getText());
		});
		
		this.placeholderVariants.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				text.set(getText());
			}
		});
		
		this.placeholderVariants.addListener(new SetChangeListener<TextVariant>() {
			@Override
			public void onChanged(javafx.collections.SetChangeListener.Change<? extends TextVariant> change) {
				if (change.wasAdded()) {
					region.getPlaceholderVariants().add(change.getElementAdded());
				}
				if (change.wasRemoved()) {
					region.getPlaceholderVariants().remove(change.getElementRemoved());
				}
			}
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
	
	// variants
	
	public ObservableSet<TextVariant> getPlaceholderVariants() {
		return this.placeholderVariants;
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (TextVariant variant : this.placeholderVariants) {
			if (sb.length() > 0) sb.append("\n\n");
			sb.append(this.getTextFor(this.placeholderType.get(), variant));
		}
		return sb.toString();
	}
	
	private String getTextFor(TextType type, TextVariant variant) {
		// TODO translate
		if (type == TextType.TITLE) {
			if (variant == TextVariant.PRIMARY) {
				return "The Primary Title";
			} else {
				return "The Secondary Title";
			}
		} else {
			if (variant == TextVariant.PRIMARY) {
				return "The primary text.";
			} else {
				return "The secondary text.";
			}
		}
	}
}
