package org.praisenter.javafx.slide.editor.ribbon;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.DateFormatEditCommand;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class DateTimeRibbonTab extends ComponentEditorRibbonTab {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ComboBox<String> cmbDateTimeFormat;
	
	public DateTimeRibbonTab(SlideEditorContext context) {
		super(context, "Date Time");

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
		this.cmbDateTimeFormat.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
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
			ObservableSlideRegion<?> comp = this.context.getSelected();
			if (comp != null && comp instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent otc = (ObservableDateTimeComponent)comp;
				this.applyCommand(new DateFormatEditCommand(ov, nv, otc, this.context.selectedProperty(), this.cmbDateTimeFormat));
			}
		});
		
		this.context.selectedProperty().addListener((obs, ov, nv) -> {
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
}
