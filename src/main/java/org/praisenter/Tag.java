package org.praisenter;

import java.text.Collator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.NONE)
public final class Tag implements Comparable<Tag> {
	private static final Collator COLLATOR = Collator.getInstance();
	
	@XmlAttribute
	private final String name;

	@SuppressWarnings("unused")
	private Tag() {
		// for jaxb
		this(null);
	}

	public Tag(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o instanceof Tag) {
			Tag tag = (Tag)o;
			if (tag.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Tag o) {
		return COLLATOR.compare(name, o.name);
	}
}
