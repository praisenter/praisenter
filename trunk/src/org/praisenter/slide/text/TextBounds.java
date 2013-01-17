/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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

/**
 * Represents the bounds of some rendered text.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class TextBounds {
	/** The text width */
	protected float width;
	
	/** The text height */
	protected float height;
	
	/**
	 * Default constructor.
	 */
	public TextBounds() {}
	
	/**
	 * Full constructor.
	 * @param width the width
	 * @param height the height
	 */
	public TextBounds(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Returns the text width.
	 * @return float
	 */
	public float getWidth() {
		return this.width;
	}
	
	/**
	 * Sets the text width.
	 * @param width the width
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	
	/**
	 * Returns the text height.
	 * @return float
	 */
	public float getHeight() {
		return this.height;
	}
	
	/**
	 * Sets the text height.
	 * @param height the height
	 */
	public void setHeight(float height) {
		this.height = height;
	}
}
