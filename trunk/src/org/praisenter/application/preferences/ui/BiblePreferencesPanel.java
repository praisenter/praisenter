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
package org.praisenter.application.preferences.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;
import org.praisenter.animation.transitions.Transitions;
import org.praisenter.application.bible.ui.BibleListCellRenderer;
import org.praisenter.application.preferences.BiblePreferences;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.slide.ui.EasingListCellRenderer;
import org.praisenter.application.slide.ui.SlideLibraryDialog;
import org.praisenter.application.slide.ui.SlideLibraryListener;
import org.praisenter.application.slide.ui.SlideThumbnailComboBoxRenderer;
import org.praisenter.application.slide.ui.TransitionListCellRenderer;
import org.praisenter.application.ui.OpaquePanel;
import org.praisenter.application.ui.SelectTextFocusListener;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.ComponentUtilities;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.SlideFile;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideThumbnail;

/**
 * Panel used to set the {@link BiblePreferences}.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class BiblePreferencesPanel extends OpaquePanel implements PreferencesEditor, ActionListener, SlideLibraryListener {
	/** The verison id */
	private static final long serialVersionUID = 460972285830298448L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BiblePreferencesPanel.class);
	
	/** The available bibles */
	private JComboBox<Bible> cmbBiblesPrimary;
	
	/** The secondary bible listing */
	private JComboBox<Bible> cmbBiblesSecondary;
	
	/** The checkbox to use the secondary bible */
	private JCheckBox chkUseSecondaryBible;
	
	/** The checkbox to include/exclude the apocrypha */
	private JCheckBox chkIncludeApocrypha;
	
	// template
	
	/** The template combo box */
	private JComboBox<SlideThumbnail> cmbTemplates;
	
	// transitions
	
	/** The combo box of send transitions */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box for the send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of send easings */
	private JComboBox<Easing> cmbSendEasings;
	
	/** The combo box of clear transitions */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box for the clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	/** The combo box of clear easings */
	private JComboBox<Easing> cmbClearEasings;
	
	/**
	 * Default constructor.
	 */
	public BiblePreferencesPanel() {
		Preferences preferences = Preferences.getInstance();
		BiblePreferences bPreferences = preferences.getBiblePreferences();
		
		// general bible settings
		JLabel lblDefaultBible = new JLabel(Messages.getString("panel.bible.preferences.defaultBible"));
		Bible[] bibles = null;
		try {
			bibles = Bibles.getBibles().toArray(new Bible[0]);
		} catch (DataException e) {
			LOGGER.error("Bibles could not be retrieved:", e);
		}
		// check for null
		if (bibles == null) {
			this.cmbBiblesPrimary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesPrimary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesPrimary.setRenderer(new BibleListCellRenderer());
		// get the default value
		Bible bible = null;
		try {
			bible = Bibles.getBible(bPreferences.getPrimaryTranslationId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesPrimary.setSelectedItem(bible);
		}
		JLabel lblIncludeApocrypha = new JLabel(Messages.getString("panel.bible.preferences.includeApocrypha"));
		this.chkIncludeApocrypha = new JCheckBox();
		this.chkIncludeApocrypha.setToolTipText(Messages.getString("panel.bible.preferences.includeApocrypha.tooltip"));
		this.chkIncludeApocrypha.setSelected(bPreferences.isApocryphaIncluded());
		
		// the secondary bible
		JLabel lblDefaultSecondaryBible = new JLabel(Messages.getString("panel.bible.preferences.defaultSecondaryBible"));
		if (bibles == null) {
			this.cmbBiblesSecondary = new JComboBox<Bible>();
		} else {
			this.cmbBiblesSecondary = new JComboBox<Bible>(bibles);
		}
		this.cmbBiblesSecondary.setRenderer(new BibleListCellRenderer());
		// get the default value
		bible = null;
		try {
			bible = Bibles.getBible(bPreferences.getSecondaryTranslationId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBiblesSecondary.setSelectedItem(bible);
		}
		
		this.chkUseSecondaryBible = new JCheckBox(Messages.getString("panel.bible.preferences.useSecondaryBible"));
		this.chkUseSecondaryBible.setToolTipText(Messages.getString("panel.bible.preferences.useSecondaryBible.tooltip"));
		this.chkUseSecondaryBible.setSelected(bPreferences.isSecondaryTranslationEnabled());
		
		// template
		
		JLabel lblTemplate = new JLabel(Messages.getString("panel.preferences.template"));
		SlideThumbnail[] thumbs = this.getSlideThumnails();
		// find the selected template
		SlideThumbnail selected = null;
		for (SlideThumbnail thumb : thumbs) {
			if (thumb.getFile() == SlideFile.NOT_STORED) {
				if (bPreferences.getTemplate() == null) {
					selected = thumb;
					break;
				}
			} else if (thumb.getFile().getRelativePath().equals(bPreferences.getTemplate())) {
				selected = thumb;
				break;
			}
		}
		Arrays.sort(thumbs);
		this.cmbTemplates = new JComboBox<SlideThumbnail>(thumbs);
		if (selected != null) {
			this.cmbTemplates.setSelectedItem(selected);
		}
		this.cmbTemplates.setToolTipText(Messages.getString("panel.preferences.template.tooltip"));
		this.cmbTemplates.setRenderer(new SlideThumbnailComboBoxRenderer());
		JButton btnAddTemplate = new JButton(Messages.getString("template.manage"));
		btnAddTemplate.setActionCommand("manageTemplates");
		btnAddTemplate.setToolTipText(Messages.getString("template.manage.tooltip"));
		btnAddTemplate.addActionListener(this);
		
		// transitions
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.preferences.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer(this.cmbSendTransitions));
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getSendTransitionId(), TransitionType.IN));
		this.txtSendTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(bPreferences.getSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		this.cmbSendEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbSendEasings.setRenderer(new EasingListCellRenderer());
		this.cmbSendEasings.setSelectedItem(Easings.getEasingForId(bPreferences.getSendTransitionEasingId()));
		this.cmbSendEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.preferences.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer(this.cmbClearTransitions));
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(bPreferences.getClearTransitionId(), TransitionType.OUT));
		this.txtClearTransitions = new JFormattedTextField(new DecimalFormat("0"));
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(bPreferences.getClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		this.cmbClearEasings = new JComboBox<Easing>(Easings.EASINGS);
		this.cmbClearEasings.setRenderer(new EasingListCellRenderer());
		this.cmbClearEasings.setSelectedItem(Easings.getEasingForId(bPreferences.getClearTransitionEasingId()));
		this.cmbClearEasings.setToolTipText(Messages.getString("easing.tooltip"));
		
		JPanel pnlTransitions = new OpaquePanel();
		GroupLayout layout = new GroupLayout(pnlTransitions);
		pnlTransitions.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblSendTransition)
						.addComponent(lblClearTransition))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbSendEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSendTransition)
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbSendEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblClearTransition)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbClearEasings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		JPanel pnlTemplate = new OpaquePanel();
		layout = new GroupLayout(pnlTemplate);
		pnlTemplate.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblTemplate)
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnAddTemplate));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblTemplate)
				.addComponent(this.cmbTemplates, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnAddTemplate));
		
		// setup the layout
		JPanel pnlGeneral = new OpaquePanel();
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblDefaultBible)
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(lblIncludeApocrypha))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkIncludeApocrypha))
				.addComponent(this.chkUseSecondaryBible));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDefaultBible)
						.addComponent(this.cmbBiblesPrimary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkUseSecondaryBible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDefaultSecondaryBible)
						.addComponent(this.cmbBiblesSecondary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblIncludeApocrypha)
						.addComponent(this.chkIncludeApocrypha)));
		
		// size the labels appropriately
		ComponentUtilities.setMinimumSize(lblDefaultBible, lblDefaultSecondaryBible, lblIncludeApocrypha, lblClearTransition, lblSendTransition, lblTemplate);
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JSeparator sep1 = new JSeparator(JSeparator.HORIZONTAL);
		JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1)
				.addComponent(pnlTemplate)
				.addComponent(sep2)
				.addComponent(pnlTransitions));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlTemplate)
				.addComponent(sep2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlTransitions));
	}
	
	/**
	 * Returns the slide thumbnail list for all the templates.
	 * @return {@link SlideThumbnail}[]
	 */
	private SlideThumbnail[] getSlideThumnails() {
		// we need to refresh the templates listing
		List<SlideThumbnail> thumbs = null;
		try {
			thumbs = SlideLibrary.getInstance().getThumbnails(BibleSlideTemplate.class);
		} catch (NotInitializedException ex) {
			thumbs = new ArrayList<SlideThumbnail>();
		}
		
		// add in the default template
		Preferences preferences = Preferences.getInstance();
		Dimension displaySize = preferences.getPrimaryOrDefaultDeviceResolution();
		BibleSlideTemplate template = BibleSlideTemplate.getDefaultTemplate(displaySize.width, displaySize.height);
		BufferedImage image = template.getThumbnail(SlideLibrary.THUMBNAIL_SIZE);
		SlideThumbnail temp = new SlideThumbnail(SlideFile.NOT_STORED, template.getName(), image);
		thumbs.add(temp);
		
		Collections.sort(thumbs);
		
		return thumbs.toArray(new SlideThumbnail[0]);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if ("manageTemplates".equals(command)) {
			boolean libraryUpdated = SlideLibraryDialog.show(WindowUtilities.getParentWindow(this), BibleSlideTemplate.class);
			if (libraryUpdated) {
				firePropertyChange(PreferencesDialog.PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, null, null);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.application.slide.ui.SlideLibraryListener#slideLibraryChanged()
	 */
	@Override
	public void slideLibraryChanged() {
		SlideThumbnail[] thumbs = this.getSlideThumnails();
		// save the currently selected template
		SlideThumbnail selected = (SlideThumbnail)this.cmbTemplates.getSelectedItem();
		this.cmbTemplates.removeAllItems();
		for (SlideThumbnail thumb : thumbs) {
			this.cmbTemplates.addItem(thumb);
		}
		this.cmbTemplates.setSelectedItem(selected);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesEditor#applyPreferences()
	 */
	@Override
	public void applyPreferences() {
		Preferences preferences = Preferences.getInstance();
		BiblePreferences bPreferences = preferences.getBiblePreferences();
		
		// save this panel's settings
		Bible primary = (Bible)this.cmbBiblesPrimary.getSelectedItem();
		if (primary == null) {
			primary = (Bible)this.cmbBiblesPrimary.getItemAt(0);
		}
		Bible secondary = (Bible)this.cmbBiblesSecondary.getSelectedItem();
		if (secondary == null) {
			secondary = (Bible)this.cmbBiblesSecondary.getItemAt(0);
		}
		
		if (primary != null) bPreferences.setPrimaryTranslationId(primary.getId());
		if (secondary != null) bPreferences.setSecondaryTranslationId(secondary.getId());
		bPreferences.setSecondaryTranslationEnabled(this.chkUseSecondaryBible.isSelected());
		bPreferences.setApocryphaIncluded(this.chkIncludeApocrypha.isSelected());
		// template
		SlideThumbnail thumbnail = ((SlideThumbnail)this.cmbTemplates.getSelectedItem());
		// check for the default template
		if (thumbnail.getFile() != SlideFile.NOT_STORED) {
			bPreferences.setTemplate(thumbnail.getFile().getRelativePath());
		} else {
			bPreferences.setTemplate(null);
		}
		// transitions
		bPreferences.setSendTransitionId(((Transition)this.cmbSendTransitions.getSelectedItem()).getId());
		bPreferences.setSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		bPreferences.setSendTransitionEasingId(((Easing)this.cmbSendEasings.getSelectedItem()).getId());
		bPreferences.setClearTransitionId(((Transition)this.cmbClearTransitions.getSelectedItem()).getId());
		bPreferences.setClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
		bPreferences.setClearTransitionEasingId(((Easing)this.cmbClearEasings.getSelectedItem()).getId());
	}
}
