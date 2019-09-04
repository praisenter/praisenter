package org.praisenter.ui.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.praisenter.async.AsyncHelper;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
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
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class SlideView extends Region implements Playable {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/images/transparent.png");
	
	private final GlobalContext context;
	
	private final ObjectProperty<Slide> slide;
	private final ObjectProperty<SlideNode> slideNode;
	
	private final DoubleProperty slideWidth;
	private final DoubleProperty slideHeight;
	
	private final ObjectProperty<SlideMode> mode;
	
	private final BooleanProperty fitToWidthEnabled;
	private final BooleanProperty fitToHeightEnabled;
	private final ObjectProperty<Scaling> viewScale;
	private final DoubleProperty viewScaleFactor;
	private final BooleanProperty viewScaleAlignCenter;
	
	private final BooleanProperty clipEnabled;
	private final Rectangle clip;
	
	public SlideView(GlobalContext context) {
		this.context = context;
		
		this.slide = new SimpleObjectProperty<>();
		this.slideNode = new SimpleObjectProperty<>();
		
		this.slideWidth = new SimpleDoubleProperty();
		this.slideHeight = new SimpleDoubleProperty();
		
		this.mode = new SimpleObjectProperty<>(SlideMode.VIEW);
		
		this.viewScale = new SimpleObjectProperty<>(Scaling.getNoScaling(10, 10));
		this.viewScaleFactor = new SimpleDoubleProperty(1);
		this.viewScaleAlignCenter = new SimpleBooleanProperty(false);
		this.fitToWidthEnabled = new SimpleBooleanProperty(false);
		this.fitToHeightEnabled = new SimpleBooleanProperty(false);
		
		this.clipEnabled = new SimpleBooleanProperty(false);

		this.setSnapToPixel(true);
		
		Pane viewBackground = new Pane();
		Pane scaleContainer = new Pane();
		
		this.slide.addListener((obs, ov, nv) -> {
			this.slideHeight.unbind();
			this.slideWidth.unbind();
			this.slideNode.set(null);
			
			if (nv != null) {
				this.slideWidth.bind(nv.widthProperty());
				this.slideHeight.bind(nv.heightProperty());
				this.slideNode.set(new SlideNode(context, nv));
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
			Scaling scale = Scaling.getUniformScaling(sw, sh, tw, th, this.fitToWidthEnabled.get(), this.fitToHeightEnabled.get());
			return scale;
		}, this.slide, this.slideWidth, this.slideHeight, this.widthProperty(), this.heightProperty(), this.fitToWidthEnabled, this.fitToHeightEnabled));
		
		this.viewScaleFactor.bind(Bindings.createDoubleBinding(() -> {
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
		
//		viewBackground.setBorder(new Border(new BorderStroke(Color.DARKTURQUOISE, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));

		viewBackground.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
			Slide slide = this.slide.get();
			SlideMode mode = this.mode.get();
			if (mode != SlideMode.PRESENT && mode != SlideMode.TELEPROMPT && slide != null) {
				return new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null));
			}
			return null;
		}, this.mode, this.slide));
		
		viewBackground.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
			if (!this.viewScaleAlignCenter.get()) return 0.0;
			Scaling scaling = this.viewScale.get();
			return scaling.x;
		}, this.viewScale, this.viewScaleAlignCenter));
		
		viewBackground.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
			if (!this.viewScaleAlignCenter.get()) return 0.0;
			Scaling scaling = this.viewScale.get();
			return scaling.y;
		}, this.viewScale, this.viewScaleAlignCenter));
		
		this.clip = new Rectangle();
		this.clip.setX(0);
		this.clip.setY(0);
		this.clip.widthProperty().bind(viewBackground.widthProperty());
		this.clip.heightProperty().bind(viewBackground.heightProperty());
		
		viewBackground.clipProperty().bind(Bindings.createObjectBinding(() -> {
			if (this.clipEnabled.get()) {
				return this.clip;
			}
			return null;
		}, this.clipEnabled));

		Scale s = new Scale();
		s.xProperty().bind(this.viewScaleFactor);
		s.yProperty().bind(this.viewScaleFactor);
		s.setPivotX(0);
		s.setPivotY(0);
		scaleContainer.getTransforms().add(s);
		
//		scaleContainer.setBorder(new Border(new BorderStroke(Color.RED, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(4.0))));
		
		viewBackground.getChildren().add(scaleContainer);
		this.getChildren().addAll(viewBackground);
	}
	
	// TODO would be nice if there was a mechanism to wait for the SlideNode to load as well (when media players are ready for example) for better PRESENT interaction
	public CompletableFuture<Void> loadSlideAsync(Slide slide) {
		final Set<UUID> mediaIds = slide.getReferencedMedia();
		final List<Media> mediaToLoad = new ArrayList<>();
		
		for (UUID mediaId : mediaIds) {
			Media media = this.context.getDataManager().getItem(Media.class, mediaId);
			if (media != null) {
				mediaToLoad.add(media);
			}
		}
		
		return CompletableFuture.runAsync(() -> {
			for (Media media : mediaToLoad) {
				if (media.getMediaType() == MediaType.IMAGE) {
					// load the image
					this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaPath());
				} else if (media.getMediaType() == MediaType.VIDEO && this.mode.get() != SlideMode.PRESENT) {
					// load the video frame (NOTE: we don't need this for present mode)
					this.context.getImageCache().getOrLoadImage(media.getId(), media.getMediaImagePath());
				}
			}
		});
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
	
	public Scaling getViewScale() {
		return this.viewScale.get();
	}
	
	public ReadOnlyObjectProperty<Scaling> viewScaleProperty() {
		return this.viewScale;
	}
	
	public double getViewScaleFactor() {
		return this.viewScaleFactor.get();
	}
	
	public ReadOnlyDoubleProperty viewScaleFactorProperty() {
		return this.viewScaleFactor;
	}

	public boolean isFitToWidthEnabled() {
		return this.fitToWidthEnabled.get();
	}
	
	public void setFitToWidthEnabled(boolean flag) {
		this.fitToWidthEnabled.set(flag);
	}
	
	public BooleanProperty fitToWidthEnabledProperty() {
		return this.fitToWidthEnabled;
	}
	
	public boolean isFitToHeightEnabled() {
		return this.fitToHeightEnabled.get();
	}
	
	public void setFitToHeightEnabled(boolean flag) {
		this.fitToHeightEnabled.set(flag);
	}
	
	public BooleanProperty fitToHeightEnabledProperty() {
		return this.fitToHeightEnabled;
	}
	
	public boolean isViewScaleAlignCenter() {
		return this.viewScaleAlignCenter.get();
	}
	
	public void setViewScaleAlignCenter(boolean flag) {
		this.viewScaleAlignCenter.set(flag);
	}
	
	public BooleanProperty viewScaleAlignCenterProperty() {
		return this.viewScaleAlignCenter;
	}
	
	public boolean isClipEnabled() {
		return this.clipEnabled.get();
	}
	
	public void setClipEnabled(boolean flag) {
		this.clipEnabled.set(flag);
	}
	
	public BooleanProperty clipEnabledProperty() {
		return this.clipEnabled;
	}
}
