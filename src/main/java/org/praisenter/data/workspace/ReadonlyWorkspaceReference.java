package org.praisenter.data.workspace;

import java.nio.file.Path;
import java.time.LocalDateTime;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadonlyWorkspaceReference {
	public Path getPath();
	public LocalDateTime getLastOpenDate();
	
	public ReadOnlyObjectProperty<Path> pathProperty();
	public ReadOnlyObjectProperty<LocalDateTime> lastOpenDateProperty();
}
