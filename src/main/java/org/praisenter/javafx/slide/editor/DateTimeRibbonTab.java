package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class DateTimeRibbonTab extends ComponentEditorRibbonTab {

	private final TextField txtDateFormat;
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
		
		this.txtDateFormat = new TextField();
		this.txtDateFormat.setEditable(false);
		this.txtDateFormat.setPrefWidth(200);
		this.txtDateFormat.setTooltip(new Tooltip());
		
		this.cmbDateTimeFormat = new ComboBox<String>(dateTimeFormats);
		this.cmbDateTimeFormat.setPrefWidth(200);
		this.cmbDateTimeFormat.setEditable(true);
		this.cmbDateTimeFormat.setValue(dateTimeFormats.get(0));
		
		// layout
		
		HBox row1 = new HBox(2, this.cmbDateTimeFormat);
		HBox row2 = new HBox(2, this.txtDateFormat);

		VBox layout = new VBox(2, row1, row2);
		
		this.container.setCenter(layout);
		
		// events
		
		this.cmbDateTimeFormat.getEditor().textProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			SimpleDateFormat format = updateExample(nv);
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent otc = (ObservableDateTimeComponent)comp;
				otc.setFormat(format);
			}
		});
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv instanceof ObservableDateTimeComponent) {
				this.setDisable(false);
				ObservableDateTimeComponent otc = (ObservableDateTimeComponent)nv;
				String format = "EEEE MMMM, d yyyy";
				SimpleDateFormat fmt = otc.getFormat();
				if (fmt != null) {
					format = fmt.toPattern();
				}
				this.cmbDateTimeFormat.setValue(format);
				updateExample(format);
			} else {
				this.setDisable(true);
			}
			mutating = false;
		});
	}
	
	private SimpleDateFormat updateExample(String format) {
		SimpleDateFormat sdf = null;
		String text = "";
		if (format != null && format.trim().length() > 0) {
			try {
				sdf = new SimpleDateFormat(format);
				text = sdf.format(new Date());
			} catch (Exception e) {
				text = "Invalid format";
			}
		} else {
			text = "Invalid format";
		}
		
		this.txtDateFormat.setText(text);
		this.txtDateFormat.getTooltip().setText(text);
		
		return sdf;
	}
}
