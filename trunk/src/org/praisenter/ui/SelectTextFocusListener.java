package org.praisenter.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Focus adapter used to select the text inside a JTextField when the
 * focus is given to it.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SelectTextFocusListener extends FocusAdapter {
	/** The text component */
	private JTextComponent component;
	
	/**
	 * Full constructor.
	 * @param component the text component
	 */
	public SelectTextFocusListener(JTextComponent component) {
		this.component = component;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.FocusAdapter#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		if (!e.isTemporary()) {
			// add a runnable to be executed later
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					component.selectAll();
				}
			});
		}
		super.focusGained(e);
	}
}
