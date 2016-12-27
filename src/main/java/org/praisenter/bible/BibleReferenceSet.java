package org.praisenter.bible;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.TextStore;
import org.praisenter.TextType;
import org.praisenter.TextVariant;

@XmlRootElement(name = "bibleReferenceSet")
@XmlAccessorType(XmlAccessType.NONE)
public final class BibleReferenceSet {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@XmlAttribute(name = "type")
	private BibleReferenceSetType type;
	
	@XmlElement(name = "reference")
	@XmlElementWrapper(name = "references")
	private final Set<BibleReferenceVerse> references;
	
	public BibleReferenceSet() {
		this.type = BibleReferenceSetType.COLLECTION;
		this.references = new LinkedHashSet<>();
	}
	
	public BibleReferenceSetType getType() {
		return this.type;
	}

	public void setType(BibleReferenceSetType type) {
		this.type = type;
	}

	public Set<BibleReferenceVerse> getReferenceVerses() {
		return this.references;
	}

	public BibleReferenceSet copy() {
		BibleReferenceSet rs = new BibleReferenceSet();
		rs.type = this.type;
		rs.references.addAll(this.references);
		return rs;
	}
	
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
		
		BibleReferenceSetType rt = this.type;
		if (this.type == null) {
			rt = BibleReferenceSetType.COLLECTION;
		}
		if (start == end) {
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
	
	private static String getRangeText(Set<BibleReferenceVerse> verses, boolean sameBook, boolean sameChapter) {
		StringBuilder sb = new StringBuilder();
		BibleReferenceVerse last = null;
		for (BibleReferenceVerse rv : verses) {
			if (last != null) {
				if (last.getBookNumber() == rv.getBookNumber()) {
					if (last.getChapterNumber() == rv.getChapterNumber()) {
						sb.append(" ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					} else {
						// chapter changed
						sb.append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					}
				} else {
					// book changed
					sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
				}
			} else if (sameChapter) {
				sb.append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBook) {
				sb.append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else {
				sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			}
			last = rv;
		}
		return sb.toString();
	}
	
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
	
	private static String getCollectionText(Set<BibleReferenceVerse> verses, boolean sameBible, boolean sameBook, boolean sameChapter) {
		StringBuilder sb = new StringBuilder();
		BibleReferenceVerse last = null;
		for (BibleReferenceVerse rv : verses) {
			if (last != null) {
				if (last.getBibleId().equals(rv.getBibleId())) {
					if (last.getBookNumber() == rv.getBookNumber()) {
						if (last.getChapterNumber() == rv.getChapterNumber()) {
							sb.append(" ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
						} else {
							// chapter changed
							sb.append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
						}
					} else {
						// book changed
						sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
					}
				} else {
					sb.append("[").append(rv.getBibleName()).append("] ").append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
				}
			} else if (sameChapter) {
				sb.append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBook) {
				sb.append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else if (sameBible) {
				sb.append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			} else {
				sb.append("[").append(rv.getBibleName()).append("] ").append(rv.getBookName()).append(" ").append(rv.getChapterNumber()).append(": ").append(rv.getVerseNumber()).append(" ").append(rv.getText());
			}
			last = rv;
		}
		return sb.toString();
	}
}
