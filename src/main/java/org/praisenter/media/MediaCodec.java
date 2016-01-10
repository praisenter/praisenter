package org.praisenter.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "codec")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaCodec {
	@XmlAttribute
	private final String name;
	
	@XmlAttribute
	private final String description;
	
	@XmlAttribute
	private final CodecType type;
	
	@SuppressWarnings("unused")
	private MediaCodec() {
		// for jaxb
		this(CodecType.UNKNOWN, null, null);
	}
	
	public MediaCodec(CodecType type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}
