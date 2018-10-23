package org.praisenter.data;

import java.util.UUID;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface Identifiable {
	public UUID getId();
	
	public ReadOnlyObjectProperty<UUID> idProperty();
	
	public boolean identityEquals(Object other);
}
