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
package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Represents a transition from one image to another.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public abstract class Transition {
	/**
	 * The transition type.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum Type {
		/** Transition in */
		IN,
		
		/** Transition out */
		OUT
	}
	
	/** The transition type */
	protected Type type;
	
	/** The transition name */
	protected String name;
	
	/**
	 * Minimal constructor.
	 * @param name the transition name
	 * @param type the transition type
	 */
	public Transition(String name, Type type) {
		if (type == null || name == null) throw new NullPointerException();
		this.name = name;
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Transition) {
			Transition other = (Transition)obj;
			if (this.getTransitionId() == other.getTransitionId()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renders this transition to the given graphics given the beginning image and the
	 * ending image.
	 * @param g2d the graphics to render to
	 * @param image0 the beginning image
	 * @param image1 the ending image
	 * @param pc the percentage completed; this can be less than zero or greater than 1 depending on the easing
	 */
	public abstract void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc);
	
	/**
	 * Returns a unique transition id for a transition sub class.
	 * @return int
	 */
	public abstract int getTransitionId();
	
	/**
	 * Returns this transition name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns this transition type.
	 * @return {@link Type}
	 */
	public Type getType() {
		return this.type;
	}
}
