package org.praisenter.display.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		Graphics2D g2d = (Graphics2D)graphics;
		
		// setup fast rendering
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		// the available rendering bounds
		Rectangle bounds = this.getTotalAvailableRenderingBounds();
		
		// call the render displays method passing the total available width and height
		this.renderDisplays(g2d, bounds);
	}
	
	/**
	 * Renders the displays.
	 * @param g2d the graphics object.
	 * @param bounds the total available rendering bounds
	 */
	protected abstract void renderDisplays(Graphics2D g2d, Rectangle bounds);
}
