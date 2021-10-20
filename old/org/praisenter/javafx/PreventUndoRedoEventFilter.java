package org.praisenter.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;


/**
 * A special event filter that intercepts the Undo and Redo events for a control to allow for custom
 * handling of those events.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PreventUndoRedoEventFilter implements EventHandler<KeyEvent> {
	private final Node node;
	
	/**
	 * Constructor.
	 * @param node the node to filter events on
	 */
	public PreventUndoRedoEventFilter(Node node) {
		this.node = node;
	}
	
	@Override
	public void handle(KeyEvent event) {
		if (ApplicationAction.UNDO.getAccelerator().match(event)) {
			if (node.getParent() != null) {
				node.getParent().fireEvent(event.copyFor(node.getParent(), node.getParent()));
			}
			event.consume();
		} else if (ApplicationAction.REDO.getAccelerator().match(event)) {
			if (node.getParent() != null) {
				node.getParent().fireEvent(event.copyFor(node.getParent(), node.getParent()));
			}
			event.consume();
		}
	}
}
