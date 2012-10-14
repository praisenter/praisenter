package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Represents a display for showing songs.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongDisplay extends FullScreenDisplay {
	/** Additional reference to the text component */
	protected TextComponent textComponent;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public SongDisplay(Dimension displaySize) {
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
		if (this.textComponent != null) {
			this.textComponent.render(graphics);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#invalidate()
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		
		if (this.textComponent != null) {
			this.textComponent.invalidate();
		}
	}
	
	/**
	 * Convenience method for setting the text.
	 * @param text the song text
	 */
	public void setSongText(String text) {
		if (this.textComponent != null) {
			this.textComponent.setText(text);
		}
	}
	
	/**
	 * Convenience method for clearing the text of the display.
	 */
	public void clearSongText() {
		if (this.textComponent != null) {
			this.textComponent.setText("");
		}
	}
	
	/**
	 * Returns the {@link TextComponent} for the scripture title.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}

	/**
	 * Sets the {@link TextComponent} for the scripture title.
	 * @param titleComponent the scripture title component
	 */
	public void setTextComponent(TextComponent titleComponent) {
		this.textComponent = titleComponent;
	}
}
