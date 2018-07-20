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

import org.praisenter.data.TextStore;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.animation.Animations;
import org.praisenter.slide.Slide;
import org.praisenter.utility.Scaling;

import javafx.animation.Transition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

// TODO Add multi-slide preview pane (primarily for songs)

/**
 * A pane to preview any given slide.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SingleSlidePreviewPane extends StackPane {
	/** The slide to render */
	private final ObjectProperty<Slide> value = new SimpleObjectProperty<Slide>();

	/** The mode to render the slide in */
	private final ObjectProperty<SlideMode> mode = new SimpleObjectProperty<SlideMode>();
	
	/** An observable slide created when the slide is set */
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>();
	
	/** The transition for the observable slide (if preview is called) */
	private final ObjectProperty<Transition> transition = new SimpleObjectProperty<Transition>();
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 * @param mode the initial slide mode
	 */
	public SingleSlidePreviewPane(PraisenterContext context, SlideMode mode) {
		this.getStyleClass().add("single-slide-preview-pane");
		
		this.mode.set(mode);
		
		final int padding = 20;
		
		this.setPadding(new Insets(padding));
		this.setSnapToPixel(true);
		
		// clip by the slide Preview area
		Rectangle clipRect = new Rectangle(this.getWidth(), this.getHeight());
		clipRect.heightProperty().bind(this.heightProperty());
		clipRect.widthProperty().bind(this.widthProperty());
		this.setClip(clipRect);
		
		Pane slideBounds = new Pane();
		slideBounds.getStyleClass().add("slide-preview-container");
		
		// we resize and position canvasBack based on the target width/height 
		// and the available width height using a uniform scale factor
		DoubleBinding widthSizing = new DoubleBinding() {
			{
				bind(widthProperty(), 
					 heightProperty());
			}
			@Override
			protected double computeValue() {
				Slide s = value.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = getWidth() - padding * 2;
					double th = getHeight() - padding * 2;
					return Math.floor(Scaling.getUniformScaling(w, h, tw, th).width) - 1;
				}
				return 0;
			}
		};
		DoubleBinding heightSizing = new DoubleBinding() {
			{
				bind(widthProperty(), 
					 heightProperty());
			}
			@Override
			protected double computeValue() {
				Slide s = value.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = getWidth() - padding * 2;
					double th = getHeight() - padding * 2;
					return Math.floor(Scaling.getUniformScaling(w, h, tw, th).height) - 1;
				}
				return 0;
			}
		};
		slideBounds.maxWidthProperty().bind(widthSizing);
		slideBounds.maxHeightProperty().bind(heightSizing);
		
		Pane slideCanvas = new Pane();
		slideCanvas.setMinSize(0, 0);
		slideCanvas.setSnapToPixel(true);
		slideCanvas.setBackground(null);
		
		// clip the canvas by the bounds
		Rectangle clip = new Rectangle(slideCanvas.getWidth(), slideCanvas.getHeight());
		clip.heightProperty().bind(slideCanvas.heightProperty());
		clip.widthProperty().bind(slideCanvas.widthProperty());
		slideCanvas.setClip(clip);
		
		ObjectBinding<Scaling> scaleFactor = new ObjectBinding<Scaling>() {
			{
				bind(widthProperty(), 
					 heightProperty());
			}
			@Override
			protected Scaling computeValue() {
				double tw = getWidth() - padding * 2;
				double th = getHeight() - padding * 2;
				
				Slide s = value.get();
				if (s == null) {
					return Scaling.getNoScaling(tw, th);
				}
				
				double w = s.getWidth();
				double h = s.getHeight();
				
				return Scaling.getUniformScaling(w, h, tw, th);
			}
		};
		
		slideBounds.getChildren().add(slideCanvas);
		this.getChildren().addAll(slideBounds);
		StackPane.setAlignment(slideBounds, Pos.CENTER);
		StackPane.setAlignment(slideCanvas, Pos.CENTER);
		
		// setup of the editor when the slide being edited changes
		this.value.addListener((obs, ov, nv) -> {
			// clean up
			Transition oldTx = this.transition.get();
			ObservableSlide<?> oldSlide = this.slide.get();
			if (oldTx != null) {
				oldTx.stop();
			}
			if (oldSlide != null) {
				oldSlide.stop();
				oldSlide.dispose();
			}
			
			// remove all the nodes
			slideCanvas.getChildren().clear();
			
			// get the current mode
			SlideMode sm = this.mode.get() == null ? SlideMode.PREVIEW : this.mode.get();
			
			// setup
			if (nv != null) {
				// create the observable slide
				ObservableSlide<Slide> os = new ObservableSlide<>(nv, context, sm);
				this.slide.set(os);
				
				if (sm == SlideMode.PREVIEW_NO_AUDIO || sm == SlideMode.PRESENT) {
					// build a transition for it
					Transition tx = Animations.buildSlideTransition(null, os);
					this.transition.set(tx);
				} else {
					this.transition.set(null);
				}
				
				// add all the nodes
				Node rootPane = os.getDisplayPane();
				slideCanvas.getChildren().add(rootPane);
				
				// bind the scaling
				os.scalingProperty().bind(scaleFactor);
				for (ObservableSlideComponent<?> component : os.getComponents()) {
					component.scalingProperty().bind(scaleFactor);
				}
			} else {
				this.transition.set(null);
				this.slide.set(null);
			}
			
			// since the width/height of the slides could be different
			// then we need to invalidate these to make sure that we
			// compute them for the new slide
			widthSizing.invalidate();
			heightSizing.invalidate();
			scaleFactor.invalidate();
		});
		
		this.mode.addListener((obs) -> {
			Slide slide = this.value.get();
			this.value.set(null);
			this.value.set(slide);
		});
	}
	
	/**
	 * Plays the slide.
	 * <p>
	 * Note that the {@link #getMode()} should be {@link SlideMode#PREVIEW_NO_AUDIO} first.
	 */
	public void play() {
		Transition oldTx = this.transition.get();
		ObservableSlide<?> oldSlide = this.slide.get();
		if (oldTx != null) {
			oldTx.playFromStart();
		}
		if (oldSlide != null) {
			oldSlide.play();
		}
	}
	
	/**
	 * Stops playback of the slide.
	 */
	public void stop() { 
		Transition oldTx = this.transition.get();
		ObservableSlide<?> oldSlide = this.slide.get();
		if (oldTx != null) {
			oldTx.stop();
		}
		if (oldSlide != null) {
			oldSlide.stop();
		}
	}
	
	// slide
	
	/**
	 * Returns the slide or null if no value has been set.
	 * @return {@link Slide}
	 */
	public Slide getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the value.
	 * @param value the slide to show or null to clear
	 */
	public void setValue(Slide value) {
		this.value.set(value);
	}
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;{@link Slide}&gt;
	 */
	public ObjectProperty<Slide> valueProperty() {
		return this.value;
	}
	
	/**
	 * Sets the placeholder data for the current slide (if present).
	 * @param data the data
	 */
	public void setPlaceholderData(TextStore data) {
		ObservableSlide<?> slide = this.slide.get();
		if (slide != null) {
			slide.getRegion().setPlaceholderData(data);
			slide.updatePlaceholders();
		}
	}
	
	// mode
	
	/**
	 * Returns the slide mode.
	 * @return {@link SlideMode}
	 */
	public SlideMode getMode() {
		return this.mode.get();
	}
	
	/**
	 * Sets the slide mode.
	 * @param mode the mode
	 */
	public void setMode(SlideMode mode) {
		this.mode.set(mode);
	}
	
	/**
	 * Returns the mode property.
	 * @return ObjectProperty&lt;{@link SlideMode}&gt;
	 */
	public ObjectProperty<SlideMode> modeProperty() {
		return this.mode;
	}
}
