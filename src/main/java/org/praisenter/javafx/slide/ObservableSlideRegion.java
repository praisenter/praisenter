package org.praisenter.javafx.slide;

import java.util.UUID;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;

import com.sun.javafx.scene.control.behavior.SliderBehavior;
import com.sun.prism.paint.RadialGradient;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public abstract class ObservableSlideRegion<T extends SlideRegion> implements SlideRegion {
	// the data
	
	final T region;
	final PraisenterContext context;
	final SlideMode mode;
	
	// editable
	
	final IntegerProperty x = new SimpleIntegerProperty();
	final IntegerProperty y = new SimpleIntegerProperty();
	final IntegerProperty width = new SimpleIntegerProperty();
	final IntegerProperty height = new SimpleIntegerProperty();
	
	final ObjectProperty<SlidePaint> background = new SimpleObjectProperty<SlidePaint>();
	final ObjectProperty<SlideStroke> border = new SimpleObjectProperty<SlideStroke>();
	
	// preview
	
	final Pane root;

	// MediaView (Video/Audio) or VBox (Image/Paint)
	final MediaView backgroundMedia;
	final VBox backgroundPaint;
	
	// always a Region
	final Region borderNode;
	
	public ObservableSlideRegion(T region, PraisenterContext context, SlideMode mode) {
		this.region = region;
		this.context = context;
		this.mode = mode;
		
		// set initial values
		this.x.set(region.getX());
		this.y.set(region.getY());
		this.width.set(region.getWidth());
		this.height.set(region.getHeight());
		this.background.set(region.getBackground());
		this.border.set(region.getBorder());
		
		// setup nodes
		this.root = new Pane();
		this.borderNode = new Region();
		this.backgroundMedia = new MediaView();
		this.backgroundPaint = new VBox();
		
		// set initial node properties
		this.update();
		
		// listen for changes
		this.x.addListener((obs, ov, nv) -> { 
			this.region.setX(nv.intValue());
			update();
		});
		this.y.addListener((obs, ov, nv) -> { 
			this.region.setY(nv.intValue());
			update();
		});
		this.width.addListener((obs, ov, nv) -> { 
			this.region.setWidth(nv.intValue());
			update();
		});
		this.height.addListener((obs, ov, nv) -> { 
			this.region.setHeight(nv.intValue());
			update();
		});
		this.background.addListener((obs, ov, nv) -> { 
			this.region.setBackground(nv);
			update();
		});
		this.border.addListener((obs, ov, nv) -> { 
			this.region.setBorder(nv);
			update();
		});
	}
	
	protected void update() {
		// set position
		int x = this.x.get();
		int y = this.y.get();
		this.root.setLayoutX(x);
		this.root.setLayoutY(y);
		
		// set sizes
		int w = this.width.get();
		int h = this.height.get();
		Fx.setSize(this.root, w, h);
		Fx.setSize(this.borderNode, w, h);
		
		SlidePaint sp = this.background.get();
		SlideStroke ss = this.border.get();
		double br = ss != null ? ss.getRadius() : 0;
		
		if (sp != null) {
			if (sp instanceof MediaObject) {
				MediaObject mo = (MediaObject) sp;
				UUID id = mo.getId();
				if (id != null) {
					Media media = context.getMediaLibrary().get(id);
					if (mode == SlideMode.EDIT || media.getMetadata().getType() == MediaType.IMAGE) {
						Image image = JavaFXTypeConverter.toJavaFXImage(context, media);
						JavaFXTypeConverter.setup(backgroundPaint, image, mo.getScaling(), w, h, br);
					} else if (media.getMetadata().getType() == MediaType.AUDIO || media.getMetadata().getType() == MediaType.VIDEO) {
						MediaPlayer player = JavaFXTypeConverter.toJavaFXMediaPlayer(context, media, mo.isLoop(), mo.isMute());
						JavaFXTypeConverter.setup(backgroundMedia, player, mo.getScaling(), w, h, br);
					} else {
						// FIXME handle
					}
				} else {
					// set to null
					JavaFXTypeConverter.setup(backgroundPaint, null, mo.getScaling(), w, h, br);
					JavaFXTypeConverter.setup(backgroundMedia, null, mo.getScaling(), w, h, br);
					backgroundPaint.setBackground(null);
				}
			} else if (sp instanceof SlideColor) { 
				this.backgroundPaint.setBackground(new Background(new BackgroundFill(JavaFXTypeConverter.toJavaFX((SlideColor)sp), new CornerRadii(br), null)));
				JavaFXTypeConverter.setup(backgroundPaint, null, null, w, h, br);
			} else if (sp instanceof SlideLinearGradient) {
				this.backgroundPaint.setBackground(new Background(new BackgroundFill(JavaFXTypeConverter.toJavaFX((SlideLinearGradient)sp), new CornerRadii(br), null)));
				JavaFXTypeConverter.setup(backgroundPaint, null, null, w, h, br);
			} else if (sp instanceof SlideRadialGradient) {
				this.backgroundPaint.setBackground(new Background(new BackgroundFill(JavaFXTypeConverter.toJavaFX((SlideRadialGradient)sp), new CornerRadii(br), null)));
				JavaFXTypeConverter.setup(backgroundPaint, null, null, w, h, br);
			} else {
				// FIXME handle
				JavaFXTypeConverter.setup(backgroundPaint, null, null, w, h, br);
				JavaFXTypeConverter.setup(backgroundMedia, null, null, w, h, br);
				backgroundPaint.setBackground(null);
			}
		} else {
			JavaFXTypeConverter.setup(backgroundPaint, null, null, w, h, br);
			JavaFXTypeConverter.setup(backgroundMedia, null, null, w, h, br);
			backgroundPaint.setBackground(null);
		}
		
		// border
		this.borderNode.setBorder(new Border(JavaFXTypeConverter.toJavaFX(this.border.get())));
	}
	
	final ObservableSlideRegion<T> build(Node content) {
		this.root.getChildren().addAll(
				this.backgroundMedia != null ? this.backgroundMedia : this.backgroundPaint,
				content,
				this.borderNode);
		return this;
	}
	
	@Override
	public UUID getId() {
		return this.region.getId();
	}

	@Override
	public boolean isBackgroundTransitionRequired(SlideRegion region) {
		return this.region.isBackgroundTransitionRequired(region);
	}
	
	@Override
	public void adjust(double pw, double ph) {
		this.region.adjust(pw, ph);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
	}
	
	@Override
	public Rectangle resize(int dw, int dh) {
		Rectangle r = this.region.resize(dw, dh);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
		return r;
	}
	
	@Override
	public void translate(int dx, int dy) {
		this.region.translate(dx, dy);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
	}

	// x
	
	@Override
	public int getX() {
		return this.x.get();
	}
	
	@Override
	public void setX(int x) {
		this.x.set(x);
	}
	
	public IntegerProperty xProperty() {
		return this.x;
	}
	
	// y
	
	@Override
	public int getY() {
		return this.y.get();
	}
	
	@Override
	public void setY(int y) {
		this.y.set(y);
	}
	
	public IntegerProperty yProperty() {
		return this.y;
	}
	
	// width

	@Override
	public int getWidth() {
		return this.width.get();
	}
	
	@Override
	public void setWidth(int width) {
		this.width.set(width);
	}
	
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	// height

	@Override
	public int getHeight() {
		return this.height.get();
	}
	
	@Override
	public void setHeight(int height) {
		this.height.set(height);
	}
	
	public IntegerProperty heightProperty() {
		return this.height;
	}
	
	// background
	
	@Override
	public void setBackground(SlidePaint background) {
		this.background.set(background);
	}
	
	@Override
	public SlidePaint getBackground() {
		return this.background.get();
	}
	
	public ObjectProperty<SlidePaint> backgroundProperty() {
		return this.background;
	}
	
	// border
	
	@Override
	public void setBorder(SlideStroke border) {
		this.border.set(border);
	}
	
	@Override
	public SlideStroke getBorder() {
		return this.border.get();
	}
	
	public ObjectProperty<SlideStroke> borderProperty() {
		return this.border;
	}
}
