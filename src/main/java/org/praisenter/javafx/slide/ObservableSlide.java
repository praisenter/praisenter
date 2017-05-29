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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.utility.Scaling;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Represents an observable {@link Slide}.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the slide type
 */
public final class ObservableSlide<T extends Slide> extends ObservableSlideRegion<T> implements Playable {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The slide name */
	private final StringProperty slideName = new SimpleStringProperty();
	
	/** The slide time */
	private final LongProperty time = new SimpleLongProperty();
	
	/** The list of components */
	private final ObservableList<ObservableSlideComponent<?>> components = FXCollections.observableArrayList();
	
	/** The list of animations */
	private final ObservableList<SlideAnimation> animations = FXCollections.observableArrayList();

	// nodes
	
	// +-----------------------+--------------+-------------------------------+
	// | Name                  | Type         | Role                          |
	// +-----------------------+--------------+-------------------------------+
	// | rootPane              | Pane         | Provides x,y positioning      |
	// | +- container          | Pane         | Provides scaling              |
	// |    +- backgroundNode  | FillPane     | For the background            |
	// |    +- borderNode      | Region       | The border                    |
	// | +- componentCanvas    | Pane         | For parent level x,y offsets  |
	// |    +- rootPane        | Pane         | Component 1                   |
	// |    +- rootPane        | Pane         | Component 2                   |
	// |    +- ....            | Pane         | Component N                   |
	// +-----------------------+--------------+-------------------------------+
	
	/** The container for all the components */
	private final Pane componentCanvas;
	
	/**
	 * Minimal constructor.
	 * @param slide the slide
	 * @param context the context
	 * @param mode the mode
	 */
	public ObservableSlide(T slide, PraisenterContext context, SlideMode mode) {
		super(slide, context, mode);
		
		this.componentCanvas = new Pane();
		this.componentCanvas.setMinSize(0, 0);
		this.componentCanvas.setSnapToPixel(true);
		
		// set initial values
		this.slideName.set(slide.getName());
		this.time.set(slide.getTime());
		
		for (SlideComponent component : slide.getComponents(SlideComponent.class)) {
			ObservableSlideComponent<?> comp = this.observableSlideComponent(component);
			if (comp != null) {
				this.components.add(comp);
				this.componentCanvas.getChildren().add(comp.getDisplayPane());
			}
		}
		
		this.animations.addAll(slide.getAnimations());
		
		// setup listeners
		this.slideName.addListener((obs, ov, nv) -> { 
			slide.setName(nv);
			updateName();
		});
		this.time.addListener((obs, ov, nv) -> { 
			slide.setTime(nv.longValue()); 
		});
		
		this.build(null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#onBuild(javafx.scene.layout.Pane, javafx.scene.layout.Pane)
	 */
	@Override
	protected void onBuild(Pane displayPane, Pane container) {
		// add all the components
		displayPane.getChildren().add(this.componentCanvas);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#onSizeUpdate(double, double, org.praisenter.utility.Scaling)
	 */
	@Override
	protected void onSizeUpdate(double width, double height, Scaling scaling) {
		Fx.setSize(this.componentCanvas, Math.ceil(width * scaling.factor), Math.ceil(height * scaling.factor));
	}
	
	/**
	 * Returns a list of all the component display panes.
	 * @return List&lt;Node&gt;
	 */
	public List<Node> getComponentDisplayPanes() {
		List<Node> panes = new ArrayList<Node>();
		for (ObservableSlideComponent<?> component : this.components) {
			panes.add(component.getDisplayPane());
		}
		return panes;
	}

	/**
	 * Returns a new {@link ObservableSlideComponent} for the given {@link SlideComponent} using this
	 * {@link ObservableSlide}'s context and slide mode.
	 * @param component the component
	 * @return {@link ObservableSlideComponent}
	 */
	public ObservableSlideComponent<?> observableSlideComponent(SlideComponent component) {
		// now create its respective observable one
		if (component instanceof MediaComponent) {
			return new ObservableMediaComponent((MediaComponent)component, this.context, this.mode);
		} else if (component instanceof DateTimeComponent) {
			return new ObservableDateTimeComponent((DateTimeComponent)component, this.context, this.mode);
		} else if (component instanceof TextPlaceholderComponent) {
			return new ObservableTextPlaceholderComponent((TextPlaceholderComponent)component, this.context, this.mode);
		} else if (component instanceof CountdownComponent) {
			return new ObservableCountdownComponent((CountdownComponent)component, this.context, this.mode);
		} else if (component instanceof BasicTextComponent) {
			return new ObservableBasicTextComponent((BasicTextComponent)component, this.context, this.mode);
		} else {
			// just log the error
			LOGGER.warn("Component type not supported " + component.getClass().getName());
		}
		return null;
	}
	
	/**
	 * Adjusts the slide and it's components to fit on the given width and height.
	 * @param width the new width; in pixels
	 * @param height the new height; in pixels
	 */
	public void fit(int width, int height) {
		this.region.fit(width, height);
		this.setX(this.region.getX());
		this.setY(this.region.getY());
		this.setWidth(this.region.getWidth());
		this.setHeight(this.region.getHeight());
		updatePosition();
		updateSize();
		for (ObservableSlideComponent<?> component : this.components) {
			component.setX(component.region.getX());
			component.setY(component.region.getY());
			component.setWidth(component.region.getWidth());
			component.setHeight(component.region.getHeight());
			component.updatePosition();
			component.updateSize();
		}
	}
	
	// playable
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#play()
	 */
	public void play() {
		super.play();
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.play();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.stop();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.dispose();
		}
	}
	
	// tags
	
	/**
	 * Returns an unmodifiable set of the tags for this slide.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.region.getTags()); 
	}
	
	/**
	 * Adds the given tag to this slide.
	 * @param tag the tag
	 * @return boolean true if the tag was added
	 */
	public boolean addTag(Tag tag) {
		return this.region.getTags().add(tag);
	}
	
	/**
	 * Removes the given tag from this slide.
	 * @param tag the tag
	 * @return boolean true if the tag was removed
	 */
	public boolean removeTag(Tag tag) {
		return this.region.getTags().remove(tag);
	}
	
	// animations
	
	/**
	 * Returns an observable list of all the animations.
	 * <p>
	 * Do not modify this list directly.
	 * @return ObservableList&lt;{@link SlideAnimation}&gt;
	 */
	public ObservableList<SlideAnimation> getAnimations() {
		return this.animations;
	}
	
	/**
	 * Adds an animation to the slide.
	 * @param animation the animation
	 * @return boolean true if it was added successfully
	 */
	public boolean addAnimation(SlideAnimation animation) {
		return this.region.getAnimations().add(animation) && this.animations.add(animation);
	}
	
	/**
	 * Removes the animation from this slide.
	 * @param animation the animation
	 * @return boolean true if it was removed successfully
	 */
	public boolean removeAnimation(SlideAnimation animation) {
		return this.region.getAnimations().remove(animation) && this.animations.remove(animation);
	}
	
	// components

	/**
	 * Returns the component with the given id or null if not found.
	 * @param id the id
	 * @return {@link ObservableSlideComponent}&lt;?&gt;
	 */
	public ObservableSlideComponent<?> getComponent(UUID id) {
		for (ObservableSlideComponent<?> component : this.components) {
			if (component.getId().equals(id)) {
				return component;
			}
		}
		return null;
	}
	
	/**
	 * Returns an observable list of all the slide components.
	 * <p>
	 * Do not modify this list directly.
	 * @return ObservableList&lt;{@link ObservableSlideComponent}&lt;?&gt;&gt;
	 */
	public ObservableList<ObservableSlideComponent<?>> getComponents() {
		return this.components;
	}
	
	/**
	 * Adds the given component to this slide.
	 * @param component the component
	 */
	public void addComponent(ObservableSlideComponent<?> component) {
		// this sets the order, so must be done first
		this.region.addComponent(component.region);
		// add to the observable list
		this.components.add(component);
		
		this.componentCanvas.getChildren().add(component.getDisplayPane());
	}

	/**
	 * Removes the given component from this slide.
	 * @param component the component
	 * @return boolean true if it was removed
	 */
	public boolean removeComponent(ObservableSlideComponent<?> component) {
		// remove the component
		if (this.region.removeComponent(component.region)) {
			this.components.removeIf(c -> c.getId().equals(component.getId()));
			this.componentCanvas.getChildren().remove(component.getDisplayPane());
			this.region.getAnimations().removeIf(a -> a != null && component.getId().equals(a.getId()));
			this.animations.removeIf(a -> a != null && component.getId().equals(a.getId()));
			return true;
		}
		return false;
	}
	
	/**
	 * Moves the given component down in the list of all components.
	 * @param component the component
	 */
	public void moveComponentDown(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentDown(component.region);
		
		// now we need to reflect those changes in the observable objects

		int index = this.components.indexOf(component);
		if (index > 0) {
			Collections.swap(this.components, index, index - 1);
			
			componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
			componentCanvas.getChildren().addAll(getComponentDisplayPanes());
		}
	}
	
	/**
	 * Moves the given component up in the list of all components.
	 * @param component the component
	 */
	public void moveComponentUp(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentUp(component.region);
		
		// now we need to reflect those changes in the observable objects
		
		int index = this.components.indexOf(component);
		if (index >= 0 && index < this.components.size() - 1) {
			Collections.swap(this.components, index, index + 1);
			
			componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
			componentCanvas.getChildren().addAll(getComponentDisplayPanes());
		}
	}
	
	/**
	 * Moves the given component to the top of the list of all components.
	 * @param component the component
	 */
	public void moveComponentFront(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentFront(component.region);
		
		// now we need to reflect those changes in the observable objects
		
		this.components.remove(component);
		this.components.add(component);
		
		componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
		componentCanvas.getChildren().addAll(getComponentDisplayPanes());
	}
	
	/**
	 * Moves the given component to the bottom of the list of all components.
	 * @param component the component
	 */
	public void moveComponentBack(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentBack(component.region);
		
		// now we need to reflect those changes in the observable objects
		
		this.components.remove(component);
		this.components.add(0, component);
		
		componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
		componentCanvas.getChildren().addAll(getComponentDisplayPanes());
	}

	// placeholder
	
	/**
	 * Updates the placeholders.
	 */
	public void updatePlaceholders() {
		this.region.updatePlaceholders();
		// iterate all the placeholders
		for (ObservableSlideComponent<?> osc : this.components) {
			if (osc instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent otpc = (ObservableTextPlaceholderComponent)osc;
				otpc.setText(otpc.getText());
			}
		}
	}
	
	// name
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return this.slideName.get();
	}

	/**
	 * Sets the name.
	 * @param name the name
	 */
	public void setName(String name) {
		this.slideName.set(name);
	}
	
	/**
	 * Returns the name property.
	 * @return StringProperty
	 */
	public StringProperty nameProperty() {
		return this.slideName;
	}

	// time
	
	/**
	 * Returns the time in milliseconds.
	 * @return long
	 */
	public long getTime() {
		return this.time.get();
	}

	/**
	 * Sets the time.
	 * @param time the time in milliseconds
	 */
	public void setTime(long time) {
		this.time.set(time);
	}
	
	/**
	 * Returns the time property.
	 * @return LongProperty
	 */
	public LongProperty timeProperty() {
		return this.time;
	}
}
