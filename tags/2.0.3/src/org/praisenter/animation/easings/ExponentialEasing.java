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
package org.praisenter.animation.easings;

import java.io.Serializable;

/**
 * Exponential easing from http://gizma.com/easing/.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExponentialEasing extends AbstractEasing implements Easing, Serializable {
	/** The version id */
	private static final long serialVersionUID = 371096626026968282L;
	
	/** The id for the easing */
	public static final int ID = 70;
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeIn(long, long)
	 */
	@Override
	public double easeIn(long time, long duration) {
		double t = (double)time / (double)duration;
		// 2^(10 * (t - 1))
		return Math.pow(2.0, 10.0 * (t - 1));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeOut(long, long)
	 */
	@Override
	public double easeOut(long time, long duration) {
		double t = (double)time / (double)duration;
		return -Math.pow(2.0, -10.0 * t) + 1.0;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Easing#easeInOut(long, long)
	 */
	@Override
	public double easeInOut(long time, long duration) {
		double t = (double)time / ((double)duration * 0.5);
		if (t < 1.0) {
			return Math.pow(2.0, 10.0 * (t - 1)) * 0.5;
		}
		t -= 1.0;
		return (-Math.pow(2.0, 10.0 * t) + 2.0) * 0.5;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.easing.Easing#getEasingId()
	 */
	@Override
	public int getId() {
		return ID;
	}
}
