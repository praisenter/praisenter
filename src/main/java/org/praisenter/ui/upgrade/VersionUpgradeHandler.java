package org.praisenter.ui.upgrade;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceManager;

public interface VersionUpgradeHandler {
	public boolean isUpgradeRequired(Version current);
	public CompletableFuture<Void> beforeLoadUpgrade(Logger logger, WorkspaceManager workspaceManager);
	public CompletableFuture<Void> afterLoadUpgrade(Logger logger, WorkspaceManager workspaceManager);
}
