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
package org.praisenter.application.preferences.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.praisenter.application.icons.Icons;
import org.praisenter.application.preferences.Preferences;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.slide.graphics.RenderQuality;

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
	
	// render qualities
	
	/** The overall render quality */
	private JComboBox<RenderQuality> cmbRenderQuality;
	
	/** The transition delay */
	private JSpinner spnTransitionDelay;
	
	/** The interpolation quality */
	private JComboBox<RenderQuality> cmbInterpolationQuality;
	
	/** The color quality */
	private JComboBox<RenderQuality> cmbColorQuality;
	
	/** The alpha interpolation quality */
	private JComboBox<RenderQuality> cmbAlphaInterpolationQuality;
	
	/** The anti-aliasing quality */
	private JComboBox<RenderQuality> cmbAntialiasingQuality;
	
	/** The text anti-aliasing quality */
	private JComboBox<RenderQuality> cmbTextAntialiasingQuality;
	
	/** The fractional metrics quality */
	private JComboBox<RenderQuality> cmbFractionalMetricsQuality;
	
	/** The stroke control quality */
	private JComboBox<RenderQuality> cmbStrokeControlQuality;
	
	// transitions
	
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
		this.cmbDevices.setRenderer(new GraphicsDeviceListCellRenderer());
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
		this.cmbRenderQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbRenderQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.tooltip"));
		this.cmbRenderQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbRenderQuality.setSelectedItem(preferences.getRenderQualities().getRenderQuality());
		
		JLabel lblTransitionDelay = new JLabel(Messages.getString("panel.general.preferences.quality.transition"));
		this.spnTransitionDelay = new JSpinner(new SpinnerNumberModel(20, 0, 100, 10));
		this.spnTransitionDelay.setEditor(new JSpinner.NumberEditor(this.spnTransitionDelay, "0"));
		this.spnTransitionDelay.setToolTipText(Messages.getString("panel.general.preferences.quality.transition.tooltip"));
		
		JLabel lblInterpolationQuality = new JLabel(Messages.getString("panel.general.preferences.quality.interpolation"));
		this.cmbInterpolationQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbInterpolationQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.interpolation.tooltip"));
		this.cmbInterpolationQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbInterpolationQuality.setSelectedItem(preferences.getRenderQualities().getInterpolationQuality());
		
		JLabel lblColorQuality = new JLabel(Messages.getString("panel.general.preferences.quality.color"));
		this.cmbColorQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbColorQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.color.tooltip"));
		this.cmbColorQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbColorQuality.setSelectedItem(preferences.getRenderQualities().getColorQuality());
		
		JLabel lblAlphaInterpolationQuality = new JLabel(Messages.getString("panel.general.preferences.quality.alphaInterpolation"));
		this.cmbAlphaInterpolationQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbAlphaInterpolationQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.alphaInterpolation.tooltip"));
		this.cmbAlphaInterpolationQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbAlphaInterpolationQuality.setSelectedItem(preferences.getRenderQualities().getAlphaInterpolationQuality());
		
		JLabel lblAntialiasingQuality = new JLabel(Messages.getString("panel.general.preferences.quality.antialiasing"));
		this.cmbAntialiasingQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbAntialiasingQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.antialiasing.tooltip"));
		this.cmbAntialiasingQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbAntialiasingQuality.setSelectedItem(preferences.getRenderQualities().getAntialiasingQuality());
		
		JLabel lblTextAntialiasingQuality = new JLabel(Messages.getString("panel.general.preferences.quality.textAntialiasing"));
		this.cmbTextAntialiasingQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbTextAntialiasingQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.textAntialiasing.tooltip"));
		this.cmbTextAntialiasingQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbTextAntialiasingQuality.setSelectedItem(preferences.getRenderQualities().getTextAntialiasingQuality());
		
		JLabel lblFractionalMetricsQuality = new JLabel(Messages.getString("panel.general.preferences.quality.fractionalMetrics"));
		this.cmbFractionalMetricsQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbFractionalMetricsQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.fractionalMetrics.tooltip"));
		this.cmbFractionalMetricsQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbFractionalMetricsQuality.setSelectedItem(preferences.getRenderQualities().getFractionalMetricsQuality());
		
		JLabel lblStrokeControlQuality = new JLabel(Messages.getString("panel.general.preferences.quality.strokeControl"));
		this.cmbStrokeControlQuality = new JComboBox<RenderQuality>(RenderQuality.values());
		this.cmbStrokeControlQuality.setToolTipText(Messages.getString("panel.general.preferences.quality.strokeControl.tooltip"));
		this.cmbStrokeControlQuality.setRenderer(new RenderQualityListCellRenderer());
		this.cmbStrokeControlQuality.setSelectedItem(preferences.getRenderQualities().getStrokeControlQuality());
		
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
		JPanel pnlGeneral = new JPanel();
		GroupLayout layout = new GroupLayout(pnlGeneral);
		pnlGeneral.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPrimaryDisplay)
						.addComponent(lblSmartVideoTransitions)
						.addComponent(lblSmartImageTransitions)
						.addComponent(lblWaitForTransition))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.cmbDevices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnIdentify))
						.addComponent(this.lblDisplayNotFound)
						.addComponent(this.lblTranslucency)
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
						.addComponent(lblSmartVideoTransitions)
						.addComponent(this.chkSmartVideoTransitions))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSmartImageTransitions)
						.addComponent(this.chkSmartImageTransitions))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblWaitForTransition)
						.addComponent(this.chkWaitForTransition)));
		
		JPanel pnlQuality = new JPanel();
		layout = new GroupLayout(pnlQuality);
		pnlQuality.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRenderQuality)
						.addComponent(lblTransitionDelay)
						.addComponent(lblInterpolationQuality)
						.addComponent(lblColorQuality)
						.addComponent(lblAlphaInterpolationQuality))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbRenderQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.spnTransitionDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbInterpolationQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbColorQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbAlphaInterpolationQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addGap(30)
						.addGap(30)
						.addGap(30)
						.addGap(30)
						.addGap(30))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAntialiasingQuality)
						.addComponent(lblTextAntialiasingQuality)
						.addComponent(lblFractionalMetricsQuality)
						.addComponent(lblStrokeControlQuality))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.cmbAntialiasingQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbTextAntialiasingQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbFractionalMetricsQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.cmbStrokeControlQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRenderQuality)
						.addComponent(this.cmbRenderQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblTransitionDelay)
						.addComponent(this.spnTransitionDelay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblAntialiasingQuality)
						.addComponent(this.cmbAntialiasingQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblInterpolationQuality)
						.addComponent(this.cmbInterpolationQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTextAntialiasingQuality)
						.addComponent(this.cmbTextAntialiasingQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblColorQuality)
						.addComponent(this.cmbColorQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFractionalMetricsQuality)
						.addComponent(this.cmbFractionalMetricsQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAlphaInterpolationQuality)
						.addComponent(this.cmbAlphaInterpolationQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblStrokeControlQuality)
						.addComponent(this.cmbStrokeControlQuality, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		JSeparator sep1 = new JSeparator();
		
		// create the main layout
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1)
				.addComponent(pnlQuality, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlGeneral)
				.addComponent(sep1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(pnlQuality));
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
				JLabel lblName = new JLabel(WindowUtilities.getDeviceName(device, i, Messages.getString("display.name.format")));
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
		preferences.setSmartVideoTransitionsEnabled(this.chkSmartVideoTransitions.isSelected());
		preferences.setSmartImageTransitionsEnabled(this.chkSmartImageTransitions.isSelected());
		preferences.setWaitForTransitionEnabled(this.chkWaitForTransition.isSelected());
		Object value = this.spnTransitionDelay.getValue();
		if (value != null && value instanceof Number) {
			preferences.setTransitionDelay(((Number)value).intValue());
		}
		
		preferences.getRenderQualities().setRenderQuality((RenderQuality)this.cmbRenderQuality.getSelectedItem());
		preferences.getRenderQualities().setInterpolationQuality((RenderQuality)this.cmbInterpolationQuality.getSelectedItem());
		preferences.getRenderQualities().setColorQuality((RenderQuality)this.cmbColorQuality.getSelectedItem());
		preferences.getRenderQualities().setAlphaInterpolationQuality((RenderQuality)this.cmbAlphaInterpolationQuality.getSelectedItem());
		preferences.getRenderQualities().setAntialiasingQuality((RenderQuality)this.cmbAntialiasingQuality.getSelectedItem());
		preferences.getRenderQualities().setTextAntialiasingQuality((RenderQuality)this.cmbTextAntialiasingQuality.getSelectedItem());
		preferences.getRenderQualities().setFractionalMetricsQuality((RenderQuality)this.cmbFractionalMetricsQuality.getSelectedItem());
		preferences.getRenderQualities().setStrokeControlQuality((RenderQuality)this.cmbStrokeControlQuality.getSelectedItem());
	}
}
