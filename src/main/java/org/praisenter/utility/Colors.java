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
package org.praisenter.utility;

/**
 * Utility class for color manipulation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Colors {
	/** hidden constructor. */
	private Colors() {}
	
	// if we need a better difference calculation see these:
	// https://stackoverflow.com/questions/9018016/how-to-compare-two-colors-for-similarity-difference
	// https://www.compuphase.com/cmetric.htm
	
	/**
	 * Returns the euclidian distance between the two colors.
	 * @param r1 the red component of the first color
	 * @param g1 the green component of the first color
	 * @param b1 the blue component of the first color
	 * @param a1 the alpha component of the first color
	 * @param r2 the red component of the second color
	 * @param g2 the green component of the second color
	 * @param b2 the blue component of the second color
	 * @param a2 the alpha component of the second color
	 * @return double
	 */
	public static final double distanceSquared(double r1, double g1, double b1, double a1, double r2, double g2, double b2, double a2) {
		double r = r2 - r1;
		double g = g2 - g1;
		double b = b2 - b1;
		double a = a2 - a1;
		return r * r + g * g + b * b + a * a;
	}
}
