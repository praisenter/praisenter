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
package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//FEATURE add more shape types (Star, Rect, etc)

/**
 * An animation where a shape is used to reveal or hide.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "shaped")
@XmlAccessorType(XmlAccessType.NONE)
public final class Shaped extends SlideAnimation {
	/** The shape */
	@XmlElement(name = "shapeType", required = false)
	ShapeType shapeType;
	
	/** The operation */
	@XmlElement(name = "operation", required = false)
	Operation operation;
	
	/**
	 * Default constructor.
	 */
	public Shaped() {
		this.shapeType = ShapeType.CIRCLE;
		this.operation = Operation.COLLAPSE;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.SlideAnimation#copy(java.util.UUID)
	 */
	@Override
	public Shaped copy(UUID id) {
		Shaped animation = new Shaped();
		copy(animation, id);
		animation.shapeType = this.shapeType;
		animation.operation = this.operation;
		return animation;
	}
	
	/**
	 * Returns the shape type.
	 * @return {@link ShapeType}
	 */
	public ShapeType getShapeType() {
		return this.shapeType;
	}
	
	/**
	 * Sets the shape type.
	 * @param shapeType the shape type
	 */
	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
	}
	
	/**
	 * Returns the operation.
	 * @return {@link Operation}
	 */
	public Operation getOperation() {
		return this.operation;
	}
	
	/**
	 * Sets the operation.
	 * @param operation the operation
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
}
