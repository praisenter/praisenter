/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.application.ui;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.praisenter.application.resources.Messages;

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
