package org.praisenter.preferences.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.PreferencesException;
import org.praisenter.resources.Messages;
import org.praisenter.threading.AbstractTask;
import org.praisenter.threading.TaskProgressDialog;
import org.praisenter.ui.BottomButtonPanel;

/**
 * Dialog used to set the preferences.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PreferencesDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -40624031567071023L;

	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(PreferencesDialog.class);
	
	/** The preferences listeners */
	private PreferencesListener[] listeners;
	
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
	 * @param listeners the preferences listeners
	 */
	protected PreferencesDialog(Window owner, PreferencesListener[] listeners) {
		super(owner, Messages.getString("dialog.preferences.title"), ModalityType.APPLICATION_MODAL);
		
		this.listeners = listeners;
		
		// create the settings panels
		this.pnlGeneralPreferences = new GeneralPreferencesPanel();
		this.pnlBiblePreferences = new BiblePreferencesPanel();
		this.pnlSongPreferences = new SongPreferencesPanel();
		this.pnlNotificationPreferences = new NotificationPreferencesPanel();
		this.pnlErrorReportingPreferences = new ErrorReportingSettingsPanel();
		
		// create the bottom buttons
		
		JButton btnSaveSettings = new JButton(Messages.getString("dialog.preferences.save"));
		btnSaveSettings.setToolTipText(Messages.getString("dialog.preferences.save.tooltip"));
		btnSaveSettings.setActionCommand("save");
		btnSaveSettings.addActionListener(this);
		
		JButton btnCancelSettings = new JButton(Messages.getString("dialog.preferences.cancel"));
		btnCancelSettings.setToolTipText(Messages.getString("dialog.preferences.cancel.tooltip"));
		btnCancelSettings.setActionCommand("cancel");
		btnCancelSettings.addActionListener(this);
		
		// create the bottom layout
		
		JPanel pnlBottom = new BottomButtonPanel();
		pnlBottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlBottom.add(btnSaveSettings);
		pnlBottom.add(btnCancelSettings);
		
		// create the tab container

		JTabbedPane pneTabs = new JTabbedPane();
		pneTabs.addTab(Messages.getString("dialog.preferences.general"), this.pnlGeneralPreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.bible"), this.pnlBiblePreferences);
		pneTabs.addTab(Messages.getString("dialog.preferences.song"), this.pnlSongPreferences);
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
			// create a task to perform the save
			AbstractTask task = new AbstractTask() {
				@Override
				public void run() {
					// apply the preferences
					pnlGeneralPreferences.applyPreferences();
					pnlBiblePreferences.applyPreferences();
					pnlSongPreferences.applyPreferences();
					pnlNotificationPreferences.applyPreferences();
					pnlErrorReportingPreferences.applyPreferences();
					try {
						// save them
						Preferences.getInstance().save();
						this.setSuccessful(true);
					} catch (PreferencesException e) {
						this.handleException(e);
					}
				}
			};
			
			// execute the save on another thread and show a progress bar
			TaskProgressDialog.show(this, Messages.getString("dialog.preferences.save.task.title"), task);
			if (task.isSuccessful()) {
				// notify of the preferences changes
				this.notifyPreferencesChanged();
				// show a success message
				JOptionPane.showMessageDialog(
						this, 
						Messages.getString("dialog.preferences.save.success.text"), 
						Messages.getString("dialog.preferences.save.success.title"), 
						JOptionPane.INFORMATION_MESSAGE);
				// close this dialog
				this.setVisible(false);
				this.dispose();
			} else {
				LOGGER.error(task.getException());
				ExceptionDialog.show(
						this, 
						Messages.getString("dialog.preferences.save.exception.title"), 
						Messages.getString("dialog.preferences.save.exception.text"), 
						task.getException());
			}
		} else if ("cancel".equals(command)) {
			// don't save the preferences and just close the dialog
			this.setVisible(false);
			this.dispose();
		}
	}
	
	/**
	 * Notifies all the listeners of the preferences saved event.
	 */
	protected void notifyPreferencesChanged() {
		for (PreferencesListener listener : this.listeners) {
			listener.preferencesChanged();
		}
	}
	
	/**
	 * Shows a new PreferencesDialog.
	 * @param owner the owner of the dialog
	 * @param listeners the array of {@link PreferencesListener}s
	 */
	public static final void show(Window owner, PreferencesListener... listeners) {
		PreferencesDialog dialog = new PreferencesDialog(owner, listeners);
		
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		dialog.dispose();
	}
}
