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

public final class DateTimePicker extends VBox {
	private final DatePicker pkrDate;
	private final TimeSpinner spnTime;
	
	private final ObjectProperty<LocalDateTime> value = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
	
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
	
	private LocalDateTime getControlValues() {
		return LocalDateTime.of(
				this.pkrDate.getValue(),
				this.spnTime.getValue());
	}
	
	public LocalDateTime getValue() {
		return this.value.get();
	}
	
	public void setValue(LocalDateTime time) {
		this.value.set(time);
	}
	
	public ObjectProperty<LocalDateTime> valueProperty() {
		return this.value;
	}
}
