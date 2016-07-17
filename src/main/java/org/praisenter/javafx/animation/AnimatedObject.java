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
package org.praisenter.javafx.animation;

import java.text.Collator;
import java.util.UUID;

/**
 * An object to represent an animatable object.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class AnimatedObject implements Comparable<AnimatedObject> {
	/** The current locale collator */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** The object id (slide/component) */
	private final UUID objectId;
	
	/** The animated object type  */
	private final AnimatedObjectType type;
	
	/** The name of the component */
	private final String name;
	
	/**
	 * Full constructor.
	 * @param objectId the object id
	 * @param type the object type
	 * @param name the object name
	 */
	public AnimatedObject(UUID objectId, AnimatedObjectType type, String name) {
		this.objectId = objectId;
		this.type = type;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AnimatedObject o) {
		return COLLATOR.compare(this.name, o.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.objectId.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AnimatedObject) {
			AnimatedObject o = (AnimatedObject)obj;
			if (this.objectId.equals(o.objectId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the object id.
	 * @return UUID
	 */
	public UUID getObjectId() {
		return this.objectId;
	}

	/**
	 * Returns the animated object type.
	 * @return {@link AnimatedObjectType}
	 */
	public AnimatedObjectType getType() {
		return this.type;
	}

	/**
	 * Returns the name of the object.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
