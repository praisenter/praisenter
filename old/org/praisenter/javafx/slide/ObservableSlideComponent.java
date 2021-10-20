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
package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.converters.EffectConverter;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.effects.SlideShadow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;

/**
 * Represents an observable {@link SlideComponent}.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> {@link SlideComponent}
 */
public abstract class ObservableSlideComponent<T extends SlideComponent> extends ObservableSlideRegion<T> implements Playable {
	/** The shadow */
	private final ObjectProperty<SlideShadow> shadow = new SimpleObjectProperty<SlideShadow>();
	
	/** The glow */
	private final ObjectProperty<SlideShadow> glow = new SimpleObjectProperty<SlideShadow>();
	
	/**
	 * Minimal constructor.
	 * @param component the slide region
	 * @param context the context
	 * @param mode the mode
	 */
	public ObservableSlideComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		this.shadow.set(this.region.getShadow());
		this.glow.set(this.region.getGlow());
		
		this.shadow.addListener((obs, ov, nv) -> {
			this.region.setShadow(nv);
			updateEffects();
		});
		this.glow.addListener((obs, ov, nv) -> {
			this.region.setGlow(nv);
			updateEffects();
		});
	}
	
	/**
	 * Builds the component using the given content node.
	 * @param content the content
	 */
	protected void build(Node content) {
		this.updateAll();
		
		this.container.getChildren().addAll(
				this.backgroundNode,
				content,
				this.borderNode);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#updateAll()
	 */
	@Override
	protected void updateAll() {
		super.updateAll();
		this.updateEffects();
	}
	
	/**
	 * Updates the Java FX component when the effects changes.
	 */
	protected final void updateEffects() {
		SlideShadow ss = this.shadow.get();
		SlideShadow sg = this.glow.get();
		EffectBuilder builder = EffectBuilder.create();
		Effect shadow = EffectConverter.toJavaFX(ss);
		Effect glow = EffectConverter.toJavaFX(sg);
		builder.add(shadow, shadow != null && shadow instanceof InnerShadow ? 10 : 30);
		builder.add(glow, glow != null && glow instanceof InnerShadow ? 20 : 40);
		Effect effect = builder.build();
		this.backgroundNode.setEffect(effect);
		
		this.onEffectsUpdate(ss, sg);
	}
	
	/**
	 * Called after the effects are updated.
	 * @param shadow the new shadow
	 * @param glow the new glow
	 */
	protected void onEffectsUpdate(SlideShadow shadow, SlideShadow glow) {}
	
	// shadow

	/**
	 * Sets the shadow.
	 * @param shadow the shadow
	 */
	public void setShadow(SlideShadow shadow) {
		this.shadow.set(shadow);
	}

	/**
	 * Returns the shadow.
	 * @return {@link SlideShadow}
	 */
	public SlideShadow getShadow() {
		return this.shadow.get();
	}
	
	/**
	 * Returns the shadow property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideShadow> shadowProperty() {
		return this.shadow;
	}

	// glow

	/**
	 * Sets the glow.
	 * @param glow the glow
	 */
	public void setGlow(SlideShadow glow) {
		this.glow.set(glow);
	}

	/**
	 * Returns the glow.
	 * @return {@link SlideShadow}
	 */
	public SlideShadow getGlow() {
		return this.glow.get();
	}
	
	/**
	 * Returns the glow property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideShadow> glowProperty() {
		return this.glow;
	}
}
