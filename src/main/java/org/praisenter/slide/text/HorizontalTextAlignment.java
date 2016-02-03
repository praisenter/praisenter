package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum HorizontalTextAlignment {
	/** Text should be aligned to the left */
	LEFT,
	
	/** Text should be aligned to the right */
	RIGHT,
	
	/** Text should be aligned to the center */
	CENTER,
	
	/** Text should be aligned justified */
	JUSTIFY
}
