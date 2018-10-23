package org.praisenter.ui;

import java.util.function.Supplier;

import org.praisenter.ui.events.ActionPromptPaneCompleteEvent;
import org.praisenter.ui.translations.Translations;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public final class ActionBar extends HBox {
	private final GlobalContext context;
	
	private final ToolBar toolbar;
	private final StackPane subToolbar;
	
	public ActionBar(GlobalContext context) {
		this.getStyleClass().add("p-action-bar");
		
		this.context = context;
		
		ToolBar toolbar = new ToolBar();
		toolbar.getStyleClass().add("p-action-bar");
		toolbar.setOrientation(Orientation.VERTICAL);
		toolbar.getItems().addAll(
			this.createButton(Action.SAVE),
//			this.createButton(Action.SAVE_AS),
			this.createButton(Action.SAVE_ALL),
			this.createButton(Action.UNDO),
			this.createButton(Action.REDO),
			this.createButton(Action.NEW),
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
		
		StackPane subToolBar = new StackPane();
		
		this.getChildren().addAll(toolbar, subToolBar);
		
		this.toolbar = toolbar;
		this.subToolbar = subToolBar;
		
		this.context.focusOwnerProperty().addListener((obs, ov, nv) -> {
			if (nv == null) {
				this.subToolbar.getChildren().clear();
			} else if (!this.isPromptPaneFocused(nv)) {
				this.subToolbar.getChildren().clear();
			}
		});
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
			// TODO move these to the ApplicationState class and some how check to see if they need to be re-set
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
	
	private boolean isPromptPaneFocused(Node focused) {
		int n = this.subToolbar.getChildren().size();
		if (n == 0) return false;
		
		Node node = focused;
		Node prompt = this.subToolbar.getChildren().get(0);
		while (node != null) {
			if (node == prompt) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}
	
	private void executeAction(Action action) {
		this.context.executeAction(action).thenAccept(node -> {
			if (node != null) {
				// TODO how can we know when to close this? the problem is that if you select something, pop this, but then select something else then continue...
				// if we're given a node back, then we need to present this node in the button sub pane
				node.addEventHandler(ActionPromptPaneCompleteEvent.ALL, (e) -> {
					// in either case we need to remove this
					// node from the subtoolbar children
					this.subToolbar.getChildren().remove(node);
				});
				this.subToolbar.getChildren().clear();
				this.subToolbar.getChildren().add(node);
				
				// set the focus, the idea being that if the focus goes away from this
				// node, then we immediately close it assuming the user has been distracted
				node.requestFocus();
			}
			// if it's null, then there's nothing to do
		});
	}
}
