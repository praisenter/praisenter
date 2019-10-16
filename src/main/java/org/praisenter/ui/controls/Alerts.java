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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class Alerts {
	private static final double MAX_WIDTH = 550;

	private Alerts() {}

	/**
	 * Creates an alert for the given exception(s) with the given title, header and content.
	 * <p>
	 * The exceptions will have their stacktraces placed in an expandable area.
	 * <p>
	 * Ideally the header and content are descriptive enough for a user to make a decision on
	 * what to do, with the exception stacktraces there for sending to support.
	 * @param owner the owner of this alert
	 * @param title the alert window title; if null, a generic message will be used
	 * @param header the alert's header section; if null, a generic message will be used
	 * @param content the alert's content section; if null, a generic message will be used
	 * @param exceptions the exception(s)
	 * @return Alert
	 */
	public static final Alert exception(
			Window owner,
			String title,
			String header,
			String content,
			Throwable... exceptions) {
		return Alerts.exception(owner, title, header, content, Arrays.asList(exceptions));
	}
	
	/**
	 * Creates an alert for the given exception(s) with the given title, header and content.
	 * <p>
	 * The exceptions will have their stacktraces placed in an expandable area.
	 * <p>
	 * Ideally the header and content are descriptive enough for a user to make a decision on
	 * what to do, with the exception stacktraces there for sending to support.
	 * @param owner the owner of this alert
	 * @param title the alert window title; if null, a generic message will be used
	 * @param header the alert's header section; if null, a generic message will be used
	 * @param content the alert's content section; if null, a generic message will be used
	 * @param exceptions the exception(s)
	 * @return Alert
	 */
	public static final Alert exception(
			Window owner,
			String title,
			String header,
			String content,
			List<Throwable> exceptions) {
		
		Alert alert = new Alert(AlertType.ERROR);
		if (owner != null) {
			alert.initOwner(owner);
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
		alert.getDialogPane().setMaxWidth(MAX_WIDTH);
		
		return alert;
	}
	
	/**
	 * Creates a new dialog with an opt out option.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param type the type of alert
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @param optOutMessage the opt out label
	 * @param optOutAction the action to execute when the opt out is changed
	 * @return Alert
	 */
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
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		pane.setMaxWidth(MAX_WIDTH);
		
		return alert;
	}
	
	/**
	 * Creates a new info dialog.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @return Alert
	 */
	public static final Alert info(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		return alert(owner, modality, AlertType.INFORMATION, title, header, content);
	}
	
	/**
	 * Creates a new info dialog.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @return Alert
	 */
	public static final Alert warn(
			Window owner,
			Modality modality,
			String title, 
			String header, 
			String content) {
		return alert(owner, modality, AlertType.WARNING, title, header, content);
	}
	
	/**
	 * Creates a new alert dialog.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param type the alert type
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @return Alert
	 */
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
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(MAX_WIDTH);
		
		return alert;
	}
	
	/**
	 * Creates a new confirm dialog.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @return Alert
	 */
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
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(MAX_WIDTH);
		
		return alert;
	}
	
	/**
	 * Creates a new Yes-No-Cancel confirmation dialog.
	 * @param owner the owner of this alert
	 * @param modality the modality of the alert
	 * @param title the alert window title
	 * @param header the alert's header section
	 * @param content the alert's content section
	 * @return Alert
	 */
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
		}
		if (modality != null) {
			alert.initModality(modality);
		}
		alert.getDialogPane().setMaxWidth(MAX_WIDTH);
		
		return alert;
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
