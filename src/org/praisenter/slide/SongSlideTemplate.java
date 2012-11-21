package org.praisenter.slide;

import java.awt.Color;
import java.awt.LinearGradientPaint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utilities.FontManager;

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
	 */
	protected SongSlideTemplate(SongSlide slide) {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public SongSlide createSlide() {
		return new SongSlide(this);
	}
	
	/**
	 * Returns the default {@link SongSlideTemplate}.
	 * <p>
	 * This is useful when no templates exist in the template library.
	 * @param width the slide template width
	 * @param height the slide template height
	 * @return {@link SongSlideTemplate}
	 */
	public static final SongSlideTemplate getDefaultTemplate(int width, int height) {
		// FIXME translate
		SongSlideTemplate template = new SongSlideTemplate("", width, height);
		
		GenericSlideComponent background = template.createPaintBackgroundComponent(new LinearGradientPaint(0, 0, width, 0, new float[] { 0.5f, 1.0f }, new Color[] { Color.BLACK, new Color(0, 0, 0, 0) }));
		template.setBackground(background);
		
		TextComponent location = template.getTextComponent();
		location.setTextPaint(Color.WHITE);
		location.setTextFont(FontManager.getDefaultFont().deriveFont(60.0f));
		location.setTextWrapped(true);
		location.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		location.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		location.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		location.setTextPadding(30);
		
		return template;
	}
}
