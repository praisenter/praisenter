package org.praisenter.panel.setup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.praisenter.control.SelectTextFocusListener;
import org.praisenter.icons.Icons;
import org.praisenter.panel.TransitionListCellRenderer;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transitions;
import org.praisenter.utilities.DelayCloseWindowTask;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to setup the general settings for the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class GeneralSetupPanel extends JPanel implements SetupPanel, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = 8936112479746612291L;
	
	/** Property name of the display drop down */
	public static final String DISPLAY_PROPERTY = "gspDisplay";
	
	/** The list of devices (not static so that it will pick up new devices) */
	private GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
	
	/** The settings being modified */
	private GeneralSettings settings;
	
	// screens
	
	/** The available devices */
	private JComboBox<GraphicsDevice> cmbDevices;
	
	/** The label for the set display not found */
	private JLabel lblDisplayNotFound;
	
	/** The label for translucency support */
	private JLabel lblTranslucency;
	
	// transitions
	
	/** The combo box of send transitions */
	private JComboBox<Transition> cmbSendTransitions;
	
	/** The text box for the send transition duration */
	private JFormattedTextField txtSendTransitions;
	
	/** The combo box of clear transitions */
	private JComboBox<Transition> cmbClearTransitions;
	
	/** The text box for the clear transition duration */
	private JFormattedTextField txtClearTransitions;
	
	/**
	 * Minimal constructor.
	 * @param settings the settings
	 */
	public GeneralSetupPanel(GeneralSettings settings) {
		this.settings = settings;
		
		GraphicsDevice primary = settings.getPrimaryOrDefaultDisplay();
		
		this.lblDisplayNotFound = new JLabel();
		this.lblDisplayNotFound.setVisible(false);
		if (settings.getPrimaryDisplay() == null) {
			// show the primary display message
			this.lblDisplayNotFound.setVerticalTextPosition(SwingConstants.TOP);
			this.lblDisplayNotFound.setText(Messages.getString("panel.general.setup.display.missing.warning"));
			this.lblDisplayNotFound.setIcon(Icons.WARNING);
			this.lblDisplayNotFound.setVisible(true);
		}
		
		JLabel lblPrimaryDisplay = new JLabel(Messages.getString("panel.general.setup.display.primaryDisplay"));
		lblPrimaryDisplay.setToolTipText(Messages.getString("panel.general.setup.display.primaryDisplay.tooltip"));
		
		// drop down for the primary display device
		this.cmbDevices = new JComboBox<GraphicsDevice>(this.devices);
		this.cmbDevices.setRenderer(new DeviceRenderer());
		if (primary != null) {
			this.cmbDevices.setSelectedItem(primary);
		}
		this.cmbDevices.addItemListener(this);
		
		// button for the identify 
		JButton btnIdentify = new JButton(Messages.getString("panel.general.setup.display.identify"));
		btnIdentify.setToolTipText(Messages.getString("panel.general.setup.display.identify.tooltip"));
		btnIdentify.addActionListener(this);
		btnIdentify.setActionCommand("identify");
		
		// translucency label
		this.lblTranslucency = new JLabel();
		this.lblTranslucency.setVisible(false);
		// check if the primary display is translucent
		if (!primary.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.lblTranslucency.setVerticalTextPosition(SwingConstants.TOP);
			this.lblTranslucency.setText(Messages.getString("panel.general.setup.display.translucent.warning"));
			this.lblTranslucency.setIcon(Icons.WARNING);
			this.lblTranslucency.setVisible(true);
		}
		
		// create the layout
		JPanel pnlDisplays = new JPanel();
		pnlDisplays.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.general.setup.display.title")));
		GroupLayout layout = new GroupLayout(pnlDisplays);
		pnlDisplays.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createSequentialGroup()
						.addComponent(lblPrimaryDisplay)
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnIdentify))
								.addComponent(this.lblTranslucency))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPrimaryDisplay)
						.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnIdentify))
				.addComponent(this.lblTranslucency));
		
		boolean transitionsSupported = settings.getPrimaryOrDefaultDisplay().isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
		
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
		
		// create the transitions layout
		JPanel pnlTransitions = new JPanel();
		pnlTransitions.setBorder(BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, this.getBackground().darker()), Messages.getString("panel.general.setup.transition.title")));
		layout = new GroupLayout(pnlTransitions);
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
		
		// create the main layout
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlDisplays, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(pnlTransitions, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlDisplays)
				.addComponent(pnlTransitions));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("identify".equals(command)) {
			// implementation note:
			// we use JDialog here instead of JFrame because we don't want the task bar
			// to show focusable windows.  Using a JDialog allows it to part of the application
			// and not a separate window.
			int n = this.devices.length;
			// identify the devices
			JDialog[] dialogs = new JDialog[n];
			// create the frames
			for (int i = 0; i < n; i++) {
				// get the device
				GraphicsDevice device = this.devices[i];
				// create a frame for each device
				JDialog dialog = dialogs[i] = new JDialog(WindowUtilities.getParentWindow(this));
				// create a label for each device
				JLabel lblName = new JLabel(getDeviceName(device));
				lblName.setFont(new Font(lblName.getFont().getName(), Font.PLAIN, 50));
				lblName.setHorizontalAlignment(SwingConstants.CENTER);
				// make sure the frame is sized and positioned correctly
				GraphicsConfiguration gc = device.getDefaultConfiguration();
				Rectangle r = gc.getBounds();
				dialog.setUndecorated(true);
				dialog.setFocusable(false);
				dialog.setLayout(new BorderLayout());
				dialog.add(lblName, BorderLayout.CENTER);
				dialog.setLocation(r.x, r.y);
				dialog.setMinimumSize(new Dimension(r.width, r.height));
				// enable translucency if available
				if (device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
					dialog.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
				}
				dialog.setVisible(true);
			}
			// close the frames in a different thread
			DelayCloseWindowTask.execute(1000, dialogs);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			// get the new value
			GraphicsDevice device = (GraphicsDevice)e.getItem();
			// test for translucency support
			if (!device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				// if this isn't available, then the translucent color/image backgrounds may not
				// work as expected
				this.lblTranslucency.setText(Messages.getString("panel.general.setup.display.translucent.warning"));
				this.lblTranslucency.setIcon(Icons.WARNING);
			} else {
				this.lblTranslucency.setText("");
				this.lblTranslucency.setIcon(null);
			}
			this.firePropertyChange(DISPLAY_PROPERTY, null, device);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		// set the settings
		this.settings.setPrimaryDisplay((GraphicsDevice)this.cmbDevices.getSelectedItem());
		this.settings.setDefaultSendTransition(((Transition)this.cmbSendTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultSendTransitionDuration(((Number)this.txtSendTransitions.getValue()).intValue());
		this.settings.setDefaultClearTransition(((Transition)this.cmbClearTransitions.getSelectedItem()).getTransitionId());
		this.settings.setDefaultClearTransitionDuration(((Number)this.txtClearTransitions.getValue()).intValue());
		// save the settings to the persistent store
		this.settings.save();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {}
	
	/**
	 * Returns the device index.
	 * @param device the device
	 * @return int
	 */
	private final int getDeviceIndex(GraphicsDevice device) {
		for (int i = 0; i < this.devices.length; i++) {
			if (device == this.devices[i]) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns a device name for the given device.
	 * @param device the device
	 * @return String
	 */
	private final String getDeviceName(GraphicsDevice device) {
		DisplayMode mode = device.getDisplayMode();
		StringBuilder sb = new StringBuilder();
		sb.append(Messages.getString("panel.general.setup.display.name"))
		  .append(getDeviceIndex(device) + 1)
		  .append(" ").append(mode.getWidth())
		  .append("x").append(mode.getHeight())
		  .append(" ").append(mode.getBitDepth())
		  .append("bit at ").append(mode.getRefreshRate())
		  .append("hz");
		return sb.toString();
	}
	
	/**
	 * Renderer for showing devices.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class DeviceRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = -909873909535605533L;
		
		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof GraphicsDevice) {
				GraphicsDevice device = (GraphicsDevice)value;
				
				this.setText(getDeviceName(device));
			}
			
			return this;
		}
	}
}
