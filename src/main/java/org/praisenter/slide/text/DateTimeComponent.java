/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.xml.adapters.SimpleDateFormatTypeAdapter;

/**
 * A component to show the current date and time based on a given format.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "dateTimeComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class DateTimeComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The date/time format */
	@XmlAttribute(name = "format", required = false)
	@XmlJavaTypeAdapter(value = SimpleDateFormatTypeAdapter.class)
	SimpleDateFormat format;

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public DateTimeComponent copy() {
		DateTimeComponent comp = new DateTimeComponent();
		this.copy(comp);
		comp.setFormat((SimpleDateFormat)this.format.clone());
		return comp;
	}
	
	/**
	 * This method does nothing since the text is generated from
	 * the current date/time and the current date format.
	 */
	@Override
	public void setText(String text) {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getText()
	 */
	@Override
	public String getText() {
		return this.format != null ? this.format.format(new Date()) : (new Date()).toString();
	}

	/**
	 * Returns the date/time format.
	 * @return SimpleDateFormat
	 */
	public SimpleDateFormat getFormat() {
		return this.format;
	}

	/**
	 * Sets the date/time format.
	 * @param format the format
	 */
	public void setFormat(SimpleDateFormat format) {
		this.format = format;
	}
}
