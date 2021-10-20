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



import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.text.HorizontalTextAlignment;
import org.praisenter.data.slide.text.VerticalTextAlignment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

/**
 * Class with a collection of text alignment related conversion methods for Java FX and Praisenter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class TextAlignmentConverter {
	/** private constructor */
	private TextAlignmentConverter() {}

	/**
	 * Returns the Insets for the given {@link SlidePadding}.
	 * @param padding the padding
	 * @return Insets
	 */
	public static Insets toJavaFX(SlidePadding padding) {
		return new Insets(padding.getTop(), padding.getRight(), padding.getBottom(), padding.getLeft());
	}
	
	/**
	 * Returns the {@link SlidePadding} for the given Insets.
	 * @param insets the insets
	 * @return {@link SlidePadding}
	 */
	public static SlidePadding fromJavaFX(Insets insets) {
		return new SlidePadding(insets.getTop(), insets.getRight(), insets.getBottom(), insets.getLeft());
	}
	
	/**
	 * Converts the given {@link HorizontalTextAlignment} to a TextAlignment.
	 * @param alignment the alignment
	 * @return TextAlignment
	 */
	public static TextAlignment toJavaFX(HorizontalTextAlignment alignment) {
		switch (alignment) {
			case RIGHT:
				return TextAlignment.RIGHT;
			case CENTER:
				return TextAlignment.CENTER;
			case JUSTIFY:
				return TextAlignment.JUSTIFY;
			default:
				return TextAlignment.LEFT;
		}
	}
	
	/**
	 * Converts the given TextAlignment to a {@link HorizontalTextAlignment}.
	 * @param alignment the alignment
	 * @return {@link HorizontalTextAlignment}
	 */
	public static HorizontalTextAlignment fromJavaFX(TextAlignment alignment) {
		switch (alignment) {
			case RIGHT:
				return HorizontalTextAlignment.RIGHT;
			case CENTER:
				return HorizontalTextAlignment.CENTER;
			case JUSTIFY:
				return HorizontalTextAlignment.JUSTIFY;
			default:
				return HorizontalTextAlignment.LEFT;
		}
	}
	
	/**
	 * Converts the given {@link VerticalTextAlignment} to a Pos.
	 * @param alignment the alignment
	 * @return Pos
	 */
	public static Pos toJavaFX(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
			default:
				return Pos.TOP_LEFT;
		}
	}
	
	/**
	 * Converts the given Pos to a {@link VerticalTextAlignment}.
	 * @param alignment the alignment
	 * @return {@link VerticalTextAlignment}
	 */
	public static VerticalTextAlignment fromJavaFX(Pos alignment) {
		switch (alignment) {
			case CENTER:
			case CENTER_RIGHT:
			case CENTER_LEFT:
				return VerticalTextAlignment.CENTER;
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
			case BOTTOM_LEFT:
				return VerticalTextAlignment.BOTTOM;
			default:
				return VerticalTextAlignment.TOP;
		}
	}
	
	/**
	 * Converts the given {@link VerticalTextAlignment} and {@link HorizontalTextAlignment} to a Pos.
	 * @param valign the vertical text alignment
	 * @param halign the horizontal text alignment
	 * @return Pos
	 */
	public static Pos toJavaFX(VerticalTextAlignment valign, HorizontalTextAlignment halign) {
		if (valign == VerticalTextAlignment.TOP) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.TOP_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.TOP_CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.TOP_CENTER;
			} else {
				return Pos.TOP_LEFT;
			}
		} else if (valign == VerticalTextAlignment.CENTER) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.CENTER_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.CENTER;
			} else {
				return Pos.CENTER_LEFT;
			}
		} else if (valign == VerticalTextAlignment.BOTTOM) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.BOTTOM_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.BOTTOM_CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.BOTTOM_CENTER;
			} else {
				return Pos.BOTTOM_LEFT;
			}
		} else {
			return Pos.CENTER;
		}
	}
}
