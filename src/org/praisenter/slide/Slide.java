package org.praisenter.slide;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.TimedMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;

public class Slide {
	protected int width;
	protected int height;
	
	protected SlideComponent background;
	
	protected List<PositionedSlideComponent> components;
	
	protected List<SlideListener> listeners;
	
	public Slide(int width, int height) {
		this.width = width;
		this.height = height;
		this.background = null;
		this.components = new ArrayList<PositionedSlideComponent>();
		this.listeners = new ArrayList<SlideListener>();
	}
	
	// rendering
	
//	public void preparePreview() {
//		
//	}
//	
//	public void prepare() {
//		
//	}
//	
	public void renderPreview(Graphics2D g) {
		if (this.background != null) {
			this.background.renderPreview(g);
		}
		
		// TODO resort the list by order
		for (PositionedSlideComponent component : this.components) {
			component.renderPreview(g);
		}
	}
	
	public void render(Graphics2D g) {
		if (this.background != null) {
			this.background.render(g);
		}
		
		// TODO resort the list by order
		for (PositionedSlideComponent component : this.components) {
			component.render(g);
		}
	}
	
	/**
	 * Should be called when the in transition begins.
	 * @param listener
	 */
	public void start(SlideListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}
	
	/**
	 * Should be called when the out transition ends.
	 * @param listener
	 */
	public void end(SlideListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	// modification

	/**
	 * Returns a new {@link ImageMediaComponent} with the given image media.
	 * @param media the image media
	 * @return {@link ImageMediaComponent}
	 */
	public ImageMediaComponent createImageBackgroundComponent(ImageMedia media) {
		ImageMediaComponent component = new ImageMediaComponent(0, 0, this.width, this.height, media);
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
		VideoMediaComponent component = new VideoMediaComponent(0, 0, this.width, this.height, media);
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
		// no border
		component.setBorderVisible(false);
		component.setBorderPaint(null);
		component.setBorderStroke(null);
	}
	
	/**
	 * Returns the background component.
	 * <p>
	 * This can be any type of component, even a {@link PositionedSlideComponent}. In this
	 * case the position should be 0,0. The width/height should also match the slide
	 * width/height.
	 * @see #createImageBackgroundComponent(ImageMedia)
	 * @see #createPaintBackgroundComponent(Paint)
	 * @see #createVideoBackgroundComponent(AbstractVideoMedia)
	 * @return {@link SlideComponent}
	 */
	public SlideComponent getBackground() {
		return this.background;
	}
	
	/**
	 * Sets the background to the given component.
	 * @see #createImageBackgroundComponent(ImageMedia)
	 * @see #createPaintBackgroundComponent(Paint)
	 * @see #createVideoBackgroundComponent(AbstractVideoMedia)
	 * @param component the background component
	 */
	public void setBackground(SlideComponent component) {
		this.background = component;
	}
	
	public void addComponent(PositionedSlideComponent component) {
		this.components.add(component);
	}
	
	public boolean removeComponent(PositionedSlideComponent component) {
		return this.components.remove(component);
	}
	
	public int getComponentCount() {
		return this.components.size();
	}
	
	public PositionedSlideComponent getComponent(int i) {
		return this.components.get(i);
	}
	
	public <E extends PositionedSlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (PositionedSlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	private List<TimedMediaComponent<?>> getTimedMediaComponents() {
		List<TimedMediaComponent<?>> components = new ArrayList<TimedMediaComponent<?>>();
		for (PositionedSlideComponent component : this.components) {
			if (TimedMediaComponent.class.isInstance(component)) {
				components.add((TimedMediaComponent<?>)component);
			}
		}
		return components;
	}
	
//	public void addListener(SlideListener listener) {
//		this.listeners.add(listener);
//	}
//	
//	public boolean removeListener(SlideListener listener) {
//		return this.listeners.remove(listener);
//	}
//	
//	public <E extends SlideListener> List<E> getListeners(Class<E> clazz) {
//		List<E> listeners = new ArrayList<E>();
//		for (SlideListener listener : this.listeners) {
//			if (clazz.isInstance(listener)) {
//				listeners.add(clazz.cast(listener));
//			}
//		}
//		return listeners;
//	}
	
	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns a template for the given slide.
	 * @return
	 */
	public Template getTemplate() {
		// TODO implement
		return null;
	}
}
