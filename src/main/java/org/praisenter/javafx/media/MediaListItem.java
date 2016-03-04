package org.praisenter.javafx.media;

import org.praisenter.media.Media;

final class MediaListItem implements Comparable<MediaListItem> {
	final String name;
	final Media media;
	final boolean loaded;
	
	public MediaListItem(String name) {
		this.name = name;
		this.media = null;
		this.loaded = false;
	}
	
	public MediaListItem(Media media) {
		this.name = media.getMetadata().getName();
		this.media = media;
		this.loaded = true;
	}
	
	@Override
	public int hashCode() {
		return loaded ? media.hashCode() : name.hashCode();
	}
	
	@Override
	public int compareTo(MediaListItem o) {
		if (this.loaded && o.loaded) {
			return this.media.compareTo(o.media);
		} else if (this.loaded) {
			return -1;
		} else {
			return 1;
		}
	}
	
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
