package org.praisenter.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

public final class JavaFXContext {
	/** The Java FX application instance */
	private final Application application;
	
	/** The Java FX main stage */
	private final Stage stage;
	
	/**
	 * Full constructor.
	 * @param application the Java FX application instance
	 * @param stage the Java FX main stage
	 */
	public JavaFXContext(
			Application application,
			Stage stage) {
		this.application = application;
		this.stage = stage;
	}

	/**
	 * Returns the application instance.
	 * @return Application
	 */
	public Application getApplication() {
		return this.application;
	}
	
	/**
	 * Returns the main stage for the application.
	 * @return Stage
	 */
	public Stage getStage() {
		return this.stage;
	}
}
