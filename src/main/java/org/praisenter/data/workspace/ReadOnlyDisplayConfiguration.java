package org.praisenter.data.workspace;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.slide.ReadOnlySlideReference;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface ReadOnlyDisplayConfiguration extends Copyable {
	public int getId();

	public UUID getBibleTemplateId();
	public UUID getSongTemplateId();
	public UUID getNotificationTemplateId();

	public boolean isPreviewTransitionEnabled();
	public boolean isAutoShowEnabled();

	public ReadOnlyIntegerProperty idProperty();
	
	public ReadOnlyObjectProperty<UUID> bibleTemplateIdProperty();
	public ReadOnlyObjectProperty<UUID> songTemplateIdProperty();
	public ReadOnlyObjectProperty<UUID> notificationTemplateIdProperty();
	
	public ReadOnlyBooleanProperty previewTransitionEnabledProperty();
	public ReadOnlyBooleanProperty autoShowEnabledProperty();
	
	public ObservableList<? extends ReadOnlySlideReference> getQueuedSlidesUnmodifiable();
}
