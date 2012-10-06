package org.praisenter.display.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import org.praisenter.display.Display;

/**
 * Represents a generic single display preview panel.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleDisplayPreviewPanel extends DisplayPreviewPanel {
	/** The version id */
	private static final long serialVersionUID = 1333771155687039925L;

	/** The display to render */
	protected Display display;
	
	/** The display name */
	protected String name;

	/**
	 * Constructor for creating a {@link SingleDisplayPreviewPanel} without
	 * the display name rendered.
	 */
	public SingleDisplayPreviewPanel() {
		super(0, false);
		this.name = null;
		this.display = null;
	}
	
	/**
	 * Constructor for creating a {@link SingleDisplayPreviewPanel} with the display
	 * name rendered.
	 * @param nameSpacing the spacing between the display and its name
	 * @param name the name of the display
	 */
	public SingleDisplayPreviewPanel(int nameSpacing, String name) {
		super(nameSpacing, true);
		this.name = name;
		this.display = null;
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
		
		// the available rendering width/height
		Rectangle bounds = this.getTotalAvailableRenderingBounds();
		
		// call the render displays method passing the total available width and height
		this.renderDisplay(g2d, bounds);
	}
	
	/**
	 * Renders the display.
	 * @param g2d the graphics object.
	 * @param bounds the available rendering bounds
	 */
	protected void renderDisplay(Graphics2D g2d, Rectangle bounds) {
		// get the display metrics
		DisplayPreviewMetrics metrics = this.getDisplayMetrics(g2d, this.display, bounds.width, bounds.height);
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// apply the y translation
		g2d.translate(bounds.x, bounds.y);
		
		// render the display
		this.renderDisplay(g2d, this.display, this.name, metrics);

		// reset the transform
		g2d.setTransform(oldTransform);
	}

	/**
	 * Returns the display.
	 * @return {@link Display}
	 */
	public Display getDisplay() {
		return this.display;
	}
	
	/**
	 * Sets the display to render.
	 * @param display the display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}
}
