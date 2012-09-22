package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.ColorBackgroundSettings;
import org.praisenter.settings.ImageBackgroundSettings;
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
		// get the minimum dimension (typically the height)
		int maxd = displaySize.height;
		if (maxd > displaySize.width) {
			// the width is smaller so use it
			maxd = displaySize.width;
		}
		// set the default screen to text component padding
		final int margin = (int)Math.floor((double)maxd * 0.04);
		
		BibleDisplay display = new BibleDisplay(displaySize);
		
		// get sub settings
		ColorBackgroundSettings cSet = settings.getColorBackgroundSettings();
		ImageBackgroundSettings iSet = settings.getImageBackgroundSettings();
		TextSettings tSet = settings.getScriptureTitleSettings();
		TextSettings bSet = settings.getScriptureTextSettings(); 
		
		// create the default color background
		ColorBackgroundComponent colorBackground = display.createColorBackgroundComponent("BackgroundColor");
		colorBackground.setColor(cSet.getColor());
		colorBackground.setVisible(cSet.isVisible());
		
		// create an empty image background
		ImageBackgroundComponent imageBackground = display.createImageBackgroundComponent("BackgroundImage");
		imageBackground.setImage(iSet.getImage());
		imageBackground.setScaleQuality(iSet.getScaleQuality());
		imageBackground.setScaleType(iSet.getScaleType());
		imageBackground.setVisible(iSet.isVisible());
		
		// compute the default width, height and position
		final int h = displaySize.height - margin * 2;
		final int w = displaySize.width - margin * 2;
		
		final int tth = (int)Math.ceil((double)h * 0.25);
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
		
		display.setColorBackgroundComponent(colorBackground);
		display.setImageBackgroundComponent(imageBackground);
		display.setScriptureTitleComponent(titleText);
		display.setScriptureTextComponent(text);
		
		return display;
	}
}