package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.text.TextComponent;

/**
 * Specific slide for showing notification text.
 * <p>
 * This slide has all the functionality of a normal slide but adds an additional
 * {@link TextComponent} for the notification text.  This component cannot be removed, 
 * but can be edited.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "NotificationSlide")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationSlide extends Slide {
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
		this(null, 0, 0);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public NotificationSlide(String name, int width, int height) {
		super(name, width, height);
		
		// compute the default height
		final int th = (int)Math.ceil((double)height * 0.20);
		
		this.textComponent = new TextComponent(0, 0, width, th);

		// add them to the components list
		this.components.add(this.textComponent);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * <p>
	 * This does not copy the slide listeners.
	 * @param slide the slide to copy
	 * @throws SlideCopyException thrown if the copy fails
	 */
	public NotificationSlide(NotificationSlide slide) throws SlideCopyException {
		super(slide);
		try {
			this.textComponent = slide.textComponent.copy();
		} catch (SlideComponentCopyException e) {
			throw new SlideCopyException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public NotificationSlide copy() throws SlideCopyException {
		return new NotificationSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	@Override
	public NotificationSlideTemplate createTemplate() throws SlideCopyException {
		return new NotificationSlideTemplate(this);
	}
	
	/**
	 * Returns the text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}
}
