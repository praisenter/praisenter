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
package org.praisenter.slide.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.preferences.ui.PreferencesListener;
import org.praisenter.resources.Messages;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.Template;
import org.praisenter.slide.ui.editor.SlideEditorDialog;
import org.praisenter.slide.ui.editor.SlideEditorOption;
import org.praisenter.slide.ui.editor.SlideEditorResult;
import org.praisenter.slide.ui.preview.SingleSlidePreviewPanel;
import org.praisenter.threading.AbstractTask;
import org.praisenter.threading.TaskProgressDialog;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel for custom slides.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlidePanel extends JPanel implements ListSelectionListener, ActionListener, SlideLibraryListener, PreferencesListener {
	/** The version id */
	private static final long serialVersionUID = 5740087054031715193L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlidePanel.class);
	
	// controls
	
	/** The list of saved slides */
	private JList<SlideThumbnail> lstSlides;
	
	/** The edit slide/template button */
	private JButton btnEditSlide;
	
	/** The copy slide/template button */
	private JButton btnCopySlide;
	
	/** The remove slide template button */
	private JButton btnRemoveSlide;
	
	/** The slide/template properties panel */
	private SlidePropertiesPanel pnlProperties;
	
	/** The slide/template preview panel */
	private SingleSlidePreviewPanel pnlPreview;
	
	// state
	
	/** The slide/template preview thread */
	private SlidePreivewThread previewThread;
	
	/**
	 * Default constructor.
	 */
	public SlidePanel() {
		this.pnlProperties = new SlidePropertiesPanel();
		this.pnlProperties.setMinimumSize(new Dimension(300, 0));
		
		this.pnlPreview = new SingleSlidePreviewPanel();
		Dimension size = new Dimension(300, 300);
		this.pnlPreview.setMinimumSize(size);
		this.pnlPreview.setPreferredSize(size);
		
		this.previewThread = new SlidePreivewThread();
		this.previewThread.start();
		
		List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
		this.lstSlides = createJList(thumbnails);
		JScrollPane scrSlides = new JScrollPane(this.lstSlides);
		
		JButton btnCreateSlide = new JButton(Messages.getString("panel.slide.create"));
		btnCreateSlide.setActionCommand("new");
		btnCreateSlide.addActionListener(this);
		btnCreateSlide.setMinimumSize(new Dimension(0, 50));
		btnCreateSlide.setFont(btnCreateSlide.getFont().deriveFont(Font.BOLD, btnCreateSlide.getFont().getSize2D() + 2.0f));
		
		this.btnEditSlide = new JButton(Messages.getString("panel.slide.edit"));
		this.btnEditSlide.setActionCommand("edit");
		this.btnEditSlide.addActionListener(this);
		this.btnEditSlide.setEnabled(false);
		
		this.btnCopySlide = new JButton(Messages.getString("panel.slide.copy"));
		this.btnCopySlide.setActionCommand("copy");
		this.btnCopySlide.addActionListener(this);
		this.btnCopySlide.setEnabled(false);
		
		this.btnRemoveSlide = new JButton(Messages.getString("panel.slide.remove"));
		this.btnRemoveSlide.setActionCommand("remove");
		this.btnRemoveSlide.addActionListener(this);
		this.btnRemoveSlide.setEnabled(false);
		
		JPanel pnlRight = new JPanel();
		GroupLayout layout = new GroupLayout(pnlRight);
		pnlRight.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(btnCreateSlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlProperties)
				.addComponent(this.btnEditSlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.btnCopySlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.btnRemoveSlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(btnCreateSlide)
				.addComponent(this.pnlProperties, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnEditSlide)
				.addComponent(this.btnCopySlide)
				.addComponent(this.btnRemoveSlide)
				.addComponent(this.pnlPreview));
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrSlides, pnlRight);
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the thread used to update the slide/template previews.
	 * @return {@link SlidePreivewThread}
	 */
	private final SlidePreivewThread getPreviewThread() {
		if (this.previewThread == null || !this.previewThread.isAlive()) {
			this.previewThread = new SlidePreivewThread();
			this.previewThread.start();
		}
		return this.previewThread;
	}
	
	/**
	 * Creates a new JList for the given list of {@link SlideThumbnail}s.
	 * @param thumbnails the list of thumbnails
	 * @return JList
	 */
	private final JList<SlideThumbnail> createJList(List<SlideThumbnail> thumbnails) {
		JList<SlideThumbnail> list = new JList<SlideThumbnail>();
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(100);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new SlideThumbnailListCellRenderer());
		list.setLayout(new BorderLayout());
		list.addListSelectionListener(this);
		// setup the items
		DefaultListModel<SlideThumbnail> model = new DefaultListModel<SlideThumbnail>();
		for (SlideThumbnail thumbnail : thumbnails) {
			model.addElement(thumbnail);
		}
		list.setModel(model);
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		// we dont need to get the selected thumnbnail for the new command
		if ("new".equals(command)) {
			// show dialog asking what type (slide/template)
			Slide slide = NewSlideDialog.show(WindowUtilities.getParentWindow(this), BasicSlide.class);
			// check for null (null means the user canceled)
			if (slide != null) {
				// open the slide/template editor
				SlideEditorResult result = SlideEditorDialog.show(WindowUtilities.getParentWindow(this), slide, null);
				// check the return type
				if (result.getChoice() != SlideEditorOption.CANCEL) {
					// when control returns here we need to update the items in the jlist with the current media library items
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(slide.getClass());
					// get the list that should store this slide
					JList<SlideThumbnail> list = this.lstSlides;
					list.clearSelection();
					// we need to reload all the thumbnails here since the user could do multiple save as...'es saving
					// multiple slides or templates
					DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
					model.removeAllElements();
					for (SlideThumbnail thumb : thumbnails) {
						model.addElement(thumb);
					}
					list.setSelectedValue(result.getThumbnail(), true);
				}
			}
			return;
		}
		
		// get the currently selected thumbnail
		JList<SlideThumbnail> list = this.lstSlides;
		final SlideThumbnail thumbnail = list.getSelectedValue();
		
		String type = Messages.getString("panel.slide");
		
		// make sure the currently selected item is not null
		if (thumbnail != null) {
			// check the action type
			if ("edit".equals(command)) {
				// get the selected slide or template
				SlideFile file = thumbnail.getFile();
				
				// load the slide on another thread
				LoadSlideTask task = new LoadSlideTask(file);
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.loading"), task);
				
				if (task.isSuccessful()) {
					// make a copy of the slide so that the user can cancel the operation
					Slide slide = task.getSlide().copy();
					SlideEditorResult result = SlideEditorDialog.show(WindowUtilities.getParentWindow(this), slide, file);
					
					if (result.getChoice() == SlideEditorOption.SAVE) {
						// we only need to update the one thumbnail
						DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
						int i = model.indexOf(thumbnail);
						model.set(i, result.getThumbnail());
						// we need to update the preview of the selected slide
						this.pnlPreview.setSlide(result.getSlide());
						this.pnlPreview.repaint();
					} else if (result.getChoice() == SlideEditorOption.SAVE_AS) {
						// when control returns here we need to update the items in the jlist with the current media library items
						List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
						list.clearSelection();
						// we need to reload all the thumbnails here since the user could do multiple save as...'es saving
						// multiple slides or templates
						DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
						model.removeAllElements();
						for (SlideThumbnail thumb : thumbnails) {
							model.addElement(thumb);
						}
						list.setSelectedValue(result.getThumbnail(), true);
					}
				} else {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.load.exception.title"), 
							MessageFormat.format(Messages.getString("panel.slide.load.exception.text"), file.getPath()), 
							task.getException());
					LOGGER.error("Failed to load [" + file.getPath() + "] from the slide library: ", task.getException());
				}
			} else if ("copy".equals(command)) {
				// get the selected slide or template
				SlideFile file = thumbnail.getFile();
				
				// load/copy/save on another thread
				LoadSlideTask task = new LoadSlideTask(file) {
					@Override
					public void run() {
						super.run();
						// if the loading of the slide was successful then try to
						// copy it and save the copy
						if (this.isSuccessful() && this.slide != null) {
							// save the slide
							// make a copy of the slide so that the user can cancel the operation
							Slide slide = this.slide.copy();
							
							// rename the slide to copy of or something
							slide.setName(MessageFormat.format(Messages.getString("panel.slide.copy.copyOf"), slide.getName()));
							
							// save the slide
							try {
								SlideLibrary.saveSlide(slide.getName(), (BasicSlide)slide);
								this.setSuccessful(true);
							} catch (Exception e) {
								this.setSuccessful(false);
								this.handleException(e);
							}
						}
					}
				};
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.copying"), task);
				
				if (task.isSuccessful()) {
					// when control returns here we need to update the items in the jlist with the current media library items
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
					list.clearSelection();
					// we need to reload all the thumbnails here since the user could do multiple save as...'es saving
					// multiple slides or templates
					DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
					model.removeAllElements();
					for (SlideThumbnail thumb : thumbnails) {
						model.addElement(thumb);
					}
					list.setSelectedValue(thumbnail, true);
				} else {
					ExceptionDialog.show(
							this, 
							MessageFormat.format(Messages.getString("panel.slide.copy.exception.title"), type), 
							MessageFormat.format(Messages.getString("panel.slide.copy.exception.text"), type.toLowerCase(), file.getPath()), 
							task.getException());
					LOGGER.error("Failed to copy [" + file.getPath() + "]: ", task.getException());
				}
			} else if ("remove".equals(command)) {
				// show an are you sure dialog, then delete the slide
				int choice = JOptionPane.showConfirmDialog(
						this, 
						MessageFormat.format(Messages.getString("panel.slide.remove.message"), thumbnail.getName()),
						MessageFormat.format(Messages.getString("panel.slide.remove.title"), type),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					// remove the slide/template in another thread
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							SlideLibrary.deleteSlide(thumbnail.getFile().getPath());
							this.setSuccessful(true);
						}
					};
					
					TaskProgressDialog.show(WindowUtilities.getParentWindow(this), MessageFormat.format(Messages.getString("panel.slide.removing"), type), task);
					if (task.isSuccessful()) {
						if (list != null) {
							// remove the thumbnail from the list
							DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
							model.removeElement(thumbnail);
						}
					} else {
						ExceptionDialog.show(
								this, 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.title"), type), 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.text"), type.toLowerCase(), thumbnail.getFile().getName()), 
								task.getException());
						LOGGER.error("An error occurred while attempting to remove [" + thumbnail.getFile().getPath() + "] from the slide library: ", task.getException());
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstSlides) {
				SlideFile file = null;
				PreviewAction<?> preview = null;
				SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new SlidePreviewAction<BasicSlide>(file.getPath(), BasicSlide.class);
				}
				
				if (preview != null && file != null) {
					this.pnlPreview.setLoading(true);
					this.getPreviewThread().queueSlide(preview);
					this.pnlProperties.setSlideFile(file, true);
					this.btnEditSlide.setEnabled(true);
					this.btnCopySlide.setEnabled(true);
					this.btnRemoveSlide.setEnabled(true);
				} else {
					this.pnlPreview.setSlide(null);
					this.pnlProperties.setSlideFile(null, true);
					this.pnlPreview.repaint();
					this.btnEditSlide.setEnabled(false);
					this.btnCopySlide.setEnabled(false);
					this.btnRemoveSlide.setEnabled(false);
				}
			}
		}
	}
	
	@Override
	public void preferencesChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	@Override
	public void slideLibraryChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/**
	 * Called when the preferences or the slide library changes.
	 * <p>
	 * The preferences can alter the selected template and the slide library
	 * can be changed from the preferences dialog. Because of this, we need
	 * to perform the same action for both events.
	 */
	private void onPreferencesOrSlideLibraryChanged() {
		// if the preferences or slide library changes we only want to make
		// sure that we are using the latest template (so if it was edited
		// we need to update the preview) and that we are using the latest
		// listing of other templates
		
		List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
		
		// update the list of templates
		SlideThumbnail selected = this.lstSlides.getSelectedValue();
		DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)this.lstSlides.getModel();
		model.removeAllElements();
		for (SlideThumbnail thumb : thumbnails) {
			model.addElement(thumb);
		}
		
		// set the selected one
		// selecting the item in the combo box will update the template
		// and the preview panel
		if (selected != null) {
			this.lstSlides.setSelectedValue(selected, true);
		} else {
			this.lstSlides.setSelectedIndex(0);
		}
		
		// redraw the preview panel
		this.pnlPreview.repaint();
	}
	
	/**
	 * Custom thread for updating the preview of a slide or template.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	private class SlidePreivewThread extends Thread {
		/** The blocking queue */
		private final BlockingQueue<PreviewAction<?>> slideQueue;
		
		/**
		 * Default constructor.
		 */
		public SlidePreivewThread() {
			super("SlidePreivewThread");
			this.setDaemon(true);
			this.slideQueue = new ArrayBlockingQueue<PreviewAction<?>>(10);
		}

		/**
		 * Queues a new preview.
		 * @param preview the preview
		 */
		public void queueSlide(PreviewAction<?> preview) {
			this.slideQueue.add(preview);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// make the thread run always
			while (true) {
				try {
					// end when this panel is no longer displayable
					if (!isDisplayable()) {
						return;
					}
					// poll for any queued slides
					PreviewAction<?> preview = this.slideQueue.poll(1000, TimeUnit.MILLISECONDS);
					// check if its null
					if (preview != null) {
						// if it isnt then attempt to load the slide
						Slide slide = null;
						try {
							// get the slide
							if (Template.class.isAssignableFrom(preview.getSlideClass())) {
								TemplatePreviewAction<?> tPreview = (TemplatePreviewAction<?>)preview;
								slide = SlideLibrary.getTemplate(preview.path, tPreview.clazz);
							} else {
								slide = SlideLibrary.getSlide(preview.path);
							}
							// update the slides (this method should not
							// do anything that should normally be done on the EDT)
							pnlPreview.setSlide(slide);
						} catch (Exception e) {
							LOGGER.error("An error occurred while loading the slide/template [" + preview.path + "]: ", e);
						}
					}
					// update the preview panel
					try {
						// we need to block this thread until this code
						// runs since we will get problems if another song
						// is in the queue
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								pnlPreview.setLoading(false);
							}
						});
					} catch (InvocationTargetException e) {
						// in this case just continue
						LOGGER.warn("An error occurred while updating the slide preview on the EDT: ", e);
					}
				} catch (InterruptedException ex) {
					// if the song search thread is interrupted then just stop it
					LOGGER.info("SlideTemplatePreviewThread was interrupted. Stopping thread.", ex);
					break;
				}
			}
		}
	}
	
	/**
	 * Task to load a slide from the Slide Library.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	private class LoadSlideTask extends AbstractTask {
		/** The slide file */
		protected SlideFile file;
		
		/** The loaded slide */
		protected Slide slide;
		
		/**
		 * Full constructor.
		 * @param file the slide file
		 */
		public LoadSlideTask(SlideFile file) {
			this.file = file;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				this.slide = SlideLibrary.getSlide(this.file.getPath());
				this.setSuccessful(true);
			} catch (Exception ex) {
				this.handleException(ex);
			}
		}
		
		/**
		 * Returns the loaded slide.
		 * @return {@link Slide}
		 */
		public Slide getSlide() {
			return this.slide;
		}
	}
}
