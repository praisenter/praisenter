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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.TextType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a set of {@link BibleReferenceVerse}s.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleReferenceSet {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The references */
	@JsonProperty
	private final Set<BibleReferenceVerse> references;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates an empty set.
	 */
	public BibleReferenceSet() {
		this.references = new LinkedHashSet<>();
	}

	/**
	 * Returns a deep copy of this reference set.
	 * @return {@link BibleReferenceSet}
	 */
	public BibleReferenceSet copy() {
		BibleReferenceSet rs = new BibleReferenceSet();
		rs.references.addAll(this.references);
		return rs;
	}
	
	/**
	 * Returns the list of references.
	 * @return Set&lt;{@link BibleReferenceVerse}&gt;
	 */
	public Set<BibleReferenceVerse> getReferenceVerses() {
		return this.references;
	}

	/**
	 * Removes all the references in this set.
	 */
	public void clear() {
		this.references.clear();
	}
	
	/**
	 * Returns the text for the given type.
	 * @param type the type
	 * @return String
	 */
	public String getText(TextType type) {
		// check for null
		if (this.references == null) {
			return null;
		}
		
		// check for empty
		int size = this.references.size();
		if (size <= 0) {
			return null;
		}
		
		// check if the verses are together in the same
		// book, bible, and/or chapter
		boolean sameChapter = true;
		boolean sameBook = true;
		boolean sameBible = true;
		BibleReferenceVerse start = null;
		BibleReferenceVerse end = null;
		for (BibleReferenceVerse rv : this.references) {
			if (start == null) {
				start = rv;
				end = rv;
				continue;
			}
			
			if (rv != start) {
				if (!rv.getBibleId().equals(start.getBibleId())) {
					sameBible = false;
					sameBook = false;
					sameChapter = false;
					// no reason to continue
					break;
				}
				if (rv.getBookNumber() != start.getBookNumber()) {
					sameBook = false;
					sameChapter = false;
				}
				if (rv.getChapterNumber() != start.getChapterNumber()) {
					sameChapter = false;
				}
			}
			
			end = rv;
		}
		
		// by default it's a lose collection of verses
		BibleReferenceSetType rt = BibleReferenceSetType.COLLECTION;
		
		// if there's only one verse, then it's a single verse, clearly
		if (this.references.size() == 1) {
			rt = BibleReferenceSetType.SINGLE;
		}
		
		
		switch (rt) {
			case RANGE:
				if (start.getBibleId().equals(end.getBibleId())) {
					if (type == TextType.TITLE) {
						return getRangeTitle(start, end);
					} else {
						return getRangeText(this.references, sameBook, sameChapter);
					}
				} else {
					LOGGER.warn("Bible reference ranges across bibles is not supported.");
					return null;
				}
			case COLLECTION:
				if (type == TextType.TITLE) {
					return getCollectionTitle(start, sameBible, sameBook, sameChapter);
				} else {
					return getCollectionText(this.references, sameBible, sameBook, sameChapter);
				}
			case SINGLE:
			default:
				if (type == TextType.TITLE) {
					return MessageFormat.format("{0} {1}:{2}", start.getBookName(), start.getChapterNumber(), start.getVerseNumber());
				} else {
					return start.getText();
				}
		}
	}
	
	// private
	
	/**
	 * Returns the title text for a range of bible verses between start and end.
	 * @param start the start verse
	 * @param end the end verse
	 * @return String
	 */
	private static String getRangeTitle(BibleReferenceVerse start, BibleReferenceVerse end) {
		if (start.getBookNumber() == end.getBookNumber()) {
			if (start.getChapterNumber() == end.getChapterNumber()) {
				return MessageFormat.format("{0} {1}:{2}-{3}", start.getBookName(), start.getChapterNumber(), start.getVerseNumber(), end.getVerseNumber());
			} else {
				return MessageFormat.format("{0} {1}:{2}-{3}:{4}", start.getBookName(), start.getChapterNumber(), start.getVerseNumber(), end.getChapterNumber(), end.getVerseNumber());
			}
		} else {
			return MessageFormat.format("{0} {1}:{2} - {3} {4}:{5}", start.getBookName(), start.getChapterNumber(), start.getVerseNumber(), end.getBookName(), end.getChapterNumber(), end.getVerseNumber());
		}
	}

	/**
	 * Returns the title text for a collection of bible verses.
	 * @param start the first verse of the collection
	 * @param sameBible true if all the verses are in the same bible
	 * @param sameBook true if all the verses are in the same book
	 * @param sameChapter true if all the verses are in the same chapter
	 * @return String
	 */
	private static String getCollectionTitle(BibleReferenceVerse start, boolean sameBible, boolean sameBook, boolean sameChapter) {
		if (sameChapter) {
			return MessageFormat.format("{0} {1}", start.getBookName(), start.getChapterNumber());
		} else if (sameBook) {
			return start.getBookName();
		} else if (sameBible) {
			return start.getBibleName();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the body text for a range of bible verses.
	 * @param verses the verses in the range
	 * @param sameBook true if all the verses are in the same book
	 * @param sameChapter true if all the verses are in the same chapter
	 * @return String
	 */
	private static String getRangeText(Set<BibleReferenceVerse> verses, boolean sameBook, boolean sameChapter) {
		List<String> strings = new ArrayList<String>();
		BibleReferenceVerse last = null;
		for (BibleReferenceVerse rv : verses) {
			StringBuilder sb = new StringBuilder();
			if (last != null) {
				if (last.getBookNumber() == rv.getBookNumber()) {
					if (last.getChapterNumber() == rv.getChapterNumber()) {
						sb.append(" ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					} else {
						// chapter changed
						sb.append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					}
				} else {
					// book changed
					sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
				}
			} else if (sameChapter) {
				sb.append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBook) {
				sb.append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else {
				sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			}
			last = rv;
			strings.add(sb.toString());
		}
		return String.join(" ", strings);
	}
	
	/**
	 * Returns the body text for a collection of bible verses.
	 * @param verses the verses of the collection
	 * @param sameBible true if all the verses are in the same bible
	 * @param sameBook true if all the verses are in the same book
	 * @param sameChapter true if all the verses are in the same chapter
	 * @return String
	 */
	private static String getCollectionText(Set<BibleReferenceVerse> verses, boolean sameBible, boolean sameBook, boolean sameChapter) {
		List<String> strings = new ArrayList<String>();
		BibleReferenceVerse last = null;
		for (BibleReferenceVerse rv : verses) {
			StringBuilder sb = new StringBuilder();
			if (last != null) {
				if (last.getBibleId().equals(rv.getBibleId())) {
					if (last.getBookNumber() == rv.getBookNumber()) {
						if (last.getChapterNumber() == rv.getChapterNumber()) {
							sb.append(" ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
						} else {
							// chapter changed
							sb.append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
						}
					} else {
						// book changed
						sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					}
				} else {
					sb.append("[").append(rv.getBibleName()).append("] ").append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
				}
			} else if (sameChapter) {
				sb.append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBook) {
				sb.append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBible) {
				sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else {
				sb.append("[").append(rv.getBibleName()).append("] ").append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(":").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			}
			last = rv;
			strings.add(sb.toString());
		}
		return String.join(" ", strings);
	}
}
