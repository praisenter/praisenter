package org.praisenter.settings.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import org.praisenter.display.Display;
import org.praisenter.display.GraphicsComponent;
import org.praisenter.display.ui.DisplayPreviewMetrics;
import org.praisenter.display.ui.SingleDisplayPreviewPanel;

/**
 * A custom preview panel for display settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DisplaySettingsPreviewPanel extends SingleDisplayPreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -22148146671505388L;

	/** The component hover line width */
	private static final int LINE_WIDTH = 1;
	
	/** Half the component hover line width */
	private static final int HALF_LINE_WIDTH = LINE_WIDTH / 2;
	
	/** The component hover dash length */
	private static final float DASH_LENGTH = 1.0f;
	
	/** The space between the dashes */
	private static final float DASH_SPACE_LENGTH = DASH_LENGTH * 3.0f; 
	
	/** The component hover border color 1 */
	private static final Color BORDER_COLOR_1 = Color.BLACK;
	
	/** The component hover border color 2 */
	private static final Color BORDER_COLOR_2 = Color.WHITE;
	
	/** The border 1 stroke */
	private static final Stroke BORDER_STROKE_1 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, 0);
	
	/** The border 2 stroke */
	private static final Stroke BORDER_STROKE_2 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, DASH_LENGTH * 2.0f);
	
	/** The current mouse over component */
	protected GraphicsComponent mouseOverComponent;
	
	/** The current scale factor */
	protected double scale;
	
	/**
	 * Returns the equivalent point in Display space from the given
	 * panel space point.
	 * @param panelPoint the panel point
	 * @return Point
	 */
	public Point getDisplayPoint(Point panelPoint) {
		Point point = new Point();
		// translate by this panels insets
		Insets insets = this.getInsets();
		// translate by the shadow width and border width
		Point offset = this.getDisplayOffset();
		point.setLocation((panelPoint.x - offset.x - insets.left) / this.scale, (panelPoint.y - offset.y - insets.top) / this.scale);
		return point;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.DisplayPreviewPanel#renderDisplay(java.awt.Graphics2D, org.praisenter.display.Display, java.lang.String, org.praisenter.display.ui.DisplayPreviewMetrics)
	 */
	@Override
	protected void renderDisplay(Graphics2D g2d, Display display, String name, DisplayPreviewMetrics metrics) {
		// perform normal rendering
		super.renderDisplay(g2d, display, name, metrics);
		
		// get the display offset
		Point offset = this.getDisplayOffset();
		
		// set the current scale
		this.scale = metrics.scale;
		
		// render the border over the normal rendering
		// render the bounds of floating components
		// only draw a border for the hovered component
		if (this.mouseOverComponent != null) {
			Rectangle b = this.mouseOverComponent.getBounds();
			// make sure the border is inside the bounds of the rectangle
			// so that the resizing makes more sense
			
			// save the old stroke & clip
			Stroke oStroke = g2d.getStroke();
			Shape clip = g2d.getClip();
			
			// set the clip
			g2d.setClip(offset.x, offset.y, metrics.width, metrics.height);
			
			// draw border 1 (this border is for light backgrounds)
			g2d.setStroke(BORDER_STROKE_1);
			g2d.setColor(BORDER_COLOR_1);
			g2d.drawRect(
					(int)Math.ceil(b.x * metrics.scale) + HALF_LINE_WIDTH + offset.x, 
					(int)Math.ceil(b.y * metrics.scale) + HALF_LINE_WIDTH + offset.y, 
					(int)Math.ceil(b.width * metrics.scale) - LINE_WIDTH, 
					(int)Math.ceil(b.height * metrics.scale) - LINE_WIDTH);
			
			// draw border 2 (this border is for dark backgrounds)
			g2d.setStroke(BORDER_STROKE_2);
			g2d.setColor(BORDER_COLOR_2);
			g2d.drawRect(
					(int)Math.ceil(b.x * metrics.scale) + HALF_LINE_WIDTH + offset.x, 
					(int)Math.ceil(b.y * metrics.scale) + HALF_LINE_WIDTH + offset.y, 
					(int)Math.ceil(b.width * metrics.scale) - LINE_WIDTH, 
					(int)Math.ceil(b.height * metrics.scale) - LINE_WIDTH);
			
			// reset the stroke & clip
			g2d.setClip(clip);
			g2d.setStroke(oStroke);
		}
	}

	/**
	 * Returns the component which the mouse is currently hovering over (or selected).
	 * <p>
	 * Returns null if the mouse is not over a component or does not have a component
	 * selected.
	 * @return {@link GraphicsComponent}
	 */
	public GraphicsComponent getMouseOverComponent() {
		return this.mouseOverComponent;
	}

	/**
	 * Sets the component the mouse is hovering over.
	 * @param component the component
	 */
	public void setMouseOverComponent(GraphicsComponent component) {
		this.mouseOverComponent = component;
	}
}
