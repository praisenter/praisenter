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

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.MediaType;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.animation.Animations;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.media.MediaObject;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

/**
 * A list cell for a {@link SlideAnimation}.
 * @author William Bittle
 * @version 3.0.0
 */
final class AnimationListCell extends ListCell<SlideAnimation> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The slide (to pull components from based on id) */
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>();
	
	/** The current animation value */
	private final ObjectProperty<SlideAnimation> animation = new SimpleObjectProperty<SlideAnimation>();
	
	// internal
	
	/** The animation name property */
	private final StringProperty animationName = new SimpleStringProperty();
	
	/** The region name property (will be bound to the animated region) */
	private final StringProperty regionName = new SimpleStringProperty();
	
	/** The media property (in the case of media components) */
	private final ObjectProperty<MediaObject> media = new SimpleObjectProperty<MediaObject>();
	
	// helper bindings
	
	/** Helper binding for the name */
	private final StringBinding name = new StringBinding() {
		{
			bind(animationName, regionName);
		}
		@Override
		protected String computeValue() {
			String an = animationName.get();
			String rn = regionName.get();
			String name = null;
			if (an != null && !an.isEmpty() && rn != null && !rn.isEmpty()) {
				name = an + " " + rn;
			} else if (an != null && !an.isEmpty()) {
				name = an;
			} else {
				return null;
			}
			return name.replaceAll("\n|\r", " ");
		}
	};
	
	/** Helper binding for the graphic */
	private final ObjectBinding<Node> graphic = new ObjectBinding<Node>() {
		{
			bind(animation, slide, media);
		}
		@Override
		protected Node computeValue() {
			ObservableSlideRegion<?> region = getRegion();
			return getGraphicForAnimatedRegion(region);
		}
	};
	
	/**
	 * Default constructor.
	 */
	public AnimationListCell() {
		this.getStyleClass().add("animation-list-cell");
		
		// the name of the animation is the concatenation of the
		// animation name and the component name, however, the 
		// component name can change based on it's properties
		// as a result, we bind the regionName to the name property
		// of the region and use a string binding to bind the two
		// together to produce the name we display
		this.textProperty().bind(this.name);
		
		Tooltip tooltip = new Tooltip();
		tooltip.textProperty().bind(this.name);
		tooltipProperty().bind(new ObjectBinding<Tooltip>() {
			{
				bind(name);
			}
			@Override
			protected Tooltip computeValue() {
				String n = name.get();
				if (n == null || n.isEmpty()) {
					return null;
				}
				return tooltip;
			}
		});
		
		// the graphic
		this.graphicProperty().bind(this.graphic);
		
		// what to do when the value changes
		this.animation.addListener((obs, ov, nv) -> {
			onAnimationChanged(nv);
		});
	}
	
	/**
	 * Called when the animation value changes.
	 * @param animation the new animation; can be null
	 */
	private void onAnimationChanged(SlideAnimation animation) {
		// reset
		this.animationName.set(null);
		this.regionName.unbind();
		this.media.unbind();
		
		// check for null
		if (animation == null) {
			return;
		}
		
		// set the animation name
		this.animationName.set(Animations.getName(animation.getAnimation()));
		
		// get the component
		ObservableSlideRegion<?> region = getRegion();
		if (region != null) {
			// bind the region name
			this.regionName.bind(region.nameProperty());
			
			// bind the media
			if (region instanceof ObservableMediaComponent) {
				ObservableMediaComponent omc = (ObservableMediaComponent)region;
				this.media.bind(omc.mediaProperty());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(SlideAnimation item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item != null && !empty) {
			this.animation.set(item);
		} else {
			this.animation.set(null);
		}
	}
	
	/**
	 * Returns the region, or null, that is being animated.
	 * @return {@link ObservableSlideRegion}
	 */
	private ObservableSlideRegion<?> getRegion() {
		if (this.animation.get() == null ||
			this.slide.get() == null) {
			return null;
		}
		
		UUID id = this.animation.get().getId();
		if (id.equals(this.slide.get().getId())) {
			return this.slide.get();
		} else {
			return this.slide.get().getComponent(id);
		}
	}
	
	/**
	 * Returns the graphic for the given region or null if the region is null.
	 * @param region the region
	 * @return Node
	 */
	private Node getGraphicForAnimatedRegion(ObservableSlideRegion<?> region) {
		if (region == null) {
			return null;
		}
		
		if (region instanceof ObservableSlide) {
			return ApplicationGlyphs.SLIDE.duplicate();
		} else if (region instanceof ObservableTextPlaceholderComponent) {
			return ApplicationGlyphs.PLACEHOLDER_COMPONENT.duplicate();
		} else if (region instanceof ObservableDateTimeComponent) {
			return ApplicationGlyphs.DATETIME_COMPONENT.duplicate();
		} else if (region instanceof ObservableCountdownComponent) {
			return ApplicationGlyphs.COUNTDOWN_COMPONENT.duplicate();
		} else if (region instanceof ObservableBasicTextComponent) {
			return ApplicationGlyphs.BASIC_TEXT_COMPONENT.duplicate();
		} else if (region instanceof ObservableMediaComponent) {
			MediaObject mo = ((ObservableMediaComponent)region).getMedia();
			if (mo == null || mo.getType() == null) {
				return ApplicationGlyphs.MEDIA_COMPONENT.duplicate();
			} else if (mo.getType() == MediaType.AUDIO) {
				return ApplicationGlyphs.AUDIO_MEDIA_COMPONENT.duplicate();
			} else if (mo.getType() == MediaType.IMAGE) {
				return ApplicationGlyphs.IMAGE_MEDIA_COMPONENT.duplicate();
			}  else if (mo.getType() == MediaType.VIDEO) {
				return ApplicationGlyphs.VIDEO_MEDIA_COMPONENT.duplicate();
			} else {
				LOGGER.warn("Unknown media type {} when choosing icon for animation.", mo.getType());
			}
		} else {
			// just log it
			LOGGER.warn("Unknown type {} when choosing icon for animation.", region.getClass());
		}
		return null;
	}
	
	/**
	 * The slide property. This needs to be bound so that the name of the animations
	 * can be updated and generated properly.
	 * @return ObjectProperty&lt;{@link ObservableSlideRegion}&lt;?&gt;&gt;
	 */
	public ObjectProperty<ObservableSlide<?>> slideProperty() {
		return this.slide;
	}
}