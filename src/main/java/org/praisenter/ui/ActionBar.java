package org.praisenter.ui;

import java.util.function.Supplier;

import org.praisenter.ui.translations.Translations;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
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
			this.createNode(Action.NEW),
			this.createNode(Action.SAVE),
			this.createNode(Action.SAVE_ALL),
			this.createNode(Action.UNDO),
			this.createNode(Action.REDO),
			this.createNode(Action.COPY),
			this.createNode(Action.CUT),
			this.createNode(Action.PASTE),
			this.createNode(Action.DELETE),
			this.createNode(Action.SELECT_ALL),
			this.createNode(Action.SELECT_INVERT),
			this.createNode(Action.SELECT_NONE),
			this.createNode(Action.RENAME),
			this.createNode(Action.RENUMBER),
			this.createNode(Action.REORDER),
			this.createNode(Action.SLIDE_COMPONENT_MOVE_UP),
			this.createNode(Action.SLIDE_COMPONENT_MOVE_DOWN),
			this.createNode(Action.SLIDE_COMPONENT_MOVE_FRONT),
			this.createNode(Action.SLIDE_COMPONENT_MOVE_BACK),
			this.createNode(Action.IMPORT),
			this.createNode(Action.EXPORT)
		);
	}
	
	private Node createNode(Action action) {
		if (action == Action.DIVIDER) {
			return new Separator(Orientation.HORIZONTAL);
		}
		if (action.getActions().length == 0) {
			return this.createButton(action);
		} else {
			return this.createMenuButton(action);
		}
	}
	
	private Button createButton(Action action) {
		Button button = new Button();
		this.setButtonProperties(button, action);
		button.setOnAction(e -> this.executeAction(action));
		return button;
	}

	private MenuButton createMenuButton(Action action) {
		MenuItem[] items = this.createMenuItemTree(action);
		MenuButton button = new MenuButton("", null, items);
		this.setButtonProperties(button, action);
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
			//button.setText(text);
			button.setTooltip(new Tooltip(text));
		}
		
		button.setMaxWidth(Double.MAX_VALUE);
	}
	
	private MenuItem[] createMenuItemTree(Action action) {
		MenuItem[] items = new MenuItem[action.getActions().length];
		int i = 0;
		for (Action sub : action.getActions()) {
			items[i++] = this.createMenuItem(sub);
		}
		return items;
	}
	
	private MenuItem createMenuItem(Action action) {
		if (action == Action.DIVIDER) {
			return new SeparatorMenuItem();
		}
		int n = action.getActions().length;
		if (n > 0) {
			MenuItem[] items = this.createMenuItemTree(action);
			Menu menu = new Menu("", null, items);
			this.setMenuItemProperties(menu, action);
			return menu;
		} else {
			MenuItem item = new MenuItem();
			item.setOnAction(e -> this.executeAction(action));
			this.setMenuItemProperties(item, action);
			return item;
		}
	}
	
	private void setMenuItemProperties(MenuItem item, Action action) {
		item.setUserData(action);
		item.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		item.visibleProperty().bind(this.context.getActionVisibleProperty(action));
		
		Supplier<Node> graphicSupplier = action.getGraphicSupplier();
		if (graphicSupplier != null) {
			Node graphic = graphicSupplier.get();
			item.setGraphic(graphic);
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
			item.setText(text);
//			item.setTooltip(new Tooltip(text));
		}
	}
	
	private void executeAction(Action action) {
		this.context.executeAction(action);
	}
}
