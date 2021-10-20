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

import org.praisenter.data.Tag;
import org.praisenter.media.Media;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * A media item in the {@link MediaLibraryPane} which could be in the process of being
 * added to the library.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaListItem implements Comparable<MediaListItem> {
	/** The media name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The media; can be null */
	private final ObjectProperty<Media> media = new SimpleObjectProperty<Media>(null);
	
	/** True if the media is present (or loaded) */
	private final BooleanProperty loaded = new SimpleBooleanProperty(false);
	
	/** An observable list of tags to maintain */
	private final ObservableSet<Tag> tags = FXCollections.observableSet();
	
	/**
	 * Sets up some dependencies.
	 */
	private MediaListItem() {
		media.addListener((obs, ov, nv) -> {
			tags.clear();
			if (nv != null) {
				tags.addAll(nv.getTags());
			}
		});
	}
	
	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public MediaListItem(String name) {
		this();
		this.name.set(name);
		this.media.set(null);
		this.loaded.set(false);
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param media the media
	 */
	public MediaListItem(Media media) {
		this();
		this.name.set(media.getName());
		this.media.set(media);
		this.loaded.set(true);
		this.tags.addAll(media.getTags());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name.get();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MediaListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded.get() && o.loaded.get()) {
			return this.media.get().compareTo(o.media.get());
		} else if (this.loaded.get()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Returns the name of the item.
	 * @return String
	 */
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * Sets the name of the item.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	/**
	 * The name property.
	 * @return StringProperty
	 */
	public StringProperty nameProperty() {
		return this.name;
	}
	
	/**
	 * Returns the media.
	 * @return {@link Media}
	 */
	public Media getMedia() {
		return this.media.get();
	}
	
	/**
	 * Sets the media.
	 * @param media the media
	 */
	public void setMedia(Media media) {
		this.media.set(media);
	}
	
	/**
	 * The media property.
	 * @return ObjectProperty&lt;{@link Media}&gt;
	 */
	public ObjectProperty<Media> mediaProperty() {
		return this.media;
	}
	
	/**
	 * Returns true if the media is loaded.
	 * @return boolean
	 */
	public boolean isLoaded() {
		return this.loaded.get();
	}
	
	/**
	 * Sets if this media has been loaded.
	 * @param loaded true if loaded
	 */
	public void setLoaded(boolean loaded) {
		this.loaded.set(loaded);
	}
	
	/**
	 * Returns the loaded property.
	 * @return BooleanProperty
	 */
	public BooleanProperty loadedProperty() {
		return this.loaded;
	}
	
	/**
	 * Returns this item's set of tags.
	 * @return ObservableSet&lt;{@link Tag}&gt;
	 */
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
}
