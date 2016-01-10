package org.praisenter.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "format")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaFormat {
	@XmlAttribute
	private final String name;
	
	@XmlAttribute
	private final String description;
	
	@XmlElementWrapper
	@XmlElement(name = "codec")
	private final List<MediaCodec> codecs;
	
	@SuppressWarnings("unused")
	private MediaFormat() {
		this(null, null);
	}
	
	public MediaFormat(String name, String description) {
		this.name = name;
		this.description = description;
		this.codecs = new ArrayList<MediaCodec>();
	}
	
	public MediaFormat(String name, String description, MediaCodec... codec) {
		this.name = name;
		this.description = description;
		this.codecs = Arrays.asList(codec);
	}
	
	public MediaFormat(String name, String description, List<MediaCodec> codecs) {
		this.name = name;
		this.description = description;
		this.codecs = codecs;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (codecs.size() > 0) {
			sb.append(" (");
			for (int i = 0; i < codecs.size(); i++) {
				MediaCodec codec = codecs.get(i);
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(codec.getName());
			}
			sb.append(")");
		}
		return sb.toString();
	}
	
	public String getCodecNames() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codecs.size(); i++) {
			MediaCodec codec = codecs.get(i);
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(codec.getName());
		}
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<MediaCodec> getCodecs() {
		return Collections.unmodifiableList(codecs);
	}
}
