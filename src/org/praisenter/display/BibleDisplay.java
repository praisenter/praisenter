package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.praisenter.data.bible.Verse;

/**
 * Represents a display for showing bible verses.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleDisplay extends FullScreenDisplay {
	/** Additional reference to the title component */
	protected TextComponent scriptureTitleComponent;
	
	/** Additional reference to the text component */
	protected TextComponent scriptureTextComponent;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public BibleDisplay(Dimension displaySize) {
		super(displaySize);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.FullScreenDisplay#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// render the backgrounds
		super.render(graphics);
		// render the text components
		if (this.scriptureTitleComponent != null) {
			this.scriptureTitleComponent.render(graphics);
		}
		if (this.scriptureTextComponent != null) {
			this.scriptureTextComponent.render(graphics);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#invalidate()
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		
		if (this.scriptureTitleComponent != null) {
			this.scriptureTitleComponent.invalidate();
		}
		if (this.scriptureTextComponent != null) {
			this.scriptureTextComponent.invalidate();
		}
	}
	
	/**
	 * Convenience method for setting the texts for the given bible verse.
	 * @param verse the verse
	 */
	public void setVerse(Verse verse) {
		if (this.scriptureTitleComponent != null) {
			this.scriptureTitleComponent.setText(verse.getBook().getName() + " " + verse.getChapter() + ":" + verse.getVerse());
		}
		if (this.scriptureTextComponent != null) {
			this.scriptureTextComponent.setText(verse.getText());
		}
	}
	
	/**
	 * Convenience method for setting the texts for the given bible verse.
	 * <p>
	 * This method allows a verse from multiple bibles to be displayed on
	 * one bible display.
	 * @param verse1 the primary bible verse
	 * @param verse2 the secondary bible verse
	 */
	public void setVerse(Verse verse1, Verse verse2) {
		if (this.scriptureTitleComponent != null) {
			this.scriptureTitleComponent.setText(verse1.getBook().getName() + " " + verse1.getChapter() + ":" + verse1.getVerse());
		}
		if (this.scriptureTextComponent != null) {
			// just split them by 2 new lines
			this.scriptureTextComponent.setText(verse1.getText() + "\n\n" + verse2.getText());
		}
	}
	
	/**
	 * Convenience method for clearing the text of the display.
	 */
	public void clearVerse() {
		if (this.scriptureTitleComponent != null) {
			this.scriptureTitleComponent.setText("");
		}
		if (this.scriptureTextComponent != null) {
			this.scriptureTextComponent.setText("");
		}
	}
	
	/**
	 * Returns the {@link TextComponent} for the scripture title.
	 * @return {@link TextComponent}
	 */
	public TextComponent getScriptureTitleComponent() {
		return this.scriptureTitleComponent;
	}

	/**
	 * Sets the {@link TextComponent} for the scripture title.
	 * @param titleComponent the scripture title component
	 */
	public void setScriptureTitleComponent(TextComponent titleComponent) {
		this.scriptureTitleComponent = titleComponent;
	}
	
	/**
	 * Returns the {@link TextComponent} for the scripture text.
	 * @return {@link TextComponent}
	 */
	public TextComponent getScriptureTextComponent() {
		return this.scriptureTextComponent;
	}
	
	/**
	 * Sets the {@link TextComponent} for the scripture text.
	 * @param textComponent the scripture text component
	 */
	public void setScriptureTextComponent(TextComponent textComponent) {
		this.scriptureTextComponent = textComponent;
	}
}
