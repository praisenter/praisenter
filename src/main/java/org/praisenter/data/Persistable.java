package org.praisenter.data;

import java.time.Instant;

import org.praisenter.data.search.Indexable;

public interface Persistable extends Indexable, Identifiable, Copyable {
	public String getName();
	public Instant getCreatedDate();
	public Instant getModifiedDate();
	public String getVersion();
	public String getFormat();
}
