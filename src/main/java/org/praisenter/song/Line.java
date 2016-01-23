package org.praisenter.song;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
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
	 * Removes (unwraps) all tag tags from the line and remove new line characters and beginning whitespace.
	 * <p>
	 * The tag tags are there for formatting which we will likely not support in the
	 * same manner as another application.
	 * <p>
	 * Returns true if the song was modified.
	 * @return boolean
	 */
	public boolean prepare() {
		boolean modified = false;
		List<Object> unwrapped = new ArrayList<Object>();
		// pass 1
		// 1. remove tags
		// 2. remove new line characters (use <br/> instead)
		// 3. remove consecutive whitespace
		for (int i = 0; i < this.elements.size(); i++) {
			Object node = this.elements.get(i);
			if (node instanceof Tag) {
				modified = true;
				prepare((Tag)node, unwrapped);
			} else if (node instanceof String) {
				modified = true;
				String s = (String)node;
				// take out all new line characters
				s = s.replaceAll(Song.NEW_LINE_REGEX, "");
				// condense consecutive whitespace with a single whitespace
				s = s.replaceAll(Song.NEW_LINE_WHITESPACE, " ");
				unwrapped.add(s);
			} else {
				unwrapped.add(node);
			}
		}
		// pass 2
		// 1. strip beginning whitespace on each string node if it comes after a br
		Class<?> last = null;
		for (int i = 0; i < unwrapped.size(); i++) {
			Object node = unwrapped.get(i);
			if (node instanceof String) {
				modified = true;
				String s = (String)node;
				if (last == Br.class || last == null) {
					s = StringUtils.stripStart(s, null);
				}
				unwrapped.set(i, s);
			}
			last = node.getClass();
		}
		// we should only contain comment, br, chord, and String elements now
		this.elements = unwrapped;
		return modified;
	}
	
	/**
	 * Recursive method to remove/unwrap tag tags from this line.
	 * @param tag the tag to start from
	 * @param unwrapped the list of unwrapped elements
	 * @see #removeTags()
	 */
	private void prepare(Tag tag, List<Object> unwrapped) {
		for (int i = 0; i < tag.elements.size(); i++) {
			Object node = tag.elements.get(i);
			if (node instanceof Tag) {
				prepare((Tag)node, unwrapped);
			} else if (node instanceof String) {
				String s = (String)node;
				s = s.replaceAll(Song.NEW_LINE_REGEX, "");
				s = s.replaceAll(Song.NEW_LINE_WHITESPACE, " ");
				unwrapped.add(s);
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
