package org.praisenter.data.slide;

import java.util.UUID;

import org.praisenter.data.TextStore;
import org.praisenter.data.bible.BibleReferenceTextStore;
import org.praisenter.data.song.SongReferenceTextStore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SlideReference implements ReadOnlySlideReference {
	private final ObjectProperty<UUID> slideId;
	private final ObjectProperty<TextStore> placeholderData;
	
	public SlideReference() {
		this.slideId = new SimpleObjectProperty<UUID>();
		this.placeholderData = new SimpleObjectProperty<TextStore>();
	}
	
	@Override
	@JsonProperty
	public UUID getSlideId() {
		return this.slideId.get();
	}
	
	@JsonProperty
	public void setSlideId(UUID slideId) {
		this.slideId.set(slideId);
	}
	
	@Override
	public ObjectProperty<UUID> slideIdProperty() {
		return this.slideId;
	}
	
	@Override
	@JsonProperty
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY)
	@JsonSubTypes({
		@Type(value = BibleReferenceTextStore.class, name = "bibleTextStore"),
		@Type(value = SongReferenceTextStore.class, name = "songTextStore")
	})
	public TextStore getPlaceholderData() {
		return this.placeholderData.get();
	}
	
	@JsonProperty
	@JsonSubTypes({
		@Type(value = BibleReferenceTextStore.class, name = "bibleTextStore"),
		@Type(value = SongReferenceTextStore.class, name = "songTextStore")
	})
	public void setPlaceholderData(TextStore data) {
		this.placeholderData.set(data);
	}
	
	@Override
	public ObjectProperty<TextStore> placeholderDataProperty() {
		return this.placeholderData;
	}
}
