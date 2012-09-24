package org.praisenter.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.display.Display;
import org.praisenter.utilities.ColorUtilities;

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
	
	/** The displays to render */
	private List<Display> displays;
	
	/**
	 * Default constructor.
	 */
	public MultipleDisplayPreviewPanel() {
		this.displays = new ArrayList<Display>();
		
		// add a border to this panel
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		
		// TODO may need to be able to specify a layout or allow scrolling... maybe row/col settings?
		// TODO allow named displays (next/prev/current verses, song part names, etc)
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
			
			double sc = pw < ph ? pw : ph;
			
			w += (int)Math.ceil(sc * (double)size.width);
			int th = (int)Math.ceil(size.height * sc) + SPACING * 4;
			if (h < th) {
				h = th;
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
		
		// the width/height of each display
		final int dw = aw / n;
		final int dh = ah;
		
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
		g2d.translate((aw - w) / 2.0 + SPACING * 2, SPACING * 2);
		
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
		Dimension size = this.getSize();
		
		// paint the background
		Paint oldPaint = g2d.getPaint();
		LinearGradientPaint gradient = new LinearGradientPaint(
				new Point2D.Float(0, 0), 
				new Point2D.Float(0, size.height - 2),
				new float[] { 0.0f, 1.0f },
				new Color[] { this.getBackground(), ColorUtilities.getColor(this.getBackground(), 0.80f) });
		
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, size.width - 3, size.height - 2);
		
		// reset the paint
		g2d.setPaint(oldPaint);
	}
}
