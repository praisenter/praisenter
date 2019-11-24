package org.praisenter.data.song;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.search.Indexable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadOnlySong extends Indexable, Persistable, Copyable, Identifiable {
	public String getCopyright();
	public String getSource();
	public String getCCLINumber();
	public String getReleased();
	public String getTransposition();
	public String getTempo();
	public String getKey();
	public String getVariant();
	public String getPublisher();
	public String getNotes();
	public String getKeywords();
	public UUID getPrimaryLyrics();
	
	public ReadOnlyStringProperty copyrightProperty();
	public ReadOnlyStringProperty sourceProperty();
	public ReadOnlyStringProperty ccliNumberProperty();
	public ReadOnlyStringProperty releasedProperty();
	public ReadOnlyStringProperty transpositionProperty();
	public ReadOnlyStringProperty tempoProperty();
	public ReadOnlyStringProperty keyProperty();
	public ReadOnlyStringProperty variantProperty();
	public ReadOnlyStringProperty publisherProperty();
	public ReadOnlyStringProperty notesProperty();
	public ReadOnlyStringProperty keywordsProperty();
	public ReadOnlyObjectProperty<UUID> primaryLyricsProperty();
	
	public ObservableList<? extends ReadOnlyLyrics> getLyricsUnmodifiable();
	
	public ReadOnlyLyrics getDefaultLyrics();
	public ReadOnlyAuthor getDefaultAuthor();
	public String getDefaultTitle();
	public ReadOnlyLyrics getLyrics(String language, String transliteration);
}
