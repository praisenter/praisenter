package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Represents a display for showing a notification.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationDisplay extends Display {
	/** Additional reference to the text component */
	protected TextComponent textComponent;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public NotificationDisplay(Dimension displaySize) {
		super(displaySize);
	}
	
	/**
	 * Optional constructor.
	 * @param name the dislay name
	 * @param displaySize the target display size
	 */
	public NotificationDisplay(String name, Dimension displaySize) {
		super(name, displaySize);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// render the backgrounds
		super.render(graphics);
		// render the text components
		this.textComponent.render(graphics);
	}
	
	/**
	 * Returns the {@link TextComponent}.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}

	/**
	 * Sets the {@link TextComponent}.
	 * @param textComponent the text component
	 */
	public void setTextComponent(TextComponent textComponent) {
		this.textComponent = textComponent;
	}
}
