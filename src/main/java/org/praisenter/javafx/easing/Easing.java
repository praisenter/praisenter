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
package org.praisenter.javafx.easing;

import javafx.animation.Interpolator;

/**
 * The base class for custom easings.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class Easing extends Interpolator {
	/** The easing type */
	final EasingType type;

	/**
	 * Full constructor.
	 * @param type the easing type
	 */
    public Easing(EasingType type) {
        this.type = type;
    }

    /**
     * Returns the easing type: in, out, or both.
     * @return {@link EasingType}
     */
    public EasingType getType() {
        return this.type;
    }

    /**
     * Returns the id of this easing.
     * @return int
     */
    public abstract int getId();
    
    /**
     * Defines the base curve for the easing.
     * The base curve is then transformed into an easing-in, easing-out easing-both curve.
     * @param v the value between 0 and 1
     * @return double
     */
    protected abstract double baseCurve(final double v);

    /**
     * Alters the function depending on the easing mode.
     * @param v the value between 0 and 1
     * @return double
     */
    @Override
    protected final double curve(final double v) {
        switch (this.type) {
            case IN:
                return baseCurve(v);
            case OUT:
                return 1 - baseCurve(1 - v);
            case BOTH:
                if (v <= 0.5) {
                    return baseCurve(2 * v) / 2;
                } else {
                    return (2 - baseCurve(2 * (1 - v))) / 2;
                }

        }
        return baseCurve(v);
    }
}
