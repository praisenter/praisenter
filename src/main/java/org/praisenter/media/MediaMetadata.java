package org.praisenter.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.xml.adapters.PathXmlAdapter;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaMetadata {
	private static final Logger LOGGER = LogManager.getLogger(MediaMetadata.class);
	public static final int UNKNOWN = -1;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(value = PathXmlAdapter.class)
	final Path path;
	
	@XmlAttribute
	final MediaType type;
	
	@XmlAttribute
	final String mimeType;
	
	@XmlAttribute
	final String name;
	
	@XmlAttribute
	final long lastModified;
	
	@XmlAttribute
	final long size;
	
	@XmlElement
	final MediaFormat format;
	
	@XmlAttribute
	final int width;
	
	@XmlAttribute
	final int height;
	
	@XmlAttribute
	final long length;
	
	@XmlAttribute
	final boolean audio;
	
	@XmlElementWrapper
	@XmlElement(name = "tag")
	final Set<Tag> tags;

	public static MediaMetadata forImage(Path path, MediaFormat format, int width, int height, Set<Tag> tags) {
		return new MediaMetadata(path, format, width, height, UNKNOWN, false, tags);
	}
	
	public static MediaMetadata forAudio(Path path, MediaFormat format, long length, Set<Tag> tags) {
		return new MediaMetadata(path, format, UNKNOWN, UNKNOWN, length, true, tags);
	}

	public static MediaMetadata forVideo(Path path, MediaFormat format, int width, int height, long length, boolean audio, Set<Tag> tags) {
		return new MediaMetadata(path, format, height, width, length, audio, tags);
	}
	
	public static MediaMetadata forRenamed(Path path, MediaMetadata media) {
		return new MediaMetadata(path, media.format, media.height, media.width, media.length, media.audio, new TreeSet<Tag>(media.tags));
	}
	
	private MediaMetadata() {
		// for jaxb
		this.mimeType = null;
		this.type = MediaType.UNKNOWN;
		this.path = null;
		this.name = null;
		this.size = UNKNOWN;
		this.lastModified = UNKNOWN;
		this.format = null;
		this.width = UNKNOWN;
		this.height = UNKNOWN;
		this.length = UNKNOWN;
		this.audio = false;
		this.tags = new TreeSet<Tag>();
	}
	
	private MediaMetadata(Path path, MediaFormat format, int width, int height, long length, boolean audio, Set<Tag> tags) {
		// get the media type
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		this.mimeType = map.getContentType(path.toString());
		this.type = MediaType.getMediaTypeFromMimeType(mimeType);
		
		// paths and names
		this.path = path;
		this.name = path.getFileName().toString();
		
		// size
		long size = UNKNOWN;
		long lastModified = UNKNOWN;
		try {
			BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
			size = attributes.size();
			lastModified = attributes.lastModifiedTime().toMillis();
		} catch (IOException ex) {
			LOGGER.warn("Unable to read file attributes for [" + path.toAbsolutePath().toString() + "]:", ex);
		}
		this.size = size;
		this.lastModified = lastModified;
		
		// the format
		this.format = format;
		
		this.width = width;
		this.height = height;
		this.length = length;
		this.audio = audio;
		
		// mutable
		this.tags = (tags == null ? new TreeSet<Tag>() : tags);
	}

	public Path getPath() {
		return path;
	}

	public MediaType getType() {
		return type;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getName() {
		return name;
	}

	public long getLastModified() {
		return lastModified;
	}

	public long getSize() {
		return size;
	}

	public MediaFormat getFormat() {
		return format;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getLength() {
		return length;
	}

	public boolean hasAudio() {
		return audio;
	}

	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(tags);
	}
}
