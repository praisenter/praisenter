package org.praisenter.data.workspace;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.Identifiable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "workspaces")
public final class Workspaces implements ReadOnlyWorkspaces, Identifiable {
	public static final double POSITION_SIZE_UNSET = -1;
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	
	private final StringProperty lastSelectedWorkspace;
	
	private final ObservableSet<String> workspaces;
	
	public Workspaces() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Version.STRING);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		
		this.lastSelectedWorkspace = new SimpleStringProperty();
		
		this.workspaces = FXCollections.observableSet(new HashSet<>());
	}

	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof Workspaces) {
			return this.id.get().equals(((Workspaces)other).id.get());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}

	@Override
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	public String getFormat() {
		return this.format.get();
	}
	
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	void setFormat(String format) {
		this.format.set(format);
	}
	
	@Override
	public ReadOnlyStringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	void setVersion(String version) {
		this.version.set(version);
	}
	
	@Override
	public ReadOnlyStringProperty versionProperty() {
		return this.version;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public String getLastSelectedWorkspace() {
		return this.lastSelectedWorkspace.get();
	}
	
	@JsonProperty
	public void setLastSelectedWorkspace(String path) {
		this.lastSelectedWorkspace.set(path);
	}
	
	@Override
	public StringProperty lastSelectedWorkspaceProperty() {
		return this.lastSelectedWorkspace;
	}
	
	@Override
	public ObservableSet<String> getWorkspacesUnmodifiable() {
		return this.workspaces;
	}
	
	@JsonProperty
	public ObservableSet<String> getWorkspaces() {
		return this.workspaces;
	}
	
	@JsonProperty
	public void setWorkspaces(Set<String> workspaces) {
		this.workspaces.addAll(workspaces);
	}
}
