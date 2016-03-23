package org.praisenter.slide.animation;

import javax.xml.bind.annotation.XmlEnum;

// FEATURE add corner directions

@XmlEnum
public enum Direction {
	// standard
	UP,
	RIGHT,
	DOWN,
	LEFT,
	
	// corners
//	UP_RIGHT,
//	UP_LEFT,
//	DOWN_RIGHT,
//	DOWN_LEFT,
	
	// circular
	CLOCKWISE,
	COUNTER_CLOCKWISE,
	WEDGE_UP,
	WEDGE_DOWN
}
