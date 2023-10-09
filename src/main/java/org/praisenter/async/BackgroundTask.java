package org.praisenter.async;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// TODO add an StringProperty action field to store "Import", "Save", "Delete", etc.
public final class BackgroundTask implements ReadOnlyBackgroundTask, Comparable<ReadOnlyBackgroundTask> {
	private final StringProperty name;
	private final StringProperty message;
	private final DoubleProperty progress;
	private final ObjectProperty<Throwable> exception;
	private final BooleanProperty complete;
	private final ObjectProperty<LocalDateTime> startTime;
	private final ObjectProperty<LocalDateTime> endTime;
	
	public BackgroundTask() {
		this.name = new SimpleStringProperty();
		this.message = new SimpleStringProperty();
		this.progress = new SimpleDoubleProperty();
		this.exception = new SimpleObjectProperty<>();
		this.complete = new SimpleBooleanProperty();
		this.startTime = new SimpleObjectProperty<>(LocalDateTime.now());
		this.endTime = new SimpleObjectProperty<>();
		
		this.complete.bind(Bindings.createBooleanBinding(() -> {
			double progress = this.progress.get();
			Throwable exception = this.exception.get();
			return progress >= 1.0 || exception != null;
		}, this.progress, this.exception));
		
		this.complete.addListener((obs, ov, nv) -> {
			if (nv) {
				this.endTime.set(LocalDateTime.now());
			}
		});
	}
	
	@Override
	public int compareTo(ReadOnlyBackgroundTask o) {
		return -this.startTime.get().compareTo(o.getStartTime());
	}
	
	private void executeOnJavaFXThread(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			r.run();
		} else {
			Platform.runLater(r);
		}
	}
	
	@Override
	public String getName() {
		return this.name.get();
	}
	
	public void setName(String name) {
		this.executeOnJavaFXThread(() -> {
			this.name.set(name);
		});
	}
	
	@Override
	public StringProperty nameProperty() { 
		return this.name;
	}
	
	@Override
	public String getMessage() {
		return this.message.get();
	}
	
	public void setMessage(String message) {
		this.executeOnJavaFXThread(() -> {
			this.message.set(message);
		});
	}
	
	@Override
	public StringProperty messageProperty() { 
		return this.message;
	}
	
	@Override
	public double getProgress() {
		return this.progress.get();
	}
	
	public void setProgress(double progress) {
		this.executeOnJavaFXThread(() -> {
			this.progress.set(progress);
		});
	}
	
	@Override
	public DoubleProperty progressProperty() { 
		return this.progress;
	}
	
	@Override
	public Throwable getException() {
		return this.exception.get();
	}
	
	public void setException(Throwable exception) {
		this.executeOnJavaFXThread(() -> {
			this.exception.set(exception);
		});
	}
	
	@Override
	public ObjectProperty<Throwable> exceptionProperty() { 
		return this.exception;
	}
	
	@Override
	public boolean isComplete() {
		return this.complete.get();
	}
	
	public ReadOnlyBooleanProperty completeProperty() {
		return this.complete;
	}
	
	@Override
	public boolean isSuccess() {
		return this.complete.get() && this.exception.get() == null;
	}
	
	@Override
	public ReadOnlyObjectProperty<LocalDateTime> startTimeProperty() {
		return this.startTime;
	}
	
	@Override
	public LocalDateTime getStartTime() {
		return this.startTime.get();
	}
	
	@Override
	public ReadOnlyObjectProperty<LocalDateTime> endTimeProperty() {
		return this.endTime;
	}
	
	@Override
	public LocalDateTime getEndTime() {
		return this.endTime.get();
	}
	
	@Override
	public Duration getDuration() {
		return Duration.between(this.startTime.get(), this.endTime.get());
	}
	
}
