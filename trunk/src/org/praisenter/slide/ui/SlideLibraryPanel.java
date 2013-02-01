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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.resources.Messages;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.BasicSlideTemplate;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideExport;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.SongSlideTemplate;
import org.praisenter.slide.Template;
import org.praisenter.slide.ui.editor.SlideEditorDialog;
import org.praisenter.slide.ui.editor.SlideEditorOption;
import org.praisenter.slide.ui.editor.SlideEditorResult;
import org.praisenter.slide.ui.preview.SingleSlidePreviewPanel;
import org.praisenter.threading.AbstractTask;
import org.praisenter.threading.TaskProgressDialog;
import org.praisenter.ui.ValidateFileChooser;
import org.praisenter.ui.ZipFileFilter;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to maintain the Slide Library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideLibraryPanel extends JPanel implements ListSelectionListener, ChangeListener, ItemListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -8314362249681392612L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideLibraryPanel.class);
	
	/** Key for the slide template card */
	private static final String SLIDE_CARD = "Slide";
	
	/** Key for the bible template card */
	private static final String BIBLE_CARD = "Bible";
	
	/** Key for the song template card */
	private static final String SONG_CARD = "Song";
	
	/** Key for the notification template card */
	private static final String NOTIFICATION_CARD = "Notification";
	
	// data
	
	/** True if the slide library was updated */
	private boolean slideLibraryUpdated;
	
	// controls
	
	/** The list of saved slides */
	private JList<SlideThumbnail> lstSlides;
	
	/** The template type combo box */
	private JComboBox<TemplateType> cmbTemplateType;
	
	/** The template type card panel */
	private JPanel pnlTemplateCards;
	
	/** The template type card layout */
	private CardLayout layTemplateCards;
	
	/** The list of saved templates */
	private JList<SlideThumbnail> lstTemplates;
	
	/** The list of saved bible templates */
	private JList<SlideThumbnail> lstBibleTemplates;
	
	/** The list of saved song templates */
	private JList<SlideThumbnail> lstSongTemplates;
	
	/** The list of saved notification templates */
	private JList<SlideThumbnail> lstNotificationTemplates;
	
	/** The edit slide/template button */
	private JButton btnEditSlide;
	
	/** The copy slide/template button */
	private JButton btnCopySlide;
	
	/** The create template button */
	private JButton btnCreateTemplate;
	
	/** The create slide button */
	private JButton btnCreateSlide;
	
	/** The remove slide template button */
	private JButton btnRemoveSlide;

	/** The export slide/template button */
	private JButton btnExportSlide;
	
	/** The slide/template tabs */
	private JTabbedPane slideTabs;
	
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
	public SlideLibraryPanel() {
		this(null);
	}
	
	/**
	 * Optional constructor.
	 * @param clazz the class type to have focused
	 */
	public SlideLibraryPanel(Class<? extends Slide> clazz) {
		this.slideLibraryUpdated = false;
		
		this.pnlProperties = new SlidePropertiesPanel();
		this.pnlProperties.setMinimumSize(new Dimension(300, 0));
		
		this.pnlPreview = new SingleSlidePreviewPanel();
		Dimension size = new Dimension(300, 300);
		this.pnlPreview.setMinimumSize(size);
		this.pnlPreview.setPreferredSize(size);
		
		this.previewThread = new SlidePreivewThread();
		this.previewThread.start();
		
		this.slideTabs = new JTabbedPane();
		this.slideTabs.setMinimumSize(new Dimension(120, 120));
		this.slideTabs.setPreferredSize(new Dimension(500, 500));
		
		// slides tab
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
			this.lstSlides = createJList(thumbnails);
			this.slideTabs.addTab(Messages.getString("panel.slide.slides"), new JScrollPane(this.lstSlides));
		}
		
		// templates tab
		{
			this.cmbTemplateType = new JComboBox<TemplateType>(TemplateType.values());
			this.cmbTemplateType.addItemListener(this);
			this.cmbTemplateType.setRenderer(new TemplateTypeListCellRenderer());
			
			JPanel pnlTemplateType = new JPanel();
			pnlTemplateType.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			pnlTemplateType.setLayout(new BorderLayout());
			pnlTemplateType.add(this.cmbTemplateType, BorderLayout.CENTER);
			
			this.pnlTemplateCards = new JPanel();
			this.layTemplateCards = new CardLayout();
			this.pnlTemplateCards.setLayout(this.layTemplateCards);
			
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlideTemplate.class);
			this.lstTemplates = createJList(thumbnails);
			
			thumbnails = SlideLibrary.getThumbnails(BibleSlideTemplate.class);
			this.lstBibleTemplates = createJList(thumbnails);
			
			thumbnails = SlideLibrary.getThumbnails(SongSlideTemplate.class);
			this.lstSongTemplates = createJList(thumbnails);
			
			thumbnails = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
			this.lstNotificationTemplates = createJList(thumbnails);
			
			this.pnlTemplateCards.add(new JScrollPane(this.lstTemplates), SLIDE_CARD);
			this.pnlTemplateCards.add(new JScrollPane(this.lstBibleTemplates), BIBLE_CARD);
			this.pnlTemplateCards.add(new JScrollPane(this.lstSongTemplates), SONG_CARD);
			this.pnlTemplateCards.add(new JScrollPane(this.lstNotificationTemplates), NOTIFICATION_CARD);
			
			this.layTemplateCards.show(this.pnlTemplateCards, SLIDE_CARD);
			
			JPanel pnlTemplates = new JPanel();
			pnlTemplates.setLayout(new BorderLayout());
			
			pnlTemplates.add(pnlTemplateType, BorderLayout.PAGE_START);
			pnlTemplates.add(this.pnlTemplateCards, BorderLayout.CENTER);
			
			this.slideTabs.addTab(Messages.getString("panel.slide.templates"), pnlTemplates);
		}
		
		JButton btnCreateSlide = new JButton(Messages.getString("panel.slide.create"));
		btnCreateSlide.setActionCommand("new");
		btnCreateSlide.addActionListener(this);
		btnCreateSlide.setMinimumSize(new Dimension(0, 50));
		btnCreateSlide.setFont(btnCreateSlide.getFont().deriveFont(Font.BOLD, btnCreateSlide.getFont().getSize2D() + 2.0f));
		
		JButton btnImport = new JButton(Messages.getString("panel.slide.import"));
		btnImport.setActionCommand("import");
		btnImport.setToolTipText(Messages.getString("panel.slide.import.tooltip"));
		btnImport.addActionListener(this);
		
		JButton btnExport = new JButton(Messages.getString("panel.slide.export"));
		btnExport.setActionCommand("export");
		btnExport.setToolTipText(Messages.getString("panel.slide.export.tooltip"));
		btnExport.addActionListener(this);
		
		this.btnEditSlide = new JButton(Messages.getString("panel.slide.edit"));
		this.btnEditSlide.setActionCommand("edit");
		this.btnEditSlide.addActionListener(this);
		this.btnEditSlide.setEnabled(false);
		
		this.btnCopySlide = new JButton(Messages.getString("panel.slide.copy"));
		this.btnCopySlide.setActionCommand("copy");
		this.btnCopySlide.addActionListener(this);
		this.btnCopySlide.setEnabled(false);
		
		this.btnCreateSlide = new JButton(Messages.getString("panel.slide.createSlide"));
		this.btnCreateSlide.setToolTipText(Messages.getString("panel.slide.createSlide.tooltip"));
		this.btnCreateSlide.setActionCommand("createSlide");
		this.btnCreateSlide.addActionListener(this);
		this.btnCreateSlide.setVisible(false);
		
		this.btnCreateTemplate = new JButton(Messages.getString("panel.slide.createTemplate"));
		this.btnCreateTemplate.setToolTipText(Messages.getString("panel.slide.createTemplate.tooltip"));
		this.btnCreateTemplate.setActionCommand("createTemplate");
		this.btnCreateTemplate.addActionListener(this);
		this.btnCreateTemplate.setVisible(false);
		
		this.btnRemoveSlide = new JButton(Messages.getString("panel.slide.remove"));
		this.btnRemoveSlide.setActionCommand("remove");
		this.btnRemoveSlide.addActionListener(this);
		this.btnRemoveSlide.setEnabled(false);
		
		this.btnExportSlide = new JButton(Messages.getString("panel.slide.export.slide"));
		this.btnExportSlide.setActionCommand("export-slide");
		this.btnExportSlide.addActionListener(this);
		this.btnExportSlide.setEnabled(false);
		
		// select the initial view
		if (clazz != null) {
			if (Template.class.isAssignableFrom(clazz)) {
				this.slideTabs.setSelectedIndex(1);
				if (BasicSlideTemplate.class.isAssignableFrom(clazz)) {
					this.cmbTemplateType.setSelectedItem(TemplateType.SLIDE);
				} else if (BibleSlideTemplate.class.isAssignableFrom(clazz)) {
					this.cmbTemplateType.setSelectedItem(TemplateType.BIBLE);
				} else if (SongSlideTemplate.class.isAssignableFrom(clazz)) {
					this.cmbTemplateType.setSelectedItem(TemplateType.SONG);
				} else if (NotificationSlideTemplate.class.isAssignableFrom(clazz)) {
					this.cmbTemplateType.setSelectedItem(TemplateType.NOTIFICATION);
				}
			}
		}
		
		JPanel pnlLibraryButtons = new JPanel();
		pnlLibraryButtons.setLayout(new GridLayout(1, 2));
		pnlLibraryButtons.add(btnImport);
		pnlLibraryButtons.add(btnExport);
		
		JPanel pnlSlideButtons = new JPanel();
		pnlSlideButtons.setLayout(new GridLayout(2, 2));
		pnlSlideButtons.add(this.btnEditSlide);
		pnlSlideButtons.add(this.btnCopySlide);
		pnlSlideButtons.add(this.btnExportSlide);
		pnlSlideButtons.add(this.btnRemoveSlide);
		
		JPanel pnlRight = new JPanel();
		GroupLayout layout = new GroupLayout(pnlRight);
		pnlRight.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(btnCreateSlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlLibraryButtons)
				.addComponent(this.pnlProperties)
				.addComponent(pnlSlideButtons)
				.addComponent(this.btnCreateSlide, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.btnCreateTemplate, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(btnCreateSlide)
				.addComponent(pnlLibraryButtons)
				.addComponent(this.pnlProperties, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlSlideButtons)
				.addComponent(this.btnCreateSlide)
				.addComponent(this.btnCreateTemplate)
				.addComponent(this.pnlPreview));
		
		this.slideTabs.addChangeListener(this);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.slideTabs, pnlRight);
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
			// default the created slide/template type to whatever tab/template they have selected
			Class<? extends Slide> clazz = BasicSlide.class;
			if (this.slideTabs.getSelectedIndex() == 1) {
				if (this.cmbTemplateType.getSelectedItem() == TemplateType.SLIDE) {
					clazz = BasicSlideTemplate.class;
				} else if (this.cmbTemplateType.getSelectedItem() == TemplateType.BIBLE) {
					clazz = BibleSlideTemplate.class;
				} else if (this.cmbTemplateType.getSelectedItem() == TemplateType.SONG) {
					clazz = SongSlideTemplate.class;
				} else if (this.cmbTemplateType.getSelectedItem() == TemplateType.NOTIFICATION) {
					clazz = NotificationSlideTemplate.class;
				}
			}
			// show dialog asking what type (slide/template)
			Slide slide = NewSlideDialog.show(WindowUtilities.getParentWindow(this), clazz);
			// check for null (null means the user canceled)
			if (slide != null) {
				// open the slide/template editor
				SlideEditorResult result = SlideEditorDialog.show(WindowUtilities.getParentWindow(this), slide, null);
				// check the return type
				if (result.getChoice() != SlideEditorOption.CANCEL) {
					// when control returns here we need to update the items in the jlist with the current media library items
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(slide.getClass());
					// get the list that should store this slide
					JList<SlideThumbnail> list = null;
					if (slide instanceof BasicSlideTemplate) {
						list = this.lstTemplates;
					} else if (slide instanceof BibleSlideTemplate) {
						list = this.lstBibleTemplates;
					} else if (slide instanceof SongSlideTemplate) {
						list = this.lstSongTemplates;
					} else if (slide instanceof NotificationSlideTemplate) {
						list = this.lstNotificationTemplates;
					} else {
						list = this.lstSlides;
					}
					updateThumbnailJList(list, thumbnails);
					this.slideLibraryUpdated = true;
				}
			}
			return;
		} else if ("import".equals(command)) {
			// show a file browser with the allowed types
			JFileChooser fc = new JFileChooser();
			// the user can only select files
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(true);
			// they can only select zip files
			fc.setFileFilter(new ZipFileFilter());
			fc.setAcceptAllFileFilterUsed(false);
			// show the dialog
			int result = fc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				// get the file
				final File file = fc.getSelectedFile();
				
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							SlideLibrary.importSlides(file);
							this.setSuccessful(true);
						} catch (Exception e) {
							this.handleException(e);
						}
					}
				};
				
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("importing"), task);
				
				if (task.isSuccessful()) {
					// update all the jlists since we could have imported all types
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
					updateThumbnailJList(this.lstSlides, thumbnails);
					
					thumbnails = SlideLibrary.getThumbnails(BasicSlideTemplate.class);
					updateThumbnailJList(this.lstTemplates, thumbnails);
					
					thumbnails = SlideLibrary.getThumbnails(BibleSlideTemplate.class);
					updateThumbnailJList(this.lstBibleTemplates, thumbnails);
					
					thumbnails = SlideLibrary.getThumbnails(SongSlideTemplate.class);
					updateThumbnailJList(this.lstSongTemplates, thumbnails);
					
					thumbnails = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
					updateThumbnailJList(this.lstNotificationTemplates, thumbnails);
					
					this.slideLibraryUpdated = true;
				} else {
					LOGGER.error("An error occurred while importing into the slide library: ", task.getException());
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.import.exception.title"), 
							MessageFormat.format(Messages.getString("panel.slide.import.exception.text"), file.getAbsolutePath()), 
							task.getException());
				}
			}
			
			return;
		} else if ("export".equals(command)) {
			// create a class to show a "are you sure" message when over writing an existing file
			JFileChooser fileBrowser = new ValidateFileChooser();
			fileBrowser.setMultiSelectionEnabled(false);
			fileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileBrowser.setDialogTitle(Messages.getString("panel.slide.export"));
			fileBrowser.setSelectedFile(new File(MessageFormat.format(Messages.getString("panel.slide.export.file"), "SlideLibrary")));
			
			int option = fileBrowser.showSaveDialog(this);
			// check the option
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileBrowser.getSelectedFile();
				
				final String targetFile = file.getAbsolutePath();
				
				// create a SlideExport object for all the slides and templates
				List<SlideExport> exportList = new ArrayList<SlideExport>();
				
				// slides
				List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
				for (SlideThumbnail thumbnail : thumbnails) {
					exportList.add(new SlideExport(thumbnail.getFile()));
				}
				thumbnails = SlideLibrary.getThumbnails(BasicSlideTemplate.class);
				for (SlideThumbnail thumbnail : thumbnails) {
					exportList.add(new SlideExport(thumbnail.getFile(), BasicSlideTemplate.class));
				}
				thumbnails = SlideLibrary.getThumbnails(BibleSlideTemplate.class);
				for (SlideThumbnail thumbnail : thumbnails) {
					exportList.add(new SlideExport(thumbnail.getFile(), BibleSlideTemplate.class));
				}
				thumbnails = SlideLibrary.getThumbnails(SongSlideTemplate.class);
				for (SlideThumbnail thumbnail : thumbnails) {
					exportList.add(new SlideExport(thumbnail.getFile(), SongSlideTemplate.class));
				}
				thumbnails = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
				for (SlideThumbnail thumbnail : thumbnails) {
					exportList.add(new SlideExport(thumbnail.getFile(), NotificationSlideTemplate.class));
				}
				
				final SlideExport[] exports = exportList.toArray(new SlideExport[0]);
				
				AbstractTask task = new AbstractTask() {
					@Override
					public void run() {
						try {
							SlideLibrary.exportSlides(targetFile, exports);
							this.setSuccessful(true);
						} catch (Exception e) {
							this.handleException(e);
						}
					}
				};
				
				// run the task
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("exporting"), task);
				
				// check the task result
				if (!task.isSuccessful()) {
					LOGGER.error("An error occurred while exporting the Slide/Template Library:", task.getException());
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.export.exception.title"), 
							Messages.getString("panel.slide.export.exception.text"), 
							task.getException());
				}
			}
			
			return;
		}
		
		// get the currently selected thumbnail
		JList<SlideThumbnail> list = null;
		int index = this.slideTabs.getSelectedIndex();
		Class<? extends Template> clazz = null;
		boolean isSlide = false;
		if (index == 0) {
			list = this.lstSlides;
			isSlide = true;
		} else if (index == 1) {
			Object type = this.cmbTemplateType.getSelectedItem();
			if (type == TemplateType.SLIDE) {
				list = this.lstTemplates;
				clazz = BasicSlideTemplate.class;
			} else if (type == TemplateType.BIBLE) {
				list = this.lstBibleTemplates;
				clazz = BibleSlideTemplate.class;
			} else if (type == TemplateType.SONG) {
				list = this.lstSongTemplates;
				clazz = SongSlideTemplate.class;
			} else if (type == TemplateType.NOTIFICATION) {
				list = this.lstNotificationTemplates;
				clazz = NotificationSlideTemplate.class;
			} else {
				LOGGER.error("Unknown template type [" + type + "]");
				return;
			}
		}
		final SlideThumbnail thumbnail = list.getSelectedValue();
		final boolean isTemplate = list != this.lstSlides;
		
		String type = Messages.getString("panel.slide");
		if (!isSlide) {
			type = Messages.getString("panel.template");
		}
		
		// make sure the currently selected item is not null
		if (thumbnail != null) {
			// check the action type
			if ("edit".equals(command)) {
				// get the selected slide or template
				SlideFile file = thumbnail.getFile();
				
				// load the slide on another thread
				LoadSlideTask task = new LoadSlideTask(file, clazz);
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
						this.slideLibraryUpdated = true;
					} else if (result.getChoice() == SlideEditorOption.SAVE_AS) {
						// when control returns here we need to update the items in the jlist with the current media library items
						List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(clazz == null ? BasicSlide.class : clazz);
						updateThumbnailJList(list, thumbnails);
						this.slideLibraryUpdated = true;
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
				LoadSlideTask task = new LoadSlideTask(file, clazz) {
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
								if (!isTemplate) {
									SlideLibrary.saveSlide(slide.getName(), (BasicSlide)slide);
								} else {
									SlideLibrary.saveTemplate(slide.getName(), (Template)slide);
								}
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
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(clazz == null ? BasicSlide.class : clazz);
					updateThumbnailJList(list, thumbnails);
					this.slideLibraryUpdated = true;
				} else {
					ExceptionDialog.show(
							this, 
							MessageFormat.format(Messages.getString("panel.slide.copy.exception.title"), type), 
							MessageFormat.format(Messages.getString("panel.slide.copy.exception.text"), type.toLowerCase(), file.getPath()), 
							task.getException());
					LOGGER.error("Failed to copy [" + file.getPath() + "]: ", task.getException());
				}
			} else if ("createTemplate".equals(command)) {
				// get the selected slide or template
				SlideFile file = thumbnail.getFile();
				
				// load/copy/save on another thread
				LoadSlideTask task = new LoadSlideTask(file, clazz) {
					@Override
					public void run() {
						super.run();
						// if the loading of the slide was successful then try to
						// copy it and save the copy
						if (this.isSuccessful() && this.slide != null) {
							// save the slide
							// make a copy of the slide so that the user can cancel the operation
							Template slide = this.slide.createTemplate();
							
							// rename the slide to copy of or something
							slide.setName(MessageFormat.format(Messages.getString("panel.slide.createTemplate.templateOf"), slide.getName()));
							
							// save the slide
							try {
								SlideLibrary.saveTemplate(slide.getName(), slide);
								this.setSuccessful(true);
							} catch (Exception e) {
								this.setSuccessful(false);
								this.handleException(e);
							}
						}
					}
				};
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.creating"), task);
				
				if (task.isSuccessful()) {
					// when control returns here we need to update the items in the jlist with the current media library items
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlideTemplate.class);
					updateThumbnailJList(this.lstTemplates, thumbnails);
					this.slideTabs.setSelectedIndex(1);
					this.cmbTemplateType.setSelectedItem(TemplateType.SLIDE);
					this.slideLibraryUpdated = true;
				} else {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.createTemplate.exception.title"), 
							MessageFormat.format(Messages.getString("panel.slide.createTemplate.exception.text"), file.getPath()), 
							task.getException());
					LOGGER.error("Failed to create template from [" + file.getPath() + "]: ", task.getException());
				}
			} else if ("createSlide".equals(command)) {
				// get the selected slide or template
				SlideFile file = thumbnail.getFile();
				
				// load/copy/save on another thread
				LoadSlideTask task = new LoadSlideTask(file, clazz) {
					@Override
					public void run() {
						super.run();
						// if the loading of the slide was successful then try to
						// copy it and save the copy
						if (this.isSuccessful() && this.slide != null) {
							// create a copy of the template using the basic slide constructor
							BasicSlide slide = new BasicSlide((BasicSlide)this.slide);
							// move over any static components
							List<SlideComponent> components = this.slide.getStaticComponents(SlideComponent.class);
							for (SlideComponent component : components) {
								slide.addComponent(component);
							}
							
							// rename the slide to copy of or something
							slide.setName(MessageFormat.format(Messages.getString("panel.slide.createSlide.slideOf"), slide.getName()));
							
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
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.creating"), task);
				
				if (task.isSuccessful()) {
					// when control returns here we need to update the items in the jlist with the current media library items
					List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
					updateThumbnailJList(this.lstSlides, thumbnails);
					this.slideTabs.setSelectedIndex(0);
					this.slideLibraryUpdated = true;
				} else {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.createSlide.exception.title"), 
							MessageFormat.format(Messages.getString("panel.slide.createSlide.exception.text"), file.getPath()), 
							task.getException());
					LOGGER.error("Failed to create slide from [" + file.getPath() + "]: ", task.getException());
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
							if (!isTemplate) {
								SlideLibrary.deleteSlide(thumbnail.getFile().getPath());
								this.setSuccessful(true);
							} else {
								SlideLibrary.deleteTemplate(thumbnail.getFile().getPath());
								this.setSuccessful(true);
							}
						}
					};
					
					TaskProgressDialog.show(WindowUtilities.getParentWindow(this), MessageFormat.format(Messages.getString("panel.slide.removing"), type), task);
					if (task.isSuccessful()) {
						if (list != null) {
							// remove the thumbnail from the list
							DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
							model.removeElement(thumbnail);
						}
						this.slideLibraryUpdated = true;
					} else {
						ExceptionDialog.show(
								this, 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.title"), type), 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.text"), type.toLowerCase(), thumbnail.getFile().getName()), 
								task.getException());
						LOGGER.error("An error occurred while attempting to remove [" + thumbnail.getFile().getPath() + "] from the slide library: ", task.getException());
					}
				}
			} else if ("export-slide".equals(command)) {
				// strip the .xml from the end
				String fileName = thumbnail.getFile().getName();
				int k = fileName.lastIndexOf(".");
				String name = fileName.substring(0, k < 0 ? fileName.length() : k);
				
				// create a class to show a "are you sure" message when over writing an existing file
				JFileChooser fileBrowser = new ValidateFileChooser();
				fileBrowser.setMultiSelectionEnabled(false);
				fileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileBrowser.setDialogTitle(MessageFormat.format(Messages.getString("panel.slide.export.slide.title"), type));
				fileBrowser.setSelectedFile(new File(MessageFormat.format(Messages.getString("panel.slide.export.file"), name)));
				
				int option = fileBrowser.showSaveDialog(this);
				// check the option
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fileBrowser.getSelectedFile();
					
					final String targetFile = file.getAbsolutePath();
					final SlideExport export = new SlideExport(thumbnail.getFile(), isSlide ? null : clazz);
					
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							try {
								SlideLibrary.exportSlides(targetFile, export);
								this.setSuccessful(true);
							} catch (Exception e) {
								this.handleException(e);
							}
						}
					};
					
					// run the task
					TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("exporting"), task);
					
					// check the task result
					if (!task.isSuccessful()) {
						LOGGER.error("An error occurred while exporting the slide/template [" + thumbnail.getFile().getPath() + "]: ", task.getException());
						ExceptionDialog.show(
								this, 
								MessageFormat.format(Messages.getString("panel.slide.export.slide.exception.title"), type), 
								MessageFormat.format(Messages.getString("panel.slide.export.slide.exception.text"), type.toLowerCase(), thumbnail.getFile().getName()), 
								task.getException());
					}
				}
			}
		}
	}
	
	/**
	 * Updates the given JList with the given thumbnails.
	 * @param list the list to update
	 * @param thumbnails the new list of thumbnails
	 */
	private static final void updateThumbnailJList(JList<SlideThumbnail> list, List<SlideThumbnail> thumbnails) {
		SlideThumbnail thumbnail = list.getSelectedValue();
		list.clearSelection();
		DefaultListModel<SlideThumbnail> model = (DefaultListModel<SlideThumbnail>)list.getModel();
		model.removeAllElements();
		for (SlideThumbnail thumb : thumbnails) {
			model.addElement(thumb);
		}
		if (thumbnail != null) {
			list.setSelectedValue(thumbnail, true);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			
			PreviewAction<?> preview = null;
			SlideFile file = null;
			boolean isSlide = false;
			if (source == this.lstSlides) {
				SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new SlidePreviewAction<BasicSlide>(file.getPath(), BasicSlide.class);
					isSlide = true;
				}
			} else if (source == this.lstTemplates) {
				SlideThumbnail thumbnail = this.lstTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<BasicSlideTemplate>(file.getPath(), BasicSlideTemplate.class);
				}
			} else if (source == this.lstBibleTemplates) {
				SlideThumbnail thumbnail = this.lstBibleTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<BibleSlideTemplate>(file.getPath(), BibleSlideTemplate.class);
				}
			} else if (source == this.lstSongTemplates) {
				SlideThumbnail thumbnail = this.lstSongTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<SongSlideTemplate>(file.getPath(), SongSlideTemplate.class);
				}
			} else if (source == this.lstNotificationTemplates) {
				SlideThumbnail thumbnail = this.lstNotificationTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<NotificationSlideTemplate>(file.getPath(), NotificationSlideTemplate.class);
				}
			}
			if (preview != null && file != null) {
				this.pnlPreview.setLoading(true);
				this.getPreviewThread().queueSlide(preview);
				this.pnlProperties.setSlideFile(file, isSlide);
				this.btnEditSlide.setEnabled(true);
				this.btnCopySlide.setEnabled(true);
				this.btnCreateSlide.setVisible(!isSlide);
				if (source == this.lstNotificationTemplates) {
					// you can't create slides from these templates
					this.btnCreateSlide.setVisible(false);
				}
				this.btnCreateTemplate.setVisible(isSlide);
				this.btnRemoveSlide.setEnabled(true);
				this.btnExportSlide.setEnabled(true);
			} else {
				this.pnlPreview.setSlide(null);
				this.pnlProperties.setSlideFile(null, true);
				this.pnlPreview.repaint();
				this.btnEditSlide.setEnabled(false);
				this.btnCopySlide.setEnabled(false);
				this.btnCreateSlide.setVisible(false);
				this.btnCreateTemplate.setVisible(false);
				this.btnRemoveSlide.setEnabled(false);
				this.btnExportSlide.setEnabled(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.slideTabs) {
			this.setPreviewAndProperties();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource() == this.cmbTemplateType) {
				Object item = e.getItem();
				String card = null;
				if (item == TemplateType.BIBLE) {
					card = BIBLE_CARD;
				} else if (item == TemplateType.SONG) {
					card = SONG_CARD;
				} else if (item == TemplateType.NOTIFICATION) {
					card = NOTIFICATION_CARD;
				} else {
					card = SLIDE_CARD;
				}
				// switch which card is shown
				this.layTemplateCards.show(this.pnlTemplateCards, card);
				this.setPreviewAndProperties();
			}
		}
	}
	
	/**
	 * Used to set the state of the preview and properties panels along
	 * with the edit/remove buttons.
	 */
	private void setPreviewAndProperties() {
		int index = this.slideTabs.getSelectedIndex();
		SlideFile file = null;
		PreviewAction<?> preview = null;
		boolean isSlide = false;
		Object type = null;
		if (index == 0) {
			SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
			if (thumbnail != null) {
				file = thumbnail.getFile();
				preview = new SlidePreviewAction<BasicSlide>(file.getPath(), BasicSlide.class);
				isSlide = true;
			}
		} else if (index == 1) {
			type = this.cmbTemplateType.getSelectedItem();
			if (type == TemplateType.SLIDE) {
				SlideThumbnail thumbnail = this.lstTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<BasicSlideTemplate>(file.getPath(), BasicSlideTemplate.class);
				}
			} else if (type == TemplateType.BIBLE) {
				SlideThumbnail thumbnail = this.lstBibleTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<BibleSlideTemplate>(file.getPath(), BibleSlideTemplate.class);
				}
			} else if (type == TemplateType.SONG) {
				SlideThumbnail thumbnail = this.lstSongTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<SongSlideTemplate>(file.getPath(), SongSlideTemplate.class);
				}
			} else if (type == TemplateType.NOTIFICATION) {
				SlideThumbnail thumbnail = this.lstNotificationTemplates.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
					preview = new TemplatePreviewAction<NotificationSlideTemplate>(file.getPath(), NotificationSlideTemplate.class);
				}
			}
		}
		if (file != null) {
			this.pnlPreview.setLoading(true);
			this.pnlProperties.setSlideFile(file, isSlide);
			this.getPreviewThread().queueSlide(preview);
			this.btnEditSlide.setEnabled(true);
			this.btnCopySlide.setEnabled(true);
			this.btnCreateSlide.setVisible(!isSlide);
			if (type == TemplateType.NOTIFICATION) {
				// you can't create slides from these templates
				this.btnCreateSlide.setVisible(false);
			}
			this.btnCreateTemplate.setVisible(isSlide);
			this.btnRemoveSlide.setEnabled(true);
			this.btnExportSlide.setEnabled(true);
		} else {
			this.pnlProperties.setSlideFile(null, true);
			this.pnlPreview.setSlide(null);
			this.pnlPreview.repaint();
			this.btnEditSlide.setEnabled(false);
			this.btnCopySlide.setEnabled(false);
			this.btnCreateSlide.setVisible(false);
			this.btnCreateTemplate.setVisible(false);
			this.btnRemoveSlide.setEnabled(false);
			this.btnExportSlide.setEnabled(false);
		}
	}
	
	/**
	 * Returns true if the slide library was updated.
	 * @return boolean
	 */
	public boolean isSlideLibraryUpdated() {
		return this.slideLibraryUpdated;
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
		
		/** The slide class; null if {@link BasicSlide} */
		protected Class<? extends Template> clazz;
		
		/** The loaded slide */
		protected Slide slide;
		
		/**
		 * Full constructor.
		 * @param file the slide file
		 * @param clazz the slide class; null if {@link BasicSlide}
		 */
		public LoadSlideTask(SlideFile file, Class<? extends Template> clazz) {
			this.file = file;
			this.clazz = clazz;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				if (this.clazz != null) {
					this.slide = SlideLibrary.getTemplate(this.file.getPath(), this.clazz);
				} else {
					this.slide = SlideLibrary.getSlide(this.file.getPath());
				}
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
