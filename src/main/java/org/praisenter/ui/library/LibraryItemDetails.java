package org.praisenter.ui.library;

import java.time.Instant;
import java.util.HashSet;

import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.layout.BorderPane;

final class LibraryItemDetails extends BorderPane {
	// data
	
	private final ObjectProperty<Persistable> item;
	
	// standard data
	
	private final StringProperty name;
	private final ObjectProperty<Instant> modified;
	private final ObjectProperty<Instant> created;
	private final ObservableSet<Tag> tags;
	
	// bible data
	
	private final StringProperty bibleLanguage;
	private final StringProperty bibleSource;
	private final StringProperty bibleCopyright;
	
	// media data
	
	private final IntegerProperty mediaWidth;
	private final IntegerProperty mediaHeight;
	private final LongProperty mediaLength;
	private final BooleanProperty mediaAudio;
	
	// slide data
	
	private final LongProperty slideTime;
	
	// slide show data
	
	private final LongProperty showTime;
	private final IntegerProperty showSlideCount;
	
	// song data
	
	// UI
	
	
	
	public LibraryItemDetails() {
		this.item = new SimpleObjectProperty<>();
		
		// standard data
		// name, modified, created, tags
		this.name = new SimpleStringProperty();
		this.modified = new SimpleObjectProperty<>();
		this.created = new SimpleObjectProperty<>();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		// bible data
		// language, source, copyright
		this.bibleLanguage = new SimpleStringProperty();
		this.bibleSource = new SimpleStringProperty();
		this.bibleCopyright = new SimpleStringProperty();
		
		// media data
		// width, height, length, hasAudio, preview
		this.mediaWidth = new SimpleIntegerProperty();
		this.mediaHeight = new SimpleIntegerProperty();
		this.mediaLength = new SimpleLongProperty();
		this.mediaAudio = new SimpleBooleanProperty();
		
		// slide data
		// total time (if not infinite), preview
		this.slideTime = new SimpleLongProperty();
		
		// slide show data
		// total time (if not infinite), slide count, preview
		this.showTime = new SimpleLongProperty();
		this.showSlideCount = new SimpleIntegerProperty();
		
		// song data
		// TODO what other info?
	}
}
