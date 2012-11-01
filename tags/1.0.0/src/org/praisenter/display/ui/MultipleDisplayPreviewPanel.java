package org.praisenter.display.ui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.display.Display;

/**
 * Represents a generic multi-display preview panel.
 * @author William Bittle
 * @param <E> the {@link Display} type
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class MultipleDisplayPreviewPanel<E extends Display> extends DisplayPreviewPanel {
	/** The version id */
	private static final long serialVersionUID = 1333771155687039925L;

	/** The displays to render */
	protected List<E> displays;
	
	/** The spacing between the displays */
	protected int innerSpacing;
	
	/**
	 * Full constructor.
	 * @param innerSpacing the spacing between the displays
	 * @param nameSpacing the spacing between the display and its name
	 * @param includeDisplayName true if display names should be rendered
	 */
	public MultipleDisplayPreviewPanel(int innerSpacing, int nameSpacing, boolean includeDisplayName) {
		super(nameSpacing, includeDisplayName);
		this.displays = new ArrayList<E>();
		this.innerSpacing = innerSpacing;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.DisplayPreviewPanel#paintPreview(java.awt.Graphics2D, java.awt.Rectangle)
	 */
	@Override
	protected void paintPreview(Graphics2D g2d, Rectangle bounds) {
		this.renderDisplays(g2d, bounds);
	}
	
	/**
	 * Renders the displays.
	 * @param g2d the graphics object.
	 * @param bounds the total available rendering bounds
	 */
	protected abstract void renderDisplays(Graphics2D g2d, Rectangle bounds);
	
	/**
	 * Returns the display name for the given display
	 * @param display the display
	 * @param index the display index
	 * @return String
	 */
	protected abstract String getDisplayName(E display, int index);
}
