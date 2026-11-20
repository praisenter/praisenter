package org.praisenter.ui.upgrade;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.data.workspace.WorkspaceManager;

final class Upgrade3113OrEarlier implements VersionUpgradeHandler {
	@Override
	public boolean isUpgradeRequired(Version current) {
		Version max = new Version(3, 1, 13);
		
		// this upgrader applies if you are less than or equal to 3.1.13
		return 
			current.isLessThan(max) ||
			current.equals(max);
	}

	@Override
	public CompletableFuture<Void> beforeLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public CompletableFuture<Void> afterLoadUpgrade(Logger logger, WorkspaceManager workspaceManager) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public boolean requiresReindex() {
		// from 3.1.14 we changed
		// Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		// to
		// Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, true));
		return true;
	}
}
