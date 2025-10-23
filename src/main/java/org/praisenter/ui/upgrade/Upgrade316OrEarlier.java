package org.praisenter.ui.upgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceManager;
import org.praisenter.data.workspace.WorkspacePathResolver;

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
			logger.debug("Attempting the contents of '{}' to prepare for upgrade", toolsPath);
			Files.walk(toolsPath)
	            .filter(Files::isRegularFile)
	            .map(Path::toFile)
	            .forEach(File::delete);
		} catch (IOException e) {
			logger.error("Failed to delete the contents of the tools path - can't upgrade ffmpeg & ffprobe tools", e);
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public CompletableFuture<Void> afterLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		// from 3.1.6 to 3.1.x we updated lucene, we should re-index
		logger.debug("Re-indexing for searching");
		return workspaceManager.reindex().thenRun(() -> {
			logger.debug("Re-index completed successfully");
		});
	}
}
