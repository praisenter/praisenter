package org.praisenter.display;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.util.UUID;

import org.praisenter.settings.GeneralSettings;

/**
 * An abstract component used on a {@link Display}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DisplayComponent {
	/** True if this component has been changed and needs to be updated */
	private boolean dirty = true;

	/** The unique component id */
	private final String id = UUID.randomUUID().toString();
	
	/** The component name */
	private String name;

	/** True if this component should be visible */
	protected boolean visible;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 */
	public DisplayComponent(String name) {
		this.name = name;
		this.visible = true;
	}
	
	/**
	 * Method to set the render quality on the given graphics object.
	 * @param graphics the graphics
	 */
	protected static final void setRenderQuality(Graphics2D graphics) {
		// setup the render quality as high as possible
		RenderQuality quality = GeneralSettings.getInstance().getRenderQuality();
		if (quality == RenderQuality.HIGH) {
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		} else if (quality == RenderQuality.MEDIUM) {
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
			graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		} else {
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		}
	}
	
	/**
	 * Renders the component to the given graphics object.
	 * @param graphics the graphics object to render to
	 */
	public abstract void render(Graphics2D graphics);
	
	/**
	 * Renders the component to the given graphics object but uses the given graphics configuration to
	 * generate the graphics.
	 * <p>
	 * This method is useful when the component is rendered to one configuration, but needs to be cached
	 * for speed for another configuration.
	 * @param graphics the graphics to render to
	 * @param configuration the target graphics configuration
	 */
	public abstract void render(Graphics2D graphics, GraphicsConfiguration configuration);
	
	/**
	 * Prepares the display component for rendering using the given graphics configuration.
	 * <p>
	 * Use this method to prepare the component for display on a different thread.
	 * @param configuration the target graphics configuration
	 */
	public abstract void prepare(GraphicsConfiguration configuration);
	
	/**
	 * Renders the graphics component to the given graphics object.
	 * <p>
	 * Override this method to render additional graphics (be sure to call the super method).
	 * @param graphics the graphics object to render to
	 */
	protected abstract void renderComponent(Graphics2D graphics);
	
	/**
	 * Invalidates any cached resources.
	 */
	public void invalidate() {
		this.dirty = true;
	}
	
	/**
	 * Returns true if this component has been changed 
	 * and needs to be updated.
	 * @return boolean
	 */
	protected boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * Sets this component to be marked as dirty.  This tells the
	 * component that any cache resources must be updated.
	 * @param flag true if this component should be marked as dirty
	 */
	protected void setDirty(boolean flag) {
		this.dirty = flag;
	}
	
	/**
	 * Returns the unique id for this component.
	 * @return String
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Returns the user friendly name for this component.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true if this component is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Sets this component to visible or invisible.
	 * @param visible visible or not visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
