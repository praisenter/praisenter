package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.TextComponent;

/**
 * Represents a {@link SlideComponent} that is renderable.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlSeeAlso({ ImageMediaComponent.class, 
			  VideoMediaComponent.class,
			  AudioMediaComponent.class,
			  TextComponent.class,
			  GenericComponent.class })
public interface RenderableComponent extends SlideComponent {
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public RenderableComponent copy();

	/**
	 * Returns the z-ordering of the component in the slide.
	 * @return int
	 */
	public abstract int getOrder();
	
	/**
	 * Sets the z-ordering of the component in the slide.
	 * <p>
	 * If the order is changed using this method, its up to the caller
	 * to ensure that the slide that contains this component is resorted.
	 * @param order the order
	 */
	public abstract void setOrder(int order);
	
	/**
	 * Returns the width of the component in pixels.
	 * @return int
	 */
	public abstract int getWidth();
	
	/**
	 * Returns the height of the component in pixels.
	 * @return int
	 */
	public abstract int getHeight();
	
	/**
	 * Sets the width of this component.
	 * @param width the width in pixels
	 */
	public abstract void setWidth(int width);
	
	/**
	 * Sets the height of this component.
	 * @param height the height in pixels
	 */
	public abstract void setHeight(int height);
	
	/**
	 * Resizes this component using the given deltas.
	 * <p>
	 * Returns a dimension including the amount the component was resized.
	 * This is only relevant when the component has reached its minimum size.
	 * @param dw the change in width in pixels
	 * @param dh the change in height in pixels
	 * @return Dimension
	 */
	public abstract Dimension resize(int dw, int dh);
	
	/**
	 * Resizes this component using the given percentatges.
	 * @param pw the width percentage
	 * @param ph the height percentage
	 */
	public abstract void adjust(double pw, double ph);
	
	// rendering
	
	/**
	 * Renders the preview version of this component.
	 * <p>
	 * Most components will render identically, however, some
	 * like the video component will not.
	 * @param g the graphics object to render to
	 */
	public abstract void renderPreview(Graphics2D g);
	
	/**
	 * Renders the component.
	 * @param g the graphics object to render to
	 */
	public abstract void render(Graphics2D g);
	
	/**
	 * Returns the {@link Fill} used to paint the background.
	 * @return Paint
	 */
	public abstract Fill getBackgroundFill();
	
	/**
	 * Sets the {@link Fill} used to paint the background.
	 * @param fill the fill
	 */
	public abstract void setBackgroundFill(Fill fill);
	
	/**
	 * Returns true if the background is visible (or will be rendered).
	 * @return boolean
	 */
	public abstract boolean isBackgroundVisible();
	
	/**
	 * Sets the background to visible (or rendered).
	 * @param visible true if the background should be rendered
	 */
	public abstract void setBackgroundVisible(boolean visible);
}
