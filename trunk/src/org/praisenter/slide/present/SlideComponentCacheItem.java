package org.praisenter.slide.present;

import java.awt.Graphics2D;

import org.praisenter.slide.RenderableSlideComponent;

/**
 * Represents a cache for a single {@link RenderableSlideComponent}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideComponentCacheItem implements SlideComponentCache {
	/** The component */
	protected RenderableSlideComponent component;
	
	/**
	 * Full constructor.
	 * @param component the component
	 */
	public SlideComponentCacheItem(RenderableSlideComponent component) {
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
