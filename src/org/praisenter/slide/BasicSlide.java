/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.Version;
import org.praisenter.images.Images;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents a slide with graphics, text, etc.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Slide")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ 
	GenericComponent.class,
	ImageMediaComponent.class, 
	VideoMediaComponent.class,
	AudioMediaComponent.class,
	TextComponent.class,
	DateTimeComponent.class })
public class BasicSlide implements Slide {
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
	@XmlElement(name = "Background")
	@XmlJavaTypeAdapter(value = RenderableComponentTypeAdapter.class)
	protected RenderableComponent background;
	
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
	protected BasicSlide() {
		this(Messages.getString("slide.unnamed"), 400, 400);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the slide/template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public BasicSlide(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.background = new EmptyRenderableComponent(Messages.getString("slide.background.name"), width, height);
		this.components = new ArrayList<SlideComponent>();
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public BasicSlide(BasicSlide slide) {
		this.name = slide.name;
		this.width = slide.width;
		this.height = slide.height;
		this.components = new ArrayList<SlideComponent>();
		
		// the background
		this.background = slide.background.copy();
		
		// the components
		for (SlideComponent component : slide.components) {
			this.components.add(component.copy());
		}
	}
	
	/**
	 * Returns the version of Praisenter to be placed in the generated
	 * XML document.
	 * <p>
	 * This method is solely for JAXB and the generated XML document thereof
	 * for versioning. The versioning will be of help later if the format
	 * of the XML changes.
	 * @return String
	 */
	@XmlAttribute(name = "Version")
	protected String getVersion() {
		return Version.getVersion();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public BasicSlide copy() {
		return new BasicSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	@Override
	public Template createTemplate() {
		return new BasicSlideTemplate(this);
	}
	
	// rendering
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		Shape clip = g.getClip();
		g.clipRect(0, 0, this.getWidth(), this.getHeight());
		
		this.background.renderPreview(g);
		
		for (RenderableComponent component : this.getComponents(RenderableComponent.class)) {
			component.renderPreview(g);
		}
		
		g.setClip(clip);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		Shape clip = g.getClip();
		g.clipRect(0, 0, this.getWidth(), this.getHeight());
		
		this.background.render(g);
		
		for (RenderableComponent component : this.getComponents(RenderableComponent.class)) {
			component.render(g);
		}
		
		g.setClip(clip);
	}
	
	// modification

	/**
	 * Returns a new {@link ImageMediaComponent} with the given image media.
	 * @param media the image media
	 * @return {@link ImageMediaComponent}
	 */
	public ImageMediaComponent createImageBackgroundComponent(ImageMedia media) {
		ImageMediaComponent component = new ImageMediaComponent(Messages.getString("slide.background.name"), media, 0, 0, this.width, this.height);
		component.setBorderVisible(false);
		return component;
	}
	
	/**
	 * Returns a new {@link VideoMediaComponent} with the given video media.
	 * @param media the video media
	 * @return {@link VideoMediaComponent}
	 */
	public VideoMediaComponent createVideoBackgroundComponent(AbstractVideoMedia media) {
		VideoMediaComponent component = new VideoMediaComponent(Messages.getString("slide.background.name"), media, 0, 0, this.width, this.height);
		component.setBackgroundVisible(false);
		component.setBorderVisible(false);
		return component;
	}
	
	/**
	 * Returns a new {@link GenericComponent} with the given fill as the background.
	 * <p>
	 * The fill can be a solid color or gradient or any other type of fill.
	 * @param fill the fill
	 * @return {@link GenericComponent}
	 */
	public GenericComponent createFillBackgroundComponent(Fill fill) {
		GenericComponent component = new GenericComponent(Messages.getString("slide.background.name"), 0, 0, this.width, this.height);
		component.setBackgroundFill(fill);
		component.setBackgroundVisible(true);
		component.setBorderVisible(false);
		return component;
	}
	
	/**
	 * Returns a new {@link EmptyRenderableComponent} that is sized for the background.
	 * @return {@link EmptyRenderableComponent}
	 */
	public EmptyRenderableComponent createEmptyBackgroundComponent() {
		return new EmptyRenderableComponent(Messages.getString("slide.background.name"), this.width, this.height);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getBackground()
	 */
	@Override
	public RenderableComponent getBackground() {
		return this.background;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setBackground(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public void setBackground(RenderableComponent component) {
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#addComponent(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public void addComponent(SlideComponent component) {
		if (component instanceof RenderableComponent) {
			int order = this.getNextIndex();
			((RenderableComponent)component).setOrder(order);
		}
		this.components.add(component);
		this.sortComponentsByOrder(this.components);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#removeComponent(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public boolean removeComponent(SlideComponent component) {
		// no re-sort required here
		return this.components.remove(component);
	}
	
	/**
	 * Returns the next order index in the list of components.
	 * @return int
	 */
	protected int getNextIndex() {
		List<RenderableComponent> components = this.getComponents(RenderableComponent.class);
		if (components.size() > 0) {
			int maximum = 1;
			for (RenderableComponent component : components) {
				if (maximum < component.getOrder()) {
					maximum = component.getOrder();
				}
			}
			return maximum + 1;
		} else {
			return 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentUp(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public void moveComponentUp(RenderableComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<RenderableComponent> components = this.getComponents(RenderableComponent.class);
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
				for (RenderableComponent cmp : components) {
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#moveComponentDown(org.praisenter.slide.RenderableComponent)
	 */
	@Override
	public void moveComponentDown(RenderableComponent component) {
		// move the given component down in the order
		
		// get all the components
		List<RenderableComponent> components = this.getComponents(RenderableComponent.class);
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
				for (RenderableComponent cmp : components) {
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		return this.getComponents(clazz, false);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponents(java.lang.Class,boolean)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz, boolean includeBackground) {
		List<E> components = new ArrayList<E>();
		if (includeBackground && this.background != null && clazz.isInstance(this.background)) {
			components.add(clazz.cast(this.background));
		}
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getStaticComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getStaticComponents(Class<E> clazz) {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getNonStaticComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getNonStaticComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getPlayableMediaComponents()
	 */
	@Override
	public List<PlayableMediaComponent<?>> getPlayableMediaComponents() {
		List<PlayableMediaComponent<?>> components = new ArrayList<PlayableMediaComponent<?>>();
		for (SlideComponent component : this.components) {
			if (PlayableMediaComponent.class.isInstance(component)) {
				components.add((PlayableMediaComponent<?>)component);
			}
		}
		return components;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#isStaticComponent(org.praisenter.slide.SlideComponent)
	 */
	@Override
	public boolean isStaticComponent(SlideComponent component) {
		List<SlideComponent> components = this.getStaticComponents(SlideComponent.class);
		return components.contains(component) || this.background == component;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.width;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
		this.background.setWidth(width);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.height;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
		this.background.setHeight(height);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#adjustSize(int, int)
	 */
	@Override
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
		List<RenderableComponent> components = this.getComponents(RenderableComponent.class);
		for (RenderableComponent component : components) {
			component.adjust(pw, ph);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public BufferedImage getThumbnail(Dimension size) {
		// render the slide to a buffered image of the right size
		BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		this.renderPreview(g);
		g.dispose();
		// scale the composite down
		image = ImageUtilities.getUniformScaledImage(image, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// create a buffered image with the transparent background
		BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		// center the scaled thumbnail image
		int x = 0;
		int y = 0;
		if (image.getWidth() != size.width) {
			x = (size.width - image.getWidth()) / 2;
		}
		if (image.getHeight() != size.height) {
			y = (size.height - image.getHeight()) / 2;
		}
		ImageUtilities.renderTiledImage(Images.TRANSPARENT_BACKGROUND, g, x, y, image.getWidth(), image.getHeight());
		g.drawImage(image, x, y, null);
		g.dispose();
		// return it
		return img;
	}
}
