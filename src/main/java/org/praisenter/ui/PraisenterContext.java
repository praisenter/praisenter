package org.praisenter.ui;

import java.util.concurrent.CompletableFuture;

import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;

final class PraisenterContext implements ReadOnlyPraisenterContext {
	ApplicationState applicationState;
	DataManager dataManager;
	Configuration configuration;
	
	public PraisenterContext(ApplicationState state) {
		this.applicationState = state;
	}
	
	@Override
	public DataManager getDataManager() {
		return this.dataManager;
	}
	
	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	@Override
	public ApplicationState getApplicationState() {
		return this.applicationState;
	}
	
	@Override
	public CompletableFuture<Void> saveConfiguration() {
		return this.dataManager.update(this.configuration);
	}
}
