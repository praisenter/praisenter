package org.praisenter.data.workspace;

import java.util.List;
import java.util.UUID;

import org.praisenter.data.bible.BibleConfiguration;
import org.praisenter.data.slide.SlideReference;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class DisplayConfiguration implements BibleConfiguration, ReadOnlyDisplayConfiguration {
	private final IntegerProperty id;
	
	private final ObjectProperty<UUID> bibleTemplateId;
	private final ObjectProperty<UUID> songTemplateId;
	private final ObjectProperty<UUID> notificationTemplateId;
	
	private final ObjectProperty<UUID> primaryBibleId;
	private final ObjectProperty<UUID> secondaryBibleId;
	
	private final BooleanProperty previewTransitionEnabled;
	private final BooleanProperty autoShowEnabled;
	
	private final ObservableList<SlideReference> queuedSlides;
	private final ObservableList<SlideReference> queuedSlidesReadOnly;
	
	public DisplayConfiguration() {
		this.id = new SimpleIntegerProperty();
		
		this.bibleTemplateId = new SimpleObjectProperty<>();
		this.songTemplateId = new SimpleObjectProperty<>();
		this.notificationTemplateId = new SimpleObjectProperty<>();
		
		this.primaryBibleId = new SimpleObjectProperty<>();
		this.secondaryBibleId = new SimpleObjectProperty<>();

		this.previewTransitionEnabled = new SimpleBooleanProperty();
		this.autoShowEnabled = new SimpleBooleanProperty();
		
		this.queuedSlides = FXCollections.observableArrayList();
		this.queuedSlidesReadOnly = FXCollections.unmodifiableObservableList(this.queuedSlides);
	}
	
	@Override
	public DisplayConfiguration copy() {
		DisplayConfiguration dc = new DisplayConfiguration();
		dc.id.set(this.id.get());
		dc.bibleTemplateId.set(this.bibleTemplateId.get());
		dc.songTemplateId.set(this.songTemplateId.get());
		dc.notificationTemplateId.set(this.notificationTemplateId.get());
		dc.primaryBibleId.set(this.primaryBibleId.get());
		dc.secondaryBibleId.set(this.secondaryBibleId.get());
		dc.previewTransitionEnabled.set(this.previewTransitionEnabled.get());
		dc.autoShowEnabled.set(this.autoShowEnabled.get());
		dc.queuedSlides.addAll(this.queuedSlides);
		return dc;
	}

	@Override
	@JsonProperty
	public int getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(int id) {
		this.id.set(id);
	}
	
	@Override
	public IntegerProperty idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public UUID getBibleTemplateId() {
		return this.bibleTemplateId.get();
	}
	
	@JsonProperty
	public void setBibleTemplateId(UUID id) {
		this.bibleTemplateId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> bibleTemplateIdProperty() {
		return this.bibleTemplateId;
	}
	
	@Override
	@JsonProperty
	public UUID getSongTemplateId() {
		return this.songTemplateId.get();
	}
	
	@JsonProperty
	public void setSongTemplateId(UUID id) {
		this.songTemplateId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> songTemplateIdProperty() {
		return this.songTemplateId;
	}
	
	@Override
	@JsonProperty
	public UUID getNotificationTemplateId() {
		return this.notificationTemplateId.get();
	}
	
	@JsonProperty
	public void setNotificationTemplateId(UUID id) {
		this.notificationTemplateId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> notificationTemplateIdProperty() {
		return this.notificationTemplateId;
	}
	
	@Override
	@JsonProperty
	public UUID getPrimaryBibleId() {
		return this.primaryBibleId.get();
	}
	
	@Override
	@JsonProperty
	public void setPrimaryBibleId(UUID id) {
		this.primaryBibleId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> primaryBibleIdProperty() {
		return this.primaryBibleId;
	}

	@Override
	@JsonProperty
	public UUID getSecondaryBibleId() {
		return this.secondaryBibleId.get();
	}
	
	@Override
	@JsonProperty
	public void setSecondaryBibleId(UUID id) {
		this.secondaryBibleId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> secondaryBibleIdProperty() {
		return this.secondaryBibleId;
	}

	@Override
	@JsonProperty
	public boolean isPreviewTransitionEnabled() {
		return this.previewTransitionEnabled.get();
	}
	
	@JsonProperty
	public void setPreviewTransitionEnabled(boolean enabled) {
		this.previewTransitionEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty previewTransitionEnabledProperty() {
		return this.previewTransitionEnabled;
	}
	
	@Override
	@JsonProperty
	public boolean isAutoShowEnabled() {
		return this.autoShowEnabled.get();
	}
	
	@JsonProperty
	public void setAutoShowEnabled(boolean enabled) {
		this.autoShowEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty autoShowEnabledProperty() {
		return this.autoShowEnabled;
	}
	
	@Override
	public ObservableList<SlideReference> getQueuedSlidesUnmodifiable() {
		return this.queuedSlidesReadOnly;
	}
	
	@JsonProperty
	public ObservableList<SlideReference> getQueuedSlides() {
		return this.queuedSlides;
	}
	
	@JsonProperty
	public void setQueuedSlides(List<SlideReference> slides) {
		this.queuedSlides.addAll(slides);
	}
}
