package org.praisenter.settings.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleSettings;
import org.praisenter.settings.ErrorReportingSettings;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.SettingsListener;
import org.praisenter.tasks.AbstractTask;
import org.praisenter.tasks.TaskProgressDialog;
import org.praisenter.ui.BottomButtonPanel;
import org.praisenter.utilities.WindowUtilities;

/**
 * Dialog used to set the settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SettingsDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -3381114826460397256L;

	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class);
	
	/** The settings listeners */
	private List<SettingsListener> listeners;
	
	// panels
	
	/** The panel for the general settings */
	private GeneralSettingsPanel pnlGeneralSettings;
	
	/** The panel for the bible settings */
	private BibleSettingsPanel pnlBibleSettings;
	
	/** The panel for the notification settings */
	private NotificationSettingsPanel pnlNotificationSettings;
	
	/** The panel for error reporting settings */
	private ErrorReportingSettingsPanel pnlErrorReportingSettings;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the dialog
	 */
	protected SettingsDialog(Window owner) {
		super(owner, Messages.getString("dialog.setup.title"), ModalityType.APPLICATION_MODAL);
		
		this.listeners = new ArrayList<SettingsListener>();
		
		// get the settings
		GeneralSettings gSettings = GeneralSettings.getInstance();
		BibleSettings bSettings = BibleSettings.getInstance();
		NotificationSettings nSettings = NotificationSettings.getInstance();
		ErrorReportingSettings eSettings = ErrorReportingSettings.getInstance();
		
		// for the setup panel we need to use the display size of the currently selected device
		// which we can get from the settings
		GraphicsDevice device = gSettings.getPrimaryOrDefaultDisplay();
		
		Dimension size = WindowUtilities.getDimension(device.getDisplayMode());
		
		// create the settings panels
		this.pnlGeneralSettings = new GeneralSettingsPanel(gSettings);
		this.pnlBibleSettings = new BibleSettingsPanel(bSettings, size);
		this.pnlNotificationSettings = new NotificationSettingsPanel(nSettings, size);
		this.pnlErrorReportingSettings = new ErrorReportingSettingsPanel(eSettings);
		
		// set the panels to listen for property change events from the general panel
		// since the general panel contains the setup for the displays
		this.pnlGeneralSettings.addPropertyChangeListener(GeneralSettingsPanel.PRIMARY_DISPLAY_PROPERTY, this.pnlBibleSettings);
		this.pnlNotificationSettings.addPropertyChangeListener(GeneralSettingsPanel.PRIMARY_DISPLAY_PROPERTY, this.pnlNotificationSettings);
		
		this.pnlGeneralSettings.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlBibleSettings.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlNotificationSettings.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		this.pnlErrorReportingSettings.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		
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
		pneTabs.addTab(Messages.getString("dialog.setup.general"), this.pnlGeneralSettings);
		pneTabs.addTab(Messages.getString("dialog.setup.bible"), this.pnlBibleSettings);
		pneTabs.addTab(Messages.getString("dialog.setup.notification"), this.pnlNotificationSettings);
		pneTabs.addTab(Messages.getString("dialog.setup.error"), this.pnlErrorReportingSettings);
		
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
					try {
						pnlGeneralSettings.saveSettings();
						pnlBibleSettings.saveSettings();
						pnlNotificationSettings.saveSettings();
						pnlErrorReportingSettings.saveSettings();
						this.setSuccessful(true);
					} catch (SettingsException ex) {
						this.handleException(ex);
					}
				}
			};
			
			// execute the save on another thread and show a progress bar
			TaskProgressDialog.show(this, Messages.getString("dialog.setup.save.task.title"), task);
			if (task.isSuccessful()) {
				// notify of the settings changes
				this.notifySettingsSaved();
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
	
	/**
	 * Notifies all the listeners of the settings saved event.
	 */
	protected void notifySettingsSaved() {
		for (SettingsListener listener : this.listeners) {
			listener.settingsSaved();
		}
	}
	
	/**
	 * Adds the given listener to this settings instance.
	 * @param listener the listener
	 */
	public void addSettingsListener(SettingsListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Removes the given listener from this settings instance.
	 * @param listener the listener to remove
	 */
	public void removeSettingsListener(SettingsListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * Shows a new SetupDialog.
	 * @param owner the owner of the dialog
	 * @param listeners the array of {@link SettingsListener}s
	 */
	public static final void show(Window owner, SettingsListener... listeners) {
		SettingsDialog dialog = new SettingsDialog(owner);
		
		// add the listeners
		if (listeners != null) {
			for (SettingsListener listener : listeners) {
				dialog.addSettingsListener(listener);
			}
		}
		
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		dialog.dispose();
	}
}
