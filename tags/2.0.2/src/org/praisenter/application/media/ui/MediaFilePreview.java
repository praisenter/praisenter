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

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.application.Constants;
import org.praisenter.application.resources.Messages;
import org.praisenter.application.ui.ImagePanel;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.media.Media;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaLoader;
import org.praisenter.media.MediaType;

/**
 * Represents an image preview panel for a JFileChooser.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class MediaFilePreview extends JPanel implements PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = -2940127890885508444L;
	
	/** Static logger */
	private static final Logger LOGGER = Logger.getLogger(MediaFilePreview.class);
	
	/** The panel to show the image */
	private ImagePanel pnlImage;
	
	/**
	 * Minimal constructor.
	 * @param fileChooser the file chooser
	 */
    public MediaFilePreview(JFileChooser fileChooser) {
    	this(fileChooser, 100, 100);
    }
    
    /**
     * Full constructor.
     * @param fileChooser the file chooser
     * @param width the preview width
     * @param height the preview height
     */
    public MediaFilePreview(JFileChooser fileChooser, int width, int height) {
    	fileChooser.addPropertyChangeListener(this);
    	
    	// the preview label
    	JLabel lblPreview = new JLabel(Messages.getString("preview"));
    	lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
    	this.pnlImage = new ImagePanel();
    	this.pnlImage.setPreferredSize(new Dimension(width, height));
    	this.pnlImage.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
    	this.pnlImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(lblPreview);
        this.add(this.pnlImage);
    }
    
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        // check what the user clicked on
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
        	// if they clicked on a directory then clear the image
            this.pnlImage.setImage(null);
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
        	// otherwise get the file and create a thumbnail
            File file = (File) e.getNewValue();
            // file will be null for folders
            if (file != null) {
	            // check the file type
	            MediaType type = MediaLibrary.getMediaType(file.getAbsolutePath());
	            // check if the type was known and its not audio (since there's no
	            // preview to show for audio media)
	            if (type != null && type != MediaType.AUDIO) {
	            	// get a media loader for the type
	            	List<MediaLoader<?>> loaders = MediaLibrary.getMediaLoaders(type);
	            	// make sure there is a loader for the type
	            	if (loaders != null && loaders.size() > 0) {
	            		for (MediaLoader<?> loader : loaders) {
			            	try {
			            		// the base path parameter is only for loading it into the media library
								Media media = loader.load(Constants.BASE_PATH, file.getAbsolutePath());
								// make sure the media was loaded
								if (media != null) {
									// check the type an assign the preview image
									if (media instanceof ImageMedia) {
										this.pnlImage.setImage(((ImageMedia)media).getImage());
									} else if (media instanceof AbstractVideoMedia) {
										this.pnlImage.setImage(((AbstractVideoMedia)media).getFirstFrame());
									}
									// once one of the loaders works, then return
									return;
								}
							} catch (MediaException ex) {
								LOGGER.warn("MediaLoader [" + loader.getClass().getName() + "] failed to load the media [" + file.getAbsolutePath() + "] due to: ", ex);
							}
	            		}
	            	} else {
	            		LOGGER.warn("No MediaLoader for type [" + type + "].");
	            	}
	            }
            }
            this.pnlImage.setImage(null);
        }
    }
}
