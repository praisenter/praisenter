package org.praisenter.ui;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.praisenter.resources.Messages;

/**
 * Custom file chooser that shows a dialog when the user selects
 * an existing file to save to that verifies the user's decision
 * to overwrite a file.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class ValidateFileChooser extends JFileChooser {
	/** The version id */
	private static final long serialVersionUID = -4513964547607086698L;

	/**
	 * Default constructor.
	 */
	public ValidateFileChooser() {
		super();
	}

	/**
	 * Optional constructor
	 * @param currentDirectory the current directory
	 * @param fileSystemView the file system view
	 */
	public ValidateFileChooser(File currentDirectory, FileSystemView fileSystemView) {
		super(currentDirectory, fileSystemView);
	}

	/**
	 * Optional constructor
	 * @param currentDirectory the current directory
	 */
	public ValidateFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	/**
	 * Optional constructor
	 * @param fileSystemView the file system view
	 */
	public ValidateFileChooser(FileSystemView fileSystemView) {
		super(fileSystemView);
	}

	/**
	 * Optional constructor
	 * @param currentDirectoryPath the current directory
	 * @param fileSystemView the file system view
	 */
	public ValidateFileChooser(String currentDirectoryPath, FileSystemView fileSystemView) {
		super(currentDirectoryPath, fileSystemView);
	}

	/**
	 * Optional constructor
	 * @param currentDirectoryPath the current directory
	 */
	public ValidateFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

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
