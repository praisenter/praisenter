package org.praisenter.data.workspace;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

import org.praisenter.data.json.LocalDateTimeJsonDeserializer;
import org.praisenter.data.json.LocalDateTimeJsonSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class WorkspaceReference implements ReadonlyWorkspaceReference, Comparable<WorkspaceReference> {
	
	private final ObjectProperty<Path> path;
	private final ObjectProperty<LocalDateTime> lastOpenDate;
	
	public WorkspaceReference() {
		this.path = new SimpleObjectProperty<Path>();
		this.lastOpenDate = new SimpleObjectProperty<LocalDateTime>();
	}
	
	@JsonCreator
	public static WorkspaceReference fromString(String value) {
		Path path = null;
		try {
			path = Paths.get(value);
		} catch (Exception ex) {
			// we don't have logging at this time so we just have to eat
			// the exception to ensure the app can still launch
		}
		
		WorkspaceReference wr = new WorkspaceReference();
		wr.path.set(path);
		wr.lastOpenDate.set(null);
		return wr;
	}
	
	@Override
	public int hashCode() {
		Path path = this.path.get();
		if (path == null) {
			return 0;
		}
		return path.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false;
		
		if (obj == this)
			return true;
		
		if (obj instanceof WorkspaceReference) {
			WorkspaceReference other = (WorkspaceReference)obj;
			return Objects.equals(this.path.get(), other.path.get());
		}
		
		return false;
	}
	
	@Override
	public int compareTo(WorkspaceReference o) {
		if (o == null)
			return -1;
		
		if (o == this)
			return 0;
		
		LocalDateTime t1 = this.lastOpenDate.get();
		LocalDateTime t2 = o.lastOpenDate.get();

		if (t1 == null && t2 != null)
			return 1;
		
		if (t1 != null && t2 == null)
			return -1;
		
		if (t1 == null && t2 == null) {
			// null compare times for both, so compare paths
			return comparePaths(this.path.get(), o.path.get());
		}
		
		// compare times
		int diff = t2.compareTo(t1);
		if (diff == 0) {
			// equal date/times, so compare paths
			return comparePaths(this.path.get(), o.path.get());
		}
		
		return diff;
	}
	
	private final int comparePaths(Path p1, Path p2) {
		// both null or both reference equals
		if (p1 == p2)
			return 0;
		
		if (p2 == null)
			return -1;
		
		if (p1 == null)
			return 1;
		
		return p1.compareTo(p2);
	}
	
	@Override
	public String toString() {
		Path path = this.path.get();
		if (path == null)
			return null;
		return path.toAbsolutePath().toString();
	}
	
	@Override
	@JsonProperty
	public Path getPath() {
		return this.path.get();
	}
	
	@JsonProperty
	public void setPath(Path path) {
		this.path.set(path);
	}
	
	@Override
	public ObjectProperty<Path> pathProperty() {
		return this.path;
	}
	
	@Override
	@JsonProperty
	@JsonSerialize(using = LocalDateTimeJsonSerializer.class)
	public LocalDateTime getLastOpenDate() {
		return this.lastOpenDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
	public void setLastOpenDate(LocalDateTime lastOpenDate) {
		this.lastOpenDate.set(lastOpenDate);
	}
	
	@Override
	public ObjectProperty<LocalDateTime> lastOpenDateProperty() {
		return this.lastOpenDate;
	}
}
