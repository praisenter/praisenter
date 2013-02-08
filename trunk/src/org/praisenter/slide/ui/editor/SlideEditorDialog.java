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
package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.resources.Messages;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.Template;
import org.praisenter.threading.AbstractTask;
import org.praisenter.threading.TaskProgressDialog;
import org.praisenter.ui.BottomButtonPanel;
import org.praisenter.ui.RestrictedFileSystemView;
import org.praisenter.ui.ValidateFileChooser;

/**
 * Dialog used to edit a slide or template.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideEditorDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4373680679963698992L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideEditorDialog.class);
	
	// data
	
	/** The slide file */
	private SlideFile file;
	
	/** The slide */
	private Slide slide;
	
	// output
	
	/** The result of the editor */
	private SlideEditorResult result;
	
	// controls
	
	/** The slide editor panel */
	private SlideEditorPanel pnlSlideEditor;
	
	/**
	 * Default constructor.
	 * @param owner the owner of the dialog
	 * @param slide the slide to edit
	 * @param file the file properties of the slide to edit; null if the slide is new
	 */
	private SlideEditorDialog(Window owner, Slide slide, SlideFile file) {
		super(owner, Messages.getString("panel.slide.editor"), ModalityType.APPLICATION_MODAL);
		
		this.file = file;
		this.slide = slide;
		this.result = new SlideEditorResult();
		this.result.choice = SlideEditorOption.CANCEL;
		
		this.pnlSlideEditor = new SlideEditorPanel(this.slide);
		
		JButton btnSave = new JButton(Messages.getString("panel.slide.editor.save"));
		btnSave.addActionListener(this);
		btnSave.setActionCommand("save");
		
		JButton btnSaveAndClose = new JButton(Messages.getString("panel.slide.editor.saveAndClose"));
		btnSaveAndClose.addActionListener(this);
		btnSaveAndClose.setActionCommand("save-and-close");
		
		JButton btnSaveAs = new JButton(Messages.getString("panel.slide.editor.saveAs"));
		btnSaveAs.addActionListener(this);
		btnSaveAs.setActionCommand("saveas");
		
		JButton btnSaveAsAndClose = new JButton(Messages.getString("panel.slide.editor.saveAsAndClose"));
		btnSaveAsAndClose.addActionListener(this);
		btnSaveAsAndClose.setActionCommand("saveas-and-close");
		
		JButton btnCancel = new JButton(Messages.getString("panel.slide.editor.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("cancel");
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(btnSave);
		pnlButtons.add(btnSaveAndClose);
		pnlButtons.add(btnSaveAs);
		pnlButtons.add(btnSaveAsAndClose);
		pnlButtons.add(btnCancel);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		container.add(this.pnlSlideEditor, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("save".equals(command)) {
			if (this.file != null) {
				this.save();
			} else {
				this.saveAs();
			}
		} else if ("saveas".equals(command)) {
			this.saveAs();
		} else if ("save-and-close".equals(command)) {
			boolean saved = false;
			if (this.file != null) {
				saved = this.save();
			} else {
				saved = this.saveAs();
			}
			if (saved) {
				this.setVisible(false);
			}
		} else if ("saveas-and-close".equals(command)) {
			boolean saved = this.saveAs();
			if (saved) {
				this.setVisible(false);
			}
		} else if ("cancel".equals(command)) {
			this.setVisible(false);
		}
	}
	
	/**
	 * Performs a save operation on the given slide.
	 * <p>
	 * Returns true if the slide was saved successfully.
	 * @return boolean
	 */
	private boolean save() {
		final SlideFile file = this.file;
		final Slide slide = this.slide;
		final boolean isSlide = !(slide instanceof Template);
		
		// remove the slide/template in another thread
		AbstractTask task = new AbstractTask() {
			@Override
			public void run() {
				try {
					if (isSlide) {
						result.thumbnail = SlideLibrary.saveSlide(file, (BasicSlide)slide);
						this.setSuccessful(true);
					} else {
						result.thumbnail = SlideLibrary.saveTemplate(file, (Template)slide);
						this.setSuccessful(true);
					}
				} catch (SlideLibraryException ex) {
					this.setSuccessful(false);
					this.handleException(ex);
				}
			}
		};
		
		TaskProgressDialog.show(this, Messages.getString("panel.slide.saving"), task);
		if (task.isSuccessful()) {
			this.result.choice = SlideEditorOption.SAVE;
			this.result.slide = slide;
			return true;
		} else {
			this.result.choice = SlideEditorOption.CANCEL;
			this.result.slide = null;
			this.result.thumbnail = null;
			
			String type = Messages.getString("panel.slide");
			if (!isSlide) {
				type = Messages.getString("panel.template");
			}
			
			ExceptionDialog.show(
					this, 
					MessageFormat.format(Messages.getString("panel.slide.save.exception.title"), type), 
					MessageFormat.format(Messages.getString("panel.slide.save.exception.text"), type.toLowerCase(), file.getRelativePath()), 
					task.getException());
			LOGGER.error("An error occurred while attempting to save [" + file.getRelativePath() + "]: ", task.getException());
		}
		return false;
	}
	
	/**
	 * Performs a save as operation.
	 * <p>
	 * This involves the user selecting a file or typing in a new file name in a file
	 * chooser dialog.
	 * <p>
	 * Returns true if the slide was saved successfully.
	 * @return boolean
	 */
	private boolean saveAs() {
		final Slide slide = this.slide;
		final boolean isSlide = !(slide instanceof Template);
		
		// show the user a file name dialog (pre-populate with slide name)
		String path = SlideLibrary.getPath(slide.getClass());
		File root = new File(path);
		
		JFileChooser chooser = new ValidateFileChooser(root, new RestrictedFileSystemView(root));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setSelectedFile(new File(slide.getName()));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new SlideFileFilter());
		chooser.setCurrentDirectory(root);
		
		int choice = chooser.showSaveDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			final String fileName = file.getName().trim();
			
			// remove the slide/template in another thread
			AbstractTask task = new AbstractTask() {
				@Override
				public void run() {
					try {
						if (isSlide) {
							result.thumbnail = SlideLibrary.addSlide(fileName, (BasicSlide)slide);
							this.setSuccessful(true);
						} else {
							result.thumbnail = SlideLibrary.addTemplate(fileName, (Template)slide);
							this.setSuccessful(true);
						}
					} catch (SlideLibraryException ex) {
						this.setSuccessful(false);
						this.handleException(ex);
					}
				}
			};
			
			TaskProgressDialog.show(this, Messages.getString("panel.slide.saving"), task);
			if (task.isSuccessful()) {
				// if they just overwrote the same file
				// return that they just did a save
				if (this.file != null && this.file.getName().equals(fileName)) {
					this.result.choice = SlideEditorOption.SAVE;
				} else {
					this.result.choice = SlideEditorOption.SAVE_AS;
				}
				this.result.slide = slide;
				return true;
			} else {
				this.result.choice = SlideEditorOption.CANCEL;
				this.result.slide = null;
				this.result.thumbnail = null;
				
				String type = Messages.getString("panel.slide");
				if (!isSlide) {
					type = Messages.getString("panel.template");
				}
				
				ExceptionDialog.show(
						this, 
						MessageFormat.format(Messages.getString("panel.slide.save.exception.title"), type), 
						MessageFormat.format(Messages.getString("panel.slide.save.exception.text"), type.toLowerCase(), fileName), 
						task.getException());
				LOGGER.error("An error occurred while attempting to save [" + fileName + "]: ", task.getException());
			}
		}
		return false;
	}
	
	/**
	 * Shows a {@link SlideEditorDialog} and returns the action made by the user.
	 * @param owner the owner of the dialog
	 * @param slide the slide to edit
	 * @param file the file properties of the slide to edit; null if the slide is new
	 * @return {@link SlideEditorResult}
	 */
	public static final SlideEditorResult show(Window owner, Slide slide, SlideFile file) {
		SlideEditorDialog dialog = new SlideEditorDialog(owner, slide, file);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		
		return dialog.result;
	}
}
