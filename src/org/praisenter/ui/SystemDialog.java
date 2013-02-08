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
package org.praisenter.ui;

import java.awt.Container;
import java.awt.Window;
import java.io.File;
import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.praisenter.Constants;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.SystemUtilities;

/**
 * Dialog showing the system information.
 * @author William Bittle
 * @version 2.0.0
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
		
		JLabel lblJavaVersion = new JLabel(Messages.getString("dialog.system.java"));
		JLabel lblJavaVendor = new JLabel(Messages.getString("dialog.system.vendor"));
		JLabel lblOperatingSystem = new JLabel(Messages.getString("dialog.system.os"));
		JLabel lblArchitecture = new JLabel(Messages.getString("dialog.system.architecture"));
		JLabel lblNumberOfCpus = new JLabel(Messages.getString("dialog.system.cpus"));
		JLabel lblLocale = new JLabel(Messages.getString("dialog.system.locale"));
		JLabel lblCurrentDir = new JLabel(Messages.getString("dialog.system.currentDirectory"));
		JLabel lblBasePath = new JLabel(Messages.getString("dialog.system.basePath"));
		
		JTextField txtJavaVersion = new JTextField(SystemUtilities.getJavaVersion());
		JTextField txtJavaVendor = new JTextField(SystemUtilities.getJavaVendor());
		JTextField txtOperatingSystem = new JTextField(SystemUtilities.getOperatingSystem());
		JTextField txtArchitecture = new JTextField(SystemUtilities.getArchitecture());
		JTextField txtNumberOfCpus = new JTextField(String.valueOf(Runtime.getRuntime().availableProcessors()));
		JTextField txtLocale = new JTextField(Locale.getDefault().toString());
		JTextField txtCurrentDir = new JTextField(new File("").getAbsolutePath());
		JTextField txtBasePath = new JTextField(Constants.BASE_PATH);
		
		txtJavaVersion.setEditable(false);
		txtJavaVendor.setEditable(false);
		txtOperatingSystem.setEditable(false);
		txtArchitecture.setEditable(false);
		txtNumberOfCpus.setEditable(false);
		txtLocale.setEditable(false);
		txtCurrentDir.setEditable(false);
		txtBasePath.setEditable(false);
		
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblJavaVersion)
						.addComponent(lblJavaVendor)
						.addComponent(lblOperatingSystem)
						.addComponent(lblArchitecture)
						.addComponent(lblNumberOfCpus)
						.addComponent(lblLocale)
						.addComponent(lblCurrentDir)
						.addComponent(lblBasePath))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtJavaVersion)
						.addComponent(txtJavaVendor)
						.addComponent(txtOperatingSystem)
						.addComponent(txtArchitecture)
						.addComponent(txtNumberOfCpus)
						.addComponent(txtLocale)
						.addComponent(txtCurrentDir)
						.addComponent(txtBasePath)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblJavaVersion)
						.addComponent(txtJavaVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblJavaVendor)
						.addComponent(txtJavaVendor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblOperatingSystem)
						.addComponent(txtOperatingSystem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblArchitecture)
						.addComponent(txtArchitecture, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblNumberOfCpus)
						.addComponent(txtNumberOfCpus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLocale)
						.addComponent(txtLocale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblCurrentDir)
						.addComponent(txtCurrentDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblBasePath)
						.addComponent(txtBasePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
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
