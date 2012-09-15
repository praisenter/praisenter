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

import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.praisenter.data.DataException;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Bibles;
import org.praisenter.icons.Icons;
import org.praisenter.panel.bible.BibleListCellRenderer;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.utilities.DelayCloseWindowTask;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to setup the general settings for the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class GeneralSetupPanel extends JPanel implements ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = 8936112479746612291L;
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(GeneralSetupPanel.class);
	
	/** Property name of the display drop down */
	public static final String DISPLAY_PROPERTY = "gspDisplay";
	
	/** The list of devices (not static so that it will pick up new devices) */
	private GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
	
	/** The settings being modified */
	private GeneralSettings settings;
	
	/** The available devices */
	private JComboBox<GraphicsDevice> cmbDevices;
	
	/** The label for the set display not found */
	private JLabel lblDisplayNotFound;
	
	/** The label for translucency support */
	private JLabel lblTranslucency;
	
	/** The available bibles */
	private JComboBox<Bible> cmbBibles;
	
	/** The checkbox to include/exclude the apocrypha */
	private JCheckBox chkIncludeApocrypha;
	
	/**
	 * Minimal constructor.
	 * @param settings the settings
	 */
	public GeneralSetupPanel(GeneralSettings settings) {
		this.settings = settings;
		
		GraphicsDevice primary = settings.getPrimaryOrDefaultDisplay();
		
		this.lblDisplayNotFound = new JLabel();
		this.lblDisplayNotFound.setVerticalTextPosition(SwingConstants.TOP);
		if (settings.getPrimaryDisplay() == null) {
			// show the primary display message
			this.lblDisplayNotFound.setText(Messages.getString("panel.display.missing.warning"));
			this.lblDisplayNotFound.setIcon(Icons.WARNING);
		}
		
		JLabel lblPrimaryDisplay = new JLabel(Messages.getString("panel.display.label.primaryDisplay"));
		lblPrimaryDisplay.setToolTipText(Messages.getString("panel.display.label.primaryDisplay.tooltip"));
		
		// drop down for the primary display device
		this.cmbDevices = new JComboBox<GraphicsDevice>(this.devices);
		this.cmbDevices.setRenderer(new DeviceRenderer());
		if (primary != null) {
			this.cmbDevices.setSelectedItem(primary);
		}
		this.cmbDevices.addItemListener(this);
		
		// button for the identify 
		JButton btnIdentify = new JButton(Messages.getString("panel.display.button.identify"));
		btnIdentify.setToolTipText(Messages.getString("panel.display.button.identify.tooltip"));
		btnIdentify.addActionListener(this);
		btnIdentify.setActionCommand("identify");
		
		// translucency label
		this.lblTranslucency = new JLabel();
		this.lblTranslucency.setVerticalTextPosition(SwingConstants.TOP);
		// check if the primary display is translucent
		if (!primary.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.lblTranslucency.setText(Messages.getString("panel.display.translucent.warning"));
			this.lblTranslucency.setIcon(Icons.WARNING);
		}
		
		// general bible settings
		JLabel lblDefaultBible = new JLabel(Messages.getString("panel.general.defaultBible"));
		Bible[] bibles = null;
		try {
			bibles = Bibles.getBibles().toArray(new Bible[0]);
		} catch (DataException e) {
			LOGGER.error("Bibles could not be retrieved:", e);
		}
		// check for null
		if (bibles == null) {
			this.cmbBibles = new JComboBox<Bible>();
		} else {
			this.cmbBibles = new JComboBox<Bible>(bibles);
		}
		this.cmbBibles.setRenderer(new BibleListCellRenderer());
		// get the default value
		Bible bible = null;
		try {
			bible = Bibles.getBible(settings.getDefaultBibleId());
		} catch (DataException e) {
			LOGGER.error("Default bible could not be retrieved:", e);
		}
		if (bible != null) {
			// set the default value
			this.cmbBibles.setSelectedItem(bible);
		}
		JLabel lblIncludeApocrypha = new JLabel(Messages.getString("panel.general.includeApocrypha"));
		lblIncludeApocrypha.setToolTipText(Messages.getString("panel.general.includeApocrypha.tooltip"));
		this.chkIncludeApocrypha = new JCheckBox();
		this.chkIncludeApocrypha.setSelected(settings.isApocryphaIncluded());
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblPrimaryDisplay)
								.addComponent(lblDefaultBible)
								.addComponent(lblIncludeApocrypha))
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.cmbDevices)
										.addComponent(btnIdentify))
								.addComponent(this.lblTranslucency)
								.addComponent(this.cmbBibles)
								.addComponent(this.chkIncludeApocrypha))));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPrimaryDisplay)
						.addComponent(this.cmbDevices)
						.addComponent(btnIdentify))
				.addComponent(this.lblTranslucency)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblDefaultBible)
						.addComponent(this.cmbBibles))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblIncludeApocrypha)
						.addComponent(this.chkIncludeApocrypha)));
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
				this.lblTranslucency.setText(Messages.getString("panel.display.translucent.warning"));
				this.lblTranslucency.setIcon(Icons.WARNING);
			} else {
				this.lblTranslucency.setText("");
				this.lblTranslucency.setIcon(null);
			}
			this.firePropertyChange(DISPLAY_PROPERTY, null, device);
		}
	}
	
	/**
	 * Saves the settings configured by this panel.
	 * @throws SettingsException if an exception occurs while assigning a setting
	 */
	public void saveSettings() throws SettingsException {
		// set the settings
		this.settings.setPrimaryDisplay((GraphicsDevice)this.cmbDevices.getSelectedItem());
		this.settings.setDefaultBibleId(((Bible)this.cmbBibles.getSelectedItem()).getId());
		this.settings.setApocryphaIncluded(this.chkIncludeApocrypha.isSelected());
		// save the settings to the persistent store
		this.settings.save();
	}
	
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
		sb.append(Messages.getString("panel.display.name"))
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
