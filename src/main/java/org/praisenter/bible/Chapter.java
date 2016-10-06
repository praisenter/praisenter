package org.praisenter.bible;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chapter")
@XmlAccessorType(XmlAccessType.NONE)
public class Chapter implements Comparable<Chapter> {
	@XmlAttribute(name = "chapter", required = false)
	short number;
	
	@XmlElement(name = "verse", required = false)
	@XmlElementWrapper(name = "verses", required = false)
	final List<Verse> verses;
	
	public Chapter() {
		this((short)0);
	}
	
	public Chapter(short number) {
		this(number, null);
	}
	
	public Chapter(short number, List<Verse> verses) {
		this.number = number;
		this.verses = verses != null ? verses : new ArrayList<Verse>();
	}
	
	@Override
	public int compareTo(Chapter c) {
		if (c == null) return 1;
		return this.number - c.number;
	}

	public short getMaxVerseNumber() {
		short max = 0;
		for (Verse verse : this.verses) {
			max = max < verse.number ? verse.number : max;
		}
		return max;
	}
	
	public Chapter copy() {
		Chapter chapter = new Chapter();
		chapter.number = this.number;
		
		for (Verse verse : this.verses) {
			chapter.verses.add(verse.copy());
		}
		
		return chapter;
	}
	
	public short getNumber() {
		return number;
	}

	public void setNumber(short number) {
		this.number = number;
	}

	public List<Verse> getVerses() {
		return verses;
	}
	
}
