package org.praisenter.javafx.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.praisenter.media.MediaType;

final class MediaTypeFilter {
	public static final List<MediaTypeFilter> FILTERS;
	
	static {
		MediaType[] types = MediaType.values();
		List<MediaTypeFilter> filters = new ArrayList<MediaTypeFilter>(types.length);
		filters.add(new MediaTypeFilter(null));
		for (MediaType type : types) {
			if (type == MediaType.UNKNOWN) {
				continue;
			}
			filters.add(new MediaTypeFilter(type));
		}
		FILTERS = Collections.unmodifiableList(filters);
	}
	
	final MediaType type;
	
	private MediaTypeFilter(MediaType type) {
		this.type = type;
	}
	
	public static final MediaTypeFilter getMediaTypeFilter(MediaType type) {
		for (MediaTypeFilter filter : FILTERS) {
			if (filter.type == type) {
				return filter;
			}
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		return type == null ? -1 : type.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaTypeFilter) {
			MediaTypeFilter mtf = (MediaTypeFilter)obj;
			if (mtf.type == this.type) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (type == null) {
			// TODO translate
			return "Type";
		} else {
			return type.getName();
		}
	}
	
	public MediaType getType() {
		return type;
	}
}
