package org.praisenter.notification.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.praisenter.display.DisplayFactory;
import org.praisenter.display.NotificationDisplay;
import org.praisenter.display.ui.DisplayWindows;
import org.praisenter.display.ui.NotificationDisplayWindow;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.GeneralSettings;
import org.praisenter.preferences.NotificationPreferences;
import org.praisenter.preferences.SettingsListener;
import org.praisenter.resources.Messages;
import org.praisenter.slide.ui.TransitionListCellRenderer;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;
import org.praisenter.transitions.Transitions;
import org.praisenter.ui.SelectTextFocusListener;
import org.praisenter.ui.WaterMark;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to send notifications.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationPanel extends JPanel implements ActionListener, SettingsListener {
	/** The version id */
	private static final long serialVersionUID = 20837022721408081L;
	
	// display
	
	/** The notification display */
	protected NotificationDisplay display;
	
	// controls
	
	/** The notification text */
	protected JTextField txtText;
	
	/** The notification wait period */
	protected JFormattedTextField txtWaitPeriod;
	
	/** The in transition */
	protected JComboBox<Transition> cmbInTransition;
	
	/** The in transition duration */
	protected JFormattedTextField txtInTransition;
	
	/** The out transition */
	protected JComboBox<Transition> cmbOutTransition;
	
	/** The out transition duration */
	protected JFormattedTextField txtOutTransition;
	
	/** The send button */
	protected JButton btnSend;
	
	/** The manual clear button */
	protected JButton btnClear;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("serial")
	public NotificationPanel() {
		GeneralSettings gSettings = GeneralSettings.getInstance();
		NotificationPreferences nSettings = NotificationPreferences.getInstance();
		
		// create the display
		Dimension displaySize = gSettings.getPrimaryDisplaySize();
		this.display = DisplayFactory.getDisplay(nSettings, displaySize, "");
		
		this.txtText = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// paint a watermark over the text box
				WaterMark.paintTextWaterMark(g, this, Messages.getString("panel.notification.text.watermark"));
			}
		};
		this.txtText.setColumns(20);
		this.txtText.addFocusListener(new SelectTextFocusListener(this.txtText));
		
		this.txtWaitPeriod = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtWaitPeriod.setToolTipText(Messages.getString("panel.notification.wait.tooltip"));
		this.txtWaitPeriod.setValue(nSettings.getDefaultWaitPeriod());
		this.txtWaitPeriod.setColumns(5);
		this.txtWaitPeriod.addFocusListener(new SelectTextFocusListener(this.txtWaitPeriod));
		
		// setup the transition lists
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(gSettings.getPrimaryOrDefaultDisplay());
		
		this.cmbInTransition = new JComboBox<Transition>(Transitions.IN);
		this.cmbInTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbInTransition.setSelectedItem(Transitions.getTransitionForId(nSettings.getDefaultSendTransition(), Transition.Type.IN));
		this.cmbInTransition.setToolTipText(Messages.getString("panel.notification.send.inTransition"));
		this.txtInTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtInTransition.addFocusListener(new SelectTextFocusListener(this.txtInTransition));
		this.txtInTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtInTransition.setValue(nSettings.getDefaultSendTransitionDuration());
		this.txtInTransition.setColumns(3);
		
		this.cmbOutTransition = new JComboBox<Transition>(Transitions.OUT);
		this.cmbOutTransition.setRenderer(new TransitionListCellRenderer());
		this.cmbOutTransition.setSelectedItem(Transitions.getTransitionForId(nSettings.getDefaultClearTransition(), Transition.Type.OUT));
		this.cmbOutTransition.setToolTipText(Messages.getString("panel.notification.send.outTransition"));
		this.txtOutTransition = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.txtOutTransition.addFocusListener(new SelectTextFocusListener(this.txtOutTransition));
		this.txtOutTransition.setToolTipText(Messages.getString("transition.duration.tooltip"));
		this.txtOutTransition.setValue(nSettings.getDefaultClearTransitionDuration());
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
				.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend)
				.addComponent(this.btnClear));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.txtText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtWaitPeriod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtInTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.cmbOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.txtOutTransition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnSend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnClear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.settings.SettingsListener#settingsSaved()
	 */
	@Override
	public void settingsSaved() {
		GeneralSettings gSettings = GeneralSettings.getInstance();
		NotificationPreferences nSettings = NotificationPreferences.getInstance();
		
		Dimension displaySize = gSettings.getPrimaryDisplaySize();
		this.display = DisplayFactory.getDisplay(nSettings, displaySize, "");
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		NotificationPreferences settings = NotificationPreferences.getInstance();
		if ("send".equals(event.getActionCommand())) {
			// get the text
			String text = this.txtText.getText();
			// check the text length
			if (text != null && !text.trim().isEmpty()) {
				// set the text on the display
				this.display.getTextComponent().setText(text);
				// create the transition animators
				TransitionAnimator in = new TransitionAnimator(
						(Transition)this.cmbInTransition.getSelectedItem(),
						((Number)this.txtInTransition.getValue()).intValue(),
						Easings.getEasingForId(settings.getSendEasing()));
				TransitionAnimator out = new TransitionAnimator(
						(Transition)this.cmbOutTransition.getSelectedItem(),
						((Number)this.txtOutTransition.getValue()).intValue(),
						Easings.getEasingForId(settings.getClearEasing()));
				// get the wait duration
				int wait = ((Number)this.txtWaitPeriod.getValue()).intValue();
				// send the notification
				NotificationDisplayWindow window = DisplayWindows.getPrimaryNotificationWindow();
				if (window != null) {
					window.send(this.display, in, out, wait);
				} else {
					// the device is no longer available
					JOptionPane.showMessageDialog(
							WindowUtilities.getParentWindow(this), 
							Messages.getString("dialog.device.primary.missing.text"), 
							Messages.getString("dialog.device.primary.missing.title"), 
							JOptionPane.WARNING_MESSAGE);
				}
			}
		} else if ("clear".equals(event.getActionCommand())) {
			// create the out transition animator
			TransitionAnimator animator = new TransitionAnimator(
					(Transition)this.cmbOutTransition.getSelectedItem(),
					((Number)this.txtOutTransition.getValue()).intValue(),
					Easings.getEasingForId(settings.getClearEasing()));
			// get the notification display window
			NotificationDisplayWindow window = DisplayWindows.getPrimaryNotificationWindow();
			if (window != null) {
				// clear it
				window.clear(animator);
			}
		}
	}
}
