package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum SlideStrokeJoin {
	/** The default bevel join type */
	BEVEL,
	
	/** The miter join type */
	MITER,
	
	/** The round join type */
	ROUND;
}
