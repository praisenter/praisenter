package org.praisenter.slide.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JDialog;

import org.praisenter.resources.Messages;
import org.praisenter.slide.Slide;

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
	 * @param owner the owner of the this dialog
	 * @param clazz the initial class type to have focused; can be null
	 */
	protected SlideLibraryDialog(Window owner, Class<? extends Slide> clazz) {
		super(owner, Messages.getString("dialog.media.title"), ModalityType.APPLICATION_MODAL);
		
		this.pnlSlideLibrary = new SlideLibraryPanel(clazz);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlSlideLibrary, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Shows a new Slide Library dialog.
	 * <p>
	 * Returns true if the slide library was updated.
	 * @param owner the owner of this dialog; can be null
	 * @param clazz the initial class type to have focused; can be null
	 * @return boolean
	 */
	public static final boolean show(Window owner, Class<? extends Slide> clazz) {
		SlideLibraryDialog dialog = new SlideLibraryDialog(owner, clazz);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		
		return dialog.pnlSlideLibrary.isSlideLibraryUpdated();
	}
}

