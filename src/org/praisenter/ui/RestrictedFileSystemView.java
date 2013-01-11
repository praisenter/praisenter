package org.praisenter.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * A file system view in which the given file (directory) is the root directory.
 * <p>
 * This class is intended to restrict the user to the given directory.
 * <p>
 * Some additional configuration is required by the the JFileChooser to ensure the
 * user cannot go up or naviate to directories.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class RestrictedFileSystemView extends FileSystemView {
	/** The directory the user is restricted to */
	private File root;
	
	/**
	 * Full constructor.
	 * @param root the directory the user is restricted to
	 */
	public RestrictedFileSystemView(File root) {
		super();
		this.root = root;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileSystemView#createNewFolder(java.io.File)
	 */
	@Override
	public File createNewFolder(File containingDir) throws IOException {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileSystemView#getDefaultDirectory()
	 */
	@Override
	public File getDefaultDirectory() {
		return this.root;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileSystemView#getHomeDirectory()
	 */
	@Override
	public File getHomeDirectory() {
		return this.root;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileSystemView#getRoots()
	 */
	@Override
	public File[] getRoots() {
		return new File[] { this.root };
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileSystemView#isRoot(java.io.File)
	 */
	@Override
	public boolean isRoot(File f) {
		if (this.root.equals(f)) {
			return true;
		}
		return super.isRoot(f);
	}
}
