package org.praisenter.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

public interface ImportExportProvider<T extends Persistable> {
	/**
	 * Determines if the given file path is supported by this format provider.
	 * @param path a path to a file
	 * @return boolean
	 */
	public boolean isSupported(Path path);
	
	/**
	 * Determines if the given mimetype is supported by this format provider.
	 * @param mimeType the file/stream mimetype
	 * @return boolean
	 */
	public boolean isSupported(String mimeType);
	
	/**
	 * Determines if the given stream (and file/entry name) is supported by this format provider.
	 * <p>
	 * This method guarantees that the stream is reset to the initial position before returning.
	 * @param name the file, stream, or entry name
	 * @param stream the stream
	 * @return boolean
	 * @throws IOException
	 */
	public boolean isSupported(String name, InputStream stream) throws IOException;
	
	/**
	 * Exports the given item to the given path.
	 * <p>
	 * This method assumes the target path will only be this item.
	 * @param adapter the persist adapter
	 * @param path the target path
	 * @param data the data to write
	 * @throws IOException
	 */
	public void exp(PersistAdapter<T> adapter, Path path, T data) throws IOException;
	
	/**
	 * Exports the given item to the given stream.
	 * <p>
	 * This method assumes the target stream will only contain this item.
	 * @param adapter the persist adapter
	 * @param stream the target stream
	 * @param data the data to write
	 * @throws IOException
	 */
	public void exp(PersistAdapter<T> adapter, OutputStream stream, T data) throws IOException;
	
	/**
	 * Exports the given item to the given zip stream.
	 * @param adapter the persist adapter
	 * @param stream the target zip stream
	 * @param data the data to write
	 * @throws IOException
	 */
	public void exp(PersistAdapter<T> adapter, ZipOutputStream stream, T data) throws IOException;
	
	/**
	 * Imports the file at the given path.
	 * <p>
	 * This method will only be called if {@link #isSupported(Path))} returns true.
	 * @param adapter the persist adapter to import the item to
	 * @param path the source file path
	 * @return {@link DataImportResult}
	 * @throws IOException
	 */
	public DataImportResult<T> imp(PersistAdapter<T> adapter, Path path) throws IOException;
}
