/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.controls;

import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 * Helper class for generating alerts.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Alerts {
	/** The max width of a dialog */
	private static final double MAX_WIDTH = 550;
	
	/** Hidden constructor */
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
		
		Alert alert = new Alert(AlertType.ERROR);
		if (owner != null) {
			alert.initOwner(owner);
		}
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle(title == null ? Translations.get("error.alert.title") : title);
		alert.setHeaderText(header == null ? Translations.get("error.alert.message") : header);
		alert.setContentText(content == null ? Translations.get("error.alert.message") : content);

		// create expandable section with the exceptions in it
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Throwable ex : exceptions) {
			ex.printStackTrace(pw);
			pw.println();
			pw.println();
		}
		String exceptionText = sw.toString();

		Label label = new Label(Translations.get("error.alert.stacktrace"));

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
	public static final Alert optOut(
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
	
	/**
	 * Custom dialog pane that overrides the details button to use
	 * a check box instead.
	 * @author William Bittle
	 * @version 3.0.0
	 */
	private static class OptOutDialogPane extends DialogPane {
		/** The opt out message */
		private final String message;
		
		/** The opt out action */
		private final Consumer<Boolean> action;
		
		/**
		 * Full constructor.
		 * @param message the opt out message
		 * @param action the opt out action
		 */
		public OptOutDialogPane(String message, Consumer<Boolean> action) {
			this.message = message;
			this.action = action;
		}
		
		/* (non-Javadoc)
		 * @see javafx.scene.control.DialogPane#createDetailsButton()
		 */
		@Override
		protected Node createDetailsButton() {
			CheckBox optOut = new CheckBox();
			optOut.setText(this.message);
			optOut.setOnAction(e -> this.action.accept(optOut.isSelected()));
			return optOut;
		}
	};
}
