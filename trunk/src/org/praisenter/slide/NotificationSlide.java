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
import javax.xml.bind.annotation.XmlAttribute;
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
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "NotificationSlide")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationSlide extends Slide {
	/** The x coordinate of this slide */
	@XmlAttribute(name = "X", required = true)
	protected int x;
	
	/** The y coordinate of this slide */
	@XmlAttribute(name = "Y", required = true)
	protected int y;

	/** The width of the target device */
	@XmlAttribute(name = "DeviceWidth", required = true)
	protected int deviceWidth;
	
	/** The height of the target device */
	@XmlAttribute(name = "DeviceHeight", required = true)
	protected int deviceHeight;
	
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
		this(null, 0, 0, 0, 0);
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
		super(name, slideWidth, slideHeight);
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
		this.textComponent = new TextComponent(Messages.getString("slide.notification.text.name"), 0, 0, slideWidth, slideHeight);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public NotificationSlide(NotificationSlide slide) {
		super(slide);
		this.x = slide.x;
		this.y = slide.y;
		this.deviceWidth = slide.deviceWidth;
		this.deviceHeight = slide.deviceHeight;
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#adjustSize(int, int)
	 */
	public void adjustSize(int deviceWidth, int deviceHeight) {
		// compute the resize percentages
		double pw = (double)deviceWidth / (double)this.deviceWidth;
		double ph = (double)deviceHeight / (double)this.deviceHeight;
		
		int w = (int)Math.ceil(this.width * pw);
		int h = (int)Math.ceil(this.height * ph);
		
		super.adjustSize(w, h);
		
		// apply this
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
	}
	
	/**
	 * Returns the text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}

	/**
	 * Returns the x coordinate for this slide in pixels.
	 * @return int
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Sets the x coordinate for this slide. 
	 * @param x the x coorindate in pixels
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y coordinate for this slide in pixels.
	 * @return int
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Sets the y coordinate for this slide.
	 * @param y the y coordinate in pixels
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the stored target device width.
	 * @return int
	 */
	public int getDeviceWidth() {
		return this.deviceWidth;
	}

	/**
	 * Sets the target device width.
	 * <p>
	 * This is used in creation of the thumbnail for the slide
	 * to ensure the positioning and size of the slide.
	 * @param deviceWidth the target device width
	 */
	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	/**
	 * Returns the stored target device height.
	 * @return int
	 */
	public int getDeviceHeight() {
		return this.deviceHeight;
	}

	/**
	 * Sets the target device height.
	 * <p>
	 * This is used in creation of the thumbnail for the slide
	 * to ensure the positioning and size of the slide.
	 * @param deviceHeight the target device height
	 */
	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}
}
