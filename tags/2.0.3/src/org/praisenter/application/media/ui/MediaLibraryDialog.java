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
package org.praisenter.application.media.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JDialog;

import org.praisenter.application.resources.Messages;

/**
 * Simple dialog to display the Media Library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class MediaLibraryDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = -6376427691814126528L;
	
	/** The media library panel */
	private MediaLibraryPanel pnlMediaLibrary;
	
	/**
	 * Minimal constructor.
	 * @param owner the owner of the this dialog; can be null
	 */
	private MediaLibraryDialog(Window owner) {
		super(owner, Messages.getString("dialog.media.title"), ModalityType.APPLICATION_MODAL);
		
		this.pnlMediaLibrary = new MediaLibraryPanel();
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlMediaLibrary, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Shows a new Media Library dialog and returns true if the media library was updated.
	 * @param owner the owner of this dialog; can be null
	 * @return boolean
	 */
	public static final boolean show(Window owner) {
		MediaLibraryDialog dialog = new MediaLibraryDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		boolean mediaLibraryUpdated = dialog.pnlMediaLibrary.isMediaLibraryUpdated();
		
		dialog.dispose();
		
		return mediaLibraryUpdated;
	}
}
