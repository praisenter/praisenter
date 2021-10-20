package org.praisenter.async;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyBackgroundTask extends Comparable<ReadOnlyBackgroundTask> {
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
	
	public boolean isSuccess();
	
	public LocalDateTime getStartTime();
	public ReadOnlyObjectProperty<LocalDateTime> startTimeProperty();
	
	public LocalDateTime getEndTime();
	public ReadOnlyObjectProperty<LocalDateTime> endTimeProperty();
	
	public Duration getDuration();
}
