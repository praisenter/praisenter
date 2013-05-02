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
package org.praisenter.application.ui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.tuple.Pair;
import org.praisenter.application.Constants;
import org.praisenter.application.Main;
import org.praisenter.application.Version;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.utilities.FileUtilities;
import org.praisenter.common.utilities.SystemUtilities;
import org.praisenter.common.utilities.WindowUtilities;

/**
 * Dialog showing the system information.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class SystemDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = 8222340775333362305L;

	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private SystemDialog(Window owner) {
		super(owner, Messages.getString("dialog.system.title"), ModalityType.APPLICATION_MODAL);
		
		// build the property listing
		List<Pair<String, String>> properties = new ArrayList<Pair<String, String>>();
		
		properties.add(Pair.of(Messages.getString("dialog.system.praisenter.version"), Version.getVersion()));
		properties.add(Pair.of(Messages.getString("dialog.system.java"), SystemUtilities.getJavaVersion()));
		properties.add(Pair.of(Messages.getString("dialog.system.vendor"), SystemUtilities.getJavaVendor()));
		properties.add(Pair.of(Messages.getString("dialog.system.javaPath"), SystemUtilities.getJavaHomeDirectory()));
		properties.add(Pair.of(Messages.getString("dialog.system.os"), SystemUtilities.getOperatingSystem()));
		properties.add(Pair.of(Messages.getString("dialog.system.architecture"), SystemUtilities.getArchitecture()));
		properties.add(Pair.of(Messages.getString("dialog.system.cpus"), String.valueOf(Runtime.getRuntime().availableProcessors())));
		properties.add(Pair.of(Messages.getString("dialog.system.locale"), Locale.getDefault().toString()));
		properties.add(Pair.of(Messages.getString("dialog.system.separator"), FileUtilities.getSeparator()));
		properties.add(Pair.of(Messages.getString("dialog.system.currentDirectory"), new File("").getAbsolutePath()));
		properties.add(Pair.of(Messages.getString("dialog.system.userPath"), SystemUtilities.getUserHomeDirectory()));
		properties.add(Pair.of(Messages.getString("dialog.system.basePath"), Constants.BASE_PATH));
		properties.add(Pair.of(Messages.getString("dialog.system.desktopSupport"), String.valueOf(Desktop.isDesktopSupported())));
		properties.add(Pair.of(Messages.getString("dialog.system.defaultDevice"), WindowUtilities.getDefaultDevice().getIDstring()));
		GraphicsDevice[] devices = WindowUtilities.getDevices();
		properties.add(Pair.of(Messages.getString("dialog.system.deviceCount"), String.valueOf(devices.length)));
		for (GraphicsDevice device : devices) {
			properties.add(Pair.of(MessageFormat.format(Messages.getString("dialog.system.translucencySupport"), device.getIDstring()), String.valueOf(device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT))));
		}
		properties.add(Pair.of(Messages.getString("dialog.system.laf"), UIManager.getLookAndFeel().getName()));
		properties.add(Pair.of(Messages.getString("dialog.system.debugEnabled"), String.valueOf(Main.isDebugEnabled())));
		
		// create the verse queue table
		@SuppressWarnings("serial")
		JTable tblProperties = new JTable(new SystemTableModel(properties)) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				if (row < 0) return super.getToolTipText();
				// since sorting is allowed, we need to translate the view row index
				// into the model row index
				row = this.convertRowIndexToModel(row);
				
				// get the text column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, 1);
				if (object != null) {
					// get the text
					return object.toString();
				}
				
				return super.getToolTipText(event);
			}
		};
		tblProperties.setAutoCreateRowSorter(true);
		tblProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblProperties.setColumnSelectionAllowed(false);
		tblProperties.setCellSelectionEnabled(false);
		tblProperties.setRowSelectionAllowed(true);
		tblProperties.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tblProperties.getColumnModel().getColumn(0).setMaxWidth(200);
		tblProperties.getColumnModel().getColumn(0).setPreferredWidth(150);
		
		JScrollPane scrProperties = new JScrollPane(tblProperties);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(scrProperties));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrProperties));
		
		this.pack();
	}
	
	/**
	 * Shows the about dialog.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		// create the dialog
		SystemDialog dialog = new SystemDialog(owner);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
		
		dialog.dispose();
	}
}
