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
package org.praisenter.data.slide.ease;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The base class for immutable custom easings.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
//@JsonSubTypes({
//	@Type(value = Back.class, name = "back"),
//	@Type(value = Bounce.class, name = "bounce"),
//	@Type(value = Circular.class, name = "circular"),
//	@Type(value = Cubic.class, name = "cubic"),
//	@Type(value = Elastic.class, name = "elastic"),
//	@Type(value = Exponential.class, name = "exponential"),
//	@Type(value = Linear.class, name = "linear"),
//	@Type(value = Quadratic.class, name = "quadratic"),
//	@Type(value = Quartic.class, name = "quartic"),
//	@Type(value = Quintic.class, name = "quintic"),
//	@Type(value = Sinusoidal.class, name = "sinusoidal")
//})
public abstract class Easing implements ReadonlyEasing, Copyable {
	protected final ObjectProperty<EasingType> easingType;

    public Easing(EasingType type) {
        if (type == null) {
        	type = EasingType.IN;
        }
        this.easingType = new SimpleObjectProperty<>(type);
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName())
		  .append("[")
		  .append(this.easingType.get())
		  .append("]");
		return sb.toString();
	}
	
    /**
     * Makes a copy of this easing.
     * @return Easing
     */
    public abstract Easing copy();
    
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
    public final double curve(final double v) {
        switch (this.easingType.get()) {
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

    @Override
    @JsonProperty
    public EasingType getEasingType() {
        return this.easingType.get();
    }
    
    @JsonProperty
    public void setEasingType(EasingType type) {
    	this.easingType.set(type);
    }
    
    @Override
    public ObjectProperty<EasingType> easingTypeProperty() {
    	return this.easingType;
    }
}
