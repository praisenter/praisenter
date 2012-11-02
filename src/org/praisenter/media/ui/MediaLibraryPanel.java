package org.praisenter.media.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.media.Thumbnail;

public class MediaLibraryPanel extends JPanel {
	// 3 tabs
	// list of media
	// button to add media
	// file filter options
	public MediaLibraryPanel() {
		// load up all the thumbnails for the media in the media library
		List<Thumbnail> images = MediaLibrary.getThumbnails(MediaType.IMAGE);
		List<Thumbnail> videos = MediaLibrary.getThumbnails(MediaType.VIDEO);
		List<Thumbnail> audio = MediaLibrary.getThumbnails(MediaType.AUDIO);
		
		JPanel pnlImages = new JPanel();
		JPanel pnlVideos = new JPanel();
		JPanel pnlAudio = new JPanel();
		
		// images list
		JList<Thumbnail> lstImages = new JList<>(images.toArray(new Thumbnail[0]));
		lstImages.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstImages.setFixedCellWidth(100);
		lstImages.setVisibleRowCount(-1);
		lstImages.setCellRenderer(new ThumbnailListCellRenderer());
		pnlImages.setLayout(new BorderLayout());
		pnlImages.add(lstImages, BorderLayout.CENTER);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Images", pnlImages);
		tabs.addTab("Videos", pnlVideos);
		tabs.addTab("Audio", pnlAudio);
		
		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);
	}
}
