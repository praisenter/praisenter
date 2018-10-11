package org.praisenter.ui;

import org.praisenter.ui.events.ActionPromptPaneCompleteEvent;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class SaveAsPromptPane extends VBox {
	private final StringProperty title;
	private final StringProperty message;
	private final StringProperty name;
	
	public SaveAsPromptPane() {
		// properties
		this.title = new SimpleStringProperty();
		this.message = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		
		// ui
		TitledPane ttlTitle = new TitledPane();
		ttlTitle.textProperty().bind(this.title);
		ttlTitle.setCollapsible(false);
		
		Label lblMessage = new Label();
		lblMessage.textProperty().bind(this.message);
		
		TextField txtName = new TextField();
		txtName.textProperty().bindBidirectional(this.name);
		
		Button ok = new Button(Translations.get("save"));
		Button cancel = new Button(Translations.get("cancel"));
		
		ok.setOnAction(e -> accept());
		cancel.setOnAction(e -> cancel());
		
		ttlTitle.setContent(new VBox(5, lblMessage, txtName, new HBox(5, cancel, ok)));
		
		this.getChildren().add(ttlTitle);
	}
	
	private void accept() {
		this.fireEvent(new ActionPromptPaneCompleteEvent(this, this, ActionPromptPaneCompleteEvent.ACCEPT));
	}
	
	private void cancel() {
		this.fireEvent(new ActionPromptPaneCompleteEvent(this, this, ActionPromptPaneCompleteEvent.CANCEL));
	}
	
	public String getTitle() {
		return this.title.get();
	}
	
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	public StringProperty titleProperty() {
		return this.title;
	}
	
	public String getMessage() {
		return this.message.get();
	}
	
	public void setMessage(String message) {
		this.message.set(message);
	}
	
	public StringProperty messageProperty() {
		return this.message;
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
}
