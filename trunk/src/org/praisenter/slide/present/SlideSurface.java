package org.praisenter.slide.present;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.praisenter.slide.Slide;

/**
 * Standard interface for rendering slides.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SlideSurface extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 957958229210490257L;

	/**
	 * Default constructor.
	 */
	protected SlideSurface() {
		super();
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}
//	
//	/**
//	 * Renders the given slide to the given image.
//	 * @param slide the slide to render
//	 * @param image the image to render to
//	 */
//	protected static final void renderSlide(Slide slide, BufferedImage image) {
//		// paint the display to the image
//		Graphics2D tg2d = image.createGraphics();
//		// make the rendering the best quality
//		tg2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		tg2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		tg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		tg2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		// set the background color to 100% transparent so that it clears
//		// the image when we call clearRect
//		tg2d.setBackground(new Color(0, 0, 0, 0));
//		tg2d.clearRect(0, 0, image.getWidth(), image.getHeight());
//		slide.render(tg2d);
//		tg2d.dispose();
//	}
	
	/**
	 * Renders the given slide to the given image.
	 * @param slide the slide to render
	 * @param image the image to render to
	 */
	protected static final void renderSlide(SlideRenderer renderer, BufferedImage image) {
		// paint the display to the image
		Graphics2D tg2d = image.createGraphics();
		// make the rendering the best quality
		// FIXME using this causes a major CPU bottleneck and i haven't noticed a huge difference in quality
//		tg2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		tg2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		tg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		tg2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		renderer.render(tg2d);
		tg2d.dispose();
	}
	
	/**
	 * Clears the target image and renders the source image to the target image.
	 * @param source the source image
	 * @param target the target image
	 */
	protected static final void copyImage(BufferedImage source, BufferedImage target) {
		Graphics2D tg2d = target.createGraphics();
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, target.getWidth(), target.getHeight());
		tg2d.drawImage(source, 0, 0, null);
		tg2d.dispose();
	}
	
	/**
	 * Validates the off-screen image is created and sized appropriately (fills the width/height of the given component).
	 * @param image the image to validate
	 * @param component the component to size to
	 * @return BufferedImage
	 */
	protected static final BufferedImage validateOffscreenImage(BufferedImage image, Component component) {
		Dimension size = component.getSize();
		if (image == null || size.width != image.getWidth() || size.height != image.getHeight()) {
			image = component.getGraphicsConfiguration().createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
		}
		return image;
	}
	
	/**
	 * Clears the given image.
	 * @param image the image
	 */
	protected static final void clearImage(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
	}
}
