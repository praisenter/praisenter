package org.praisenter.data;

import java.util.UUID;

import javafx.beans.property.ObjectProperty;

public interface Identifiable {
	public UUID getId();
	public void setId(UUID id);
	public ObjectProperty<UUID> idProperty();
	
	public boolean identityEquals(Object other);
}
