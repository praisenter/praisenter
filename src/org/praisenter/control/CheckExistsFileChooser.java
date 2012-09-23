package org.praisenter.control;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.praisenter.resources.Messages;

/**
 * Custom file chooser that shows a dialog when the user selects
 * an existing file to save to that verifies the user's decision
 * to overwrite a file.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CheckExistsFileChooser extends JFileChooser {
	/** The version id */
	private static final long serialVersionUID = -4513964547607086698L;

	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#approveSelection()
	 */
	@Override
	public void approveSelection() {
		// check the dialog type
		if (getDialogType() == SAVE_DIALOG) {
			// see if the selected file exists
			File selectedFile = getSelectedFile();
			if ((selectedFile != null) && selectedFile.exists()) {
				// see if the user is ok with it
				int response = JOptionPane.showConfirmDialog(
								this,
								MessageFormat.format(Messages.getString("save.replace.text"), selectedFile.getName()),
								Messages.getString("save.replace.title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}
		}

		super.approveSelection();
	}
}
