package org.praisenter.slide;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bibleSlide")
@XmlAccessorType(XmlAccessType.NONE)
public final class BibleSlide extends TemplatedSlide implements Slide, SlideRegion {
	// the properties describing the verse selected
	
	/** The book code */
	@XmlElement(name = "bookCode", required = false)
	String bookCode;
	
	/** The chapter */
	@XmlElement(name = "chapter", required = false)
	int chapter;
	
	/** The verse */
	@XmlElement(name = "verse", required = false)
	int verse;
	
	// Note: the order should be preserved so we shouldn't have to know
	// which element applies to the primary, secondary, etc. we can
	// just assume it by index in the list
	// Note: if the list is empty, the default bible should be used
	// Note: list of the names of the bibles (we don't have a transferable id)
	@XmlElementWrapper(name = "bibles", required = false)
	@XmlElement(name = "bible", required = false)
	final List<String> bibles;
	
	// constructors
	
	public BibleSlide() {
		this(null);
	}
	
	public BibleSlide(BasicSlide slide) {
		this(slide, null, -1, -1, null);
	}
	
	public BibleSlide(BasicSlide slide, String bookCode, int chapter, int verse, List<String> bibles) {
		// set the template id
		super(getRootTemplateId(slide));
		// copy the slide to this slide
		if (slide != null) {
			slide.copy((Slide)this);
		}
		// set the bible fields
		this.bookCode = bookCode;
		this.chapter = chapter;
		this.verse = verse;
		this.bibles = bibles != null ? new ArrayList<String>(bibles) : new ArrayList<String>();
	}
	

	/* (non-Javadoc)
	 * @see org.praisenter.slide.BasicSlide#copy()
	 */
	@Override
	public BibleSlide copy() {
		return new BibleSlide(this, this.bookCode, this.chapter, this.verse, this.bibles);
	}
	
	
	// getter/setters
	
	public String getBookCode() {
		return this.bookCode;
	}

	public void setBookCode(String bookCode) {
		this.bookCode = bookCode;
	}

	public int getChapter() {
		return this.chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	public int getVerse() {
		return this.verse;
	}

	public void setVerse(int verse) {
		this.verse = verse;
	}

	public List<String> getBibles() {
		return this.bibles;
	}
}
