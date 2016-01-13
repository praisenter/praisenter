package org.praisenter.javafx;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public final class Alerts {
	/** Hidden constructor */
	private Alerts() {}
	
	public static final Alert exception(Exception... exceptions) {
		return exception(null, null, null, exceptions);
	}
	
	public static final Alert exception(String content, Exception... exceptions) {
		return exception(null, null, content, exceptions);
	}
	
	public static final Alert exception(String title, String content, Exception... exceptions) {
		return exception(title, null, content, exceptions);
	}
	
	public static final Alert exception(
			String title,
			String header,
			String content,
			Exception... exceptions) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title == null ? Translations.getTranslation("error.alert.title") : title);
		alert.setHeaderText(header == null ? Translations.getTranslation("error.alert.message") : header);
		alert.setContentText(content == null ? Translations.getTranslation("error.alert.message") : content);

		// create expandable section with the exceptions in it
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Exception ex : exceptions) {
			ex.printStackTrace(pw);
			pw.println();
			pw.println();
		}
		String exceptionText = sw.toString();

		Label label = new Label(Translations.getTranslation("error.alert.stacktrace"));

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
		alert.getDialogPane().setPrefWidth(550);
		
		return alert;
	}
}
