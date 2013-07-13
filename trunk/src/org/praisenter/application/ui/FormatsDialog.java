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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.tuple.Pair;
import org.praisenter.application.resources.Messages;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaLoader;
import org.praisenter.media.MediaType;

/**
 * Dialog showing the about information.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.2
 */
public class FormatsDialog extends JDialog {
	/** The version id */
	private static final long serialVersionUID = 4110922903709506566L;

	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private FormatsDialog(Window owner) {
		super(owner, Messages.getString("dialog.formats.title"), ModalityType.MODELESS);
		// set the size
		this.setPreferredSize(new Dimension(550, 450));
		
		Container container = this.getContentPane();
		
		// set the layout
		container.setLayout(new BorderLayout());
		
		JTable tblImageFormats = this.getTableForMediaType(MediaType.IMAGE);
		JTable tblAudioFormats = this.getTableForMediaType(MediaType.AUDIO);
		JTable tblVideoFormats = this.getTableForMediaType(MediaType.VIDEO);
		
		JTabbedPane tabs = new JTabbedPane();
		
		JScrollPane scrImage = new JScrollPane(tblImageFormats);
		scrImage.setBorder(null);
		tabs.addTab(Messages.getString("dialog.formats.image"), scrImage);
		
		JScrollPane scrAudio = new JScrollPane(tblAudioFormats);
		scrAudio.setBorder(null);
		tabs.addTab(Messages.getString("dialog.formats.audio"), scrAudio);
		
		JScrollPane scrVideo = new JScrollPane(tblVideoFormats);
		scrVideo.setBorder(null);
		tabs.addTab(Messages.getString("dialog.formats.video"), scrVideo);
		
		container.add(tabs, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Returns a JTable of formats for the given media type.
	 * @param type the media type
	 * @return JTable
	 */
	private JTable getTableForMediaType(MediaType type) {
		@SuppressWarnings("serial")
		JTable tblFormats = new JTable(new MediaFormatsTableModel(this.getSupportedFormats(type))) {
			@Override
			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = this.rowAtPoint(p);
				int col = this.columnAtPoint(p);
				if (row < 0) return super.getToolTipText();
				if (col < 0) return super.getToolTipText();
				// since sorting is allowed, we need to translate the view row index
				// into the model row index
				row = this.convertRowIndexToModel(row);
				col = this.convertColumnIndexToModel(col);
				
				// get the text column value
				TableModel model = this.getModel();
				Object object = model.getValueAt(row, col);
				if (object != null) {
					// get the text
					return object.toString();
				}
				
				return super.getToolTipText(event);
			}
		};
		tblFormats.setAutoCreateRowSorter(true);
		tblFormats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblFormats.setColumnSelectionAllowed(false);
		tblFormats.setCellSelectionEnabled(false);
		tblFormats.setRowSelectionAllowed(true);
		tblFormats.getColumnModel().getColumn(0).setPreferredWidth(50);
		tblFormats.getColumnModel().getColumn(1).setPreferredWidth(300);
		tblFormats.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		return tblFormats;
	}
	
	/**
	 * Returns the formats for the given media type.
	 * @param type the media type
	 * @return List&lt;Pair&lt;String,String&gt;&gt;
	 */
	private List<Pair<String, String>> getSupportedFormats(MediaType type) {
		List<Pair<String, String>> formats = new ArrayList<Pair<String, String>>();
		// get all the loaders for images
		List<MediaLoader<?>> loaders = MediaLibrary.getMediaLoaders(type);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedContainerFormats());
		}
		// sort the formats by the format name
		Collections.sort(formats, new Comparator<Pair<String,String>>() {
			@Override
			public int compare(Pair<String, String> o1, Pair<String, String> o2) {
				// compare by the first element
				return o1.getLeft().compareToIgnoreCase(o2.getLeft());
			}
		});
		return formats;
	}
	
	/**
	 * Shows the about dialog.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		// create the dialog
		FormatsDialog dialog = new FormatsDialog(owner);
		dialog.setLocationRelativeTo(owner);
		// show the dialog
		dialog.setVisible(true);
	}
}
