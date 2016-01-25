package org.praisenter.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class Verse implements DisplayText {
	/** The verse name */
	@XmlAttribute(name = "name", required = false)
	String name;
	
	/** The verse lines */
	@XmlElement(name = "fragment")
	@XmlElementWrapper(name = "fragments", required = false)
	List<VerseFragment> fragments;
	
	@XmlAttribute(name = "fontSize", required = false)
	int fontSize;
	
	public Verse() {
		this.name = "c1";
		this.fragments = new ArrayList<VerseFragment>();
		this.fontSize = 60;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getDisplayText(DisplayType.MAIN);
	}
	
	@Override
	public String getDisplayText(DisplayType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<VerseFragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<VerseFragment> fragments) {
		this.fragments = fragments;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
