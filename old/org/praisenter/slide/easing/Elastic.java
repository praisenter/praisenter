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
package org.praisenter.slide.easing;

/**
 * Represents an elastic easing.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Elastic extends Easing {
	/** The overshoot factor */
	private static final double s = 1;
	
	/** The number of oscillations */
	private static final double o = 3;
	
	/**
	 * Default constructor for JAXB.
	 */
	Elastic() {
		super(EasingType.IN);
	}
	
	/**
	 * Minimal constructor.
	 * @param type the easing type
	 */
	public Elastic(EasingType type) {
		super(type);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.easing.Easing#baseCurve(double)
	 */
	@Override
	protected double baseCurve(double v) {
		if (v == 0) {
            return 0;
        }
        if (v == 1) {
            return 1;
        }
        double p = 1.0 / o;
        double a = Elastic.s;
        double s;
        if (a < Math.abs(1)) {
            a = 1;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(1 / a);
        }
        return -(a * Math.pow(2, 10 * (v -= 1)) * Math.sin((v - s) * (2 * Math.PI) / p));
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.easing.Easing#copy()
	 */
	@Override
	public Elastic copy() {
		return new Elastic(this.type);
	}
}
