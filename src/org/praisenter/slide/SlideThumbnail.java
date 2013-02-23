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

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.common.xml.BufferedImageTypeAdapter;

/**
 * Represents a cached thumbnail on the file system.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "SlideThumbnail")
public class SlideThumbnail implements Comparable<SlideThumbnail> {
	/** The file properties */
	@XmlElement(name = "File", required = true, nillable = false)
	protected SlideFile file;
	
	/** The user designated name */
	@XmlElement(name = "Name")
	protected String name;
	
	/** The thumbnail image */
	@XmlElement(name = "Image", nillable = true, required = false)
	@XmlJavaTypeAdapter(value = BufferedImageTypeAdapter.class)
	protected BufferedImage image;
	
	/**
	 * Default constructor.
	 */
	protected SlideThumbnail() {}
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param name the slide/template name
	 * @param image the thumbnail image
	 */
	public SlideThumbnail(SlideFile file, String name, BufferedImage image) {
		super();
		this.file = file;
		this.name = name;
		this.image = image;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SlideThumbnail o) {
		if (o == null) {
			return 1;
		}
		// compare on file name
		if (o.file == SlideFile.NOT_STORED) {
			return 1;
		}
		if (this.file == SlideFile.NOT_STORED) {
			return -1;
		}
		return this.file.getName().compareTo(o.getFile().getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideThumbnail) {
			SlideThumbnail st = (SlideThumbnail)obj;
			if (st.file.equals(this.file)) {
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
		return this.file.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SlideThumbnail[File=").append(this.file)
		  .append("|Name=").append(this.name)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the media item's file information.
	 * @return {@link SlideFile}
	 */
	public SlideFile getFile() {
		return this.file;
	}
	
	/**
	 * Returns the thumbnail of the media item.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Returns the slide/template name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
