package org.praisenter.media;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum MediaType {
	IMAGE("Image"),
	VIDEO("Video"),
	AUDIO("Audio"),
	UNKNOWN("Unknown");
	
	private final String name;
	
	private MediaType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static final MediaType getMediaTypeFromMimeType(String mimeType) {
		if (mimeType.contains("image")) {
			return IMAGE;
		} else if (mimeType.contains("video")) {
			return VIDEO;
		} else if (mimeType.contains("audio")) {
			return AUDIO;
		} else {
			return UNKNOWN;
		}
	}
}
