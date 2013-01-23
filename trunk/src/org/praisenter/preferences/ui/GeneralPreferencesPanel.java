/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.preferences.ui;

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

import org.praisenter.icons.Icons;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.RenderQuality;
import org.praisenter.resources.Messages;
import org.praisenter.threading.DelayCloseWindowTask;
import org.praisenter.utilities.WindowUtilities;

/**
 * Panel used to setup the general preferences for the application.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class GeneralPreferencesPanel extends JPanel implements PreferencesEditor, ActionListener, ItemListener {
	/** The version id */
	private static final long serialVersionUID = 7677045112182344610L;
	
	/** The list of devices (not static so that it will pick up new devices) */
	private GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
	
	// screens
	
	/** The available devices */
	private JComboBox<GraphicsDevice> cmbDevices;
	
	/** The label for the set display not found */
	private JLabel lblDisplayNotFound;
	
	/** The label for translucency support */
	private JLabel lblTranslucency;
	
	// other
	
	/** The render qualities */
	private JComboBox<RenderQuality> cmbRenderQualities;
	
	/** The check box for smart video transitions */
	private JCheckBox chkSmartVideoTransitions;

	/** The check box for smart image transitions */
	private JCheckBox chkSmartImageTransitions;
	
	/** The check box for waiting for transitions */
	private JCheckBox chkWaitForTransition;
	
	/**
	 * Default constructor.
	 */
	public GeneralPreferencesPanel() {
		Preferences preferences = Preferences.getInstance();

		// get the setup device
		GraphicsDevice device = WindowUtilities.getSecondaryDevice();
		String deviceId = preferences.getPrimaryDeviceId();
		if (deviceId != null && deviceId.trim().length() > 0) {
			// this will assign device to null if its not found
			device = WindowUtilities.getScreenDeviceForId(preferences.getPrimaryDeviceId());
		}
		
		this.lblDisplayNotFound = new JLabel();
		this.lblDisplayNotFound.setVisible(false);
		if (device == null) {
			// show the primary display message
			this.lblDisplayNotFound.setVerticalTextPosition(SwingConstants.TOP);
			this.lblDisplayNotFound.setText(Messages.getString("panel.general.preferences.display.missing.warning"));
			this.lblDisplayNotFound.setIcon(Icons.WARNING);
			this.lblDisplayNotFound.setVisible(true);
		}
		
		JLabel lblPrimaryDisplay = new JLabel(Messages.getString("panel.general.preferences.display.primaryDisplay"));
		
		// drop down for the primary display device
		this.cmbDevices = new JComboBox<GraphicsDevice>(this.devices);
		this.cmbDevices.setToolTipText(Messages.getString("panel.general.preferences.display.primaryDisplay.tooltip"));
		this.cmbDevices.setRenderer(new DeviceRenderer());
		if (device != null) {
			this.cmbDevices.setSelectedItem(device);
		}
		this.cmbDevices.addItemListener(this);
		
		// button for the identify 
		JButton btnIdentify = new JButton(Messages.getString("panel.general.preferences.display.identify"));
		btnIdentify.setToolTipText(Messages.getString("panel.general.preferences.display.identify.tooltip"));
		btnIdentify.addActionListener(this);
		btnIdentify.setActionCommand("identify");
		
		// translucency label
		this.lblTranslucency = new JLabel();
		this.lblTranslucency.setVisible(false);
		// check if the primary display is translucent
		if (device != null && !device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.lblTranslucency.setVerticalTextPosition(SwingConstants.TOP);
			this.lblTranslucency.setText(Messages.getString("panel.general.preferences.display.translucent.warning"));
			this.lblTranslucency.setIcon(Icons.WARNING);
			this.lblTranslucency.setVisible(true);
		}
		
		JLabel lblRenderQuality = new JLabel(Messages.getString("panel.general.preferences.quality"));
		this.cmbRenderQualities = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbRenderQualities.setToolTipText(Messages.getString("panel.general.preferences.quality.tooltip"));
		this.cmbRenderQualities.setRenderer(new RenderQualityRenderer());
		this.cmbRenderQualities.setSelectedItem(preferences.getRenderQuality());
		this.cmbRenderQualities.addItemListener(this);
		
		JLabel lblSmartVideoTransitions = new JLabel(Messages.getString("panel.general.preferences.smartVideoTransitions"));
		this.chkSmartVideoTransitions = new JCheckBox();
		this.chkSmartVideoTransitions.setToolTipText(Messages.getString("panel.general.preferences.smartVideoTransitions.tooltip"));
		this.chkSmartVideoTransitions.setSelected(preferences.isSmartVideoTransitionsEnabled());
		
		JLabel lblSmartImageTransitions = new JLabel(Messages.getString("panel.general.preferences.smartImageTransitions"));
		this.chkSmartImageTransitions = new JCheckBox();
		this.chkSmartImageTransitions.setToolTipText(Messages.getString("panel.general.preferences.smartImageTransitions.tooltip"));
		this.chkSmartImageTransitions.setSelected(preferences.isSmartImageTransitionsEnabled());
		
		JLabel lblWaitForTransition = new JLabel(Messages.getString("panel.general.preferences.waitForTransition"));
		this.chkWaitForTransition = new JCheckBox();
		this.chkWaitForTransition.setToolTipText(Messages.getString("panel.general.preferences.waitForTransition.tooltip"));
		this.chkWaitForTransition.setSelected(preferences.isWaitForTransitionEnabled());
		
		// create the layout
		JPanel pnlDisplays = new JPanel();
		GroupLayout layout = new GroupLayout(pnlDisplays);
		pnlDisplays.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPrimaryDisplay)
						.addComponent(lblRenderQuality)
						.addComponent(lblSmartVideoTransitions)
						.addComponent(lblSmartImageTransitions)
						.addComponent(lblWaitForTransition))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnIdentify))
						.addComponent(this.lblDisplayNotFound)
						.addComponent(this.lblTranslucency)
						.addComponent(this.cmbRenderQualities, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.chkSmartVideoTransitions)
						.addComponent(this.chkSmartImageTransitions)
						.addComponent(this.chkWaitForTransition)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPrimaryDisplay)
						.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnIdentify))
				.addComponent(this.lblDisplayNotFound)
				.addComponent(this.lblTranslucency)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRenderQuality)
						.addComponent(this.cmbRenderQualities, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSmartVideoTransitions)
						.addComponent(this.chkSmartVideoTransitions))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSmartImageTransitions)
						.addComponent(this.chkSmartImageTransitions))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblWaitForTransition)
						.addComponent(this.chkWaitForTransition)));
		
		// create the main layout
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlDisplays));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlDisplays));
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
					this.lblTranslucency.setText(Messages.getString("panel.general.preferences.display.translucent.warning"));
					this.lblTranslucency.setIcon(Icons.WARNING);
				} else {
					this.lblTranslucency.setText("");
					this.lblTranslucency.setIcon(null);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.preferences.ui.PreferencesEditor#applyPreferences()
	 */
	@Override
	public void applyPreferences() {
		Preferences preferences = Preferences.getInstance();
		
		GraphicsDevice device = (GraphicsDevice)this.cmbDevices.getSelectedItem();
		if (device != null) {
			preferences.setPrimaryDeviceId(device.getIDstring());
			preferences.setPrimaryDeviceResolution(WindowUtilities.getDimension(device.getDisplayMode()));
		}
		preferences.setRenderQuality((RenderQuality)this.cmbRenderQualities.getSelectedItem());
		preferences.setSmartVideoTransitionsEnabled(this.chkSmartVideoTransitions.isSelected());
		preferences.setSmartImageTransitionsEnabled(this.chkSmartImageTransitions.isSelected());
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
		int rate = mode.getRefreshRate();
		if (rate <= 0) {
			// assume its 60 (mac os x and LCD monitors)
			rate = 60;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(Messages.getString("panel.general.preferences.display.name"))
		  .append(getDeviceIndex(device) + 1)
		  .append(" ").append(mode.getWidth())
		  .append("x").append(mode.getHeight())
		  .append(" ").append(mode.getBitDepth())
		  .append("bit at ").append(rate)
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
					this.setText(Messages.getString("panel.general.preferences.quality.high"));
					this.setToolTipText(Messages.getString("panel.general.preferences.quality.high.tooltip"));
				} else if (quality == RenderQuality.MEDIUM) {
					this.setText(Messages.getString("panel.general.preferences.quality.medium"));
					this.setToolTipText(Messages.getString("panel.general.preferences.quality.medium.tooltip"));
				} else {
					this.setText(Messages.getString("panel.general.preferences.quality.low"));
					this.setToolTipText(Messages.getString("panel.general.preferences.quality.low.tooltip"));
				}
			}
			
			return this;
		}
	}
}
