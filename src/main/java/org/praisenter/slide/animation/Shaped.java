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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.easing.Easing;

//FEATURE (L) Add more shape types (Star, Rect, etc)

/**
 * An animation where a shape is used to reveal or hide.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "shaped")
@XmlAccessorType(XmlAccessType.NONE)
public final class Shaped extends Animation {
	/** The default shape type */
	public static final ShapeType DEFAULT_SHAPE_TYPE = ShapeType.CIRCLE;
	
	/** The default operation */
	public static final Operation DEFAULT_OPERATION = Operation.EXPAND;
	
	/** The shape */
	@XmlElement(name = "shapeType", required = false)
	final ShapeType shapeType;
	
	/** The operation */
	@XmlElement(name = "operation", required = false)
	final Operation operation;

	/**
	 * Default constructor for JAXB.
	 */
	Shaped() {
		super(AnimationType.IN);
		this.shapeType = DEFAULT_SHAPE_TYPE;
		this.operation = DEFAULT_OPERATION;
	}
	
	/**
	 * Full constructor.
	 * @param type the animation type
	 * @param duration the duration (in milliseconds)
	 * @param delay the delay (in milliseconds)
	 * @param repeatCount the repeat count; 1 or higher
	 * @param autoReverse true if auto-reverse should occur when repeat count is greater than 1
	 * @param easing the easing
	 * @param shapeType the shape
	 * @param operation the operation
	 */
	public Shaped(AnimationType type,
			long duration,
			long delay,
			int repeatCount,
			boolean autoReverse,
			Easing easing,
			ShapeType shapeType,
			Operation operation) {
		super(type, duration, delay, repeatCount, autoReverse, easing);
		this.shapeType = shapeType == null ? DEFAULT_SHAPE_TYPE : shapeType;
		this.operation = operation == null ? DEFAULT_OPERATION : operation;
	}
	
	/**
	 * Copy constructor.
	 * @param other the animation to copy
	 */
	public Shaped(Shaped other) {
		this(other.type,
			 other.duration,
			 other.delay,
			 other.repeatCount,
			 other.autoReverse,
			 other.easing,
			 other.shapeType,
			 other.operation);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.Animation#copy()
	 */
	@Override
	public Shaped copy() {
		return new Shaped(this);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.Animation#copy(org.praisenter.slide.animation.AnimationType)
	 */
	@Override
	public Shaped copy(AnimationType type) {
		return new Shaped(
				type,
				this.duration,
				this.delay,
				this.repeatCount,
				this.autoReverse,
				this.easing,
				this.shapeType,
				this.operation);
	}
	
	/**
	 * Returns the shape type.
	 * @return {@link ShapeType}
	 */
	public ShapeType getShapeType() {
		return this.shapeType;
	}

	/**
	 * Returns the operation.
	 * @return {@link Operation}
	 */
	public Operation getOperation() {
		return this.operation;
	}
}
