package org.praisenter.ui.themes;

import javafx.scene.control.ListCell;

public class ThemeListCell extends ListCell<atlantafx.base.theme.Theme> {
	@Override
	protected void updateItem(atlantafx.base.theme.Theme item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setText(null);
		} else {
			this.setText(item.getName());
		}
	}
}
