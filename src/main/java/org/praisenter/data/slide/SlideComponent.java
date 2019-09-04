/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.data.slide;

import java.util.List;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.animation.Animation;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.Rectangle;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.text.CountdownComponent;
import org.praisenter.data.slide.text.DateTimeComponent;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.data.slide.text.TextPlaceholderComponent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Abstract implementation of the {@link SlideComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({ 
  @Type(value = TextComponent.class, name = "textComponent"), 
  @Type(value = TextPlaceholderComponent.class, name = "textPlaceholderComponent"),
  @Type(value = CountdownComponent.class, name = "countdownComponent"),
  @Type(value = DateTimeComponent.class, name = "dateTimeComponent"),
  @Type(value = MediaComponent.class, name = "mediaComponent")
})
public abstract class SlideComponent extends SlideRegion implements ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	protected final DoubleProperty x;
	protected final DoubleProperty y;
	
	private final ObjectProperty<SlideShadow> shadow;
	private final ObjectProperty<SlideShadow> glow;
	
	private final ObservableList<Animation> animations;
	private final ObservableList<Animation> animationsReadOnly;
	
	public SlideComponent() {
		super();
		this.x = new SimpleDoubleProperty(0);
		this.y = new SimpleDoubleProperty(0);
		this.shadow = new SimpleObjectProperty<>();
		this.glow = new SimpleObjectProperty<>();
		
		this.animations = FXCollections.observableArrayList();
		this.animationsReadOnly = FXCollections.unmodifiableObservableList(this.animations);
	}
	
	public abstract SlideComponent copy();
	
	protected void copyTo(SlideComponent component) {
		super.copyTo(component);
		component.x.set(this.x.get());
		component.y.set(this.y.get());
		
		SlideShadow glow = this.glow.get();
		if (glow != null) {
			component.glow.set(glow.copy());
		} else {
			component.glow.set(null);
		}
		
		SlideShadow shadow = this.shadow.get();
		if (shadow != null) {
			component.shadow.set(shadow.copy());
		} else {
			component.shadow.set(null);
		}

		for (Animation animation: this.animations) {
			component.animations.add(animation.copy());
		}
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.x.get(), this.y.get(), this.width.get(), this.height.get());
	}
	
	public void adjust(double pw, double ph) {
		// adjust width/height
		super.adjust(pw, ph);
		
		// adjust positioning
		this.x.set(Math.ceil(this.x.get() * pw));
		this.y.set(Math.ceil(this.y.get() * ph));
	}

	public void translate(double dx, double dy) {
		this.x.set(this.x.get() + dx);
		this.y.set(this.y.get() + dy);
	}
	
	@Override
	public boolean isOverlapping(ReadOnlySlideComponent component) {
		double ax1 = this.x.get();
		double ay1 = this.y.get();
		double ax2 = ax1 + this.width.get();
		double ay2 = ay1 + this.height.get();
		
		double bx1 = component.getX();
		double by1 = component.getY();
		double bx2 = ax1 + component.getWidth();
		double by2 = ay1 + component.getHeight();
		
		return ax1 < bx2 && ax2 > bx1 && ay1 < by2 && ay2 > by1;
		
		//return false;
	}
	
	@Override
	@JsonProperty
	public double getX() {
		return this.x.get();
	}
	
	@JsonProperty
	public void setX(double x) {
		this.x.set(x);
	}
	
	@Override
	public DoubleProperty xProperty() {
		return this.x;
	}
	
	@Override
	@JsonProperty
	public double getY() {
		return this.y.get();
	}
	
	@JsonProperty
	public void setY(double y) {
		this.y.set(y);
	}
	
	@Override
	public DoubleProperty yProperty() {
		return this.y;
	}
	
	@Override
	@JsonProperty
	public SlideShadow getShadow() {
		return this.shadow.get();
	}
	
	@JsonProperty
	public void setShadow(SlideShadow shadow) {
		this.shadow.set(shadow);
	}
	
	@Override
	@Watchable(name = "shadow")
	public ObjectProperty<SlideShadow> shadowProperty() {
		return this.shadow;
	}
	
	@Override
	@JsonProperty
	public SlideShadow getGlow() {
		return this.glow.get();
	}
	
	@JsonProperty
	public void setGlow(SlideShadow glow) {
		this.glow.set(glow);
	}
	
	@Override
	@Watchable(name = "glow")
	public ObjectProperty<SlideShadow> glowProperty() {
		return this.glow;
	}

	@JsonProperty
	@Watchable(name = "animations")
	public ObservableList<Animation> getAnimations() {
		return this.animations;
	}
	
	@JsonProperty
	public void setAnimations(List<Animation> animations) {
		this.animations.setAll(animations);
	}
	
	@Override
	public ObservableList<Animation> getAnimationsUnmodifiable() {
		return this.animationsReadOnly;
	}
}
