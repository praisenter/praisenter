package org.praisenter.data.slide.text;

import java.time.Duration;
import java.time.LocalDateTime;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadOnlyCountdownComponent extends ReadOnlyTextComponent, ReadOnlyTimedTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public CountdownMode getCountdownMode();
	public Duration getCountdownDuration();
	public LocalDateTime getCountdownTarget();
	public boolean isCountdownTimeOnly();
	public boolean isStopAtZeroEnabled();
	public String getCountdownFormat();
	
	public ReadOnlyObjectProperty<CountdownMode> countdownModeProperty();
	public ReadOnlyObjectProperty<Duration> countdownDurationProperty();
	public ReadOnlyObjectProperty<LocalDateTime> countdownTargetProperty();
	public ReadOnlyBooleanProperty countdownTimeOnlyProperty();
	public ReadOnlyBooleanProperty stopAtZeroEnabledProperty();
	public ReadOnlyStringProperty countdownFormatProperty();
}
