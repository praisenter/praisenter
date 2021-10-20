package org.praisenter.javafx;

import java.util.UUID;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideShow;

import javafx.scene.input.DataFormat;

public final class DataFormats {
	private DataFormats() {}

	public static final DataFormat BIBLES = DataFormats.forSerializedList(Bible.class);
	public static final DataFormat BOOKS = DataFormats.forJsonSerializedList(Book.class);
	public static final DataFormat CHAPTERS = DataFormats.forJsonSerializedList(Chapter.class);
	public static final DataFormat VERSES = DataFormats.forJsonSerializedList(Verse.class);
	
	public static final DataFormat SLIDES = DataFormats.forJsonSerializedList(Slide.class);
	public static final DataFormat SLIDE_SHOWS = DataFormats.forJsonSerializedList(SlideShow.class);
	public static final DataFormat SLIDE_COMPONENT = DataFormats.forJsonSerializedClass(SlideComponent.class);

	public static final DataFormat getUniqueFormat() {
		return new DataFormat("application/x-praisenter-" + UUID.randomUUID().toString().replaceAll("-", ""));
	}
	
	private static final DataFormat forJsonSerializedClass(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-json;class=" + clazz.getName());
	}
	
	private static final DataFormat forJsonSerializedList(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-json-list;class=" + clazz.getName());
	}

	private static final DataFormat forSerializedClass(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-serialized;class=" + clazz.getName());
	}
	
	private static final DataFormat forSerializedList(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-serialized-list;class=" + clazz.getName());
	}
}
