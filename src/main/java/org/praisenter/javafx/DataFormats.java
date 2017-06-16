package org.praisenter.javafx;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;

import javafx.scene.input.DataFormat;

public final class DataFormats {
	private DataFormats() {}

	public static final DataFormat BIBLES = DataFormats.forSerializedList(Bible.class);
	public static final DataFormat BOOKS = DataFormats.forXmlSerializedList(Book.class);
	public static final DataFormat CHAPTERS = DataFormats.forXmlSerializedList(Chapter.class);
	public static final DataFormat VERSES = DataFormats.forXmlSerializedList(Verse.class);
	
	public static final DataFormat SLIDES = DataFormats.forXmlSerializedList(Slide.class);
	public static final DataFormat SLIDE_COMPONENT = DataFormats.forXmlSerializedClass(SlideComponent.class);

	private static final DataFormat forXmlSerializedClass(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-xml;class=" + clazz.getName());
	}
	
	private static final DataFormat forXmlSerializedList(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-xml-list;class=" + clazz.getName());
	}

	private static final DataFormat forSerializedClass(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-serialized;class=" + clazz.getName());
	}
	
	private static final DataFormat forSerializedList(Class<?> clazz) {
		return new DataFormat("application/x-praisenter-serialized-list;class=" + clazz.getName());
	}
}
