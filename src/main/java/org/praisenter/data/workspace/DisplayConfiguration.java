package org.praisenter.data.workspace;

import java.util.List;
import java.util.UUID;

import org.praisenter.data.bible.BibleConfiguration;
import org.praisenter.data.slide.SlideReference;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class DisplayConfiguration implements BibleConfiguration, ReadOnlyDisplayConfiguration {
	private final IntegerProperty id;
	private final BooleanProperty primary;
	private final BooleanProperty active;
	private final StringProperty name;
	private final StringProperty defaultName;
	private final ObjectProperty<DisplayType> type;
	private final IntegerProperty controllingDisplayId;
	
	private final IntegerProperty x;
	private final IntegerProperty y;
	private final IntegerProperty width;
	private final IntegerProperty height;
	private final IntegerProperty framesPerSecond;
	
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
		this.primary = new SimpleBooleanProperty(false);
		this.name = new SimpleStringProperty();
		this.defaultName = new SimpleStringProperty();
		this.active = new SimpleBooleanProperty(false);
		this.type = new SimpleObjectProperty<>();
		this.controllingDisplayId = new SimpleIntegerProperty(NOT_CONTROLLED);
		
		this.x = new SimpleIntegerProperty();
		this.y = new SimpleIntegerProperty();
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
		this.framesPerSecond = new SimpleIntegerProperty();
		
		this.bibleTemplateId = new SimpleObjectProperty<>();
		this.songTemplateId = new SimpleObjectProperty<>();
		this.notificationTemplateId = new SimpleObjectProperty<>();
		
		this.primaryBibleId = new SimpleObjectProperty<>();
		this.secondaryBibleId = new SimpleObjectProperty<>();

		this.previewTransitionEnabled = new SimpleBooleanProperty();
		this.autoShowEnabled = new SimpleBooleanProperty();
		
		this.queuedSlides = FXCollections.observableArrayList();
		this.queuedSlidesReadOnly = FXCollections.unmodifiableObservableList(this.queuedSlides);
		
		this.defaultName.bind(Bindings.createStringBinding(() -> {
			return (this.id.get() + 1) + " (" + this.x.get() + "," + this.y.get() + ") " + this.width.get() + "x" + this.height.get();
		}, this.id, this.x, this.y, this.width, this.height));
	}
	
	@Override
	public DisplayConfiguration copy() {
		DisplayConfiguration dc = new DisplayConfiguration();
		dc.id.set(this.id.get());
		dc.primary.set(this.primary.get());
		dc.name.set(this.name.get());
		dc.type.set(this.type.get());
		dc.controllingDisplayId.set(this.controllingDisplayId.get());
		dc.x.set(this.x.get());
		dc.y.set(this.y.get());
		dc.width.set(this.width.get());
		dc.height.set(this.height.get());
		dc.framesPerSecond.set(this.framesPerSecond.get());
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
	public String toString() {
		return this.defaultName.get();
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
	@JsonProperty
	public boolean isPrimary() {
		return this.primary.get();
	}
	
	@JsonProperty
	public void setPrimary(boolean flag) {
		this.primary.set(flag);
	}
	
	@Override
	public BooleanProperty primaryProperty() {
		return this.primary;
	}
	
	@Override
	@JsonProperty
	public boolean isActive() {
		return this.active.get();
	}
	
	@JsonProperty
	public void setActive(boolean flag) {
		this.active.set(flag);
	}
	
	@Override
	public BooleanProperty activeProperty() {
		return this.active;
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
	public StringProperty nameProperty() {
		return this.name;
	}
	
	@Override
	@JsonProperty
	public DisplayType getType() {
		return this.type.get();
	}
	
	@JsonProperty
	public void setType(DisplayType type) {
		this.type.set(type);
	}
	
	@Override
	public ObjectProperty<DisplayType> typeProperty() {
		return this.type;
	}
	
	@Override
	public String getDefaultName() {
		return this.defaultName.get();
	}
	
	@Override
	public ReadOnlyStringProperty defaultNameProperty() {
		return this.defaultName;
	}
	
	@Override
	@JsonProperty
	public int getControllingDisplayId() {
		return this.controllingDisplayId.get();
	}
	
	@JsonProperty
	public void setControllingDisplayId(int displayId) {
		this.controllingDisplayId.set(displayId);
	}

	@Override
	public IntegerProperty controllingDisplayIdProperty() {
		return this.controllingDisplayId;
	}
	
	@Override
	@JsonProperty
	public int getX() {
		return this.x.get();
	}
	
	@JsonProperty
	public void setX(int x) {
		this.x.set(x);
	}
	
	@Override
	public IntegerProperty xProperty() {
		return this.x;
	}
	
	@Override
	@JsonProperty
	public int getY() {
		return this.y.get();
	}
	
	@JsonProperty
	public void setY(int y) {
		this.y.set(y);
	}
	
	@Override
	public IntegerProperty yProperty() {
		return this.y;
	}
	
	@Override
	@JsonProperty
	public int getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	public void setWidth(int width) {
		this.width.set(width);
	}
	
	@Override
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public int getHeight() {
		return this.height.get();
	}
	
	@JsonProperty
	public void setHeight(int height) {
		this.height.set(height);
	}
	
	@Override
	public IntegerProperty heightProperty() {
		return this.height;
	}
	
	@Override
	@JsonProperty
	public int getFramesPerSecond() {
		return this.framesPerSecond.get();
	}
	
	@JsonProperty
	public void setFramesPerSecond(int fps) {
		this.framesPerSecond.set(fps);
	}
	
	@Override
	public IntegerProperty framesPerSecondProperty() {
		return this.framesPerSecond;
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
