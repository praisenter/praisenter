package org.praisenter;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.bible.BibleReferenceSet;

// annoyingly need to define this here so that JAXB knows what classes
// it can instantiate and requires this to be an abstract class
@XmlSeeAlso({
	BibleReferenceSet.class
})
public abstract class TextTypeSet {
	public abstract String getText(TextType type);
	public abstract TextTypeSet copy();
}
