package org.praisenter.data.song.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * Represents a custom action for the {@link SongQuickSendPanel}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongQuickSendAction extends AbstractAction {
	/** The version id */
	private static final long serialVersionUID = 7086837760498137114L;
	
	/** The key stroke the action is bound to */
	protected KeyStroke keyStroke;
	
	/** The action command (so we can use the same handling code) */
	protected String command;
	
	/** The listener of the action events */
	protected ActionListener listener;
	
	/**
	 * Full constructor.
	 * @param listener the listener for the action events
	 * @param keyStroke the bound key stroke
	 * @param command the command
	 */
	public SongQuickSendAction(ActionListener listener, KeyStroke keyStroke, String command) {
		this.keyStroke = keyStroke;
		this.command = command;
		this.listener = listener;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.listener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), this.command));
	}
}
