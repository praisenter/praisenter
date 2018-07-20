package org.praisenter.javafx.slide.editor.ribbon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.command.ActionEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.converters.TimeFormatConverter;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.CountdownFormatEditCommand;
import org.praisenter.javafx.slide.editor.commands.CountdownTargetEditCommand;
import org.praisenter.javafx.slide.editor.commands.TimeOnlyEditCommand;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;

class CountdownRibbonTab extends ComponentEditorRibbonTab {
	//private final DateTimePicker pkrCountdownTime;
	
	private final DatePicker pkrDate;
	private final CheckBox chkTimeOnly;
	
	private final Spinner<Integer> spnHours;
	private final Spinner<Integer> spnMinutes;
	private final Spinner<Integer> spnSeconds;
	
	private final ComboBox<String> cmbCountdownFormat;
	
	private final List<Pair<String, String>> formatMapping;
	
	private class Converter extends StringConverter<Integer> {
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
	
	public CountdownRibbonTab(SlideEditorContext context) {
		super(context, "Countdown");

		formatMapping = new ArrayList<Pair<String, String>>();
		formatMapping.add(new Pair<String, String>("YY", "%1$02d"));
		formatMapping.add(new Pair<String, String>("MM", "%2$02d"));
		formatMapping.add(new Pair<String, String>("DD", "%3$02d"));
		formatMapping.add(new Pair<String, String>("hh", "%4$02d"));
		formatMapping.add(new Pair<String, String>("mm", "%5$02d"));
		formatMapping.add(new Pair<String, String>("ss", "%6$02d"));
		formatMapping.add(new Pair<String, String>("Y", "%1$01d"));
		formatMapping.add(new Pair<String, String>("M", "%2$01d"));
		formatMapping.add(new Pair<String, String>("D", "%3$01d"));
		formatMapping.add(new Pair<String, String>("h", "%4$01d"));
		formatMapping.add(new Pair<String, String>("m", "%5$01d"));
		formatMapping.add(new Pair<String, String>("s", "%6$01d"));
		
		ObservableList<String> countdownFormats = FXCollections.observableArrayList();
		countdownFormats.add("YY:MM:DD:hh:mm:ss");
		countdownFormats.add("MM:DD:hh:mm:ss");
		countdownFormats.add("DD:hh:mm:ss");
		countdownFormats.add("hh:mm:ss");
		countdownFormats.add("mm:ss");
		countdownFormats.add("ss");
		
		this.cmbCountdownFormat = new ComboBox<String>(countdownFormats);
		this.cmbCountdownFormat.setPrefWidth(175);
		this.cmbCountdownFormat.setEditable(true);
		this.cmbCountdownFormat.setValue(countdownFormats.get(2));
		this.cmbCountdownFormat.getEditor().addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this));
		
		this.chkTimeOnly = new CheckBox("Time Only?");

		StringConverter<Integer> converter = new Converter();
		
		this.pkrDate = new DatePicker(LocalDate.now());
		this.pkrDate.setMaxWidth(110);
		this.pkrDate.getEditor().addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this));
		
		this.spnHours = new Spinner<Integer>(0, 23, 0, 1);
		this.spnHours.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnHours.getValueFactory().setConverter(converter);
		this.spnHours.getValueFactory().setWrapAround(true);
		this.spnHours.setEditable(true);
		this.spnHours.setPrefWidth(55);
		this.spnHours.getEditor().addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this));
		
		this.spnMinutes = new Spinner<Integer>(0, 59, 0, 1);
		this.spnMinutes.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnMinutes.getValueFactory().setConverter(converter);
		this.spnMinutes.getValueFactory().setWrapAround(true);
		this.spnMinutes.setEditable(true);
		this.spnMinutes.setPrefWidth(55);
		this.spnMinutes.getEditor().addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this));
		
		this.spnSeconds = new Spinner<Integer>(0, 59, 0, 1);
		this.spnSeconds.getEditor().setTextFormatter(new TextFormatter<Integer>(converter, 0, op));
		this.spnSeconds.getValueFactory().setConverter(converter);
		this.spnSeconds.getValueFactory().setWrapAround(true);
		this.spnSeconds.setEditable(true);
		this.spnSeconds.setPrefWidth(55);
		this.spnSeconds.getEditor().addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this));
		
		// tooltips
		
		this.pkrDate.setTooltip(new Tooltip("The date to count down to"));
		this.chkTimeOnly.setTooltip(new Tooltip("If checked the countdown will ignore the date"));
		this.spnHours.setTooltip(new Tooltip("The hour (0-23) to count down to"));
		this.spnMinutes.setTooltip(new Tooltip("The minute to count down to"));
		this.spnSeconds.setTooltip(new Tooltip("The second to count down to"));
		this.cmbCountdownFormat.setTooltip(new Tooltip("The format of the countdown"));
		
		// layout
		
		HBox row1 = new HBox(2, this.pkrDate, this.chkTimeOnly);
		HBox row2 = new HBox(2, this.spnHours, this.spnMinutes, this.spnSeconds);
		HBox row3 = new HBox(2, this.cmbCountdownFormat);

		row1.setAlignment(Pos.BASELINE_LEFT);
		VBox layout = new VBox(2, row1, row2, row3);
		
		this.container.setCenter(layout);
		
		// events
		
		this.managedProperty().bind(this.visibleProperty());
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
			this.mutating = true;
			
			LocalDateTime dt = LocalDateTime.now();
			if (nv != null && nv instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent otc = (ObservableCountdownComponent)nv;
				String format = TimeFormatConverter.fromPattern(otc.getCountdownFormat());
				this.cmbCountdownFormat.setValue(format);
				this.setVisible(true);
				
				dt = otc.getCountdownTarget();
			} else {
				this.cmbCountdownFormat.setValue(countdownFormats.get(2));
				this.setVisible(false);
			}
			
			this.setTargetValues(dt);
			
			this.mutating = false;
		});

		this.chkTimeOnly.selectedProperty().addListener((obs, ov, nv) -> {
			// disable the date picker if this is checked
			this.pkrDate.setDisable(nv);
			if (this.mutating) return;
			ObservableSlideRegion<?> comp = this.context.getSelected();
			if (comp != null && comp instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent otc = (ObservableCountdownComponent)comp;
				this.applyCommand(new TimeOnlyEditCommand(ov, nv, otc, this.context.selectedProperty(), this.chkTimeOnly));
			}
		});
		
		this.cmbCountdownFormat.getEditor().textProperty().addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			ObservableSlideRegion<?> comp = this.context.getSelected();
			if (comp != null && comp instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent otc = (ObservableCountdownComponent)comp;
				this.applyCommand(new CountdownFormatEditCommand(ov, nv, otc, this.context.selectedProperty(), this.cmbCountdownFormat));
			}
		});
		
		InvalidationListener listener = (obs) -> {
			if (mutating) return;
			
			LocalDateTime newValue = LocalDateTime.of(
					this.pkrDate.getValue(),
					LocalTime.of(
						this.spnHours.getValue(), 
						this.spnMinutes.getValue(), 
						this.spnSeconds.getValue()));
			
			ObservableSlideRegion<?> comp = this.context.getSelected();
			if (comp != null && comp instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent otc = (ObservableCountdownComponent)comp;
				LocalDateTime oldValue = otc.getCountdownTarget();
				
				this.applyCommand(CommandFactory.chain(
						new CountdownTargetEditCommand(oldValue, newValue, otc, this.context.selectedProperty(), this.pkrDate),
						new ActionEditCommand(null, self -> {
							this.setTargetValues(oldValue);
						}, self -> {
							this.setTargetValues(newValue);
						})));
			}
		};
		
		this.pkrDate.valueProperty().addListener(listener);
		this.spnHours.valueProperty().addListener(listener);
		this.spnMinutes.valueProperty().addListener(listener);
		this.spnSeconds.valueProperty().addListener(listener);
	}
	
	private void setTargetValues(LocalDateTime dt) {
		this.pkrDate.setValue(dt != null ? dt.toLocalDate() : LocalDate.now());
		LocalTime t = dt.toLocalTime();
		this.spnHours.getValueFactory().setValue(t.getHour());
		this.spnMinutes.getValueFactory().setValue(t.getMinute());
		this.spnSeconds.getValueFactory().setValue(t.getSecond());
		
	}
}
