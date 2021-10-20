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

import java.time.LocalDateTime;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.CountdownComponent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents an observable wrapper for a {@link CountdownComponent}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ObservableCountdownComponent extends ObservableTextComponent<CountdownComponent> implements Playable {
	/** A timer to update the countdown */
	private final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			setText(region.getText());
		}
	};
	
	/** The countdown target date/time */
	private final ObjectProperty<LocalDateTime> countdownTarget = new SimpleObjectProperty<LocalDateTime>();
	
	/** The time only count down flag */
	private final BooleanProperty countdownTimeOnly = new SimpleBooleanProperty();
	
	/** The countdown format */
	private final StringProperty countdownFormat = new SimpleStringProperty();
	
	/**
	 * Minimal constructor.
	 * @param component the text component
	 * @param context the context
	 * @param mode the slide mode
	 */
	public ObservableCountdownComponent(CountdownComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.countdownTarget.set(component.getCountdownTarget());
		this.countdownTimeOnly.set(component.isCountdownTimeOnly());
		this.countdownFormat.set(component.getCountdownFormat());
		
		this.countdownTarget.addListener((obs, ov, nv) -> { 
			this.region.setCountdownTarget(nv); 
			this.setText(this.region.getText());
		});
		this.countdownTimeOnly.addListener((obs, ov, nv) -> {
			this.region.setCountdownTimeOnly(nv);
			this.setText(this.region.getText());
		});
		this.countdownFormat.addListener((obs, ov, nv) -> { 
			this.region.setCountdownFormat(nv);
			this.setText(this.region.getText());
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
	
	// target
	
	/**
	 * Returns the count down target date/time.
	 * @return LocalDateTime
	 */
	public LocalDateTime getCountdownTarget() {
		return this.countdownTarget.get();
	}
	
	/**
	 * Sets the count down target date/time.
	 * @param target the target date/time to count down to
	 */
	public void setCountdownTarget(LocalDateTime target) {
		this.countdownTarget.set(target);
	}
	
	/**
	 * Returns the count down target property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<LocalDateTime> countdownTargetProperty() {
		return this.countdownTarget;
	}
	
	// time only

	/**
	 * Returns true if the countdown is time-only.
	 * @return boolean
	 */
	public boolean isCountdownTimeOnly() {
		return this.countdownTimeOnly.get();
	}
	
	/**
	 * Sets the count down to ignore or reflect the target's date.
	 * @param flag true if the target's date portion should be ignored
	 */
	public void setCountdownTimeOnly(boolean flag) {
		this.countdownTimeOnly.set(flag);
	}
	
	/**
	 * Returns the count down time-only property.
	 * @return BooleanProperty
	 */
	public BooleanProperty countdownTimeOnlyProperty() {
		return this.countdownTimeOnly;
	}
	
	// format
	
	/**
	 * Returns the count down format.
	 * @return String
	 */
	public String getCountdownFormat() {
		return this.countdownFormat.get();
	}
	
	/**
	 * Sets the count down format.
	 * @param format the format
	 */
	public void setCountdownFormat(String format) {
		this.countdownFormat.set(format);
	}
	
	/**
	 * Returns the count down format property.
	 * @return StringProperty
	 */
	public StringProperty countdownFormatProperty() {
		return this.countdownFormat;
	}
}
