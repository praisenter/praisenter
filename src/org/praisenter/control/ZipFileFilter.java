package org.praisenter.control;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.praisenter.resources.Messages;
import org.praisenter.utilities.FileUtilities;

/**
 * File filter for supported images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ZipFileFilter extends FileFilter {
	/** ZIP extension */
	private static final String ZIP = "zip";
	
	/** Array of supported extensions */
	private static final String[] FORMATS = new String[] { ZIP };
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		// if its a directory then show it
		if (file.isDirectory()) {
			return true;
		}
		// otherwise check the extension
		String extension = FileUtilities.getExtension(file);
		if (extension != null) {
			for (String format : ZipFileFilter.FORMATS) {
				if (format.equalsIgnoreCase(extension)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.getString("filter.zip.description");
	}
}
