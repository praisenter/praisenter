package org.praisenter.display.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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
import org.praisenter.resources.Messages;
import org.praisenter.utilities.FontManager;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents a panel that shows a preview of bible displays
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class InlineDisplayPreviewPanel extends DisplayPreviewPanel {
	/** The version id */
	private static final long serialVersionUID = -6376569581892016128L;
	
	/** The previous, current, and next verse display names */
	private static final String[] DISPLAY_NAMES = new String[] {
		Messages.getString("panel.bible.preview.previous"),
		Messages.getString("panel.bible.preview.current"),
		Messages.getString("panel.bible.preview.next")
	};
	
	/**
	 * Default constructor.
	 */
	public InlineDisplayPreviewPanel() {
		this(true);
	}
	
	/**
	 * Optional constructor.
	 * @param showDisplayNames true if the display names should be shown
	 */
	public InlineDisplayPreviewPanel(boolean showDisplayNames) {
		super();
		
		this.displays.add(null);
		this.displays.add(null);
		this.displays.add(null);
	}
	
	/**
	 * Sets the previous verse display.
	 * @param display the display
	 */
	public void setPreviousVerseDisplay(BibleDisplay display) {
		this.displays.set(0, display);
	}
	
	/**
	 * Sets the current verse display.
	 * @param display the display
	 */
	public void setCurrentVerseDisplay(BibleDisplay display) {
		this.displays.set(1, display);
	}
	
	/**
	 * Sets the next verse display.
	 * @param display the display
	 */
	public void setNextVerseDisplay(BibleDisplay display) {
		this.displays.set(2, display);
	}
	
	/**
	 * Returns the previous verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getPreviousVerseDisplay() {
		return this.displays.get(0);
	}
	
	/**
	 * Returns the current verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getCurrentVerseDisplay() {
		return this.displays.get(1);	
	}
	
	/**
	 * Returns the next verse display.
	 * @return {@link BibleDisplay}
	 */
	public BibleDisplay getNextVerseDisplay() {
		return this.displays.get(2);
	}
	
	@Override
	protected void renderDisplays(Graphics2D g2d, int x, int y, int taw, int tah) {
		int n = this.displays.size();
		
		Rectangle2D textBounds = FontManager.getDefaultFont().getMaxCharBounds(g2d.getFontRenderContext());
		int mh = 0;
		if (this.includeDisplayNames) {
			mh = (int)Math.ceil(textBounds.getHeight()) + this.nameSpacing;
		}
		
		// the width/height of each display
		final int adw = taw / n;
		final int adh = tah - mh;
		
		// save the old transform
		AffineTransform oldTransform = g2d.getTransform();
		
		// get the starting x to center the slides
		double w = this.getTotalWidth(adw, adh);
		
		// apply the y translation
		g2d.translate(x + (this.getSize().getWidth() - w) * 0.5, y);
		
		// preferably the displays are all the same aspect ratio
		// but we can't guarantee it
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			
			Rectangle bounds = this.renderDisplay(g2d, display, DISPLAY_NAMES[i], adw, adh);
			
			// apply the x translation of the width
			g2d.translate(bounds.width + this.innerSpacing, 0);
		}
		
		// reset the transform
		g2d.setTransform(oldTransform);
	}
	
	protected double getTotalWidth(int aw, int ah) {
		int n = this.displays.size();
		double w = this.innerSpacing * (n - 1);
		for (int i = 0; i < n; i++) {
			Display display = this.displays.get(i);
			Dimension ds = display.getDisplaySize();
			final int dw = ds.width;
			final int dh = ds.height;
			
			final double pw = (double)aw / (double)dw;
			final double ph = (double)ah / (double)dh;
			
			// use the most significant scale factor
			final double scale = pw < ph ? pw : ph;
			
			// compute this display's width and add it to the total
			w += dw * scale;
		}
		return w;
	}
}
