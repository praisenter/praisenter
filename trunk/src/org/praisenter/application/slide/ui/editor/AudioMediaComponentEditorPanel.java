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
package org.praisenter.application.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.application.media.ui.MediaLibraryDialog;
import org.praisenter.application.media.ui.MediaThumbnailListCellRenderer;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;
import org.praisenter.slide.media.AudioMediaComponent;

/**
 * Editor panel for {@link AudioMediaComponent}s.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.0
 */
public class AudioMediaComponentEditorPanel  extends SlideComponentEditorPanel<AudioMediaComponent> implements ListSelectionListener, ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 1742777929088652998L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(AudioMediaComponentEditorPanel.class);
	
	/** The audio label */
	private JLabel lblAudio;
	
	/** The audio media list */
	private JList<MediaThumbnail> lstAudio;
	
	/** The checkbox for muting the audio */
	private JCheckBox chkAudioMuted;
	
	/** The checkbox for looping the media */
	private JCheckBox chkLooped;
	
	/** The button to manage the media library */
	private JButton btnManageMedia;
	
	/**
	 * Default constructor.
	 */
	public AudioMediaComponentEditorPanel() {
		this.lblAudio = new JLabel(Messages.getString("panel.slide.editor.audio"));
		
		// load up all the thumbnails for the media in the media library
		List<MediaThumbnail> audio = null;
		try {
			audio = MediaLibrary.getInstance().getThumbnails(MediaType.AUDIO);
		} catch (NotInitializedException e) {
			audio = new ArrayList<MediaThumbnail>();
		}
		this.lstAudio = this.createJList(audio);
		this.lstAudio.addListSelectionListener(this);

		this.chkAudioMuted = new JCheckBox(Messages.getString("panel.slide.editor.media.muteAudio"));
		this.chkAudioMuted.setToolTipText(Messages.getString("panel.slide.editor.media.muteAudio.tooltip"));
		this.chkAudioMuted.addChangeListener(this);
		
		this.chkLooped = new JCheckBox(Messages.getString("panel.slide.editor.media.loop"));
		this.chkLooped.setToolTipText(Messages.getString("panel.slide.editor.media.loop.tooltip"));
		this.chkLooped.addChangeListener(this);
		
		this.btnManageMedia = new JButton(Messages.getString("panel.slide.editor.mediaLibrary"));
		this.btnManageMedia.addActionListener(this);
		this.btnManageMedia.setActionCommand("media-library");
		
		this.createLayout();
	}

	/**
	 * Creates a new JList for the given list of {@link MediaThumbnail}s.
	 * @param thumbnails the list of thumbnails
	 * @return JList
	 */
	private JList<MediaThumbnail> createJList(List<MediaThumbnail> thumbnails) {
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
	
	/**
	 * Creates the layout for a generic slide component.
	 */
	protected void createLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstAudio);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblName)
								.addComponent(this.lblAudio))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtName)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.chkLooped)
										.addComponent(this.chkAudioMuted))))
				.addComponent(pane)
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.lblAudio)
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(pane)
				.addComponent(this.btnManageMedia));
	}
	
	/**
	 * Sets the layout of the media panel on the given panel.
	 * @param panel the panel
	 */
	protected void createMediaLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstAudio);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pane)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pane)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.chkLooped)
						.addComponent(this.chkAudioMuted))
				.addComponent(this.btnManageMedia));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			Object source = e.getSource();
			if (source == this.lstAudio) {
				MediaThumbnail thumbnail = this.lstAudio.getSelectedValue();
				// make sure there is a selected value
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					
					if (this.slideComponent != null) {
						try {
							AbstractAudioMedia media = (AbstractAudioMedia)MediaLibrary.getInstance().getMedia(file);
							this.slideComponent.setMedia(media);
							this.notifyEditorListeners();
						} catch (Exception ex) {
							LOGGER.error(ex);
						}
					}
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
		if (source == this.chkAudioMuted) {
			boolean flag = this.chkAudioMuted.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setAudioMuted(flag);
				this.notifyEditorListeners();
			}
		} else if (source == this.chkLooped) {
			boolean flag = this.chkLooped.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setLoopEnabled(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("media-library".equals(command)) {
			boolean mediaLibraryUpdated = MediaLibraryDialog.show(WindowUtilities.getParentWindow(this));
			
			// only update the list if media was added or removed
			if (mediaLibraryUpdated) {
				// when control returns here we need to update the items in the jlist with the current media library items
				List<MediaThumbnail> thumbnails = null;
				try {
					thumbnails = MediaLibrary.getInstance().getThumbnails(MediaType.AUDIO);
				} catch (NotInitializedException ex) {
					thumbnails = new ArrayList<MediaThumbnail>();
				}
				// save the selected value
				MediaThumbnail thumb = this.lstAudio.getSelectedValue();
				this.lstAudio.clearSelection();
				DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)this.lstAudio.getModel();
				model.removeAllElements();
				for (MediaThumbnail thumbnail : thumbnails) {
					model.addElement(thumbnail);
				}
				this.lstAudio.setSelectedValue(thumb, true);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#setSlideComponent(org.praisenter.slide.SlideComponent, boolean)
	 */
	@Override
	public void setSlideComponent(AudioMediaComponent slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		this.disableNotification();
		if (slideComponent != null) {
			if (slideComponent.getMedia() != null) {
				try {
					MediaThumbnail thumbnail = MediaLibrary.getInstance().getThumbnail(slideComponent.getMedia());
					if (thumbnail != null) {
						this.lstAudio.setSelectedValue(thumbnail, true);
					}
				} catch (NotInitializedException e) {
					LOGGER.error(e);
				}
			}
			this.chkAudioMuted.setSelected(slideComponent.isAudioMuted());
			this.chkLooped.setSelected(slideComponent.isLoopEnabled());
		} else {
			this.lstAudio.clearSelection();
			this.chkAudioMuted.setSelected(false);
			this.chkLooped.setSelected(false);
		}
		this.enableNotification();
	}
}
