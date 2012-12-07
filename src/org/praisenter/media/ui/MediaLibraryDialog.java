package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JDialog;

import org.praisenter.resources.Messages;

/**
 * Simple dialog to display the Media Library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class MediaLibraryDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = -6376427691814126528L;
	
	/** The media library panel */
	private MediaLibraryPanel pnlMediaLibrary;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the this dialog; can be null
	 */
	public MediaLibraryDialog(Window owner) {
		super(owner, Messages.getString("dialog.media.title"), ModalityType.APPLICATION_MODAL);
		
		this.pnlMediaLibrary = new MediaLibraryPanel();
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlMediaLibrary, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Shows a new Media Library dialog.
	 * @param owner the owner of this dialog; can be null
	 */
	public static final void show(Window owner) {
		MediaLibraryDialog dialog = new MediaLibraryDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
}
