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
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "SlideFile")
@XmlAccessorType(XmlAccessType.NONE)
public class SlideFile {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideFile.class);
	
	/** Number to represent unknown file size */
	private static final int UNKNOWN_FILE_SIZE = -1;
	
	/** Static field for slides/templates that are not stored */
	public static final SlideFile NOT_STORED = new SlideFile();
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (this == NOT_STORED) {
			if (obj == NOT_STORED) {
				return true;
			} else {
				return false;
			}
		}
		if (obj instanceof SlideFile) {
			SlideFile sf = (SlideFile)obj;
			if (this.path.equals(sf.path)) {
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
		return this.path.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SlideFile[Path=").append(this.path)
		  .append("|Name=").append(this.name)
		  .append("|Size=").append(this.size)
		  .append("]");
		return sb.toString();
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
	 * This is typically caused by an IO exception when the size was read.
	 * @return boolean
	 */
	public boolean isUnknownFileSize() {
		return this.size == UNKNOWN_FILE_SIZE;
	}
}
