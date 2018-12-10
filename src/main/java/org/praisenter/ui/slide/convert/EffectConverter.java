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
package org.praisenter.ui.slide.convert;



import org.praisenter.data.slide.effects.ShadowType;
import org.praisenter.data.slide.effects.SlideColorAdjust;
import org.praisenter.data.slide.effects.SlideShadow;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;

/**
 * Class with a collection of effect related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class EffectConverter {
	/** private constructor */
	private EffectConverter() {}
	
	/**
	 * Returns an Effect for the given {@link SlideShadow}.
	 * @param shadow the shadow or glow
	 * @return Effect
	 */
	public static Effect toJavaFX(SlideShadow shadow) {
		if (shadow == null) {
			return null;
		}
		
		if (shadow.getType() == ShadowType.INNER) {
			InnerShadow s = new InnerShadow(
					shadow.getRadius(),
					shadow.getOffsetX(),
					shadow.getOffsetY(),
					PaintConverter.toJavaFX(shadow.getColor()));
			s.setChoke(shadow.getSpread());
			return s;
		} else {
			DropShadow s = new DropShadow(
					shadow.getRadius(),
					shadow.getOffsetX(),
					shadow.getOffsetY(),
					PaintConverter.toJavaFX(shadow.getColor()));
			s.setSpread(shadow.getSpread());
			return s;
		}
	}
	
	/**
	 * Returns an Effect for the given {@link SlideColorAdjust}.
	 * @param colorAdjust the color adjustment
	 * @return Effect
	 */
	public static Effect toJavaFX(SlideColorAdjust colorAdjust) {
		if (colorAdjust == null) {
			return null;
		}
		return new ColorAdjust(
				colorAdjust.getHue(),
				colorAdjust.getSaturation(),
				colorAdjust.getBrightness(),
				colorAdjust.getContrast());
	}
}
