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
package org.praisenter.data.media;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.praisenter.Constants;
import org.praisenter.Editable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.data.search.Indexable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

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
 * should be mutated by calling the relevant methods in the class.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "media")
@Editable
public final class Media implements ReadOnlyMedia, Indexable, Persistable, Copyable, Identifiable {
	/** Represents an unknown or not-applicable quantity */
	public static final int UNKNOWN = -1;
	
	/** The data type */
	public static final String DATA_TYPE_MEDIA = "media";

	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	private final StringProperty name;
	private final StringProperty extension;
	private final ObjectProperty<Instant> createdDate;
	private final ObjectProperty<Instant> modifiedDate;
	private final StringProperty mimeType;
	private final ObjectProperty<MediaType> mediaType;
	private final ObjectProperty<MediaFormat> mediaFormat;
	private final LongProperty size;
	private final IntegerProperty width;
	private final IntegerProperty height;
	private final LongProperty length;
	private final BooleanProperty audioAvailable;
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;
	
	private final ObjectProperty<Path> mediaPath;
	private final ObjectProperty<Path> mediaImagePath;
	private final ObjectProperty<Path> mediaThumbnailPath;
	
	/**
	 * Default constructor.
	 */
	public Media() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Constants.VERSION);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new SimpleStringProperty();
		this.extension = new SimpleStringProperty();
		this.createdDate = new SimpleObjectProperty<Instant>(Instant.now());
		this.modifiedDate = new SimpleObjectProperty<Instant>(this.createdDate.get());
		this.mimeType = new SimpleStringProperty();
		this.mediaType = new SimpleObjectProperty<MediaType>();
		this.size = new SimpleLongProperty(0);
		this.mediaFormat = new SimpleObjectProperty<MediaFormat>();
		this.width = new SimpleIntegerProperty(0);
		this.height = new SimpleIntegerProperty(0);
		this.length = new SimpleLongProperty(0);
		this.audioAvailable = new SimpleBooleanProperty(false);
		this.tags = FXCollections.observableSet(new TreeSet<Tag>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
		
		this.mediaPath = new SimpleObjectProperty<Path>();
		this.mediaImagePath = new SimpleObjectProperty<Path>();
		this.mediaThumbnailPath = new SimpleObjectProperty<Path>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.identityEquals(obj);
	}
	
	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof Media) {
			return other != null && ((Media)other).id.get().equals(this.id.get());
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
		return this.name.get();
	}

	/* (non-Javadoc)
	 * @see org.praisenter.persist.Copyable#copy()
	 */
	@Override
	public Media copy() {
		Media media = new Media();
		media.format.set(this.format.get());
		media.version.set(this.version.get());
		media.id.set(this.id.get());
		media.name.set(this.name.get());
		media.extension.set(this.extension.get());
		media.createdDate.set(this.createdDate.get());
		media.modifiedDate.set(this.modifiedDate.get());
		media.mimeType.set(this.mimeType.get());
		media.mediaType.set(this.mediaType.get());
		media.mediaFormat.set(this.mediaFormat.get());
		media.size.set(this.size.get());
		media.width.set(this.width.get());
		media.height.set(this.height.get());
		media.length.set(this.length.get());
		media.audioAvailable.set(this.audioAvailable.get());
		media.tags.addAll(this.tags);
		media.mediaPath.set(this.mediaPath.get());
		media.mediaImagePath.set(this.mediaImagePath.get());
		media.mediaThumbnailPath.set(this.mediaThumbnailPath.get());
		return media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.data.search.Indexable#index()
	 */
	@Override
	public List<Document> index() {
		List<Document> documents = new ArrayList<Document>();
		
		Document document = new Document();

		// allow filtering by the bible id
		document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
		
		// allow filtering by type
		document.add(new StringField(FIELD_TYPE, DATA_TYPE_MEDIA, Field.Store.YES));
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.name.get());
		
		for (Tag tag : this.tags) {
			sb.append(" ").append(tag.getName());
		}
		
		document.add(new TextField(FIELD_TEXT, sb.toString(), Field.Store.YES));
		
		documents.add(document);
		
		return documents;
	}
	
	@Override
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	public String getFormat() {
		return this.format.get();
	}
	
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	void setFormat(String format) {
		this.format.set(format);
	}
	
	@Override
	public ReadOnlyStringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	void setVersion(String version) {
		this.version.set(version);
	}
	
	@Override
	public ReadOnlyStringProperty versionProperty() {
		return this.version;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	@Editable("name")
	public StringProperty nameProperty() {
		return this.name;
	}

	@Override
	@JsonProperty
	public String getExtension() {
		return this.extension.get();
	}
	
	@JsonProperty
	void setExtension(String extension) {
		this.extension.set(extension);
	}
	
	@Override
	public StringProperty extensionProperty() {
		return this.extension;
	}
	
	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getCreatedDate() {
		return this.createdDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setCreatedDate(Instant date) {
		this.createdDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> createdDateProperty() {
		return this.createdDate;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getModifiedDate() {
		return this.modifiedDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setModifiedDate(Instant date) {
		this.modifiedDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> modifiedDateProperty() {
		return this.modifiedDate;
	}
	
	@Override
	@JsonProperty
	public MediaType getMediaType() {
		return this.mediaType.get();
	}
	
	@JsonProperty
	void setMediaType(MediaType mediaType) {
		this.mediaType.set(mediaType);
	}
	
	@Override
	public ObjectProperty<MediaType> mediaTypeProperty() {
		return this.mediaType;
	}
	
	@Override
	@JsonProperty
	public int getHeight() {
		return this.height.get();
	}
	
	@JsonProperty
	void setHeight(int height) {
		this.height.set(height);
	}

	@Override
	public IntegerProperty heightProperty() {
		return this.height;
	}
	
	@Override
	@JsonProperty
	public int getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	void setWidth(int width) {
		this.width.set(width);
	}
	
	@Override
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public long getLength() {
		return this.length.get();
	}
	
	@JsonProperty
	void setLength(long length) {
		this.length.set(length);
	}
	
	@Override
	public LongProperty lengthProperty() {
		return this.length;
	}
	
	@Override
	@JsonProperty
	public long getSize() {
		return this.size.get();
	}
	
	@JsonProperty
	void setSize(long size) {
		this.size.set(size);
	}
	
	@Override
	public LongProperty sizeProperty() {
		return this.size;
	}
	
	@Override
	@JsonProperty
	public MediaFormat getMediaFormat() {
		return this.mediaFormat.get();
	}
	
	@JsonProperty
	void setMediaFormat(MediaFormat mediaFormat) {
		this.mediaFormat.set(mediaFormat);
	}
	
	@Override
	public ObjectProperty<MediaFormat> mediaFormatProperty() {
		return this.mediaFormat;
	}
	
	@Override
	@JsonProperty
	public String getMimeType() {
		return this.mimeType.get();
	}
	
	@JsonProperty
	void setMimeType(String mimeType) {
		this.mimeType.set(mimeType);
	}
	
	@Override
	public StringProperty mimeTypeProperty() {
		return this.mimeType;
	}
	
	@JsonProperty
	@Override
	public boolean isAudioAvailable() {
		return this.audioAvailable.get();
	}
	
	@JsonProperty
	void setAudioAvailable(boolean audioAvailable) {
		this.audioAvailable.set(audioAvailable);
	}
	
	@Override
	public BooleanProperty audioAvailableProperty() {
		return this.audioAvailable;
	}
	
	@JsonProperty
	@Editable("tags")
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
	
	@JsonProperty
	public void setTags(Set<Tag> tags) {
		this.tags.addAll(tags);
	}
	
	@Override
	public ObservableSet<Tag> getTagsUnmodifiable() {
		return this.tagsReadOnly;
	}

	@Override
	public Path getMediaPath() {
		return this.mediaPath.get();
	}
	
	void setMediaPath(Path path) {
		this.mediaPath.set(path);
	}

	@Override
	public ReadOnlyObjectProperty<Path> mediaPathProperty() {
		return this.mediaPath;
	}
	
	@Override
	public Path getMediaImagePath() {
		return this.mediaImagePath.get();
	}
	
	void setMediaImagePath(Path path) {
		this.mediaImagePath.set(path);
	}

	@Override
	public ReadOnlyObjectProperty<Path> mediaImagePathProperty() {
		return this.mediaImagePath;
	}
	
	@Override
	public Path getMediaThumbnailPath() {
		return this.mediaThumbnailPath.get();
	}
	
	void setMediaThumbnailPath(Path path) {
		this.mediaThumbnailPath.set(path);
	}
	
	@Override
	public ReadOnlyObjectProperty<Path> mediaThumbnailPathProperty() {
		return this.mediaThumbnailPath;
	}
}
