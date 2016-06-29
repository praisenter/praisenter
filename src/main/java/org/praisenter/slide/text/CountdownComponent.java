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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

/**
 * A component to show a count down to a specified local time.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "countdownComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class CountdownComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent {
	private static final String DEFAULT_FORMAT = "%1$02d:%2$02d:%3$02d:%4$02d:%5$02d:%6$02d";
	
	/** The target countdown time */
	@XmlElement(name = "target", required = false)
	// FIXME not sure if needed
	//@XmlJavaTypeAdapter(value = LocalTimeXmlAdapter.class)
	LocalDateTime target;

	/** The duration format */
	@XmlElement(name = "format", required = false)
	String format;
	
	/**
	 * Default constructor.
	 */
	public CountdownComponent() {
		this.target = null;
		this.format = DEFAULT_FORMAT;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public CountdownComponent copy() {
		CountdownComponent comp = new CountdownComponent();
		this.copy(comp);
		
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
		if (this.target != null) {
			LocalDateTime temp = LocalDateTime.now();
			
			// get the individual durations and advance each time
			long years = temp.until(this.target, ChronoUnit.YEARS); 	temp = temp.plusYears(years);
			long months = temp.until(this.target, ChronoUnit.MONTHS); 	temp = temp.plusMonths(months);
			long days = temp.until(this.target, ChronoUnit.DAYS); 		temp = temp.plusDays(days);
			long hours = temp.until(this.target, ChronoUnit.HOURS); 	temp = temp.plusHours(hours);
			long minutes = temp.until(this.target, ChronoUnit.MINUTES); temp = temp.plusMinutes(minutes);
			long seconds = temp.until(this.target, ChronoUnit.SECONDS);
			
			return String.format(
			        this.format, 
			        years,
			        months,
			        days,
			        hours,
			        minutes,
			        seconds);
		}
		return String.format(
		        this.format, 
		        0,
		        0,
		        0,
		        0,
		        0,
		        0);
	}

	/**
	 * Returns the target time to count down to.
	 * @return LocalDateTime
	 */
	public LocalDateTime getTarget() {
		return this.target;
	}
	
	/**
	 * Sets the target time to count down to.
	 * @param target the target time
	 */
	public void setTarget(LocalDateTime target) {
		this.target = target;
	}

	/**
	 * Gets the count down format.
	 * @return String
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Sets the count down format.
	 * @param format the format string
	 */
	public void setFormat(String format) {
		if (format == null || format.length() <= 0) {
			format = DEFAULT_FORMAT;
		}
		this.format = format;
	}
}
