package org.praisenter.async;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyBackgroundTask extends Comparable<ReadOnlyBackgroundTask> {
	/**
	 * Returns the item name that was involved in this background task.
	 * @return String
	 */
	public String getName();
	public ReadOnlyStringProperty nameProperty();
	
	/**
	 * Returns a short name for the operation taking place.
	 * @return String
	 */
	public String getOperation();
	public ReadOnlyStringProperty operationProperty();
	
	/**
	 * Returns a description of the action that occurred
	 * @return String
	 */
	public String getMessage();
	public ReadOnlyStringProperty messageProperty();
	
	/**
	 * Returns a string that describes the type of item
	 * @return String
	 */
	public String getType();
	public ReadOnlyStringProperty typeProperty();
	
	/**
	 * Returns the progress of the background task. 
	 * @return double 1.0 for complete, anything else is incomplete
	 */
	public double getProgress();
	public ReadOnlyDoubleProperty progressProperty();
	
	/**
	 * Returns the exception that occurred when performing the task.
	 * @return Throwable null if successful
	 */
	public Throwable getException();
	public ReadOnlyObjectProperty<Throwable> exceptionProperty();
	
	/**
	 * Returns true if the task is complete
	 * @return boolean
	 */
	public boolean isComplete();
	public ReadOnlyBooleanProperty completeProperty();
	
	/**
	 * Returns true if the task was successful
	 * @return boolean
	 */
	public boolean isSuccess();
	
	/**
	 * Returns the task start time
	 * @return LocalDateTime
	 */
	public LocalDateTime getStartTime();
	public ReadOnlyObjectProperty<LocalDateTime> startTimeProperty();
	
	/**
	 * Returns the task end time
	 * @return LocalDateTime
	 */
	public LocalDateTime getEndTime();
	public ReadOnlyObjectProperty<LocalDateTime> endTimeProperty();
	
	/**
	 * Returns the task duration
	 * @return Duration
	 */
	public Duration getDuration();
}
