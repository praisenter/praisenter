package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.GraphicsComponentSettings;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SongSettings;
import org.praisenter.settings.TextComponentSettings;

/**
 * Helper class to create displays from settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO allow background sharing of components; this could improve performance significantly
public class DisplayFactory {
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
		GraphicsComponentSettings<GraphicsComponent> bSet = settings.getBackgroundSettings();
		TextComponentSettings tSet = settings.getScriptureTitleSettings();
		TextComponentSettings xSet = settings.getScriptureTextSettings(); 
		
		// create a background
		GraphicsComponent background = display.createBackgroundComponent("Background");
		background.setBackgroundColor(bSet.getBackgroundColor());
		background.setBackgroundColorCompositeType(bSet.getBackgroundColorCompositeType());
		background.setBackgroundColorVisible(bSet.isBackgroundColorVisible());
		background.setBackgroundImage(bSet.getBackgroundImage());
		background.setBackgroundImageScaleQuality(bSet.getBackgroundImageScaleQuality());
		background.setBackgroundImageScaleType(bSet.getBackgroundImageScaleType());
		background.setBackgroundImageVisible(bSet.isBackgroundImageVisible());
		background.setVisible(bSet.isVisible());
		
		// compute the default width, height and position
		final int h = displaySize.height - margin * 2;
		final int w = displaySize.width - margin * 2;
		
		final int tth = (int)Math.ceil((double)h * 0.20);
		final int th = h - tth - margin;
		
		// create the title text component
		Rectangle titleBounds = tSet.getBounds();
		if (titleBounds == null) {
			titleBounds = new Rectangle(margin, margin, w, tth);
		}
		TextComponent titleText = new TextComponent("ScriptureTitle", Messages.getString("display.bible.title.defaultText"), titleBounds.width, titleBounds.height, true);
		// general
		titleText.setX(titleBounds.x);
		titleText.setY(titleBounds.y);
		titleText.setVisible(tSet.isVisible());
		// color
		titleText.setBackgroundColor(tSet.getBackgroundColor());
		titleText.setBackgroundColorCompositeType(tSet.getBackgroundColorCompositeType());
		titleText.setBackgroundColorVisible(tSet.isBackgroundColorVisible());
		// image
		titleText.setBackgroundImage(tSet.getBackgroundImage());
		titleText.setBackgroundImageScaleQuality(tSet.getBackgroundImageScaleQuality());
		titleText.setBackgroundImageScaleType(tSet.getBackgroundImageScaleType());
		titleText.setBackgroundImageVisible(tSet.isBackgroundImageVisible());
		// text
		titleText.setTextColor(tSet.getTextColor());
		titleText.setHorizontalTextAlignment(tSet.getHorizontalTextAlignment());
		titleText.setVerticalTextAlignment(tSet.getVerticalTextAlignment());
		titleText.setTextFontScaleType(tSet.getTextFontScaleType());
		titleText.setTextWrapped(tSet.isTextWrapped());
		titleText.setPadding(tSet.getPadding());
		titleText.setTextFont(tSet.getTextFont());
		
		
		// create the text component
		Rectangle textBounds = xSet.getBounds();
		if (textBounds == null) {
			textBounds = new Rectangle(margin, tth + margin * 2, w, th);
		}
		TextComponent text = new TextComponent("ScriptureText", Messages.getString("display.bible.body.defaultText"), textBounds.width, textBounds.height, true);
		// general
		text.setX(textBounds.x);
		text.setY(textBounds.y);
		text.setVisible(xSet.isVisible());
		// color
		text.setBackgroundColor(xSet.getBackgroundColor());
		text.setBackgroundColorCompositeType(xSet.getBackgroundColorCompositeType());
		text.setBackgroundColorVisible(xSet.isBackgroundColorVisible());
		// image
		text.setBackgroundImage(xSet.getBackgroundImage());
		text.setBackgroundImageScaleQuality(xSet.getBackgroundImageScaleQuality());
		text.setBackgroundImageScaleType(xSet.getBackgroundImageScaleType());
		text.setBackgroundImageVisible(xSet.isBackgroundImageVisible());
		// text
		text.setTextColor(xSet.getTextColor());
		text.setHorizontalTextAlignment(xSet.getHorizontalTextAlignment());
		text.setVerticalTextAlignment(xSet.getVerticalTextAlignment());
		text.setTextFontScaleType(xSet.getTextFontScaleType());
		text.setTextWrapped(xSet.isTextWrapped());
		text.setPadding(xSet.getPadding());
		text.setTextFont(xSet.getTextFont());
		
		display.setBackgroundComponent(background);
		display.setScriptureTitleComponent(titleText);
		display.setScriptureTextComponent(text);
		
		return display;
	}
	
	/**
	 * Creates a new {@link NotificationDisplay}.
	 * @param settings the settings
	 * @param displaySize the target display size
	 * @param text the text to show in the display
	 * @return {@link NotificationDisplay}
	 */
	public static final NotificationDisplay getDisplay(NotificationSettings settings, Dimension displaySize, String text) {
		TextComponentSettings tSet = settings.getTextSettings();
		
		NotificationDisplay display = new NotificationDisplay(displaySize);
		
		// get the minimum dimension (typically the height)
		int maxd = displaySize.height;
		if (maxd > displaySize.width) {
			// the width is smaller so use it
			maxd = displaySize.width;
		}

		final int h = displaySize.height;
		final int w = displaySize.width;
		// compute the default height
		final int th = (int)Math.ceil((double)h * 0.30);
		
		Rectangle bounds = tSet.getBounds();
		if (bounds == null) {
			bounds = new Rectangle(0, 0, w, th);
		}
		TextComponent textComponent = new TextComponent("Text", text, bounds.width, bounds.height, true);
		// general
		textComponent.setX(bounds.x);
		textComponent.setY(bounds.y);
		textComponent.setVisible(true);
		// color
		textComponent.setBackgroundColor(tSet.getBackgroundColor());
		textComponent.setBackgroundColorCompositeType(tSet.getBackgroundColorCompositeType());
		textComponent.setBackgroundColorVisible(tSet.isBackgroundColorVisible());
		// image
		textComponent.setBackgroundImage(tSet.getBackgroundImage());
		textComponent.setBackgroundImageScaleQuality(tSet.getBackgroundImageScaleQuality());
		textComponent.setBackgroundImageScaleType(tSet.getBackgroundImageScaleType());
		textComponent.setBackgroundImageVisible(tSet.isBackgroundImageVisible());
		// text
		textComponent.setTextColor(tSet.getTextColor());
		textComponent.setHorizontalTextAlignment(tSet.getHorizontalTextAlignment());
		textComponent.setVerticalTextAlignment(tSet.getVerticalTextAlignment());
		textComponent.setTextFontScaleType(tSet.getTextFontScaleType());
		textComponent.setTextWrapped(tSet.isTextWrapped());
		textComponent.setPadding(tSet.getPadding());
		textComponent.setTextFont(tSet.getTextFont());
		
		display.setTextComponent(textComponent);
		
		return display;
	}
	
	/**
	 * Creates a new {@link SongDisplay} from the given {@link SongSettings}.
	 * @param settings the settings
	 * @param displaySize the target display size
	 * @return {@link SongDisplay}
	 */
	public static final SongDisplay getDisplay(SongSettings settings, Dimension displaySize) {
		// get the minimum dimension (typically the height)
		int maxd = displaySize.height;
		if (maxd > displaySize.width) {
			// the width is smaller so use it
			maxd = displaySize.width;
		}
		// set the default screen to text component padding
		final int margin = (int)Math.floor((double)maxd * 0.04);
		
		SongDisplay display = new SongDisplay(displaySize);
		
		// get sub settings
		GraphicsComponentSettings<GraphicsComponent> bSet = settings.getBackgroundSettings();
		TextComponentSettings tSet = settings.getTextSettings();
		
		// create a background
		GraphicsComponent background = display.createBackgroundComponent("Background");
		background.setBackgroundColor(bSet.getBackgroundColor());
		background.setBackgroundColorCompositeType(bSet.getBackgroundColorCompositeType());
		background.setBackgroundColorVisible(bSet.isBackgroundColorVisible());
		background.setBackgroundImage(bSet.getBackgroundImage());
		background.setBackgroundImageScaleQuality(bSet.getBackgroundImageScaleQuality());
		background.setBackgroundImageScaleType(bSet.getBackgroundImageScaleType());
		background.setBackgroundImageVisible(bSet.isBackgroundImageVisible());
		background.setVisible(bSet.isVisible());
		
		// compute the default width, height and position
		final int h = displaySize.height - margin * 2;
		final int w = displaySize.width - margin * 2;
		
		// create the title text component
		Rectangle textBounds = tSet.getBounds();
		if (textBounds == null) {
			textBounds = new Rectangle(margin, margin, w, h);
		}
		TextComponent text = new TextComponent("Text", Messages.getString("display.song.defaultText"), textBounds.width, textBounds.height, true);
		// general
		text.setX(textBounds.x);
		text.setY(textBounds.y);
		text.setVisible(tSet.isVisible());
		// color
		text.setBackgroundColor(tSet.getBackgroundColor());
		text.setBackgroundColorCompositeType(tSet.getBackgroundColorCompositeType());
		text.setBackgroundColorVisible(tSet.isBackgroundColorVisible());
		// image
		text.setBackgroundImage(tSet.getBackgroundImage());
		text.setBackgroundImageScaleQuality(tSet.getBackgroundImageScaleQuality());
		text.setBackgroundImageScaleType(tSet.getBackgroundImageScaleType());
		text.setBackgroundImageVisible(tSet.isBackgroundImageVisible());
		// text
		text.setTextColor(tSet.getTextColor());
		text.setHorizontalTextAlignment(tSet.getHorizontalTextAlignment());
		text.setVerticalTextAlignment(tSet.getVerticalTextAlignment());
		text.setTextFontScaleType(tSet.getTextFontScaleType());
		text.setTextWrapped(tSet.isTextWrapped());
		text.setPadding(tSet.getPadding());
		text.setTextFont(tSet.getTextFont());
		
		display.setBackgroundComponent(background);
		display.setTextComponent(text);
		
		return display;
	}
}
