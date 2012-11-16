package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a template of a {@link BibleSlide}.
 * <p>
 * Templates do not differ from their slide counterparts at this time. This distinction is
 * in place for the {@link SlideLibrary} and for future possible distinctions between the
 * two ideas.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "BibleSlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class BibleSlideTemplate extends BibleSlide implements Template {
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected BibleSlideTemplate() {
		super(null, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to copy
	 * @throws SlideCopyException thrown if the copy fails
	 */
	protected BibleSlideTemplate(BibleSlide slide) throws SlideCopyException {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public BibleSlide createSlide() throws SlideCopyException {
		return new BibleSlide(this);
	}
}
