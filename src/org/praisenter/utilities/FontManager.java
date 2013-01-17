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
package org.praisenter.utilities;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

/**
 * Class used to help manage fonts.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class FontManager {
	/** The array of fonts available on the system */
	private static final String[] FONT_FAMILY_NAMES = loadAllFonts();
	
	/** The default font; We use the current UIManager's default label font */
	private static final Font DEFAULT_FONT = UIManager.getDefaults().getFont("Label.font");
	
	/** Cache for the font family fonts; These are used to derive the fonts that go into {@link #FONT_CACHE} */
	private static final Map<String, Font> FONT_FAMILY_CACHE = new HashMap<String, Font>();
	
	/** Cache for the derived fonts */
	private static final Map<FontKey, Font> FONT_CACHE = new HashMap<FontKey, Font>();
	
	/** Hidden default constructor */
	private FontManager() {}
	
	/**
	 * Represents a key for a map for a derived font.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private static final class FontKey {
		/** The font family */
		private String family;
		
		/** The font style */
		private int style;
		
		/** The font size */
		private float size;
		
		/**
		 * Full constructor.
		 * @param family the font family
		 * @param style the font style
		 * @param size the font size
		 */
		public FontKey(String family, int style, float size) {
			this.family = family;
			this.style = style;
			this.size = size;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("FontKey[family=").append(this.family)
			  .append("|style=").append(this.style)
			  .append("|size=").append(this.size);
			
			return sb.toString();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (obj instanceof FontKey) {
				FontKey fk = (FontKey)obj;
				if (fk.family.equals(this.family) &&
					fk.style == this.style &&
					fk.size == this.size) {
					return true;
				}
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			int result = this.family.hashCode();
			result = 37 * result + this.style;
			result = 37 * result + Float.floatToIntBits(this.size);
			return result;
		}
	}
	
	/**
	 * Returns a font of the specified family, style, and size.
	 * @param family the font family
	 * @param style the font style
	 * @param size the font size
	 * @return Font
	 */
	public static final Font getFont(String family, int style, float size) {
		// make sure we only allow one thread at a time here
		synchronized (FontManager.FONT_CACHE) {
			// create the font key
			FontKey key = new FontKey(family, style, size);
			// get the font
			Font font = FontManager.FONT_CACHE.get(key);
			// check if it exists
			if (font == null) {
				// check the font family cache
				font = FontManager.FONT_FAMILY_CACHE.get(family);
				// check if it exists
				if (font == null) {
					// then we need to create a whole new font
					// just use the default label size
					Font defaultFont = FontManager.getDefaultFont();
					int dsize = defaultFont.getSize();
					// create the font
					font = new Font(family, style, dsize);
					// add it to the family cache
					FontManager.FONT_FAMILY_CACHE.put(family, font);
				}
				// now we need to derive the font to match the input
				font = font.deriveFont(style, size);
				// add the derived font to the font cache
				FontManager.FONT_CACHE.put(key, font);
			}
			return font;
		}
	}
	
	/**
	 * Loads all the font family names from the system.
	 * <p>
	 * This may take some time.
	 * @return String[]
	 */
	private static final String[] loadAllFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}
	
	/**
	 * Returns all the font families on this system.
	 * @return String[]
	 */
	public static final String[] getFontFamilyNames() {
		return FontManager.FONT_FAMILY_NAMES;
	}
	
	/**
	 * Gets the default font for labels.
	 * @return Font
	 */
	public static final Font getDefaultFont() {
		return FontManager.DEFAULT_FONT;
	}
}
