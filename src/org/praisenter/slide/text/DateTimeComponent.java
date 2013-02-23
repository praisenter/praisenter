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
package org.praisenter.slide.text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.log4j.Logger;
import org.praisenter.common.xml.SimpleDateFormatTypeAdapter;
import org.praisenter.slide.PositionedComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientFill;

/**
 * Text component in which displays a date and/or time.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "DateTimeComponent")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({
	ColorFill.class,
	LinearGradientFill.class,
	RadialGradientFill.class
})
public class DateTimeComponent extends TextComponent implements PositionedComponent, RenderableComponent, SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = -5536740793364156423L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(DateTimeComponent.class);
	
	/** The default date format */
	private static final String DEFAULT_FORMAT = "EEEE MMMM d, yyyy";
	
	/** The date/time format */
	@XmlElement(name = "Format", required = false, nillable = true)
	@XmlJavaTypeAdapter(value = SimpleDateFormatTypeAdapter.class)
	protected SimpleDateFormat dateTimeFormat;
	
	/** True if the date/time should update */
	@XmlElement(name = "UpdateEnabled", required = false, nillable = true)
	protected boolean dateTimeUpdateEnabled;
	
	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected DateTimeComponent() {
		this(null, 0, 0, 0, 0, null);
	}

	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public DateTimeComponent(String name, int width, int height) {
		this(name, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public DateTimeComponent(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, DEFAULT_FORMAT);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param format the date/time format string
	 */
	public DateTimeComponent(String name, int x, int y, int width, int height, String format) {
		super(name, x, y, width, height, null);
		
		// use the format passed in or the default
		if (format == null || format.trim().length() == 0) {
			format = DEFAULT_FORMAT;
		}
		
		try {
			this.dateTimeFormat = new SimpleDateFormat(format);
		} catch (Exception e) {
			LOGGER.warn("Invalid format [" + format + "].");
		}
		
		this.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		this.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		this.setTextWrapped(false);
		this.dateTimeUpdateEnabled = false;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public DateTimeComponent(DateTimeComponent component) {
		super(component);
		this.dateTimeFormat = component.dateTimeFormat;
		this.dateTimeUpdateEnabled = component.dateTimeUpdateEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#copy()
	 */
	@Override
	public DateTimeComponent copy() {
		return new DateTimeComponent(this);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getTextToRender()
	 */
	@Override
	protected String getTextToRender() {
		return this.dateTimeFormat.format(new Date());
	}
	
	/**
	 * Sets the date/time format of this component.
	 * @param format the format
	 */
	public void setDateTimeFormat(String format) {
		super.setText(format);
		try {
			this.dateTimeFormat = new SimpleDateFormat(format);
		} catch (Exception e) {
			LOGGER.warn("Invalid format [" + format + "].");
		}
	}
	
	/**
	 * Returns the date/time format for this component.
	 * @return String
	 */
	public String getDateTimeFormat() {
		return this.dateTimeFormat.toPattern();
	}
	
	/**
	 * Returns true if the component should update the time.
	 * @return boolean
	 */
	public boolean isDateTimeUpdateEnabled() {
		return this.dateTimeUpdateEnabled;
	}

	/**
	 * Sets the component to update the date/time.
	 * @param flag true to update the date/time
	 */
	public void setDateTimeUpdateEnabled(boolean flag) {
		this.dateTimeUpdateEnabled = flag;
	}
}
