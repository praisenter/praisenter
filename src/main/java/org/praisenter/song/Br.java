package org.praisenter.song;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.utility.RuntimeProperties;

@XmlRootElement(name = "br")
@XmlAccessorType(XmlAccessType.NONE)
public final class Br implements VerseFragment, SongOutput {
	private static final String BR = "<br/>";
	
	@Override
	public String toString() {
		return BR;
	}
	
	@Override
	public String getOutput(SongOutputType type) {
		if (type == SongOutputType.TEXT) {
			return RuntimeProperties.NEW_LINE_SEPARATOR;
		} else {
			return BR;
		}
	}
	
}
