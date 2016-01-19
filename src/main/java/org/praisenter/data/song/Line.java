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

@XmlRootElement(name = "lines")
@XmlAccessorType(XmlAccessType.NONE)
public final class Line implements DisplayText {
	/** Parts are ignored right now */
	@XmlAttribute(name = "part", required = false)
	String part;
	
	// FIXME not handled at the moment, I'm not really sure what this does, the only value is "optional" which i think means you can break here into two slides if its too long?
	@XmlAttribute(name = "break", required = false)
	String lineBreak;
	
	/** The sub elements, can be tag, comment, br, chord, or text all mixed together... lovely */
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
	public Line() {
		this.elements = new ArrayList<Object>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getDisplayText(DisplayType.MAIN);
	}
	
	/**
	 * Removes (unwraps) all tag tags from the line.
	 * <p>
	 * The tag tags are there for formatting which we will likely not support in the
	 * same manner as another application.
	 * <p>
	 * Returns true if the line contained tag tags.
	 * @return boolean
	 */
	public boolean removeTags() {
		boolean containsTags = false;
		List<Object> unwrapped = new ArrayList<Object>();
		for (int i = 0; i < this.elements.size(); i++) {
			Object node = this.elements.get(i);
			if (node instanceof Tag) {
				containsTags = true;
				removeTags((Tag)node, unwrapped);
			} else {
				unwrapped.add(node);
			}
		}
		// we should only contain comment, br, chord, and String elements now
		this.elements = unwrapped;
		return containsTags;
	}
	
	/**
	 * Recursive method to remove/unwrap tag tags from this line.
	 * @param tag the tag to start from
	 * @param unwrapped the list of unwrapped elements
	 * @see #removeTags()
	 */
	private void removeTags(Tag tag, List<Object> unwrapped) {
		for (int i = 0; i < tag.elements.size(); i++) {
			Object node = tag.elements.get(i);
			if (node instanceof Tag) {
				removeTags((Tag)node, unwrapped);
			} else {
				unwrapped.add(node);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.DisplayText#getDisplayText(org.praisenter.DisplayType)
	 */
	@Override
	public String getDisplayText(DisplayType type) {
		StringBuilder sb = new StringBuilder();
		for (Object o : this.elements) {
			if (o instanceof String) {
				sb.append(o);
			} else if (o instanceof DisplayText) {
				sb.append(((DisplayText)o).getDisplayText(type));
			}
		}
		return sb.toString();
	}
	
	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
}
