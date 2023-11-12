package org.praisenter.ui.upgrade;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceConfiguration;
import org.praisenter.data.workspace.WorkspaceManager;

import atlantafx.base.theme.PrimerDark;

final class Upgrade300OrEarlier implements VersionUpgradeHandler {
	@Override
	public boolean isUpgradeRequired(Version current) {
		Version max = new Version(3, 0, 0);
		
		// this upgrader applies if you are less than or equal to 3.0.0
		return 
			current.isLessThan(max) ||
			current.equals(max);
	}

	@Override
	public CompletableFuture<Void> beforeLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		WorkspaceConfiguration config = workspaceManager.getWorkspaceConfiguration();
		
		// from 3.0.0 to 3.1.x we changed the whole L&F over to atlantaFx
		// this changed the default theme and the default font size
		// we also introduced the concept of an accent color
		logger.debug("Updating application font size, theme, and accent color");
		config.setApplicationFontSize(WorkspaceConfiguration.DEFAULT_FONT_SIZE);
		config.setThemeName(new PrimerDark().getUserAgentStylesheet());
		config.setAccentName(null);
		
		return workspaceManager.saveWorkspaceConfiguration().thenRun(() -> {
			logger.debug("Update of configuration was successful");
		});
	}
	
	@Override
	public CompletableFuture<Void> afterLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		// from 3.0.0 to 3.1.x we update lucene, we should re-index
		logger.debug("Re-indexing for searching");
		return workspaceManager.reindex().thenRun(() -> {
			logger.debug("Re-index completed successfully");
		});
	}
}
