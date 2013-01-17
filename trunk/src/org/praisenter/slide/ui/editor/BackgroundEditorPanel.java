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
package org.praisenter.slide.ui.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.resources.Messages;
import org.praisenter.slide.EmptyRenderableComponent;
import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;

/**
 * The panel used to modify the background of a slide.
 * <p>
 * This panel allows the configuration of a paint/image/video background.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class BackgroundEditorPanel extends RenderableComponentEditorPanel<RenderableComponent> implements ItemListener, EditorListener {
	/** The version id */
	private static final long serialVersionUID = -1449249027195041598L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(BackgroundEditorPanel.class);
	
	/** The paint card */
	private static final String GENERIC_CARD = "Generic";
	
	/** The image card */
	private static final String IMAGE_CARD = "Image";
	
	/** The video card */
	private static final String VIDEO_CARD = "Video";
	
	/** The empty card */
	private static final String EMPTY_CARD = "Empty";

	// data
	
	/** The configured empty component */
	private EmptyRenderableComponent emptyComponent;
	
	/** The configured paint component */
	private GenericComponent genericComponent;
	
	/** The configured image component */
	private ImageMediaComponent imageComponent;
	
	/** The configured video component */
	private VideoMediaComponent videoComponent;
	
	// controls

	/** The background type combo box */
	private JComboBox<BackgroundType> cmbBackgroundType; 
	
	/** The paint background type panel */
	private GenericComponentEditorPanel<GenericComponent> pnlGeneric;
	
	/** The image background type panel */
	private ImageMediaComponentEditorPanel pnlImage;
	
	/** The video background type panel */
	private VideoMediaComponentEditorPanel pnlVideo;
	
	/** The panel for the background types */
	private JPanel pnlCards;
	
	/** The layout for the background types */
	private CardLayout layCards;
	
	/**
	 * Default constructor.
	 */
	public BackgroundEditorPanel() {
		this.cmbBackgroundType = new JComboBox<BackgroundType>(BackgroundType.values());
		this.cmbBackgroundType.setToolTipText(Messages.getString("panel.slide.editor.background.type.tooltip"));
		this.cmbBackgroundType.addItemListener(this);
		this.cmbBackgroundType.setRenderer(new BackgroundTypeListCellRenderer());
		JPanel pnlPadding = new JPanel();
		pnlPadding.setLayout(new BorderLayout());
		pnlPadding.add(this.cmbBackgroundType, BorderLayout.CENTER);
		pnlPadding.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		
		this.pnlGeneric = new GenericComponentEditorPanel<GenericComponent>();
		this.pnlGeneric.addEditorListener(this);
		
		this.pnlImage = new ImageMediaComponentEditorPanel();
		this.pnlImage.addEditorListener(this);
		
		this.pnlVideo = new VideoMediaComponentEditorPanel();
		this.pnlVideo.addEditorListener(this);
		
		JPanel pnlEmpty = new JPanel();
		
		this.pnlCards = new JPanel();
		this.layCards = new CardLayout();
		
		this.pnlCards.setLayout(this.layCards);
		this.pnlCards.add(this.pnlGeneric, GENERIC_CARD);
		this.pnlCards.add(this.pnlImage, IMAGE_CARD);
		this.pnlCards.add(this.pnlVideo, VIDEO_CARD);
		this.pnlCards.add(pnlEmpty, EMPTY_CARD);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(pnlPadding)
				.addComponent(this.pnlCards));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(pnlPadding, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.pnlCards));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.EditorListener#editPerformed(org.praisenter.slide.ui.editor.EditEvent)
	 */
	@Override
	public void editPerformed(EditEvent event) {
		// just pass this event through
		this.notifyEditorListeners();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == this.cmbBackgroundType) {
			Object item = e.getItem();
			if (item == BackgroundType.PAINT) {
				this.layCards.show(this.pnlCards, GENERIC_CARD);
			} else if (item == BackgroundType.IMAGE) {
				this.layCards.show(this.pnlCards, IMAGE_CARD);
			} else if (item == BackgroundType.VIDEO) {
				this.layCards.show(this.pnlCards, VIDEO_CARD);
			} else {
				this.layCards.show(this.pnlCards, EMPTY_CARD);
			}
			this.notifyEditorListeners();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.RenderableComponentEditorPanel#setSlideComponent(org.praisenter.slide.RenderableComponent, boolean)
	 */
	@Override
	public void setSlideComponent(RenderableComponent slideComponent, boolean isStatic) {
		super.setSlideComponent(slideComponent, isStatic);
		
		if (slideComponent != null) {
			this.cmbBackgroundType.setEnabled(true);
			
			String name = slideComponent.getName();
			int w = slideComponent.getWidth();
			int h = slideComponent.getHeight();
			if (slideComponent instanceof ImageMediaComponent) {
				this.emptyComponent = new EmptyRenderableComponent(name, w, h);
				this.genericComponent = new GenericComponent(name, w, h);
				this.imageComponent = (ImageMediaComponent)slideComponent;
				this.videoComponent = new VideoMediaComponent(name, null, w, h);
				
				this.cmbBackgroundType.setSelectedItem(BackgroundType.IMAGE);
				this.layCards.show(this.pnlCards, IMAGE_CARD);
			} else if (slideComponent instanceof VideoMediaComponent) {
				this.emptyComponent = new EmptyRenderableComponent(name, w, h);
				this.genericComponent = new GenericComponent(name, w, h);
				this.imageComponent = new ImageMediaComponent(name, null, w, h);
				this.videoComponent = (VideoMediaComponent)slideComponent;
				
				this.pnlVideo.setSlideComponent((VideoMediaComponent)slideComponent, isStatic);
				this.cmbBackgroundType.setSelectedItem(BackgroundType.VIDEO);
				this.layCards.show(this.pnlCards, VIDEO_CARD);
			} else if (slideComponent instanceof GenericComponent) {
				this.emptyComponent = new EmptyRenderableComponent(name, w, h);
				this.genericComponent = (GenericComponent)slideComponent;
				this.videoComponent = new VideoMediaComponent(name, null, w, h);
				this.imageComponent = new ImageMediaComponent(name, null, w, h);
				
				this.pnlGeneric.setSlideComponent((GenericComponent)slideComponent, isStatic);
				this.cmbBackgroundType.setSelectedItem(BackgroundType.PAINT);
				this.layCards.show(this.pnlCards, GENERIC_CARD);
			} else if (slideComponent instanceof EmptyRenderableComponent) {
				this.emptyComponent = (EmptyRenderableComponent)slideComponent;
				this.genericComponent = new GenericComponent(name, w, h);
				this.videoComponent = new VideoMediaComponent(name, null, w, h);
				this.imageComponent = new ImageMediaComponent(name, null, w, h);
				
				this.cmbBackgroundType.setSelectedItem(BackgroundType.NONE);
				this.layCards.show(this.pnlCards, EMPTY_CARD);
			} else {
				// assume its null and log an error
				this.emptyComponent = null;
				this.imageComponent = null;
				this.videoComponent = null;
				this.genericComponent = null;
				
				this.cmbBackgroundType.setSelectedItem(BackgroundType.NONE);
				this.layCards.show(this.pnlCards, EMPTY_CARD);
				this.cmbBackgroundType.setEnabled(false);
				
				LOGGER.error("Unknown component type: [" + slideComponent.getClass().getName() + "].");
			}
			
			this.pnlGeneric.setSlideComponent(this.genericComponent, isStatic);
			this.pnlImage.setSlideComponent(this.imageComponent, isStatic);
			this.pnlVideo.setSlideComponent(this.videoComponent, isStatic);
		} else {
			this.emptyComponent = null;
			this.imageComponent = null;
			this.videoComponent = null;
			this.genericComponent = null;
			
			this.cmbBackgroundType.setSelectedItem(BackgroundType.NONE);
			this.layCards.show(this.pnlCards, EMPTY_CARD);
			this.cmbBackgroundType.setEnabled(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.editor.SlideComponentEditorPanel#getSlideComponent()
	 */
	@Override
	public RenderableComponent getSlideComponent() {
		RenderableComponent oldBackground = this.slideComponent;
		RenderableComponent newBackground = null;
		
		if (this.cmbBackgroundType.getSelectedItem() == BackgroundType.PAINT) {
			newBackground = this.genericComponent;
		} else if (this.cmbBackgroundType.getSelectedItem() == BackgroundType.IMAGE) {
			newBackground = this.imageComponent;
		} else if (this.cmbBackgroundType.getSelectedItem() == BackgroundType.VIDEO) {
			newBackground = this.videoComponent;
		} else {
			newBackground = this.emptyComponent;
		}
		
		if (newBackground == null) {
			return this.slideComponent;
		}
		
		// we need to use the size of the component that was in the slide
		// just in case the slide size is changed
		newBackground.setWidth(oldBackground.getWidth());
		newBackground.setHeight(oldBackground.getHeight());
		return newBackground;
	}
}
