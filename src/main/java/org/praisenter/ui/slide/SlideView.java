package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.Scaling;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class SlideView extends Pane implements Playable {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/images/transparent.png");
	
	private final ObjectProperty<Slide> slide;
	private final ObjectProperty<SlideNode> slideNode;
	
	private final DoubleProperty slideWidth;
	private final DoubleProperty slideHeight;
	
	private final ObjectProperty<SlideMode> mode;
	
	private final BooleanProperty viewScalingEnabled;
	private final ObjectProperty<Scaling> viewScale;
	private final DoubleProperty viewScaleX;
	private final DoubleProperty viewScaleY;
	
	public SlideView(GlobalContext context) {
		this.slide = new SimpleObjectProperty<>();
		this.slideNode = new SimpleObjectProperty<>();
		
		this.slideWidth = new SimpleDoubleProperty();
		this.slideHeight = new SimpleDoubleProperty();
		
		this.mode = new SimpleObjectProperty<>(SlideMode.VIEW);
		
		this.viewScalingEnabled = new SimpleBooleanProperty(false);
		this.viewScale = new SimpleObjectProperty<>(Scaling.getNoScaling(10, 10));
		this.viewScaleX = new SimpleDoubleProperty(1);
		this.viewScaleY = new SimpleDoubleProperty(1);

		this.setSnapToPixel(true);
		
		Pane viewBackground = new Pane();
		Pane scaleContainer = new Pane();
		
		this.slide.addListener((obs, ov, nv) -> {
			if (nv != null) {
				this.slideWidth.bind(nv.widthProperty());
				this.slideHeight.bind(nv.heightProperty());
				this.slideNode.set(new SlideNode(context, nv));
			} else {
				this.slideHeight.unbind();
				this.slideWidth.unbind();
				this.slideNode.set(null);
			}
		});
		
		this.slideNode.addListener((obs, ov, nv) -> {
			if (ov != null) {
				scaleContainer.getChildren().remove(ov);
				ov.dispose();
				ov.mode.unbind();
			}
			if (nv != null) {
				scaleContainer.getChildren().add(nv);
				nv.mode.bind(this.mode);
			}
		});
		
		// Node hierarchy:
		// +-------------------------------+--------------+---------------------------------------------------------+
		// | Name                          | Type         | Role                                                    |
		// +-------------------------------+--------------+---------------------------------------------------------+
		// | this                          | Pane         | Used to determine available width/height                |
		// | +- viewBackground             | Pane         | Transparent background, uniformly scaled width/height   |
		// |                               |              | based on parent node                                    |
		// |    +- scaleContainer          | Pane         | Uniform scaling from 0,0                                |
		// |       +- slideNode            | StackPane    | The root pane for the slide                             |
		// +-------------------------------+--------------+---------------------------------------------------------+
		
		// clip by the slidePreview area
//		Rectangle clipRect = new Rectangle(this.getWidth(), this.getHeight());
//		clipRect.heightProperty().bind(this.heightProperty());
//		clipRect.widthProperty().bind(this.widthProperty());
//		this.setClip(clipRect);
		
		// create the slideBounds area for the
		// unscaled transparency background
		// move to CSS
//		slideBounds.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		// add a drop shadow effect for better looks
//		DropShadow sdw = new DropShadow();
//		sdw.setRadius(5);
//		sdw.setColor(Color.rgb(0, 0, 0, 0.3));
//		slideBounds.setEffect(sdw);
//		
//		this.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
//			SlideMode mode = this.mode.get();
//			if (mode != SlideMode.PRESENT && mode != SlideMode.TELEPROMPT) {
//				return new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null));
//			}
//			return null;
//		}, this.mode));
//		
		// we size the slideBounds to a uniform scaled version
		// using the current available space and the slide's 
		// target resolution
		
//		this.slideWidth.addListener((obs, ov, nv) -> {
//			System.out.println("width updated" + nv);
//		});
//		
//		this.slideHeight.addListener((obs, ov, nv) -> {
//			System.out.println("height updated" + nv);
//		});
		
		this.viewScale.bind(Bindings.createObjectBinding(() -> {
			double tw = this.getWidth();
			double th = this.getHeight();
			Slide slide = this.slide.get();
			if (slide == null) return Scaling.getNoScaling(tw, th);
			// NOTE: we can't access the slide.getWidth/getHeight methods here, instead we need to 
			//		 access the local slideWidth/slideHeight properties or we don't get notifications
			//		 of them changing. See the following link for more details:
			// https://stackoverflow.com/questions/40690022/javafx-custom-bindings-not-working
			double sw = this.slideWidth.get();
			double sh = this.slideHeight.get();
			if (!this.viewScalingEnabled.get()) return Scaling.getNoScaling(sw, sh);
			Scaling scale = Scaling.getUniformScaling(sw, sh, tw, th);
			return scale;
		}, this.slideWidth, this.slideHeight, this.widthProperty(), this.heightProperty(), this.viewScalingEnabled));
		
		this.viewScaleX.bind(Bindings.createDoubleBinding(() -> {
			Scaling scaling = this.viewScale.get();
			return scaling.factor;
		}, this.viewScale));
		
		this.viewScaleY.bind(Bindings.createDoubleBinding(() -> {
			Scaling scaling = this.viewScale.get();
			return scaling.factor;
		}, this.viewScale));
		
//		this.setBorder(new Border(new BorderStroke(Color.DARKBLUE, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));
		
		viewBackground.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			Scaling scaling = this.viewScale.get();
			return scaling.width;
		}, this.viewScale));
		
		viewBackground.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			Scaling scaling = this.viewScale.get();
			return scaling.height;
		}, this.viewScale));
		
//		sppane.setBorder(new Border(new BorderStroke(Color.DARKTURQUOISE, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));

		viewBackground.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
			SlideMode mode = this.mode.get();
			if (mode != SlideMode.PRESENT && mode != SlideMode.TELEPROMPT) {
				return new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null));
			}
			return null;
		}, this.mode));
		
		viewBackground.setClip(null);

		Scale s = new Scale();
		s.xProperty().bind(this.viewScaleX);
		s.yProperty().bind(this.viewScaleY);
		s.setPivotX(0);
		s.setPivotY(0);
		scaleContainer.getTransforms().add(s);
		
		viewBackground.getChildren().add(scaleContainer);
		this.getChildren().addAll(viewBackground);
	}

	@Override
	public void play() {
		SlideNode slideView = this.slideNode.get();
		if (slideView != null) {
			slideView.play();
		}
	}

	@Override
	public void pause() {
		SlideNode slideView = this.slideNode.get();
		if (slideView != null) {
			slideView.pause();
		}
	}

	@Override
	public void stop() {
		SlideNode slideView = this.slideNode.get();
		if (slideView != null) {
			slideView.stop();
		}
	}

	@Override
	public void dispose() {
		SlideNode slideView = this.slideNode.get();
		if (slideView != null) {
			slideView.dispose();
		}
	}
	
	public Slide getSlide() {
		return this.slide.get();
	}
	
	public void setSlide(Slide slide) {
		this.slide.set(slide);
	}
	
	public ObjectProperty<Slide> slideProperty() {
		return this.slide;
	}
	
	public SlideMode getViewMode() {
		return this.mode.get();
	}
	
	public void setViewMode(SlideMode mode) {
		this.mode.set(mode);
	}
	
	public ObjectProperty<SlideMode> viewModeProperty() {
		return this.mode;
	}
	
	public boolean isViewScalingEnabled() {
		return this.viewScalingEnabled.get();
	}
	
	public void setViewScalingEnabled(boolean flag) {
		this.viewScalingEnabled.set(flag);
	}
	
	public BooleanProperty viewScalingEnabledProperty() {
		return this.viewScalingEnabled;
	}
	
	public Scaling getViewScale() {
		return this.viewScale.get();
	}
	
	public ReadOnlyObjectProperty<Scaling> viewScaleProperty() {
		return this.viewScale;
	}
	
	public double getViewScaleX() {
		return this.viewScaleX.get();
	}
	
	public ReadOnlyDoubleProperty viewScaleXProperty() {
		return this.viewScaleX;
	}
	
	public double getViewScaleY() {
		return this.viewScaleY.get();
	}
	
	public ReadOnlyDoubleProperty viewScaleYProperty() {
		return this.viewScaleY;
	}
}
