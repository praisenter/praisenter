package org.praisenter.data.workspace;           

import org.praisenter.data.Identifiable;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableSet;

public interface ReadOnlyWorkspaces extends Identifiable {
	public String getFormat();
	public String getVersion();
	
	public String getLastSelectedWorkspace();

	public ReadOnlyStringProperty formatProperty();
	public ReadOnlyStringProperty versionProperty();
	
	public ReadOnlyStringProperty lastSelectedWorkspaceProperty();
	
	public ObservableSet<String> getWorkspacesUnmodifiable();
}
