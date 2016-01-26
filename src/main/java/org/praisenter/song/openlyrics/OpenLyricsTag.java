package org.praisenter.song.openlyrics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsTag {
	/** The name of the tag */
	@XmlAttribute(name = "name")
	String name;
	
	/** The tag's child elements */
	@XmlElementRefs({
		@XmlElementRef(name = "tag", type = OpenLyricsTag.class),
		@XmlElementRef(name = "comment", type = OpenLyricsLineComment.class),
		@XmlElementRef(name = "br", type = OpenLyricsBr.class),
		@XmlElementRef(name = "chord", type = OpenLyricsChord.class)
	})
	@XmlMixed
	List<Object> elements;

	/** 
	 * Default constructor. 
	 */
	public OpenLyricsTag() {
		this.elements = new ArrayList<Object>();
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
