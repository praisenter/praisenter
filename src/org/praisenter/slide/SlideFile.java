package org.praisenter.slide;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * Generic slide file information class.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "SlideFile")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideFile {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideFile.class);
	
	/** Number to represent unknown file size */
	private static final int UNKNOWN_FILE_SIZE = -1;
	
	/** The file path and name */
	@XmlAttribute(name = "Path")
	protected String path;
	
	/** The file name */
	@XmlAttribute(name = "Name")
	protected String name;
	
	/** The file size in bytes */
	@XmlAttribute(name = "Size")
	protected long size;
	
	/**
	 * Default constructor.
	 * <p>
	 * Should only be used for JAXB.
	 */
	protected SlideFile() {}
	
	/**
	 * Full constructor.
	 * @param filePath the file name and path
	 */
	public SlideFile(String filePath) {
		Path path = FileSystems.getDefault().getPath(filePath);
		long size = UNKNOWN_FILE_SIZE;
		try {
			size = Files.size(path);
		} catch (IOException ex) {
			LOGGER.warn("Unable to read file size for [" + filePath + "]:", ex);
		}
		String name = path.getFileName().toString();
		this.path = path.toString();
		this.name = name;
		this.size = size;
	}
	
	/**
	 * Returns the file path and name.
	 * @return String
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Returns the file name of the media.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the file size in bytes.
	 * @return long
	 */
	public long getSize() {
		return this.size;
	}
	
	/**
	 * Returns true if the file size is unknown.
	 * <p>
	 * This is typically cause by an IO exception when the size was read.
	 * @return boolean
	 */
	public boolean isUnknownFileSize() {
		return this.size == UNKNOWN_FILE_SIZE;
	}
}
