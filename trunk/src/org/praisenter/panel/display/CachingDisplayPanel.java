package org.praisenter.panel.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.praisenter.display.Display;

/**
 * Panel used for display on a selected device.
 * <p>
 * This panel accepts a display and renders it to a local buffered image.
 * From thereon, the image is used to render the panel.  If the underlying
 * display is updated, this panel will not update.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CachingDisplayPanel extends JPanel {
	// TODO this could be an issue when we begin to use video backgrounds
	
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	/** The cached image */
	protected BufferedImage image;
	
	/**
	 * Default constructor.
	 */
	public CachingDisplayPanel() {
		this.image = null;
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}
	
	/**
	 * Sets the display to the given display.
	 * <p>
	 * This will trigger the re-rendering of the display
	 * to the cached image.
	 * @param display the display
	 */
	public void setDisplay(Display display) {
		// if display is null, then clear the image
		if (display == null) {
			this.image = null;
			this.repaint();
			return;
		}
		
		Dimension size = this.getSize();
		// if the image is null or the panel has been resized then we need to create a new compatible image
		if (this.image == null || size.width != this.image.getWidth() || size.height != this.image.getHeight()) {
			// create the image
			this.image = this.getGraphicsConfiguration().createCompatibleImage(this.getSize().width, this.getSize().height, Transparency.TRANSLUCENT);
		}
		
		// paint the display to the image
		Graphics2D tg2d = this.image.createGraphics();
		// make the rendering the best quality
		tg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		tg2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		tg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		tg2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, this.getSize().width, this.getSize().height);
		display.render(tg2d);
		tg2d.dispose();
		
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (this.image != null) {
			g.drawImage(this.image, 0, 0, null);
		}
	}
}
