package org.praisenter.data.bible;

import java.util.UUID;

import javafx.beans.property.ObjectProperty;

public interface BibleConfiguration {
	public UUID getPrimaryBibleId();
	public UUID getSecondaryBibleId();

	public void setPrimaryBibleId(UUID id);
	public void setSecondaryBibleId(UUID id);
	
	public ObjectProperty<UUID> primaryBibleIdProperty();
	public ObjectProperty<UUID> secondaryBibleIdProperty();
	
}
