package org.praisenter.data.slide;

import java.util.UUID;

import org.praisenter.data.TextStore;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideReference {
	public UUID getSlideId();
	public TextStore getPlaceholderData();
	
	public ReadOnlyObjectProperty<UUID> slideIdProperty();
	public ReadOnlyObjectProperty<TextStore> placeholderDataProperty();
}
