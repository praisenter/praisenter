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
	
	public static final Glyph REFRESH = FONT_AWESOME.create(FontAwesome.Glyph.REFRESH);
	public static final Glyph INFO = FONT_AWESOME.create(FontAwesome.Glyph.INFO_CIRCLE).color(Color.DODGERBLUE);
	public static final Glyph WARN = FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.ORANGE);
	public static final Glyph ADD = FONT_AWESOME.create(FontAwesome.Glyph.PLUS).color(Color.LIMEGREEN);
	public static final Glyph REMOVE = FONT_AWESOME.create(FontAwesome.Glyph.MINUS).color(Color.RED);
	
	// menu
	
	public static final Glyph MENU_CLOSE = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE);
	public static final Glyph MENU_SAVE = FONT_AWESOME.create(FontAwesome.Glyph.SAVE);
	public static final Glyph MENU_SAVE_AS = FONT_AWESOME.create(FontAwesome.Glyph.SAVE);
	public static final Glyph MENU_RENAME = FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL);
	public static final Glyph MENU_DELETE = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE);
	public static final Glyph MENU_COPY = FONT_AWESOME.create(FontAwesome.Glyph.COPY);
	public static final Glyph MENU_CUT = FONT_AWESOME.create(FontAwesome.Glyph.CUT);
	public static final Glyph MENU_PASTE = FONT_AWESOME.create(FontAwesome.Glyph.PASTE);
	public static final Glyph MENU_PREFERENCES = FONT_AWESOME.create(FontAwesome.Glyph.GEAR);
	public static final Glyph MENU_IMPORT = FONT_AWESOME.create(FontAwesome.Glyph.LEVEL_DOWN);
	public static final Glyph MENU_EXPORT = FONT_AWESOME.create(FontAwesome.Glyph.LEVEL_UP);
	public static final Glyph MENU_ABOUT = FONT_AWESOME.create(FontAwesome.Glyph.INFO);
	
	// tasks
	
	public static final Glyph TASK_SUCCESS = FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN);
	public static final Glyph TASK_FAILURE = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED);
	public static final Glyph TASK_CANCELED = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.LIGHTGRAY);
	
	// bible
	
	public static final Glyph BIBLE_EDIT_ARROW_LEFT = FONT_AWESOME.create(FontAwesome.Glyph.ARROW_LEFT);
	public static final Glyph BIBLE_NAV_INVALID = FONT_AWESOME.create(FontAwesome.Glyph.CLOSE).color(Color.RED);
	
	// media player
	
	public static final Glyph PLAYER_PLAY = FONT_AWESOME.create(FontAwesome.Glyph.PLAY);
	public static final Glyph PLAYER_PAUSE = FONT_AWESOME.create(FontAwesome.Glyph.PAUSE);
	public static final Glyph PLAYER_VOLUME_MUTE = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF);
	public static final Glyph PLAYER_VOLUME_CONTROL = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP);
	
	// slide editing
	
	public static final Glyph SLIDE = FONT_AWESOME.create(FontAwesome.Glyph.DESKTOP);
	public static final Glyph BASIC_TEXT_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FONT);
	public static final Glyph COUNTDOWN_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.CLOCK_ALT);
	public static final Glyph DATETIME_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.CALENDAR_ALT);
	public static final Glyph PLACEHOLDER_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL);
	public static final Glyph AUDIO_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.MUSIC);
	public static final Glyph VIDEO_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FILM);
	public static final Glyph IMAGE_MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.IMAGE);
	public static final Glyph MEDIA_COMPONENT = FONT_AWESOME.create(FontAwesome.Glyph.FILM);
	
	public static final Glyph MEDIA_SCALE_NONE = FONT_AWESOME.create(FontAwesome.Glyph.CROP);
	public static final Glyph MEDIA_SCALE_NONUNIFORM = OPEN_ICONIC.create(OpenIconic.Glyph.RESIZE_BOTH);
	public static final Glyph MEDIA_SCALE_UNIFORM = FONT_AWESOME.create(FontAwesome.Glyph.ARROWS);
	public static final Glyph MEDIA_LOOP = OPEN_ICONIC.create(OpenIconic.Glyph.LOOP);
	public static final Glyph MEDIA_MUTE = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF);
	public static final Glyph MEDIA_VOLUME = FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP);
	
	public static final Glyph FONT_SCALING = FONT_AWESOME.create(FontAwesome.Glyph.TEXT_HEIGHT);
	public static final Glyph HALIGN_LEFT = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT);
	public static final Glyph HALIGN_RIGHT = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT);
	public static final Glyph HALIGN_CENTER = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER);
	public static final Glyph HALIGN_JUSTIFY = FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY);
	public static final Glyph VALIGN_TOP = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_TOP);
	public static final Glyph VALIGN_CENTER = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_CENTER);
	public static final Glyph VALIGN_BOTTOM = OPEN_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_BOTTOM);
	public static final Glyph WRAP_TEXT = FONT_AWESOME.create(FontAwesome.Glyph.PARAGRAPH);
}
