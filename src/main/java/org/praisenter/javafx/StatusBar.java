package org.praisenter.javafx;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Callback;

// TODO translate
public class StatusBar extends HBox {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	

	
	private final PraisenterContext context;
	
	public StatusBar(PraisenterContext context) {
		this.context = context;

		Button button = new Button("Progress");
		button.setContentDisplay(ContentDisplay.RIGHT);
		
		ProgressBar progress = new ProgressBar();
		progress.setProgress(0);
		this.context.getExecutorService().runningProperty().addListener((obs, ov, nv) -> {
			if (nv.intValue() > 0) {
				progress.setProgress(-1);
			} else {
				progress.setProgress(0);
			}
		});
		progress.setMinWidth(100);
		button.setGraphic(progress);
		
		ListView<MonitoredTask<?>> view = new ListView<MonitoredTask<?>>(context.getExecutorService().tasksProperty());
		view.setPrefSize(200, 300);
		view.setCellFactory(new Callback<ListView<MonitoredTask<?>>, ListCell<MonitoredTask<?>>>() {
			@Override
			public ListCell<MonitoredTask<?>> call(ListView<MonitoredTask<?>> param) {
				return new ListCell<MonitoredTask<?>>() {
					private final Glyph success = FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN);
					private final Glyph error = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED);
					private final Glyph cancel = FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.LIGHTGRAY);
					private final Glyph warn = FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.GOLD);
					
					final ObjectProperty<Worker.State> status = new SimpleObjectProperty<>(Worker.State.READY);
					final ObjectProperty<MonitoredTaskResultStatus> result = new SimpleObjectProperty<>();
					final ProgressIndicator indicator = new ProgressIndicator();
					final Tooltip tooltip = new Tooltip();
					
					{
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
				};
			}
		});
		BorderPane layout = new BorderPane(view);
		
		PopOver pop = new PopOver(layout);
		pop.setDetachable(false);
		pop.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		pop.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		pop.setAutoFix(false);
		
		button.setOnAction(e -> {
			// show task detail view
			pop.show(button);
		});
		
		this.setSpacing(2);
		this.getChildren().addAll(button);
	}
}
