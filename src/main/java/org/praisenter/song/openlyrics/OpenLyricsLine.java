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

import org.apache.commons.lang3.StringUtils;

@XmlRootElement(name = "lines")
@XmlAccessorType(XmlAccessType.NONE)
public final class OpenLyricsLine {
	/** Parts are ignored right now */
	@XmlAttribute(name = "part", required = false)
	String part;
	
	/** Break is ignored right now */
	@XmlAttribute(name = "break", required = false)
	String lineBreak;
	
	/** The sub elements, can be tag, comment, br, chord, or text all mixed together... lovely */
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
	public OpenLyricsLine() {
		this.elements = new ArrayList<Object>();
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
			if (node instanceof OpenLyricsTag) {
				modified = true;
				prepare((OpenLyricsTag)node, unwrapped);
			} else if (node instanceof String) {
				modified = true;
				String s = (String)node;
				// take out all new line characters
				s = s.replaceAll(OpenLyricsSong.NEW_LINE_REGEX, "");
				// condense consecutive whitespace with a single whitespace
				s = s.replaceAll(OpenLyricsSong.NEW_LINE_WHITESPACE, " ");
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
				if (last == OpenLyricsBr.class || last == null) {
					s = StringUtils.stripStart(s, null);
				}
				unwrapped.set(i, s);
			}
			last = node.getClass();
		}
		// pass 3
		// 1. condense adjacent String nodes, this will also remove empty string nodes
		StringBuilder sb = new StringBuilder();
		List<Object> condensed = new ArrayList<Object>();
		for (int i = 0; i < unwrapped.size(); i++) {
			Object node = unwrapped.get(i);
			if (node.getClass() == String.class) {
				sb.append(node);
				if (i + 1 != unwrapped.size() && unwrapped.get(i + 1).getClass() != String.class) {
					condensed.add(sb.toString());
					sb = new StringBuilder();
				}
			} else {
				condensed.add(node);
			}
		}
		if (sb.length() > 0) {
			condensed.add(sb.toString());
		}
		// we should only contain comment, br, chord, and String elements now
		this.elements = condensed;
		return modified;
	}
	
	/**
	 * Recursive method to remove/unwrap tag tags from this line.
	 * @param tag the tag to start from
	 * @param unwrapped the list of unwrapped elements
	 * @see #removeTags()
	 */
	private void prepare(OpenLyricsTag tag, List<Object> unwrapped) {
		for (int i = 0; i < tag.elements.size(); i++) {
			Object node = tag.elements.get(i);
			if (node instanceof OpenLyricsTag) {
				prepare((OpenLyricsTag)node, unwrapped);
			} else if (node instanceof String) {
				String s = (String)node;
				s = s.replaceAll(OpenLyricsSong.NEW_LINE_REGEX, "");
				s = s.replaceAll(OpenLyricsSong.NEW_LINE_WHITESPACE, " ");
				unwrapped.add(s);
			} else {
				unwrapped.add(node);
			}
		}
	}
	
	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public String getLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(String lineBreak) {
		this.lineBreak = lineBreak;
	}

	public List<Object> getElements() {
		return elements;
	}

	public void setElements(List<Object> elements) {
		this.elements = elements;
	}
}
