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
package org.praisenter.javafx.slide;

import org.praisenter.Tag;
import org.praisenter.slide.Slide;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * A list item for slides.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideListItem implements Comparable<SlideListItem> {
	/** The slide name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The slide; can be null */
	private final ObjectProperty<Slide> slide = new SimpleObjectProperty<Slide>(null);
	
	/** True if the slide is present (or loaded) */
	private final BooleanProperty loaded = new SimpleBooleanProperty(false);

	/** An observable list of tags to maintain */
	private final ObservableSet<Tag> tags = FXCollections.observableSet();

	/**
	 * Sets up some dependencies.
	 */
	private SlideListItem() {
		slide.addListener((obs, ov, nv) -> {
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
	public SlideListItem(String name) {
		this();
		this.name.set(name);
		this.slide.set(null);
		this.loaded.set(false);
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param slide the slide
	 */
	public SlideListItem(Slide slide) {
		this();
		this.name.set(slide.getName());
		this.slide.set(slide);
		this.loaded.set(true);
		this.tags.addAll(slide.getTags());
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
	public int compareTo(SlideListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded.get() && o.loaded.get()) {
			return this.slide.get().compareTo(o.slide.get());
		} else if (this.loaded.get()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Returns the name of this item.
	 * @return String
	 */
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * Sets the name of this item.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	/**
	 * Returns the name property.
	 * @return StringProperty
	 */
	public StringProperty nameProperty() {
		return this.name;
	}
	
	/**
	 * Returns the slide or null.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slide.get();
	}
	
	/**
	 * Sets the slide.
	 * @param slide the slide
	 */
	public void setSlide(Slide slide) {
		this.slide.set(slide);
	}
	
	/**
	 * Returns the slide property.
	 * @return ObjectProperty&lt;{@link Slide}&gt;
	 */
	public ObjectProperty<Slide> slideProperty() {
		return this.slide;
	}
	
	/**
	 * Returns true if this slide is loaded.
	 * @return boolean
	 */
	public boolean isLoaded() {
		return this.loaded.get();
	}
	
	/**
	 * Sets if this slide has been loaded.
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
