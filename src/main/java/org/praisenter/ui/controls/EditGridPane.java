package org.praisenter.ui.controls;

import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;

public class EditGridPane extends RowVisGridPane {
	public EditGridPane() {
		this.getStyleClass().add("p-edit-grid-pane");
		
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
		
		// TODO move to CSS
		this.setVgap(3);
		this.setHgap(3);
	}
}
