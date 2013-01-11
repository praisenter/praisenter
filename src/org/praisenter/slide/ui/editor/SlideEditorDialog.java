package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Container;
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
import org.praisenter.utilities.WindowUtilities;

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
	
	/** The editor option chosen by the user */
	private SlideEditorOption choice;
	
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
		this.choice = SlideEditorOption.CANCEL;
		
		this.pnlSlideEditor = new SlideEditorPanel(this.slide);
		
		JButton btnSave = new JButton(Messages.getString("panel.slide.editor.save"));
		btnSave.addActionListener(this);
		btnSave.setActionCommand("save");
		
		JButton btnSaveAs = new JButton(Messages.getString("panel.slide.editor.saveAs"));
		btnSaveAs.addActionListener(this);
		btnSaveAs.setActionCommand("saveas");
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.add(btnSave);
		pnlButtons.add(btnSaveAs);
		
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
		}
	}
	
	/**
	 * Performs a save operation on the given slide.
	 */
	private void save() {
		final SlideFile file = this.file;
		final Slide slide = this.slide;
		final boolean isSlide = !(slide instanceof Template);
		
		// remove the slide/template in another thread
		AbstractTask task = new AbstractTask() {
			@Override
			public void run() {
				try {
					if (isSlide) {
						SlideLibrary.saveSlide(file, (BasicSlide)slide);
						this.setSuccessful(true);
					} else {
						SlideLibrary.saveTemplate(file, (Template)slide);
						this.setSuccessful(true);
					}
				} catch (SlideLibraryException ex) {
					this.setSuccessful(false);
					this.handleException(ex);
				}
			}
		};
		
		TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.saving"), task);
		if (task.isSuccessful()) {
			this.choice = SlideEditorOption.SAVE;
		} else {
			String type = Messages.getString("panel.slide");
			if (!isSlide) {
				type = Messages.getString("panel.template");
			}
			ExceptionDialog.show(
					this, 
					MessageFormat.format(Messages.getString("panel.slide.save.exception.title"), type), 
					MessageFormat.format(Messages.getString("panel.slide.save.exception.text"), type.toLowerCase(), file.getPath()), 
					task.getException());
			LOGGER.error("An error occurred while attempting to save [" + file.getPath() + "]: ", task.getException());
		}
	}
	
	/**
	 * Performs a save as operation.
	 * <p>
	 * This involves the user selecting a file or typing in a new file name in a file
	 * chooser dialog.
	 */
	private void saveAs() {
		final Slide slide = this.slide;
		final boolean isSlide = !(slide instanceof Template);
		
		// show the user a file name dialog (pre-populate with slide name)
		String path = SlideLibrary.getPath(slide.getClass());
		File root = new File(path);
		
		JFileChooser chooser = new JFileChooser(root, new RestrictedFileSystemView(root));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setSelectedFile(new File(slide.getName()));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new SlideFileFilter());
		chooser.setCurrentDirectory(root);
		
		int choice = chooser.showSaveDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			final String fileName = file.getName();
			
			// remove the slide/template in another thread
			AbstractTask task = new AbstractTask() {
				@Override
				public void run() {
					try {
						if (isSlide) {
							SlideLibrary.saveSlide(fileName, (BasicSlide)slide);
							this.setSuccessful(true);
						} else {
							SlideLibrary.saveTemplate(fileName, (Template)slide);
							this.setSuccessful(true);
						}
					} catch (SlideLibraryException ex) {
						this.setSuccessful(false);
						this.handleException(ex);
					}
				}
			};
			
			TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.saving"), task);
			if (task.isSuccessful()) {
				// if they just overwrote the same file
				// return that they just did a save
				if (this.file != null && this.file.getName().equals(fileName)) {
					this.choice = SlideEditorOption.SAVE;
				} else {
					this.choice = SlideEditorOption.SAVE_AS;
				}
			} else {
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
	}
	
	/**
	 * Shows a {@link SlideEditorDialog} and returns the action made by the user.
	 * @param owner the owner of the dialog
	 * @param slide the slide to edit
	 * @param file the file properties of the slide to edit; null if the slide is new
	 * @return {@link SlideEditorOption}
	 */
	public static final SlideEditorOption show(Window owner, Slide slide, SlideFile file) {
		SlideEditorDialog dialog = new SlideEditorDialog(owner, slide, file);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		dialog.dispose();
		
		return dialog.choice;
	}
}
