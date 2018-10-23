package org.praisenter.data;

import java.time.Instant;

import org.praisenter.data.search.Indexable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface Persistable extends Indexable, Identifiable, Copyable {
	public String getName();
	public Instant getCreatedDate();
	public Instant getModifiedDate();
	public String getVersion();
	public String getFormat();
	
	public ReadOnlyStringProperty nameProperty();
	public ReadOnlyObjectProperty<Instant> createdDateProperty();
	public ReadOnlyObjectProperty<Instant> modifiedDateProperty();
	public ReadOnlyStringProperty versionProperty();
	public ReadOnlyStringProperty formatProperty();
}
