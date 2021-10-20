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
package org.praisenter.javafx.slide.editor.controls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.UnaryOperator;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

/**
 * Represents a picker for both date and time.
 * @author William Bittle
 * @version 3.0.0
 */
public final class DateTimePicker extends VBox {
	/** The value */
	private final ObjectProperty<LocalDateTime> value = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
	
	/** True if the value is being altered */
	private boolean mutating = false;
	
	// controls
	
	/** The date picker */
	private final DatePicker pkrDate;
	
	/** The hour */
	private final Spinner<Integer> spnHours;
	
	/** The minute */
	private final Spinner<Integer> spnMinutes;
	
	/** The second */
	private final Spinner<Integer> spnSeconds;
	
	/**
	 * Filter method to only allow numeric digits in the spinner.
	 */
	private UnaryOperator<Change> op = new UnaryOperator<Change>() {
		@Override
		public Change apply(Change c) {
			String newText = c.getControlNewText();
		    if (newText.matches("[0-9]+")) {
		        return c;
		    }
		    return null;
		}
	};

	/**
	 * Constructor.
	 */
	public DateTimePicker() {
		setSpacing(2);
		
		IntegerStringConverter converter = new IntegerStringConverter();
		
		this.pkrDate = new DatePicker(LocalDate.now());
		this.pkrDate.setMaxWidth(110);
		
		this.spnHours = new Spinner<Integer>(0, 23, 0, 1);
		this.spnHours.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnHours.getValueFactory().setConverter(converter);
		this.spnHours.getValueFactory().setWrapAround(true);
		this.spnHours.setEditable(true);
		this.spnHours.setPrefWidth(55);
		
		this.spnMinutes = new Spinner<Integer>(0, 59, 0, 1);
		this.spnMinutes.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnMinutes.getValueFactory().setConverter(converter);
		this.spnMinutes.getValueFactory().setWrapAround(true);
		this.spnMinutes.setEditable(true);
		this.spnMinutes.setPrefWidth(55);
		
		this.spnSeconds = new Spinner<Integer>(0, 59, 0, 1);
		this.spnSeconds.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnSeconds.getValueFactory().setConverter(converter);
		this.spnSeconds.getValueFactory().setWrapAround(true);
		this.spnSeconds.setEditable(true);
		this.spnSeconds.setPrefWidth(55);
		
		HBox date = new HBox(2, this.pkrDate);
		HBox time = new HBox(this.spnHours, this.spnMinutes, this.spnSeconds);
		
		this.getChildren().addAll(date, time);
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				value.set(getControlValues());
			}
		};
		
		this.pkrDate.valueProperty().addListener(listener);
		this.spnHours.valueProperty().addListener(listener);
		this.spnMinutes.valueProperty().addListener(listener);
		this.spnSeconds.valueProperty().addListener(listener);
		
		this.value.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null) {
				this.pkrDate.setValue(nv.toLocalDate());
				LocalTime t = nv.toLocalTime();
				this.spnHours.getValueFactory().setValue(t.getHour());
				this.spnMinutes.getValueFactory().setValue(t.getMinute());
				this.spnSeconds.getValueFactory().setValue(t.getSecond());
			}
			mutating = false;
		});
	}
	
	/**
	 * Returns the LocalDateTime value for the current control states.
	 * @return LocalDateTime
	 */
	private LocalDateTime getControlValues() {
		return LocalDateTime.of(
				this.pkrDate.getValue(),
				LocalTime.of(
					this.spnHours.getValue(), 
					this.spnMinutes.getValue(), 
					this.spnSeconds.getValue()));
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
