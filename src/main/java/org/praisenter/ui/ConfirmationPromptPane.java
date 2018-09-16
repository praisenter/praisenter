package org.praisenter.ui;

import org.praisenter.ui.events.ActionPromptPaneCompleteEvent;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class ConfirmationPromptPane extends VBox {
	private final StringProperty title;
	private final StringProperty message;
	private final BooleanProperty askAgain;
	private final BooleanProperty showAskAgain;
	
	public ConfirmationPromptPane() {
		// properties
		this.title = new SimpleStringProperty();
		this.message = new SimpleStringProperty();
		this.askAgain = new SimpleBooleanProperty(true);
		this.showAskAgain = new SimpleBooleanProperty(false);
		
		this.setFocusTraversable(false);
		
		// ui
		TitledPane ttlTitle = new TitledPane();
		ttlTitle.textProperty().bind(this.title);
		ttlTitle.setCollapsible(false);
		ttlTitle.setFocusTraversable(false);
		
		Label lblMessage = new Label();
		lblMessage.textProperty().bind(this.message);
		lblMessage.setFocusTraversable(false);
		
		CheckBox chkAskAgain = new CheckBox();
		chkAskAgain.setText(Translations.get("askagain"));
		chkAskAgain.visibleProperty().bind(this.showAskAgain);
		chkAskAgain.managedProperty().bind(this.showAskAgain);
		chkAskAgain.setFocusTraversable(false);
		this.askAgain.bind(chkAskAgain.selectedProperty());
		
		Button ok = new Button(Translations.get("ok"));
		Button cancel = new Button(Translations.get("cancel"));
		
		ok.setFocusTraversable(false);
		cancel.setFocusTraversable(false);
		
		ok.setOnAction(e -> accept());
		cancel.setOnAction(e -> cancel());
		
		ttlTitle.setContent(new VBox(5, lblMessage, chkAskAgain, new HBox(5, cancel, ok)));
		
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
	
	public boolean getAskAgain() {
		return this.askAgain.get();
	}
	
	public void setAskAgain(boolean askAgain) {
		this.askAgain.set(askAgain);
	}
	
	public BooleanProperty askAgainProperty() {
		return this.askAgain;
	}
	
	public boolean getShowAskAgain() {
		return this.showAskAgain.get();
	}
	
	public void setShowAskAgain(boolean showAskAgain) {
		this.showAskAgain.set(showAskAgain);
	}
	
	public BooleanProperty showAskAgainProperty() {
		return this.showAskAgain;
	}
}
