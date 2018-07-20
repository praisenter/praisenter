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
package org.praisenter.javafx.slide.editor;

import org.praisenter.ui.translations.Translations;

/**
 * Represents an animation option (animation or easing) primary used to map a class
 * to an image to be placed in a grided region.
 * <p>
 * Uses the class name to look up the image for the option.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class AnimationOption implements Comparable<AnimationOption> {
	/** The option's class */
	final Class<?> type;
	
	/** The option's sort order */
	final int order;
	
	/** The option's name */
	final String name;
	
	/**
	 * Full constructor.
	 * @param type the option's class
	 * @param order the option's sort order
	 */
	public AnimationOption(Class<?> type, int order) {
		this.type = type;
		this.order = order;
		this.name = Translations.get(type.getName());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AnimationOption o) {
		return this.order - o.order;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.type.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AnimationOption) {
			AnimationOption o = (AnimationOption)obj;
			return o.type == this.type;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns the class for this option.
	 * @return Class&lt;?&gt;
	 */
	public Class<?> getType() {
		return this.type;
	}
	
	/**
	 * Returns the sort order of this option.
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Returns the name of this option.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
