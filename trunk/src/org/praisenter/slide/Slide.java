package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.resources.Messages;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents a slide with graphics, text, etc.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "Slide")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ ImageMediaComponent.class, 
	  VideoMediaComponent.class, 
	  TextComponent.class,
	  GenericSlideComponent.class })
public class Slide {
	/** Comparator for sorting by z-order */
	private static final SlideComponentOrderComparator ORDER_COMPARATOR = new SlideComponentOrderComparator();
	
	/** The width of the slide */
	@XmlAttribute(name = "Width", required = true)
	protected int width;
	
	/** The height of the slide */
	@XmlAttribute(name = "Height", required = true)
	protected int height;
	
	/** The slide/template name */
	@XmlElement(name = "Name", required = true, nillable = false)
	protected String name;
	
	/** The slide background */
	@XmlAnyElement(lax = true)
	protected RenderableSlideComponent background;
	
	/** The slide components */
	@XmlElementWrapper(name = "Components")
	@XmlAnyElement(lax = true)
	protected List<SlideComponent> components;

	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected Slide() {}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the slide/template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public Slide(String name, int width, int height) {
		this.name = name;
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
		this.name = slide.name;
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
	
	/**
	 * Returns a template for this slide.
	 * @return {@link Template}
	 */
	public Template createTemplate() {
		return new SlideTemplate(this);
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
		for (RenderableSlideComponent component : this.getComponents(RenderableSlideComponent.class)) {
			component.renderPreview(g);
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
		for (RenderableSlideComponent component : this.getComponents(RenderableSlideComponent.class)) {
			component.render(g);
		}
	}
	
	// modification

	/**
	 * Returns a new {@link ImageMediaComponent} with the given image media.
	 * @param media the image media
	 * @return {@link ImageMediaComponent}
	 */
	public ImageMediaComponent createImageBackgroundComponent(ImageMedia media) {
		ImageMediaComponent component = new ImageMediaComponent(Messages.getString("slide.background.name"), media, 0, 0, this.width, this.height);
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
		VideoMediaComponent component = new VideoMediaComponent(Messages.getString("slide.background.name"), media, 0, 0, this.width, this.height);
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
		GenericSlideComponent component = new GenericSlideComponent(Messages.getString("slide.background.name"), 0, 0, this.width, this.height);
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
		component.setOrder(0);
		this.background = component;
	}
	
	/**
	 * Sorts the given components using their z-ordering.
	 * @param components the list of components to sort
	 */
	protected <E extends SlideComponent> void sortComponentsByOrder(List<E> components) {
		Collections.sort(components, ORDER_COMPARATOR);
	}
	
	/**
	 * Adds the given component.
	 * @param component the component to add
	 */
	public void addComponent(SlideComponent component) {
		if (component instanceof RenderableSlideComponent) {
			int order = this.getNextIndex();
			((RenderableSlideComponent)component).setOrder(order);
		}
		this.components.add(component);
		this.sortComponentsByOrder(this.components);
	}
	
	/**
	 * Removes the given component.
	 * @param component the component to remove
	 * @return boolean true if the component was removed
	 */
	public boolean removeComponent(SlideComponent component) {
		// no re-sort required here
		return this.components.remove(component);
	}
	
	/**
	 * Returns the next order index in the list of components.
	 * @return int
	 */
	protected int getNextIndex() {
		List<RenderableSlideComponent> components = this.getComponents(RenderableSlideComponent.class);
		if (components.size() > 0) {
			int maximum = 1;
			for (RenderableSlideComponent component : components) {
				if (maximum < component.getOrder()) {
					maximum = component.getOrder();
				}
			}
			return maximum + 1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Moves the given component up by one.
	 * <p>
	 * If the given component is not on this slide, this method does nothing.
	 * <p>
	 * If the given component is already the last component in this slide then
	 * the component is not modified.
	 * <p>
	 * Otherwise the given component is moved up by one and the next component is 
	 * moved back by one.
	 * @param component the component to move up
	 */
	public void moveComponentUp(RenderableSlideComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<RenderableSlideComponent> components = this.getComponents(RenderableSlideComponent.class);
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			int size = components.size();
			// see if the component is already in the last position
			if (components.get(size - 1).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				// if its not in the last position then we need to 
				// move it up and change the subsequent component (move it back by one)
				int order = component.getOrder();
				for (RenderableSlideComponent cmp : components) {
					// see if the current component order is greater
					// than this component's order
					if (cmp.getOrder() == order + 1) {
						// we only need to move back the next component
						cmp.setOrder(cmp.getOrder() - 1);
						break;
					}
				}
				// move the given component up
				component.setOrder(order + 1);
			}
		}
	}
	
	/**
	 * Moves the given component down by one.
	 * <p>
	 * If the given component is not on this slide, this method does nothing.
	 * <p>
	 * If the given component is already the first component in this slide then
	 * the component is not modified.
	 * <p>
	 * Otherwise the given component is moved down by one and the previous component is 
	 * moved up by one.
	 * @param component the component to move down
	 */
	public void moveComponentDown(RenderableSlideComponent component) {
		// move the given component down in the order
		
		// get all the components
		List<RenderableSlideComponent> components = this.getComponents(RenderableSlideComponent.class);
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			// see if the component is already in the first position
			if (components.get(0).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				// if its not in the first position then we need to 
				// move it down and change the previous component (move it up by one)
				int order = component.getOrder();
				for (RenderableSlideComponent cmp : components) {
					// find the previous component
					if (cmp.getOrder() == order - 1) {
						// we only need to move up the previous component
						cmp.setOrder(cmp.getOrder() + 1);
						break;
					}
				}
				// move the given component up
				component.setOrder(order - 1);
			}
		}
	}
	
	/**
	 * Returns a list of the all the components of the given type.
	 * <p>
	 * This method will not return the background component even if it is
	 * of the given type.
	 * <p>
	 * This method will return the components in ascending order.
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
	 * Returns a list of the all the components of the given type that cannot
	 * be removed from the slide.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public <E extends SlideComponent> List<E> getStaticComponents(Class<E> clazz) {
		return Collections.emptyList();
	}
	
	/**
	 * Returns a list of the all the components of the given type that can
	 * be removed from the slide.
	 * <p>
	 * This method will not return the background component even if it is
	 * of the given type.
	 * <p>
	 * This method will return the components in ascending order.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public <E extends SlideComponent> List<E> getNonStaticComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	/**
	 * Returns all the {@link PlayableMediaComponent}s on this {@link Slide}.
	 * <p>
	 * This is useful for display of the slide to being/end media playback.
	 * <p>
	 * This method will not return the background component even if it is of type
	 * {@link PlayableMediaComponent}.
	 * @return List&lt;{@link PlayableMediaComponent}&gt;
	 */
	public List<PlayableMediaComponent<?>> getPlayableMediaComponents() {
		List<PlayableMediaComponent<?>> components = new ArrayList<PlayableMediaComponent<?>>();
		for (SlideComponent component : this.components) {
			if (PlayableMediaComponent.class.isInstance(component)) {
				components.add((PlayableMediaComponent<?>)component);
			}
		}
		return components;
	}
	
	/**
	 * Returns this slide/template's name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets this slide/template's name.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Adjusts the slide and all sub components to fit the given size.
	 * @param width the target width
	 * @param height the target height
	 */
	public void adjustSize(int width, int height) {
		// compute the resize percentages
		double pw = (double)width / (double)this.width;
		double ph = (double)height / (double)this.height;
		// set the slide size
		this.width = width;
		this.height = height;
		// set the background size
		this.background.setWidth(width);
		this.background.setHeight(height);
		// set the sizes for the components
		List<RenderableSlideComponent> components = this.getComponents(RenderableSlideComponent.class);
		for (RenderableSlideComponent component : components) {
			component.resize(pw, ph);
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
		image = ImageUtilities.getUniformScaledImage(image, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return it
		return image;
	}
}
