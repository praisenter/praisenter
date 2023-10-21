package org.praisenter.ui.pages;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.ui.Icons;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

final class BackgroundTaskDetailPane extends VBox {
	private final ObjectProperty<ReadOnlyBackgroundTask> task;
	
	private final StringProperty status;
	private final StringProperty statusText;
	private final ObservableList<String> statusTextClasses;
	private final ObservableList<Node> statusIcon;
	
	private final BooleanProperty error;
	private final BooleanProperty complete;
	
	private final StringProperty name;
	private final StringProperty operation;
	private final StringProperty message;
	private final StringProperty type;
	private final StringProperty errorMessage;
	private final StringProperty duration;
	
	public BackgroundTaskDetailPane() {
		this.task = new SimpleObjectProperty<ReadOnlyBackgroundTask>();
		
		this.status = new SimpleStringProperty();
		this.statusText = new SimpleStringProperty();
		this.statusTextClasses = FXCollections.observableArrayList();
		this.statusIcon = FXCollections.observableArrayList();
		
		this.error = new SimpleBooleanProperty();
		this.complete = new SimpleBooleanProperty();
		
		this.name = new SimpleStringProperty();
		this.operation = new SimpleStringProperty();
		this.message = new SimpleStringProperty();
		this.type = new SimpleStringProperty();
		this.errorMessage = new SimpleStringProperty();
		this.duration = new SimpleStringProperty();
		
		this.task.addListener((obs, ov, nv) -> {
			this.status.unbind();
			this.statusText.unbind();
			
			this.error.unbind();
			this.complete.unbind();
			
			this.name.unbind();
			this.operation.unbind();
			this.message.unbind();
			this.type.unbind();
			this.errorMessage.unbind();
			this.duration.unbind();
			
			if (nv != null) {
				this.status.bind(Bindings.createStringBinding(() -> {
					if (!nv.isComplete()) {
						return Icons.PENDING;
					} else if (nv.isSuccess()) { 
						return Icons.SUCCESS;
					} else {
						return Icons.ERROR;
					}
				}, nv.completeProperty(), nv.exceptionProperty()));
				
				this.statusText.bind(Bindings.createStringBinding(() -> {
					if (this.status.get() == Icons.PENDING) {
						return Translations.get("task.status.pending");
					} else if (this.status.get() == Icons.SUCCESS) { 
						return Translations.get("task.status.success");
					} else {
						return Translations.get("task.status.failed");
					}
				}, this.status));
				
				this.error.bind(Bindings.createBooleanBinding(() -> {
					return nv.getException() != null;
				}, nv.exceptionProperty()));
				
				this.complete.bind(nv.completeProperty());
				
				this.name.bind(nv.nameProperty());
				this.operation.bind(nv.operationProperty());
				this.message.bind(nv.messageProperty());
				this.type.bind(nv.typeProperty());
				
				this.errorMessage.bind(Bindings.createStringBinding(() -> {
					Throwable ex = nv.getException();
					if (ex != null) {
						return ex.getMessage();
					}
					return null;
				}, nv.exceptionProperty()));
				
				this.duration.bind(Bindings.createStringBinding(() -> {
					if (nv.isComplete()) {
						long n = nv.getDuration().getNano();
						double s = nv.getDuration().getSeconds();
						double tms = (s * 1000.0) + (n / 1_000_000.0);
						double ts = s + (n / 1_000_000.0 / 1_000.0);
						if (ts >= 60) {
							return Translations.get("task.duration",
									// hours
									String.format("%.00f", Math.floor(ts / 3600)),
									// minutes
									String.format("%.00f", Math.floor((ts % 3600) / 60)),
									// seconds
									String.format("%.00f", Math.floor((ts % 60))));
						} else if (tms >= 1000) {
							return Translations.get("task.duration.seconds", String.format("%.0f", ts));
						} else {
							return Translations.get("task.duration.milliseconds", String.format("%.0f", tms));
						}
					}
					return null;
				}, nv.completeProperty(), nv.startTimeProperty(), nv.endTimeProperty()));
			}
		});
		
		this.status.addListener((obs, ov, nv) -> {
			this.statusTextClasses.removeAll(Styles.TEXT_SMALL, Styles.WARNING, Styles.SUCCESS, Styles.DANGER);
			if (nv == Icons.PENDING) {
				this.statusTextClasses.addAll(Styles.TEXT_SMALL, Styles.WARNING);
			} else if (nv == Icons.SUCCESS) { 
				this.statusTextClasses.addAll(Styles.TEXT_SMALL, Styles.SUCCESS);
			} else {
				this.statusTextClasses.addAll(Styles.TEXT_SMALL, Styles.DANGER);
			}
		});

		this.status.addListener((obs, ov, nv) -> {
			this.statusIcon.clear();
			Region icon = Icons.getIcon(nv);
			if (nv == Icons.PENDING) {
				icon.getStyleClass().add("p-icon-pending");
			} else if (nv == Icons.SUCCESS) { 
				icon.getStyleClass().add("p-icon-success");
			} else {
				icon.getStyleClass().add("p-icon-error");
			}
			this.statusIcon.add(icon);
		});
		
		Button btnCopy = new Button("", Icons.getIcon(Icons.COPY));
		btnCopy.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
		btnCopy.setOnAction(e -> {
			ReadOnlyBackgroundTask task = this.task.get();
			if (task != null) {
				ClipboardContent content = new ClipboardContent();
				String copyText = null;
				Throwable ex = task.getException();
				if (ex != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					copyText = ex.getMessage() + "\n" + sw.toString();
				} else {
					copyText = task.getName();
				}
				
				content.putString(copyText);
				Clipboard clipboard = Clipboard.getSystemClipboard();
				clipboard.setContent(content);
			}
		});
		btnCopy.visibleProperty().bind(this.error);
		btnCopy.managedProperty().bind(btnCopy.visibleProperty());
		
		Label lblErrorMessage = new Label();
		lblErrorMessage.setWrapText(true);
		lblErrorMessage.textProperty().bind(this.errorMessage);

		lblErrorMessage.getStyleClass().add(Styles.TEXT_MUTED);
		lblErrorMessage.visibleProperty().bind(this.error);
		lblErrorMessage.managedProperty().bind(lblErrorMessage.visibleProperty());
		
		Label lblDuration = new Label();
		lblDuration.textProperty().bind(this.duration);
		lblDuration.getStyleClass().add(Styles.TEXT_SMALL);
		HBox layoutDuration = new HBox(7, Icons.getIcon(Icons.TIMER), lblDuration);
		layoutDuration.setAlignment(Pos.CENTER_LEFT);
		layoutDuration.visibleProperty().bind(this.complete);
		layoutDuration.managedProperty().bind(layoutDuration.visibleProperty());
		
		Label lblStatusText = new Label();
		lblStatusText.textProperty().bind(this.statusText);
		// make sure we default to the OOB label styles
		this.statusTextClasses.addAll(lblStatusText.getStyleClass());
		Bindings.bindContent(lblStatusText.getStyleClass(), this.statusTextClasses);
		
		StackPane stkStatusIcon = new StackPane();
		Bindings.bindContent(stkStatusIcon.getChildren(), this.statusIcon);
		HBox layoutStatus = new HBox(7, stkStatusIcon, lblStatusText);
		layoutStatus.setAlignment(Pos.CENTER_LEFT);
		
		Label lblDescription = new Label();
		lblDescription.setWrapText(true);
		lblDescription.textProperty().bind(this.message);

		Label lblName = new Label();
		lblName.getStyleClass().addAll(Styles.TITLE_4);
		lblName.setWrapText(true);
		lblName.textProperty().bind(this.name);
		
		Label lblType = new Label();
		lblType.setWrapText(true);
		lblType.textProperty().bind(this.type);
		lblType.visibleProperty().bind(this.type.isNotNull());
		lblType.managedProperty().bind(lblType.visibleProperty());
		
		HBox header = new HBox(5, lblName);
		header.setAlignment(Pos.CENTER_LEFT);
		
		Button btnOperation = new Button();
		btnOperation.textProperty().bind(this.operation);
		btnOperation.getStyleClass().addAll(Styles.SMALL, Styles.BUTTON_OUTLINED, Styles.ROUNDED);
		HBox tags = new HBox(2, btnOperation, btnCopy);
		tags.setAlignment(Pos.CENTER_LEFT);
		
		HBox footer = new HBox(15, layoutDuration, layoutStatus);
		footer.setAlignment(Pos.CENTER_LEFT);
		
		this.setSpacing(5);
		this.getChildren().addAll(
			header,
			tags,
			new Separator(Orientation.HORIZONTAL),
			lblDescription,
			lblType,
			lblErrorMessage,
			new Separator(Orientation.HORIZONTAL),
			footer
		);
	}
	
	public ReadOnlyBackgroundTask getBackgroundTask() {
		return this.task.get();
	}
	
	public void setBackgroundTask(ReadOnlyBackgroundTask task) {
		this.task.set(task);
	}
	
	public ObjectProperty<ReadOnlyBackgroundTask> backgroundTaskProperty() {
		return this.task;
	}
}