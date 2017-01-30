package org.praisenter.javafx.slide;

import java.util.UUID;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.utility.Scaling;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public abstract class ObservableSlideRegion<T extends SlideRegion> {
	protected final PraisenterContext context;
	protected final SlideMode mode;
	
	// the data
	
	protected final T region;
	
	// editable properties
	
	protected final DoubleProperty x = new SimpleDoubleProperty();
	protected final DoubleProperty y = new SimpleDoubleProperty();
	protected final DoubleProperty width = new SimpleDoubleProperty();
	protected final DoubleProperty height = new SimpleDoubleProperty();
	
	protected final ObjectProperty<SlidePaint> background = new SimpleObjectProperty<SlidePaint>();
	protected final ObjectProperty<SlideStroke> border = new SimpleObjectProperty<SlideStroke>();
	protected final DoubleProperty opacity = new SimpleDoubleProperty();
	protected final ObjectProperty<SlideShadow> shadow = new SimpleObjectProperty<SlideShadow>();
	protected final ObjectProperty<SlideShadow> glow = new SimpleObjectProperty<SlideShadow>();
	
	protected final ObjectProperty<Scaling> scale = new SimpleObjectProperty<Scaling>();
	
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
	// +-----------------------+--------------+-------------------------------+
	
	protected final Pane rootPane;
	private final Pane container;
	private final FillPane backgroundNode;
	private final Region borderNode;
	
	public ObservableSlideRegion(T region, PraisenterContext context, SlideMode mode) {
		this.region = region;
		this.context = context;
		this.mode = mode;
		
		this.scale.set(Scaling.getNoScaling(region.getWidth(), region.getHeight()));
		
		// set initial values
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
		if (this.mode == SlideMode.EDIT ||
			this.mode == SlideMode.MUSICIAN ||
			this.mode == SlideMode.PREVIEW) {
			this.container.setCacheHint(CacheHint.SPEED);
		}
		this.container.setBackground(null);
		this.container.setMouseTransparent(true);
		
		this.borderNode = new Region();
		this.borderNode.setMouseTransparent(true);
		
		this.backgroundNode = new FillPane(context, mode);
		this.backgroundNode.setMouseTransparent(true);
		
		this.rootPane = new Pane(this.container);
		this.rootPane.getStyleClass().add("slide-component");
		
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
			updateFill();
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
			this.container.setScaleX(nv.factor);
			this.container.setScaleY(nv.factor);
			
			updatePosition();
			updateSize();
		});
	}
	
	void updatePosition() {
		// set position
		
		// get the coordinates in slide space
		double x = this.x.get();
		double y = this.y.get();
		
		// set the position of the edit pane (which has the border)
		// to slide coordinates transformed into the parent node coordinates
		Scaling s = this.scale.get();
		
		this.rootPane.setLayoutX(x * s.factor);
		this.rootPane.setLayoutY(y * s.factor);
		
		updateScaledTranslation();
	}
	
	void updateSize() {
		double w = this.width.get();
		double h = this.height.get();
		
		Fx.setSize(this.container, w, h);
		Fx.setSize(this.borderNode, w, h);
		
		this.backgroundNode.setSize(w, h);
		
		Scaling s = this.scale.get();
		Fx.setSize(this.rootPane, w * s.factor, h * s.factor);
		
		updateScaledTranslation();
	}
	
	void updateScaledTranslation() {
		Scaling s = this.scale.get();
		// scaling operates from the center of the node
		// so we have to reposition the node so that it
		// stays in the same place
		// FIXME the border is jacking something up
		this.container.setTranslateX(-(this.width.get() - this.width.get() * s.factor) / 2.0);
		this.container.setTranslateY(-(this.height.get() - this.height.get() * s.factor) / 2.0);
	}
	
	void updateBorder() {
		SlideStroke ss = this.border.get();
		
		// create new border
		if (ss != null) {
			this.borderNode.setBorder(new Border(JavaFXTypeConverter.toJavaFX(ss)));
		} else {
			this.borderNode.setBorder(null);
		}
		
		double r = ss != null ? ss.getRadius() : 0.0;
		this.backgroundNode.setBorderRadius(r);
	}
	
	void updateFill() {
		SlidePaint paint = this.background.get();
		this.backgroundNode.setPaint(paint);
	}
	
	void updateOpacity() {
		this.container.setOpacity(this.opacity.get());
	}
	
	void updateEffects() {
		EffectBuilder builder = EffectBuilder.create();
		Effect shadow = JavaFXTypeConverter.toJavaFX(this.shadow.get());
		Effect glow = JavaFXTypeConverter.toJavaFX(this.glow.get());
		builder.add(shadow, shadow != null && shadow instanceof InnerShadow ? 10 : 30);
		builder.add(glow, glow != null && glow instanceof InnerShadow ? 20 : 40);
		Effect effect = builder.build();
		this.container.setEffect(effect);
	}
	
	void build(Node content) {
		// set initial node properties
		this.updatePosition();
		this.updateBorder();
		this.updateFill();
		this.updateSize();
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
	}
	
	/**
	 * Returns the root node of this slide to be placed in an
	 * scene using any {@link SlideMode}.
	 * @return Pane
	 */
	public Pane getDisplayPane() {
		return this.rootPane;
	}
	
	// playable stuff
	
	public void play() {
		this.backgroundNode.play();
	}
	
	public void stop() {
		this.backgroundNode.stop();
	}
	
	public void dispose() {
		this.backgroundNode.dispose();
	}
	
	public UUID getId() {
		return this.region.getId();
	}

	public boolean isBackgroundTransitionRequired(SlideRegion region) {
		return this.region.isBackgroundTransitionRequired(region);
	}
	
	public void adjust(double pw, double ph) {
		this.region.adjust(pw, ph);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
	}
	
	public Rectangle resize(int dw, int dh) {
		Rectangle r = this.region.resize(dw, dh);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
		return r;
	}
	
	public void translate(int dx, int dy) {
		this.region.translate(dx, dy);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
	}

	// x
	
	public double getX() {
		return this.x.get();
	}
	
	public void setX(double x) {
		this.x.set(x);
	}
	
	public DoubleProperty xProperty() {
		return this.x;
	}
	
	// y
	
	public double getY() {
		return this.y.get();
	}
	
	public void setY(double y) {
		this.y.set(y);
	}
	
	public DoubleProperty yProperty() {
		return this.y;
	}
	
	// width

	public double getWidth() {
		return this.width.get();
	}
	
	public void setWidth(double width) {
		this.width.set(width);
	}
	
	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	// height

	public double getHeight() {
		return this.height.get();
	}
	
	public void setHeight(double height) {
		this.height.set(height);
	}
	
	public DoubleProperty heightProperty() {
		return this.height;
	}
	
	// background
	
	public void setBackground(SlidePaint background) {
		this.background.set(background);
	}
	
	public SlidePaint getBackground() {
		return this.background.get();
	}
	
	public ObjectProperty<SlidePaint> backgroundProperty() {
		return this.background;
	}
	
	// border
	
	public void setBorder(SlideStroke border) {
		this.border.set(border);
	}
	
	public SlideStroke getBorder() {
		return this.border.get();
	}
	
	public ObjectProperty<SlideStroke> borderProperty() {
		return this.border;
	}

	// opacity
	
	public void setOpacity(double opacity) {
		this.opacity.set(opacity);
	}
	
	public double getOpacity() {
		return this.opacity.get();
	}
	
	public DoubleProperty opacityProperty() {
		return this.opacity;
	}

	// shadow
	
	public void setShadow(SlideShadow shadow) {
		this.shadow.set(shadow);
	}
	
	public SlideShadow getShadow() {
		return this.shadow.get();
	}
	
	public ObjectProperty<SlideShadow> shadowProperty() {
		return this.shadow;
	}

	// opacity

	public void setGlow(SlideShadow glow) {
		this.glow.set(glow);
	}
	
	public SlideShadow getGlow() {
		return this.glow.get();
	}
	
	public ObjectProperty<SlideShadow> glowProperty() {
		return this.glow;
	}
	
	// scale
	
	public void setScaling(Scaling scaling) {
		this.scale.set(scaling);
	}
	
	public Scaling getScaling() {
		return this.scale.get();
	}
	
	public ObjectProperty<Scaling> scalingProperty() {
		return this.scale;
	}
	
	// the slide
	
	public T getRegion() {
		return this.region;
	}
}
