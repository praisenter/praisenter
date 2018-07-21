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
package org.praisenter.ui.fonts;

import java.io.InputStream;
import java.util.Arrays;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.INamedCharacter;

/**
 * The Open Iconic glyph font.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @see <a href="https://useiconic.com/open/">Open Iconic</a>
 */
public final class OpenIconic extends GlyphFont {
	/** The font name */
    public static final String FONT_NAME = "Icons";

	public static enum Glyph implements INamedCharacter {
		ACCOUNT_LOGIN('\ue000'),
		ACCOUNT_LOGOUT('\ue001'),
		ACTION_REDO('\ue002'),
		ACTION_UNDO('\ue003'),
		ALIGN_CENTER('\ue004'),
		ALIGN_LEFT('\ue005'),
		ALIGN_RIGHT('\ue006'),
		APERTURE('\ue007'),
		ARROW_BOTTOM('\ue008'),
		ARROW_CIRCLE_BOTTOM('\ue009'),
		ARROW_CIRCLE_LEFT('\ue00a'),
		ARROW_CIRCLE_RIGHT('\ue00b'),
		ARROW_CIRCLE_TOP('\ue00c'),
		ARROW_LEFT('\ue00d'),
		ARROW_RIGHT('\ue00e'),
		ARROW_THICK_BOTTOM('\ue00f'),
		ARROW_THICK_LEFT('\ue010'),
		ARROW_THICK_RIGHT('\ue011'),
		ARROW_THICK_TOP('\ue012'),
		ARROW_TOP('\ue013'),
		AUDIO_SPECTRUM('\ue014'),
		AUDIO('\ue015'),
		BADGE('\ue016'),
		BAN('\ue017'),
		BAR_CHART('\ue018'),
		BASKET('\ue019'),
		BATTERY_EMPTY('\ue01a'),
		BATTERY_FULL('\ue01b'),
		BEAKER('\ue01c'),
		BELL('\ue01d'),
		BLUETOOTH('\ue01e'),
		BOLD('\ue01f'),
		BOLT('\ue020'),
		BOOK('\ue021'),
		BOOKMARK('\ue022'),
		BOX('\ue023'),
		BRIEFCASE('\ue024'),
		BRITISH_POUND('\ue025'),
		BROWSER('\ue026'),
		BRUSH('\ue027'),
		BUG('\ue028'),
		BULLHORN('\ue029'),
		CALCULATOR('\ue02a'),
		CALENDAR('\ue02b'),
		CAMERA_SLR('\ue02c'),
		CARET_BOTTOM('\ue02d'),
		CARET_LEFT('\ue02e'),
		CARET_RIGHT('\ue02f'),
		CARET_TOP('\ue030'),
		CART('\ue031'),
		CHAT('\ue032'),
		CHECK('\ue033'),
		CHEVRON_BOTTOM('\ue034'),
		CHEVRON_LEFT('\ue035'),
		CHEVRON_RIGHT('\ue036'),
		CHEVRON_TOP('\ue037'),
		CIRCLE_CHECK('\ue038'),
		CIRCLE_X('\ue039'),
		CLIPBOARD('\ue03a'),
		CLOCK('\ue03b'),
		CLOUD_DOWNLOAD('\ue03c'),
		CLOUD_UPLOAD('\ue03d'),
		CLOUD('\ue03e'),
		CLOUDY('\ue03f'),
		CODE('\ue040'),
		COG('\ue041'),
		COLLAPSE_DOWN('\ue042'),
		COLLAPSE_LEFT('\ue043'),
		COLLAPSE_RIGHT('\ue044'),
		COLLAPSE_UP('\ue045'),
		COMMAND('\ue046'),
		COMMENT_SQUARE('\ue047'),
		COMPASS('\ue048'),
		CONTRAST('\ue049'),
		COPYWRITING('\ue04a'),
		CREDIT_CARD('\ue04b'),
		CROP('\ue04c'),
		DASHBOARD('\ue04d'),
		DATA_TRANSFER_DOWNLOAD('\ue04e'),
		DATA_TRANSFER_UPLOAD('\ue04f'),
		DELETE('\ue050'),
		DIAL('\ue051'),
		DOCUMENT('\ue052'),
		DOLLAR('\ue053'),
		DOUBLE_QUOTE_SANS_LEFT('\ue054'),
		DOUBLE_QUOTE_SANS_RIGHT('\ue055'),
		DOUBLE_QUOTE_SERIF_LEFT('\ue056'),
		DOUBLE_QUOTE_SERIF_RIGHT('\ue057'),
		DROPLET('\ue058'),
		EJECT('\ue059'),
		ELEVATOR('\ue05a'),
		ELLIPSES('\ue05b'),
		ENVELOPE_CLOSED('\ue05c'),
		ENVELOPE_OPEN('\ue05d'),
		EURO('\ue05e'),
		EXCERPT('\ue05f'),
		EXPAND_DOWN('\ue060'),
		EXPAND_LEFT('\ue061'),
		EXPAND_RIGHT('\ue062'),
		EXPAND_UP('\ue063'),
		EXTERNAL_LINK('\ue064'),
		EYE('\ue065'),
		EYEDROPPER('\ue066'),
		FILE('\ue067'),
		FIRE('\ue068'),
		FLAG('\ue069'),
		FLASH('\ue06a'),
		FOLDER('\ue06b'),
		FORK('\ue06c'),
		FULLSCREEN_ENTER('\ue06d'),
		FULLSCREEN_EXIT('\ue06e'),
		GLOBE('\ue06f'),
		GRAPH('\ue070'),
		GRID_FOUR_UP('\ue071'),
		GRID_THREE_UP('\ue072'),
		GRID_TWO_UP('\ue073'),
		HARD_DRIVE('\ue074'),
		HEADER('\ue075'),
		HEADPHONES('\ue076'),
		HEART('\ue077'),
		HOME('\ue078'),
		IMAGE('\ue079'),
		INBOX('\ue07a'),
		INFINITY('\ue07b'),
		INFO('\ue07c'),
		ITALIC('\ue07d'),
		JUSTIFY_CENTER('\ue07e'),
		JUSTIFY_LEFT('\ue07f'),
		JUSTIFY_RIGHT('\ue080'),
		KEY('\ue081'),
		LAPTOP('\ue082'),
		LAYERS('\ue083'),
		LIGHTBULB('\ue084'),
		LINK_BROKEN('\ue085'),
		LINK_INTACT('\ue086'),
		LIST_RICH('\ue087'),
		LIST('\ue088'),
		LOCATION('\ue089'),
		LOCK_LOCKED('\ue08a'),
		LOCK_UNLOCKED('\ue08b'),
		LOOP_CIRCULAR('\ue08c'),
		LOOP_SQUARE('\ue08d'),
		LOOP('\ue08e'),
		MAGNIFYING_GLASS('\ue08f'),
		MAP_MARKER('\ue090'),
		MAP('\ue091'),
		MEDIA_PAUSE('\ue092'),
		MEDIA_PLAY('\ue093'),
		MEDIA_RECORD('\ue094'),
		MEDIA_SKIP_BACKWARD('\ue095'),
		MEDIA_SKIP_FORWARD('\ue096'),
		MEDIA_STEP_BACKWARD('\ue097'),
		MEDIA_STEP_FORWARD('\ue098'),
		MEDIA_STOP('\ue099'),
		MEDICAL_CROSS('\ue09a'),
		MENU('\ue09b'),
		MICROPHONE('\ue09c'),
		MINUS('\ue09d'),
		MONITOR('\ue09e'),
		MOON('\ue09f'),
		MOVE('\ue0a0'),
		MUSICAL_NOTE('\ue0a1'),
		PAPERCLIP('\ue0a2'),
		PENCIL('\ue0a3'),
		PEOPLE('\ue0a4'),
		PERSON('\ue0a5'),
		PHONE('\ue0a6'),
		PIE_CHART('\ue0a7'),
		PIN('\ue0a8'),
		PLAY_CIRCLE('\ue0a9'),
		PLUS('\ue0aa'),
		POWER_STANDBY('\ue0ab'),
		PRINT('\ue0ac'),
		PROJECT('\ue0ad'),
		PULSE('\ue0ae'),
		PUZZLE_PIECE('\ue0af'),
		QUESTION_MARK('\ue0b0'),
		RAIN('\ue0b1'),
		RANDOM('\ue0b2'),
		RELOAD('\ue0b3'),
		RESIZE_BOTH('\ue0b4'),
		RESIZE_HEIGHT('\ue0b5'),
		RESIZE_WIDTH('\ue0b6'),
		RSS_ALT('\ue0b7'),
		RSS('\ue0b8'),
		SCRIPT('\ue0b9'),
		SHARE_BOXED('\ue0ba'),
		SHARE('\ue0bb'),
		SHIELD('\ue0bc'),
		SIGNAL('\ue0bd'),
		SIGNPOST('\ue0be'),
		SORT_ASCENDING('\ue0bf'),
		SORT_DESCENDING('\ue0c0'),
		SPREADSHEET('\ue0c1'),
		STAR('\ue0c2'),
		SUN('\ue0c3'),
		TABLET('\ue0c4'),
		TAG('\ue0c5'),
		TAGS('\ue0c6'),
		TARGET('\ue0c7'),
		TASK('\ue0c8'),
		TERMINAL('\ue0c9'),
		TEXT('\ue0ca'),
		THUMB_DOWN('\ue0cb'),
		THUMB_UP('\ue0cc'),
		TIMER('\ue0cd'),
		TRANSFER('\ue0ce'),
		TRASH('\ue0cf'),
		UNDERLINE('\ue0d0'),
		VERTICAL_ALIGN_BOTTOM('\ue0d1'),
		VERTICAL_ALIGN_CENTER('\ue0d2'),
		VERTICAL_ALIGN_TOP('\ue0d3'),
		VIDEO('\ue0d4'),
		VOLUME_HIGH('\ue0d5'),
		VOLUME_LOW('\ue0d6'),
		VOLUME_OFF('\ue0d7'),
		WARNING('\ue0d8'),
		WIFI('\ue0d9'),
		WRENCH('\ue0da'),
		X('\ue0db'),
		YEN('\ue0dc'),
		ZOOM_IN('\ue0dd'),
		ZOOM_OUT('\ue0de');

        private final char ch;

        /**
         * Creates a named Glyph mapped to the given character
         * @param ch
         */
        Glyph( char ch ) {
            this.ch = ch;
        }

        @Override
        public char getChar() {
            return ch;
        }
	}
	
    /**
     * Creates a new OpenIconic instance which uses the provided font source.
     * @param url the url to the font
     */
    public OpenIconic(String url){
        super(FONT_NAME, 12, url, true);
        registerAll(Arrays.asList(Glyph.values()));
    }

    /**
     * Creates a new OpenIconic instance which uses the provided font source.
     * @param is the input stream to the font
     */
    public OpenIconic(InputStream is){
        super(FONT_NAME, 12, is, true);
        registerAll(Arrays.asList(Glyph.values()));
    }
}
