package org.praisenter.data;

import java.util.ArrayList;
import java.util.List;

public final class DataImportResult<T> {
	private final List<T> created;
	private final List<T> updated;
	private final List<String> warnings;
	private final List<Exception> errors;
	
	public DataImportResult() {
		this.created = new ArrayList<>();
		this.updated = new ArrayList<>();
		this.warnings = new ArrayList<>();
		this.errors = new ArrayList<>();
	}

	public List<T> getCreated() {
		return this.created;
	}
	
	public List<T> getUpdated() {
		return this.updated;
	}

	public List<String> getWarnings() {
		return this.warnings;
	}

	public List<Exception> getErrors() {
		return this.errors;
	}
	
	public void add(DataImportResult<T> other) {
		this.created.addAll(other.created);
		this.updated.addAll(other.updated);
		this.warnings.addAll(other.warnings);
		this.errors.addAll(other.errors);
	}
	
	public boolean isEmpty() {
		return this.created.size() <= 0 && this.updated.size() <= 0;
	}
}
