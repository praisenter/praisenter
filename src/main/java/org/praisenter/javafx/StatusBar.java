package org.praisenter.javafx;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Callback;

// TODO translate
public class StatusBar extends HBox {
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
					final ProgressBar bar = new ProgressBar();
					final Tooltip tooltip = new Tooltip();
					{
						bar.setPrefWidth(150);
						setTooltip(tooltip);
						setPrefWidth(180);
						setMaxWidth(USE_PREF_SIZE);
					}
					@Override
					protected void updateItem(MonitoredTask<?> item, boolean empty) {
						super.updateItem(item, empty);
						bar.progressProperty().unbind();
						if (empty) {
							this.setText(null);
							this.setGraphic(null);
						} else {
							bar.progressProperty().bind(item.progressProperty());
							this.setText(item.getName());
							this.tooltip.setText(item.getName());
							this.setTextOverrun(OverrunStyle.ELLIPSIS);
							this.setContentDisplay(ContentDisplay.BOTTOM);
							this.setWrapText(false);
							this.setAlignment(Pos.CENTER);
							this.setGraphic(bar);
						}
					}
				};
			}
		});
		BorderPane layout = new BorderPane(view);
		
//		Popup pop1 = new Popup();
//		pop1.getContent().add(layout);
		
		PopOver pop = new PopOver(layout);
		pop.setDetachable(false);
		pop.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		pop.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		button.setOnAction(e -> {
			// show task detail view
			pop.show(button);
//			pop1.show(getScene().getWindow());
		});
		
		this.setSpacing(2);
		this.getChildren().addAll(button);
	}
}
