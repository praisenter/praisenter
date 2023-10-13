package org.praisenter.ui.controls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.praisenter.ui.translations.Translations;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class Dialogs {
	private Dialogs() {}

	public static final Alert exception(
			Window owner,
			Throwable... exceptions) {
		return Dialogs.exception(owner, null, null, null, Arrays.asList(exceptions));
	}
	
	public static final Alert exception(
			Window owner,
			String title,
			String header,
			String content,
			Throwable... exceptions) {
		return Dialogs.exception(owner, title, header, content, Arrays.asList(exceptions));
	}
	
	public static final Alert exception(
			Window owner,
			String title,
			String header,
			String content,
			List<Throwable> exceptions) {
		
		Alert alert = new Alert(AlertType.ERROR);
		if (owner != null) {
			alert.initOwner(owner);
			WindowHelper.inheritStylesheets(alert.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(alert.getDialogPane().getScene().getRoot(), owner);
		}
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle(title == null ? Translations.get("error.title") : title);
		alert.setHeaderText(header == null ? Translations.get("error.message") : header);
		alert.setContentText(content == null ? Translations.get("error.message") : content);
		
		// create expandable section with the exceptions in it
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Throwable ex : exceptions) {
			ex.printStackTrace(pw);
			pw.println();
			pw.println();
		}
		String exceptionText = sw.toString();

		Label label = new Label(Translations.get("error.stacktrace"));

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(false);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setPrefSize(800, 600);
		alert.getDialogPane().setExpanded(true);
		
		return alert;
	}
	
	public static final Alert confirmWithOptOut(
			Window owner,
			Modality modality,
			AlertType type, 
			String title, 
			String header, 
			String content,
			String optOutMessage, 
			Consumer<Boolean> optOutAction) {
		Alert alert = new Alert(type);
		// override the dialog pane with a new one with the opt out option
		DialogPane pane = new OptOutDialogPane(optOutMessage, optOutAction);
		// copy the buttons
		pane.getButtonTypes().addAll(alert.getButtonTypes());
		// copy the classes
		pane.getStyleClass().addAll(alert.getDialogPane().getStyleClass());
		alert.setDialogPane(pane);
		alert.setContentText(content);
		// fool the Alert into thinking there is something expanded
		pane.setExpandableContent(new Group());
		pane.setExpanded(true);
		alert.setTitle(title);
		alert.setHeaderText(header);
		
		if (owner != null) {
			alert.initOwner(owner);
			WindowHelper.inheritStylesheets(alert.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(alert.getDialogPane().getScene().getRoot(), owner);
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		pane.setMaxWidth(550);
		
		return alert;
	}
	
	public static final Alert info(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		return alert(owner, modality, AlertType.INFORMATION, title, header, content);
	}
	
	public static final Alert warn(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		return alert(owner, modality, AlertType.WARNING, title, header, content);
	}
	
	private static final Alert alert(
			Window owner,
			Modality modality,
			AlertType type,
			String title, 
			String header, 
			String content) {
		Alert alert = new Alert(type);
		alert.setContentText(content);
		alert.setTitle(title);
		alert.setHeaderText(header);
		
		if (owner != null) {
			alert.initOwner(owner);
			WindowHelper.inheritStylesheets(alert.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(alert.getDialogPane().getScene().getRoot(), owner);

		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(550);
		
		return alert;
	}
	
	public static final Alert confirm(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(content);
		alert.setTitle(title);
		alert.setHeaderText(header);
		
		if (owner != null) {
			alert.initOwner(owner);
			WindowHelper.inheritStylesheets(alert.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(alert.getDialogPane().getScene().getRoot(), owner);
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(550);
		
		return alert;
	}
	
	public static final Alert yesNoCancel(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(content);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.getButtonTypes().setAll(
				ButtonType.YES,
				ButtonType.NO,
				ButtonType.CANCEL);
		
		if (owner != null) {
			alert.initOwner(owner);
			WindowHelper.inheritStylesheets(alert.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(alert.getDialogPane().getScene().getRoot(), owner);
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(550);
		
		return alert;
	}
	
	public static final TextInputDialog textInput(
			Window owner,
			Modality modality,
			String initialValue,
			String title, 
			String header, 
			String content) {
    	TextInputDialog prompt = new TextInputDialog(initialValue);
    	
    	if (owner != null) {
    		prompt.initOwner(owner);
    		WindowHelper.inheritStylesheets(prompt.getDialogPane().getScene(), owner);
    		WindowHelper.inheritPseudoStates(prompt.getDialogPane().getScene().getRoot(), owner);
		}
		if (modality != null) {
			prompt.initModality(modality);
		}
    	
    	prompt.setTitle(title);
    	prompt.setHeaderText(header);
    	prompt.setContentText(content);
    	prompt.setResizable(true);
    	prompt.getDialogPane().setPrefWidth(400);
    	prompt.getDialogPane().setMinWidth(400);
    	
    	return prompt;
	}

	private static class OptOutDialogPane extends DialogPane {
		private final String message;
		private final Consumer<Boolean> action;

		public OptOutDialogPane(String message, Consumer<Boolean> action) {
			this.message = message;
			this.action = action;
		}
		
		@Override
		protected Node createDetailsButton() {
			CheckBox optOut = new CheckBox();
			optOut.setText(this.message);
			optOut.setOnAction(e -> this.action.accept(optOut.isSelected()));
			return optOut;
		}
	};
}
