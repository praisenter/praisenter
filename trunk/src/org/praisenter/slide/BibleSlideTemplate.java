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

import org.praisenter.common.utilities.ColorUtilities;
import org.praisenter.common.utilities.FontManager;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LinearGradientDirection;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.Stop;
import org.praisenter.slide.resources.Messages;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

/**
 * Represents a template of a {@link BibleSlide}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "BibleSlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class BibleSlideTemplate extends BibleSlide implements Slide, Template, Serializable {
	/** The version id */
	private static final long serialVersionUID = 4087075161706434146L;

	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected BibleSlideTemplate() {
		super(Messages.getString("slide.unnamed"), 0, 0);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public BibleSlideTemplate(String name, int width, int height) {
		super(name, width, height);
	}
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to copy
	 */
	protected BibleSlideTemplate(BibleSlide slide) {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.BibleSlide#copy()
	 */
	@Override
	public BibleSlideTemplate copy() {
		return new BibleSlideTemplate(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public BibleSlide createSlide() {
		return new BibleSlide(this);
	}
	
	/**
	 * Returns the default {@link BibleSlideTemplate}.
	 * <p>
	 * This is useful when no templates exist in the template library.
	 * @param width the slide template width
	 * @param height the slide template height
	 * @return {@link BibleSlideTemplate}
	 */
	public static final BibleSlideTemplate getDefaultTemplate(int width, int height) {
		BibleSlideTemplate template = new BibleSlideTemplate(Messages.getString("template.bible.default.name"), width, height);
		
		Fill fill = new LinearGradientFill(LinearGradientDirection.TOP,
				new Stop(0.0f, Color.BLACK),
				new Stop(0.5f, ColorUtilities.getColorAtMidpoint(Color.BLACK, Color.BLUE)),
				new Stop(1.0f, Color.BLUE));
		GenericComponent background = template.createFillBackgroundComponent(fill);
		template.setBackground(background);
		
		TextComponent location = template.getScriptureLocationComponent();
		location.setText(Messages.getString("slide.bible.location.default"));
		location.setTextFill(new ColorFill(Color.WHITE));
		location.setTextFont(FontManager.getDefaultFont().deriveFont(80.0f));
		location.setTextWrapped(false);
		location.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		location.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		location.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		location.setTextPadding(30);
		
		TextComponent text = template.getScriptureTextComponent();
		text.setText(Messages.getString("slide.bible.text.default"));
		text.setTextFill(new ColorFill(Color.WHITE));
		text.setTextFont(FontManager.getDefaultFont().deriveFont(50.0f));
		text.setTextWrapped(true);
		text.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		text.setVerticalTextAlignment(VerticalTextAlignment.TOP);
		text.setTextFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		text.setTextPadding(30);
		
		return template;
	}
}
