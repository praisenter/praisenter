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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.praisenter.application.media.ui.MediaLibraryDialog;
import org.praisenter.application.media.ui.MediaUI;
import org.praisenter.application.resources.Messages;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.WindowUtilities;
import org.praisenter.media.ImageMedia;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnail;
import org.praisenter.media.MediaType;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.media.ImageMediaComponent;

/**
 * Editor panel for {@link ImageMediaComponent}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ImageMediaComponentEditorPanel  extends PositionedComponentEditor<ImageMediaComponent> implements ListSelectionListener, ItemListener, ChangeListener, ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1702500646124912915L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(ImageMediaComponentEditorPanel.class);
	
	/** The image label */
	private JLabel lblImage;
	
	/** The scale type label */
	private JLabel lblScaleType;

	/** The image visibility checkbox */
	private JCheckBox chkImageVisible;
	
	/** The image media list */
	private JList<MediaThumbnail> lstImages;
	
	/** The scale type combo box */
	private JComboBox<ScaleType> cmbScaleType;
	
	/** The button to manage the media library */
	private JButton btnManageMedia;
	
	/**
	 * Default constructor.
	 */
	public ImageMediaComponentEditorPanel() {
		super(false);
		
		this.lblImage = new JLabel(Messages.getString("panel.slide.editor.image"));
		
		this.chkImageVisible = new JCheckBox(Messages.getString("panel.slide.editor.visible"));
		this.chkImageVisible.addChangeListener(this);
		
		// load up all the thumbnails for the media in the media library
		List<MediaThumbnail> images = null;
		try {
			images = MediaLibrary.getInstance().getThumbnails(MediaType.IMAGE);
		} catch (NotInitializedException ex) {
			images = new ArrayList<MediaThumbnail>();
		}
		this.lstImages = MediaUI.createJList(images);
		this.lstImages.addListSelectionListener(this);
		
		this.lblScaleType = new JLabel(Messages.getString("panel.slide.editor.image.scaleType"));
		this.cmbScaleType = new JComboBox<ScaleType>(ScaleType.values());
		this.cmbScaleType.setRenderer(new ScaleTypeListCellRenderer());
		this.cmbScaleType.addItemListener(this);
		
		this.btnManageMedia = new JButton(Messages.getString("panel.slide.editor.mediaLibrary"));
		this.btnManageMedia.addActionListener(this);
		this.btnManageMedia.setActionCommand("media-library");
		
		this.createLayout();
	}

	/**
	 * Creates the layout for an image media component.
	 */
	protected void createLayout() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JScrollPane pane = new JScrollPane(this.lstImages);
		pane.setPreferredSize(new Dimension(200, 150));
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblName)
								.addComponent(this.lblBackground)
								.addComponent(this.lblBorder)
								.addComponent(this.lblScaleType)
								.addComponent(this.lblImage))
						.addGroup(layout.createParallelGroup()
								.addComponent(this.txtName)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.btnBackgroundFill)
										.addComponent(this.chkBackgroundVisible))
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.btnBorderFill)
										.addComponent(this.btnBorderStyle)
										.addComponent(this.chkBorderVisible))
								.addComponent(this.cmbScaleType)
								.addComponent(this.chkImageVisible)))
				.addComponent(pane)
				.addComponent(this.btnManageMedia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblName)
						.addComponent(this.txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBackground)
						.addComponent(this.btnBackgroundFill)
						.addComponent(this.chkBackgroundVisible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblBorder)
						.addComponent(this.btnBorderFill)
						.addComponent(this.btnBorderStyle)
						.addComponent(this.chkBorderVisible))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblScaleType)
						.addComponent(this.cmbScaleType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblImage)
						.addComponent(this.chkImageVisible))
				.addComponent(pane)
				.addComponent(this.btnManageMedia));
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
				// make sure there is a selected value
				if (thumbnail != null) {
					MediaFile file = thumbnail.getFile();
					
					if (this.slideComponent != null) {
						try {
							ImageMedia media = (ImageMedia)MediaLibrary.getInstance().getMedia(file);
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
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object source = e.getSource();
			if (source == this.cmbScaleType) {
				if (this.slideComponent != null) {
					this.slideComponent.setScaleType((ScaleType)e.getItem());
					this.notifyEditorListeners();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericComponentEditorPanel#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		super.stateChanged(e);
		
		Object source = e.getSource();
		if (source == this.chkImageVisible) {
			boolean flag = this.chkImageVisible.isSelected();
			if (this.slideComponent != null) {
				this.slideComponent.setImageVisible(flag);
				this.notifyEditorListeners();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		
		String command = e.getActionCommand();
		if ("media-library".equals(command)) {
			boolean mediaLibraryUpdated = MediaLibraryDialog.show(WindowUtilities.getParentWindow(this));
			
			// only update the list if media was added or removed
			if (mediaLibraryUpdated) {
				// when control returns here we need to update the items in the jlist with the current media library items
				List<MediaThumbnail> thumbnails = null;
				try {
					thumbnails = MediaLibrary.getInstance().getThumbnails(MediaType.IMAGE);
				} catch (NotInitializedException ex) {
					thumbnails = new ArrayList<MediaThumbnail>();
				}
				// save the selected value
				MediaThumbnail thumb = this.lstImages.getSelectedValue();
				this.lstImages.clearSelection();
				DefaultListModel<MediaThumbnail> model = (DefaultListModel<MediaThumbnail>)this.lstImages.getModel();
				model.removeAllElements();
				for (MediaThumbnail thumbnail : thumbnails) {
					model.addElement(thumbnail);
				}
				this.lstImages.setSelectedValue(thumb, true);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.GenericComponentEditorPanel#setSlideComponent(org.praisenter.slide.GenericComponent, boolean)
	 */
	@Override
	public void setSlideComponent(ImageMediaComponent slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		if (slideComponent != null) {
			if (slideComponent.getMedia() != null) {
				try {
					MediaThumbnail thumbnail = MediaLibrary.getInstance().getThumbnail(slideComponent.getMedia());
					if (thumbnail != null) {
						this.lstImages.setSelectedValue(thumbnail, true);
					}
				} catch (NotInitializedException ex) {
					LOGGER.error(ex);
				}
			}
			this.cmbScaleType.setSelectedItem(slideComponent.getScaleType());
			this.chkImageVisible.setSelected(slideComponent.isImageVisible());
		} else {
			this.lstImages.clearSelection();
			this.cmbScaleType.setSelectedIndex(0);
			this.chkImageVisible.setSelected(false);
		}
	}
}
