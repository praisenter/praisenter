package org.praisenter.data.workspace;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.slide.ReadOnlySlideReference;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;

public interface ReadOnlyDisplayConfiguration extends Copyable {
	public int getId();
	public boolean isPrimary();
	public boolean isActive();
	public String getName();
	public String getDefaultName();
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	
	public UUID getBibleTemplateId();
	public UUID getSongTemplateId();
	public UUID getNotificationTemplateId();

	public boolean isPreviewTransitionEnabled();
	public boolean isAutoShowEnabled();

	public ReadOnlyIntegerProperty idProperty();
	public ReadOnlyBooleanProperty primaryProperty();
	public ReadOnlyBooleanProperty activeProperty();
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty defaultNameProperty();
	public ReadOnlyIntegerProperty xProperty();
	public ReadOnlyIntegerProperty yProperty();
	public ReadOnlyIntegerProperty widthProperty();
	public ReadOnlyIntegerProperty heightProperty();
	
	public ReadOnlyObjectProperty<UUID> bibleTemplateIdProperty();
	public ReadOnlyObjectProperty<UUID> songTemplateIdProperty();
	public ReadOnlyObjectProperty<UUID> notificationTemplateIdProperty();
	
	public ReadOnlyBooleanProperty previewTransitionEnabledProperty();
	public ReadOnlyBooleanProperty autoShowEnabledProperty();
	
	public ObservableList<? extends ReadOnlySlideReference> getQueuedSlidesUnmodifiable();
}
