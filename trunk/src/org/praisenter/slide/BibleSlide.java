package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.text.TextComponent;

/**
 * Specific slide for showing bible verses.
 * <p>
 * This slide has all the functionality of a normal slide but adds two
 * {@link TextComponent}s for the bible location and text.  These components
 * cannot be removed, but can be edited.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "BibleSlide")
@XmlAccessorType(XmlAccessType.NONE)
public class BibleSlide extends Slide {
	/** The scripture location component (like: Genesis 1:1) */
	@XmlElement(name = "ScriptureLocationComponent")
	protected TextComponent scriptureLocationComponent;
	
	/** The scripture text component (like: In the beginning...) */
	@XmlElement(name = "ScriptureTextComponent")
	protected TextComponent scriptureTextComponent;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected BibleSlide() {
		this(null, 0, 0);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public BibleSlide(String name, int width, int height) {
		super(name, width, height);
		
		// get the minimum dimension (typically the height)
		int maxd = height;
		if (maxd > width) {
			// the width is smaller so use it
			maxd = width;
		}
		// set the default screen to text component padding
		final int margin = (int)Math.floor((double)maxd * 0.04);
		
		// compute the default width, height and position
		final int h = height - margin * 2;
		final int w = width - margin * 2;
		
		final int tth = (int)Math.ceil((double)h * 0.20);
		final int th = h - tth - margin;
		
		this.scriptureLocationComponent = new TextComponent(margin, margin, w, tth);
		this.scriptureTextComponent = new TextComponent(margin, tth + margin * 2, w, th);
		
		// add them to the components list
		this.components.add(this.scriptureLocationComponent);
		this.components.add(this.scriptureTextComponent);
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
	public BibleSlide(BibleSlide slide) throws SlideCopyException {
		super(slide);
		try {
			this.scriptureLocationComponent = slide.scriptureLocationComponent.copy();
			this.scriptureTextComponent = slide.scriptureTextComponent.copy();
		} catch (SlideComponentCopyException e) {
			throw new SlideCopyException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public BibleSlide copy() throws SlideCopyException {
		return new BibleSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	public BibleSlideTemplate createTemplate() throws SlideCopyException {
		return new BibleSlideTemplate(this);
	}
	
	/**
	 * Returns the scripture location component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getScriptureLocationComponent() {
		return this.scriptureLocationComponent;
	}

	/**
	 * Returns the scripture text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getScriptureTextComponent() {
		return this.scriptureTextComponent;
	}
}
