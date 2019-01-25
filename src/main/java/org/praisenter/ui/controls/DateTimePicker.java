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
package org.praisenter.ui.controls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;

/**
 * Represents a picker for both date and time.
 * @author William Bittle
 * @version 3.0.0
 */
public final class DateTimePicker extends VBox {
	/** The value */
	private final ObjectProperty<LocalDateTime> value = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());

	// controls
	
	/** The date picker */
	private final DatePicker pkrDate;
	
	/** The time */
	private final TimeSpinner spnTime;
	
	/**
	 * Constructor.
	 */
	public DateTimePicker() {
		setSpacing(2);
		
		this.pkrDate = new DatePicker(LocalDate.now());
		this.spnTime = new TimeSpinner();
		
		this.getChildren().addAll(this.pkrDate, this.spnTime);
		
		BindingHelper.bindBidirectional(this.value, this.pkrDate.valueProperty(), new ObjectConverter<LocalDateTime, LocalDate>() {
			@Override
			public LocalDate convertFrom(LocalDateTime t) {
				return t != null ? t.toLocalDate() : LocalDate.now();
			}
			@Override
			public LocalDateTime convertTo(LocalDate e) {
				return DateTimePicker.this.getControlValues();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.spnTime.getValueFactory().valueProperty(), new ObjectConverter<LocalDateTime, LocalTime>() {
			@Override
			public LocalTime convertFrom(LocalDateTime t) {
				return t != null ? t.toLocalTime() : LocalTime.now();
			}
			@Override
			public LocalDateTime convertTo(LocalTime e) {
				return DateTimePicker.this.getControlValues();
			}
		});
	}
	
	/**
	 * Returns the LocalDateTime value for the current control states.
	 * @return LocalDateTime
	 */
	private LocalDateTime getControlValues() {
		return LocalDateTime.of(
				this.pkrDate.getValue(),
				this.spnTime.getValue());
	}
	
	/**
	 * Returns the current value.
	 * @return LocalDateTime
	 */
	public LocalDateTime getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value.
	 * @param time the new value
	 */
	public void setValue(LocalDateTime time) {
		this.value.set(time);
	}
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;LocalDateTime&gt;
	 */
	public ObjectProperty<LocalDateTime> valueProperty() {
		return this.value;
	}
}
