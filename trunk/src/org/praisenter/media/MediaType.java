package org.praisenter.media;

import javax.xml.bind.annotation.XmlEnum;

/**
 * The supported media types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlEnum
public enum MediaType {
	/** Image media type */
	IMAGE,
	
	/** Video media type */
	VIDEO,
	
	/** Audio media type */
	AUDIO
}
