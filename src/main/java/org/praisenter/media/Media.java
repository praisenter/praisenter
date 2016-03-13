/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
import java.text.Collator;

/**
 * A media item in the media library.
 * <p>
 * The referenced media item should already be in a compatible format and should
 * have all necessary files in place in the media library at the time these
 * objects are received by a caller.
 * <p>
 * The Media class implements the Comparable interface to provide a default sort
 * based on the linked file path and name.
 * <p>
 * Instances of the Media object are immutable with one exception: tags.  The tags
 * should be mutated by calling the relevant methods in the {@link MediaLibrary} class.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Media implements Comparable<Media> {
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** The metadata */
	final MediaMetadata metadata;
	
	/** The thumbnail */
	final BufferedImage thumbnail;
	
	/**
	 * Minimal constructor.
	 * @param metadata the metadata
	 * @param thumbnail the thumbnail
	 */
	Media(MediaMetadata metadata, BufferedImage thumbnail) {
		this.metadata = metadata;
		this.thumbnail = thumbnail;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Media o) {
		return COLLATOR.compare(this.metadata.name, o.metadata.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Media) {
			Media media = (Media)obj;
			// their type and path must be equal
			if (media.getMetadata().id.equals(this.metadata.id)) {
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
		return this.metadata.path.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.metadata.path.toAbsolutePath().toString();
	}
	
	/**
	 * Returns the media's metadata.
	 * @return {@link MediaMetadata}
	 */
	public MediaMetadata getMetadata() {
		return this.metadata;
	}
	
	/**
	 * Returns the media's thumbnail image.
	 * @return BufferedImage
	 */
	public BufferedImage getThumbnail() {
		return this.thumbnail;
	}
}
