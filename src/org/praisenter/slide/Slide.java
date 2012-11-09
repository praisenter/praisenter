package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.TimedMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents a slide with graphics, text, etc.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "Slide")
public class Slide {
	/** The width of the slide */
	@XmlAttribute(name = "Width", required = true)
	protected int width;
	
	/** The height of the slide */
	@XmlAttribute(name = "Height", required = true)
	protected int height;
	
	/** The slide background */
	@XmlElement(name = "Background", required = false, nillable = true)
	protected RenderableSlideComponent background;
	
	/** The slide components */
	@XmlElement(name = "Component", required = false, nillable = true)
	protected List<SlideComponent> components;
	
	/**
	 * Minimal constructor.
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public Slide(int width, int height) {
		this.width = width;
		this.height = height;
		this.background = null;
		this.components = new ArrayList<SlideComponent>();
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public Slide(Slide slide) {
		this.width = slide.width;
		this.height = slide.height;
		this.components = new ArrayList<SlideComponent>();
		// the background
		if (slide.background != null) {
			this.background = slide.background.copy();
		}
		// the components
		for (SlideComponent component : slide.components) {
			this.components.add(component.copy());
		}
	}
	
	/**
	 * Returns a deep copy of this {@link Slide}.
	 * @return {@link Slide}
	 */
	public Slide copy() {
		return new Slide(this);
	}
	
	// rendering
	
	/**
	 * Renders a preview of this slide.
	 * @param g the graphics object to render to
	 */
	public void renderPreview(Graphics2D g) {
		if (this.background != null) {
			this.background.renderPreview(g);
		}
		
		// TODO resort the list by order
		for (SlideComponent component : this.components) {
			if (component instanceof RenderableSlideComponent) {
				RenderableSlideComponent renderable = (RenderableSlideComponent)component;
				renderable.renderPreview(g);
			}
		}
	}
	
	/**
	 * Renders the current state of this slide.
	 * @param g the graphics object to render to
	 */
	public void render(Graphics2D g) {
		if (this.background != null) {
			this.background.render(g);
		}
		
		// TODO resort the list by order
		for (SlideComponent component : this.components) {
			if (component instanceof RenderableSlideComponent) {
				RenderableSlideComponent renderable = (RenderableSlideComponent)component;
				renderable.render(g);
			}
		}
	}
	
	// modification

	/**
	 * Returns a new {@link ImageMediaComponent} with the given image media.
	 * @param media the image media
	 * @return {@link ImageMediaComponent}
	 */
	public ImageMediaComponent createImageBackgroundComponent(ImageMedia media) {
		ImageMediaComponent component = new ImageMediaComponent(media, 0, 0, this.width, this.height);
		// setup all the other properties
		this.setupBackgroundComponent(component);
		
		return component;
	}
	
	/**
	 * Returns a new {@link VideoMediaComponent} with the given video media.
	 * @param media the video media
	 * @return {@link VideoMediaComponent}
	 */
	public VideoMediaComponent createVideoBackgroundComponent(AbstractVideoMedia media) {
		VideoMediaComponent component = new VideoMediaComponent(media, 0, 0, this.width, this.height);
		// setup all the other properties
		this.setupBackgroundComponent(component);
		// since videos are opaque don't render the background
		component.setBackgroundPaintVisible(false);
		component.setBackgroundPaint(null);
		
		return component;
	}
	
	/**
	 * Returns a new {@link GenericSlideComponent} with the given paint as the
	 * background.
	 * <p>
	 * The paint can be a solid color or gradient or any other type of paint.
	 * @param paint the paint
	 * @return {@link GenericSlideComponent}
	 */
	public GenericSlideComponent createPaintBackgroundComponent(Paint paint) {
		GenericSlideComponent component = new GenericSlideComponent(0, 0, this.width, this.height);
		// set the media
		component.setBackgroundPaint(paint);
		component.setBackgroundPaintVisible(true);
		// setup all the other properties
		this.setupBackgroundComponent(component);
		
		return component;
	}
	
	/**
	 * Setups up the properties for a background component.
	 * @param component the component
	 */
	private void setupBackgroundComponent(GenericSlideComponent component) {
		// no border on backgrounds
		component.setBorderVisible(false);
		component.setBorderPaint(null);
		component.setBorderStroke(null);
	}
	
	/**
	 * Returns the background component.
	 * <p>
	 * This can be any type of component, even a {@link RenderableSlideComponent}. In this
	 * case the position should be 0,0. The width/height should also match the slide
	 * width/height.
	 * @see #createImageBackgroundComponent(ImageMedia)
	 * @see #createPaintBackgroundComponent(Paint)
	 * @see #createVideoBackgroundComponent(AbstractVideoMedia)
	 * @return {@link SlideComponent}
	 */
	public RenderableSlideComponent getBackground() {
		return this.background;
	}
	
	/**
	 * Sets the background to the given component.
	 * @see #createImageBackgroundComponent(ImageMedia)
	 * @see #createPaintBackgroundComponent(Paint)
	 * @see #createVideoBackgroundComponent(AbstractVideoMedia)
	 * @param component the background component
	 */
	public void setBackground(RenderableSlideComponent component) {
		this.background = component;
	}
	
	/**
	 * Adds the given component.
	 * @param component the component to add
	 */
	public void addComponent(SlideComponent component) {
		this.components.add(component);
	}
	
	/**
	 * Removes the given component.
	 * @param component the component to remove
	 * @return boolean true if the component was removed
	 */
	public boolean removeComponent(SlideComponent component) {
		return this.components.remove(component);
	}
	
	/**
	 * Returns the number of components on this slide.
	 * <p>
	 * This does not include the background component or any specialized components.
	 * <p>
	 * Used with {@link #getComponent(int)}, you can iterate over all the components
	 * in the components list.
	 * @return int
	 */
	public int getComponentCount() {
		return this.components.size();
	}
	
	/**
	 * Returns the component at the given index.
	 * @param i the component index
	 * @return {@link SlideComponent}
	 * @throws IndexOutOfBoundsException thrown if i is less than zero or greater than the size
	 */
	public SlideComponent getComponent(int i) {
		return this.components.get(i);
	}
	
	/**
	 * Returns a list of the given component type.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	/**
	 * Returns all the {@link TimedMediaComponent}s on this {@link Slide}.
	 * <p>
	 * This is useful for display of the slide to being/end media playback.
	 * @return List&lt;{@link TimedMediaComponent}&gt;
	 */
	public List<TimedMediaComponent<?>> getTimedMediaComponents() {
		List<TimedMediaComponent<?>> components = new ArrayList<TimedMediaComponent<?>>();
		for (SlideComponent component : this.components) {
			if (TimedMediaComponent.class.isInstance(component)) {
				components.add((TimedMediaComponent<?>)component);
			}
		}
		return components;
	}
	
	/**
	 * Returns the width of this slide in pixels.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Sets the width of this slide.
	 * <p>
	 * This method will also modify the width of the background component to
	 * match, if it's set.
	 * @param width the width in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
		if (this.background != null) {
			this.background.setWidth(width);
		}
	}

	/**
	 * Returns the height of this slide in pixels.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Sets the height of this slide.
	 * <p>
	 * This method will also modify the height of the background component to
	 * match, if it's set.
	 * @param height the height in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
		if (this.background != null) {
			this.background.setHeight(height);
		}
	}
	
	/**
	 * Creates a new thumbnail for this slide using the given size.
	 * @param size the size of the thumbnail
	 * @return BufferedImage
	 */
	public BufferedImage getThumbnail(Dimension size) {
		// render the slide to a buffered image of the right size
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		this.renderPreview(g);
		g.dispose();
		// scale the composite down
		image = ImageUtilities.getUniformScaledImage(image, size.width, size.height, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		// return it
		return image;
	}
}
