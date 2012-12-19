package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import org.praisenter.slide.PositionedComponent;
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
	
	// for mouse over
	
	/** The border 1 stroke */
	private static final Stroke BORDER_STROKE_1 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, 0);
	
	/** The border 2 stroke */
	private static final Stroke BORDER_STROKE_2 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, DASH_LENGTH * 2.0f);

	// for selected
	
	/** The border 3 stroke */
	private static final Stroke BORDER_STROKE_3 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH * 6, DASH_SPACE_LENGTH * 6 }, 0);

	/** The border 4 stroke */
	private static final Stroke BORDER_STROKE_4 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH * 6, DASH_SPACE_LENGTH * 6 }, DASH_LENGTH * 12);
	
	/** The current mouse over component */
	protected PositionedComponent mouseOverComponent;
	
	/** The selected component */
	protected PositionedComponent selectedComponent;
	
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
		if (this.mouseOverComponent != null && this.selectedComponent != this.mouseOverComponent) {
			Rectangle r = this.mouseOverComponent.getRectangleBounds();
			// make sure the border is inside the bounds of the rectangle
			// so that the resizing makes more sense
			
			// save the old stroke & clip
			Stroke oStroke = g2d.getStroke();
			Shape clip = g2d.getClip();
			
			// set the clip
			g2d.clipRect(offset.x, offset.y, metrics.width, metrics.height);
			
			int x = (int)Math.floor(r.x * metrics.scale) + HALF_LINE_WIDTH + offset.x + 1;
			int y = (int)Math.floor(r.y * metrics.scale) + HALF_LINE_WIDTH + offset.y + 1;
			int w = (int)Math.ceil(r.width * metrics.scale) - LINE_WIDTH;
			int h = (int)Math.ceil(r.height * metrics.scale) - LINE_WIDTH;
			
			// draw border 1 (this border is for light backgrounds)
			g2d.setStroke(BORDER_STROKE_1);
			g2d.setColor(BORDER_COLOR_1);
			g2d.drawRect(x, y, w, h); 
			
			// draw border 2 (this border is for dark backgrounds)
			g2d.setStroke(BORDER_STROKE_2);
			g2d.setColor(BORDER_COLOR_2);
			g2d.drawRect(x, y, w, h);
			
			g2d.setStroke(oStroke);
			
			g2d.setColor(Color.BLACK);
			g2d.drawString(this.mouseOverComponent.getName(), x + 6, y + 16);
			g2d.setColor(Color.WHITE);
			g2d.drawString(this.mouseOverComponent.getName(), x + 5, y + 15);
			
			g2d.setClip(clip);
		}
		
		// render the border over the normal rendering
		// render the bounds of floating components
		// only draw a border for the hovered component
		if (this.selectedComponent != null) {
			Rectangle r = this.selectedComponent.getRectangleBounds();
			// make sure the border is inside the bounds of the rectangle
			// so that the resizing makes more sense
			
			// save the old stroke & clip
			Stroke oStroke = g2d.getStroke();
			Shape clip = g2d.getClip();
			
			// set the clip
			g2d.clipRect(offset.x, offset.y, metrics.width, metrics.height);
			
			int x = (int)Math.floor(r.x * metrics.scale) + HALF_LINE_WIDTH + offset.x + 1;
			int y = (int)Math.floor(r.y * metrics.scale) + HALF_LINE_WIDTH + offset.y + 1;
			int w = (int)Math.ceil(r.width * metrics.scale) - LINE_WIDTH;
			int h = (int)Math.ceil(r.height * metrics.scale) - LINE_WIDTH;
			
			// draw border 1 (this border is for light backgrounds)
			g2d.setStroke(BORDER_STROKE_3);
			g2d.setColor(BORDER_COLOR_1);
			g2d.drawRect(x, y, w, h); 
			
			// draw border 2 (this border is for dark backgrounds)
			g2d.setStroke(BORDER_STROKE_4);
			g2d.setColor(BORDER_COLOR_2);
			g2d.drawRect(x, y, w, h);
			
			g2d.setStroke(oStroke);
			
			g2d.setColor(Color.BLACK);
			g2d.drawString(this.selectedComponent.getName(), x + 6, y + 16);
			g2d.setColor(Color.WHITE);
			g2d.drawString(this.selectedComponent.getName(), x + 5, y + 15);
			
			g2d.setClip(clip);
		}
	}

	/**
	 * Returns the component which the mouse is currently hovering over.
	 * <p>
	 * Returns null if the mouse is not over a component.
	 * @return {@link PositionedComponent}
	 */
	public PositionedComponent getMouseOverComponent() {
		return this.mouseOverComponent;
	}

	/**
	 * Sets the component the mouse is hovering over.
	 * @param component the component
	 */
	public void setMouseOverComponent(PositionedComponent component) {
		this.mouseOverComponent = component;
	}

	/**
	 * Returns the selected component.
	 * <p>
	 * Returns null if no component is currently selected.
	 * @return {@link PositionedComponent}
	 */
	public PositionedComponent getSelectedComponent() {
		return this.selectedComponent;
	}

	/**
	 * Set the selected component.
	 * @param selectedComponent the component
	 */
	public void setSelectedComponent(PositionedComponent selectedComponent) {
		this.selectedComponent = selectedComponent;
	}
}