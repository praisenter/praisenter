package org.praisenter.data.slide;

import java.util.UUID;

import org.praisenter.data.TextStore;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlySlideReference {
	public UUID getSlideId();
	public TextStore getPlaceholderData();
	public String getName();
	
	public ReadOnlyObjectProperty<UUID> slideIdProperty();
	public ReadOnlyObjectProperty<TextStore> placeholderDataProperty();
	public ReadOnlyStringProperty nameProperty();
}
