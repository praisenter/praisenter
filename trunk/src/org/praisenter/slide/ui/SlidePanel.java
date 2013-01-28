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
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
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
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.easings.CubicEasing;
import org.praisenter.easings.Easing;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.ui.PreferencesListener;
import org.praisenter.resources.Messages;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.Template;
import org.praisenter.slide.ui.present.ClearEvent;
import org.praisenter.slide.ui.present.SendEvent;
import org.praisenter.slide.ui.present.SlideWindow;
import org.praisenter.slide.ui.present.SlideWindows;
import org.praisenter.slide.ui.preview.SingleSlidePreviewPanel;
import org.praisenter.transitions.Swap;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;
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
	
	// state
	
	/** The slide/template preview thread */
	private SlidePreivewThread previewThread;
	
	/** The preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/**
	 * Default constructor.
	 */
	public SlidePanel() {
		this.pnlPreview = new SingleSlidePreviewPanel();
		Dimension size = new Dimension(300, 300);
		this.pnlPreview.setMinimumSize(size);
		this.pnlPreview.setPreferredSize(size);
		
		this.previewThread = new SlidePreivewThread();
		this.previewThread.start();
		
		List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BasicSlide.class);
		this.lstSlides = createJList(thumbnails);
		JScrollPane scrSlides = new JScrollPane(this.lstSlides);
		
		// get the primary device
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(Swap.ID, Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(400);
		this.txtSendTransitions.setColumns(3);
		
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(Swap.ID, Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(300);
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
		JPanel pnlSendClearButtons = new JPanel();
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
		
		JPanel pnlLeft = new JPanel();
		GroupLayout layout = new GroupLayout(pnlLeft);
		pnlLeft.setLayout(layout);
		
		JSeparator sep = new JSeparator();
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.pnlPreview)
				.addComponent(sep)
				.addComponent(pnlSendClearButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.pnlPreview)
				.addComponent(sep, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlSendClearButtons));
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, scrSlides);
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(0.4);
		
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
		
		if ("send".equals(command)) {
			// get the transition
			Transition transition = (Transition)this.cmbSendTransitions.getSelectedItem();
			int duration = ((Number)this.txtSendTransitions.getValue()).intValue();
			Easing easing = Easings.getEasingForId(CubicEasing.ID);
			TransitionAnimator ta = new TransitionAnimator(transition, duration, easing);
			SlideWindow primary = SlideWindows.getPrimarySlideWindow();
			if (primary != null) {
				Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
				Slide slide = this.pnlPreview.getSlide();
				// check the slide size against the display size
				if (slide.getWidth() != size.width || slide.getHeight() != size.height) {
					// log a message and modify the template to fit
					LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
					slide.adjustSize(size.width, size.height);
				}
				primary.execute(new SendEvent(slide, ta));
			} else {
				// the device is no longer available
				LOGGER.warn("The primary display doesn't exist.");
				JOptionPane.showMessageDialog(
						WindowUtilities.getParentWindow(this), 
						Messages.getString("dialog.device.primary.missing.text"), 
						Messages.getString("dialog.device.primary.missing.title"), 
						JOptionPane.WARNING_MESSAGE);
			}
		} else if ("clear".equals(command)) {
			Transition transition = (Transition)this.cmbClearTransitions.getSelectedItem();
			int duration = ((Number)this.txtClearTransitions.getValue()).intValue();
			Easing easing = Easings.getEasingForId(CubicEasing.ID);
			TransitionAnimator ta = new TransitionAnimator(transition, duration, easing);
			SlideWindow primary = SlideWindows.getPrimarySlideWindow();
			if (primary != null) {
				primary.execute(new ClearEvent(ta));
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
				} else {
					this.pnlPreview.setSlide(null);
					this.pnlPreview.repaint();
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
}
