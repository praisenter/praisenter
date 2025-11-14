package org.praisenter.data.song;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Strings;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.praisenter.Constants;
import org.praisenter.Editable;
import org.praisenter.Version;
import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.data.search.Indexable;
import org.praisenter.utility.StringManipulator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "song")
@Editable
public final class Song implements ReadOnlySong, Indexable, Persistable, Copyable, Identifiable {
	public static final String DATA_TYPE_SONG = "song";
	public static final String FIELD_LYRICS_ID = "lyrics";
	public static final String FIELD_SECTION_ID = "section";
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	private final StringProperty name;
	private final ObjectProperty<Instant> createdDate;
	private final ObjectProperty<Instant> modifiedDate;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty ccliNumber;
	private final StringProperty released;
	private final StringProperty transposition;
	private final StringProperty tempo;
	private final StringProperty key;
	private final StringProperty variant;
	private final StringProperty publisher;
	private final StringProperty notes;
	private final StringProperty keywords;
	private final ObjectProperty<UUID> primaryLyrics;
	
	private final ObservableList<Lyrics> lyrics;
	private final ObservableList<Lyrics> lyricsReadOnly;
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;

	public Song() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Version.STRING);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new SimpleStringProperty();
		this.createdDate = new SimpleObjectProperty<>(Instant.now());
		this.modifiedDate = new SimpleObjectProperty<>(this.createdDate.get());
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.ccliNumber = new SimpleStringProperty();
		this.released = new SimpleStringProperty();
		this.transposition = new SimpleStringProperty();
		this.tempo = new SimpleStringProperty();
		this.key = new SimpleStringProperty();
		this.variant = new SimpleStringProperty();
		this.publisher = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		this.keywords = new SimpleStringProperty();
		this.primaryLyrics = new SimpleObjectProperty<UUID>();
		
		this.lyrics = FXCollections.observableArrayList();
		this.lyricsReadOnly = FXCollections.unmodifiableObservableList(this.lyrics);
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.persist.Persistable#identityEquals(java.lang.Object)
	 */
	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof Song) {
			Song song = (Song)other;
			return song.id.get().equals(this.id.get());
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.persist.Copyable#copy()
	 */
	@Override
	public Song copy() {
		Song s = new Song();
		s.format.set(this.format.get());
		s.version.set(this.version.get());
		s.id.set(this.id.get());
		s.createdDate.set(this.createdDate.get());
		s.modifiedDate.set(this.modifiedDate.get());
		s.name.set(this.name.get());
		s.source.set(this.source.get());
		s.copyright.set(this.copyright.get());
		s.ccliNumber.set(this.ccliNumber.get());
		s.released.set(this.released.get());
		s.transposition.set(this.transposition.get());
		s.tempo.set(this.tempo.get());
		s.key.set(this.key.get());
		s.variant.set(this.variant.get());
		s.publisher.set(this.publisher.get());
		s.notes.set(this.notes.get());
		s.keywords.set(this.keywords.get());
		s.primaryLyrics.set(this.primaryLyrics.get());
		
		for (Lyrics lyrics : this.lyrics) {
			s.lyrics.add(lyrics.copy());
		}
		
		s.tags.addAll(this.tags);
		return s;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name.get();
	}
	
	@Override
	public ReadOnlyLyrics getLyricsById(UUID id) {
		for (Lyrics lyrics : this.lyrics) {
			if (lyrics.getId().equals(id)) {
				return lyrics;
			}
		}
		return null;
	}
	
	@Override
	public Lyrics getDefaultLyrics() {
		Locale locale = Locale.getDefault();
		Lyrics lyrics = null;
		int matchLevel = 0;
		int verseCount = 0;
		if (this.lyrics.size() > 0) {
			// default to the first one
			lyrics = this.lyrics.get(0);
			// try to find the best one
			for (Lyrics lrcs : this.lyrics) {
				Locale sl = lrcs.getLocale();
				int vc = lrcs.getSections().size();
				// don't choose an empty one
				if (vc == 0) {
					continue;
				}
				// see if it's the primary set (set by the user)
				if (this.primaryLyrics.get() != null && this.primaryLyrics.get().equals(lrcs.getId())) {
					return lrcs;
				// see if it's the original set of lyrics
				} else if (lrcs.isOriginal()) {
					return lrcs;
				// otherwise its the one without a language setting
				} else if (lrcs.getLanguage() == null || lrcs.getLanguage().isEmpty()) {
					return lrcs;
				// otherwise its the first one that matches the current locale
				// check the current match level to make sure we get the last one 
				// (we want the first)
				} else if (sl != null &&
						   locale.getLanguage().equals(sl.getLanguage()) &&
						   locale.getCountry().equals(sl.getCountry()) &&
						   matchLevel < 2) {
					lyrics = lrcs;
					matchLevel = 2;
				// otherwise its the first one that matches the current language
				// check the current match level to make sure we don't replace one
			    // that is more locale specific or get the last one (we want the first)
				} else if (sl != null &&
						   locale.getLanguage().equals(sl.getLanguage()) &&
						   matchLevel < 1) {
					lyrics = lrcs;
					matchLevel = 1;
				// otherwise choose the one with the most verses
				} else if (verseCount < vc &&
						   matchLevel < 1) {
					lyrics = lrcs;
					verseCount = vc;
				}
			}
		}
		return lyrics;
	}
	
	@Override
	public String getDefaultTitle() {
		Lyrics lyrics = this.getDefaultLyrics();
		if (lyrics != null) {
			return lyrics.getTitle();
		}
		return null;
	}

	@Override
	public Author getDefaultAuthor() {
		Lyrics lyrics = this.getDefaultLyrics();
		if (lyrics != null) {
			return lyrics.getDefaultAuthor();
		}
		return null;
	}
	
	@Override
	public Lyrics getLyrics(String language, String transliteration) {
		for (Lyrics lyrics : this.lyrics) {
			if (Strings.CI.equals(lyrics.getLanguage(), language) &&
				Strings.CI.equals(lyrics.getTransliteration(), transliteration)) {
				return lyrics;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.search.Indexable#index()
	 */
	@Override
	public List<Document> index() {
		List<Document> documents = new ArrayList<Document>();
		
		StringBuilder text = new StringBuilder();
		
		// store any lyrics titles
		for (Lyrics lyrics : this.lyrics) {
			String title = lyrics.getTitle();
			if (!StringManipulator.isNullOrEmpty(title)) 
				text.append(title).append("\n");
		}
		
		// store the song name
		String name = this.getName();
		if (!StringManipulator.isNullOrEmpty(name)) 
			text.append(name).append("\n");
		
		// store the song keywords
		String keywords = this.getKeywords();
		if (!StringManipulator.isNullOrEmpty(keywords)) 
			text.append(keywords).append("\n");
		
		// add the document for the whole song
		{
			Document document = new Document();
	
			// allow filtering by the song id
			document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
			
			// allow filtering by type
			document.add(new StringField(FIELD_TYPE, DATA_TYPE_SONG, Field.Store.YES));
	
			// check the text
			String alltext = text.toString();
			if (!StringManipulator.isNullOrEmpty(alltext)) {
				document.add(new TextField(FIELD_TEXT, alltext, Field.Store.YES));
			}
			
			documents.add(document);
		}
		
		// store the song text first so that when we highlight
		// matched text we favor the song text above the name, title, keywords
		for (Lyrics lyrics : this.lyrics) {
			for (Section section : lyrics.getSections()) {
				String sectionText = section.getText();
				if (!StringManipulator.isNullOrEmpty(sectionText)) {
					Document document = new Document();

					// allow filtering by the song id
					document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
					
					// allow filtering by type
					document.add(new StringField(FIELD_TYPE, DATA_TYPE_SONG, Field.Store.YES));

					// stored data so we can look up the verse
					document.add(new StoredField(FIELD_LYRICS_ID, lyrics.getId().toString()));
					document.add(new StoredField(FIELD_SECTION_ID, section.getId().toString()));
					document.add(new TextField(FIELD_TEXT, section.getText(), Field.Store.YES));
					
					documents.add(document);
				}
			}
		}
		
		
		String tags = this.tags.stream().map(t -> t.getName()).collect(Collectors.joining(" "));
		if (!StringManipulator.isNullOrEmpty(tags)) {
			Document document = new Document();
			document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
			document.add(new StringField(FIELD_TYPE, DATA_TYPE_SONG, Field.Store.YES));
			document.add(new TextField(FIELD_TAGS, tags, Field.Store.YES));
			documents.add(document);
		}
		
		return documents;
	}
	
	@Override
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	public String getFormat() {
		return this.format.get();
	}
	
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	void setFormat(String format) {
		this.format.set(format);
	}
	
	@Override
	public ReadOnlyStringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	void setVersion(String version) {
		this.version.set(version);
	}
	
	@Override
	public ReadOnlyStringProperty versionProperty() {
		return this.version;
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
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	@Watchable(name = "name")
	public StringProperty nameProperty() {
		return this.name;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getCreatedDate() {
		return this.createdDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setCreatedDate(Instant date) {
		this.createdDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> createdDateProperty() {
		return this.createdDate;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getModifiedDate() {
		return this.modifiedDate.get();
	}

	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setModifiedDate(Instant date) {
		this.modifiedDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> modifiedDateProperty() {
		return this.modifiedDate;
	}
	
	@Override
	@JsonProperty
	public String getSource() {
		return this.source.get();
	}
	
	@JsonProperty
	public void setSource(String source) {
		this.source.set(source);
	}
	
	@Override
	@Watchable(name = "source")
	public StringProperty sourceProperty() {
		return this.source;
	}
	
	@Override
	@JsonProperty
	public String getCopyright() {
		return this.copyright.get();
	}
	
	@JsonProperty
	public void setCopyright(String copyright) {
		this.copyright.set(copyright);
	}
	
	@Override
	@Watchable(name = "copyright")
	public StringProperty copyrightProperty() {
		return this.copyright;
	}

	@Override
	@JsonProperty
	public String getCCLINumber() {
		return this.ccliNumber.get();
	}
	
	@JsonProperty
	public void setCCLINumber(String ccliNumber) {
		this.ccliNumber.set(ccliNumber);
	}
	
	@Override
	@Watchable(name = "ccliNumber")
	public StringProperty ccliNumberProperty() {
		return this.ccliNumber;
	}
	
	@Override
	@JsonProperty
	public String getReleased() {
		return this.released.get();
	}
	
	@JsonProperty
	public void setReleased(String released) {
		this.released.set(released);
	}
	
	@Override
	@Watchable(name = "released")
	public StringProperty releasedProperty() {
		return this.released;
	}
	
	@Override
	@JsonProperty
	public String getTransposition() {
		return this.transposition.get();
	}
	
	@JsonProperty
	public void setTransposition(String transposition) {
		this.transposition.set(transposition);
	}
	
	@Override
	@Watchable(name = "transposition")
	public StringProperty transpositionProperty() {
		return this.transposition;
	}

	@Override
	@JsonProperty
	public String getTempo() {
		return this.tempo.get();
	}
	
	@JsonProperty
	public void setTempo(String tempo) {
		this.tempo.set(tempo);
	}
	
	@Override
	@Watchable(name = "tempo")
	public StringProperty tempoProperty() {
		return this.tempo;
	}
	
	@Override
	@JsonProperty
	public String getKey() {
		return this.key.get();
	}
	
	@JsonProperty
	public void setKey(String key) {
		this.key.set(key);
	}
	
	@Override
	@Watchable(name = "key")
	public StringProperty keyProperty() {
		return this.key;
	}
	
	@Override
	@JsonProperty
	public String getVariant() {
		return this.variant.get();
	}
	
	@JsonProperty
	public void setVariant(String variant) {
		this.variant.set(variant);
	}
	
	@Override
	@Watchable(name = "variant")
	public StringProperty variantProperty() {
		return this.variant;
	}
	
	@Override
	@JsonProperty
	public String getPublisher() {
		return this.publisher.get();
	}
	
	@JsonProperty
	public void setPublisher(String publisher) {
		this.publisher.set(publisher);
	}
	
	@Override
	@Watchable(name = "publisher")
	public StringProperty publisherProperty() {
		return this.publisher;
	}
	
	@Override
	@JsonProperty
	public String getNotes() {
		return this.notes.get();
	}
	
	@JsonProperty
	public void setNotes(String notes) {
		this.notes.set(notes);
	}
	
	@Override
	@Watchable(name = "notes")
	public StringProperty notesProperty() {
		return this.notes;
	}
	
	@Override
	@JsonProperty
	public String getKeywords() {
		return this.keywords.get();
	}
	
	@JsonProperty
	public void setKeywords(String keywords) {
		this.keywords.set(keywords);
	}
	
	@Override
	@Watchable(name = "keywords")
	public StringProperty keywordsProperty() {
		return this.keywords;
	}
	
	@Override
	@JsonProperty
	public UUID getPrimaryLyrics() {
		return this.primaryLyrics.get();
	}
	
	@JsonProperty
	public void setPrimaryLyrics(UUID primaryLyrics) {
		this.primaryLyrics.set(primaryLyrics);
	}
	
	@Override
	@Watchable(name = "primaryLyrics")
	public ObjectProperty<UUID> primaryLyricsProperty() {
		return this.primaryLyrics;
	}
	
	@JsonProperty
	public void setLyrics(List<Lyrics> lyrics) {
		this.lyrics.setAll(lyrics);
	}
	
	@JsonProperty
	@Watchable(name = "lyrics")
	public ObservableList<Lyrics> getLyrics() {
		return this.lyrics;
	}
	
	@Override
	public ObservableList<? extends ReadOnlyLyrics> getLyricsUnmodifiable() {
		return this.lyricsReadOnly;
	}

	@Override
	@JsonProperty
	@Watchable(name = "tags")
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
	
	@Override
	@JsonProperty
	public void setTags(Set<Tag> tags) {
		this.tags.addAll(tags);
	}
	
	@Override
	public ObservableSet<Tag> getTagsUnmodifiable() {
		return this.tagsReadOnly;
	}

	@Override
	public Set<UUID> getDependencies() {
		return Collections.emptySet();
	}
}
