package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;

import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class DateTimeRibbonTab extends ComponentEditorRibbonTab {

	private final ComboBox<String> cmbDateTimeFormat;
	
	public DateTimeRibbonTab() {
		super("Date Time");

		ObservableList<String> dateTimeFormats = FXCollections.observableArrayList();
		dateTimeFormats.add("EEEE MMMM, d yyyy");
		dateTimeFormats.add("EEEE MMMM, d yyyy h:mm a");
		dateTimeFormats.add("EEEE MMMM, d yyyy h:mm a z");
		dateTimeFormats.add("EEE MMM, d yyyy");
		dateTimeFormats.add("EEE MMM, d yyyy h:mm a");
		dateTimeFormats.add("EEE MMM, d yyyy h:mm a z");
		dateTimeFormats.add("M/d/yyyy");
		dateTimeFormats.add("M/d/yyyy h:mm a");
		dateTimeFormats.add("M/d/yyyy h:mm a z");

		this.cmbDateTimeFormat = new ComboBox<String>(dateTimeFormats);
		this.cmbDateTimeFormat.setPrefWidth(175);
		this.cmbDateTimeFormat.setEditable(true);
		this.cmbDateTimeFormat.setValue(dateTimeFormats.get(0));
		
		// tooltip
		
		this.cmbDateTimeFormat.setTooltip(new Tooltip("The format of the date and time"));
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbDateTimeFormat);

		VBox layout = new VBox(2, row1);
		
		this.container.setCenter(layout);
		
		// events
		this.managedProperty().bind(this.visibleProperty());
		
		this.cmbDateTimeFormat.getEditor().textProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			SimpleDateFormat format = getFormat(nv);
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent otc = (ObservableDateTimeComponent)comp;
				otc.setDateTimeFormat(format);
			}
		});
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent otc = (ObservableDateTimeComponent)nv;
				String format = "EEEE MMMM, d yyyy";
				SimpleDateFormat fmt = otc.getDateTimeFormat();
				if (fmt != null) {
					format = fmt.toPattern();
				}
				this.cmbDateTimeFormat.setValue(format);
				this.setVisible(true);
			} else {
				this.setVisible(false);
			}
			mutating = false;
		});
	}
	
	private SimpleDateFormat getFormat(String format) {
		if (format != null && format.trim().length() > 0) {
			try {
				return new SimpleDateFormat(format);
			} catch (Exception e) {
				// TODO log
			}
		}
		return null;
	}
}
