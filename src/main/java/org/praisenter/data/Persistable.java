package org.praisenter.data;

import java.time.Instant;
import java.util.Set;

import org.praisenter.data.search.Indexable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableSet;

public interface Persistable extends Indexable, Identifiable, Copyable {
	public String getName();
	public Instant getCreatedDate();
	public Instant getModifiedDate();
	public String getVersion();
	public String getFormat();
	
	public void setName(String name);
	public void setCreatedDate(Instant instant);
	public void setModifiedDate(Instant instant);
	
	public StringProperty nameProperty();
	public ObjectProperty<Instant> createdDateProperty();
	public ObjectProperty<Instant> modifiedDateProperty();
	public ReadOnlyStringProperty versionProperty();
	public ReadOnlyStringProperty formatProperty();
	
	public ObservableSet<Tag> getTags();
	public void setTags(Set<Tag> tags);
	public ObservableSet<Tag> getTagsUnmodifiable();
	
	public Persistable copy();
}
