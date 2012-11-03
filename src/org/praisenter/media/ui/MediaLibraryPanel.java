package org.praisenter.media.ui;

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
import org.praisenter.media.MediaType;
import org.praisenter.media.Thumbnail;
import org.praisenter.resources.Messages;
import org.praisenter.ui.ImageFileFilter;
import org.praisenter.ui.ImageFilePreview;

/**
 * Panel used to maintain the Media Library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME we need a progress bar on the "add/remove to media library"
public class MediaLibraryPanel extends JPanel implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -5811856651322928169L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MediaLibraryPanel.class);
	
	/** Image support */
	protected static final boolean IMAGES_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.IMAGE);
	
	/** Video support */
	protected static final boolean VIDEOS_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.VIDEO);
	
	/** Audio support */
	protected static final boolean AUDIO_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.AUDIO);
	
	// controls
	
	/** The tabs for the media types */
	protected JTabbedPane mediaTabs;
	
	/** The image media list */
	protected JList<Thumbnail> lstImages;
	
	/** The video media list */
	protected JList<Thumbnail> lstVideos;
	
	/** The audio media list */
	protected JList<Thumbnail> lstAudio;
	
	/**
	 * Default constructor.
	 */
	public MediaLibraryPanel() {
		// add a button to add media
		JButton btnAddMedia = new JButton(Messages.getString("panel.media.add"));
		btnAddMedia.setActionCommand("addMedia");
		btnAddMedia.addActionListener(this);
		
		JButton btnRemove = new JButton(Messages.getString("panel.media.remove"));
		btnRemove.setActionCommand("removeMedia");
		btnRemove.addActionListener(this);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new BorderLayout());
		pnlButtons.add(btnAddMedia, BorderLayout.LINE_START);
		pnlButtons.add(btnRemove, BorderLayout.LINE_END);
		
		this.mediaTabs = new JTabbedPane();
		
		// make sure the media library supports the media
		if (IMAGES_SUPPORTED) {
			// load up all the thumbnails for the media in the media library
			List<Thumbnail> images = MediaLibrary.getThumbnails(MediaType.IMAGE);
			JPanel pnlImages = new JPanel();
			pnlImages.setLayout(new BorderLayout());
			// images list
			this.lstImages = createJList(images);
			pnlImages.add(this.lstImages, BorderLayout.CENTER);
			
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.images"), pnlImages);
		}
		
		// make sure the media library supports the media
		if (VIDEOS_SUPPORTED) {
			List<Thumbnail> videos = MediaLibrary.getThumbnails(MediaType.VIDEO);
			JPanel pnlVideos = new JPanel();
			pnlVideos.setLayout(new BorderLayout());
			// images list
			this.lstVideos = createJList(videos);
			pnlVideos.add(this.lstVideos, BorderLayout.CENTER);
			
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.videos"), pnlVideos);
		}
		
		// make sure the media library supports the media
		if (AUDIO_SUPPORTED) {
			List<Thumbnail> audio = MediaLibrary.getThumbnails(MediaType.AUDIO);
			JPanel pnlAudio = new JPanel();
			pnlAudio.setLayout(new BorderLayout());
			// images list
			this.lstAudio = createJList(audio);
			pnlAudio.add(this.lstAudio, BorderLayout.CENTER);
			
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.audio"), pnlAudio);
		}
		
		this.setLayout(new BorderLayout());
		this.add(pnlButtons, BorderLayout.PAGE_START);
		this.add(this.mediaTabs, BorderLayout.CENTER);
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
		if ("addMedia".equals(command)) {
			// show a file browser with the allowed types
			JFileChooser fc = new JFileChooser();
			// the user can only select files
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			// they can only select one file
			fc.setMultiSelectionEnabled(false);
			// they can only select image files
			FileFilter filter = new ImageFileFilter();
			fc.setFileFilter(filter);
			fc.addChoosableFileFilter(filter);
			if (VIDEOS_SUPPORTED) {
				fc.addChoosableFileFilter(new AllFilesFileFilter(Messages.getString("filter.video.description")));
			}
			if (AUDIO_SUPPORTED) {
				fc.addChoosableFileFilter(new AllFilesFileFilter(Messages.getString("filter.audio.description")));
			}
			// provide a preview for image files
			fc.setAccessory(new ImageFilePreview(fc));
			// they cannot switch to all files
			fc.setAcceptAllFileFilterUsed(false);
			// show the dialog
			int result = fc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				// get the file
				File file = fc.getSelectedFile();
				// attempt to load the image
				try {
					String image = file.getAbsolutePath();
					Media media = MediaLibrary.addMedia(image);
					// add the thumbnail to the list
					JList<Thumbnail> list = null;
					if (media.getType() == MediaType.IMAGE) {
						list = this.lstImages;
					} else if (media.getType() == MediaType.VIDEO) {
						list = this.lstVideos;
					} else if (media.getType() == MediaType.AUDIO) {
						list = this.lstAudio;
					} else {
						// do nothing
						return;
					}
					DefaultListModel<Thumbnail> model = (DefaultListModel<Thumbnail>)list.getModel();
					model.addElement(MediaLibrary.getThumbnail(media));
				} catch (Exception ex) {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.media.add.exception.title"), 
							MessageFormat.format(Messages.getString("panel.media.add.exception.text"), file.getAbsolutePath()), 
							ex);
					LOGGER.error("An error occurred while attempting to add [" + file.getAbsolutePath() + "] to the media library: ", ex);
				}
			}
		} else if ("removeMedia".equals(command)) {
			// get the selected media from the currently visible tab
			int index = this.mediaTabs.getSelectedIndex();
			// get the JList to modify
			JList<Thumbnail> list = null;
			if (index == 0) {
				list = this.lstImages;
			} else if (index == 1) {
				list = this.lstVideos;
			} else if (index == 2) {
				list = this.lstAudio;
			} else {
				// do nothing
				return;
			}
			
			// image tab
			Thumbnail thumbnail = list.getSelectedValue();
			// make sure something is selected
			if (thumbnail != null) {
				// remove the media from the media library
				try {
					MediaLibrary.removeMedia(thumbnail.getFileProperties().getFilePath());
					// remove the thumbnail from the list
					DefaultListModel<Thumbnail> model = (DefaultListModel<Thumbnail>)list.getModel();
					model.removeElement(thumbnail);
				} catch (IOException ex) {
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.media.remove.exception.title"), 
							MessageFormat.format(Messages.getString("panel.media.remove.exception.text"), thumbnail.getFileProperties().getFileName()), 
							ex);
					LOGGER.error("An error occurred while attempting to remove [" + thumbnail.getFileProperties().getFilePath() + "] from the media library: ", ex);
				}
			}
		}
	}
}
