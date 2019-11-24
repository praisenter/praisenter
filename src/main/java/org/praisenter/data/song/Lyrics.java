package org.praisenter.data.song;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Lyrics implements ReadOnlyLyrics, Copyable {
	private final ObjectProperty<UUID> id;
	private final BooleanProperty original;
	private final StringProperty language;
	private final StringProperty transliteration;
	private final StringProperty title;
	
	private final ObservableList<Author> authors;
	private final ObservableList<SongBook> songbooks;
	private final ObservableList<Section> sections;
	
	private final ObservableList<Author> authorsUnmodifiable;
	private final ObservableList<SongBook> songbooksUnmodifiable;
	private final ObservableList<Section> sectionsUnmodifiable;
	
	public Lyrics() {
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.original = new SimpleBooleanProperty(false);
		this.language = new SimpleStringProperty();
		this.transliteration = new SimpleStringProperty();
		this.title = new SimpleStringProperty();
		
		this.authors = FXCollections.observableArrayList();
		this.songbooks = FXCollections.observableArrayList();
		this.sections = FXCollections.observableArrayList();
		
		this.authorsUnmodifiable = FXCollections.unmodifiableObservableList(this.authors);
		this.songbooksUnmodifiable = FXCollections.unmodifiableObservableList(this.songbooks);
		this.sectionsUnmodifiable = FXCollections.unmodifiableObservableList(this.sections);
	}
	
	@Override
	public String toString() {
		return this.title.get();
	}
	
	@Override
	public Lyrics copy() {
		Lyrics v = new Lyrics();
		v.id.set(this.id.get());
		v.original.set(this.original.get());
		v.language.set(this.language.get());
		v.transliteration.set(this.transliteration.get());
		v.title.set(this.title.get());
		
		for (Author author : this.authors) {
			v.authors.add(author.copy());
		}
		for (SongBook songbook : this.songbooks) {
			v.songbooks.add(songbook.copy());
		}
		for (Section section : this.sections) {
			v.sections.add(section.copy());
		}
		
		return v;
	}

	@Override
	public Author getDefaultAuthor() {
		Author author = null;
		int matchType = 0;
		if (this.authors.size() > 0) {
			// default to the first one
			author = this.authors.get(0);
			// try to find the best one
			for (Author auth : this.authors) {
				// don't choose an empty one
				String name = auth.getName();
				String type = auth.getType();
				
				if (name == null || name.length() <= 0) {
					continue;
				}
				// otherwise its the first one without a type setting
				if (type == null || type.length() == 0) {
					return auth;
				// otherwise its the first with type words
				} else if (Author.TYPE_LYRICS.equals(type) && matchType < 1) {
					auth = author;
					matchType = 1;
				// otherwise its the first with type music
				} else if (Author.TYPE_MUSIC.equals(type) && matchType < 2) {
					auth = author;
					matchType = 2;
				}
				// otherwise its the first
			}
		}
		return author;
	}
	
	@Override
	public Section getSectionByName(String name) {
		for (Section section : this.sections) {
			if (name.equalsIgnoreCase(section.getName())) {
				return section;
			}
		}
		return null;
	}

	@Override
	public Locale getLocale() {
		String language = this.language.get();
		if (language != null) {
			return Locale.forLanguageTag(language);
		}
		return null;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public boolean isOriginal() {
		return this.original.get();
	}
	
	@JsonProperty
	public void setOriginal(boolean original) {
		this.original.set(original);
	}
	
	@Override
	@Watchable(name = "original")
	public BooleanProperty originalProperty() {
		return this.original;
	}
	
	@Override
	@JsonProperty
	public String getTitle() {
		return this.title.get();
	}
	
	@JsonProperty
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	@Override
	@Watchable(name = "title")
	public StringProperty titleProperty() {
		return this.title;
	}
	
	@Override
	@JsonProperty
	public String getLanguage() {
		return this.language.get();
	}
	
	@JsonProperty
	public void setLanguage(String language) {
		this.language.set(language);
	}
	
	@Override
	@Watchable(name = "language")
	public StringProperty languageProperty() {
		return this.language;
	}

	@Override
	@JsonProperty
	public String getTransliteration() {
		return this.transliteration.get();
	}
	
	@JsonProperty
	public void setTransliteration(String transliteration) {
		this.transliteration.set(transliteration);
	}
	
	@Override
	@Watchable(name = "transliteration")
	public StringProperty transliterationProperty() {
		return this.transliteration;
	}

	@JsonProperty
	public void setAuthors(List<Author> authors) {
		this.authors.setAll(authors);
	}
	
	@JsonProperty
	@Watchable(name = "authors")
	public ObservableList<Author> getAuthors() {
		return this.authors;
	}
	
	@Override
	public ObservableList<? extends ReadOnlyAuthor> getAuthorsUnmodifiable() {
		return this.authorsUnmodifiable;
	}

	@JsonProperty
	public void setSongBooks(List<SongBook> songbooks) {
		this.songbooks.setAll(songbooks);
	}
	
	@JsonProperty
	@Watchable(name = "songbooks")
	public ObservableList<SongBook> getSongBooks() {
		return this.songbooks;
	}
	
	@Override
	public ObservableList<? extends ReadOnlySongBook> getSongBooksUnmodifiable() {
		return this.songbooksUnmodifiable;
	}

	@JsonProperty
	public void setSections(List<Section> sections) {
		this.sections.setAll(sections);
	}
	
	@JsonProperty
	@Watchable(name = "sections")
	public ObservableList<Section> getSections() {
		return this.sections;
	}
	
	@Override
	public ObservableList<? extends ReadOnlySection> getSectionsUnmodifiable() {
		return this.sectionsUnmodifiable;
	}
}
