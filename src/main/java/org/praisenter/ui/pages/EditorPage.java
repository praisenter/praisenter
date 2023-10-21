package org.praisenter.ui.pages;

import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.document.DocumentsToolbar;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public final class EditorPage extends BorderPane {
	public EditorPage(GlobalContext context) {
		final DocumentsToolbar actionBar = new DocumentsToolbar(context);
		final DocumentsPane documentsPane = new DocumentsPane(context);
		
		Label lblSelectItemToEdit = new Label(Translations.get("editor.item.select"));
		lblSelectItemToEdit.setWrapText(true);
		
		this.setTop(actionBar);
		this.setCenter(documentsPane);
		
		// show the label when nothing is being edited, otherwise show the editor
		this.centerProperty().bind(Bindings.createObjectBinding(() -> {
			if (context.getOpenDocumentsUnmodifiable().size() > 0) {
				return documentsPane;
			}
			return lblSelectItemToEdit;
		}, context.getOpenDocumentsUnmodifiable()));
	}
}
