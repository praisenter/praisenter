package org.praisenter.ui.library;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideShow;
import org.praisenter.ui.translations.Translations;

public enum LibraryListType {
	BIBLE(Translations.get("bible"), 100),
	SONG(Translations.get("song"), 300),
	SLIDE(Translations.get("slide"), 500),
	SHOW(Translations.get("show"), 700),
	IMAGE(Translations.get("image"), 1000),
	VIDEO(Translations.get("video"), 1100),
	AUDIO(Translations.get("audio"), 1200);
	
	private final String name;
	private final int order;
	
	private LibraryListType(String name, int order) {
		this.name = name;
		this.order = order;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public static LibraryListType from(Object obj) {
		if (obj == null) {
			throw new NullPointerException("Cannot determine item type of a null object.");
		}
		if (obj instanceof Bible) return BIBLE;
		//if (obj instanceof )
		if (obj instanceof Slide) return SLIDE;
		if (obj instanceof SlideShow) return SHOW;
		if (obj instanceof Media) {
			MediaType type = ((Media) obj).getMediaType();
			if (type == MediaType.IMAGE) return IMAGE;
			if (type == MediaType.VIDEO) return VIDEO;
			if (type == MediaType.AUDIO) return AUDIO;
		}
		throw new RuntimeException("Unknown list item type '" + obj.getClass().getName() + "'");
	}
}
