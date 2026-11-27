package org.praisenter.ui.upgrade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.data.workspace.WorkspacePathResolver;
import org.praisenter.utility.FileHelper;

final class Upgrade316OrEarlier implements VersionUpgradeHandler {
	@Override
	public boolean isUpgradeRequired(Version current) {
		Version max = new Version(3, 1, 6);
		
		// this upgrader applies if you are less than or equal to 3.1.6
		return 
			current.isLessThan(max) ||
			current.equals(max);
	}

	@Override
	public CompletableFuture<Void> beforeLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		WorkspacePathResolver pathResolver = workspaceManager.getWorkspacePathResolver();
		Path mediaPath = pathResolver.getMediaPath();
		Path toolsPath = mediaPath.resolve("tools");
		try {
			logger.debug("Deleting the contents of '{}' to prepare for upgrade", toolsPath);
			FileHelper.deleteDirectory(toolsPath, false);
		} catch (IOException e) {
			logger.error("Failed to delete the contents of the tools path {} - can't upgrade ffmpeg & ffprobe tools", toolsPath.toAbsolutePath(), e);
			logger.error("To resolve this, manually delete the contents of the tools path {}.  When startup continues it will attempt to recreate the necessary files.", toolsPath.toAbsolutePath());
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public CompletableFuture<Void> afterLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		// No specific post-load upgrade steps for this version
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public boolean requiresReindex() {
		// from 3.1.6 to 3.1.x we updated lucene, we should re-index
		return true;
	}
}
