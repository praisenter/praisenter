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
package org.praisenter.javafx.slide;

import java.text.SimpleDateFormat;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.DateTimeComponent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an observable {@link DateTimeComponent}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ObservableDateTimeComponent extends ObservableTextComponent<DateTimeComponent> implements Playable {
	/** A timer to update the countdown */
	private final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			setText(region.getText());
		}
	};
	
	/** The date/time format */
	private final ObjectProperty<SimpleDateFormat> dateTimeFormat = new SimpleObjectProperty<SimpleDateFormat>();
	
	/**
	 * Minimal constructor.
	 * @param component the text component
	 * @param context the context
	 * @param mode the slide mode
	 */
	public ObservableDateTimeComponent(DateTimeComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.dateTimeFormat.set(component.getDateTimeFormat());
		
		// listen for changes
		this.dateTimeFormat.addListener((obs, ov, nv) -> { 
			this.region.setDateTimeFormat(nv);
			String text = this.region.getText();
			this.setText(text);
		});
		
		this.build();
	}
	
	// playable stuff
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#play()
	 */
	public void play() {
		super.play();
		this.timer.start();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#stop()
	 */
	public void stop() {
		super.stop();
		this.timer.stop();
	}
	
	// format
	
	/**
	 * Returns the date/time format.
	 * @return SimpleDateFormat
	 */
	public SimpleDateFormat getDateTimeFormat() {
		return this.dateTimeFormat.get();
	}
	
	/**
	 * Sets the date/time format.
	 * @param format the format
	 */
	public void setDateTimeFormat(SimpleDateFormat format) {
		this.dateTimeFormat.set(format);
	}
	
	/**
	 * Returns the date/time format property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SimpleDateFormat> dateTimeFormatProperty() {
		return this.dateTimeFormat;
	}
}
