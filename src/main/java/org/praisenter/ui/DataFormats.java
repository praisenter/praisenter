package org.praisenter.ui;

import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.SlideReference;
import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.SongBook;

import javafx.scene.input.DataFormat;

public final class DataFormats {
	private DataFormats() {}
	
	// a List<UUID> object
	public static final DataFormat PRAISENTER_ID_LIST = 				new DataFormat("application/x-praisenter-id-list");
	
	// JSON serialized arrays of types
	public static final DataFormat PRAISENTER_BOOK_ARRAY = 				new DataFormat("application/x-praisenter-json-array;class=" + Book[].class.getName());
	public static final DataFormat PRAISENTER_CHAPTER_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Chapter[].class.getName());
	public static final DataFormat PRAISENTER_VERSE_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Verse[].class.getName());
	public static final DataFormat PRAISENTER_LYRICS_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Lyrics[].class.getName());
	public static final DataFormat PRAISENTER_AUTHOR_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Author[].class.getName());
	public static final DataFormat PRAISENTER_SECTION_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Section[].class.getName());
	public static final DataFormat PRAISENTER_SONGBOOK_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + SongBook[].class.getName());
	public static final DataFormat PRAISENTER_SLIDE_COMPONENT_ARRAY = 	new DataFormat("application/x-praisenter-json-array;class=" + SlideComponent[].class.getName());
	public static final DataFormat PRAISENTER_SLIDE_ARRAY = 			new DataFormat("application/x-praisenter-json-array;class=" + Slide[].class.getName());
	public static final DataFormat PRAISENTER_SLIDE_REFERENCE_ARRAY =	new DataFormat("application/x-praisenter-json-array;class=" + SlideReference[].class.getName());
	
	// an integer
	public static final DataFormat PRAISENTER_INDEX = 					new DataFormat("application/x-praisenter-index");
}
