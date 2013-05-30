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

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.common.xml.BufferedImageTypeAdapter;

/**
 * Represents a thumbnail for a {@link Media} item.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "MediaThumbnail")
public class MediaThumbnail implements Comparable<MediaThumbnail> {
	/** The file properties */
	@XmlElement(name = "File", required = true, nillable = false)
	protected MediaFile file;
	
	/** The thumbnail image */
	@XmlElement(name = "Image", nillable = true, required = false)
	@XmlJavaTypeAdapter(value = BufferedImageTypeAdapter.class)
	protected BufferedImage image;

	/** The media type */
	@XmlAttribute(name = "MediaType", required = true)
	protected MediaType mediaType;
	
	/**
	 * Default constructor.
	 */
	protected MediaThumbnail() {}
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param image the thumbnail image
	 * @param mediaType the media type
	 */
	public MediaThumbnail(MediaFile file, BufferedImage image, MediaType mediaType) {
		super();
		this.file = file;
		this.image = image;
		this.mediaType = mediaType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MediaThumbnail o) {
		// compare on file name
		return this.file.getName().compareTo(o.getFile().getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaThumbnail) {
			MediaThumbnail other = (MediaThumbnail)obj;
			if (other.mediaType == this.mediaType &&
				other.file.equals(this.file)) {
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
		sb.append("MediaThumbnail[Type=").append(this.mediaType)
		  .append("|File=").append(this.file)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the media type.
	 * @return {@link MediaType}
	 */
	public MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * Returns the media item's file information.
	 * @return {@link MediaFile}
	 */
	public MediaFile getFile() {
		return this.file;
	}
	
	/**
	 * Returns the thumbnail of the media item.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
}
