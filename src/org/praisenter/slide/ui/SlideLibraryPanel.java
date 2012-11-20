package org.praisenter.slide.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.praisenter.slide.BibleSlideTemplate;
import org.praisenter.slide.NotificationSlideTemplate;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.slide.SlideTemplate;
import org.praisenter.slide.SlideThumbnail;
import org.praisenter.slide.SongSlideTemplate;

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
		
		List<SlideThumbnail> ts = SlideLibrary.getThumbnails(BibleSlideTemplate.class);
		JComboBox<SlideThumbnail> cmbTest = new JComboBox<SlideThumbnail>(ts.toArray(new SlideThumbnail[0]));
		cmbTest.setRenderer(new SlideThumbnailComboBoxRenderer());
		
		JTabbedPane tabs = new JTabbedPane();
		
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(Slide.class);
			JList<SlideThumbnail> lstThumbs = createJList(thumbnails);
			tabs.addTab("Slides", lstThumbs);
		}
		
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(SlideTemplate.class);
			JList<SlideThumbnail> lstThumbs = createJList(thumbnails);
			tabs.addTab("Templates", lstThumbs);
		}
		
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(BibleSlideTemplate.class);
			JList<SlideThumbnail> lstThumbs = createJList(thumbnails);
			tabs.addTab("Bible", lstThumbs);
		}
		
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(SongSlideTemplate.class);
			JList<SlideThumbnail> lstThumbs = createJList(thumbnails);
			tabs.addTab("Songs", lstThumbs);
		}
		
		{
			List<SlideThumbnail> thumbnails = SlideLibrary.getThumbnails(NotificationSlideTemplate.class);
			JList<SlideThumbnail> lstThumbs = createJList(thumbnails);
			tabs.addTab("Notification", lstThumbs);
		}
		
		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);
		this.add(cmbTest, BorderLayout.PAGE_START);
	}
	
	/**
	 * Creates a new JList for the given list of {@link SlideThumbnail}s.
	 * @param thumbnails the list of thumbnails
	 * @return JList
	 */
	private static final JList<SlideThumbnail> createJList(List<SlideThumbnail> thumbnails) {
		JList<SlideThumbnail> list = new JList<SlideThumbnail>();
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(100);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new SlideThumbnailListCellRenderer());
		list.setLayout(new BorderLayout());
		// setup the items
		DefaultListModel<SlideThumbnail> model = new DefaultListModel<SlideThumbnail>();
		for (SlideThumbnail thumbnail : thumbnails) {
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
