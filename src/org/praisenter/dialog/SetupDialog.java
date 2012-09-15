package org.praisenter.dialog;

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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.praisenter.control.BottomButtonPanel;
import org.praisenter.panel.setup.BibleDisplaySetupPanel;
import org.praisenter.panel.setup.GeneralSetupPanel;
import org.praisenter.resources.Messages;
import org.praisenter.settings.BibleDisplaySettings;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.settings.SettingsListener;
import org.praisenter.utilities.WindowUtilities;

/**
 * Dialog used to set the settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SetupDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -3381114826460397256L;

	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(SetupDialog.class);
	
	/** The settings listeners */
	private List<SettingsListener> listeners;
	
	// panels
	
	/** The panel for the general settings */
	private GeneralSetupPanel pnlGeneralSettings;
	
	/** The panel for setting up the bible display */
	private BibleDisplaySetupPanel pnlBibleDisplaySettings;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the dialog
	 */
	protected SetupDialog(Window owner) {
		super(owner, Messages.getString("dialog.setup.title"), ModalityType.APPLICATION_MODAL);
		
		this.listeners = new ArrayList<SettingsListener>();
		
		// get the settings
		GeneralSettings gSettings = GeneralSettings.getInstance();
		BibleDisplaySettings bSettings = BibleDisplaySettings.getInstance();
		
		// for the setup panel we need to use the display size of the currently selected device
		// which we can get from the settings
		GraphicsDevice device = gSettings.getPrimaryOrDefaultDisplay();
		
		Dimension size = WindowUtilities.getDimension(device.getDisplayMode());
		
		// create the settings panels
		this.pnlGeneralSettings = new GeneralSetupPanel(gSettings);
		this.pnlBibleDisplaySettings = new BibleDisplaySetupPanel(bSettings, size);
		
		// set the panels to listen for property change events from the general panel
		this.pnlGeneralSettings.addPropertyChangeListener(GeneralSetupPanel.DISPLAY_PROPERTY, this.pnlBibleDisplaySettings);
		
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
		pneTabs.addTab(Messages.getString("dialog.setup.bible"), this.pnlBibleDisplaySettings);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlGeneralSettings, BorderLayout.PAGE_START);
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
			try {
				this.pnlGeneralSettings.saveSettings();
				this.pnlBibleDisplaySettings.saveSettings();
				// notify of the settings changes
				this.notifySettingsSaved();
				// show a success message
				JOptionPane.showMessageDialog(
						this, 
						Messages.getString("dialog.setup.save.success.text"), 
						Messages.getString("dialog.setup.save.success.title"), 
						JOptionPane.INFORMATION_MESSAGE);
			} catch (SettingsException ex) {
				ExceptionDialog.show(
						this, 
						Messages.getString("dialog.setup.save.exception.title"), 
						Messages.getString("dialog.setup.save.exception.text"), 
						ex);
				LOGGER.error(ex);
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
		SetupDialog dialog = new SetupDialog(owner);
		
		// add the listeners
		if (listeners != null) {
			for (SettingsListener listener : listeners) {
				dialog.addSettingsListener(listener);
			}
		}
		
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
}
