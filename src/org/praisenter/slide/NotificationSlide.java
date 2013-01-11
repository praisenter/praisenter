package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.utilities.ImageUtilities;

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
public class NotificationSlide extends AbstractPositionedSlide implements Slide {
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
	 * @see org.praisenter.slide.Slide#getComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = super.getComponents(clazz);
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
		// return it
		return image;
	}
	
	/**
	 * Returns the text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}
}
