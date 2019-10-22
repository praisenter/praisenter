package org.praisenter.ui.slide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.praisenter.data.TextStore;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.SlideRegion;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.data.slide.text.TextPlaceholderComponent;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
import org.praisenter.ui.slide.convert.TransitionConverter;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.Scaling;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
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
//	
//	private final ObjectProperty<Slide> slide1;
//	private final ObjectProperty<SlideNode> slideNode1;
	
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

	private final Pane surface;
	
	private Transition placeholderTransition;
	private Transition slideTransition;
	
	public SlideView(GlobalContext context) {
		this.context = context;
		
		this.slide = new SimpleObjectProperty<>();
		this.slideNode = new SimpleObjectProperty<>();
//		
//		this.slide1 = new SimpleObjectProperty<>();
//		this.slideNode1 = new SimpleObjectProperty<>();
//		
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
		this.surface = scaleContainer;
		
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
		
//		this.slideNode.addListener((obs, ov, nv) -> {
//			if (ov != null) {
//				scaleContainer.getChildren().remove(ov);
//				ov.dispose();
//				ov.mode.unbind();
//			}
//			if (nv != null) {
//				scaleContainer.getChildren().add(nv);
//				nv.mode.bind(this.mode);
//			}
//		});
		
//		this.slide1.addListener((obs, ov, nv) -> {
////			this.slideHeight.unbind();
////			this.slideWidth.unbind();
//			this.slideNode1.set(null);
//			
//			if (nv != null) {
////				this.slideWidth.bind(nv.widthProperty());
////				this.slideHeight.bind(nv.heightProperty());
//				this.slideNode1.set(new SlideNode(context, nv));
//			}
//		});
//		
//		this.slideNode1.addListener((obs, ov, nv) -> {
//			if (ov != null) {
//				scaleContainer.getChildren().remove(ov);
//				ov.dispose();
//				ov.mode.unbind();
//			}
//			if (nv != null) {
//				scaleContainer.getChildren().add(nv);
//				nv.mode.bind(this.mode);
//			}
//		});
		
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

	@Override
	public void play() {
		SlideNode slideNode = this.slideNode.get();
		if (slideNode != null) {
			slideNode.play();
		}
	}

	@Override
	public void pause() {
		SlideNode slideNode = this.slideNode.get();
		if (slideNode != null) {
			slideNode.pause();
		}
	}

	@Override
	public void stop() {
		SlideNode slideNode = this.slideNode.get();
		if (slideNode != null) {
			slideNode.stop();
		}
	}

	@Override
	public void dispose() {
		SlideNode slideNode = this.slideNode.get();
		if (slideNode != null) {
			slideNode.dispose();
		}
		Transition slideTx = this.slideTransition;
		if (slideTx != null) {
			slideTx.stop();
		}
		Transition placeholderTx = this.placeholderTransition;
		if (placeholderTx != null) {
			placeholderTx.stop();
		}
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
	
	public void swapSlide(Slide slide) {
		Slide oldSlide = this.slide.get();
		SlideNode oldNode = this.slideNode.get();
		
		// clean up
		if (oldNode != null) {
			this.slideNode.setValue(null);
			this.surface.getChildren().remove(oldNode);
			oldNode.mode.unbind();
			oldNode.dispose();
			oldNode = null;
		}
		
		// set new slide (always set null to ensure that
		// it registers the change of value)
		this.slide.set(null);
		this.slide.set(slide);
		
		if (slide != null) {
			final SlideNode newNode = this.slideNode.get();
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
		}
	}
	
	public void transitionSlide(Slide slide) {
		// TODO it works without this, but maybe we should enforce it
//		if (this.slideTransition != null) {
//			return;
//		}
		
		Slide oldSlide = this.slide.get();
		final SlideNode oldNode = this.slideNode.get();
		
		// set new slide (always set null to ensure that
		// it registers the change of value)
		this.slide.set(null);
		this.slide.set(slide);
		
		// create transition between the slides
		ParallelTransition tx = new ParallelTransition();
		
		if (oldNode != null) {
			Slide basis = slide != null ? slide : oldSlide;
			tx.getChildren().add(TransitionConverter.toJavaFX(basis.getTransition(), basis, null, oldNode, false));
		}

		if (slide != null) {
			final SlideNode newNode = this.slideNode.get();
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			tx.getChildren().add(TransitionConverter.toJavaFX(slide.getTransition(), slide, null, newNode, true));
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
		}
		
		tx.setOnFinished(e -> {
			if (oldNode != null) {
				this.surface.getChildren().remove(oldNode);
				oldNode.mode.unbind();
				oldNode.dispose();
			}
		});
		
		this.slideTransition = tx;
		
		tx.play();
	}

	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void swapPlaceholders(TextStore data) {
		Slide slide = this.slide.get();
		if (slide == null) return;
		
		slide.setPlaceholderData(data);
	}
	
	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void transitionPlaceholders(TextStore data) {
		// if the previous transition isn't complete, then do nothing
		// TODO need to queue it up and take the last one
		if (this.placeholderTransition != null) {
			return;
		}
		
		Slide slide = this.slide.get();
		if (slide == null) return;
		
		SlideNode slideNode = this.slideNode.get();
		if (slideNode == null) return;
		
		// copy the place holder components and convert them to static text components
		// so that they don't change when we update the place holder data
		int index = 0;
		Map<Integer, SlideComponent> asis = new HashMap<Integer, SlideComponent>();
		for (SlideComponent sc : slide.getComponents()) {
			if (sc instanceof TextPlaceholderComponent) {
				// convert to text components
				TextComponent tc = ((TextPlaceholderComponent) sc).toTextComponent();
				// for performance, we don't want to create another video player
				// for this temporary component. In addition, the video wouldn't
				// be playing back from the same position anyway
				if (this.isBackgroundVideo(tc)) {
					tc.setBackground(null);
					tc.setBorder(null);
				}
				asis.put(index, tc);
			}
			index++;
		}
		
		// add them to the slide
		// NOTE: we need to add them right after their source component to make sure
		// then animations are correct.  To do that we need to track how many we've added
		// so far and offset the index by that amount
		int added = 0;
		for (Integer key : asis.keySet()) {
			SlideComponent sc = asis.get(key);
			slide.getComponents().add(key + added + 1, sc);
			added++;
		}
		
		// update the placeholder data
		// TODO do we need to copy here?
		slide.setPlaceholderData(data);

		// setup transition
		SlideAnimation source = slide.getTransition();
		
		ParallelTransition tx = new ParallelTransition();
		for (SlideRegionNode<?> node : slideNode.getSlideComponentNodesUnmodifiable()) {
			// if the node is one of the copied ones
			// then we need to transition it out
			for (SlideComponent sc : asis.values()) {
				if (node.region == sc) {
					tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, sc, node, false));
				}
			}
			
			// otherwise if it's a placehoder we need
			// to transition it in
			if (node.region instanceof TextPlaceholderComponent) {
				Node nodeToAnimate = node;
				if (this.isBackgroundVideo(node.region)) {
					nodeToAnimate = node.content;
				}
				tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, (TextPlaceholderComponent)node.region, nodeToAnimate, true));
			}
		}

		// when complete, remove all the asis components
		tx.setOnFinished(e -> {
			slide.getComponents().removeAll(asis.values());
			this.placeholderTransition = null;
		});
		
		this.placeholderTransition = tx;
		
		// play the transition
		tx.play();
	}
	
	private boolean isBackgroundVideo(SlideRegion region) {
		SlidePaint bg = region.getBackground();
		if (bg instanceof MediaObject) {
			MediaObject mo = (MediaObject) bg;
			MediaType type = mo.getMediaType();
			if (type == MediaType.VIDEO) {
				return true;
			}
		}
		return false;
	}
	
	public Slide getSlide() {
		return this.slide.get();
	}
	
	public void setSlide(Slide slide) {
//		this.slide.set(slide);
		this.swapSlide(slide);
	}
	
	public ReadOnlyObjectProperty<Slide> slideProperty() {
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
