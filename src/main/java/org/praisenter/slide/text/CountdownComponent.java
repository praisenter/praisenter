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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.xml.adapters.LocalDateTimeXmlAdapter;

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
	@XmlElement(name = "countdownTarget", required = false)
	@XmlJavaTypeAdapter(value = LocalDateTimeXmlAdapter.class)
	LocalDateTime countdownTarget;

	/** The duration format */
	@XmlElement(name = "countdownFormat", required = false)
	String countdownFormat;
	
	/**
	 * Default constructor.
	 */
	public CountdownComponent() {
		this.countdownTarget = null;
		this.countdownFormat = DEFAULT_FORMAT;
	}
	
	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public CountdownComponent(CountdownComponent other, boolean exact) {
		super(other, exact);
		this.countdownTarget = other.countdownTarget;
		this.countdownFormat = other.countdownFormat;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	@Override
	public CountdownComponent copy() {
		return this.copy(false);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy(boolean)
	 */
	@Override
	public CountdownComponent copy(boolean exact) {
		return new CountdownComponent(this, exact);
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
		if (this.countdownTarget != null) {
			return formatCountdown(this.countdownFormat, this.countdownTarget);
		}
		return String.format(
		        (this.countdownFormat != null && this.countdownFormat.trim().length() > 0 ? this.countdownFormat : DEFAULT_FORMAT), 
		        0,
		        0,
		        0,
		        0,
		        0,
		        0);
	}
	
	/**
	 * Computes and formats the difference in target date and now.
	 * @param format the format defined by String.format
	 * @param target the target date/time
	 * @return String
	 */
	public static final String formatCountdown(String format, LocalDateTime target) {
		LocalDateTime temp = LocalDateTime.now();
		
		// get the individual durations and advance each time
		long years = temp.until(target, ChronoUnit.YEARS); 	temp = temp.plusYears(years);
		long months = temp.until(target, ChronoUnit.MONTHS); 	temp = temp.plusMonths(months);
		long days = temp.until(target, ChronoUnit.DAYS); 		temp = temp.plusDays(days);
		long hours = temp.until(target, ChronoUnit.HOURS); 	temp = temp.plusHours(hours);
		long minutes = temp.until(target, ChronoUnit.MINUTES); temp = temp.plusMinutes(minutes);
		long seconds = temp.until(target, ChronoUnit.SECONDS);
		
		return String.format(
		        format, 
		        years,
		        months,
		        days,
		        hours,
		        minutes,
		        seconds);
	}

	/**
	 * Returns the target time to count down to.
	 * @return LocalDateTime
	 */
	public LocalDateTime getCountdownTarget() {
		return this.countdownTarget;
	}
	
	/**
	 * Sets the target time to count down to.
	 * @param target the target time
	 */
	public void setCountdownTarget(LocalDateTime target) {
		this.countdownTarget = target;
	}

	/**
	 * Gets the count down format.
	 * @return String
	 */
	public String getCountdownFormat() {
		return this.countdownFormat;
	}

	/**
	 * Sets the count down format.
	 * @param format the format string
	 */
	public void setCountdownFormat(String format) {
		if (format == null || format.length() <= 0) {
			format = DEFAULT_FORMAT;
		}
		this.countdownFormat = format;
	}
}
