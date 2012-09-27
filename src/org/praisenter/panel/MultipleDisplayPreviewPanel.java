package org.praisenter.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.display.Display;
import org.praisenter.utilities.FontManager;

/**
 * Represents a panel that shows a preview of multiple displays.
 * <p>
 * This panel will attempt to fit the displays into its size.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MultipleDisplayPreviewPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MultipleDisplayPreviewPanel.class);
	
	/** Spacing between the displays */
	private static final int SPACING = 10;
	
	/** Key to get the cached background image */
	private static final String KEY_BACKGROUND = "key.background";
	
	/** True if the display name should be shown */
	private boolean showDisplayNames;
	
	/** The displays to render */
	private List<Display> displays;
	
	/** The map of cached images */
	private Map<String, BufferedImage> cachedImages; 
	
	/**
	 * Default constructor.
	 */
	public MultipleDisplayPreviewPanel() {
		this(true);
	}
	
	/**
	 * Optional constructor.
	 * @param showDisplayNames true if the display names should be shown
	 */
	public MultipleDisplayPreviewPanel(boolean showDisplayNames) {
		this.showDisplayNames = showDisplayNames;
		this.displays = new ArrayList<Display>();
		this.cachedImages = new HashMap<String, BufferedImage>();
		
		// add a border to this panel
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		
		// TODO may need to be able to specify a layout or allow scrolling... maybe row/col settings?
	}
	
	/**
	 * Adds a display to this preview panel.
	 * @param display the display
	 */
	public void addDisplay(Display display) {
		this.displays.add(display);
	}
	
	/**
	 * Removes the given display from this preview panel and returns
	 * true if successful.
	 * @param display the display
	 * @return boolean
	 */
	public boolean removeDisplay(Display display) {
		this.cachedImages.remove(display.getName());
		return this.displays.remove(display);
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
	 * Computes the size of this component given the maximum
	 * dimension of one display.
	 * @param maximum the maximum dimension of a display
	 * @return Dimension
	 */
	private Dimension getComputedSize(int maximum) {
		int w = SPACING * 4;
		int h = 0;
		
		int n = this.displays.size();
		if (n == 0) return null;
		
		w += SPACING * (n - 1);
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			Dimension size = display.getDisplaySize();
			
			double pw = (double)maximum / (double)size.width;
			double ph = (double)maximum / (double)size.height;
			
			int th = 0;
			if (this.showDisplayNames) {
				Font font = FontManager.getDefaultFont();
				TextLayout layout = new TextLayout(display.getName(), font, new FontRenderContext(new AffineTransform(), true, false));
				th = (int)Math.ceil(layout.getAscent() + layout.getDescent() + layout.getLeading()) + SPACING;
			}
			
			double sc = pw < ph ? pw : ph;
			
			w += (int)Math.ceil(sc * (double)size.width);
			int dh = (int)Math.ceil(size.height * sc) + SPACING * 4 + th;
			if (h < dh) {
				h = dh;
			}
		}
		
		return new Dimension(w, h);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// paint the standard JPanel stuff
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		Dimension size = this.getSize();
		
		long t0 = System.nanoTime();
		
		// paint the background
		this.paintGradientBackground(g2d);
		
		// determine the size of each display
		final int n = this.displays.size();
		
		// the available width/height
		final int aw = size.width - (SPACING) * (n - 1) - SPACING * 4;
		final int ah = size.height - SPACING * 4;
		
		FontMetrics metrics = g.getFontMetrics(FontManager.getDefaultFont());
		int mh = 0; 
		if (this.showDisplayNames) {
			mh = metrics.getHeight();
		}
		
		// the width/height of each display
		final int dw = aw / n;
		final int dh = ah - mh;
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// get the starting x to center the slides
		double w = 0.0;
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			Dimension ds = display.getDisplaySize();
			final int tw = ds.width;
			final int th = ds.height;
			
			final double pw = (double)dw / (double)tw;
			final double ph = (double)dh / (double)th;
			
			// use the most significant scale factor
			final double scale = pw < ph ? pw : ph;
			
			// compute this display's width and add it to the total
			w += tw * scale;
		}
		
		// apply the y translation
		g2d.translate((aw - w) / 2.0 + SPACING * 2, SPACING * 2 + mh);
		
		// preferably the displays are all the same aspect ratio
		// but we can't guarantee it
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			Dimension ds = display.getDisplaySize();
			final int tw = ds.width;
			final int th = ds.height;
			
			final double pw = (double)dw / (double)tw;
			final double ph = (double)dh / (double)th;
			
			// use the most significant scale factor
			final double scale = pw < ph ? pw : ph;
			
			if (this.showDisplayNames) {
				this.paintDisplayName(g2d, display.getName(), (int)Math.ceil(tw * scale));
//				Font oFont = g2d.getFont();
//				g2d.setFont(FontManager.getDefaultFont());
//				g2d.setColor(Color.BLACK);
//				
//				String name = display.getName();
//				g2d.drawString(name, ((int)Math.ceil(tw * scale) - metrics.stringWidth(name)) / 2, -ty);
//				
//				g2d.setFont(oFont);
			}
			
			// the sub old transform
			AffineTransform ot = g2d.getTransform();
			// create a scaling transform
			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
			
			// set the scaling type to fastest
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			// set the rendering type to fastest
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			
			// apply the new transform
			g2d.transform(at);
			
			// draw the display
			display.render(g2d);
			
			// reapply the old transform
			g2d.setTransform(ot);
			
			// apply the x translation of the width
			g2d.translate(tw * scale + SPACING, 0);
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
		
		long t1 = System.nanoTime();
		LOGGER.debug("MultipleDisplayPreviewPanel render time: " + (double)(t1 - t0) / 1000000000.0 + " seconds");
	}
	
	/**
	 * Paints a gradient to the given graphics object.
	 * @param g2d the graphics to paint to
	 */
	private void paintGradientBackground(Graphics2D g2d) {
		BufferedImage image = this.cachedImages.get(KEY_BACKGROUND);
		Dimension size = this.getSize();
		// see if we need to re-render the image
		if (image == null || image.getWidth() != size.width || image.getHeight() != size.height) {
			// create a new image of the right size
			image = g2d.getDeviceConfiguration().createCompatibleImage(size.width, size.height, Transparency.OPAQUE);
			
			Graphics2D ig2d = image.createGraphics();
			Color color1 = new Color(181, 189, 200);
			Color color2 = new Color( 40,  52,  59);
			// paint the background to the image
			LinearGradientPaint gradient = new LinearGradientPaint(
					new Point2D.Float(0, 0), 
					new Point2D.Float(0, size.height - 2),
					new float[] { 0.0f, 1.0f },
					new Color[] { color1, color2 });
			
			ig2d.setPaint(gradient);
			ig2d.fillRect(0, 0, size.width, size.height);
			ig2d.dispose();
			
			this.cachedImages.put(KEY_BACKGROUND, image);
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
	private void paintDisplayName(Graphics2D g2d, String name, int w) {
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
		g2d.drawImage(image, (w - iw) / 2, -(ih + SPACING), null);
	}
}
