package org.praisenter.ui.controls;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class FormField extends VBox {
	private static final String FORM_FIELD_CSS = "p-form-field";
	private static final String FORM_FIELD_LABEL_CSS = "p-form-field-label";
	private static final String FORM_FIELD_VALUE_CSS = "p-form-field-value";
	private static final String FORM_FIELD_HELP_CSS = "p-form-field-help";

	public FormField(
			String label, 
			Node... fields) {
		this(label, null, false, fields);
	}
	
	public FormField(
			String label,
			String helpText,
			Node... fields) {
		this(label, helpText, false, fields);
	}
	
	public FormField(
			String label, 
			String helpText, 
			boolean useTooltip,
			Node... fields) {
		super();
		
		this.getStyleClass().add(FORM_FIELD_CSS);
		
		Label lbl = new Label(label);
		lbl.getStyleClass().add(FORM_FIELD_LABEL_CSS);
		this.getChildren().add(lbl);
		
		if (helpText != null && !helpText.isBlank()) {
			if (useTooltip) {
				Tooltip tt = new Tooltip(helpText);
//				Label graphic = Glyphs.INFO.duplicate();
//				graphic.setTooltip(tt);
//				lbl.setGraphic(graphic);
			} else {
				Label desc = new Label(helpText);
				desc.setWrapText(true);
				desc.getStyleClass().add(FORM_FIELD_HELP_CSS);
				this.getChildren().add(desc);
			}
		}		

		if (fields.length > 1) {
			HBox fc = new HBox(fields);
			fc.getStyleClass().add(FORM_FIELD_VALUE_CSS);
			fc.setFillHeight(true);
			this.getChildren().add(fc);
		} else {
			this.getChildren().add(fields[0]);
		}
		
		this.setFillWidth(true);
	}
}
