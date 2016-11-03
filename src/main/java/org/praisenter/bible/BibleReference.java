package org.praisenter.bible;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bibleReference")
@XmlAccessorType(XmlAccessType.NONE)
public final class BibleReference {
	public static final short NOT_SET = -1;
	
	@XmlAttribute(name = "bibleId", required = false)
	private final UUID bibleId;
	
	@XmlAttribute(name = "bookNumber", required = false)
	private final short bookNumber;
	
	@XmlAttribute(name = "chapterNumber", required = false)
	private final short chapterNumber;
	
	@XmlAttribute(name = "verseNumber", required = false)
	private final short verseNumber;
	
	private BibleReference() {
		// for jaxb
		this.bibleId = null;
		this.bookNumber = NOT_SET;
		this.chapterNumber = NOT_SET;
		this.verseNumber = NOT_SET;
	}
	
	public BibleReference(UUID bibleId, short bookNumber, short chapterNumber, short verseNumber) {
		this.bibleId = bibleId;
		this.bookNumber = bookNumber;
		this.chapterNumber = chapterNumber;
		this.verseNumber = verseNumber;
	}

	public UUID getBibleId() {
		return this.bibleId;
	}

	public short getBookNumber() {
		return this.bookNumber;
	}

	public short getChapterNumber() {
		return this.chapterNumber;
	}

	public short getVerseNumber() {
		return this.verseNumber;
	}
}
