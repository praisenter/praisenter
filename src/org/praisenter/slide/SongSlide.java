package org.praisenter.slide;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;
import org.praisenter.slide.text.TextComponent;

/**
 * Specific slide for showing song text.
 * <p>
 * This slide has all the functionality of a normal slide but adds an additional
 * {@link TextComponent} for the song text.  This component cannot be removed, 
 * but can be edited.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "SongSlide")
@XmlAccessorType(XmlAccessType.NONE)
public class SongSlide extends Slide {
	/** The text component */
	@XmlElement(name = "TextComponent")
	protected TextComponent textComponent;

	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected SongSlide() {
		this(Messages.getString("slide.unnamed"), 400, 400);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public SongSlide(String name, int width, int height) {
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
		
		this.textComponent = new TextComponent(Messages.getString("slide.song.text.name"), margin, margin, w, h);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This will perform a deep copy where necessary.
	 * @param slide the slide to copy
	 */
	public SongSlide(SongSlide slide) {
		super(slide);
		this.textComponent = slide.textComponent.copy();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#copy()
	 */
	@Override
	public SongSlide copy() {
		return new SongSlide(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Slide#createTemplate()
	 */
	@Override
	public SongSlideTemplate createTemplate() {
		return new SongSlideTemplate(this);
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
	
	/**
	 * Returns the text component.
	 * @return {@link TextComponent}
	 */
	public TextComponent getTextComponent() {
		return this.textComponent;
	}
}
