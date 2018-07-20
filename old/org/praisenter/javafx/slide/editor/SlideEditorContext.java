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
package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.command.EditManager;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The context for the editing of slides.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideEditorContext {
	/** The Praisenter context */
	private final PraisenterContext praisenterContext;
	
	/** The edit manager */
	private final EditManager manager = new EditManager();
	
	// properties
	
	/** The current slide being edited */
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<>();
	
	/** The selected region */
	private final ObjectProperty<ObservableSlideRegion<?>> selected = new SimpleObjectProperty<>();

	/**
	 * Constructor.
	 * @param context the praisenter context
	 */
	public SlideEditorContext(PraisenterContext context) {
		this.praisenterContext = context;
	}
	
	// getters
	
	/**
	 * Returns the Praisenter context.
	 * @return {@link PraisenterContext}
	 */
	public PraisenterContext getPraisenterContext() {
		return this.praisenterContext;
	}
	
	/**
	 * Returns the edit manager.
	 * @return {@link EditManager}
	 */
	public EditManager getEditManager() {
		return this.manager;
	}
	
	// the slide
	
	/**
	 * Returns the current slide property.
	 * @return ObjectProperty&lt;{@link ObservableSlide}&lt;?&gt;&gt;
	 */
	public ObjectProperty<ObservableSlide<?>> slideProperty() {
		return this.slide;
	}
	
	/**
	 * Returns the current slide.
	 * @return {@link ObservableSlide}&lt;?&gt;
	 */
	public ObservableSlide<?> getSlide() {
		return this.slideProperty().get();
	}
	
	/**
	 * Sets the current slide.
	 * @param slide the slide
	 */
	public void setSlide(final ObservableSlide<?> slide) {
		this.slideProperty().set(slide);
	}
	
	// selected
	
	/**
	 * Returns the selected region property.
	 * @return {@link ObjectProperty}&lt;{@link ObservableSlideRegion}&lt;?&gt;&gt;
	 */
	public ObjectProperty<ObservableSlideRegion<?>> selectedProperty() {
		return this.selected;
	}
	
	/**
	 * Returns the selected region.
	 * @return {@link ObservableSlideRegion}&lt;?&gt;
	 */
	public ObservableSlideRegion<?> getSelected() {
		return this.selectedProperty().get();
	}
	
	/**
	 * Sets the selected region.
	 * @param selected the selected region
	 */
	public void setSelected(final ObservableSlideRegion<?> selected) {
		this.selectedProperty().set(selected);
	}
}
