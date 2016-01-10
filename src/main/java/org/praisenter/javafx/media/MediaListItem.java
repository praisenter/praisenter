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
		this.name = null;
		this.media = media;
		this.loaded = true;
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
}
