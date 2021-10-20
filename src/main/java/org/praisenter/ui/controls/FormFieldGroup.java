package org.praisenter.ui.controls;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class FormFieldGroup extends TitledPane {
	private static final String FORM_FIELD_SET_CSS = "p-form-field-group";
	
	public FormFieldGroup() {
		super();
		this.setDefaults();
	}

	public FormFieldGroup(String title, Node content) {
		super(title, content);
		this.setDefaults();
	}
	
	private void setDefaults() {
		this.getStyleClass().add(FORM_FIELD_SET_CSS);
		this.setAnimated(false);
	}
}
