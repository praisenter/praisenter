/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.bible;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.Localized;
import org.praisenter.json.InstantJsonDeserializer;
import org.praisenter.json.InstantJsonSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a Bible translation.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@Type(value = Bible.class, name = "bible")
})
public final class Bible implements Comparable<Bible>, Serializable, Localized {
	/** The serialization id */
	private static final long serialVersionUID = 2081803110927884508L;

	/** The current version number */
	public static final String CURRENT_VERSION = "1";

	// final
	
	/** The format (for format identification only) */
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	private final String format;
	
	/** The version number */
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	private final String version;
	
	// internally modifiable

	/** The bible id */
	@JsonProperty
	private UUID id;

	/** The date the bible was created or imported */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant createdDate;
	
	// modifiable

	/** The date the bible was last modified */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	Instant modifiedDate;

	/** True if a warning was found during import */
	@JsonProperty
	boolean hadImportWarning;
	
	/** The name of the bible */
	@JsonProperty
	String name;
	
	/** The language the bible is in */
	@JsonProperty
	String language;

	/** The source for the bible's contents */
	@JsonProperty
	String source;
	
	/** The copyright */
	@JsonProperty
	String copyright;
	
	/** The books in this bible */
	@JsonProperty
	final List<Book> books;
	
	/** Any notes */
	@JsonProperty
	String notes;
	
	/**
	 * Default constructor.
	 */
	public Bible() {
		this(Constants.FORMAT_NAME, CURRENT_VERSION);
	}
	
	/**
	 * Full constructor.
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 * @param importDate the import date; can be null
	 * @param copyright the copyright (if any)
	 * @param notes bible notes
	 * @param hadImportWarning true if a warning occurred during import
	 * @param books the books for this bible
	 */
	public Bible(
		  String name, 
		  String language, 
		  String source, 
		  Instant importDate,
		  String copyright,
		  String notes,
		  boolean hadImportWarning,
		  List<Book> books) {
		this(
			Constants.FORMAT_NAME,
			CURRENT_VERSION,
			name,
			language,
			source,
			importDate,
			copyright,
			notes,
			hadImportWarning,
			books);
	}

	/**
	 * Constructor for JSON deserialization.
	 * @param format the format in the file
	 * @param version the version in the flie
	 */
	@JsonCreator
	private Bible(
			@JsonProperty("format") String format,
			@JsonProperty("version") String version) {
		this(format,
			version,
			null,
			null,
			null,
			null,
			null,
			null,
			false,
			null);
	}
	
	/**
	 * Full constructor.
	 * @param format the format
	 * @param version the version
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 * @param importDate the import date; can be null
	 * @param copyright the copyright (if any)
	 * @param notes bible notes
	 * @param hadImportWarning true if a warning occurred during import
	 * @param books the books for this bible
	 */
	private Bible(
		  String format,
		  String version,
		  String name, 
		  String language, 
		  String source, 
		  Instant importDate,
		  String copyright,
		  String notes,
		  boolean hadImportWarning,
		  List<Book> books) {
		this.format = format;
		this.version = version;
		this.id = UUID.randomUUID();
		this.name = name;
		this.language = language;
		this.source = source;
		this.createdDate = importDate != null ? importDate : Instant.now();
		this.modifiedDate = this.createdDate;
		this.copyright = copyright;
		this.notes = notes;
		this.hadImportWarning = hadImportWarning;
		this.books = books != null ? books : new ArrayList<Book>();
	}

	/**
	 * Copy constructor.
	 * @param bible the bible to copy
	 * @param exact whether to make an exact copy or not
	 */
	public Bible(Bible bible, boolean exact) {
		this.format = bible.format;
		this.version = bible.version;
		this.books = new ArrayList<Book>();
		this.copyright = bible.copyright;
		this.language = bible.language;
		this.name = bible.name;
		this.notes = bible.notes;
		this.source = bible.source;
		this.hadImportWarning = bible.hadImportWarning;
		
		if (exact) {
			this.id = bible.id;
			this.createdDate = bible.createdDate;
			this.modifiedDate = bible.modifiedDate;
		} else {
			this.id = UUID.randomUUID();
			this.createdDate = Instant.now();
			this.modifiedDate = this.createdDate;
		}
		
		for (Book book : bible.books) {
			this.books.add(book.copy());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Bible o) {
		if (o == null) return 1;
		// sort by id
		return this.name.compareTo(o.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.Localized#getLocale()
	 */
	@Override
	public Locale getLocale() {
		if (this.language != null) {
			return Locale.forLanguageTag(this.language);
		}
		return null;
	}
	
	/**
	 * Performs a deep copy of this bible.
	 * @param exact if an exact copy should be returned
	 * @return {@link Bible}
	 */
	public Bible copy(boolean exact) {
		return new Bible(this, exact);
	}
	
	/**
	 * Sets this bible as an instance of the given bible.
	 * <p>
	 * When this method is called this bible will now have
	 * the same id and name as the given bible, along with
	 * other internal properties. The bible data, books and
	 * their chapters and verses are not copied from the
	 * given bible.  Also, editable metadata is not copied.
	 * @param bible the bible act as
	 */
	public void as(Bible bible) {
		this.id = bible.id;
		this.name = bible.name;
		this.createdDate = bible.createdDate;
		this.hadImportWarning = bible.hadImportWarning;
		this.modifiedDate = bible.modifiedDate;
	}
	
	/**
	 * Returns the maximum book number in this bible.
	 * @return short
	 */
	public short getMaxBookNumber() {
		short max = 0;
		for (Book book : this.books) {
			max = max < book.number ? book.number : max;
		}
		return max;
	}
	
	/**
	 * Returns the id for this {@link Bible}.
	 * @return int
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the name of this {@link Bible}.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this {@link Bible}.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the language of this {@link Bible}.
	 * <p>
	 * The language code is not the ISO language code.
	 * @return String
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Sets the language for this bible.
	 * @param language the language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns the source for this {@link Bible}'s contents.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the source of this bible.
	 * @param source the source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the created date.
	 * @return Instant
	 */
	public Instant getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Returns the last modified date.
	 * @return Instant
	 */
	public Instant getModifiedDate() {
		return this.modifiedDate;
	}

	/**
	 * Sets the last modified date.
	 * @param date the last modified date
	 */
	public void setModifiedDate(Instant date) {
		this.modifiedDate = date;
	}
	
	/**
	 * Returns the copyright information (if any).
	 * @return String
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * Sets the copyright for this bible.
	 * @param copyright the copyright
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Sets the notes.
	 * @param notes the notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Returns the total verse count.
	 * @return int
	 */
	public int getVerseCount() {
		int n = 0;
		if (this.books != null) {
			for (Book book : this.books) {
				if (book.chapters != null) {
					for (Chapter chapter : book.chapters) {
						n += chapter.verses.size();
					}
				}
			}
		}
		return n;
	}
	
	/**
	 * Returns the number of books.
	 * @return int
	 */
	public int getBookCount() {
		if (this.books != null) {
			return this.books.size();
		}
		return 0;
	}

	/**
	 * Returns the specified verse or null if it doesn't exist.
	 * @param bookNumber the book number
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerse}
	 */
	public LocatedVerse getVerse(short bookNumber, short chapterNumber, short verseNumber) {
		for (Book book : this.books) {
			if (book.number == bookNumber) {
				for (Chapter chapter : book.chapters) {
					if (chapter.number == chapterNumber) {
						for (Verse verse : chapter.verses) {
							if (verse.number == verseNumber) {
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
	public LocatedVerse getNextVerse(short bookNumber, short chapterNumber, short verseNumber) {
		boolean found = false;
		for (Book book : this.books) {
			if (found || book.number == bookNumber) {
				for (Chapter chapter : book.chapters) {
					if (found || chapter.number == chapterNumber) {
						for (Verse verse : chapter.verses) {
							if (!found && verse.number == verseNumber) {
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
	public LocatedVerse getPreviousVerse(short bookNumber, short chapterNumber, short verseNumber) {
		boolean found = false;
		for (int i = this.books.size() - 1; i >= 0; i--) {
			Book book = this.books.get(i);
			if (found || book.number == bookNumber) {
				for (int j = book.chapters.size() - 1; j >= 0; j--) {
					Chapter chapter = book.chapters.get(j);
					if (found || chapter.number == chapterNumber) {
						for (int k = chapter.verses.size() - 1; k >= 0; k--) {
							Verse verse = chapter.verses.get(k);
							if (!found && verse.number == verseNumber) {
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
	public LocatedVerseTriplet getTriplet(short bookNumber, short chapterNumber, short verseNumber) {
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (Book book : this.books) {
			if (start || book.number == bookNumber) {
				for (Chapter chapter : book.chapters) {
					if (start || chapter.number == chapterNumber) {
						for (Verse verse : chapter.verses) {
							if (!start && verse.number == verseNumber) {
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
	public LocatedVerseTriplet getNextTriplet(short bookNumber, short chapterNumber, short verseNumber) {
		LocatedVerse previous = null;
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (Book book : this.books) {
			if (start || book.number == bookNumber) {
				for (Chapter chapter : book.chapters) {
					if (start || chapter.number == chapterNumber) {
						for (Verse verse : chapter.verses) {
							if (!start && verse.number == verseNumber) {
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
	public LocatedVerseTriplet getPreviousTriplet(short bookNumber, short chapterNumber, short verseNumber) {
		LocatedVerse previous = null;
		LocatedVerse current = null;
		LocatedVerse next = null;
		boolean start = false;
		for (int i = this.books.size() - 1; i >= 0; i--) {
			Book book = this.books.get(i);
			if (start || book.number == bookNumber) {
				for (int j = book.chapters.size() - 1; j >= 0; j--) {
					Chapter chapter = book.chapters.get(j);
					if (start || chapter.number == chapterNumber) {
						for (int k = chapter.verses.size() - 1; k >= 0; k--) {
							Verse verse = chapter.verses.get(k);
							if (!start && verse.number == verseNumber) {
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
	public LocatedVerseTriplet getMatchingTriplet(LocatedVerseTriplet triplet) {
		LocatedVerse previous = triplet.getPrevious();
		LocatedVerse current = triplet.getCurrent();
		LocatedVerse next = triplet.getNext();
		
		previous = previous != null ? this.getVerse(previous.getBook().number, previous.getChapter().number, previous.getVerse().number) : null;
		current = current != null ? this.getVerse(current.getBook().number, current.getChapter().number, current.getVerse().number) : null;;
		next = next != null ? this.getVerse(next.getBook().number, next.getChapter().number, next.getVerse().number) : null;;
		
		return new LocatedVerseTriplet(previous, current, next);
	}
	
	/**
	 * Returns the last book of this bible.
	 * @return {@link Book}
	 */
	public Book getLastBook() {
		if (this.books.isEmpty()) {
			return null;
		}
		return this.books.get(this.books.size() - 1);
	}
	
	/**
	 * Returns the books of this bible.
	 * @return List&lt;{@link Book}&gt;
	 */
	public List<Book> getBooks() {
		return this.books;
	}
	
	/**
	 * Returns true if a warning occurred during import of
	 * this bible.
	 * @return boolean
	 */
	public boolean hadImportWarning() {
		return this.hadImportWarning;
	}
	
	/**
	 * Returns the format.
	 * @return String
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Returns the version.
	 * @return String
	 */
	public String getVersion() {
		return this.version;
	}
}
