package org.praisenter.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for any type of file.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class AllFilesFileFilter extends FileFilter {
	/** The filter description */
	protected String description;
	
	/**
	 * Minimal constructor.
	 * @param description the filter description
	 */
	public AllFilesFileFilter(String description) {
		this.description = description;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		// if its a directory then show it
		if (file.isDirectory()) {
			return true;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}
}
