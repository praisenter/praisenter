package org.praisenter.settings.ui;

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

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.praisenter.display.RenderQuality;
import org.praisenter.icons.Icons;
import org.praisenter.resources.Messages;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.settings.SettingsException;
import org.praisenter.tasks.DelayCloseWindowTask;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to setup the general settings for the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class GeneralSettingsPanel extends JPanel implements SettingsPanel, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = 8936112479746612291L;
	
	/** Property name of the display drop down */
	public static final String PRIMARY_DISPLAY_PROPERTY = "PrimaryDisplay";
	
	/** Property name of the render quality drop down */
	public static final String RENDER_QUALITY_PROPERTY = "RenderQuality";
	
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
	
	/** The render qualities */
	private JComboBox<RenderQuality> cmbRenderQualities;
	
	/**
	 * Minimal constructor.
	 * @param settings the settings
	 */
	public GeneralSettingsPanel(GeneralSettings settings) {
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
		
		// drop down for the primary display device
		this.cmbDevices = new JComboBox<GraphicsDevice>(this.devices);
		this.cmbDevices.setToolTipText(Messages.getString("panel.general.setup.display.primaryDisplay.tooltip"));
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
		
		JLabel lblRenderQuality = new JLabel(Messages.getString("panel.general.setup.quality"));
		this.cmbRenderQualities = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbRenderQualities.setToolTipText(Messages.getString("panel.general.setup.quality.tooltip"));
		this.cmbRenderQualities.setRenderer(new RenderQualityRenderer());
		this.cmbRenderQualities.setSelectedItem(settings.getRenderQuality());
		this.cmbRenderQualities.addItemListener(this);
		
		// create the layout
		JPanel pnlDisplays = new JPanel();
		pnlDisplays.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		GroupLayout layout = new GroupLayout(pnlDisplays);
		pnlDisplays.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(lblPrimaryDisplay)
								.addComponent(lblRenderQuality))
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnIdentify))
								.addComponent(this.lblTranslucency)
								.addComponent(this.cmbRenderQualities, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.lblDisplayNotFound)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPrimaryDisplay)
						.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnIdentify))
				.addComponent(this.lblTranslucency)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRenderQuality)
						.addComponent(this.cmbRenderQualities, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(Messages.getString("panel.bible.setup.general"), pnlDisplays);
		
		JLabel lblMessage = new JLabel(Messages.getString("panel.general.setup.message"));
		lblMessage.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		// create the main layout
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblMessage)
				.addComponent(tabs));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblMessage)
				.addComponent(tabs));
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
			if (e.getSource() == this.cmbDevices) {
				// get the new value
				GraphicsDevice device = (GraphicsDevice)e.getItem();
				// test for translucency support
				if (!device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
					// if this device doesn't support translucency, then the translucent color/image backgrounds will not work
					this.lblTranslucency.setText(Messages.getString("panel.general.setup.display.translucent.warning"));
					this.lblTranslucency.setIcon(Icons.WARNING);
				} else {
					this.lblTranslucency.setText("");
					this.lblTranslucency.setIcon(null);
				}
				this.firePropertyChange(PRIMARY_DISPLAY_PROPERTY, null, device);
			} else if (e.getSource() == this.cmbRenderQualities) {
				this.firePropertyChange(RENDER_QUALITY_PROPERTY, null, e.getItem());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.panel.setup.SetupPanel#saveSettings()
	 */
	@Override
	public void saveSettings() throws SettingsException {
		// set the settings
		this.settings.setPrimaryDisplay((GraphicsDevice)this.cmbDevices.getSelectedItem());
		this.settings.setRenderQuality((RenderQuality)this.cmbRenderQualities.getSelectedItem());
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
	
	/**
	 * Renderer for showing render qualities.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class RenderQualityRenderer extends DefaultListCellRenderer {
		/** The version id */
		private static final long serialVersionUID = 3065297114501982402L;
		
		/* (non-Javadoc)
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof RenderQuality) {
				RenderQuality quality = (RenderQuality)value;
				if (quality == RenderQuality.HIGH) {
					this.setText(Messages.getString("panel.general.setup.quality.high"));
					this.setToolTipText(Messages.getString("panel.general.setup.quality.high.tooltip"));
				} else if (quality == RenderQuality.MEDIUM) {
					this.setText(Messages.getString("panel.general.setup.quality.medium"));
					this.setToolTipText(Messages.getString("panel.general.setup.quality.medium.tooltip"));
				} else {
					this.setText(Messages.getString("panel.general.setup.quality.low"));
					this.setToolTipText(Messages.getString("panel.general.setup.quality.low.tooltip"));
				}
			}
			
			return this;
		}
	}
}
