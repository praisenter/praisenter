package org.praisenter.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DataReadResult<T> {
	private final T data;
	private final List<String> warnings;
	
	public DataReadResult(T data) {
		this.data = data;
		this.warnings = Collections.unmodifiableList(new ArrayList<>());
	}
	
	public DataReadResult(T data, List<String> warnings) {
		this.data = data;
		this.warnings = Collections.unmodifiableList(warnings);
	}
	
	public T getData() {
		return this.data;
	}
	
	public List<String> getWarnings() {
		return this.warnings;
	}
}
