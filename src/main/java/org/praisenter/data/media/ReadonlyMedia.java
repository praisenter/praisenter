package org.praisenter.data.media;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.search.Indexable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableSet;

public interface ReadonlyMedia extends Indexable, Persistable, Copyable, Identifiable {
	public String getExtension();
	public MediaType getMediaType();
	public MediaFormat getMediaFormat();
	public String getMimeType();
	public long getSize();
	public int getWidth();
	public int getHeight();
	public long getLength();
	public boolean isAudioAvailable();
	
	public ReadOnlyStringProperty formatProperty();
	public ReadOnlyStringProperty versionProperty();
	public ReadOnlyObjectProperty<UUID> idProperty();
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty extensionProperty();
	public ReadOnlyObjectProperty<Instant> createdDateProperty();
	public ReadOnlyObjectProperty<Instant> modifiedDateProperty();
	public ReadOnlyObjectProperty<MediaType> mediaTypeProperty();
	public ReadOnlyObjectProperty<MediaFormat> mediaFormatProperty();
	public ReadOnlyStringProperty mimeTypeProperty();
	public ReadOnlyLongProperty sizeProperty();
	public ReadOnlyIntegerProperty widthProperty();
	public ReadOnlyIntegerProperty heightProperty();
	public ReadOnlyLongProperty lengthProperty();
	public ReadOnlyBooleanProperty audioAvailableProperty();
	
	public ObservableSet<Tag> getTagsUnmodifiable();
	
	/**
	 * Returns the file system path to the media.
	 * @return Path
	 */
	public Path getMediaPath();
	
	/**
	 * Returns the file system path to the media's thumbnail based off of the
	 * media image.
	 * @return Path
	 * @see #getMediaImagePath()
	 */
	public Path getMediaThumbnailPath();
	
	/**
	 * Returns the file system path to the media's image (for images this is
	 * the image itself, for video this is the best frame, for audio this is a
	 * default image).
	 * @return Path
	 */
	public Path getMediaImagePath();
	
	public ReadOnlyObjectProperty<Path> mediaPathProperty();
	public ReadOnlyObjectProperty<Path> mediaImagePathProperty();
	public ReadOnlyObjectProperty<Path> mediaThumbnailPathProperty();
}
