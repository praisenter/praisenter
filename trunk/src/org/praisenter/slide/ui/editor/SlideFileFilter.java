package org.praisenter.slide.ui.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.praisenter.Constants;
import org.praisenter.resources.Messages;
import org.praisenter.utilities.FileUtilities;

/**
 * File filter for slide and template files.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideFileFilter extends FileFilter {
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		// if its a directory then don't show it
		if (file.isDirectory()) {
			return false;
		}
		
		// check for the thumbnails file
		if (file.getName().toLowerCase().matches(Constants.THUMBNAIL_FILE)) {
			return false;
		}
		
		// otherwise check the extension
		String extension = FileUtilities.getExtension(file);
		if (extension != null) {
			if ("xml".equalsIgnoreCase(extension)) {
				return true;
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.getString("panel.slide.file.filter");
	}
}