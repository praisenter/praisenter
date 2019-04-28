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

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.slide.graphics.DashPattern;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.graphics.SlideStrokeCap;
import org.praisenter.data.slide.graphics.SlideStrokeJoin;
import org.praisenter.data.slide.graphics.SlideStrokeStyle;
import org.praisenter.data.slide.graphics.SlideStrokeType;

import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * Class with a collection of border related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BorderConverter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** private constructor */
	private BorderConverter() {}
	
	/**
	 * Converts the given {@link SlideStrokeCap} to a StrokeLineCap.
	 * @param cap the stroke cap
	 * @return StrokeLineCap
	 */
	public static StrokeLineCap toJavaFX(SlideStrokeCap cap) {
		if (cap == null) return null;
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
			default:
				return StrokeLineCap.SQUARE;
		}
	}
	
	/**
	 * Converts the given StrokeLineCap to a {@link SlideStrokeCap}.
	 * @param cap the cap type
	 * @return {@link SlideStrokeCap}
	 */
	public static SlideStrokeCap fromJavaFX(StrokeLineCap cap) {
		if (cap == null) return null;
		switch (cap) {
			case BUTT:
				return SlideStrokeCap.BUTT;
			case ROUND:
				return SlideStrokeCap.ROUND;
			default:
				return SlideStrokeCap.SQUARE;
		}
	}
	
	/**
	 * Converts the given {@link SlideStrokeJoin} to a StrokeLineJoin.
	 * @param join the join type
	 * @return StrokeLineJoin
	 */
	public static StrokeLineJoin toJavaFX(SlideStrokeJoin join) {
		if (join == null) return null;
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
			default:
				return StrokeLineJoin.MITER;
		}
	}
	
	/**
	 * Converts the given StrokeLineJoin to a {@link SlideStrokeJoin}.
	 * @param join the join type
	 * @return {@link SlideStrokeJoin}
	 */
	public static SlideStrokeJoin fromJavaFX(StrokeLineJoin join) {
		if (join == null) return null;
		switch (join) {
			case BEVEL:
				return SlideStrokeJoin.BEVEL;
			case ROUND:
				return SlideStrokeJoin.ROUND;
			default:
				return SlideStrokeJoin.MITER;
		}
	}
	
	/**
	 * Converts the given {@link SlideStrokeType} to a StrokeType.
	 * @param type the stroke type
	 * @return StrokeType
	 */
	public static StrokeType toJavaFX(SlideStrokeType type) {
		if (type == null) return null;
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
			default:
				return StrokeType.CENTERED;
		}
	}
	
	/**
	 * Converts the given StrokeType to a {@link SlideStrokeType}.
	 * @param type the stroke type
	 * @return {@link SlideStrokeType}
	 */
	public static SlideStrokeType fromJavaFX(StrokeType type) {
		if (type == null) return null;
		switch (type) {
			case INSIDE:
				return SlideStrokeType.INSIDE;
			case OUTSIDE:
				return SlideStrokeType.OUTSIDE;
			default:
				return SlideStrokeType.CENTERED;
		}
	}
	
	/**
	 * Converts the given {@link SlideStrokeStyle} and line width to a BorderStrokeStyle.
	 * @param style the style
	 * @param lineWidth the border width
	 * @return BorderStrokeStyle
	 */
	public static BorderStrokeStyle toJavaFX(SlideStrokeStyle style, double lineWidth) {
		if (style == null) {
			return null;
		}
		
		Double[] dashes = style.getDashes();
		DashPattern pattern = DashPattern.getDashPattern(dashes);
		// does the style match a dash pattern?
		// we don't need to scale in the case of SOLID and if
		// it doesn't match a dash pattern, then SOLID is returned
		// so this should work for both cases
		if (pattern != DashPattern.SOLID) {
			// scale the dashes based on the line width
			dashes = pattern.getScaledDashPattern(lineWidth);
		}
		
		return new BorderStrokeStyle(
				toJavaFX(style.getType()), 
				toJavaFX(style.getJoin()), 
				toJavaFX(style.getCap()), 
				Double.MAX_VALUE, 
				0.0, 
				Arrays.asList(dashes));
	}
	
	/**
	 * Converts the given BorderStrokeStyle to a {@link SlideStrokeStyle}.
	 * @param style the style
	 * @return {@link SlideStrokeStyle}
	 */
	public static SlideStrokeStyle fromJavaFX(BorderStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new SlideStrokeStyle(
				fromJavaFX(style.getType()), 
				fromJavaFX(style.getLineJoin()), 
				fromJavaFX(style.getLineCap()), 
				style.getDashArray().toArray(new Double[0]));
	}
	
	/**
	 * Converts the given {@link SlideStroke} to a BorderStroke.
	 * @param stroke the border
	 * @return BorderStroke
	 */
	public static BorderStroke toJavaFX(SlideStroke stroke) {
		if (stroke == null) {
			return null;
		}
		// convert to JavaFX paint type
		SlidePaint sp = stroke.getPaint();
		Paint paint = null;
		if (sp instanceof SlideColor) {
			paint = PaintConverter.toJavaFX((SlideColor)sp);
		} else if (sp instanceof SlideGradient) {
			paint = PaintConverter.toJavaFX((SlideGradient)sp);
		} else {
			LOGGER.warn("Media paints are not supported with borders.");
		}
		if (paint == null) {
			return null;
		}
		return new BorderStroke(
				paint,
				toJavaFX(stroke.getStyle(), stroke.getWidth()),
				new CornerRadii(stroke.getRadius()),
				new BorderWidths(stroke.getWidth()));
	}
}
