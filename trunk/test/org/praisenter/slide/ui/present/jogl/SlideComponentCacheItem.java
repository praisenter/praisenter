package org.praisenter.slide.ui.present.jogl;

import java.awt.Graphics2D;

import org.praisenter.slide.RenderableComponent;

/**
 * Represents a cache for a single {@link RenderableComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentCacheItem implements SlideComponentCache {
	/** The component */
	protected RenderableComponent component;
	
	/**
	 * Full constructor.
	 * @param component the component
	 */
	public SlideComponentCacheItem(RenderableComponent component) {
		this.component = component;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.display.SlideComponentCache#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		if (this.component != null) {
			this.component.render(g);
		}
	}
}
