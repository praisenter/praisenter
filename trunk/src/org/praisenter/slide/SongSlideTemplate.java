package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a template of a {@link NotificationSlide}.
 * <p>
 * Templates do not differ from their slide counterparts at this time. This distinction is
 * in place for the {@link SlideLibrary} and for future possible distinctions between the
 * two ideas.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "SongSlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class SongSlideTemplate extends SongSlide implements Template {
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected SongSlideTemplate() {
		super(null, 0, 0);
	}

	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public SongSlideTemplate(String name, int width, int height) {
		super(name, width, height);
	}
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to copy
	 * @throws SlideCopyException thrown if the copy fails
	 */
	protected SongSlideTemplate(SongSlide slide) throws SlideCopyException {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public SongSlide createSlide() throws SlideCopyException {
		return new SongSlide(this);
	}
}
