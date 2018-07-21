package org.praisenter.ui;

import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;

import javafx.application.Application;
import javafx.stage.Stage;

final class PraisenterContext implements ReadOnlyPraisenterContext {
	DataManager dataManager;
	Configuration configuration;
	
	Application application;
	Stage stage;
	
	@Override
	public DataManager getDataManager() {
		return this.dataManager;
	}
	
	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	@Override
	public Application getApplication() {
		return this.application;
	}
	
	@Override
	public Stage getStage() {
		return this.stage;
	}
}
