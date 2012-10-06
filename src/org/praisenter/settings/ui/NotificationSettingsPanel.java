package org.praisenter.settings.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.praisenter.display.NotificationDisplay;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.NotificationSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.transitions.ui.TransitionListCellRenderer;
import org.praisenter.ui.SelectTextFocusListener;

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

	// transitions
	
	/** The combo box of send transitions */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box for the send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of clear transitions */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box for the clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	// the notification slide
	
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
		
		// transitions
		boolean transitionsSupported = GeneralSettings.getInstance().getPrimaryOrDefaultDisplay().isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
		
		JLabel lblSendTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultSend"));
		this.cmbSendTransitions = new JComboBox<Transition>(Transitions.IN);
		this.cmbSendTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbSendTransitions.setSelectedItem(Transitions.getTransitionForId(settings.getDefaultSendTransition(), Transition.Type.IN));
		this.txtSendTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtSendTransitions.addFocusListener(new SelectTextFocusListener(this.txtSendTransitions));
		this.txtSendTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtSendTransitions.setValue(settings.getDefaultSendTransitionDuration());
		this.txtSendTransitions.setColumns(3);
		
		JLabel lblClearTransition = new JLabel(Messages.getString("panel.general.setup.transition.defaultClear"));
		this.cmbClearTransitions = new JComboBox<Transition>(Transitions.OUT);
		this.cmbClearTransitions.setRenderer(new TransitionListCellRenderer());
		this.cmbClearTransitions.setSelectedItem(Transitions.getTransitionForId(settings.getDefaultClearTransition(), Transition.Type.OUT));
		this.txtClearTransitions = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtClearTransitions.addFocusListener(new SelectTextFocusListener(this.txtClearTransitions));
		this.txtClearTransitions.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtClearTransitions.setValue(settings.getDefaultClearTransitionDuration());
		this.txtClearTransitions.setColumns(3);
		
		if (!transitionsSupported) {
			this.cmbSendTransitions.setEnabled(false);
			this.txtSendTransitions.setEnabled(false);
			this.cmbClearTransitions.setEnabled(false);
			this.txtClearTransitions.setEnabled(false);
		}
		
		JPanel pnlTransitions = new JPanel();
		pnlTransitions.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
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
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSendTransition)
						.addComponent(this.cmbSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtSendTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblClearTransition)
						.addComponent(this.cmbClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.txtClearTransitions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		// create the notification display panel
		this.pnlDisplay = new NotificationDisplaySettingsPanel(settings, displaySize);
		
		// setup the layout
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		
		layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblDefaultWaitPeriod)
				.addComponent(this.txtDefaultWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.notification.setup.general"), pnlGeneral);
		tabs.addTab(Messages.getString("panel.general.setup.transition.title"), pnlTransitions);
		
		JLabel lblMessage = new JLabel(Messages.getString("panel.notification.setup.message"));
		Font font = lblMessage.getFont();
		lblMessage.setFont(font.deriveFont(font.getSize2D() * 1.5f));
		lblMessage.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblMessage)
				.addComponent(tabs, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlDisplay, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblMessage)
				.addComponent(tabs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlDisplay));
	}

	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		// save this panel's settings
		this.settings.setDefaultWaitPeriod(((Number)this.txtDefaultWaitPeriod.getValue()).intValue());
		// transitions
		this.settings.setDefaultSendTransition(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		this.settings.setDefaultClearTransition(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
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
