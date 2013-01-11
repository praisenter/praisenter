package org.praisenter.slide;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;
import org.praisenter.slide.graphics.ColorFill;
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
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "NotificationSlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationSlideTemplate extends NotificationSlide implements Slide, Template {
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected NotificationSlideTemplate() {
		super();
	}

	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param deviceWidth the width of the target device
	 * @param deviceHeight the height of the target device
	 * @param slideWidth the width of the slide
	 * @param slideHeight the height of the slide
	 */
	public NotificationSlideTemplate(String name, int deviceWidth, int deviceHeight, int slideWidth, int slideHeight) {
		super(name, deviceWidth, deviceHeight, slideWidth, slideHeight);
	}
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to copy
	 */
	protected NotificationSlideTemplate(NotificationSlide slide) {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.NotificationSlide#copy()
	 */
	@Override
	public NotificationSlideTemplate copy() {
		return new NotificationSlideTemplate(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public NotificationSlide createSlide() {
		return new NotificationSlide(this);
	}
	
	/**
	 * Returns the default {@link NotificationSlideTemplate}.
	 * <p>
	 * This is useful when no templates exist in the template library.
	 * @param width the slide template width
	 * @param height the slide template height
	 * @return {@link NotificationSlideTemplate}
	 */
	public static final NotificationSlideTemplate getDefaultTemplate(int width, int height) {
		// the default template will be at the top
		final int h = (int)Math.ceil((double)height * 0.20);
		NotificationSlideTemplate template = new NotificationSlideTemplate(Messages.getString("template.notification.default.name"), width, height, width, h);
		
		GenericComponent background = template.createFillBackgroundComponent(new ColorFill(0, 0, 0, 170));
		template.setBackground(background);
		
		TextComponent text = template.getTextComponent();
		text.setText(Messages.getString("slide.notification.text.default"));
		text.setTextFill(new ColorFill(Color.WHITE));
		text.setTextFont(FontManager.getDefaultFont().deriveFont(50.0f));
		text.setTextWrapped(true);
		text.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		text.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		text.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		
		return template;
	}
}
