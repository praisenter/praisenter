package org.praisenter.javafx.slide;

import java.time.LocalDateTime;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.CountdownComponent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ObservableCountdownComponent extends ObservableTextComponent<CountdownComponent> {
	
	final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			setText(region.getText());
		}
	};
	
	private final ObjectProperty<LocalDateTime> countdownTarget = new SimpleObjectProperty<LocalDateTime>();
	private final StringProperty countdownFormat = new SimpleStringProperty();
	
	public ObservableCountdownComponent(CountdownComponent component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.countdownTarget.set(component.getCountdownTarget());
		this.countdownFormat.set(component.getCountdownFormat());
		
		this.countdownTarget.addListener((obs, ov, nv) -> { 
			this.region.setCountdownTarget(nv); 
			this.setText(this.region.getText());
		});
		this.countdownFormat.addListener((obs, ov, nv) -> { 
			this.region.setCountdownFormat(nv);
			this.setText(this.region.getText());
		});
				
		this.build();
	}
	
	// playable stuff
	
	public void play() {
		super.play();
		this.timer.start();
	}
	
	public void stop() {
		super.stop();
		this.timer.stop();
	}
	
	// target
	
	public LocalDateTime getCountdownTarget() {
		return this.countdownTarget.get();
	}
	
	public void setCountdownTarget(LocalDateTime target) {
		this.countdownTarget.set(target);
	}
	
	public ObjectProperty<LocalDateTime> countdownTargetProperty() {
		return this.countdownTarget;
	}
	
	// format
	
	public String getCountdownFormat() {
		return this.countdownFormat.get();
	}
	
	public void setCountdownFormat(String format) {
		this.countdownFormat.set(format);
	}
	
	public StringProperty countdownFormatProperty() {
		return this.countdownFormat;
	}
}
