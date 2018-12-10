package org.praisenter.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;

//JAVABUG (H) 07/20/17 [workaround] TextField/TextArea have their own undo/redo management that gets in the way https://bugs.openjdk.java.net/browse/JDK-8091301

public final class TextInputFieldEventFilter implements EventHandler<KeyEvent> {
	private final Node node;
	
	public static void applyTextInputFieldEventFilter(Node node) {
		new TextInputFieldEventFilter(node);
	}
	
	public static void applyTextInputFieldEventFilter(Node... nodes) {
		for (Node node : nodes) {
			new TextInputFieldEventFilter(node);
		}
	}
	
	/**
	 * Constructor.
	 * @param node the node to filter events on
	 */
	private TextInputFieldEventFilter(Node node) {
		this.node = node;
		// re-route the key patterns for undo/redo
		node.addEventFilter(KeyEvent.ANY, this);
		// prevent the popup of the OOB context menu
		node.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
	}
	
	@Override
	public void handle(KeyEvent event) {
		if (Action.UNDO.getAccelerator().match(event) ||
			Action.REDO.getAccelerator().match(event) ||
			Action.COPY.getAccelerator().match(event) ||
			Action.CUT.getAccelerator().match(event) ||
			Action.PASTE.getAccelerator().match(event)) {
			
			Node next = this.node.getParent();
			
			// execute the action on the closest action pane
			boolean found = false;
			while (next != null) {
				if (next instanceof ActionPane) {
					found = true;
					next.fireEvent(event.copyFor(node.getParent(), node.getParent()));
				}
				next = next.getParent();
			}
			
			// otherwise execute on the direct parent (doesn't work for spinner and probably other composite controls)
			if (!found) {
				next = this.node.getParent();
				if (next != null) {
					next.fireEvent(event.copyFor(node.getParent(), node.getParent()));
				}
			}
			
			event.consume();
		}
	}
}
