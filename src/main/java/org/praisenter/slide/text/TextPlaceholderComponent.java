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

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

/**
 * Represents a text component whose text will be supplied from an external source.
 * <p>
 * The external source can place text into the component based on the give types applied
 * to the placeholder.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "textPlaceholderComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class TextPlaceholderComponent extends BasicTextComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The placeholder type */
	@XmlElement(name = "placeholderType", required = false)
	PlaceholderType placeholderType;
	
	/** The placeholder variants */
	@XmlElement(name = "variant", required = false)
	@XmlElementWrapper(name = "placeholderVariants", required = false)
	final Set<PlaceholderVariant> placeholderVariants;

	/**
	 * Default constructor.
	 */
	public TextPlaceholderComponent() {
		this.placeholderType = PlaceholderType.TEXT;
		this.placeholderVariants = new TreeSet<PlaceholderVariant>();
		this.placeholderVariants.add(PlaceholderVariant.PRIMARY);
	}

	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public TextPlaceholderComponent(TextPlaceholderComponent other, boolean exact) {
		super(other, exact);
		this.placeholderType = other.placeholderType;
		this.placeholderVariants = new TreeSet<PlaceholderVariant>(other.placeholderVariants);
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

	/**
	 * Returns the placeholder type.
	 * @return {@link PlaceholderType}
	 */
	public PlaceholderType getPlaceholderType() {
		return this.placeholderType;
	}

	/**
	 * Sets the type of this placeholder.
	 * @param type the type
	 */
	public void setPlaceholderType(PlaceholderType type) {
		if (type == null) {
			type = PlaceholderType.TEXT;
		}
		this.placeholderType = type;
	}

	/**
	 * Returns this placeholder's variants.
	 * @return Set&lt;{@link PlaceholderVariant}&gt;
	 */
	public Set<PlaceholderVariant> getPlaceholderVariants() {
		return this.placeholderVariants;
	}
}
