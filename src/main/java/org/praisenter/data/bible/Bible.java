package org.praisenter.data.bible;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.praisenter.Constants;
import org.praisenter.Editable;
import org.praisenter.Version;
import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Localized;
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
@JsonTypeName(value = "bible")
@Editable
public final class Bible implements ReadOnlyBible, Indexable, Persistable, Copyable, Identifiable, Localized {
	/** The lucene field to store the book number as a searchable value */
	public static final String FIELD_BOOK_ID = "bookid";
	
	/** The lucene field to store the book number */
	public static final String FIELD_BOOK_NUMBER = "booknumber";
	
	/** The lucene field to store the chapter number */
	public static final String FIELD_VERSE_CHAPTER = "chapter";
	
	/** The lucene field to store the verse number */
	public static final String FIELD_VERSE_NUMBER = "verse";
	
	/** The data type */
	public static final String DATA_TYPE_BIBLE = "bible";
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	private final StringProperty name;
	private final ObjectProperty<Instant> createdDate;
	private final ObjectProperty<Instant> modifiedDate;
	private final StringProperty language;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty notes;
	
	private final ObservableList<Book> books;
	private final ObservableList<Book> booksReadOnly;
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;

	public Bible() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Version.STRING);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new SimpleStringProperty();
		this.createdDate = new SimpleObjectProperty<>(Instant.now());
		this.modifiedDate = new SimpleObjectProperty<>(this.createdDate.get());
		this.language = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		
		this.books = FXCollections.observableArrayList();
		this.booksReadOnly = FXCollections.unmodifiableObservableList(this.books);
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
	}
	
	public Bible(String name) {
		this();
		this.name.set(name);
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
		if (other instanceof Bible) {
			Bible bible = (Bible)other;
			return bible.id.get().equals(this.id.get());
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.persist.Copyable#copy()
	 */
	@Override
	public Bible copy() {
		Bible b = new Bible();
		b.format.set(this.format.get());
		b.version.set(this.version.get());
		b.id.set(this.id.get());
		b.name.set(this.name.get());
		b.createdDate.set(this.createdDate.get());
		b.modifiedDate.set(this.modifiedDate.get());
		b.language.set(this.language.get());
		b.source.set(this.source.get());
		b.copyright.set(this.copyright.get());
		b.notes.set(this.notes.get());
		for (Book book : this.books) {
			b.books.add(book.copy());
		}
		b.tags.addAll(this.tags);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name.get();
	}
	
	@Override
	public Locale getLocale() {
		String language = this.language.get();
		if (language != null) {
			return Locale.forLanguageTag(language);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.search.Indexable#index()
	 */
	@Override
	public List<Document> index() {
		List<Document> documents = new ArrayList<Document>();
		for (Book book : this.getBooks()) {
			for (Chapter chapter : book.getChapters()) {
				for (Verse verse : chapter.getVerses()) {
					Document document = new Document();

					// allow filtering by the bible id
					document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
					
					// allow filtering by type
					document.add(new StringField(FIELD_TYPE, DATA_TYPE_BIBLE, Field.Store.YES));
					
					// allow filtering by the book number
					document.add(new IntPoint(FIELD_BOOK_ID, book.getNumber()));
					
					// stored data so we can look up the verse
					document.add(new StoredField(FIELD_BOOK_NUMBER, book.getNumber()));
					document.add(new StoredField(FIELD_VERSE_CHAPTER, chapter.getNumber()));
					document.add(new StoredField(FIELD_VERSE_NUMBER, verse.getNumber()));
					
					if (!StringManipulator.isNullOrEmpty(verse.getText())) {
						document.add(new TextField(FIELD_TEXT, verse.getText(), Field.Store.YES));
					}
					
					documents.add(document);
				}
			}
		}
		
		String tags = this.tags.stream().map(t -> t.getName()).collect(Collectors.joining(" "));
		if (!StringManipulator.isNullOrEmpty(tags)) {
			Document document = new Document();
			document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
			document.add(new StringField(FIELD_TYPE, DATA_TYPE_BIBLE, Field.Store.YES));
			document.add(new TextField(FIELD_TAGS, tags, Field.Store.YES));
			documents.add(document);
		}
		
		return documents;
	}
	
	/**
	 * Returns the total verse count.
	 * @return int
	 */
	@Override
	public int getVerseCount() {
		int n = 0;
		for (Book book : this.books) {
			for (Chapter chapter : book.getChapters()) {
				n += chapter.getVerses().size();
			}
		}
		return n;
	}
	
	/**
	 * Returns the number of books.
	 * @return int
	 */
	@Override
	public int getBookCount() {
		return this.books.size();
	}

	/**
	 * Returns the specified verse or null if it doesn't exist.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerse}
	 */
	@Override
	public LocatedVerse getVerse(int bookNumber, int chapterNumber, int verseNumber) {
		for (Book book : this.books) {
			if (bookNumber == book.getNumber()) {
				for (Chapter chapter : book.getChapters()) {
					if (chapterNumber == chapter.getNumber()) {
						for (Verse verse : chapter.getVerses()) {
							if (verse.getNumber() == verseNumber) {
								return new LocatedVerse(this, book, chapter, verse);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the next verse after the given location or null if one doesn't exist.
	 * <p>
	 * The next verse is defined as the next in the list of verses, not necessarily the
	 * next verse in terms of number. This should be handled on bible editing side of
	 * things.
	 * <p>
	 * This method will cross book and chapter boundaries.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerse}
	 */
	@Override
	public LocatedVerse getNextVerse(int bookNumber, int chapterNumber, int verseNumber) {
		boolean found = false;
		for (Book book : this.books) {
			if (found || book.getNumber() == bookNumber) {
				for (Chapter chapter : book.getChapters()) {
					if (found || chapter.getNumber() == chapterNumber) {
						for (Verse verse : chapter.getVerses()) {
							if (!found && verse.getNumber() == verseNumber) {
								// we've found the verse
								// so try to go to the next one
								found = true;
								continue;
							}
							if (found) {
								return new LocatedVerse(this, book, chapter, verse);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the previous verse before the given location or null if one doesn't exist.
	 * <p>
	 * The previous verse is defined as the previous in the list of verses, not necessarily the
	 * previous verse in terms of number. This should be handled on bible editing side of
	 * things.
	 * <p>
	 * This method will cross book and chapter boundaries.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerse}
	 */
	@Override
	public LocatedVerse getPreviousVerse(int bookNumber, int chapterNumber, int verseNumber) {
		boolean found = false;
		for (int i = this.books.size() - 1; i >= 0; i--) {
			Book book = this.books.get(i);
			if (found || book.getNumber() == bookNumber) {
				List<Chapter> chapters = book.getChapters();
				for (int j = chapters.size() - 1; j >= 0; j--) {
					Chapter chapter = chapters.get(j);
					if (found || chapter.getNumber() == chapterNumber) {
						List<Verse> verses = chapter.getVerses();
						for (int k = verses.size() - 1; k >= 0; k--) {
							Verse verse = verses.get(k);
							if (!found && verse.getNumber() == verseNumber) {
								// we've found the verse
								// so try to go to the next one
								found = true;
								continue;
							}
							if (found) {
								return new LocatedVerse(this, book, chapter, verse);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the verse for the given location or null if one doesn't exist.
	 * <p>
	 * This method will also return the previous and the next. For example, 
	 * if the triplet of Genesis 1:5 is requested, this method will return 
	 * previous = Genesis 1:4, current = Genesis 1:5, and next = Genesis 1:6.
	 * <p>
	 * The next verse is defined as the next in the list of verses, not necessarily the
	 * next verse in terms of number. This should be handled on bible editing side of
	 * things.
	 * <p>
	 * This method will cross book and chapter boundaries.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerseTriplet}
	 */
	@Override
	public LocatedVerseTriplet getTriplet(int bookNumber, int chapterNumber, int verseNumber) {
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (Book book : this.books) {
			if (start || book.getNumber() == bookNumber) {
				for (Chapter chapter : book.getChapters()) {
					if (start || chapter.getNumber() == chapterNumber) {
						for (Verse verse : chapter.getVerses()) {
							if (!start && verse.getNumber() == verseNumber) {
								// we've found the verse
								// so try to go to the next one
								current = new LocatedVerse(this, book, chapter, verse);
								start = true;
								continue;
							} else if (start && next == null) {
								next = new LocatedVerse(this, book, chapter, verse);
							}
						}
					}
				}
			}
		}
		if (current != null) {
			return new LocatedVerseTriplet(
					this.getPreviousVerse(bookNumber, chapterNumber, verseNumber), 
					current, 
					next);
		}
		return null;
	}
	
	/**
	 * Returns the next verse after the given location or null if one doesn't exist.
	 * <p>
	 * This method will also return the next-next and the next-previous. For example, 
	 * if the next triplet of Genesis 1:5 is requested, this method will return 
	 * previous = Genesis 1:5, current = Genesis 1:6, and next = Genesis 1:7.
	 * <p>
	 * The next verse is defined as the next in the list of verses, not necessarily the
	 * next verse in terms of number. This should be handled on bible editing side of
	 * things.
	 * <p>
	 * This method will cross book and chapter boundaries.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerseTriplet}
	 */
	@Override
	public LocatedVerseTriplet getNextTriplet(int bookNumber, int chapterNumber, int verseNumber) {
		LocatedVerse previous = null;
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (Book book : this.books) {
			if (start || book.getNumber() == bookNumber) {
				for (Chapter chapter : book.getChapters()) {
					if (start || chapter.getNumber() == chapterNumber) {
						for (Verse verse : chapter.getVerses()) {
							if (!start && verse.getNumber() == verseNumber) {
								// we've found the verse
								// so try to go to the next one
								previous = new LocatedVerse(this, book, chapter, verse);
								start = true;
								continue;
							} else if (start && current == null) {
								current = new LocatedVerse(this, book, chapter, verse);
							} else if (start && next == null) {
								next = new LocatedVerse(this, book, chapter, verse);
								return new LocatedVerseTriplet(previous, current, next);
							}
						}
					}
				}
			}
		}
		if (current != null) {
			return new LocatedVerseTriplet(previous, current, next);
		}
		return null;
	}
	
	/**
	 * Returns the previous verse before the given location or null if one doesn't exist.
	 * <p>
	 * This method will also return the previous-previous and the previous-next. For example, 
	 * if the previous triplet of Genesis 1:5 is requested, this method will return 
	 * previous = Genesis 1:3, current = Genesis 1:4, and next = Genesis 1:5.
	 * <p>
	 * The previous verse is defined as the previous in the list of verses, not necessarily the
	 * previous verse in terms of number. This should be handled on bible editing side of
	 * things.
	 * <p>
	 * This method will cross book and chapter boundaries.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerseTriplet}
	 */
	@Override
	public LocatedVerseTriplet getPreviousTriplet(int bookNumber, int chapterNumber, int verseNumber) {
		LocatedVerse previous = null;
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (int i = this.books.size() - 1; i >= 0; i--) {
			Book book = this.books.get(i);
			if (start || book.getNumber() == bookNumber) {
				List<Chapter> chapters = book.getChapters();
				for (int j = chapters.size() - 1; j >= 0; j--) {
					Chapter chapter = chapters.get(j);
					if (start || chapter.getNumber() == chapterNumber) {
						List<Verse> verses = chapter.getVerses();
						for (int k = verses.size() - 1; k >= 0; k--) {
							Verse verse = verses.get(k);
							if (!start && verse.getNumber() == verseNumber) {
								// we've found the verse
								// so try to go to the next one
								next = new LocatedVerse(this, book, chapter, verse);
								start = true;
								continue;
							} else if (start && current == null) {
								current = new LocatedVerse(this, book, chapter, verse);
							} else if (start && previous == null) {
								previous = new LocatedVerse(this, book, chapter, verse);
								return new LocatedVerseTriplet(previous, current, next);
							}
						}
					}
				}
			}
		}
		if (current != null) {
			return new LocatedVerseTriplet(previous, current, next);
		}
		return null;
	}
	
	/**
	 * Returns a matching triplet from this bible for given triplet.
	 * @param triplet the triplet to find
	 * @return {@link LocatedVerseTriplet}
	 */
	@Override
	public LocatedVerseTriplet getMatchingTriplet(LocatedVerseTriplet triplet) {
		LocatedVerse previous = triplet.getPrevious();
		LocatedVerse current = triplet.getCurrent();
		LocatedVerse next = triplet.getNext();
		
		previous = previous != null ? this.getVerse(previous.getBook().getNumber(), previous.getChapter().getNumber(), previous.getVerse().getNumber()) : null;
		current = current != null ? this.getVerse(current.getBook().getNumber(), current.getChapter().getNumber(), current.getVerse().getNumber()) : null;;
		next = next != null ? this.getVerse(next.getBook().getNumber(), next.getChapter().getNumber(), next.getVerse().getNumber()) : null;;
		
		return new LocatedVerseTriplet(previous, current, next);
	}
	
	/**
	 * Returns the last book of this bible.
	 * @return {@link Book}
	 */
	@Override
	public Book getLastBook() {
		if (this.books.isEmpty()) {
			return null;
		}
		return this.books.get(this.books.size() - 1);
	}
	
	/**
	 * Attempts to find a matching book for the given book.
	 * @param book the book to find
	 * @return {@link Book}
	 */
	@Override
	public Book getMatchingBook(ReadOnlyBook book) {
		if (book == null) return null;
		// try name first
		for (Book b : this.books) {
			if (b.getName().equalsIgnoreCase(book.getName())) {
				return b;
			}
		}
		// then try number
		for (Book b : this.books) {
			if (b.getNumber() == book.getNumber()) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Returns the highest book number among all the books.
	 * @return int
	 */
	@Override
	public int getMaxBookNumber() {
		int max = -Integer.MAX_VALUE;
		for (Book book : this.books) {
			int n = book.getNumber();
			if (n > max) {
				max = n;
			}
		}
		return max >= 0 ? max : 0;
	}
	
	public void renumber() {
		int n = 1;
		for (Book book : this.books) {
			book.setNumber(n++);
			book.renumber();
		}
	}
	
	public void reorder() {
		FXCollections.sort(this.books);
		for (Book book : this.books) {
			book.reorder();
		}
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
	
	@JsonProperty
	public void setBooks(List<Book> books) {
		this.books.setAll(books);
	}
	
	@JsonProperty
	@Watchable(name = "books")
	public ObservableList<Book> getBooks() {
		return this.books;
	}
	
	@Override
	public ObservableList<? extends ReadOnlyBook> getBooksUnmodifiable() {
		return this.booksReadOnly;
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
}
