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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.BottomButtonPanel;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.common.threading.AbstractTask;

/**
 * Dialog used to set the preferences.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class PreferencesDialog extends JDialog implements ActionListener, PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = -40624031567071023L;

	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(PreferencesDialog.class);

	/** A property name for listening for slide/template library changes from subcomponents */
	public static final String PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED = "slideTemplateLibraryChanged";
	
	// data
	
	/** True if the preferences were updated */
	private boolean preferencesUpdated;
	
	// panels
	
	/** The panel for the general settings */
	private GeneralPreferencesPanel pnlGeneralPreferences;
	
	/** The panel for the bible settings */
	private BiblePreferencesPanel pnlBiblePreferences;

	/** The panel for the song settings */
	private SongPreferencesPanel pnlSongPreferences;

	/** The panel for the slide settings */
	private SlidePreferencesPanel pnlSlidePreferences;
	
	/** The panel for the notification settings */
	private NotificationPreferencesPanel pnlNotificationPreferences;
	
	/** The panel for error reporting settings */
	private ErrorReportingSettingsPanel pnlErrorReportingPreferences;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the dialog
	 */
	protected PreferencesDialog(Window owner) {
		super(owner, Messages.getString("dialog.preferences.title"), ModalityType.APPLICATION_MODAL);
		
		this.preferencesUpdated = false;
		
		// create the settings panels
		this.pnlGeneralPreferences = new GeneralPreferencesPanel();
		this.pnlBiblePreferences = new BiblePreferencesPanel();
		this.pnlSongPreferences = new SongPreferencesPanel();
		this.pnlSlidePreferences = new SlidePreferencesPanel();
		this.pnlNotificationPreferences = new NotificationPreferencesPanel();
		this.pnlErrorReportingPreferences = new ErrorReportingSettingsPanel();
		
		// add listeners
		this.pnlBiblePreferences.addPropertyChangeListener(PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, this);
		this.pnlSongPreferences.addPropertyChangeListener(PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, this);
		this.pnlNotificationPreferences.addPropertyChangeListener(PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED, this);
		
		// create the bottom buttons
		
		JButton btnSaveSettings = new JButton(Messages.getString("dialog.preferences.save"));
		btnSaveSettings.setToolTipText(Messages.getString("dialog.preferences.save.tooltip"));
		btnSaveSettings.setActionCommand("save");
		btnSaveSettings.addActionListener(this);
		
		JButton btnSaveAndCloseSettings = new JButton(Messages.getString("dialog.preferences.saveAndClose"));
		btnSaveAndCloseSettings.setToolTipText(Messages.getString("dialog.preferences.saveAndClose.tooltip"));
		btnSaveAndCloseSettings.setActionCommand("save-and-close");
		btnSaveAndCloseSettings.addActionListener(this);
		
		JButton btnCancelSettings = new JButton(Messages.getString("dialog.preferences.cancel"));
		btnCancelSettings.setActionCommand("cancel");
		btnCancelSettings.addActionListener(this);
		
		// create the bottom layout
		
		JPanel pnlBottom = new BottomButtonPanel();
		pnlBottom.setLayout(new FlowLayout(FlowLayout.TRAILING));
		pnlBottom.add(btnSaveSettings);
		pnlBottom.add(btnSaveAndCloseSettings);
		pnlBottom.add(btnCancelSettings);
		
		// create the tab container

		JTabbedPane pneTabs = new JTabbedPane();
		pneTabs.addTab(Messages.getString("dialog.preferences.general"), this.pnlGeneralPreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.bible"), this.pnlBiblePreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.song"), this.pnlSongPreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.slide"), this.pnlSlidePreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.notification"), this.pnlNotificationPreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.error"), this.pnlErrorReportingPreferences);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(pneTabs, BorderLayout.CENTER);
		container.add(pnlBottom, BorderLayout.PAGE_END);
		
		// size everything
		this.pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("save".equals(command)) {
			this.save();
		} else if ("save-and-close".equals(command)) {
			boolean success = this.save();
			if (success) {
				// close this dialog
				this.setVisible(false);
			}
		} else if ("cancel".equals(command)) {
			// don't save the preferences and just close the dialog
			this.setVisible(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (PROPERTY_SLIDE_TEMPLATE_LIBRARY_CHANGED.equals(e.getPropertyName())) {
			// update all the panels due to the library change
			this.pnlBiblePreferences.slideLibraryChanged();
			this.pnlSongPreferences.slideLibraryChanged();
			this.pnlNotificationPreferences.slideLibraryChanged();
			// update the preferencesChanged flag to yes (even if the modal is closed
			// and preferences were not changed we need to still return true so that
			// the main panel knows it needs to update the listings of templates)
			this.preferencesUpdated = true;
		}
	}
	
	/**
	 * Saves the preferences and returns true if successful.
	 * @return boolean
	 */
	private boolean save() {
		// create a task to perform the save
		AbstractTask task = new AbstractTask() {
			@Override
			public void run() {
				// apply the preferences
				pnlGeneralPreferences.applyPreferences();
				pnlBiblePreferences.applyPreferences();
				pnlSongPreferences.applyPreferences();
				pnlSlidePreferences.applyPreferences();
				pnlNotificationPreferences.applyPreferences();
				pnlErrorReportingPreferences.applyPreferences();
				try {
					// save them
					Preferences.getInstance().save();
					this.setSuccessful(true);
				} catch (Exception e) {
					this.handleException(e);
				}
			}
		};
		
		// execute the save on another thread and show a progress bar
		TaskProgressDialog.show(this, Messages.getString("dialog.preferences.save.task.title"), task);
		if (task.isSuccessful()) {
			this.preferencesUpdated = true;
			// show a success message
			JOptionPane.showMessageDialog(
					this, 
					Messages.getString("dialog.preferences.save.success.text"), 
					Messages.getString("dialog.preferences.save.success.title"), 
					JOptionPane.INFORMATION_MESSAGE);
			return true;
		} else {
			LOGGER.error(task.getException());
			ExceptionDialog.show(
					this, 
					Messages.getString("dialog.preferences.save.exception.title"), 
					Messages.getString("dialog.preferences.save.exception.text"), 
					task.getException());
		}
		
		return false;
	}
	
	/**
	 * Shows a new PreferencesDialog and returns true if the preferences were updated.
	 * @param owner the owner of the dialog
	 * @return boolean
	 */
	public static final boolean show(Window owner) {
		PreferencesDialog dialog = new PreferencesDialog(owner);
		
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		dialog.dispose();
		
		return dialog.preferencesUpdated;
	}
}
