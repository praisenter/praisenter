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
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientStop;
import org.praisenter.data.slide.graphics.SlideLinearGradient;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideRadialGradient;

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
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	/**
	 * Converts the given Color into a {@link SlideColor}.
	 * @param color the color
	 * @return {@link SlideColor}
	 */
	public static SlideColor fromJavaFX(Color color) {
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
		} else if (paint instanceof SlideLinearGradient) {
			bgPaint = toJavaFX((SlideLinearGradient)paint);
		} else if (paint instanceof SlideRadialGradient) {
			bgPaint = toJavaFX((SlideRadialGradient)paint);
		}
		return bgPaint;
	}
	
	/**
	 * Converts the given {@link SlideGradientCycleType} into a CycleMethod.
	 * @param cycle the cycle type
	 * @return CycleMethod
	 */
	public static CycleMethod toJavaFX(SlideGradientCycleType cycle) {
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
	 * Converts the given CycleMethod into a {@link SlideGradientCycleType}.
	 * @param cycle the cycle type
	 * @return {@link SlideGradientCycleType}
	 */
	public static SlideGradientCycleType fromJavaFX(CycleMethod cycle) {
		switch (cycle) {
			case REPEAT:
				return SlideGradientCycleType.REPEAT;
			case REFLECT:
				return SlideGradientCycleType.REFLECT;
			default:
				return SlideGradientCycleType.NONE;
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
	 * Converts the given Stop into a {@link SlideGradientStop}.
	 * @param stop the stop
	 * @return {@link SlideGradientStop}
	 */
	private static SlideGradientStop fromJavaFX(Stop stop) {
		return new SlideGradientStop(stop.getOffset(), fromJavaFX(stop.getColor()));
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
	 * Converts the given Stops into a list of {@link SlideGradientStop}s.
	 * @param stops the stops
	 * @return List&lt;{@link SlideGradientStop}&gt;
	 */
	private static List<SlideGradientStop> fromJavaFX(List<Stop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<SlideGradientStop> stps = new ArrayList<SlideGradientStop>();
		for (Stop s : stops) {
			stps.add(fromJavaFX(s));
		}
		return stps;
	}

	/**
	 * Converts the given {@link SlideLinearGradient} into a LinearGradient.
	 * @param gradient the gradient
	 * @return LinearGradient
	 */
	public static LinearGradient toJavaFX(SlideLinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new LinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(), 
				true, 
				toJavaFX(gradient.getCycleType()), 
				toJavaFX(gradient.getStops()));
	}
	
	/**
	 * Converts the given LinearGradient into a {@link SlideLinearGradient}.
	 * @param gradient the gradient
	 * @return {@link SlideLinearGradient}
	 */
	public static SlideLinearGradient fromJavaFX(LinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		SlideLinearGradient slg = new SlideLinearGradient();
		slg.setStartX(gradient.getStartX());
		slg.setStartY(gradient.getStartY());
		slg.setEndX(gradient.getEndX());
		slg.setEndY(gradient.getEndY());
		slg.setCycleType(fromJavaFX(gradient.getCycleMethod()));
		slg.setStops(fromJavaFX(gradient.getStops()));
		
		return slg;
	}

	/**
	 * Converts the given {@link SlideRadialGradient} into a RadialGradient.
	 * @param gradient the gradient
	 * @return RadialGradient
	 */
	public static RadialGradient toJavaFX(SlideRadialGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new RadialGradient(
				0.0, 
				0.0, 
				gradient.getCenterX(), 
				gradient.getCenterY(), 
				gradient.getRadius(),
				true, 
				toJavaFX(gradient.getCycleType()), 
				toJavaFX(gradient.getStops()));
	}
	
	/**
	 * Converts the given RadialGradient into a {@link SlideRadialGradient}.
	 * @param gradient the gradient
	 * @return {@link SlideRadialGradient}
	 */
	public static SlideRadialGradient fromJavaFX(RadialGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		SlideRadialGradient srg = new SlideRadialGradient();
		srg.setCenterX(gradient.getCenterX());
		srg.setCenterY(gradient.getCenterY());
		srg.setRadius(gradient.getRadius());
		srg.setCycleType(fromJavaFX(gradient.getCycleMethod()));
		srg.setStops(fromJavaFX(gradient.getStops()));
		
		return srg;
	}
}
