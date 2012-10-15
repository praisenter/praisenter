package org.praisenter.display.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.praisenter.display.Display;
import org.praisenter.images.Images;
import org.praisenter.utilities.FontManager;
import org.praisenter.utilities.ImageUtilities;

/**
 * Generic display preview panel containing the methods required to display
 * a preview of a display.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DisplayPreviewPanel extends JPanel implements ComponentListener {
	/** The version id */
	private static final long serialVersionUID = -5823617514354905867L;

	/** The outer border color */
	protected static final Color OUTER_BORDER_COLOR = Color.GRAY.darker();
	
	/** The inner border color */
	protected static final Color INNER_BORDER_COLOR = Color.WHITE;
	
	/** The outer border width */
	protected static final int OUTER_BORDER_WIDTH = 1;
	
	/** The inner border width */
	protected static final int INNER_BORDER_WIDTH = 1;
	
	/** The total border width */
	protected static final int TOTAL_BORDER_WIDTH = OUTER_BORDER_WIDTH + INNER_BORDER_WIDTH;
	
	/** The shadow width */
	protected static final int SHADOW_WIDTH = 8;
	
	/** The shadow caching prefix */
	protected static final String SHADOW_CACHE_PREFIX = "SHADOW";
	
	/** The transparent background caching prefix */
	protected static final String BACKGROUND_CACHE_PREFIX = "BACKGROUND";
	
	/** The available height for the display text */
	protected static final int TEXT_HEIGHT = 20;
	
	// fields
	
	/** The spacing between the display and the name */
	protected int nameSpacing;
	
	/** True if display names should be included */
	protected boolean includeDisplayName;
	
	// caching

	/** The map of cached shadow images */
	protected Map<String, BufferedImage> shadowImageCache;
	
	/** The map of cached images */
	protected Map<String, BufferedImage> imageCache;
	
	// loading
	
	/** Label for showing a loading animated gif */
	protected JLabel lblLoading;
	
	/** True if a loading animation should be shown rather than the displays */
	private boolean loading;
	
	/**
	 * Full constructor.
	 * @param nameSpacing the spacing between the display and its name
	 * @param includeDisplayName true if the display name should be rendered
	 */
	public DisplayPreviewPanel(int nameSpacing, boolean includeDisplayName) {
		// create the image cache
		this.shadowImageCache = new HashMap<String, BufferedImage>();
		this.imageCache = new HashMap<String, BufferedImage>();
		
		this.nameSpacing = nameSpacing;
		this.includeDisplayName = includeDisplayName;
		
		ImageIcon icon = new ImageIcon(DisplayPreviewPanel.class.getResource("/org/praisenter/icons/loading.gif"));
		this.lblLoading = new JLabel(icon, JLabel.CENTER);
		this.lblLoading.setVisible(false);
		this.lblLoading.setHorizontalAlignment(JLabel.CENTER);
		this.lblLoading.setVerticalAlignment(JLabel.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(this.lblLoading, BorderLayout.CENTER);
		this.loading = false;
		
		// have this panel listen for itself resizing
		this.addComponentListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent event) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent event) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent event) {
		// when the panel is resized we need to clear the shadow image cache
		this.shadowImageCache.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent event) {}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if (!this.loading) {
			Graphics2D g2d = (Graphics2D)graphics;
			
			// setup fast rendering
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			
			// the available rendering width/height
			Rectangle bounds = this.getTotalAvailableRenderingBounds();
			
			this.paintPreview(g2d, bounds);
		}
	}

	/**
	 * Returns the total available bounds for rendering.
	 * @return Rectangle
	 */
	protected Rectangle getTotalAvailableRenderingBounds() {
		Dimension size = this.getSize();
		Insets insets = this.getInsets();
		int w = size.width;
		int h = size.height;
		return new Rectangle(
				insets.left,
				insets.top,
				w - insets.left - insets.right,
				h - insets.top - insets.bottom);
	}
	
	/**
	 * Paints the preview.
	 * @param graphics the grahpics objec to paint to
	 * @param bounds the available paint bounds
	 */
	protected abstract void paintPreview(Graphics2D graphics, Rectangle bounds);
	
	/**
	 * Renders the given display to the given graphics object.
	 * @param g2d the graphics object to render to
	 * @param display the display to render
	 * @param name the display name to render
	 * @param metrics the display preview metrics
	 */
	protected void renderDisplay(Graphics2D g2d, Display display, String name, DisplayPreviewMetrics metrics) {
		final int idw = metrics.width;
		final int idh = metrics.height;
		final int th = metrics.textHeight;
		final double scale = metrics.scale;
		
		// save the old transform
		AffineTransform ot = g2d.getTransform();

		if (this.includeDisplayName && name != null) {
			// render the text
			this.renderDisplayName(g2d, name, idw);

			g2d.translate(0, th);
		}
		
		// render the shadow using the display width/height
		this.renderShadow(g2d, idw + TOTAL_BORDER_WIDTH * 2, idh + TOTAL_BORDER_WIDTH * 2, SHADOW_WIDTH);
		
		// translate
		g2d.translate(SHADOW_WIDTH + TOTAL_BORDER_WIDTH, SHADOW_WIDTH + TOTAL_BORDER_WIDTH);

		// render the transparent background
		this.renderTransparentBackground(g2d, idw, idh);
		
		Shape clip = g2d.getClip();
		g2d.clipRect(0, 0, idw, idh);
		
		// create a scaling transform
		AffineTransform ot2 = g2d.getTransform();
		AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		
		// use the fastest rendering possible
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		
		// apply the new transform
		g2d.transform(at);
		
		// render the display
		display.render(g2d);
		
		g2d.setTransform(ot2);
		g2d.setClip(clip);
		
		// paint the outer border
		g2d.setColor(OUTER_BORDER_COLOR);
		g2d.drawRect(-TOTAL_BORDER_WIDTH, -TOTAL_BORDER_WIDTH, idw + TOTAL_BORDER_WIDTH * 2-1, idh + TOTAL_BORDER_WIDTH * 2-1);
		// paint the inner border
		g2d.setColor(INNER_BORDER_COLOR);
		g2d.drawRect(-INNER_BORDER_WIDTH, -INNER_BORDER_WIDTH, idw + INNER_BORDER_WIDTH * 2-1, idh + INNER_BORDER_WIDTH * 2-1);
		
		// re-apply the old transform
		g2d.setTransform(ot);
	}
	
	/**
	 * Returns the offset of a display.
	 * <p>
	 * The offset is the total border width + the shadow width.
	 * @return Point
	 */
	protected Point getDisplayOffset() {
		return new Point(
				TOTAL_BORDER_WIDTH + SHADOW_WIDTH,
				TOTAL_BORDER_WIDTH + SHADOW_WIDTH);
	}
	
	/**
	 * Returns the actual rendered display metrics.
	 * @param g2d the graphics object
	 * @param display the display
	 * @param adw the available width (for name, shadow, and borders)
	 * @param adh the available height (for name, shadow, and borders)
	 * @return {@link DisplayPreviewMetrics}
	 */
	protected DisplayPreviewMetrics getDisplayMetrics(Graphics2D g2d, Display display, int adw, int adh) {
		int th = 0;
		// get the text height
		if (this.includeDisplayName) {
			th = TEXT_HEIGHT + this.nameSpacing;
		}
		
		// get the real width and height of the display area
		final int border = TOTAL_BORDER_WIDTH * 2;
		final int shadow = SHADOW_WIDTH * 2;
		final double radw = adw - border - shadow;
		final double radh = adh - border - shadow - th;
		
		// get the scaled of the display
		Dimension size = display.getDisplaySize();
		final double sw = radw / size.getWidth();
		final double sh = radh / size.getHeight();
		final double scale = sw < sh ? sw : sh;
		
		// compute the display width and height
		final double dw = scale * size.getWidth();
		final double dh = scale * size.getHeight();
		
		// to get pixel perfect results we need to truncate the image by one to be safe
		final int idw = (int)Math.ceil(dw) - 1;
		final int idh = (int)Math.ceil(dh) - 1;
		
		DisplayPreviewMetrics metrics = new DisplayPreviewMetrics();
		metrics.scale = scale;
		metrics.width = idw;
		metrics.height = idh;
		metrics.textHeight = th;
		metrics.totalWidth = idw + border + shadow;
		metrics.totalHeight = idh + border + shadow + th;
		
		return metrics;
	}
	
	/**
	 * Renders a drop shadow for the given display.
	 * @param g2d the graphics to paint to
	 * @param w the width of the display
	 * @param h the height of the display
	 * @param sw the shadow width
	 */
	private void renderShadow(Graphics2D g2d, int w, int h, int sw) {
		String key = SHADOW_CACHE_PREFIX + "_" + w + "_" + h;
		BufferedImage image = this.shadowImageCache.get(key);
		
		// see if we need to re-render the image
		if (image == null || image.getWidth() < w || image.getHeight() < h) {
			// create a new image of the right size
			image = ImageUtilities.getDropShadowImage(g2d.getDeviceConfiguration(), w, h, sw);
			this.shadowImageCache.put(key, image);
		}
		
		// render the image
		g2d.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Paints a drop shadow for the given display.
	 * @param g2d the graphics to paint to
	 * @param w the width of the display
	 * @param h the height of the display
	 */
	private void renderTransparentBackground(Graphics2D g2d, int w, int h) {
		String key = BACKGROUND_CACHE_PREFIX;
		BufferedImage image = this.imageCache.get(key);
		
		// see if we need to re-render the image
		if (image == null || image.getWidth() < w || image.getHeight() < h) {
			// create a new image of the right size
			image = ImageUtilities.getTiledImage(Images.TRANSPARENT_BACKGROUND, g2d.getDeviceConfiguration(), w, h);
			this.imageCache.put(key, image);
		}
		
		Shape oClip = g2d.getClip();
		g2d.clipRect(0, 0, w, h);
		// render the image
		g2d.drawImage(image, 0, 0, null);
		g2d.setClip(oClip);
	}
	
	/**
	 * Paints the display name at the current position
	 * @param g2d the graphics to paint to
	 * @param name the display name
	 * @param w the width of the display (to center the text)
	 */
	private void renderDisplayName(Graphics2D g2d, String name, int w) {
		BufferedImage image = this.imageCache.get(name);
		FontMetrics metrics = g2d.getFontMetrics(FontManager.getDefaultFont());
		int ih = metrics.getHeight();
		int iw = metrics.stringWidth(name);
		// see if we need to re-render the image
		if (image == null || image.getWidth() != iw || image.getHeight() != ih) {
			// create a new image of the right size
			image = g2d.getDeviceConfiguration().createCompatibleImage(iw, ih, Transparency.BITMASK);
			
			Graphics2D ig2d = image.createGraphics();
			
			ig2d.setFont(FontManager.getDefaultFont());
			ig2d.setColor(Color.BLACK);
			ig2d.drawString(name, 0, metrics.getAscent());
			ig2d.dispose();
			
			this.imageCache.put(name, image);
		}
		
		// render the image
		g2d.drawImage(image, (w - iw) / 2, 0, null);
	}
	
	/**
	 * Sets the loading status of this preview panel.
	 * <p>
	 * Use the {@link #repaint()} method to update the preview without the 
	 * loading animation.
	 * @param flag true if the preview is loading
	 */
	public void setLoading(boolean flag) {
		this.loading = flag;
		this.lblLoading.setVisible(flag);
		this.repaint();
	}
	
	/**
	 * Returns the spacing between the display and its name.
	 * @return int
	 */
	public int getNameSpacing() {
		return nameSpacing;
	}

	/**
	 * Returns true if the display name is rendered.
	 * @return boolean
	 */
	public boolean isIncludeDisplayName() {
		return includeDisplayName;
	}
}
