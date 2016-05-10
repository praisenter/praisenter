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

/**
 * A bible item in the {@link BibleLibraryPane} which could be in the process of being
 * added to the library.
 * <p>
 * This class will always have the {@link #name} field set, but {@link #bible} will
 * be null when {@link #loaded} is false.
 * @author William Bittle
 * @version 3.0.0
 */
final class BibleListItem implements Comparable<BibleListItem> {
	/** The bible name */
	final String name;
	
	/** The bible; can be null */
	final Bible bible;
	
	/** True if the bible is present (or loaded) */
	final boolean loaded;
	
	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public BibleListItem(String name) {
		this.name = name;
		this.bible = null;
		this.loaded = false;
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param bible the bible
	 */
	public BibleListItem(Bible bible) {
		this.name = bible.getName();
		this.bible = bible;
		this.loaded = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BibleListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded && o.loaded) {
			return this.bible.compareTo(o.bible);
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
		if (obj instanceof BibleListItem) {
			BibleListItem item = (BibleListItem)obj;
			if (item.loaded == this.loaded) {
				if (item.loaded) {
					return item.bible.equals(this.bible);
				} else {
					return item.name.equals(this.name);
				}
			}
		}
		return false;
	}
}
