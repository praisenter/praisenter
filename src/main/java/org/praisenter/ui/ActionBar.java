package org.praisenter.ui;

import java.util.function.Supplier;

import org.praisenter.ui.translations.Translations;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;

public final class ActionBar extends ToolBar {
	private final GlobalContext context;
	
	public ActionBar(GlobalContext context) {
		this.getStyleClass().add("p-action-bar");
		
		this.context = context;
		
		this.setOrientation(Orientation.VERTICAL);
		this.getItems().addAll(
			this.createButton(Action.SAVE),
			this.createButton(Action.SAVE_ALL),
			this.createButton(Action.UNDO),
			this.createButton(Action.REDO),
			this.createButton(Action.NEW),
			this.createButton(Action.NEW_BOOK),
			this.createButton(Action.NEW_CHAPTER),
			this.createButton(Action.NEW_VERSE),
			this.createButton(Action.COPY),
			this.createButton(Action.CUT),
			this.createButton(Action.PASTE),
			this.createButton(Action.DELETE),
			this.createButton(Action.SELECT_ALL),
			this.createButton(Action.SELECT_INVERT),
			this.createButton(Action.SELECT_NONE),
			this.createButton(Action.RENAME),
			this.createButton(Action.RENUMBER),
			this.createButton(Action.REORDER),
			this.createButton(Action.IMPORT),
			this.createButton(Action.EXPORT)
		);
	}
	
	private Button createButton(Action action) {
		Button button = new Button();
		
		button.setUserData(action);
		button.setOnAction(e -> this.executeAction(action));
		button.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		button.visibleProperty().bind(this.context.getActionVisibleProperty(action));
		button.managedProperty().bind(button.visibleProperty());
		// act like a menu - don't receive focus
		button.setFocusTraversable(false);
		
		Supplier<Node> graphicSupplier = action.getGraphicSupplier();
		if (graphicSupplier != null) {
			Node graphic = graphicSupplier.get();
			button.setGraphic(graphic);
		}
		
		KeyCombination accelerator = action.getAccelerator();
		if (accelerator != null) {
			this.context.getScene().getAccelerators().put(accelerator, () -> {
				this.executeAction(action);
			});
		}
		
		String messageKey = action.getMessageKey();
		if (messageKey != null) {
			String text = Translations.get(messageKey);
			button.setText(text);
			button.setTooltip(new Tooltip(text));
		}
		
		return button;
	}
	
	private void executeAction(Action action) {
		this.context.executeAction(action);
	}
}
