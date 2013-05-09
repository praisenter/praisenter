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
package org.praisenter.application.slide.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
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
import javax.swing.JFormattedTextField;
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
import org.praisenter.animation.TransitionAnimator;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.application.Praisenter;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.preferences.SlidePreferences;
import org.praisenter.application.preferences.ui.PreferencesListener;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.editor.SlideEditorDialog;
import org.praisenter.application.slide.ui.editor.SlideEditorOption;
import org.praisenter.application.slide.ui.editor.SlideEditorResult;
import org.praisenter.application.slide.ui.preview.SingleSlidePreviewPanel;
import org.praisenter.application.ui.OpaquePanel;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.presentation.ClearEvent;
import org.praisenter.presentation.PresentationEventConfiguration;
import org.praisenter.presentation.PresentationManager;
import org.praisenter.presentation.PresentationWindowType;
import org.praisenter.presentation.SendEvent;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;

/**
 * Panel for custom slides.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class SlidePanel extends OpaquePanel implements ListSelectionListener, ActionListener, SlideLibraryListener, PreferencesListener {
	/** The version id */
	private static final long serialVersionUID = 5740087054031715193L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlidePanel.class);
	
	// controls
	
	/** The list of saved slides */
	private JList<SlideThumbnail> lstSlides;
	
	/** The slide/template preview panel */
	private SingleSlidePreviewPanel pnlPreview;

	/** The combo box of transitions for sending */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box of send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of transitions for clearing */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box of clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	/** The edit slide button */
	private JButton btnEdit;
	
	/** The remove slide button */
	private JButton btnRemove;
	
	// state
	
	/** The slide/template preview thread */
	private SlidePreivewThread previewThread;
	
	/** The preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/** The slide preferences */
	private SlidePreferences sPreferences = this.preferences.getSlidePreferences();
	
	/**
	 * Default constructor.
	 */
	public SlidePanel() {
		this.pnlPreview = new SingleSlidePreviewPanel();
		this.pnlPreview.setOpaque(false);
		Dimension size = new Dimension(100, 100);
		this.pnlPreview.setPreferredSize(size);
		
		this.previewThread = new SlidePreivewThread();
		this.previewThread.start();
		
		List<SlideThumbnail> thumbnails = null;
		try {
			thumbnails = SlideLibrary.getInstance().getThumbnails(BasicSlide.class);
		} catch (NotInitializedException e) {
			thumbnails = new ArrayList<SlideThumbnail>();
		}
		this.lstSlides = createJList(thumbnails);
		JScrollPane scrSlides = new JScrollPane(this.lstSlides);
		scrSlides.setPreferredSize(new Dimension(250, 400));

		JButton btnNewSlide = new JButton(Messages.getString("panel.slide.new"));
		btnNewSlide.setToolTipText(Messages.getString("panel.slide.new.tooltip"));
		btnNewSlide.addActionListener(this);
		btnNewSlide.setActionCommand("newSlide");
		
		this.btnEdit = new JButton(Messages.getString("slide.edit"));
		this.btnEdit.setToolTipText(Messages.getString("slide.edit.tooltip"));
		this.btnEdit.setEnabled(false);
		this.btnEdit.setActionCommand("editSlide");
		this.btnEdit.addActionListener(this);
		
		this.btnRemove = new JButton(Messages.getString("panel.slide.remove"));
		this.btnRemove.setToolTipText(Messages.getString("panel.slide.remove.tooltip"));
		this.btnRemove.addActionListener(this);
		this.btnRemove.setActionCommand("removeSlide");
		this.btnRemove.setEnabled(false);
		
		JButton btnManageSlides = new JButton(Messages.getString("slide.manage"));
		btnManageSlides.setToolTipText(Messages.getString("slide.manage.tooltip"));
		btnManageSlides.addActionListener(this);
		btnManageSlides.setActionCommand("manageSlides");
		
		JPanel pnlSlideControls = new OpaquePanel();
		pnlSlideControls.setLayout(new GridLayout(2, 2, 4, 4));
		pnlSlideControls.add(btnNewSlide);
		pnlSlideControls.add(this.btnEdit);
		pnlSlideControls.add(this.btnRemove);
		pnlSlideControls.add(btnManageSlides);
		
		JPanel pnlRight = new OpaquePanel();
		GroupLayout rLayout = new GroupLayout(pnlRight);
		pnlRight.setLayout(rLayout);
		
		rLayout.setAutoCreateGaps(true);
		rLayout.setHorizontalGroup(rLayout.createParallelGroup()
				.addComponent(scrSlides)
				.addComponent(pnlSlideControls));
		rLayout.setVerticalGroup(rLayout.createSequentialGroup()
				.addComponent(scrSlides)
				.addComponent(pnlSlideControls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		// get the primary device
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(this.sPreferences.getSendTransitionId(), TransitionType.IN));
		this.txtSendTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(this.sPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(this.sPreferences.getClearTransitionId(), TransitionType.OUT));
		this.txtClearTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(this.sPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		JButton btnSend = new JButton(Messages.getString("panel.bible.send"));
		btnSend.setToolTipText(Messages.getString("panel.bible.send.tooltip"));
		btnSend.addActionListener(this);
		btnSend.setActionCommand("send");
		btnSend.setFont(btnSend.getFont().deriveFont(Font.BOLD, btnSend.getFont().getSize2D() + 3.0f));
		btnSend.setMinimumSize(new Dimension(0, 50));
		
		JButton btnClear = new JButton(Messages.getString("panel.bible.clear"));
		btnClear.setToolTipText(Messages.getString("panel.bible.clear.tooltip"));
		btnClear.addActionListener(this);
		btnClear.setActionCommand("clear");
		btnClear.setMinimumSize(new Dimension(0, 50));
		
		// create a panel and layout for the send/clear controls
		JPanel pnlSendClearButtons = new OpaquePanel();
		GroupLayout subLayout = new GroupLayout(pnlSendClearButtons);
		pnlSendClearButtons.setLayout(subLayout);
		pnlSendClearButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		subLayout.setAutoCreateGaps(true);
		subLayout.setHorizontalGroup(subLayout.createSequentialGroup()
				.addGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addComponent(this.cmbSendTransitions)
								.addComponent(this.txtSendTransitions))
						.addComponent(btnSend, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(subLayout.createParallelGroup()
						.addGroup(subLayout.createSequentialGroup()
								.addComponent(this.cmbClearTransitions)
								.addComponent(this.txtClearTransitions))
						.addComponent(btnClear, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		subLayout.setVerticalGroup(subLayout.createSequentialGroup()
				.addGroup(subLayout.createParallelGroup()
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(subLayout.createParallelGroup()
						.addComponent(btnSend)
						.addComponent(btnClear)));
		
		JPanel pnlNoResize = new OpaquePanel();
		GroupLayout layout = new GroupLayout(pnlNoResize);
		pnlNoResize.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlSendClearButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlSendClearButtons, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		
		JSplitPane pnePreview = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.pnlPreview, pnlNoResize);
		pnePreview.setResizeWeight(0.85);
		pnePreview.setOneTouchExpandable(true);
		pnePreview.setBorder(null);
		pnePreview.setOpaque(false);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnePreview, pnlRight);
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(0.6);
		pane.setBorder(null);
		pane.setOpaque(false);
		
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
	
	/**
	 * Verifies the slide is sized to the primary display size.
	 * <p>
	 * If not, the slide is adjusted to fit.
	 * @param slide the slide
	 */
	private void verifySlideDimensions(BasicSlide slide) {
		Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
		// check the template size against the display size
		if (slide.getWidth() != size.width || slide.getHeight() != size.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Slide [" + slide.getName() + "] is not sized correctly for the primary display. Adjusing slide.");
			slide.adjustSize(size.width, size.height);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if ("send".equals(command)) {
			Slide slide = this.pnlPreview.getSlide();
			if (slide != null) {
				// use a copy
				slide = slide.copy();
				// get the transition
				Transition transition = (Transition)this.cmbSendTransitions.getSelectedItem();
				int duration = ((Number)this.txtSendTransitions.getValue()).intValue();
				int delay = this.preferences.getTransitionDelay();
				Easing easing = Easings.getEasingForId(this.sPreferences.getSendTransitionEasingId());
				TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
				
				Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
				// check the slide size against the display size
				if (slide.getWidth() != size.width || slide.getHeight() != size.height) {
					// log a message and modify the template to fit
					LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
					slide.adjustSize(size.width, size.height);
				}
				
				// get the configuration
				PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
				
				// execute a new send event to the primary fullscreen display
				PresentationManager.getInstance().execute(new SendEvent(configuration, animator, slide));
			}
		} else if ("clear".equals(command)) {
			Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
			int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
			int delay = this.preferences.getTransitionDelay();
			Easing easing = Easings.getEasingForId(this.sPreferences.getClearTransitionEasingId());
			TransitionAnimator animator = new TransitionAnimator(transition, duration, delay, easing);
			// get the configuration
			PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.FULLSCREEN);
			// execute a new clear event to the primary fullscreen display
			PresentationManager.getInstance().execute(new ClearEvent(configuration, animator));
		} else if ("editSlide".equals(command)) {
			SlideFile file = null;
			SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
			if (thumbnail != null) {
				file = thumbnail.getFile();
				// load the slide on another thread
				LoadSlideTask task = new LoadSlideTask(file, null);
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.slide.loading"), task);
				
				if (task.isSuccessful()) {
					// edit a copy just in case they cancel or do a save as to a new file
					Slide slide = task.getSlide().copy();
					SlideEditorResult result = SlideEditorDialog.show(WindowUtilities.getParentWindow(this), slide, file);
					if (result.getChoice() == SlideEditorOption.SAVE || result.getChoice() == SlideEditorOption.SAVE_AS) {
						firePropertyChange(Praisenter.PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, null, null);
					}
				} else {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.slide.load.exception.title"), 
							MessageFormat.format(Messages.getString("panel.slide.load.exception.text"), file.getRelativePath()), 
							task.getException());
					LOGGER.error("Failed to load [" + file.getRelativePath() + "] from the slide library: ", task.getException());
				}
			}
		} else if ("newSlide".equals(command)) {
			// get the target display size
			Dimension size = Preferences.getInstance().getPrimaryOrDefaultDeviceResolution();
			BasicSlide slide = new BasicSlide(Messages.getString("panel.slide.create.slide.name"), size.width, size.height);
			// check for null (null means the user canceled)
			if (slide != null) {
				// open the slide/template editor
				SlideEditorResult result = SlideEditorDialog.show(WindowUtilities.getParentWindow(this), slide, null);
				// check the return type
				if (result.getChoice() != SlideEditorOption.CANCEL) {
					// update this panel only
					this.onPreferencesOrSlideLibraryChanged();
				}
			}
		} else if ("removeSlide".equals(command)) {
			// get the selected slide
			final SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
			if (thumbnail != null) {
				// show an are you sure dialog, then delete the slide
				int choice = JOptionPane.showConfirmDialog(
						this, 
						MessageFormat.format(Messages.getString("panel.slide.remove.message"), thumbnail.getName()),
						MessageFormat.format(Messages.getString("panel.slide.remove.title"), Messages.getString("panel.slide")),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					// remove the slide/template in another thread
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							try {
								SlideLibrary library = SlideLibrary.getInstance();
								library.deleteSlide(thumbnail.getFile());
								this.setSuccessful(true);
							} catch (Exception ex) {
								this.handleException(ex);
							}
						}
					};
					
					TaskProgressDialog.show(WindowUtilities.getParentWindow(this), MessageFormat.format(Messages.getString("panel.slide.removing"), Messages.getString("panel.slide")), task);
					if (task.isSuccessful()) {
						// update the listing
						this.onPreferencesOrSlideLibraryChanged();
					} else {
						ExceptionDialog.show(
								this, 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.title"), Messages.getString("panel.slide")), 
								MessageFormat.format(Messages.getString("panel.slide.remove.exception.text"), Messages.getString("panel.slide").toLowerCase(), thumbnail.getFile().getName()), 
								task.getException());
						LOGGER.error("An error occurred while attempting to remove [" + thumbnail.getFile().getRelativePath() + "] from the slide library: ", task.getException());
					}
				}
			}
		} else if ("manageSlides".equals(command)) {
			boolean updated = SlideLibraryDialog.show(WindowUtilities.getParentWindow(this), BasicSlide.class);
			if (updated) {
				firePropertyChange(Praisenter.PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, null, null);
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
				SlideThumbnail thumbnail = this.lstSlides.getSelectedValue();
				if (thumbnail != null) {
					file = thumbnail.getFile();
				}
				
				if (file != null) {
					this.pnlPreview.setLoading(true);
					this.getPreviewThread().queueSlide(file.getRelativePath());
					this.btnEdit.setEnabled(true);
					this.btnRemove.setEnabled(true);
				} else {
					this.btnEdit.setEnabled(false);
					this.btnRemove.setEnabled(false);
					this.pnlPreview.setSlide(null);
					this.pnlPreview.repaint();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesListener#preferencesChanged()
	 */
	@Override
	public void preferencesChanged() {
		this.onPreferencesOrSlideLibraryChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
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
		
		List<SlideThumbnail> thumbnails = null;
		try {
			thumbnails = SlideLibrary.getInstance().getThumbnails(BasicSlide.class);
		} catch (NotInitializedException e) {
			thumbnails = new ArrayList<SlideThumbnail>();
		}
		
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
		private final BlockingQueue<String> slideQueue;
		
		/**
		 * Default constructor.
		 */
		public SlidePreivewThread() {
			super("SlidePreivewThread");
			this.setDaemon(true);
			this.slideQueue = new ArrayBlockingQueue<String>(10);
		}

		/**
		 * Queues a new preview.
		 * @param path the path to the slide in the slide library
		 */
		public void queueSlide(String path) {
			this.slideQueue.add(path);
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
					String path = this.slideQueue.poll(1000, TimeUnit.MILLISECONDS);
					// check if its null
					if (path != null) {
						// if it isnt then attempt to load the slide
						BasicSlide slide = null;
						try {
							// get the slide
							slide = SlideLibrary.getInstance().getSlide(path);
							if (slide != null) {
								// make a copy of the slide
								slide = slide.copy();
								// verify the dimensions of the slide
								verifySlideDimensions(slide);
								// update the slides (this method should not
								// do anything that should normally be done on the EDT)
								pnlPreview.setSlide(slide);
							}
						} catch (Exception e) {
							LOGGER.error("An error occurred while loading the slide [" + path + "]: ", e);
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
}
