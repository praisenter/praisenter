package org.praisenter.preferences.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.resources.Messages;
import org.praisenter.threading.AbstractTask;
import org.praisenter.threading.TaskProgressDialog;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog used to set the settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PreferencesDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -40624031567071023L;

	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(PreferencesDialog.class);
	
	/** The settings listeners */
//	private List<SettingsListener> listeners;
	
	// panels
	
	/** The panel for the general settings */
	private GeneralPreferencesPanel pnlGeneralPreferences;
	
	/** The panel for the bible settings */
	private BiblePreferencesPanel pnlBiblePreferences;

	/** The panel for the song settings */
	private SongPreferencesPanel pnlSongPreferences;
	
	/** The panel for the notification settings */
	private NotificationPreferencesPanel pnlNotificationPreferences;
	
	/** The panel for error reporting settings */
	private ErrorReportingSettingsPanel pnlErrorReportingPreferences;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the dialog
	 */
	protected PreferencesDialog(Window owner) {
		super(owner, Messages.getString("dialog.setup.title"), ModalityType.APPLICATION_MODAL);
		
//		this.listeners = new ArrayList<SettingsListener>();
		
		// create the settings panels
		this.pnlGeneralPreferences = new GeneralPreferencesPanel();
		this.pnlBiblePreferences = new BiblePreferencesPanel();
		this.pnlSongPreferences = new SongPreferencesPanel();
		this.pnlNotificationPreferences = new NotificationPreferencesPanel();
		this.pnlErrorReportingPreferences = new ErrorReportingSettingsPanel();
		
		this.pnlGeneralPreferences.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlBiblePreferences.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlSongPreferences.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlNotificationPreferences.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlErrorReportingPreferences.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		
		// create the bottom buttons
		
		JButton btnSaveSettings = new JButton(Messages.getString("dialog.setup.save"));
		btnSaveSettings.setToolTipText(Messages.getString("dialog.setup.save.tooltip"));
		btnSaveSettings.setActionCommand("save");
		btnSaveSettings.addActionListener(this);
		
		JButton btnCancelSettings = new JButton(Messages.getString("dialog.setup.cancel"));
		btnCancelSettings.setToolTipText(Messages.getString("dialog.setup.cancel.tooltip"));
		btnCancelSettings.setActionCommand("cancel");
		btnCancelSettings.addActionListener(this);
		
		// create the bottom layout
		
		JPanel pnlBottom = new BottomButtonPanel();
		pnlBottom.setLayout(new FlowLayout());
		pnlBottom.add(btnSaveSettings);
		pnlBottom.add(btnCancelSettings);
		
		// create the tab container

		JTabbedPane pneTabs = new JTabbedPane();
		pneTabs.addTab(Messages.getString("dialog.setup.general"), this.pnlGeneralPreferences);
		pneTabs.addTab(Messages.getString("dialog.setup.bible"), this.pnlBiblePreferences);
		pneTabs.addTab(Messages.getString("dialog.setup.song"), this.pnlSongPreferences);
		pneTabs.addTab(Messages.getString("dialog.setup.notification"), this.pnlNotificationPreferences);
		pneTabs.addTab(Messages.getString("dialog.setup.error"), this.pnlErrorReportingPreferences);
		
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
			// create a task to perform the save
			AbstractTask task = new AbstractTask() {
				@Override
				public void run() {
//					try {
//						pnlGeneralSettings.saveSettings();
//						pnlBibleSettings.saveSettings();
//						pnlSongSettings.saveSettings();
//						pnlNotificationSettings.saveSettings();
//						pnlErrorReportingSettings.saveSettings();
//						this.setSuccessful(true);
//					} catch (SettingsException ex) {
//						this.handleException(ex);
//					}
				}
			};
			
			// execute the save on another thread and show a progress bar
			TaskProgressDialog.show(this, Messages.getString("dialog.setup.save.task.title"), task);
			if (task.isSuccessful()) {
				// notify of the settings changes
//				this.notifySettingsSaved();
				// show a success message
				JOptionPane.showMessageDialog(
						this, 
						Messages.getString("dialog.setup.save.success.text"), 
						Messages.getString("dialog.setup.save.success.title"), 
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				LOGGER.error(task.getException());
				ExceptionDialog.show(
						this, 
						Messages.getString("dialog.setup.save.exception.title"), 
						Messages.getString("dialog.setup.save.exception.text"), 
						task.getException());
			}
		} else if ("cancel".equals(command)) {
			// don't save the settings and just close the dialog
			this.setVisible(false);
			this.dispose();
		}
	}
	
//	/**
//	 * Notifies all the listeners of the settings saved event.
//	 */
//	protected void notifySettingsSaved() {
//		for (SettingsListener listener : this.listeners) {
//			listener.settingsSaved();
//		}
//	}
//	
//	/**
//	 * Adds the given listener to this settings instance.
//	 * @param listener the listener
//	 */
//	public void addSettingsListener(SettingsListener listener) {
//		this.listeners.add(listener);
//	}
//	
//	/**
//	 * Removes the given listener from this settings instance.
//	 * @param listener the listener to remove
//	 */
//	public void removeSettingsListener(SettingsListener listener) {
//		this.listeners.remove(listener);
//	}
	
	/**
	 * Shows a new SetupDialog.
	 * @param owner the owner of the dialog
	 * @param listeners the array of {@link SettingsListener}s
	 */
	public static final void show(Window owner/*, SettingsListener... listeners*/) {
		PreferencesDialog dialog = new PreferencesDialog(owner);
		
		// add the listeners
//		if (listeners != null) {
//			for (SettingsListener listener : listeners) {
//				dialog.addSettingsListener(listener);
//			}
//		}
		
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		dialog.dispose();
	}
}
