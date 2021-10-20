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

import java.util.ArrayList;
import java.util.List;

import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientStop;
import org.praisenter.data.slide.graphics.SlideGradientType;
import org.praisenter.data.slide.graphics.SlidePaint;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 * Class with a collection of paint (color, gradient, etc) related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PaintConverter {
	/** private constructor */
	private PaintConverter() {}

	/**
	 * Converts the given {@link SlideColor} into a Color.
	 * @param color the color
	 * @return Color
	 */
	public static Color toJavaFX(SlideColor color) {
		if (color == null) return Color.BLACK;
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	/**
	 * Converts the given Color into a {@link SlideColor}.
	 * @param color the color
	 * @return {@link SlideColor}
	 */
	public static SlideColor fromJavaFX(Color color) {
		if (color == null) return new SlideColor();
		return new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
	}
	
	/**
	 * Converts the given {@link SlidePaint} into a Paint.
	 * @param paint the paint
	 * @return Paint
	 */
	public static Paint toJavaFX(SlidePaint paint) {
		Paint bgPaint = null;
		if (paint instanceof SlideColor) {
			bgPaint = toJavaFX((SlideColor)paint);
		} else if (paint instanceof SlideGradient) {
			bgPaint = toJavaFX((SlideGradient)paint);
		}
		return bgPaint;
	}
	
	/**
	 * Converts the given {@link SlideGradientCycleType} into a CycleMethod.
	 * @param cycle the cycle type
	 * @return CycleMethod
	 */
	public static CycleMethod toJavaFX(SlideGradientCycleType cycle) {
		if (cycle == null) 
			return CycleMethod.NO_CYCLE;
		
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	/**
	 * Converts the given {@link SlideGradientStop} into a Stop.
	 * @param stop the stop
	 * @return Stop
	 */
	private static Stop toJavaFX(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), toJavaFX(stop.getColor()));
	}
	
	/**
	 * Converts the given {@link SlideGradientStop}s into a list of Stops.
	 * @param stops the stops
	 * @return List&lt;Stop&gt;
	 */
	private static List<Stop> toJavaFX(List<SlideGradientStop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(toJavaFX(s));
		}
		return stps;
	}
	
	/**
	 * Converts the given {@link SlideLinearGradient} into a LinearGradient.
	 * @param gradient the gradient
	 * @return LinearGradient
	 */
	public static Paint toJavaFX(SlideGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		if (gradient.getType() == SlideGradientType.RADIAL) {
			double x1 = gradient.getStartX();
			double x2 = gradient.getEndX();
			double y1 = gradient.getStartY();
			double y2 = gradient.getEndY();
			double dx = x2 - x1;
			double dy = y2 - y1;
			double r = Math.sqrt(dx * dx + dy * dy);
			return new RadialGradient(
					0.0, 
					0.0, 
					gradient.getStartX(), 
					gradient.getStartY(), 
					r,
					true, 
					toJavaFX(gradient.getCycleType()), 
					toJavaFX(gradient.getStops()));
		} else {
			return new LinearGradient(
					gradient.getStartX(), 
					gradient.getStartY(), 
					gradient.getEndX(), 
					gradient.getEndY(), 
					true, 
					toJavaFX(gradient.getCycleType()), 
					toJavaFX(gradient.getStops()));
		}
	}
}
