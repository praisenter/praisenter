package org.praisenter.display.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.praisenter.display.BibleDisplay;
import org.praisenter.display.Display;
import org.praisenter.images.Images;
import org.praisenter.utilities.FontManager;
import org.praisenter.utilities.ImageUtilities;

public abstract class DisplayPreviewPanel extends JPanel {
	protected static final Color OUTER_BORDER_COLOR = Color.GRAY.darker();
	protected static final Color INNER_BORDER_COLOR = Color.WHITE;
	protected static final int OUTER_BORDER_WIDTH = 1;
	protected static final int INNER_BORDER_WIDTH = 1;
	protected static final int TOTAL_BORDER_WIDTH = OUTER_BORDER_WIDTH + INNER_BORDER_WIDTH;
	protected static final int SHADOW_WIDTH = 6;
	protected static final String SHADOW_CACHE_PREFIX = "SHADOW";
	protected static final String BACKGROUND_CACHE_PREFIX = "BACKGROUND";
	
	/** The displays to render */
	protected List<BibleDisplay> displays;
	
	/** The spacing between the displays */
	protected int innerSpacing;
	
	protected int nameSpacing;
	protected boolean includeDisplayNames;
	
	// caching
	
	/** The map of cached images */
	protected Map<String, BufferedImage> cachedImages;
	
	public DisplayPreviewPanel() {
		this.displays = new ArrayList<BibleDisplay>();
		this.cachedImages = new HashMap<String, BufferedImage>();
		this.innerSpacing = 20;
		this.nameSpacing = 5;
		this.includeDisplayNames = true;
	}

	/**
	 * Sets the minimum size of this component to the computed
	 * size using the given maximum display dimension.
	 * @param maximum the maximum dimension of a display
	 */
	public void setMinimumSize(int maximum) {
		Dimension size = this.getComputedSize(maximum);
		if (size != null) {
			this.setMinimumSize(size);
		}
	}
	
	/**
	 * Returns a dimension that will fit the displays given the maximum
	 * display dimension.
	 * @param maximum the maximum display dimension
	 * @return Dimension
	 */
	protected Dimension getComputedSize(int maximum) {
		int n = this.displays.size();

		// FIXME what should we return here?
		// FIXME this is only for inline display
		if (n == 0) return null;
		
		Insets insets = this.getInsets();
		
		double m = maximum;
		double w = insets.left + insets.right;
		double h = 0;
		
		// loop over the displays
		for (int i = 0; i < n; i++) {
			// get the display size
			Display display = this.displays.get(i);
			Dimension size = display.getDisplaySize();
			
			// compute the width/height scales
			double pw = m / size.getWidth();
			double ph = m / size.getHeight();
			
			// use the scaling factor that will scale the most
			double sc = pw < ph ? pw : ph;
			
			// compute the width of the display with all the features
			double dw = sc * size.getWidth();
			double dh = sc * size.getHeight();
			// increment the width
			w += dw;
			// only choose the largest height
			if (dh > h) {
				h = dh;
			}
		}
		
		h += insets.top + insets.bottom;
		if (this.includeDisplayNames) {
			Rectangle2D bounds = FontManager.getDefaultFont().getMaxCharBounds(new FontRenderContext(new AffineTransform(), true, false));
			h += bounds.getHeight() + this.nameSpacing;
		}
		
		return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		Graphics2D g2d = (Graphics2D)graphics;
		Dimension size = this.getSize();
		Insets insets = this.getInsets();
		
		// determine the size of each display
		final int n = this.displays.size();
		
		// the available rendering width/height
		final int taw = size.width - insets.left - insets.right - this.innerSpacing * (n - 1);
		final int tah = size.height - insets.top - insets.bottom;
		int x = insets.left;
		int y = insets.top;
		
		this.renderDisplays(g2d, x, y, taw, tah);
	}
	
	protected abstract void renderDisplays(Graphics2D g2d, int x, int y, int taw, int tah);
	
	/**
	 * Renders the given display to the given graphics object.
	 * <p>
	 * The available width and height are used to render the display name, shadows, and borders.
	 * <p>
	 * The full available width and height may not be used, so this method returns the computed 
	 * integer bounds of the used area.
	 * @param g2d the graphics object to render to
	 * @param display the display to render
	 * @param name the display name to render
	 * @param adw the available display width
	 * @param adh the available display height
	 * @return Rectangle the bounds of the rendered display
	 */
	protected Rectangle renderDisplay(Graphics2D g2d, Display display, String name, double adw, double adh) {
		// get the width and height of the actual display
		double radw = adw - TOTAL_BORDER_WIDTH * 2.0 - SHADOW_WIDTH * 2.0;
		double radh = adh - TOTAL_BORDER_WIDTH * 2.0 - SHADOW_WIDTH * 2.0;
		
		// get the scaled of the display
		Dimension size = display.getDisplaySize();
		double sw = radw / size.getWidth();
		double sh = radh / size.getHeight();
		double scale = sw < sh ? sw : sh;
		
		// compute the display width and height
		double dw = scale * size.getWidth();
		double dh = scale * size.getHeight();
		
		// to get pixel perfect results we need to truncate the image by one to be safe
		int idw = (int)Math.ceil(dw) - 1;
		int idh = (int)Math.ceil(dh) - 1;
		
		// save the old transform
		AffineTransform ot = g2d.getTransform();

		if (this.includeDisplayNames) {
			// render the text
			this.renderDisplayName(g2d, name, idw);
			
			// translate
			FontMetrics metrics = g2d.getFontMetrics();
			int th = metrics.getMaxAscent() + metrics.getMaxDescent() + metrics.getLeading();
			g2d.translate(0, th + this.nameSpacing);
		}
		
		// render the shadow using the display width/height
		this.renderShadow(g2d, idw + TOTAL_BORDER_WIDTH * 2, idh + TOTAL_BORDER_WIDTH * 2, SHADOW_WIDTH);
		
		// translate
		g2d.translate(SHADOW_WIDTH + TOTAL_BORDER_WIDTH, SHADOW_WIDTH + TOTAL_BORDER_WIDTH);

		// render the transparent background
		this.renderTransparentBackground(g2d, idw, idh);
		
		Shape clip = g2d.getClip();
		g2d.setClip(0, 0, idw, idh);
		
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
		
		return new Rectangle(0, 0, idw, idh);
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
		BufferedImage image = this.cachedImages.get(key);
		
		// see if we need to re-render the image
		if (image == null || image.getWidth() < w || image.getHeight() < h) {
			// create a new image of the right size
			image = ImageUtilities.getDropShadowImage(g2d.getDeviceConfiguration(), w, h, sw);
			this.cachedImages.put(key, image);
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
		String key = BACKGROUND_CACHE_PREFIX + "_" + w + "_" + h;
		BufferedImage image = this.cachedImages.get(key);
		
		// see if we need to re-render the image
		if (image == null || image.getWidth() < w || image.getHeight() < h) {
			// create a new image of the right size
			image = ImageUtilities.getTiledImage(Images.TRANSPARENT_BACKGROUND, g2d.getDeviceConfiguration(), w, h);
			this.cachedImages.put(key, image);
		}
		
		// render the image
		g2d.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Paints the display name at the current position
	 * @param g2d the graphics to paint to
	 * @param name the display name
	 * @param w the width of the display (to center the text)
	 */
	private void renderDisplayName(Graphics2D g2d, String name, int w) {
		BufferedImage image = this.cachedImages.get(name);
		FontMetrics metrics = g2d.getFontMetrics(FontManager.getDefaultFont());
		int ih = metrics.getHeight();
		int iw = metrics.stringWidth(name);
		// see if we need to re-render the image
		if (image == null || image.getWidth() != iw || image.getHeight() != ih) {
			// create a new image of the right size
			image = g2d.getDeviceConfiguration().createCompatibleImage(iw, ih, Transparency.TRANSLUCENT);
			
			Graphics2D ig2d = image.createGraphics();
			
			ig2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			ig2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			ig2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			ig2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			ig2d.setFont(FontManager.getDefaultFont());
			ig2d.setColor(Color.BLACK);
			ig2d.drawString(name, 0, metrics.getAscent());
			ig2d.dispose();
			
			this.cachedImages.put(name, image);
		}
		
		// render the image
		g2d.drawImage(image, (w - iw) / 2, 0, null);
	}
}
