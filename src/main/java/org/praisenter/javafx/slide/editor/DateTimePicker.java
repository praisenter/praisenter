package org.praisenter.javafx.slide.editor;

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
import javafx.util.StringConverter;

class DateTimePicker extends VBox {
	
	private final ObjectProperty<LocalDateTime> value = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
	
	private boolean mutating = false;
	
	// controls
	
	private final DatePicker pkrDate;
	private final Spinner<Integer> spnHours;
	private final Spinner<Integer> spnMinutes;
	private final Spinner<Integer> spnSeconds;
	
	private class Converter extends StringConverter<Integer> {
//	     private final DecimalFormat df = new DecimalFormat("#.##");

	     @Override 
	     public String toString(Integer value) {
	         // If the specified value is null, return a zero-length String
	         if (value == null) {
	             return "";
	         }

	         return String.format("%02d", value);
	     }

	     @Override 
	     public Integer fromString(String value) {
	         try {
	             // If the specified value is null or zero-length, return null
	             if (value == null) {
	                 return null;
	             }

	             value = value.trim();

	             if (value.length() < 1) {
	                 return null;
	             }

	             // Perform the requested parsing
	             return Integer.parseInt(value);
	         } catch (NumberFormatException ex) {
	             return null;
	         }
	     }
	};
	
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

	public DateTimePicker() {
		setSpacing(2);
		
		StringConverter<Integer> converter = new Converter();
		
		this.pkrDate = new DatePicker(LocalDate.now());
		
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
		
		HBox time = new HBox(this.spnHours, this.spnMinutes, this.spnSeconds);
		
		this.getChildren().addAll(this.pkrDate, time);
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				mutating = true;
				value.set(getControlValues());
				mutating = false;
			}
		};
		
		this.pkrDate.valueProperty().addListener(listener);
		this.spnHours.valueProperty().addListener(listener);
		this.spnMinutes.valueProperty().addListener(listener);
		this.spnSeconds.valueProperty().addListener(listener);
		
		this.value.addListener((obs, ov, nv) -> {
			if (mutating) return;
			mutating = true;
			this.pkrDate.setValue(nv.toLocalDate());
			LocalTime t = nv.toLocalTime();
			this.spnHours.getValueFactory().setValue(t.getHour());
			this.spnMinutes.getValueFactory().setValue(t.getMinute());
			this.spnSeconds.getValueFactory().setValue(t.getSecond());
			mutating = false;
		});
	}
	
	private LocalDateTime getControlValues() {
		return LocalDateTime.of(
				this.pkrDate.getValue(),
				LocalTime.of(
						this.spnHours.getValue(), 
						this.spnMinutes.getValue(), 
						this.spnSeconds.getValue()));
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
