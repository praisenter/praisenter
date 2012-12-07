package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.ui.SlidePreviewMetrics;
import org.praisenter.slide.ui.preview.SingleSlidePreviewPanel;

/**
 * A custom preview panel for editing slides.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideEditorPreviewPanel extends SingleSlidePreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -8164430032153293315L;

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
	protected PositionedSlideComponent mouseOverComponent;
	
	/** The current scale factor */
	protected double scale;
	
	/**
	 * Returns the equivalent point in Slide space from the given
	 * panel space point.
	 * @param panelPoint the panel space point
	 * @return Point
	 */
	public Point getSlideSpacePoint(Point panelPoint) {
		Point point = new Point();
		// translate by this panels insets
		Insets insets = this.getInsets();
		// translate by the shadow width and border width
		Point offset = this.getSlideOffset();
		point.setLocation((panelPoint.x - offset.x - insets.left) / this.scale, (panelPoint.y - offset.y - insets.top) / this.scale);
		return point;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.preview.AbstractSlidePreviewPanel#renderSlide(java.awt.Graphics2D, org.praisenter.slide.Slide, org.praisenter.slide.ui.SlidePreviewMetrics)
	 */
	@Override
	protected void renderSlide(Graphics2D g2d, Slide slide, SlidePreviewMetrics metrics) {
		super.renderSlide(g2d, slide, metrics);
		
		// get the slide offset
		Point offset = this.getSlideOffset();
		
		// set the current scale
		this.scale = metrics.scale;
		
		// render the border over the normal rendering
		// render the bounds of floating components
		// only draw a border for the hovered component
		if (this.mouseOverComponent != null) {
			Shape b = this.mouseOverComponent.getBounds();
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
			g2d.draw(b);
//			g2d.drawRect(
//					(int)Math.ceil(b.x * metrics.scale) + HALF_LINE_WIDTH + offset.x, 
//					(int)Math.ceil(b.y * metrics.scale) + HALF_LINE_WIDTH + offset.y, 
//					(int)Math.ceil(b.width * metrics.scale) - LINE_WIDTH, 
//					(int)Math.ceil(b.height * metrics.scale) - LINE_WIDTH);
			
			// draw border 2 (this border is for dark backgrounds)
			g2d.setStroke(BORDER_STROKE_2);
			g2d.setColor(BORDER_COLOR_2);
			g2d.draw(b);
//			g2d.drawRect(
//					(int)Math.ceil(b.x * metrics.scale) + HALF_LINE_WIDTH + offset.x, 
//					(int)Math.ceil(b.y * metrics.scale) + HALF_LINE_WIDTH + offset.y, 
//					(int)Math.ceil(b.width * metrics.scale) - LINE_WIDTH, 
//					(int)Math.ceil(b.height * metrics.scale) - LINE_WIDTH);
			
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
	 * @return {@link PositionedSlideComponent}
	 */
	public PositionedSlideComponent getMouseOverComponent() {
		return this.mouseOverComponent;
	}

	/**
	 * Sets the component the mouse is hovering over.
	 * @param component the component
	 */
	public void setMouseOverComponent(PositionedSlideComponent component) {
		this.mouseOverComponent = component;
	}
}