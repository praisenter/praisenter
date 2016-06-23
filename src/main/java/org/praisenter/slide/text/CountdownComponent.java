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

import java.time.Duration;
import java.time.LocalTime;

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
	/** The target countdown time */
	@XmlElement(name = "target", required = false)
	// FIXME not sure if needed
	//@XmlJavaTypeAdapter(value = LocalTimeXmlAdapter.class)
	LocalTime target;

	/** The duration format */
	@XmlElement(name = "format", required = false)
	String format;
	
	/**
	 * Default constructor.
	 */
	public CountdownComponent() {
		this.target = null;
		// FIXME specifying a format this way won't work fully, we need to know what value goes where
		this.format = "%2$02d:%3$02d:%4$02d";
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
			LocalTime now = LocalTime.now();
			Duration duration = null;
			if (now.isAfter(this.target)) {
				Duration d1 = Duration.between(now, LocalTime.MAX);
				Duration d2 = Duration.between(LocalTime.MIDNIGHT, this.target);
				duration = d1.plus(d2);
			} else {
				duration = Duration.between(now, this.target);
			}
			if (duration.isNegative()) {
				// shouldn't happen, but who knows
				duration = Duration.ZERO;
			}
			long seconds = duration.getSeconds();
		    long absSeconds = Math.abs(seconds);
		    return String.format(
		        this.format,
		        absSeconds / 86400,
		        absSeconds / 3600,
		        (absSeconds % 3600) / 60,
		        absSeconds % 60);
		}
		return String.format(
		        this.format,
		        0,
		        0,
		        0,
		        0);
	}

	/**
	 * Returns the target time to count down to.
	 * @return LocalTime
	 */
	public LocalTime getTarget() {
		return this.target;
	}
	
	/**
	 * Sets the target time to count down to.
	 * @param target the target time
	 */
	public void setTarget(LocalTime target) {
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
			format = "%2$02d:%3$02d:%4$02d";
		}
		this.format = format;
	}
}
