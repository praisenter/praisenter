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
package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A bible item in the {@link BibleLibraryPane} which could be in the process of being
 * added to the library.
 * <p>
 * This class will always have a name set, but the bible will be null until its been loaded.
 * @author William Bittle
 * @version 3.0.0
 */
final class BibleListItem implements Comparable<BibleListItem> {
	/** The bible name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The bible; can be null */
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<Bible>(null);
	
	/** True if the bible is present (or loaded) */
	private final BooleanProperty loaded = new SimpleBooleanProperty(false);
	
	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public BibleListItem(String name) {
		this.name.set(name);
		this.bible.set(null);
		this.loaded.set(false);
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param bible the bible
	 */
	public BibleListItem(Bible bible) {
		this.name.set(bible.getName());
		this.bible.set(bible);
		this.loaded.set(true);
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
	public int compareTo(BibleListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded.get() && o.loaded.get()) {
			return this.bible.get().compareTo(o.bible.get());
		} else if (this.loaded.get()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof BibleListItem) {
			BibleListItem item = (BibleListItem)obj;
			if (item.loaded.get() == this.loaded.get()) {
				if (item.loaded.get()) {
					return item.bible.get().equals(this.bible.get());
				} else {
					return item.name.get().equals(this.name.get());
				}
			}
		}
		return false;
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
	 * Returns the bible or null.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible.get();
	}
	
	/**
	 * Sets the bible.
	 * @param bible the bible
	 */
	public void setBible(Bible bible) {
		this.bible.set(bible);
	}
	
	/**
	 * Returns the bible property.
	 * @return ObjectProperty&lt;{@link Bible}&gt;
	 */
	public ObjectProperty<Bible> bibleProperty() {
		return this.bible;
	}
	
	/**
	 * Returns true if this bible is loaded.
	 * @return boolean
	 */
	public boolean isLoaded() {
		return this.loaded.get();
	}
	
	/**
	 * Sets if this bible has been loaded.
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
}
