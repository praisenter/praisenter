package org.praisenter.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.praisenter.resources.Messages;

/**
 * Represents an image preview panel for a JFileChooser.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageFilePreview extends JPanel implements PropertyChangeListener {
	/** The version id */
	private static final long serialVersionUID = -2940127890885508444L;
	
	/** Static logger */
	private static final Logger LOGGER = Logger.getLogger(ImageFilePreview.class);
	
	/** The panel to show the image */
	private ImagePanel pnlImage;
	
	/**
	 * Minimal constructor.
	 * @param fileChooser the file chooser
	 */
    public ImageFilePreview(JFileChooser fileChooser) {
    	this(fileChooser, 100, 100);
    }
    
    /**
     * Full constructor.
     * @param fileChooser the file chooser
     * @param width the preview width
     * @param height the preview height
     */
    public ImageFilePreview(JFileChooser fileChooser, int width, int height) {
    	fileChooser.addPropertyChangeListener(this);
    	
    	// the preview label
    	JLabel lblPreview = new JLabel(Messages.getString("panel.image.preview"));
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
            if (file != null) {
				try {
					BufferedImage image = ImageIO.read(file);
					this.pnlImage.setImage(image);
				} catch (IOException ex) {
					LOGGER.warn("Generation of the preview in the image selector failed: ", ex);
				}
            } else {
            	this.pnlImage.setImage(null);
            }
        }
    }
}
