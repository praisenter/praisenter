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
package org.praisenter.slide.text;

import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a text component whose text will be supplied from an external source.
 * <p>
 * The external source can place text into the component based on the given types applied
 * to the placeholder.
 * @author William Bittle
 * @version 3.0.0
 */
public class TextPlaceholderComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent {
	// NOTE: the text for a placeholder component should not be serialized
	/** The text */
	String text;

	/** The placeholder type */
	@JsonProperty
	TextType placeholderType;
	
	/** The placeholder variant */
	@JsonProperty
	TextVariant placeholderVariant;

	/**
	 * Default constructor.
	 */
	public TextPlaceholderComponent() {
		this.text = null;
		this.placeholderType = TextType.TEXT;
		this.placeholderVariant = TextVariant.PRIMARY;
	}

	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public TextPlaceholderComponent(TextPlaceholderComponent other, boolean exact) {
		super(other, exact);
		this.text = other.text;
		this.placeholderType = other.placeholderType;
		this.placeholderVariant = other.placeholderVariant;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public TextPlaceholderComponent copy() {
		return this.copy(false);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy(boolean)
	 */
	@Override
	public TextPlaceholderComponent copy(boolean exact) {
		return new TextPlaceholderComponent(this, exact);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getName()
	 */
	@Override
	public String getName() {
		return this.text;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getText()
	 */
	@Override
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns the placeholder type.
	 * @return {@link TextType}
	 */
	public TextType getPlaceholderType() {
		return this.placeholderType;
	}

	/**
	 * Sets the type of this placeholder.
	 * @param type the type
	 */
	public void setPlaceholderType(TextType type) {
		if (type == null) {
			type = TextType.TEXT;
		}
		this.placeholderType = type;
	}

	/**
	 * Returns this placeholder's variant.
	 * @return {@link TextVariant}
	 */
	public TextVariant getPlaceholderVariant() {
		return this.placeholderVariant;
	}
	
	/**
	 * Sets this placeholder's variant.
	 * @param variant the text variant
	 */
	public void setPlaceholderVariant(TextVariant variant) {
		if (variant == null) {
			variant = TextVariant.PRIMARY;
		}
		this.placeholderVariant = variant;
	}
}
