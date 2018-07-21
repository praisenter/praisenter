package org.praisenter.ui;

import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;

import javafx.application.Application;
import javafx.stage.Stage;

public interface ReadOnlyPraisenterContext {
	public DataManager getDataManager();
	public Configuration getConfiguration();
	public Stage getStage();
	public Application getApplication();
}
