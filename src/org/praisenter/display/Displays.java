package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.StillBackgroundSettings;
import org.praisenter.settings.TextSettings;

/**
 * Helper class to create displays from settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Displays {
	/**
	 * Creates a new {@link BibleDisplay} from the given {@link BibleSettings}.
	 * @param settings the settings
	 * @param displaySize the target display size
	 * @return {@link BibleDisplay}
	 */
	public static final BibleDisplay getDisplay(BibleSettings settings, Dimension displaySize) {
		return getDisplay(settings, null, displaySize);
	}
	
	/**
	 * Creates a new {@link BibleDisplay} from the given {@link BibleSettings}.
	 * @param settings the settings
	 * @param name the display name
	 * @param displaySize the target display size
	 * @return {@link BibleDisplay}
	 */
	public static final BibleDisplay getDisplay(BibleSettings settings, String name, Dimension displaySize) {
		// get the minimum dimension (typically the height)
		int maxd = displaySize.height;
		if (maxd > displaySize.width) {
			// the width is smaller so use it
			maxd = displaySize.width;
		}
		// set the default screen to text component padding
		final int margin = (int)Math.floor((double)maxd * 0.04);
		
		BibleDisplay display = name == null ? new BibleDisplay(displaySize) : new BibleDisplay(name, displaySize);
		
		// get sub settings
		StillBackgroundSettings sSet = settings.getStillBackgroundSettings();
		TextSettings tSet = settings.getScriptureTitleSettings();
		TextSettings bSet = settings.getScriptureTextSettings(); 
		
		// create a still background
		StillBackgroundComponent stillBackground = display.createStillBackgroundComponent("Background");
		stillBackground.setColor(sSet.getColor());
		stillBackground.setColorCompositeType(sSet.getColorCompositeType());
		stillBackground.setColorVisible(sSet.isColorVisible());
		stillBackground.setImage(sSet.getImage());
		stillBackground.setImageScaleQuality(sSet.getImageScaleQuality());
		stillBackground.setImageScaleType(sSet.getImageScaleType());
		stillBackground.setImageVisible(sSet.isImageVisible());
		stillBackground.setVisible(sSet.isVisible());
		
		// compute the default width, height and position
		final int h = displaySize.height - margin * 2;
		final int w = displaySize.width - margin * 2;
		
		final int tth = (int)Math.ceil((double)h * 0.15);
		final int th = h - tth - margin;
		
		// create the title text component
		Rectangle titleBounds = tSet.getBounds();
		if (titleBounds == null) {
			titleBounds = new Rectangle(margin, margin, w, tth);
		}
		TextComponent titleText = new TextComponent("ScriptureTitle", Messages.getString("display.bible.title.defaultText"), titleBounds.width, titleBounds.height, true);
		titleText.setX(titleBounds.x);
		titleText.setY(titleBounds.y);
		titleText.setTextColor(tSet.getTextColor());
		titleText.setTextAlignment(tSet.getTextAlignment());
		titleText.setTextFontScaleType(tSet.getTextFontScaleType());
		titleText.setTextWrapped(tSet.isTextWrapped());
		titleText.setPadding(tSet.getPadding());
		titleText.setTextFont(tSet.getTextFont());
		titleText.setVisible(tSet.isVisible());
		
		// create the text component
		Rectangle textBounds = bSet.getBounds();
		if (textBounds == null) {
			textBounds = new Rectangle(margin, tth + margin * 2, w, th);
		}
		TextComponent text = new TextComponent("ScriptureText", Messages.getString("display.bible.body.defaultText"), textBounds.width, textBounds.height, true);
		text.setX(textBounds.x);
		text.setY(textBounds.y);
		text.setTextColor(bSet.getTextColor());
		text.setTextAlignment(bSet.getTextAlignment());
		text.setTextFontScaleType(bSet.getTextFontScaleType());
		text.setTextWrapped(bSet.isTextWrapped());
		text.setPadding(bSet.getPadding());
		text.setTextFont(bSet.getTextFont());
		text.setVisible(bSet.isVisible());
		
		display.setStillBackgroundComponent(stillBackground);
		display.setScriptureTitleComponent(titleText);
		display.setScriptureTextComponent(text);
		
		return display;
	}
}
