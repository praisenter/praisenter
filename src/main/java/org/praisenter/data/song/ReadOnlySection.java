package org.praisenter.data.song;

import java.util.UUID;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlySection extends Copyable {
	public UUID getId();
	public String getName();
	public String getText();
	
	public ReadOnlyObjectProperty<UUID> idProperty();
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyStringProperty textProperty();
}
