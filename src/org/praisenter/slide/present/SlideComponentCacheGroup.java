package org.praisenter.slide.present;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.List;

import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.Slide;

/**
 * Represents a group of {@link RenderableSlideComponent}s that are rendered together
 * to create one cached rendering. This saves a significant amount of time
 * during the rendering process of the entire {@link Slide}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentCacheGroup implements SlideComponentCache {
	/** The list of components in this group */
	protected List<RenderableSlideComponent> components;
	
	/** The cached rendering */
	protected BufferedImage image;
	
	/**
	 * Full constructor.
	 * @param components the components for this group.
	 * @param gc the graphics configuration
	 * @param w the slide width
	 * @param h the slide height
	 */
	public SlideComponentCacheGroup(List<RenderableSlideComponent> components, GraphicsConfiguration gc, int w, int h) {
		this.components = components;
		this.createCachedImage(gc, w, h);
	}
	
	/**
	 * Creates a cached image and renders the components to that image.
	 * @param gc the graphics configuration
	 * @param w the slide width
	 * @param h the slide height
	 */
	private void createCachedImage(GraphicsConfiguration gc, int w, int h) {
		this.image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g = this.image.createGraphics();
		// use best quality
		g.setRenderingHints(SlideRenderer.BEST_QUALITY);
		// renders all the components to the given graphics
		for (RenderableSlideComponent component : this.components) {
			component.render(g);
		}
		g.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.display.SlideComponentCache#render(java.awt.Graphics2D)
	 */
	public void render(Graphics2D g) {
		g.drawImage(this.image, 0, 0, null);
	}
}
