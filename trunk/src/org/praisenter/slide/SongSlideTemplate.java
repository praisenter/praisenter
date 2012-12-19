package org.praisenter.slide;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LinearGradientDirection;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.Stop;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utilities.ColorUtilities;
import org.praisenter.utilities.FontManager;

/**
 * Represents a template of a {@link NotificationSlide}.
 * <p>
 * Templates do not differ from their slide counterparts at this time. This distinction is
 * in place for the {@link SlideLibrary} and for future possible distinctions between the
 * two ideas.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
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
		super();
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
	 * @see org.praisenter.slide.SongSlide#copy()
	 */
	@Override
	public SongSlideTemplate copy() {
		return new SongSlideTemplate(this);
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
		SongSlideTemplate template = new SongSlideTemplate(Messages.getString("template.song.default.name"), width, height);
		
		Fill fill = new LinearGradientFill(LinearGradientDirection.TOP,
				new Stop(0.0f, Color.BLACK),
				new Stop(0.5f, ColorUtilities.getColorAtMidpoint(Color.BLACK, new Color(0, 0, 0, 0))),
				new Stop(1.0f, 0, 0, 0, 0));
		GenericComponent background = template.createFillBackgroundComponent(fill);
		template.setBackground(background);
		
		TextComponent text = template.getTextComponent();
		text.setText(Messages.getString("slide.song.text.default"));
		text.setTextFill(new ColorFill(Color.WHITE));
		text.setTextFont(FontManager.getDefaultFont().deriveFont(60.0f));
		text.setTextWrapped(true);
		text.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		text.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		text.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		text.setTextPadding(30);
		
		return template;
	}
}
