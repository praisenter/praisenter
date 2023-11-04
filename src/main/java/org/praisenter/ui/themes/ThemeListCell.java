package org.praisenter.ui.themes;

import javafx.scene.control.ListCell;

public class ThemeListCell extends ListCell<AtlantaFXTheme> {
	@Override
	protected void updateItem(AtlantaFXTheme item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setText(null);
		} else {
			this.setText(item.getTheme().getName());
		}
	}
}
