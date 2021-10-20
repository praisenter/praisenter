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

import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.SlideFontPosture;
import org.praisenter.data.slide.text.SlideFontWeight;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Class with a collection of font related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class FontConverter {
	/** private constructor */
	private FontConverter() {}
	
	/**
	 * Converts the given {@link SlideFontWeight} to a FontWeight.
	 * @param weight the weight
	 * @return FontWeight
	 */
	public static FontWeight toJavaFX(SlideFontWeight weight) {
		switch (weight) {
			case BLACK:
				return FontWeight.BLACK;
			case BOLD:
				return FontWeight.BOLD;
			case EXTRA_BOLD:
				return FontWeight.EXTRA_BOLD;
			case EXTRA_LIGHT:
				return FontWeight.EXTRA_LIGHT;
			case LIGHT:
				return FontWeight.LIGHT;
			case MEDIUM:
				return FontWeight.MEDIUM;
			case SEMI_BOLD:
				return FontWeight.SEMI_BOLD;
			case THIN:
				return FontWeight.THIN;
			case NORMAL:
			default:
				return FontWeight.NORMAL;
		}
	}
	
	/**
	 * Converts the given FontWeight to a {@link SlideFontWeight}.
	 * @param weight the weight
	 * @return {@link SlideFontWeight}
	 */
	public static SlideFontWeight fromJavaFX(FontWeight weight) {
		switch (weight) {
			case BLACK:
				return SlideFontWeight.BLACK;
			case BOLD:
				return SlideFontWeight.BOLD;
			case EXTRA_BOLD:
				return SlideFontWeight.EXTRA_BOLD;
			case EXTRA_LIGHT:
				return SlideFontWeight.EXTRA_LIGHT;
			case LIGHT:
				return SlideFontWeight.LIGHT;
			case MEDIUM:
				return SlideFontWeight.MEDIUM;
			case SEMI_BOLD:
				return SlideFontWeight.SEMI_BOLD;
			case THIN:
				return SlideFontWeight.THIN;
			case NORMAL:
			default:
				return SlideFontWeight.NORMAL;
		}
	}
	
	/**
	 * Converts the given {@link SlideFontPosture} to a FontPosture.
	 * @param posture the posture
	 * @return FontPosture
	 */
	public static FontPosture toJavaFX(SlideFontPosture posture) {
		switch (posture) {
			case ITALIC:
				return FontPosture.ITALIC;
			case REGULAR:
			default:
				return FontPosture.REGULAR;
		}
	}
	
	/**
	 * Converts the given FontPosture to a {@link SlideFontPosture}.
	 * @param posture the posture
	 * @return {@link SlideFontPosture}
	 */
	public static SlideFontPosture fromJavaFX(FontPosture posture) {
		switch (posture) {
			case ITALIC:
				return SlideFontPosture.ITALIC;
			case REGULAR:
			default:
				return SlideFontPosture.REGULAR;
		}
	}
	
	/**
	 * Converts the given praisenter font to a Font.
	 * @param font the font
	 * @return Font
	 */
	public static Font toJavaFX(SlideFont font) {
		if (font == null) {
			return Font.getDefault();
		}
		return Font.font(
				font.getFamily(), 
				toJavaFX(font.getWeight()),
				toJavaFX(font.getPosture()),
				font.getSize());
	}
	
	/**
	 * Converts the given Java FX font to a {@link SlideFont}.
	 * @param font the font
	 * @return {@link SlideFont}
	 */
	public static SlideFont fromJavaFX(Font font) {
		if (font == null) {
			return null;
		}
		String style = font.getStyle();
		String[] styles = (style == null ? "" : style.trim().toUpperCase()).split(" ");
		SlideFont sf = new SlideFont();
		sf.setFamily(font.getFamily());
		sf.setWeight(fromJavaFX(getWeight(styles)));
		sf.setPosture(fromJavaFX(getPosture(styles)));
		sf.setSize(font.getSize());
		return sf;
	}
	
	/**
	 * Converts the given set of styles to a FontWeight.
	 * <p>
	 * Returns FontWeight.NORMAL if the styles do not match any know weight.
	 * @param styles
	 * @return FontWeight
	 */
	private static final FontWeight getWeight(String[] styles) {
		for (String s : styles) {
			FontWeight weight = FontWeight.findByName(s);
			if (weight != null) {
				return weight;
			}
		}
		return FontWeight.NORMAL;
	}
	
	/**
	 * Converts the given set of styles to a FontPosture.
	 * <p>
	 * Returns FontPosture.REGULAR if the styles do not match any know posture.
	 * @param styles
	 * @return FontPosture
	 */
	private static final FontPosture getPosture(String[] styles) {
		for (String s : styles) {
			FontPosture posture = FontPosture.findByName(s);
			if (posture != null) {
				return posture;
			}
		}
		return FontPosture.REGULAR;
	}
}
