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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.common.utilities.ImageUtilities;
import org.praisenter.slide.resources.Messages;
import org.praisenter.slide.text.TextComponent;

/**
 * Specific slide for showing notification text.
 * <p>
 * This slide has all the functionality of a normal slide but adds an additional
 * {@link TextComponent} for the notification text.  This component cannot be removed, 
 * but can be edited.
 * <p>
 * Notifications can be positioned and sized unlike normal slides.  This allows the
 * notification to be completely customized like a normal slide, but still be positioned,
 * sized and displayed as a notification.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "NotificationSlide")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationSlide extends AbstractPositionedSlide implements Slide, Serializable {
	/** The version id */
	private static final long serialVersionUID = -2265173376035060164L;
	
	/** The text component */
	@XmlElement(name = "TextComponent")
	protected TextComponent textComponent;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected NotificationSlide() {
		this(Messages.getString("slide.unnamed"), 0, 0, 400, 400);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param deviceWidth the width of the target device
	 * @param deviceHeight the height of the target device
	 * @param slideWidth the width of the slide
	 * @param slideHeight the height of the slide
	 */
	public NotificationSlide(String name, int deviceWidth, int deviceHeight, int slideWidth, int slideHeight) {
		super(name, deviceWidth, deviceHeight, slideWidth, slideHeight);
		this.textComponent = new TextComponent(Messages.getString("slide.notification.text.name"), 25, 25, slideWidth - 50, slideHeight - 50);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public NotificationSlide(NotificationSlide slide) {
		super(slide);
		this.textComponent = slide.textComponent.copy();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public NotificationSlide copy() {
		return new NotificationSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	@Override
	public NotificationSlideTemplate createTemplate() {
		return new NotificationSlideTemplate(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.BasicSlide#getComponents(java.lang.Class, boolean)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz, boolean includeBackground) {
		List<E> components = super.getComponents(clazz, includeBackground);
		if (clazz.isAssignableFrom(TextComponent.class)) {
			components.add(clazz.cast(this.textComponent));
		}
		this.sortComponentsByOrder(components);
		return components;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getStaticComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getStaticComponents(Class<E> clazz) {
		if (clazz.isAssignableFrom(TextComponent.class)) {
			List<E> components = new ArrayList<E>();
			components.add(clazz.cast(this.textComponent));
			return components;
		}
		return super.getStaticComponents(clazz);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// we need to apply the translation before executing normal rendering
		AffineTransform oldTransform = g.getTransform();
		g.translate(this.x, this.y);
		super.render(g);
		g.setTransform(oldTransform);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// we need to apply the translation before executing normal rendering
		AffineTransform oldTransform = g.getTransform();
		g.translate(this.x, this.y);
		super.renderPreview(g);
		g.setTransform(oldTransform);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public BufferedImage getThumbnail(Dimension size) {
		// render the slide to a buffered image of the right size
		BufferedImage image = new BufferedImage(this.deviceWidth, this.deviceHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		this.renderPreview(g);
		g.dispose();
		// scale the composite down
		image = ImageUtilities.getUniformScaledImage(image, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// create a buffered image with the transparent background
		BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		// render the scaled slide onto the transparent background
		ImageUtilities.renderTiledImage(TRANSPARENT_BACKGROUND, g, 0, 0, image.getWidth(), image.getHeight());
		g.drawImage(image, 0, 0, null);
		g.dispose();
		// return it
		return img;
	}
	
	/**
	 * Returns the text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}
}
