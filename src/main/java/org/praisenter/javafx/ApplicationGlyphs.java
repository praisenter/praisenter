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
package org.praisenter.javafx;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.resources.OpenIconic;

import javafx.scene.paint.Color;

/**
 * Helper class to store glyphs used through out the application.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ApplicationGlyphs {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	/** The open iconic glyph-font pack */
	private static final GlyphFont OPEN_ICONIC = GlyphFontRegistry.font("Icons");
	
	/** hidden constructor */
	private ApplicationGlyphs() {}
	
	// general
	
	/** Refresh icon */
	public static final Glyph REFRESH = FONT_AWESOME.create(FontAwesome.Glyph.REFRESH);
	
	/** Info icon */
	public static final Glyph INFO = FONT_AWESOME.create(FontAwesome.Glyph.INFO_CIRCLE).color(Color.DODGERBLUE);
	
	/** Warn icon */
	public static final Glyph WARN = FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.ORANGE);

	/** Error icon */
	public static final Glyph ERROR = FONT_AWESOME.create(FontAwesome.Glyph.TIMES_CIRCLE).color(Color.RED);
	
	/** Add icon */
	public static final Glyph ADD = FONT_AWESOME.create(FontAwesome.Glyph.PLUS).color(Color.LIMEGREEN);
	
	/** Remove icon */
	public static final Glyph REMOVE = FONT_AWESOME.create(FontAwesome.Glyph.MINUS).color(Color.RED);

	/** Download icon */
	public static final Glyph EXPORT = FONT_AWESOME.create(FontAwesome.Glyph.LEVEL_UP);

	/** Upload icon */
	public static final Glyph IMPORT = FONT_AWESOME.create(FontAwesome.Glyph.LEVEL_DOWN);
	
	// menu
	
	/** Menu home icon */
	public static final Glyph MENU_HOME = OPEN_ICONIC.create(OpenIconic.Glyph.MONITOR).color(Color.rgb(0, 151, 219));
	
	/** Menu close icon */
	public static final Glyph MENU_CLOSE = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE);
	
	/** Menu save icon */
	public static final Glyph MENU_SAVE = FONT_AWESOME.create(FontAwesome.Glyph.SAVE);
	
	/** Menu save as icon */
	public static final Glyph MENU_SAVE_AS = FONT_AWESOME.create(FontAwesome.Glyph.SAVE);
	
	/** Menu rename icon */
	public static final Glyph MENU_RENAME = FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL);
	
	/** Menu delete icon */
	public static final Glyph MENU_DELETE = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE);
	
	/** Menu copy icon */
	public static final Glyph MENU_COPY = FONT_AWESOME.create(FontAwesome.Glyph.COPY);
	
	/** Menu cut icon */
	public static final Glyph MENU_CUT = FONT_AWESOME.create(FontAwesome.Glyph.CUT);
	
	/** Menu paste icon */
	public static final Glyph MENU_PASTE = FONT_AWESOME.create(FontAwesome.Glyph.PASTE);
	
	/** Menu undo icon */
	public static final Glyph MENU_UNDO = OPEN_ICONIC.create(OpenIconic.Glyph.ACTION_UNDO);
	
	/** Menu redo icon */
	public static final Glyph MENU_REDO = OPEN_ICONIC.create(OpenIconic.Glyph.ACTION_REDO);
	
	/** Menu preferences icon */
	public static final Glyph MENU_PREFERENCES = FONT_AWESOME.create(FontAwesome.Glyph.GEAR);
	
	/** Menu import icon (alias for {@link #IMPORT}) */
	public static final Glyph MENU_IMPORT = IMPORT;
	
	/** Menu export icon (alias for {@link #EXPORT}) */
	public static final Glyph MENU_EXPORT = EXPORT;
	
	/** Menu about icon */
	public static final Glyph MENU_ABOUT = FONT_AWESOME.create(FontAwesome.Glyph.INFO);
	
	// tasks
	
	/** Task success icon */
	public static final Glyph TASK_SUCCESS = FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN);
	
	/** Task failure icon */
	public static final Glyph TASK_FAILURE = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED);
	
	/** Task cancelled icon */
	public static final Glyph TASK_CANCELED = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.LIGHTGRAY);
	
	// bible
	
	/** Bible edit arrow */
	public static final Glyph BIBLE_EDIT_ARROW_LEFT = FONT_AWESOME.create(FontAwesome.Glyph.ARROW_LEFT);
	
	/** Bible navigation (lookup) invalid icon */
	public static final Glyph BIBLE_NAV_INVALID = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE).color(Color.RED);
	
	// media player
	
	/** Media player play icon */
	public static final Glyph PLAYER_PLAY = FONT_AWESOME.create(FontAwesome.Glyph.PLAY);
	
	/** Media player pause icon */
	public static final Glyph PLAYER_PAUSE = FONT_AWESOME.create(FontAwesome.Glyph.PAUSE);
	
	/** Media player volume mute icon */
	public static final Glyph PLAYER_VOLUME_MUTE = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF);
	
	/** Media player volume non-muted icon */
	public static final Glyph PLAYER_VOLUME_CONTROL = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP);
	
	// slide editing
	
	/** Slide icon */
	public static final Glyph SLIDE = FONT_AWESOME.create(FontAwesome.Glyph.DESKTOP);
	
	/** Basic text component icon */
	public static final Glyph BASIC_TEXT_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FONT);
	
	/** Countdown component icon */
	public static final Glyph COUNTDOWN_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.CLOCK_ALT);
	
	/** Datetime component icon */
	public static final Glyph DATETIME_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.CALENDAR_ALT);
	
	/** Placeholder component icon */
	public static final Glyph PLACEHOLDER_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL);
	
	/** Audio media component icon */
	public static final Glyph AUDIO_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.MUSIC);
	
	/** Video media component icon */
	public static final Glyph VIDEO_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FILM);
	
	/** Image media component icon */
	public static final Glyph IMAGE_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.IMAGE);
	
	/** General media component icon */
	public static final Glyph MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FILM);
	
	
	/** Media scale none icon */
	public static final Glyph MEDIA_SCALE_NONE = FONT_AWESOME.create(FontAwesome.Glyph.CROP);
	
	/** Media scale non-uniform icon */
	public static final Glyph MEDIA_SCALE_NONUNIFORM = OPEN_ICONIC.create(OpenIconic.Glyph.RESIZE_BOTH);
	
	/** Media scale uniform icon */
	public static final Glyph MEDIA_SCALE_UNIFORM = FONT_AWESOME.create(FontAwesome.Glyph.ARROWS);
	
	/** Media loop icon */
	public static final Glyph MEDIA_LOOP = OPEN_ICONIC.create(OpenIconic.Glyph.LOOP);
	
	/** Media mute icon */
	public static final Glyph MEDIA_MUTE = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF);
	
	/** Media non-muted icon */
	public static final Glyph MEDIA_VOLUME = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP);
	
	
	/** Font scale icon */
	public static final Glyph FONT_SCALING = FONT_AWESOME.create(FontAwesome.Glyph.TEXT_HEIGHT);
	
	/** Text left horizontal alignment icon */
	public static final Glyph HALIGN_LEFT = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT);
	
	/** Text right horizontal alignment icon */
	public static final Glyph HALIGN_RIGHT = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT);
	
	/** Text center horizontal alignment icon */
	public static final Glyph HALIGN_CENTER = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER);
	
	/** Text justify horizontal alignment icon */
	public static final Glyph HALIGN_JUSTIFY = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY);
	
	/** Text top vertical alignment icon */
	public static final Glyph VALIGN_TOP = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_TOP);
	
	/** Text center vertical alignment icon */
	public static final Glyph VALIGN_CENTER = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_CENTER);
	
	/** Text bottom vertical alignment icon */
	public static final Glyph VALIGN_BOTTOM = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_BOTTOM);
	
	/** Text wrapping toggle icon */
	public static final Glyph WRAP_TEXT = FONT_AWESOME.create(FontAwesome.Glyph.PARAGRAPH);
}
