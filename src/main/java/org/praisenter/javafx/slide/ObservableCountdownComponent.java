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
			text.set(region.getText());
		}
	};
	
	final ObjectProperty<LocalDateTime> target = new SimpleObjectProperty<LocalDateTime>();
	final StringProperty format = new SimpleStringProperty();
	
	public ObservableCountdownComponent(CountdownComponent component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.target.set(component.getTarget());
		this.format.set(component.getFormat());
		
		this.target.addListener((obs, ov, nv) -> { 
			this.region.setTarget(nv); 
			this.text.set(this.region.getText());
		});
		this.format.addListener((obs, ov, nv) -> { 
			this.region.setFormat(nv); 
			this.text.set(this.region.getText());
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
	
	public LocalDateTime getTarget() {
		return this.target.get();
	}
	
	public void setTarget(LocalDateTime target) {
		this.target.set(target);
	}
	
	public ObjectProperty<LocalDateTime> targetProperty() {
		return this.target;
	}
	
	// format
	
	public String getFormat() {
		return this.format.get();
	}
	
	public void setFormat(String format) {
		this.format.set(format);
	}
	
	public StringProperty formatProperty() {
		return this.format;
	}
}
