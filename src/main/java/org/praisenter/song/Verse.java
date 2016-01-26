package org.praisenter.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "verse")
@XmlAccessorType(XmlAccessType.NONE)
public final class Verse implements SongOutput {
	
	String type;
	int number;
	String part;
	
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
		this.type = "c";
		this.number = 1;
		this.part = null;
		this.name = "c1";
		this.fragments = new ArrayList<VerseFragment>();
		this.fontSize = 60;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getOutput(SongOutputType.TEXT);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getOutput(SongOutputType type) {
		StringBuilder sb = new StringBuilder();
		for (VerseFragment fragment : this.fragments) {
			sb.append(fragment.getOutput(type));
		}
		return sb.toString();
	}
	
	private void setName() {
		this.name = 
				(this.type == null || this.type.length() == 0 ? "c" : this.part) + 
				this.number + 
				(this.part == null || this.part.length() == 0 ? "" : this.part);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		setName();
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		setName();
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
		setName();
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
