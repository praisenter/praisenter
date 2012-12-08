package org.praisenter.slide.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JDialog;

import org.praisenter.resources.Messages;

/**
 * Simple dialog to display the Slide Library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideLibraryDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = -7657879561093117638L;
	
	/** The slide library panel */
	private SlideLibraryPanel pnlSlideLibrary;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the this dialog; can be null
	 */
	protected SlideLibraryDialog(Window owner) {
		super(owner, Messages.getString("dialog.media.title"), ModalityType.APPLICATION_MODAL);
		
		this.pnlSlideLibrary = new SlideLibraryPanel();
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlSlideLibrary, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Shows a new Slide Library dialog.
	 * @param owner the owner of this dialog; can be null
	 */
	public static final void show(Window owner) {
		SlideLibraryDialog dialog = new SlideLibraryDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
	}
}

