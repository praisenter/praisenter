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
package org.praisenter.javafx.media;

import org.praisenter.Tag;
import org.praisenter.media.Media;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * A media item in the {@link MediaLibraryPane} which could be in the process of being
 * added to the library.
 * <p>
 * This class will always have the {@link #name} field set, but {@link #media} will
 * be null when {@link #loaded} is false.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaListItem implements Comparable<MediaListItem> {
	/** The media name */
	final String name;
	
	/** The media; can be null */
	final Media media;
	
	/** True if the media is present (or loaded) */
	final boolean loaded;
	
	/** An observable list of tags to maintain */
	final ObservableSet<Tag> tags;
	
	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public MediaListItem(String name) {
		this.name = name;
		this.media = null;
		this.loaded = false;
		this.tags = null;
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param media the media
	 */
	public MediaListItem(Media media) {
		this.name = media.getMetadata().getName();
		this.media = media;
		this.loaded = true;
		this.tags = FXCollections.observableSet();
		this.tags.addAll(media.getMetadata().getTags());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return loaded ? media.hashCode() : name.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MediaListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded && o.loaded) {
			return this.media.compareTo(o.media);
		} else if (this.loaded) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaListItem) {
			MediaListItem item = (MediaListItem)obj;
			if (item.loaded == this.loaded) {
				if (item.loaded) {
					return item.media.equals(this.media);
				} else {
					return item.name.equals(this.name);
				}
			}
		}
		return false;
	}
}
