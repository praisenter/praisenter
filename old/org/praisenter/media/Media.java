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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.Tag;
import org.praisenter.data.json.BufferedImagePngJsonDeserializer;
import org.praisenter.data.json.BufferedImagePngJsonSerializer;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.data.media.MediaType;
import org.praisenter.utility.MimeType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A media item in the media library.
 * <p>
 * The referenced media item should already be in a compatible format and should
 * have all necessary files in place in the media library at the time these
 * objects are received by a caller.
 * <p>
 * The Media class implements the Comparable interface to provide a default sort
 * based on the linked file path and name.
 * <p>
 * Instances of the Media object are immutable with one exception: tags.  The tags
 * should be mutated by calling the relevant methods in the {@link MediaLibrary} class.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@Type(value = Media.class, name = "media")
})
public final class Media implements Comparable<Media> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The collator for locale dependent sorting */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** Represents an unknown or not-applicable quantity */
	public static final int UNKNOWN = -1;

	/** The current version of this format */
	public static final String CURRENT_VERSION = "1";
	
	/** The format (for format identification only) */
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	private final String format;
	
	/** The version number */
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	private final String version;
	
	/** The media's unique id */
	@JsonProperty
	final UUID id;
	
	/** The path to the media file */
	Path path;
	
	/** The media's file name */
	@JsonProperty
	final String fileName;
	
	/** The media type */
	@JsonProperty
	final MediaType type;
	
	/** The mime type */
	@JsonProperty
	final String mimeType;
	
	/** The file name */
	@JsonProperty
	final String name;

	/** The date the file was added to the library */
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	final Instant dateAdded;
	
	/** The file's last modified timestamp */
	@JsonProperty
	final long lastModified;
	
	/** The file's size in bytes */
	@JsonProperty
	final long size;
	
	/** The file's format */
	@JsonProperty
	final MediaFormat mediaFormat;
	
	/** The file's width in pixels (if applicable) */
	@JsonProperty
	final int width;
	
	/** The file's height in pixels (if applicable) */
	@JsonProperty
	final int height;
	
	/** The file's length in seconds (if applicable) */
	@JsonProperty
	final long length;
	
	/** True if the media contains audio */
	@JsonProperty
	final boolean audio;
	
	/** The media's tags for searching/sorting */
	@JsonProperty
	final Set<Tag> tags;

	/** The media thumbnail */
	@JsonProperty
	@JsonSerialize(using = BufferedImagePngJsonSerializer.class)
	@JsonDeserialize(using = BufferedImagePngJsonDeserializer.class)
	final BufferedImage thumbnail;

	/**
	 * Returns a new {@link Media} for an image.
	 * @param path the path to the media
	 * @param format the format
	 * @param width the width
	 * @param height the height
	 * @param tags the tags; can be null
	 * @param thumbnail the image thumbnail
	 * @return {@link Media}
	 */
	static final Media forImage(Path path, MediaFormat format, int width, int height, Set<Tag> tags, BufferedImage thumbnail) {
		return new Media(null, path, format, width, height, UNKNOWN, false, null, tags, thumbnail);
	}
	
	/**
	 * Returns a new {@link Media} for audio.
	 * @param path the path to the media
	 * @param format the format
	 * @param length the length
	 * @param tags the tags; can be null
	 * @return {@link Media}
	 */
	static final Media forAudio(Path path, MediaFormat format, long length, Set<Tag> tags) {
		return new Media(null, path, format, UNKNOWN, UNKNOWN, length, true, null, tags, null);
	}

	/**
	 * Returns a new {@link Media} for a video.
	 * @param path the path to the media
	 * @param format the format
	 * @param width the width
	 * @param height the height
	 * @param length the length
	 * @param audio if the video contains audio
	 * @param tags the tags; can be null
	 * @param thumbnail the video thumbnail
	 * @return {@link Media}
	 */
	static final Media forVideo(Path path, MediaFormat format, int width, int height, long length, boolean audio, Set<Tag> tags, BufferedImage thumbnail) {
		return new Media(null, path, format, width, height, length, audio, null, tags, thumbnail);
	}
	
	/**
	 * Returns a new {@link Media} for a renamed media.
	 * @param path the new path
	 * @param media the old media metadata
	 * @return {@link Media}
	 */
	final static Media forRenamed(Path path, Media media) {
		return new Media(media.id, path, media.mediaFormat, media.width, media.height, media.length, media.audio, media.dateAdded, new TreeSet<Tag>(media.tags), media.thumbnail);
	}
	
	/**
	 * Returns a new {@link Media} for updated media.
	 * @param dateAdded the original date added
	 * @param tags the original tags
	 * @param media the new media metadata
	 * @return {@link Media}
	 */
	final static Media forUpdated(Instant dateAdded, Set<Tag> tags, Media media) {
		return new Media(media.id, media.path, media.mediaFormat, media.width, media.height, media.length, media.audio, dateAdded, tags, media.thumbnail);
	}
	
	/**
	 * Default constructor.
	 */
	private Media() {
		this.format = Constants.FORMAT_NAME;
		this.version = CURRENT_VERSION;
		this.id = UUID.randomUUID();
		this.mimeType = null;
		this.type = null;
		this.path = null;
		this.name = null;
		this.fileName = null;
		this.size = UNKNOWN;
		this.dateAdded = Instant.now();
		this.lastModified = UNKNOWN;
		this.mediaFormat = null;
		this.width = UNKNOWN;
		this.height = UNKNOWN;
		this.length = UNKNOWN;
		this.audio = false;
		this.tags = new TreeSet<Tag>();
		this.thumbnail = null;
	}
	
	/**
	 * Full constructor.
	 * @param id the id
	 * @param path the path to the media
	 * @param mediaFormat the format
	 * @param width the width; {@link #UNKNOWN} if not applicable
	 * @param height the height; {@link #UNKNOWN} if not applicable
	 * @param length the length; {@link #UNKNOWN} if not applicable
	 * @param audio if audio is present
	 * @param dateAdded the date the media was added to the library
	 * @param tags the tags; can be null
	 * @param thumbnail the media thumbnail
	 */
	private Media(UUID id, Path path, MediaFormat mediaFormat, int width, int height, long length, boolean audio, Instant dateAdded, Set<Tag> tags, BufferedImage thumbnail) {
		this.id = id == null ? UUID.randomUUID() : id;
		this.format = Constants.FORMAT_NAME;
		this.version = CURRENT_VERSION;
		
		// get the media type
		this.mimeType = MimeType.get(path);
		this.type = MediaType.getMediaTypeFromMimeType(this.mimeType);
		
		// paths and names
		this.path = path;
		String name = path.getFileName().toString();
		this.fileName = name;
		
		// remove extension (if present)
		int idx = name.lastIndexOf('.');
		if (idx >= 0) {
			name = name.substring(0, idx);
		}
		this.name = name;
		
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
		this.mediaFormat = mediaFormat;
		
		this.width = width;
		this.height = height;
		this.length = length;
		this.audio = audio;
		
		// mutable
		this.tags = (tags == null ? new TreeSet<Tag>() : tags);
		
		this.thumbnail = thumbnail;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Media o) {
		return COLLATOR.compare(this.name, o.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Media) {
			Media media = (Media)obj;
			// their type and path must be equal
			if (media.id.equals(this.id)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
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
	 * Returns the path to the frame for this (video) media.
	 * <p>
	 * Returns null if this media is audio or an image.
	 * @return Path
	 */
	public Path getFramePath() {
		if (this.type == MediaType.VIDEO) {
			return MediaLibrary.getFramePath(this.path);
		}
		return null;
	}

	/**
	 * Returns the media file name.
	 * @return String
	 */
	public String getFileName() {
		return this.fileName;
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
	public MediaFormat getMediaFormat() {
		return this.mediaFormat;
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
	
	/**
	 * Returns the thumbnail for this media.
	 * @return BufferedImage
	 */
	public BufferedImage getThumbnail() {
		return this.thumbnail;
	}

	/**
	 * Returns the format.
	 * @return String
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Returns the version.
	 * @return String
	 */
	public String getVersion() {
		return this.version;
	}
}
