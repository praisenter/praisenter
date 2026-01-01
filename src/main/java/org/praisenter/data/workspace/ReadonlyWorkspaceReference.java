package org.praisenter.data.workspace;

import java.nio.file.Path;
import java.time.LocalDateTime;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadonlyWorkspaceReference {
	public Path getPath();
	public LocalDateTime getLastOpenDate();
	public String getSecurityToken();
	
	public ReadOnlyObjectProperty<Path> pathProperty();
	public ReadOnlyObjectProperty<LocalDateTime> lastOpenDateProperty();
	public ReadOnlyStringProperty securityTokenProperty();
}
