package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.DisplayText;
import org.praisenter.DisplayType;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.NONE)
public final class Tag implements DisplayText {
	/** The name of the tag */
	@XmlAttribute(name = "name")
	String name;
	
	/** The tag's child elements */
	@XmlElementRefs({
		@XmlElementRef(name = "tag", type = Tag.class),
		@XmlElementRef(name = "comment", type = LineComment.class),
		@XmlElementRef(name = "br", type = Br.class),
		@XmlElementRef(name = "chord", type = Chord.class)
	})
	@XmlMixed
	List<Object> elements;

	/** 
	 * Default constructor. 
	 */
	public Tag() {
		this.elements = new ArrayList<Object>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getDisplayText(DisplayType type) {
		StringBuilder sb = new StringBuilder();
		if (type == DisplayType.EDIT) {
			sb.append("<tag");
			if (this.name != null && this.name.length() > 0) {
				sb.append(" name='").append(this.name).append("'>");
			} else {
				sb.append(">");
			}
		}
		
		for (Object o : this.elements) {
			if (o instanceof String) {
				sb.append(o.toString());
			} else if (o instanceof DisplayText) {
				sb.append(((DisplayText)o).getDisplayText(type));
			}
		}
		
		if (type == DisplayType.EDIT) {
			sb.append("</tag>");
		}
		
		return sb.toString();
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
