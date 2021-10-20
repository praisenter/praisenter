package org.praisenter.ui.controls;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;

public class FormFieldSection extends RowVisGridPane {
	private static final String FORM_FIELD_SECTION_CSS = "p-form-field-section";
	private static final String FORM_FIELD_SECTION_HEADER_CSS = "p-form-field-section-header";
	private static final String FORM_FIELD_SECTION_HELP_CSS = "p-form-field-section-help";
	
	private int row = 0;
	
	public FormFieldSection() {
		this(null);
	}
	
	public FormFieldSection(String header) {
		super();
		this.getStyleClass().add(FORM_FIELD_SECTION_CSS);
		
		if (header != null && !header.isBlank()) {
			Label lblHeader = new Label(header);
			lblHeader.getStyleClass().add(FORM_FIELD_SECTION_HEADER_CSS);
			this.add(lblHeader, 0, this.row++, 2);
		}
		
		ColumnConstraints cc0 = new ColumnConstraints();
		cc0.setFillWidth(true);
		cc0.setHalignment(HPos.LEFT);
		cc0.setPercentWidth(30);
		this.getColumnConstraints().add(cc0);
		
		ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setFillWidth(true);
		cc1.setHalignment(HPos.LEFT);
		cc1.setPercentWidth(70);
		this.getColumnConstraints().add(cc1);
	}
	
	public int addField(Node field) {
		return this.addField(null, null, field);
	}
	
	public int addField(String label, Node field) {
		return this.addField(label, null, field);
	}
	
	public int addField(String label, String helpText, Node field) {
		Label lblFieldLabel = null;
		if (label != null) {
			lblFieldLabel = new Label(label);
		}
		
		int row = this.row;
		this.row++;
		
		if (lblFieldLabel != null) {
			this.add(lblFieldLabel, 0, row);
		}
		
		this.add(field, lblFieldLabel != null ? 1 : 0, row, lblFieldLabel != null ? 1 : 2);

		if (helpText != null && !helpText.isBlank()) {
			Label lblHelpText = new Label(helpText);
			lblHelpText.getStyleClass().add(FORM_FIELD_SECTION_HELP_CSS);
			this.add(lblHelpText, 1, this.row++);
		}
		
		return row;
	}
	
	public int addSubSection(Node section) {
		int row = this.row;
		this.add(section, 0, this.row++, 2);
		return row;
	}
}
