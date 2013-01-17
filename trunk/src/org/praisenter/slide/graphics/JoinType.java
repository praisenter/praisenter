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
package org.praisenter.slide.graphics;

import java.awt.BasicStroke;

/**
 * Enumeration of the supported join types.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public enum JoinType {
	/** The default bevel join type */
	BEVEL(BasicStroke.JOIN_BEVEL),
	
	/** The miter join type */
	MITER(BasicStroke.JOIN_MITER),
	
	/** The round join type */
	ROUND(BasicStroke.JOIN_ROUND);
	
	/** The Java2D stroke value */
	private final int strokeValue;
	
	/**
	 * Minimal constructor.
	 * @param strokeValue the Java2D stroke value
	 */
	private JoinType(int strokeValue) {
		this.strokeValue = strokeValue;
	}

	/**
	 * Returns the Java2D stroke value.
	 * @return int
	 */
	public int getStrokeValue() {
		return this.strokeValue;
	}
	
	/**
	 * Returns the enum for the given Java2D stroke value.
	 * @param value the Java2D stroke value
	 * @return {@link JoinType}
	 */
	public static JoinType getJoinType(int value) {
		if (value == BasicStroke.JOIN_MITER) {
			return JoinType.MITER;
		} else if (value == BasicStroke.JOIN_ROUND) {
			return JoinType.ROUND;
		} else {
			return JoinType.BEVEL;
		}
	}
}
