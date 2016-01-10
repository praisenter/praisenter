package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.text.Collator;

public final class Media implements Comparable<Media> {
	private final Collator COLLATOR = Collator.getInstance();
	
	final MediaMetadata metadata;
	final BufferedImage thumbnail;
	
	public Media(MediaMetadata metadata, BufferedImage thumbnail) {
		this.metadata = metadata;
		this.thumbnail = thumbnail;
	}
	
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
			if (media.getMetadata().path.equals(metadata.path)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return metadata.path.hashCode();
	}
	
	@Override
	public String toString() {
		return metadata.path.toAbsolutePath().toString();
	}
	
	public MediaMetadata getMetadata() {
		return metadata;
	}
	
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
}
