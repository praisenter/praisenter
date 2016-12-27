package org.praisenter.bible;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum BibleReferenceSetType {
	SINGLE,
	RANGE,
	COLLECTION
}
