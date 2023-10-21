package org.praisenter.ui.document;

import java.util.function.Supplier;

import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;

public final class DocumentsToolbar extends ToolBar {
	private static final String ACTION_BAR_CSS = "p-action-bar";
	
	private final GlobalContext context;
	
	public DocumentsToolbar(GlobalContext context) {
		this.getStyleClass().add(ACTION_BAR_CSS);
		
		this.context = context;
		
		this.setOrientation(Orientation.HORIZONTAL);
		this.getItems().addAll(
			this.createButton(Action.NEW_SLIDE),
			this.createButton(Action.NEW_BIBLE),
			this.createButton(Action.NEW_SONG),
			this.createSeparatorForGroup(Action.NEW_BOOK, Action.NEW_CHAPTER, Action.NEW_VERSE),
			this.createButton(Action.NEW_BOOK),
			this.createButton(Action.NEW_CHAPTER),
			this.createButton(Action.NEW_VERSE),
			this.createSeparatorForGroup(Action.NEW_LYRICS, Action.NEW_SECTION, Action.NEW_AUTHOR, Action.NEW_SONGBOOK),
			this.createButton(Action.NEW_LYRICS),
			this.createButton(Action.NEW_AUTHOR),
			this.createButton(Action.NEW_SONGBOOK),
			this.createButton(Action.NEW_SECTION),
			this.createSeparatorForGroup(Action.NEW_SLIDE_TEXT_COMPONENT, Action.NEW_SLIDE_PLACEHOLDER_COMPONENT, Action.NEW_SLIDE_DATETIME_COMPONENT, Action.NEW_SLIDE_COUNTDOWN_COMPONENT, Action.NEW_SLIDE_MEDIA_COMPONENT),
			this.createButton(Action.NEW_SLIDE_TEXT_COMPONENT),
			this.createButton(Action.NEW_SLIDE_PLACEHOLDER_COMPONENT),
			this.createButton(Action.NEW_SLIDE_DATETIME_COMPONENT),
			this.createButton(Action.NEW_SLIDE_COUNTDOWN_COMPONENT),
			this.createButton(Action.NEW_SLIDE_MEDIA_COMPONENT),
			this.createSeparatorForGroup(Action.SAVE, Action.SAVE_ALL),
			this.createButton(Action.SAVE),
			this.createButton(Action.SAVE_ALL),
			this.createSeparatorForGroup(Action.UNDO, Action.REDO),
			this.createButton(Action.UNDO),
			this.createButton(Action.REDO),
			this.createSeparatorForGroup(Action.COPY, Action.CUT, Action.PASTE, Action.DELETE),
			this.createButton(Action.COPY),
			this.createButton(Action.CUT),
			this.createButton(Action.PASTE),
			this.createButton(Action.DELETE),
			this.createSeparatorForGroup(Action.SELECT_ALL, Action.SELECT_INVERT, Action.SELECT_NONE),
			this.createButton(Action.SELECT_ALL),
			this.createButton(Action.SELECT_INVERT),
			this.createButton(Action.SELECT_NONE),
			this.createSeparatorForGroup(Action.RENUMBER, Action.REORDER),
			this.createButton(Action.RENUMBER),
			this.createButton(Action.REORDER),
			this.createSeparatorForGroup(Action.SLIDE_COMPONENT_MOVE_UP, Action.SLIDE_COMPONENT_MOVE_DOWN, Action.SLIDE_COMPONENT_MOVE_FRONT, Action.SLIDE_COMPONENT_MOVE_BACK),
			this.createButton(Action.SLIDE_COMPONENT_MOVE_UP),
			this.createButton(Action.SLIDE_COMPONENT_MOVE_DOWN),
			this.createButton(Action.SLIDE_COMPONENT_MOVE_FRONT),
			this.createButton(Action.SLIDE_COMPONENT_MOVE_BACK)
		);
	}
	
	private Button createButton(Action action) {
		Button button = new Button();
		this.setButtonProperties(button, action);
		button.setOnAction(e -> this.executeAction(action));
		return button;
	}

	private void setButtonProperties(Labeled button, Action action) {
		button.setUserData(action);
		button.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		button.visibleProperty().bind(this.context.getActionVisibleProperty(action));
		button.managedProperty().bind(button.visibleProperty());
		// act like a menu - don't receive focus
		button.setFocusTraversable(false);
		
		Supplier<Node> graphicSupplier = action.getGraphicSupplier();
		if (graphicSupplier != null) {
			Node graphic = graphicSupplier.get();
			button.setGraphic(graphic);
		} else {
			button.setText(Translations.get(action.getMessageKey()));
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
			button.setTooltip(new Tooltip(text));
		}
		
		button.setMaxWidth(Double.MAX_VALUE);
	}
	
	private Separator createSeparatorForGroup(Action... actions) {
		Separator sep = new Separator(Orientation.VERTICAL);
		
		ReadOnlyBooleanProperty[] dependents = new ReadOnlyBooleanProperty[actions.length];
		for (int i = 0; i < actions.length; i++) {
			dependents[i] = this.context.getActionVisibleProperty(actions[i]);
		}
		
		sep.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			boolean visible = false;
			for (ReadOnlyBooleanProperty prop : dependents) {
				visible |= prop.get();
			}
			return visible;
		}, dependents));
		sep.managedProperty().bind(sep.visibleProperty());
		
		return sep;
	}
	
	private void executeAction(Action action) {
		this.context.executeAction(action);
	}
}
