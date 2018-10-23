package org.praisenter.async;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyBackgroundTask {
	public String getName();
	public ReadOnlyStringProperty nameProperty();
	
	public String getMessage();
	public ReadOnlyStringProperty messageProperty();
	
	public double getProgress();
	public ReadOnlyDoubleProperty progressProperty();
	
	public Throwable getException();
	public ReadOnlyObjectProperty<Throwable> exceptionProperty();
	
	public boolean isComplete();
	public ReadOnlyBooleanProperty completeProperty();
}
