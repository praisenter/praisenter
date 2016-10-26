package org.praisenter.javafx;

import javafx.scene.input.DataFormat;

public final class DataFormats {
	private DataFormats() {}
	
	public static final DataFormat UUID = new DataFormat("text/x-java-uuid");
	public static final DataFormat BIBLE_ID = new DataFormat("text/x-praisenter-bible-id");
	
	public static final DataFormat BOOKS = DataFormats.forSerializedClass(org.praisenter.bible.Book.class);
	public static final DataFormat CHAPTERS = DataFormats.forSerializedClass(org.praisenter.bible.Chapter.class);
	public static final DataFormat VERSES = DataFormats.forSerializedClass(org.praisenter.bible.Verse.class);
	
	private static final DataFormat forSerializedClass(Class<?> clazz) {
		return new DataFormat("application/x-java-serialized-object;class=" + clazz.getName());
	}
}
