package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;

public interface PathResolver<T extends Persistable> {
	/**
	 * Initializes the location where all items will be stored
	 * @throws IOException
	 */
	public void initialize() throws IOException;

	/**
	 * Returns the base path where all items are stored.
	 * @return Path
	 */
	public Path getBasePath();
	
	/**
	 * Returns the filename for the item: {uuid}.{ext}.
	 * @param item the item
	 * @return Path
	 */
	public Path getFileName(T item);
	
	/**
	 * Returns the relative path for the given item, either {uuid}.{ext}
	 * or {subpath}/{uuid}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getRelativePath(T item);
	
	/**
	 * Returns the path to the item, either {basepath}/{uuid}.{ext} or
	 * {basepath}/{subpath}/{uuid}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getPath(T item);
	
	/**
	 * Returns the base path used when exporting: {exportpath}
	 * @return Path
	 */
	public Path getExportBasePath();
	
	/**
	 * Returns the export path for an item, {exportpath}/{relativepath}/{uuid}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getExportPath(T item);

	// raw
	
	/**
	 * Returns a path to the "raw" item is stored: {exportpath}/{relativepath}/{uuid}.{ext}
	 * <p>
	 * The "raw" item is the pure file that represents the item.  In the case of Bibles, song lyrics,
	 * slides, it's the JSON file, but in the case of media it's the actual media file, the video,
	 * image or audio.
	 * @param item the item
	 * @return Path
	 */
	public Path getRawPath(T item);
	
	// friendly
	
	/**
	 * Returns a friendly file name for the item: {name}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getFriendlyFileName(T item);

	/**
	 * Returns the relative path using the friendly name: {relativepath}/{name}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getFriendlyRelativePath(T item);
	
	/**
	 * Returns the export path for an item using the friendly name, {exportpath}/{relativepath}/{name}.{ext}
	 * @param item the item
	 * @return Path
	 */
	public Path getFriendlyExportPath(T item);
}
