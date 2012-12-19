package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.praisenter.media.MediaThumbnail;

/**
 * Helper class for creating media UI elements.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class MediaUI {
	/**
	 * Creates a new JList for the given list of {@link MediaThumbnail}s.
	 * @param thumbnails the list of thumbnails
	 * @return JList
	 */
	public static final JList<MediaThumbnail> createJList(List<MediaThumbnail> thumbnails) {
		JList<MediaThumbnail> list = new JList<MediaThumbnail>();
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(100);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new MediaThumbnailListCellRenderer());
		list.setLayout(new BorderLayout());
		// setup the items
		DefaultListModel<MediaThumbnail> model = new DefaultListModel<MediaThumbnail>();
		for (MediaThumbnail thumbnail : thumbnails) {
			model.addElement(thumbnail);
		}
		list.setModel(model);
		
		return list;
	}
}
