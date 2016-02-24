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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
	
	// types
	
	/** Type to indicate all text should go here */
	public static final int TYPE_ALL = Integer.MAX_VALUE;
	
	/** Type to indicate only the primary text should go here */
	public static final int TYPE_PRIMARY = 1;
	
	/** Type to indicate only the secondary text should go here */
	public static final int TYPE_SECONDARY = 2;
	
	/** Type to indicate only the tertiary text should go here */
	public static final int TYPE_TERTIARY = 4;
	
	/** Type to indicate only the quaternary text should go here */
	public static final int TYPE_QUATERNARY = 8;
	
	/** Type to indicate only the quinary text should go here */
	public static final int TYPE_QUINARY = 16;
	
	/** Type to indicate only the senary text should go here */
	public static final int TYPE_SENARY = 32;
	
	/**
	 * Returns the type given an index.
	 * <p>
	 * 0 => 1 (TYPE_PRIMARY)<br>
	 * 1 => 2 (TYPE_SECONDARY)<br>
	 * 2 => 4 (TYPE_TERTIARY)<br>
	 * 3 => 8 (TYPE_QUATERNARY)<br>
	 * ...
	 * @param index the index
	 * @return the type
	 */
	public static final int getTypeByIndex(int index) {
		return (int)Math.pow(2, index);
	}
	
	// fields
	
	/** The placeholder type */
	@XmlAttribute(name = "type", required = false)
	int type;

	/**
	 * Creates a new placeholder for all text.
	 */
	public TextPlaceholderComponent() {
		// by default, all
		this.type = TYPE_ALL;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public TextPlaceholderComponent copy() {
		TextPlaceholderComponent comp = new TextPlaceholderComponent();
		this.copy(comp);
		comp.setType(this.type);
		return comp;
	}
	
	/**
	 * Returns true if this placeholder should show the given type.
	 * <p>
	 * For example: 
	 * <code>isType(TextPlaceholderComponent.TYPE_TERTIARY)</code>
	 * @param type the type
	 * @return boolean
	 */
	public boolean isType(int type) {
		return (this.type & type) == type;
	}
	
	/**
	 * Adds the given type to this placeholder's current types.
	 * @param type the new type
	 */
	public void addType(int type) {
		this.type = this.type | type;
	}
	
	/**
	 * Returns the condensed types.
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Explicitly sets the type to the given type.
	 * @param type the type
	 */
	public void setType(int type) {
		this.type = type;
	}
}
