/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.media;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.apache.log4j.Logger;

/**
 * Generic file properties class.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "MediaFile")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ImageMediaFile.class, VideoMediaFile.class, AudioMediaFile.class})
public class MediaFile implements Serializable {
	/** The version id */
	private static final long serialVersionUID = 303574109495901021L;

	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MediaFile.class);
	
	/** Number to represent unknown file size */
	private static final int UNKNOWN_FILE_SIZE = -1;
	
	/** The relative path to the application directory */
	@XmlAttribute(name = "RelativePath")
	protected String relativePath;

	/** The full file path and name */
	@XmlAttribute(name = "FullPath")
	protected String fullPath;
	
	/** The file name */
	@XmlAttribute(name = "Name")
	protected String name;
	
	/** The file size in bytes */
	@XmlAttribute(name = "Size")
	protected long size;
	
	/** The file format */
	@XmlAttribute(name = "Format")
	protected String format;
	
	/**
	 * Default constructor.
	 * <p>
	 * Should only be used for JAXB.
	 */
	protected MediaFile() {}
	
	/**
	 * Full constructor.
	 * @param basePath the base path
	 * @param fullPath the full file name and path
	 * @param format the file format
	 */
	public MediaFile(String basePath, String fullPath, String format) {
		FileSystem system = FileSystems.getDefault();
		Path path = system.getPath(fullPath);
		// get the relative path
		// we want to use this for storing the media in the media library
		// so that when stuff that reference the media is exported to another
		// system, we don't have file path problems
		Path rel = system.getPath(basePath).relativize(path);
		this.fullPath = path.toString();
		this.relativePath = rel.toString();
		long size = UNKNOWN_FILE_SIZE;
		try {
			size = Files.size(path);
		} catch (IOException ex) {
			LOGGER.warn("Unable to read file size for [" + this.relativePath + "]:", ex);
		}
		String name = path.getFileName().toString();
		this.name = name;
		this.size = size;
		this.format = format;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaFile) {
			MediaFile other = (MediaFile)obj;
			// only path must be equal
			if (other.relativePath.equals(this.relativePath)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.relativePath.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MediaFile[Path=").append(this.fullPath)
		  .append("|RelativePath=").append(this.relativePath)
		  .append("|Name=").append(this.name)
		  .append("|Size=").append(this.size)
		  .append("|Format=").append(this.format)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the full file path and name.
	 * <p>
	 * This should only be used for output to the user.
	 * Instead the {@link #getRelativePath()} should be used
	 * for all Media Library operations.
	 * @return String
	 */
	public String getFullPath() {
		return this.fullPath;
	}

	/**
	 * Returns the relative path.
	 * @return String
	 */
	public String getRelativePath() {
		return this.relativePath;
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
	 * Returns the file format.
	 * @return String
	 */
	public String getFormat() {
		return this.format;
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
