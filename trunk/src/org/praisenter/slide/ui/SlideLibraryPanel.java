package org.praisenter.slide.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.praisenter.data.errors.ui.ExceptionDialog;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;
import org.praisenter.resources.Messages;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.ui.ImageFileFilter;
import org.praisenter.ui.ImageFilePreview;
import org.praisenter.ui.ThumbnailListCellRenderer;
import org.praisenter.xml.Thumbnail;

/**
 * Panel used to maintain the Slide Library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideLibraryPanel extends JPanel implements ActionListener {
	/** The version id */

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideLibraryPanel.class);
	
	// controls
	
	/**
	 * Default constructor.
	 */
	public SlideLibraryPanel() {
		this.setLayout(new BorderLayout());
		List<Thumbnail> thumbnails = SlideLibrary.getThumbnails(Slide.class);
		JList lstThumbs = createJList(thumbnails);
		this.add(lstThumbs, BorderLayout.CENTER);
	}
	
	/**
	 * Creates a new JList for the given list of {@link Thumbnail}s.
	 * @param thumbnails the list of thumbnails
	 * @return JList
	 */
	private static final JList<Thumbnail> createJList(List<Thumbnail> thumbnails) {
		JList<Thumbnail> list = new JList<Thumbnail>();
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(100);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new ThumbnailListCellRenderer());
		list.setLayout(new BorderLayout());
		// setup the items
		DefaultListModel<Thumbnail> model = new DefaultListModel<Thumbnail>();
		for (Thumbnail thumbnail : thumbnails) {
			model.addElement(thumbnail);
		}
		list.setModel(model);
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
	}
}
