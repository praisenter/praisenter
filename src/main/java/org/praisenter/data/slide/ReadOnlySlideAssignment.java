package org.praisenter.data.slide;

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideAssignment extends Copyable, Identifiable {
	public UUID getSlideId();
	
	public ReadOnlyObjectProperty<UUID> idProperty();
	public ReadOnlyObjectProperty<UUID> slideIdProperty();
}
