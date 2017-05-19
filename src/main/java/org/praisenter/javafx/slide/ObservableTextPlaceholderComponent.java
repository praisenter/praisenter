/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.slide;

import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an observable {@link TextPlaceholderComponent}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ObservableTextPlaceholderComponent extends ObservableTextComponent<TextPlaceholderComponent> implements Playable {
	/** The placeholder type */
	private final ObjectProperty<TextType> placeholderType = new SimpleObjectProperty<TextType>();
	
	/** The placeholder variant */
	private final ObjectProperty<TextVariant> placeholderVariant = new SimpleObjectProperty<TextVariant>();
	
	/**
	 * Minimal constructor.
	 * @param component the text component
	 * @param context the context
	 * @param mode the slide mode
	 */
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
	
	/**
	 * Returns the placeholder type.
	 * @return {@link TextType}
	 */
	public TextType getPlaceholderType() {
		return this.placeholderType.get();
	}
	
	/**
	 * Sets the placeholder type.
	 * @param type the type
	 */
	public void setPlaceholderType(TextType type) {
		this.placeholderType.set(type);
	}
	
	/**
	 * Returns the placeholder type property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<TextType> placeholderTypeProperty() {
		return this.placeholderType;
	}
	
	// placeholder variant
	
	/**
	 * Returns the placeholder variant.
	 * @return {@link TextVariant}
	 */
	public TextVariant getPlaceholderVariant() {
		return this.placeholderVariant.get();
	}
	
	/**
	 * Sets the placeholder variant.
	 * @param variant the variant
	 */
	public void setPlaceholderVariant(TextVariant variant) {
		this.placeholderVariant.set(variant);
	}
	
	/**
	 * Returns the placeholder variant property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<TextVariant> placeholderVariantProperty() {
		return this.placeholderVariant;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableTextComponent#getText()
	 */
	public String getText() {
		String text = this.region.getText();
		if (this.mode == SlideMode.EDIT && (text == null || text.length() == 0)) {
			text = this.getTextFor(this.placeholderType.get(), this.placeholderVariant.get());
		}
		return text;
	}
	
	/**
	 * Returns the default text for the given TextType and TextVariant.
	 * @param type the type
	 * @param variant the variant
	 * @return String
	 */
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
