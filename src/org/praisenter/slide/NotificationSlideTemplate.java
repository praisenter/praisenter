/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide;

import java.awt.Color;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.common.utilities.FontManager;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.resources.Messages;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

/**
 * Represents a template of a {@link NotificationSlide}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "NotificationSlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationSlideTemplate extends NotificationSlide implements Slide, Template, Serializable {
	/** The version id */
	private static final long serialVersionUID = -3136596492866517853L;

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
