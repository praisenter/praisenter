package org.praisenter.javafx.slide;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public final class ObservableSlide<T extends Slide> extends ObservableSlideRegion<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final StringProperty name = new SimpleStringProperty();
	private final ObjectProperty<Path> path = new SimpleObjectProperty<Path>();
	private final LongProperty time = new SimpleLongProperty();
	
	private final ObservableList<ObservableSlideComponent<?>> components = FXCollections.observableArrayList();

	private final Pane componentCanvas;
	
	public ObservableSlide(T slide, ObservableSlideContext context, SlideMode mode) {
		super(slide, context, mode);
		
		this.componentCanvas = new Pane();
		this.componentCanvas.setMinSize(0, 0);
		
		// set initial values
		this.name.set(slide.getName());
		this.path.set(slide.getPath());
		this.time.set(slide.getTime());
		
		for (SlideComponent component : slide.getComponents(SlideComponent.class)) {
			ObservableSlideComponent<?> comp = this.observableSlideComponent(component);
			if (comp != null) {
				this.components.add(comp);
				this.componentCanvas.getChildren().add(comp.getDisplayPane());
			}
		}
		
		// setup listeners
		this.name.addListener((obs, ov, nv) -> { 
			slide.setName(nv); 
		});
		this.path.addListener((obs, ov, nv) -> { 
			slide.setPath(nv); 
		});
		this.time.addListener((obs, ov, nv) -> { 
			slide.setTime(nv.longValue()); 
		});
		
		this.scale.addListener((obs, ov, nv) -> {
			rootPane.setTranslateX(nv.sx);
			rootPane.setTranslateY(nv.sy);
		});
		
		this.build();
	}

	void build() {
		super.build(null);
		this.rootPane.getChildren().add(this.componentCanvas);
	}
	
	@Override
	void updateSize() {
		super.updateSize();
		
		// also update the size and scaling for the component canvas
		int w = this.width.get();
		int h = this.height.get();
		
		Scaling s = this.scale.get();
		Fx.setSize(this.componentCanvas, w * s.scale, h * s.scale);
	}
	
	public List<Node> getComponentDisplayPanes() {
		List<Node> panes = new ArrayList<Node>();
		for (ObservableSlideComponent<?> component : this.components) {
			panes.add(component.getDisplayPane());
		}
		return panes;
	}

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
			return new ObservableBasicTextComponent<BasicTextComponent>((BasicTextComponent)component, this.context, this.mode);
		} else {
			// just log the error
			LOGGER.warn("Component type not supported " + component.getClass().getName());
		}
		return null;
	}
	
	public void fit(int width, int height) {
		this.region.fit(width, height);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
		updatePosition();
		updateSize();
		for (ObservableSlideComponent<?> component : this.components) {
			component.x.set(component.region.getX());
			component.y.set(component.region.getY());
			component.width.set(component.region.getWidth());
			component.height.set(component.region.getHeight());
			component.updatePosition();
			component.updateSize();
		}
	}
	
	// playable
	
	public void play() {
		super.play();
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.play();
		}
	}
	
	// tags
	
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.region.getTags()); 
	}
	
	public boolean addTag(Tag tag) {
		return this.region.getTags().add(tag);
	}
	
	public boolean removeTag(Tag tag) {
		return this.region.getTags().remove(tag);
	}
	
	// animations
	
	public List<SlideAnimation> getAnimations() {
		return Collections.unmodifiableList(this.region.getAnimations());
	}
	
	public List<SlideAnimation> getAnimations(UUID id) {
		return Collections.unmodifiableList(this.region.getAnimations(id));
	}
	
	public boolean addAnimation(SlideAnimation animation) {
		return this.region.getAnimations().add(animation);
	}
	
	public boolean removeAnimation(SlideAnimation animation) {
		return this.region.getAnimations().remove(animation);
	}
	
	// components

	public Iterator<ObservableSlideComponent<?>> componentIterator() {
		return this.components.listIterator();
	}
	
	public void addComponent(ObservableSlideComponent<?> component) {
		// this sets the order, so must be done first
		this.region.addComponent(component.region);
		// copy over the order to the observable
		component.order.set(component.region.getOrder());
		// add to the observable list
		this.components.add(component);
		
		this.componentCanvas.getChildren().add(component.getDisplayPane());
	}

	public boolean removeComponent(ObservableSlideComponent<?> component) {
		// remove the component
		if (this.region.removeComponent(component.region)) {
			this.components.removeIf(c -> c.getId().equals(component.getId()));
			this.componentCanvas.getChildren().remove(component.getDisplayPane());
			return true;
		}
		return false;
	}
	
	public void moveComponentDown(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentDown(component.region);
		
		// now we need to reflect those changes in the observable objects
		
		// reset the orders
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.setOrder(comp.region.getOrder());
		}
		// resort the components list
		FXCollections.sort(this.components);
		
		componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
		componentCanvas.getChildren().addAll(getComponentDisplayPanes());
	}
	
	public void moveComponentUp(ObservableSlideComponent<?> component) {
		// this will set the order of the components and sort them
		this.region.moveComponentUp(component.region);
		
		// now we need to reflect those changes in the observable objects
		
		// reset the orders
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.setOrder(comp.region.getOrder());
		}
		// resort the components list
		FXCollections.sort(this.components);
		
		componentCanvas.getChildren().removeAll(getComponentDisplayPanes());
		componentCanvas.getChildren().addAll(getComponentDisplayPanes());
	}
	
	// name
	
	public String getName() {
		return this.name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}

	// path
	
	public Path getPath() {
		return this.path.get();
	}

	public void setPath(Path path) {
		this.path.set(path);
	}

	public ObjectProperty<Path> pathProperty() {
		return this.path;
	}

	// time
	
	public long getTime() {
		return this.time.get();
	}

	public void setTime(long time) {
		this.time.set(time);
	}
	
	public LongProperty timeProperty() {
		return this.time;
	}

	// others
	
	public String getVersion() {
		return this.region.getVersion();
	}

	public boolean hasPlaceholders() {
		return this.region.hasPlaceholders();
	}
}
