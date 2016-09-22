package org.praisenter.javafx.slide;

import org.praisenter.slide.Slide;

final class SlideListItem implements Comparable<SlideListItem> {
	/** The slide name */
	final String name;
	
	/** The slide; can be null */
	final Slide slide;
	
	/** True if the slide is present (or loaded) */
	final boolean loaded;

	/**
	 * Optional constructor for pending items.
	 * @param name the name
	 */
	public SlideListItem(String name) {
		this.name = name;
		this.slide = null;
		this.loaded = false;
	}
	
	/**
	 * Optional constructor for existing items.
	 * @param slide the slide
	 */
	public SlideListItem(Slide slide) {
		this.name = slide.getName();
		this.slide = slide;
		this.loaded = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
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
	public int compareTo(SlideListItem o) {
		// sort loaded last
		// then sort by media (name)
		if (this.loaded && o.loaded) {
			return this.slide.compareTo(o.slide);
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
		if (obj instanceof SlideListItem) {
			SlideListItem item = (SlideListItem)obj;
			if (item.loaded == this.loaded) {
				if (item.loaded) {
					return item.slide.equals(this.slide);
				} else {
					return item.name.equals(this.name);
				}
			}
		}
		return false;
	}
}
