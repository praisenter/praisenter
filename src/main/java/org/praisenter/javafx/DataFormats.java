package org.praisenter.javafx;

import org.praisenter.bible.Bible;

import javafx.scene.input.DataFormat;

public final class DataFormats {
	private DataFormats() {}
	
	public static final DataFormat UUID = new DataFormat("text/x-java-uuid");
	public static final DataFormat BIBLE_ID = new DataFormat("text/x-praisenter-bible-id");
}
