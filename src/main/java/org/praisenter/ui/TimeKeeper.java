package org.praisenter.ui;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class TimeKeeper {
	private static final ObjectProperty<LocalDateTime> CURRENT_TIME = new SimpleObjectProperty<>(LocalDateTime.now());
	private static final Timer TIMER = new Timer("Time Updater", true);
	private static final TimerTask TIMER_TASK = new TimerTask() {
		@Override
		public void run() {
			Platform.runLater(() -> {
				CURRENT_TIME.set(LocalDateTime.now());
			});
		}
	};
	
	static {
		TIMER.scheduleAtFixedRate(TIMER_TASK, 1000, 1000);
	}
	
	private TimeKeeper() {}
	
	public static final ReadOnlyObjectProperty<LocalDateTime> currentTimeProperty() {
		return CURRENT_TIME;
	}
	
	public static final LocalDateTime getCurrentTime() {
		return CURRENT_TIME.get();
	}
}
