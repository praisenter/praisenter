package org.praisenter.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/**
 * A special event filter that intercepts the Undo and Redo events for a control to allow for custom
 * handling of the events.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PreventUndoRedoEventFilter implements EventHandler<KeyEvent> {
	private final Node node;
	
	public PreventUndoRedoEventFilter(Node node) {
		this.node = node;
	}
	
	@Override
	public void handle(KeyEvent event) {
		if (ApplicationAction.UNDO.getAccelerator().match(event)) {
			event.consume();
			node.fireEvent(new ApplicationEvent(this, node, ApplicationEvent.ALL, ApplicationAction.UNDO));
		} else if (ApplicationAction.REDO.getAccelerator().match(event)) {
			event.consume();
			node.fireEvent(new ApplicationEvent(this, node, ApplicationEvent.ALL, ApplicationAction.REDO));
		}
	}
}
