package org.praisenter.ui.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public final class TextAreaInputDialog extends Alert {
	private final StringProperty text;
	
	public TextAreaInputDialog(AlertType alertType) {
		super(alertType);

		this.text = new SimpleStringProperty();
		
		TextArea textArea = new TextArea();
		textArea.setWrapText(false);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		textArea.textProperty().bindBidirectional(this.text);

		DialogPane dp = this.getDialogPane();
		dp.setContent(new BorderPane(textArea));
		dp.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	}
	
	public String getText() {
		return this.text.get();
	}
	
	public void setText(String text) {
		this.text.set(text);
	}
	
	public StringProperty textProperty() {
		return this.text;
	}
}
