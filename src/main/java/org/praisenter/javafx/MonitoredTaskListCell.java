package org.praisenter.javafx;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

class MonitoredTaskListCell extends ListCell<MonitoredTask<?>> {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final Glyph success = FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN);
	private final Glyph error = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED);
	private final Glyph cancel = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.LIGHTGRAY);
	private final Glyph warn = FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.GOLD);
	
	private final ObjectProperty<Worker.State> status = new SimpleObjectProperty<>(Worker.State.READY);
	private final ObjectProperty<MonitoredTaskResultStatus> result = new SimpleObjectProperty<>();
	private final ProgressIndicator indicator = new ProgressIndicator();
	private final Tooltip tooltip = new Tooltip();
	
	public MonitoredTaskListCell() {
		this.indicator.setPrefSize(30, 30);
		this.setTooltip(this.tooltip);
		this.setPrefWidth(180);
		this.setMaxWidth(USE_PREF_SIZE);
		this.setWrapText(false);
		this.setTextOverrun(OverrunStyle.ELLIPSIS);
		this.setContentDisplay(ContentDisplay.LEFT);
		this.setAlignment(Pos.BASELINE_LEFT);
		this.setGraphic(this.indicator);
		this.status.addListener((obs, ov, nv) -> {
			setTextGraphic();
		});
		this.result.addListener((obs, ov, nv) -> {
			setTextGraphic();
		});
	}
	
	private void setTextGraphic() {
		Worker.State nv = this.status.get();
		MonitoredTaskResultStatus status = this.result.get();
		
		// unless the task result has been set, use the task's status
		if (status == null) {
			if (nv == Worker.State.RUNNING || nv == Worker.State.READY || nv == Worker.State.SCHEDULED) {
				this.setGraphic(this.indicator);
			} else if (nv == Worker.State.CANCELLED) {
				this.setGraphic(cancel);
			} else if (nv == Worker.State.FAILED) {
				this.setGraphic(error);
			} else if (nv == Worker.State.SUCCEEDED) {
				this.setGraphic(success);
			} else {
				this.setGraphic(null);
			}
		} else {
			// otherwise use the result status
			if (status == MonitoredTaskResultStatus.WARNING) {
				this.setGraphic(warn);
			} else if (status == MonitoredTaskResultStatus.ERROR) {
				this.setGraphic(error);
			} else if (status == MonitoredTaskResultStatus.SUCCESS) {
				this.setGraphic(success);
			} else {
				this.setGraphic(null);
			}
		}
	}
	
	@Override
	protected void updateItem(MonitoredTask<?> item, boolean empty) {
		super.updateItem(item, empty);
		this.indicator.progressProperty().unbind();
		this.status.unbind();
		this.result.unbind();
		if (empty) {
			this.setGraphic(null);
		} else {
			this.indicator.progressProperty().bind(item.progressProperty());
			this.status.bind(item.stateProperty());
			this.result.bind(item.resultStatusProperty());
			setTextGraphic();
			this.setText(item.getName());
			this.tooltip.setText(item.getName());
		}
	}
}
