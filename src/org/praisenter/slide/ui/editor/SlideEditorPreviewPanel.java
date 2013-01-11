package org.praisenter.slide.ui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.praisenter.slide.AbstractPositionedSlide;
import org.praisenter.slide.PositionedComponent;
import org.praisenter.slide.RenderableComponent;
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
	
	/** The size of the resize prongs (should be an odd number to center on corners and sides well) */
	protected static final int RESIZE_PRONG_SIZE = 9;
	
	// for mouse over
	
	/** The border 1 stroke */
	private static final Stroke BORDER_STROKE_1 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, 0);
	
	/** The border 2 stroke */
	private static final Stroke BORDER_STROKE_2 = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, Math.max(1.0f, HALF_LINE_WIDTH), new float[] { DASH_LENGTH, DASH_SPACE_LENGTH }, DASH_LENGTH * 2.0f);

	// for selected
	
	/** The current mouse over component */
	protected PositionedComponent mouseOverComponent;
	
	/** The selected component */
	protected PositionedComponent selectedComponent;
	
	/** The selected background component */
	protected RenderableComponent backgroundComponent;
	
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
	
	/**
	 * Returns the prong size in slide space.
	 * @return int
	 */
	public int getProngSize() {
		return (int)Math.round(RESIZE_PRONG_SIZE / this.scale);
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
		
		// we need to make sure we position the borders relative to the slide position
		int sx = 0;
		int sy = 0;
		if (slide instanceof AbstractPositionedSlide) {
			AbstractPositionedSlide pSlide = (AbstractPositionedSlide)slide;
			sx = (int)Math.round(pSlide.getX() * metrics.scale);
			sy = (int)Math.round(pSlide.getY() * metrics.scale);
		}
		
		// render the border over the normal rendering
		// render the bounds of floating components
		// only draw a border for the hovered component
		if (this.mouseOverComponent != null && this.selectedComponent != this.mouseOverComponent) {
			Rectangle r = this.mouseOverComponent.getRectangleBounds();
			// make sure the border is inside the bounds of the rectangle
			// so that the resizing makes more sense
			
			// save the old stroke & clip
			Shape clip = g2d.getClip();
			
			// set the clip
			g2d.clipRect(offset.x, offset.y, metrics.width, metrics.height);
			
			int x = (int)Math.floor(r.x * metrics.scale) + HALF_LINE_WIDTH + offset.x + 1;
			int y = (int)Math.floor(r.y * metrics.scale) + HALF_LINE_WIDTH + offset.y + 1;
			int w = (int)Math.ceil(r.width * metrics.scale) - LINE_WIDTH;
			int h = (int)Math.ceil(r.height * metrics.scale) - LINE_WIDTH;
			
			this.drawBorder(g2d, sx + x, sy + y, w, h);
			
			this.drawName(g2d, this.mouseOverComponent.getName(), sx + x, sy + y);
			
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
			Shape clip = g2d.getClip();
			
			// set the clip
			g2d.clipRect(offset.x, offset.y, metrics.width, metrics.height);
			
			int x = (int)Math.floor(r.x * metrics.scale) + HALF_LINE_WIDTH + offset.x + 1;
			int y = (int)Math.floor(r.y * metrics.scale) + HALF_LINE_WIDTH + offset.y + 1;
			int w = (int)Math.ceil(r.width * metrics.scale) - LINE_WIDTH;
			int h = (int)Math.ceil(r.height * metrics.scale) - LINE_WIDTH;
			
			this.drawBorder(g2d, sx + x, sy + y, w, h);
			
			this.drawProngs(g2d, sx + x, sy + y, w, h);
			
			this.drawName(g2d, this.selectedComponent.getName(), sx + x, sy + y);
			
			g2d.setClip(clip);
		}
		
		if (this.backgroundComponent != null) {
			// make sure the border is inside the bounds of the rectangle
			// so that the resizing makes more sense
			
			// save the old stroke & clip
			Shape clip = g2d.getClip();
			
			// set the clip; clip by the bounds of this panel rather than the offset bounds
			g2d.clipRect(0, 0, this.getWidth(), this.getHeight());
			
			int x = HALF_LINE_WIDTH + offset.x + 1;
			int y = HALF_LINE_WIDTH + offset.y + 1;
			int w = (int)Math.ceil(slide.getWidth() * metrics.scale) - LINE_WIDTH - 1;
			int h = (int)Math.ceil(slide.getHeight() * metrics.scale) - LINE_WIDTH - 1;
			
			this.drawBorder(g2d, sx + x, sy + y, w, h);
			
			// if the slide is a positioned slide or the selected component is not the
			// background of a normal slide, then show the resize prongs
			if (slide instanceof AbstractPositionedSlide) {
				this.drawProngs(g2d, sx + x, sy + y, w, h);
			}
			
			this.drawName(g2d, this.backgroundComponent.getName(), sx + x, sy + y);
			
			g2d.setClip(clip);
		}
	}
	
	/**
	 * Draws the selection/hover border on the given rectangle.
	 * @param g2d  the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	private void drawBorder(Graphics2D g2d, int x, int y, int w, int h) {
		Stroke oStroke = g2d.getStroke();
		
		// draw border 1 (this border is for light backgrounds)
		g2d.setStroke(BORDER_STROKE_1);
		g2d.setColor(BORDER_COLOR_1);
		g2d.drawRect(x, y, w, h); 
		
		// draw border 2 (this border is for dark backgrounds)
		g2d.setStroke(BORDER_STROKE_2);
		g2d.setColor(BORDER_COLOR_2);
		g2d.drawRect(x, y, w, h);

		g2d.setStroke(oStroke);
	}
	
	/**
	 * Draws the 8 resize prongs around the given rectangle.
	 * @param g2d  the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 */
	private void drawProngs(Graphics2D g2d, int x, int y, int w, int h) {
		int rps = RESIZE_PRONG_SIZE;
		int hrps = (RESIZE_PRONG_SIZE + 1) / 2;
		
		// prong backgrounds
		g2d.setColor(new Color(255, 255, 255, 170));
		
		// bottom-right
		g2d.fillRect(x + w - hrps, y + h - hrps, rps, rps);
		// bottom
		g2d.fillRect(x + w / 2 - hrps, y + h - hrps, rps, rps);
		// bottom-left
		g2d.fillRect(x - hrps, y + h - hrps, rps, rps);
		// left
		g2d.fillRect(x - hrps, y + h / 2 - hrps, rps, rps);
		// top-left
		g2d.fillRect(x - hrps, y - hrps, rps, rps);
		// top
		g2d.fillRect(x + w / 2 - hrps, y - hrps, rps, rps);
		// top-right
		g2d.fillRect(x + w - hrps, y - hrps, rps, rps);
		// right
		g2d.fillRect(x + w - hrps, y + h / 2 - hrps, rps, rps);
		
		// prong borders
		g2d.setColor(Color.DARK_GRAY);
		
		// bottom-right
		g2d.drawRect(x + w - hrps, y + h - hrps, rps, rps);
		// bottom
		g2d.drawRect(x + w / 2 - hrps, y + h - hrps, rps, rps);
		// bottom-left
		g2d.drawRect(x - hrps, y + h - hrps, rps, rps);
		// left
		g2d.drawRect(x - hrps, y + h / 2 - hrps, rps, rps);
		// top-left
		g2d.drawRect(x - hrps, y - hrps, rps, rps);
		// top
		g2d.drawRect(x + w / 2 - hrps, y - hrps, rps, rps);
		// top-right
		g2d.drawRect(x + w - hrps, y - hrps, rps, rps);
		// right
		g2d.drawRect(x + w - hrps, y + h / 2 - hrps, rps, rps);
	}
	
	/**
	 * Draws the given name at the given location where x,y represent the top
	 * left corner of the text bounds.
	 * @param g2d the graphics object to render to
	 * @param name the name to render
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void drawName(Graphics2D g2d, String name, int x, int y) {
		final int padding = 6;
		FontMetrics metrics = g2d.getFontMetrics();
		
		g2d.setColor(new Color(0, 0, 0, 150));
		Rectangle2D r = metrics.getStringBounds(name, g2d);
		g2d.fillRect(x, y, (int)Math.ceil(r.getWidth()) + 2 * padding, (int)Math.ceil(r.getHeight()) + 2 * padding - metrics.getDescent());
		
		x += padding;
		y += metrics.getAscent() + padding - metrics.getDescent();
		
		g2d.setColor(Color.WHITE);
		g2d.drawString(name, x, y);
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

	/**
	 * Returns the background component.
	 * <p>
	 * Returns null if the background component is not currently selected.
	 * @return {@link RenderableComponent}
	 */
	public RenderableComponent getSelectedBackgroundComponent() {
		return this.backgroundComponent;
	}

	/**
	 * Sets the selected background component.
	 * @param background the background component
	 */
	public void setSelectedBackgroundComponent(RenderableComponent background) {
		this.backgroundComponent = background;
	}
}