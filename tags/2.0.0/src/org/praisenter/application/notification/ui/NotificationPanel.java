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
package org.praisenter.application.notification.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.praisenter.animation.TransitionAnimator;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.application.preferences.NotificationPreferences;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.preferences.ui.PreferencesListener;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.SlideLibraryListener;
import org.praisenter.application.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.application.slide.ui.TransitionListCellRenderer;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.application.ui.WaterMark;
import org.praisenter.common.NotInitializedException;
import org.praisenter.presentation.ClearEvent;
import org.praisenter.presentation.PresentationEventConfiguration;
import org.praisenter.presentation.PresentationManager;
import org.praisenter.presentation.PresentationWindowType;
import org.praisenter.presentation.SendWaitClearEvent;
import org.praisenter.slide.NotificationSlide;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideLibraryException;
import org.praisenter.slide.SlideThumbnail;

/**
 * Panel used to send notifications.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class NotificationPanel extends JPanel implements ActionListener, ItemListener, PreferencesListener, SlideLibraryListener {
	/** The version id */
	private static final long serialVersionUID = 20837022721408081L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(NotificationPanel.class);
	
	// slide
	
	/** The notification slide */
	private NotificationSlide slide;
	
	// controls
	
	/** The notification text */
	private JTextField txtText;

	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
	/** The notification wait period */
	private JFormattedTextField txtWaitPeriod;
	
	/** The in transition */
	private JComboBox<Transition> cmbInTransition;
	
	/** The in transition duration */
	private JFormattedTextField txtInTransition;
	
	/** The out transition */
	private JComboBox<Transition> cmbOutTransition;
	
	/** The out transition duration */
	private JFormattedTextField txtOutTransition;
	
	/** The send button */
	private JButton btnSend;
	
	/** The manual clear button */
	private JButton btnClear;
	
	// preferences 
	
	/** A local reference to the preferences */
	private Preferences preferences = Preferences.getInstance();
	
	/** A local references to the notification preferences */
	private NotificationPreferences nPreferences = this.preferences.getNotificationPreferences();
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public NotificationPanel() {
		// get the primary device and size
		GraphicsDevice device = this.preferences.getPrimaryOrDefaultDevice();
		
		// get the bible slide template
		NotificationSlideTemplate template = this.getTemplate();
		
		// create the slide
		this.slide = template.createSlide();
		
		this.txtText = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.notification.text.watermark"));
			}
		};
		this.txtText.setColumns(10);
		this.txtText.addFocusListener(new SelectTextFocusListener(this.txtText));
		
		SlideThumbnail[] thumbnails = this.getThumbnails();
		SlideThumbnail selected = this.getSelectedThumbnail(thumbnails);
		this.cmbTemplates = new JComboBox<SlideThumbnail>(thumbnails);
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.template"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		this.cmbTemplates.addItemListener(this);
		
		this.txtWaitPeriod = new JFormattedTextField(new DecimalFormat("0"));
		this.txtWaitPeriod.setToolTipText(Messages.getString("panel.notification.wait.tooltip"));
		this.txtWaitPeriod.setValue(this.nPreferences.getWaitPeriod());
		this.txtWaitPeriod.setColumns(4);
		this.txtWaitPeriod.addFocusListener(new SelectTextFocusListener(this.txtWaitPeriod));
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(device);
		
		this.cmbInTransition = new JComboBox<Transition>(Transitions.IN);
		this.cmbInTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbInTransition.setSelectedItem(Transitions.getTransitionForId(this.nPreferences.getSendTransitionId(), TransitionType.IN));
		this.cmbInTransition.setToolTipText(Messages.getString("panel.notification.send.inTransition"));
		this.txtInTransition = new JFormattedTextField(new DecimalFormat("0"));
		this.txtInTransition.addFocusListener(new SelectTextFocusListener(this.txtInTransition));
		this.txtInTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtInTransition.setValue(this.nPreferences.getSendTransitionDuration());
		this.txtInTransition.setColumns(3);
		
		this.cmbOutTransition = new JComboBox<Transition>(Transitions.OUT);
		this.cmbOutTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbOutTransition.setSelectedItem(Transitions.getTransitionForId(this.nPreferences.getClearTransitionId(), TransitionType.OUT));
		this.cmbOutTransition.setToolTipText(Messages.getString("panel.notification.send.outTransition"));
		this.txtOutTransition = new JFormattedTextField(new DecimalFormat("0"));
		this.txtOutTransition.addFocusListener(new SelectTextFocusListener(this.txtOutTransition));
		this.txtOutTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtOutTransition.setValue(this.nPreferences.getClearTransitionDuration());
		this.txtOutTransition.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbInTransition.setEnabled(false);
			this.txtInTransition.setEnabled(false);
			this.cmbOutTransition.setEnabled(false);
			this.txtOutTransition.setEnabled(false);
		}
		
		this.btnSend = new JButton(Messages.getString("panel.notification.send"));
		this.btnSend.setToolTipText(Messages.getString("panel.notification.send.tooltip"));
		this.btnSend.addActionListener(this);
		this.btnSend.setActionCommand("send");
		
		this.btnClear = new JButton(Messages.getString("panel.notification.clear"));
		this.btnClear.setToolTipText(Messages.getString("panel.notification.clear.tooltip"));
		this.btnClear.setActionCommand("clear");
		this.btnClear.addActionListener(this);
		
		// set the layout
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtText)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbTemplates)
								.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.btnSend)
						.addComponent(this.btnClear)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnClear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Returns the template to use from the preferences.
	 * @return {@link NotificationSlideTemplate}
	 */
	private NotificationSlideTemplate getTemplate() {
		// get the primary device size
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		// get the bible slide template
		NotificationSlideTemplate template = null;
		String templatePath = this.nPreferences.getTemplate();
		try {
			SlideLibrary library = SlideLibrary.getInstance();
			if (templatePath != null && templatePath.trim().length() > 0 && library != null) {
				try {
					template = library.getTemplate(templatePath, NotificationSlideTemplate.class);
				} catch (SlideLibraryException e) {
					LOGGER.error("Unable to load default notification template [" + templatePath + "]: ", e);
				}
			}
		} catch (NotInitializedException e) {
			LOGGER.error(e);
		}
		
		if (template == null) {
			// if its still null, then use the default template
			template = NotificationSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		}
		
		// check the template size against the display size
		this.verifyTemplateDimensions(template, displaySize);
		
		return template;
	}
	
	/**
	 * Verifies the template is sized to the given size.
	 * <p>
	 * If not, the template is adjusted to fit.
	 * @param template the template
	 * @param size the size
	 */
	private void verifyTemplateDimensions(NotificationSlideTemplate template, Dimension size) {
		// check the template size against the display size
		if (template.getDeviceWidth() != size.width || template.getDeviceHeight() != size.height) {
			// log a message and modify the template to fit
			LOGGER.warn("Template is not sized correctly for the primary display. Adjusing template.");
			template.adjustSize(size.width, size.height);
		}
	}
	
	/**
	 * Returns an array of {@link SlideThumbnail}s for {@link NotificationSlideTemplate}s.
	 * @return {@link SlideThumbnail}[]
	 */
	private SlideThumbnail[] getThumbnails() {
		Dimension displaySize = this.preferences.getPrimaryOrDefaultDeviceResolution();
		
		List<SlideThumbnail> thumbs = null;
		try {
			thumbs = SlideLibrary.getInstance().getThumbnails(NotificationSlideTemplate.class);
		} catch (NotInitializedException e) {
			thumbs = new ArrayList<SlideThumbnail>();
		}
		
		// add in the default template
		NotificationSlideTemplate dTemplate = NotificationSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = dTemplate.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, dTemplate.getName(), image);
		thumbs.add(temp);
		
		// sort them
		Collections.sort(thumbs);
		
		return thumbs.toArray(new SlideThumbnail[0]);
	}
	
	/**
	 * Returns the selected thumbnail for the selected {@link NotificationSlideTemplate}
	 * given in the preferences.
	 * @param thumbnails the list of all slide thumbnails
	 * @return {@link SlideThumbnail}
	 */
	private SlideThumbnail getSelectedThumbnail(SlideThumbnail[] thumbnails) {
		for (SlideThumbnail thumb : thumbnails) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (this.nPreferences.getTemplate() == null) {
					return thumb;
				}
			} else if (thumb.getFile().getRelativePath().equals(this.nPreferences.getTemplate())) {
				return thumb;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbTemplates) {
				SlideThumbnail thumbnail = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
				if (thumbnail != null) {
					try {
						SlideLibrary library = SlideLibrary.getInstance();
						NotificationSlideTemplate template = null;
						Dimension size = this.preferences.getPrimaryOrDefaultDeviceResolution();
						if (thumbnail.getFile() == SlideFile.NOT_STORED) {
							template = NotificationSlideTemplate.getDefaultTemplate(size.width, size.height);
						} else {
							try {
								template = library.getTemplate(thumbnail.getFile(), NotificationSlideTemplate.class);
							} catch (SlideLibraryException ex) {
								// just log the error
								LOGGER.error("Failed to switch to template: [" + thumbnail.getFile().getRelativePath() + "]", ex);
								return;
							}
						}
						this.verifyTemplateDimensions(template, size);
						this.slide = template.createSlide();
					} catch (NotInitializedException e1) {
						// ignore the error
					}
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
		SlideThumbnail[] thumbnails = this.getThumbnails();
		
		// update the list of templates
		SlideThumbnail selected = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		if (selected == null) {
			selected = this.getSelectedThumbnail(thumbnails);
		}
		this.cmbTemplates.removeAllItems();
		for (SlideThumbnail thumb : thumbnails) {
			this.cmbTemplates.addItem(thumb);
		}
		
		// set the selected one
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		} else {
			this.cmbTemplates.setSelectedIndex(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("send".equals(command)) {
			// get the text
			String text = this.txtText.getText();
			// check the text length
			if (text != null && !text.trim().isEmpty()) {
				// set the text on the slide
				this.slide.getTextComponent().setText(text);
				// copy the slide
				Slide slide = this.slide.copy();
				int delay = this.preferences.getTransitionDelay();
				// create the transition animators
				TransitionAnimator in = new TransitionAnimator(
						(Transition)this.cmbInTransition.getSelectedItem(),
						((Number)this.txtInTransition.getValue()).intValue(),
						delay,
						Easings.getEasingForId(this.nPreferences.getSendTransitionEasingId()));
				TransitionAnimator out = new TransitionAnimator(
						(Transition)this.cmbOutTransition.getSelectedItem(),
						((Number)this.txtOutTransition.getValue()).intValue(),
						delay,
						Easings.getEasingForId(this.nPreferences.getClearTransitionEasingId()));
				// get the wait duration
				int wait = ((Number)this.txtWaitPeriod.getValue()).intValue();
				// get the configuration
				PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.WINDOWED);
				// execute a new send event to the primary fullscreen display
				PresentationManager.getInstance().execute(new SendWaitClearEvent(configuration, in, slide, out, wait));
			}
		} else if ("clear".equals(command)) {
			// create the out transition animator
			TransitionAnimator animator = new TransitionAnimator(
					(Transition)this.cmbOutTransition.getSelectedItem(),
					((Number)this.txtOutTransition.getValue()).intValue(),
					this.preferences.getTransitionDelay(),
					Easings.getEasingForId(this.nPreferences.getClearTransitionEasingId()));
			// get the configuration
			PresentationEventConfiguration configuration = this.preferences.getPresentationEventConfiguration(PresentationWindowType.WINDOWED);
			// execute a new clear event to the primary fullscreen display
			PresentationManager.getInstance().execute(new ClearEvent(configuration, animator));
		}
	}
}
