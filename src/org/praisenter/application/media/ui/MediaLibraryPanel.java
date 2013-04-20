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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.praisenter.application.errors.ui.ExceptionDialog;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.AllFilesFileFilter;
import org.praisenter.application.ui.ImageFileFilter;
import org.praisenter.application.ui.TaskProgressDialog;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.threading.AbstractTask;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.media.Media;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;

/**
 * Panel used to maintain the Media Library.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class MediaLibraryPanel extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {
	/** The version id */
	private static final long serialVersionUID = -5811856651322928169L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MediaLibraryPanel.class);
	
	/** Image support */
	private static final boolean IMAGES_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.IMAGE);
	
	/** Video support */
	private static final boolean VIDEOS_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.VIDEO);
	
	/** Audio support */
	private static final boolean AUDIO_SUPPORTED = MediaLibrary.isMediaSupported(MediaType.AUDIO);
	
	// data
	
	/** True if media was added or removed */
	private boolean mediaLibraryUpdated;
	
	// controls
	
	/** The remove the selected media button */
	private JButton btnRemoveMedia;
	
	/** The tabs for the media types */
	private JTabbedPane mediaTabs;
	
	/** The image media list */
	private JList<MediaThumbnail> lstImages;
	
	/** The video media list */
	private JList<MediaThumbnail> lstVideos;
	
	/** The audio media list */
	private JList<MediaThumbnail> lstAudio;
	
	/** The media properties panel */
	private MediaPropertiesPanel pnlProperties;
	
	/**
	 * Default constructor.
	 */
	public MediaLibraryPanel() {
		this.mediaLibraryUpdated = false;
		
		this.mediaTabs = new JTabbedPane();
		
		MediaLibrary library = null;
		List<MediaThumbnail> empty = new ArrayList<MediaThumbnail>();
		try {
			library = MediaLibrary.getInstance();
		} catch (NotInitializedException ex) {
			LOGGER.error(ex);
		}
		
		// make sure the media library supports the media
		if (IMAGES_SUPPORTED) {
			// load up all the thumbnails for the media in the media library
			List<MediaThumbnail> images = library != null ? library.getThumbnails(MediaType.IMAGE) : empty;
			this.lstImages = MediaUI.createJList(images);
			this.lstImages.addListSelectionListener(this);
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.images"), new JScrollPane(this.lstImages));
		}
		
		// make sure the media library supports the media
		if (VIDEOS_SUPPORTED) {
			List<MediaThumbnail> videos = library != null ? library.getThumbnails(MediaType.VIDEO) : empty;
			this.lstVideos = MediaUI.createJList(videos);
			this.lstVideos.addListSelectionListener(this);
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.videos"), new JScrollPane(this.lstVideos));
		}
		
		// make sure the media library supports the media
		if (AUDIO_SUPPORTED) {
			List<MediaThumbnail> audio = library != null ? library.getThumbnails(MediaType.AUDIO) : empty;
			this.lstAudio = MediaUI.createJList(audio);
			this.lstAudio.addListSelectionListener(this);
			this.mediaTabs.addTab(Messages.getString("panel.media.tabs.audio"), new JScrollPane(this.lstAudio));
		}
		
		this.mediaTabs.addChangeListener(this);
		this.mediaTabs.setMinimumSize(new Dimension(120, 120));
		this.mediaTabs.setPreferredSize(new Dimension(500, 500));
		
		this.pnlProperties = new MediaPropertiesPanel();
		this.pnlProperties.setMinimumSize(new Dimension(300, 0));
		
		// add a button to add media
		JButton btnAddMedia = new JButton(Messages.getString("panel.media.add"));
		btnAddMedia.setActionCommand("addMedia");
		btnAddMedia.addActionListener(this);
		btnAddMedia.setMinimumSize(new Dimension(0, 50));
		btnAddMedia.setFont(btnAddMedia.getFont().deriveFont(Font.BOLD, btnAddMedia.getFont().getSize2D() + 2.0f));
		
		this.btnRemoveMedia = new JButton(Messages.getString("panel.media.remove"));
		this.btnRemoveMedia.setActionCommand("removeMedia");
		this.btnRemoveMedia.addActionListener(this);
		this.btnRemoveMedia.setEnabled(false);
		
		JPanel pnlRight = new JPanel();
		GroupLayout layout = new GroupLayout(pnlRight);
		pnlRight.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(btnAddMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(this.pnlProperties)
				.addComponent(this.btnRemoveMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(btnAddMedia)
				.addGap(10, 10, 10)
				.addComponent(this.pnlProperties, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.btnRemoveMedia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.mediaTabs, pnlRight);
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstImages) {
				MediaThumbnail thumbnail = this.lstImages.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				}
			} else if (source == this.lstVideos) {
				MediaThumbnail thumbnail = this.lstVideos.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				}
			} else if (source == this.lstAudio) {
				MediaThumbnail thumbnail = this.lstAudio.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.mediaTabs) {
			int index = this.mediaTabs.getSelectedIndex();
			if (index == 0) {
				MediaThumbnail thumbnail = this.lstImages.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				} else {
					this.pnlProperties.setMediaFile(null);
					this.btnRemoveMedia.setEnabled(false);
				}
			} else if (index == 1) {
				MediaThumbnail thumbnail = this.lstVideos.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				} else {
					this.pnlProperties.setMediaFile(null);
					this.btnRemoveMedia.setEnabled(false);
				}
			} else if (index == 2) {
				MediaThumbnail thumbnail = this.lstAudio.getSelectedValue();
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					this.pnlProperties.setMediaFile(file);
					this.btnRemoveMedia.setEnabled(true);
				} else {
					this.pnlProperties.setMediaFile(null);
					this.btnRemoveMedia.setEnabled(false);
				}
			}
		}
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
			fc.setMultiSelectionEnabled(true);
			fc.setAcceptAllFileFilterUsed(true);
			// they can only select image files
			FileFilter filter = new ImageFileFilter();
			fc.addChoosableFileFilter(filter);
			if (VIDEOS_SUPPORTED) {
				fc.addChoosableFileFilter(new AllFilesFileFilter(Messages.getString("filter.video.description")));
			}
			if (AUDIO_SUPPORTED) {
				fc.addChoosableFileFilter(new AllFilesFileFilter(Messages.getString("filter.audio.description")));
			}
			// provide a preview for image files
			fc.setAccessory(new MediaFilePreview(fc));
			// set the all the file filter as the default
			fc.setFileFilter(fc.getAcceptAllFileFilter());
			// show the dialog
			int result = fc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				// get the files
				File[] files = fc.getSelectedFiles();
				
				// get all the paths
				String[] paths = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					paths[i] = files[i].getAbsolutePath();
				}
				
				// load up all the selected files
				AddMediaTask task = new AddMediaTask(paths);
				TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.media.addingMedia"), task);
				
				List<String> failed = task.getFailed();
				List<Media> media = task.getMedia();
				
				// update the lists if any worked
				if (failed.size() != paths.length) {
					for (Media m : media) {
						try {
							// get the thumbnail
							MediaThumbnail thumb = MediaLibrary.getInstance().getThumbnail(m);
							// add the thumbnail to the appropriate list
							JList<MediaThumbnail> list = null;
							if (m.getType() == MediaType.IMAGE) {
								list = this.lstImages;
							} else if (m.getType() == MediaType.VIDEO) {
								list = this.lstVideos;
							} else if (m.getType() == MediaType.AUDIO) {
								list = this.lstAudio;
							} else {
								// do nothing
								continue;
							}
							DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)list.getModel();
							model.addElement(thumb);
						} catch (NotInitializedException ex) {
							// just log the error and continue
							LOGGER.error(ex);
						}
						
					}
					this.mediaLibraryUpdated = true;
				}
				
				// show errors if any failed
				if (failed.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for (String path : failed) {
						sb.append(path).append("<br />");
					}
					ExceptionDialog.show(
							this, 
							Messages.getString("panel.media.add.exception.title"), 
							MessageFormat.format(Messages.getString("panel.media.add.exception.text"), sb.toString()), 
							task.getException());
				}
			}
		} else if ("removeMedia".equals(command)) {
			// get the selected media from the currently visible tab
			int index = this.mediaTabs.getSelectedIndex();
			// get the JList to modify
			JList<MediaThumbnail> list = null;
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
			MediaThumbnail thumbnail = list.getSelectedValue();
			// make sure something is selected
			if (thumbnail != null) {
				// remove the media from the media library
				final MediaFile file = thumbnail.getFile();
				
				// make sure the user wants to do this
				int choice = JOptionPane.showConfirmDialog(
						this,
						MessageFormat.format(Messages.getString("panel.media.remove.areYouSure.message"), thumbnail.getFile().getName()),
						Messages.getString("panel.media.remove.areYouSure.title"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				
				if (choice == JOptionPane.YES_OPTION) {
					// create a remove task
					AbstractTask task = new AbstractTask() {
						@Override
						public void run() {
							try {
								MediaLibrary.getInstance().removeMedia(file);
								this.setSuccessful(true);
							} catch (Exception e) {
								this.handleException(e);
							}
						}
					};
					
					TaskProgressDialog.show(WindowUtilities.getParentWindow(this), Messages.getString("panel.media.removingMedia"), task);
					
					if (task.isSuccessful()) {
						// remove the thumbnail from the list
						DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)list.getModel();
						model.removeElement(thumbnail);
						
						this.mediaLibraryUpdated = true;
					} else {
						ExceptionDialog.show(
								this, 
								Messages.getString("panel.media.remove.exception.title"), 
								MessageFormat.format(Messages.getString("panel.media.remove.exception.text"), thumbnail.getFile().getName()), 
								task.getException());
						LOGGER.error("An error occurred while attempting to remove [" + thumbnail.getFile().getRelativePath() + "] from the media library: ", task.getException());
					}
				}
			}
		}
	}
	
	/**
	 * Returns true if media was removed or added to the media library.
	 * @return boolean
	 */
	public boolean isMediaLibraryUpdated() {
		return this.mediaLibraryUpdated;
	}
}
