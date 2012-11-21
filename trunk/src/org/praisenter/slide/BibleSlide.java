package org.praisenter.slide;

import java.util.List;

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
		
		this.scriptureLocationComponent.setOrder(1);
		this.scriptureTextComponent.setOrder(2);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public BibleSlide(BibleSlide slide) {
		super(slide);
		this.scriptureLocationComponent = slide.scriptureLocationComponent.copy();
		this.scriptureTextComponent = slide.scriptureTextComponent.copy();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public BibleSlide copy() {
		return new BibleSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	public BibleSlideTemplate createTemplate() {
		return new BibleSlideTemplate(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#getComponents(java.lang.Class)
	 */
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = super.getComponents(clazz);
		if (clazz.isAssignableFrom(TextComponent.class)) {
			components.add(clazz.cast(this.scriptureLocationComponent));
			components.add(clazz.cast(this.scriptureTextComponent));
		}
		this.sortComponentsByOrder(components);
		return components;
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
