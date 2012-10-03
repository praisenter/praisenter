package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.praisenter.display.NotificationDisplay;
import org.praisenter.resources.Messages;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SettingsException;

/**
 * Panel used to set the {@link NotificationSettings}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationSettingsPanel extends JPanel implements SettingsPanel {
	/** The verison id */
	private static final long serialVersionUID = 460972285830298448L;

	/** The settings being configured */
	protected NotificationSettings settings;

	/** The default wait period text box */
	protected JFormattedTextField txtDefaultWaitPeriod;
	
	/** The panel used to setup the {@link NotificationDisplay} */
	protected NotificationDisplaySettingsPanel pnlDisplay;
	
	/**
	 * Minimal constructor.
	 * @param settings the {@link NotificationSettings}
	 * @param displaySize the target display size
	 */
	public NotificationSettingsPanel(NotificationSettings settings, Dimension displaySize) {
		this.settings = settings;
		// general notification settings
		JLabel lblDefaultWaitPeriod = new JLabel(Messages.getString("panel.notification.setup.defaultWaitPeriod"));
		lblDefaultWaitPeriod.setToolTipText(Messages.getString("panel.notification.setup.defaultWaitPeriod.tooltip"));
		this.txtDefaultWaitPeriod = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtDefaultWaitPeriod.setValue(settings.getDefaultWaitPeriod());
		this.txtDefaultWaitPeriod.setColumns(6);
		
		// create the notification display panel
		this.pnlDisplay = new NotificationDisplaySettingsPanel(settings, displaySize);
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.notification.setup.general")));
		
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlDisplay, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(this.pnlDisplay));
	}

	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		// save this panel's settings
		this.settings.setDefaultWaitPeriod(((Number)this.txtDefaultWaitPeriod.getValue()).intValue());
		// save the display panel's settings
		this.pnlDisplay.saveSettings();
		// save the settings to persistent store
		this.settings.save();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// the display panel cares about these events
		this.pnlDisplay.propertyChange(event);
	}
}
