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
package org.praisenter.application.icons;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import org.praisenter.common.utilities.ImageUtilities;

/**
 * Class storing all the icons used by the application.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class Icons {
	// application icons
	
	/** The 16x16 application icon */
	public static final ImageIcon ICON_16 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon16x16.png");
	
	/** The 32x32 application icon */
	public static final ImageIcon ICON_32 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon32x32.png");
	
	/** The 48x48 application icon */
	public static final ImageIcon ICON_48 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon48x48.png");
	
	/** The 64x64 application icon */
	public static final ImageIcon ICON_64 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon64x64.png");
	
	/** The 96x96 application icon */
	public static final ImageIcon ICON_96 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon96x96.png");
	
	/** The 128x128 application icon */
	public static final ImageIcon ICON_128 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon128x128.png");
	
	/** The 256x256 application icon */
	public static final ImageIcon ICON_256 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon256x256.png");
	
	/** The 512x512 application icon */
	public static final ImageIcon ICON_512 = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/icon512x512.png");
	
	/** The Application icons */
	public static final ImageIcon[] APPLICATION_ICONS = new ImageIcon[] {
		ICON_16,
		ICON_32,
		ICON_48,
		ICON_64,
		ICON_96,
		ICON_128,
		ICON_256,
		ICON_512
	};

	/** The unmodifiable list of application icons */
	public static final List<Image> APPLICATION_ICON_LIST = getApplicationIcons();
	
	/**
	 * Returns the application icons as an unmodifiable list.
	 * @return List&lt;Image&gt;
	 */
	private static final List<Image> getApplicationIcons() {
		List<Image> icons = new ArrayList<Image>(APPLICATION_ICONS.length);
		for (ImageIcon icon : APPLICATION_ICONS) {
			icons.add(icon.getImage());
		}
		return Collections.unmodifiableList(icons);
	}
	
	// UI Icons
	
	/** Verse found icon */
	public static final ImageIcon FOUND = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/found.png");
	
	/** Verse not found icon */
	public static final ImageIcon NOT_FOUND = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/not_found.png");
	
	/** Item selected indicator icon */
	public static final ImageIcon SELECTED = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/selected.png");
	
	/** Horizontal align text left icon */
	public static final ImageIcon HORIZONTAL_ALIGN_LEFT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/halign-left.png");
	
	/** Horizontal align text center icon */
	public static final ImageIcon HORIZONTAL_ALIGN_CENTER = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/halign-center.png");
	
	/** Horizontal align text right icon */
	public static final ImageIcon HORIZONTAL_ALIGN_RIGHT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/halign-right.png");
	
	/** Vertical align text top icon */
	public static final ImageIcon VERTICAL_ALIGN_TOP = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/valign-top.png");
	
	/** Vertical align text center icon */
	public static final ImageIcon VERTICAL_ALIGN_CENTER = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/valign-center.png");
	
	/** Vertical align text bottom icon */
	public static final ImageIcon VERTICAL_ALIGN_BOTTOM = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/valign-bottom.png");
	
	/** Bold text icon */
	public static final ImageIcon BOLD = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/bold.png");
	
	/** Italic text icon */
	public static final ImageIcon ITALIC = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/italic.png");

	/** Fill icon */
	public static final ImageIcon FILL = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/fill.png");
	
	/** Border icon */
	public static final ImageIcon BORDER = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/border.png");
	
	/** Image scale none icon */
	public static final ImageIcon IMAGE_SCALE_NONE = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/image-scale-none.png");
	
	/** Image scale non-uniform icon */
	public static final ImageIcon IMAGE_SCALE_NONUNIFORM = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/image-scale-nonuniform.png");
	
	/** Image scale uniform icon */
	public static final ImageIcon IMAGE_SCALE_UNIFORM = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/image-scale-uniform.png");
	
	/** Font size scaling type none icon */
	public static final ImageIcon FONT_SIZE_NONE = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/font-size-none.png");
	
	/** Font size scaling type reduce only icon */
	public static final ImageIcon FONT_SIZE_REDUCE_ONLY = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/font-size-reduce-only.png");
	
	/** Font size scaling type best fit icon */
	public static final ImageIcon FONT_SIZE_BEST_FIT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/font-size-best-fit.png");
	
	/** Warning icon */
	public static final ImageIcon WARNING = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/warning.png");
	
	/** Information icon */
	public static final ImageIcon INFORMATION = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/information.png");
	
	/** Audio component icon */
	public static final ImageIcon AUDIO_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/audio-component.png");
	
	/** Generic component icon */
	public static final ImageIcon GENERIC_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/generic-component.png");
	
	/** Image component icon */
	public static final ImageIcon IMAGE_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/image-component.png");
	
	/** Video component icon */
	public static final ImageIcon VIDEO_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/video-component.png");
	
	/** Text component icon */
	public static final ImageIcon TEXT_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/text-component.png");
	
	/** Date component icon */
	public static final ImageIcon DATE_COMPONENT = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/date-component.png");
	
	/** Move back icon */
	public static final ImageIcon MOVE_BACK = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/move-back.png");
	
	/** Move forward icon */
	public static final ImageIcon MOVE_FORWARD = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/move-forward.png");
	
	/** Remove icon */
	public static final ImageIcon REMOVE = ImageUtilities.getIconFromClassPathSuppressExceptions("/org/praisenter/application/icons/remove.png");
}
