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
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.praisenter.application.resources.Messages;
import org.praisenter.media.MediaCodec;
import org.praisenter.media.MediaContainerFormat;
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
		
		// the container format table
		@SuppressWarnings("serial")
		JTable tblFormats = new JTable(new MediaContainerFormatsTableModel(this.getSupportedContainerFormats())) {
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
		
		// the codecs table
		@SuppressWarnings("serial")
		JTable tblCodecs = new JTable(new MediaCodecTableModel(this.getSupportedCodecs())) {
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
		tblCodecs.setAutoCreateRowSorter(true);
		tblCodecs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblCodecs.setColumnSelectionAllowed(false);
		tblCodecs.setCellSelectionEnabled(false);
		tblCodecs.setRowSelectionAllowed(true);
		tblCodecs.getColumnModel().getColumn(0).setPreferredWidth(50);
		tblCodecs.getColumnModel().getColumn(1).setPreferredWidth(300);
		tblCodecs.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		// setup the tabs
		JTabbedPane tabs = new JTabbedPane();
		
		JScrollPane scrFormats = new JScrollPane(tblFormats);
		scrFormats.setBorder(null);
		tabs.addTab(Messages.getString("dialog.formats.formats"), scrFormats);
		
		JScrollPane scrCodecs = new JScrollPane(tblCodecs);
		scrCodecs.setBorder(null);
		tabs.addTab(Messages.getString("dialog.formats.codecs"), scrCodecs);
		
		container.add(tabs, BorderLayout.CENTER);
		
		this.pack();
	}
	
	/**
	 * Returns all the supported codecs.
	 * @return List&lt;{@link MediaCodec}&gt;
	 */
	private List<MediaCodec> getSupportedCodecs() {
		SortedSet<MediaCodec> formats = new TreeSet<MediaCodec>();
		
		// get all the loaders for images
		List<MediaLoader<?>> loaders = MediaLibrary.getMediaLoaders(MediaType.IMAGE);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedCodecs());
		}
		
		// get all the loaders for images
		loaders = MediaLibrary.getMediaLoaders(MediaType.AUDIO);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedCodecs());
		}
		
		// get all the loaders for images
		loaders = MediaLibrary.getMediaLoaders(MediaType.VIDEO);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedCodecs());
		}
		
		return Arrays.asList(formats.toArray(new MediaCodec[0]));
	}
	
	/**
	 * Returns all the supported container formats.
	 * @return List&lt;{@link MediaContainerFormat}&gt;
	 */
	private List<MediaContainerFormat> getSupportedContainerFormats() {
		SortedSet<MediaContainerFormat> formats = new TreeSet<MediaContainerFormat>();
		
		// get all the loaders for images
		List<MediaLoader<?>> loaders = MediaLibrary.getMediaLoaders(MediaType.IMAGE);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedContainerFormats());
		}
		
		// get all the loaders for images
		loaders = MediaLibrary.getMediaLoaders(MediaType.AUDIO);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedContainerFormats());
		}
		
		// get all the loaders for images
		loaders = MediaLibrary.getMediaLoaders(MediaType.VIDEO);
		// combine all the supported formats into one list
		for (MediaLoader<?> loader : loaders) {
			formats.addAll(loader.getSupportedContainerFormats());
		}
		
		return Arrays.asList(formats.toArray(new MediaContainerFormat[0]));
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
