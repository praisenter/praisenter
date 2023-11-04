package org.praisenter.ui.controls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;

public final class DateTimePicker extends EditorFieldGroup {
	private static final String DATETIME_PICKER_CSS = "p-datetime-picker";
	
	private final DatePicker pkrDate;
	private final TimeSpinner spnTime;
	
	private final ObjectProperty<LocalDateTime> value = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
	
	public DateTimePicker() {
		this.getStyleClass().add(DATETIME_PICKER_CSS);
		
		this.pkrDate = new DatePicker(LocalDate.now());
		this.spnTime = new TimeSpinner();
		
		EditorField fldDate = new EditorField(Translations.get("slide.countdown.target"), this.pkrDate);
		EditorField fldTime = new EditorField("", Translations.get("slide.countdown.target.description"), this.spnTime);
		
		this.getChildren().addAll(fldDate, fldTime);
		
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
