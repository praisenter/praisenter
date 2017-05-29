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

import java.util.UUID;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.converters.BorderConverter;
import org.praisenter.javafx.slide.converters.EffectConverter;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.utility.Scaling;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

/**
 * Represents an observable wrapper of a {@link SlideRegion}.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the {@link SlideRegion} type
 */
public abstract class ObservableSlideRegion<T extends SlideRegion> implements Playable {
	/** The context */
	protected final PraisenterContext context;
	
	/** The slide mode */
	protected final SlideMode mode;
	
	// the data
	
	/** The region */
	protected final T region;
	
	// editable properties
	
	/** The region name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The x coordinate */
	private final DoubleProperty x = new SimpleDoubleProperty();
	
	/** The y coordinate */
	private final DoubleProperty y = new SimpleDoubleProperty();
	
	/** The width coordinate */
	private final DoubleProperty width = new SimpleDoubleProperty();
	
	/** The height coordinate */
	private final DoubleProperty height = new SimpleDoubleProperty();
	
	/** The background */
	private final ObjectProperty<SlidePaint> background = new SimpleObjectProperty<SlidePaint>();
	
	/** The border */
	private final ObjectProperty<SlideStroke> border = new SimpleObjectProperty<SlideStroke>();
	
	/** The global opacity */
	private final DoubleProperty opacity = new SimpleDoubleProperty();
	
	/** The shadow */
	private final ObjectProperty<SlideShadow> shadow = new SimpleObjectProperty<SlideShadow>();
	
	/** The glow */
	private final ObjectProperty<SlideShadow> glow = new SimpleObjectProperty<SlideShadow>();
	
	// for preview/editing
	
	/** The scaling factor for edit and preview modes */
	private final ObjectProperty<Scaling> scale = new SimpleObjectProperty<Scaling>();
	
	// nodes
	
	// Node hierarchy:
	// +-----------------------+--------------+-------------------------------+
	// | Name                  | Type         | Role                          |
	// +-----------------------+--------------+-------------------------------+
	// | rootPane              | Pane         | Provides x,y positioning      |
	// | +- container          | Pane         | Provides scaling              |
	// |    +- backgroundNode  | FillPane     | For the background            |
	// |    +- content         | Node         | The region's content (if any) |
	// |    +- borderNode      | Region       | The border                    |
	// | +- editBorderNode     | Region       | The edit border               |
	// +-----------------------+--------------+-------------------------------+
	
	/** The root node */
	private final Pane displayPane;
	
	/** The container node for the background, content, and border */
	private final Pane container;
	
	/** The background node */
	private final FillPane backgroundNode;
	
	/** The border node */
	private final Region borderNode;
	
	/** The edit-border node (only for edit mode) */
	private final Region editBorderNode;
	
	/**
	 * Minimal constructor.
	 * @param region the slide region
	 * @param context the context
	 * @param mode the mode
	 */
	public ObservableSlideRegion(T region, PraisenterContext context, SlideMode mode) {
		this.region = region;
		this.context = context;
		this.mode = mode;
		
		// set the default scaling
		this.scale.set(Scaling.getNoScaling(region.getWidth(), region.getHeight()));
		
		// set initial values
		this.name.set(region.getName());
		this.x.set(region.getX());
		this.y.set(region.getY());
		this.width.set(region.getWidth());
		this.height.set(region.getHeight());
		this.background.set(region.getBackground());
		this.border.set(region.getBorder());
		this.opacity.set(region.getOpacity());
		this.shadow.set(region.getShadow());
		this.glow.set(region.getGlow());
		
		// setup nodes
		this.container = new Pane();
		// this is the magic for it to be fast enough
		this.container.setCache(true);
		if (this.mode == SlideMode.EDIT) {
			this.container.setCacheHint(CacheHint.SPEED);
		} else {
			this.container.setCacheHint(CacheHint.DEFAULT);
		}
		this.container.setBackground(null);
		this.container.setMouseTransparent(true);
		this.container.setSnapToPixel(true);
		
		this.borderNode = new Region();
		this.borderNode.setMouseTransparent(true);
		this.borderNode.setSnapToPixel(true);
		
		this.backgroundNode = new FillPane(context, mode);
		this.backgroundNode.setMouseTransparent(true);
		this.backgroundNode.setSnapToPixel(true);
		
		this.displayPane = new Pane(this.container);
		this.displayPane.setSnapToPixel(true);
		
		if (this.mode == SlideMode.EDIT) {
			this.editBorderNode = new Region();
			this.editBorderNode.setSnapToPixel(true);
			this.editBorderNode.prefWidthProperty().bind(this.displayPane.widthProperty());
			this.editBorderNode.prefHeightProperty().bind(this.displayPane.heightProperty());
			this.editBorderNode.getStyleClass().add("slide-edit-region");
			this.displayPane.getChildren().add(this.editBorderNode);
		} else {
			this.editBorderNode = null;
		}
		
		// listen for changes
		this.x.addListener((obs, ov, nv) -> { 
			this.region.setX(nv.intValue());
			this.updatePosition();
		});
		this.y.addListener((obs, ov, nv) -> { 
			this.region.setY(nv.intValue());
			this.updatePosition();
		});
		this.width.addListener((obs, ov, nv) -> { 
			this.region.setWidth(nv.intValue());
			updateSize();
		});
		this.height.addListener((obs, ov, nv) -> { 
			this.region.setHeight(nv.intValue());
			updateSize();
		});
		this.background.addListener((obs, ov, nv) -> { 
			this.region.setBackground(nv);
			updateBackground();
		});
		this.border.addListener((obs, ov, nv) -> { 
			this.region.setBorder(nv);
			updateBorder();
		});
		this.opacity.addListener((obs, ov, nv) -> {
			this.region.setOpacity(nv.doubleValue());
			updateOpacity();
		});
		this.shadow.addListener((obs, ov, nv) -> {
			this.region.setShadow(nv);
			updateEffects();
		});
		this.glow.addListener((obs, ov, nv) -> {
			this.region.setGlow(nv);
			updateEffects();
		});
		this.scale.addListener((obs, ov, nv) -> {
			// scale from the top left corner of the node
			this.container.getTransforms().clear();
			this.container.getTransforms().add(new Scale(nv.factor, nv.factor, 0, 0));

			updatePosition();
			updateSize();
		});
	}

	/**
	 * Returns the node that should be placed on a Java FX scene to render the region.
	 * @return Node
	 */
	public Node getDisplayPane() {
		return this.displayPane;
	}

	/**
	 * Returns the node that displays a border during edit mode.
	 * <p>
	 * Returns null if not in edit mode.
	 * @return Node
	 */
	public Node getEditBorderNode() {
		return this.editBorderNode;
	}
	
	/**
	 * Returns the region being wrapped.
	 * @return T
	 */
	public T getRegion() {
		return this.region;
	}

	/**
	 * Returns the id of the region.
	 * @return UUID
	 */
	public UUID getId() {
		return this.region.getId();
	}

	// actions
	
	/**
	 * Updates the name property based on the state of the region.
	 */
	protected final void updateName() {
		this.name.set(this.region.getName());
	}
	
	/**
	 * Updates the Java FX component when the position changes.
	 */
	protected final void updatePosition() {
		// set position
		
		// get the coordinates in slide space
		double x = this.x.get();
		double y = this.y.get();
		
		// set the position of the edit pane (which has the border)
		// to slide coordinates transformed into the parent node coordinates
		Scaling s = this.scale.get();
		
		this.displayPane.setLayoutX(Math.floor(x * s.factor));
		this.displayPane.setLayoutY(Math.floor(y * s.factor));
		
		this.onPositionUpdate(x, y, s);
	}
	
	/**
	 * Updates the Java FX component when the size changes.
	 */
	protected final void updateSize() {
		double w = this.width.get();
		double h = this.height.get();
		
		Fx.setSize(this.container, w, h);
		Fx.setSize(this.borderNode, w, h);
		
		this.backgroundNode.setSize(w, h);
		
		Scaling s = this.scale.get();
		Fx.setSize(this.displayPane, Math.ceil(w * s.factor), Math.ceil(h * s.factor));
		
		this.onSizeUpdate(w, h, s);
	}
	
	/**
	 * Updates the Java FX component when the border changes.
	 */
	protected final void updateBorder() {
		SlideStroke ss = this.border.get();
		
		// create new border
		if (ss != null) {
			this.borderNode.setBorder(new Border(BorderConverter.toJavaFX(ss)));
		} else {
			this.borderNode.setBorder(null);
		}
		
		double r = ss != null ? ss.getRadius() : 0.0;
		this.backgroundNode.setBorderRadius(r);
		
		this.onBorderUpdate(ss);
	}
	
	/**
	 * Updates the Java FX component when the background fill changes.
	 */
	protected final void updateBackground() {
		SlidePaint paint = this.background.get();
		this.backgroundNode.setPaint(paint);
		
		this.onBackgroundUpdate(paint);
	}
	
	/**
	 * Updates the Java FX component when the opacity changes.
	 */
	protected final void updateOpacity() {
		double o = this.opacity.get();
		this.container.setOpacity(o);
		
		this.onOpacityUpdate(o);
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
	
	// events
	
	/**
	 * Called after the position is updated.
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 * @param scaling the scaling
	 */
	protected void onPositionUpdate(double x, double y, Scaling scaling) {}
	
	/**
	 * Called after the size is updated.
	 * @param width the new width
	 * @param height the new height
	 * @param scaling the scaling
	 */
	protected void onSizeUpdate(double width, double height, Scaling scaling) {}
	
	/**
	 * Called after the border is updated.
	 * @param stroke the border
	 */
	protected void onBorderUpdate(SlideStroke stroke) {}
	
	/**
	 * Called after the background fill is updated.
	 * @param paint the new fill
	 */
	protected void onBackgroundUpdate(SlidePaint paint) {}
	
	/**
	 * Called after the global opacity is updated.
	 * @param opacity the new opacity
	 */
	protected void onOpacityUpdate(double opacity) {}
	
	/**
	 * Called after the effects are updated.
	 * @param shadow the new shadow
	 * @param glow the new glow
	 */
	protected void onEffectsUpdate(SlideShadow shadow, SlideShadow glow) {}
	
	// building
	
	/**
	 * Sets up the region for display and modification.
	 * @param content the content of the region
	 */
	protected final void build(Node content) {
		// set initial node properties
		this.updatePosition();
		this.updateBorder();
		this.updateSize();
		this.updateBackground();
		this.updateOpacity();
		this.updateEffects();
		
		if (content != null) {
			this.container.getChildren().addAll(
					this.backgroundNode,
					content,
					this.borderNode);
		} else {
			this.container.getChildren().addAll(
					this.backgroundNode,
					this.borderNode);
		}
		
		this.onBuild(this.displayPane, this.container);
	}
	
	/**
	 * Called after the basic node hiearchy has been built.
	 * @param displayPane the root display pane
	 * @param container the container for the content
	 */
	protected void onBuild(Pane displayPane, Pane container) {}

	// playable
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#play()
	 */
	public void play() {
		this.backgroundNode.play();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#stop()
	 */
	public void stop() {
		this.backgroundNode.stop();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.Playable#dispose()
	 */
	public void dispose() {
		this.backgroundNode.dispose();
	}
	
	// x
	
	/**
	 * Returns the x coordinate.
	 * @return double
	 */
	public double getX() {
		return this.x.get();
	}
	
	/**
	 * Sets the x coordinate.
	 * @param x the x
	 */
	public void setX(double x) {
		this.x.set(x);
	}
	
	/**
	 * Returns the x property.
	 * @return DoubleProperty
	 */
	public DoubleProperty xProperty() {
		return this.x;
	}
	
	// y
	
	/**
	 * Returns the y coordinate.
	 * @return double
	 */
	public double getY() {
		return this.y.get();
	}
	
	/**
	 * Sets the y coordinate.
	 * @param y the y
	 */
	public void setY(double y) {
		this.y.set(y);
	}
	
	/**
	 * Returns the y property.
	 * @return DoubleProperty
	 */
	public DoubleProperty yProperty() {
		return this.y;
	}
	
	// width

	/**
	 * Returns the width.
	 * @return double
	 */
	public double getWidth() {
		return this.width.get();
	}
	
	/**
	 * Sets the width.
	 * @param width the width
	 */
	public void setWidth(double width) {
		this.width.set(width);
	}
	
	/**
	 * Returns the width property.
	 * @return DoubleProperty
	 */
	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	// height

	/**
	 * Returns the height.
	 * @return double
	 */
	public double getHeight() {
		return this.height.get();
	}

	/**
	 * Sets the height.
	 * @param height the height
	 */
	public void setHeight(double height) {
		this.height.set(height);
	}
	
	/**
	 * Returns the height property.
	 * @return DoubleProperty
	 */
	public DoubleProperty heightProperty() {
		return this.height;
	}
	
	// background

	/**
	 * Sets the background.
	 * @param background the background
	 */	
	public void setBackground(SlidePaint background) {
		this.background.set(background);
	}

	/**
	 * Returns the background paint.
	 * @return {@link SlidePaint}
	 */
	public SlidePaint getBackground() {
		return this.background.get();
	}

	/**
	 * Returns the background property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlidePaint> backgroundProperty() {
		return this.background;
	}
	
	// border

	/**
	 * Sets the border.
	 * @param border the border
	 */
	public void setBorder(SlideStroke border) {
		this.border.set(border);
	}

	/**
	 * Returns the border.
	 * @return {@link SlideStroke}
	 */
	public SlideStroke getBorder() {
		return this.border.get();
	}
	
	/**
	 * Returns the border property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideStroke> borderProperty() {
		return this.border;
	}

	// opacity

	/**
	 * Sets the opacity.
	 * @param opacity the opacity
	 */
	public void setOpacity(double opacity) {
		this.opacity.set(opacity);
	}

	/**
	 * Returns the opacity.
	 * @return double
	 */
	public double getOpacity() {
		return this.opacity.get();
	}
	
	/**
	 * Returns the opacity property.
	 * @return DoubleProperty
	 */
	public DoubleProperty opacityProperty() {
		return this.opacity;
	}

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

	// opacity

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
	
	// scale
	
	/**
	 * Sets the scaling.
	 * @param scaling the scaling
	 */
	public void setScaling(Scaling scaling) {
		this.scale.set(scaling);
	}

	/**
	 * Returns the scaling.
	 * @return {@link Scaling}
	 */
	public Scaling getScaling() {
		return this.scale.get();
	}
	
	/**
	 * Returns the scaling property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<Scaling> scalingProperty() {
		return this.scale;
	}
	
	// name
	
	/**
	 * Returns the name of the region.
	 * @return String
	 */
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * Returns the name property.
	 * @return ReadOnlyStringProperty
	 */
	public ReadOnlyStringProperty nameProperty() {
		return this.name;
	}
}
