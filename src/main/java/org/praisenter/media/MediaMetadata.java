/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
import org.praisenter.xml.adapters.InstantXmlAdapter;
import org.praisenter.xml.adapters.PathXmlAdapter;

/**
 * Metadata about a media file.
 * <p>
 * This class is used to cache metadata about a media file for faster load times.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaMetadata {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** Represents an unknown or not-applicable quantity */
	public static final int UNKNOWN = -1;
	
	/** The media's unique id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;
	
	/** The path to the media file */
	@XmlAttribute(name = "path", required = false)
	@XmlJavaTypeAdapter(value = PathXmlAdapter.class)
	final Path path;
	
	/** The media type */
	@XmlAttribute(name = "type", required = false)
	final MediaType type;
	
	/** The mime type */
	@XmlAttribute(name = "mimeType", required = false)
	final String mimeType;
	
	/** The file name */
	@XmlAttribute(name = "name", required = false)
	final String name;

	/** The date the file was added to the library */
	@XmlAttribute(name = "dateAdded", required = false)
	@XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
	final Instant dateAdded;
	
	/** The file's last modified timestamp */
	@XmlAttribute(name = "lastModified", required = false)
	final long lastModified;
	
	/** The file's size in bytes */
	@XmlAttribute(name = "size", required = false)
	final long size;
	
	/** The file's format */
	@XmlElement(name = "format", required = false)
	final MediaFormat format;
	
	/** The file's width in pixels (if applicable) */
	@XmlAttribute(name = "width", required = false)
	final int width;
	
	/** The file's height in pixels (if applicable) */
	@XmlAttribute(name = "height", required = false)
	final int height;
	
	/** The file's length in seconds (if applicable) */
	@XmlAttribute(name = "length", required = false)
	final long length;
	
	/** True if the media contains audio */
	@XmlAttribute(name = "audio", required = false)
	final boolean audio;
	
	/** The media's tags for searching/sorting */
	@XmlElementWrapper(name = "tags", required = false)
	@XmlElement(name = "tag", required = false)
	final Set<Tag> tags;

	/**
	 * Returns a new {@link MediaMetadata} for an image.
	 * @param path the path to the media
	 * @param format the format
	 * @param width the width
	 * @param height the height
	 * @param tags the tags; can be null
	 * @return {@link MediaMetadata}
	 */
	static final MediaMetadata forImage(Path path, MediaFormat format, int width, int height, Set<Tag> tags) {
		return new MediaMetadata(null, path, format, width, height, UNKNOWN, false, null, tags);
	}
	
	/**
	 * Returns a new {@link MediaMetadata} for audio.
	 * @param path the path to the media
	 * @param format the format
	 * @param length the length
	 * @param tags the tags; can be null
	 * @return {@link MediaMetadata}
	 */
	static final MediaMetadata forAudio(Path path, MediaFormat format, long length, Set<Tag> tags) {
		return new MediaMetadata(null, path, format, UNKNOWN, UNKNOWN, length, true, null, tags);
	}

	/**
	 * Returns a new {@link MediaMetadata} for a video.
	 * @param path the path to the media
	 * @param format the format
	 * @param width the width
	 * @param height the height
	 * @param length the length
	 * @param audio if the video contains audio
	 * @param tags the tags; can be null
	 * @return {@link MediaMetadata}
	 */
	static final MediaMetadata forVideo(Path path, MediaFormat format, int width, int height, long length, boolean audio, Set<Tag> tags) {
		return new MediaMetadata(null, path, format, width, height, length, audio, null, tags);
	}
	
	/**
	 * Returns a new {@link MediaMetadata} for a renamed media.
	 * @param path the new path
	 * @param media the old media metadata
	 * @return {@link MediaMetadata}
	 */
	final static MediaMetadata forRenamed(Path path, MediaMetadata media) {
		return new MediaMetadata(media.id, path, media.format, media.width, media.height, media.length, media.audio, media.dateAdded, new TreeSet<Tag>(media.tags));
	}
	
	/**
	 * Returns a new {@link MediaMetadata} for updated media.
	 * @param dateAdded the original date added
	 * @param tags the original tags
	 * @param media the new media metadata
	 * @return {@link MediaMetadata}
	 */
	final static MediaMetadata forUpdated(Instant dateAdded, Set<Tag> tags, MediaMetadata media) {
		return new MediaMetadata(media.id, media.path, media.format, media.width, media.height, media.length, media.audio, dateAdded, tags);
	}
	
	/**
	 * Default constructor.
	 * Used by JAXB.
	 */
	private MediaMetadata() {
		// for jaxb
		this.id = null;
		this.mimeType = null;
		this.type = null;
		this.path = null;
		this.name = null;
		this.size = UNKNOWN;
		this.dateAdded = Instant.now();
		this.lastModified = UNKNOWN;
		this.format = null;
		this.width = UNKNOWN;
		this.height = UNKNOWN;
		this.length = UNKNOWN;
		this.audio = false;
		this.tags = new TreeSet<Tag>();
	}
	
	/**
	 * Full constructor.
	 * @param id the id
	 * @param path the path to the media
	 * @param format the format
	 * @param width the width; {@link #UNKNOWN} if not applicable
	 * @param height the height; {@link #UNKNOWN} if not applicable
	 * @param length the length; {@link #UNKNOWN} if not applicable
	 * @param audio if audio is present
	 * @param dateAdded the date the media was added to the library
	 * @param tags the tags; can be null
	 */
	private MediaMetadata(UUID id, Path path, MediaFormat format, int width, int height, long length, boolean audio, Instant dateAdded, Set<Tag> tags) {
		this.id = id == null ? UUID.randomUUID() : id;
		
		// get the media type
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		this.mimeType = map.getContentType(path.toString());
		this.type = MediaType.getMediaTypeFromMimeType(this.mimeType);
		
		// paths and names
		this.path = path;
		this.name = path.getFileName().toString();
		
		// date added
		this.dateAdded = dateAdded != null ? dateAdded : Instant.now();
		
		// size & last modified
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

	/**
	 * Returns the unique id for this media.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the path to the media.
	 * @return Path
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Returns the media type.
	 * @return {@link MediaType}
	 */
	public MediaType getType() {
		return this.type;
	}

	/**
	 * Returns the mime type.
	 * @return String
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * Returns the file name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the date this file was added.
	 * @return Date
	 */
	public Instant getDateAdded() {
		return this.dateAdded;
	}
	
	/**
	 * Returns the last modified timestamp.
	 * @return long
	 */
	public long getLastModified() {
		return this.lastModified;
	}

	/**
	 * Returns the file size in bytes.
	 * @return long
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * Returns the media's format.
	 * @return {@link MediaFormat}
	 */
	public MediaFormat getFormat() {
		return this.format;
	}

	/**
	 * Returns the width of the media or {@link #UNKNOWN}
	 * if not applicable.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the media or {@link #UNKNOWN}
	 * if not applicable.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the length of the media or {@link #UNKNOWN}
	 * if not applicable.
	 * @return int
	 */
	public long getLength() {
		return this.length;
	}

	/**
	 * Returns true if the media contains audio.
	 * @return boolean
	 */
	public boolean hasAudio() {
		return this.audio;
	}

	/**
	 * Returns an unmodifiable set of the tags attached to
	 * this media.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.tags);
	}
}
