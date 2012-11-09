package org.praisenter.slide;

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
public class BibleSlide extends Slide {
	/** The scripture location component (like: Genesis 1:1) */
	@XmlElement(name = "ScriptureLocationComponent")
	protected TextComponent scriptureLocationComponent;
	
	/** The scripture text component (like: In the beginning...) */
	@XmlElement(name = "ScriptureTextComponent")
	protected TextComponent scriptureTextComponent;
	
	/**
	 * Full constructor.
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public BibleSlide(int width, int height) {
		super(width, height);
		
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
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * <p>
	 * This does not copy the slide listeners.
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
