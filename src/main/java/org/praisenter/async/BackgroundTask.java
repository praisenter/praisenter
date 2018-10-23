package org.praisenter.async;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class BackgroundTask implements ReadOnlyBackgroundTask {
	private final StringProperty name;
	private final StringProperty message;
	private final DoubleProperty progress;
	private final ObjectProperty<Throwable> exception;
	private final BooleanProperty complete;
	
	public BackgroundTask() {
		this.name = new SimpleStringProperty();
		this.message = new SimpleStringProperty();
		this.progress = new SimpleDoubleProperty();
		this.exception = new SimpleObjectProperty<>();
		this.complete = new SimpleBooleanProperty();
		
		this.complete.bind(Bindings.createBooleanBinding(() -> {
			double progress = this.progress.get();
			Throwable exception = this.exception.get();
			return progress >= 1.0 || exception != null;
		}, this.progress, this.exception));
	}
	
	private void executeOnJavaFXThread(Runnable r) {
		if (Platform.isFxApplicationThread()) {
			r.run();
		} else {
			Platform.runLater(r);
		}
	}
	
	public String getName() {
		return this.name.get();
	}
	
	public void setName(String name) {
		this.executeOnJavaFXThread(() -> {
			this.name.set(name);
		});
	}
	
	public StringProperty nameProperty() { 
		return this.name;
	}
	
	public String getMessage() {
		return this.message.get();
	}
	
	public void setMessage(String message) {
		this.executeOnJavaFXThread(() -> {
			this.message.set(message);
		});
	}
	
	public StringProperty messageProperty() { 
		return this.message;
	}
	
	public double getProgress() {
		return this.progress.get();
	}
	
	public void setProgress(double progress) {
		this.executeOnJavaFXThread(() -> {
			this.progress.set(progress);
		});
	}
	
	public DoubleProperty progressProperty() { 
		return this.progress;
	}
	
	public Throwable getException() {
		return this.exception.get();
	}
	
	public void setException(Throwable exception) {
		this.executeOnJavaFXThread(() -> {
			this.exception.set(exception);
		});
	}
	
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
}
