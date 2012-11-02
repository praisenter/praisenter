package org.praisenter.slide.io;

import org.praisenter.slide.Slide;

import com.xuggle.xuggler.Version;

public class TemplateXmlGenerator {
	/** The template generator version 1 */
	public static final int VERSION_1 = 1;
	
	/** The current version */
	public static final int CURRENT_VERSION = VERSION_1;
	
	/** The array of all versions */
	public static final int[] VERSIONS = new int[] {
		VERSION_1
	};
	
	// TODO create xsd for this file
	public static final String toXml(Slide slide) {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
	
	private static final String toXml() {
		return null;
	}
}
