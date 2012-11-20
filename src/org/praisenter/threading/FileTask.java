package org.praisenter.threading;

import java.io.File;

/**
 * Represents a task involving a file (importing/exporting/saving/etc).
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FileTask extends AbstractTask {
	/** The file to import */
	private File file;
	
	/**
	 * Minimal constructor.
	 * @param file the file
	 */
	public FileTask(File file) {
		this.file = file;
	}

	/**
	 * Returns the file to import.
	 * @return File
	 */
	public File getFile() {
		return this.file;
	}
}
