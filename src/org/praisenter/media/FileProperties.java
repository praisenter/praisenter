package org.praisenter.media;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Generic file properties class.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "FileProperties")
public class FileProperties {
	/** The file path and name */
	@XmlAttribute(name = "FilePath")
	protected String filePath;
	
	/** The file name */
	@XmlAttribute(name = "FileName")
	protected String fileName;
	
	/** The file size in bytes */
	@XmlAttribute(name = "FileSize")
	protected long fileSize;
	
	/**
	 * Default constructor.
	 */
	protected FileProperties() {}
	
	/**
	 * Full constructor.
	 * @param filePath the file path and name
	 * @param fileName the file name
	 * @param fileSize the file size in bytes
	 */
	public FileProperties(String filePath, String fileName, long fileSize) {
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
	}
	
	/**
	 * Returns a new {@link FileProperties} object for the given file name and path.
	 * @param filePath the file name and path
	 * @return {@link FileProperties}
	 */
	public static final FileProperties getFileProperties(String filePath) {
		Path path = FileSystems.getDefault().getPath(filePath);
		long size = 0;
		try {
			size = Files.size(path);
		} catch (IOException ex) {
			// FIXME handle error or just ignore this?
		}
		String name = path.getFileName().toString();
		return new FileProperties(filePath, name, size);
	}
	
	/**
	 * Returns the file path and name.
	 * @return String
	 */
	public String getFilePath() {
		return this.filePath;
	}
	
	/**
	 * Returns the file name of the media.
	 * @return String
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Returns the file size in bytes.
	 * @return long
	 */
	public long getFileSize() {
		return this.fileSize;
	}
}
