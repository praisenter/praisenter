package org.praisenter.ui.slide;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.TextStore;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.text.TextPlaceholderComponent;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
import org.praisenter.ui.slide.convert.TransitionConverter;
import org.praisenter.utility.Scaling;

import javafx.animation.Animation.Status;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class SlideView extends Region implements Playable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Image TRANSPARENT_PATTERN = new Image(SlideView.class.getResourceAsStream("/org/praisenter/images/transparent.png"));
	
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
	private final BooleanProperty checkeredBackgroundEnabled;
	private final BooleanProperty autoHideEnabled;
	
	private final BooleanProperty clipEnabled;
	private final Rectangle clip;

	private final Pane surface;
	
	private final Deque<TransitionRequest> requests;
	private Transition currentTransition;
	
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
		this.checkeredBackgroundEnabled = new SimpleBooleanProperty(true);
		this.autoHideEnabled = new SimpleBooleanProperty(false);
		
		this.clipEnabled = new SimpleBooleanProperty(false);

		this.requests = new ArrayDeque<>();
		this.currentTransition = null;
		
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
		
		// we size the slideBounds to a uniform scaled version
		// using the current available space and the slide's 
		// target resolution
		
		this.viewScale.bind(Bindings.createObjectBinding(() -> {
			double tw = this.getWidth();
			double th = this.getHeight();
			// NOTE: we can't access the slide.getWidth/getHeight methods here, instead we need to 
			//		 access the local slideWidth/slideHeight properties or we don't get notifications
			//		 of them changing. See the following link for more details:
			// https://stackoverflow.com/questions/40690022/javafx-custom-bindings-not-working
			double sw = this.slideWidth.get();
			double sh = this.slideHeight.get();
			if (sw <= 0 || sh <= 0) return Scaling.getNoScaling(tw, th);
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
//			Slide slide = this.slide.get();
//			SlideMode mode = this.mode.get();
//			if (mode != SlideMode.PRESENT && mode != SlideMode.TELEPROMPT && slide != null) {
			if (this.checkeredBackgroundEnabled.get()) {
				return new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null));
			}
			return null;
		}, this.checkeredBackgroundEnabled));
		
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
		Transition tx = this.currentTransition;
		if (tx != null) {
			tx.stop();
		}
		
		SlideNode slideNode = this.slideNode.get();
		if (slideNode != null) {
			slideNode.dispose();
		}
		this.requests.clear();
	}
	
	// TODO would be nice if there was a mechanism to wait for the SlideNode to load as well (when media players are ready for example) for better PRESENT interaction
	public CompletableFuture<Void> loadSlideAsync(Slide slide) {
		final Set<UUID> mediaIds = slide.getReferencedMedia();
		final List<Media> mediaToLoad = new ArrayList<>();
		
		for (UUID mediaId : mediaIds) {
			Media media = this.context.getWorkspaceManager().getItem(Media.class, mediaId);
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

		// clear any pending requests
		this.requests.clear();
		
		if (this.currentTransition != null) {
			LOGGER.debug("Transition in progress - immediately finishing it.");
			this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
		}

		if (oldSlide == null && slide == null) {
			return;
		}
		
		LOGGER.debug("Swapping slide: {} with {}", oldSlide, slide);
		
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
		if (oldSlide == slide) {
			this.slide.set(null);
		}
		this.slide.set(slide);
		
		if (slide != null) {
			final SlideNode newNode = this.slideNode.get();
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
			
			// even if we're doing a swap, we still need to check if
			// we should hide the slide after the auto-hide time
			if (this.autoHideEnabled.get()) {
				long time = slide.getTime();
				if (time != Slide.TIME_FOREVER && time > 0) {
					PauseTransition wait = new PauseTransition(new Duration(time * 1000));
					wait.setOnFinished(e -> {
						if (slide != null) {
							Slide current = this.slide.get();
							if (current == slide) {
								this.swapSlide(null);
							}
						}
					});
					wait.play();
				}
			}
		}
	}
	
	public void transitionSlide(Slide slide) {
		this.transitionSlide(slide, true);
	}
	
	public void transitionSlide(Slide slide, boolean waitForTransition) {
		// check if there's a transition currently executing
		if (this.currentTransition != null && this.currentTransition.getStatus() != Status.STOPPED) {
			// if there is one, should we stop it where it is and start the new
			// transition or should we queue it up to play after the executing one
			if (!waitForTransition) {
				// if we don't want to wait
				LOGGER.debug("A transition is currently in progress, stopping and doing new transition");
				// clear any pending requests so we don't execute them after ending the current transition
				this.requests.clear();
				// jump to the end of the current transition
				this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
				// then stop it
				this.currentTransition.stop();
				// manually complete any finalization steps for the transition
				EventHandler<ActionEvent> eh = this.currentTransition.getOnFinished();
				if (eh != null) {
					eh.handle(null);
				}
				// and set it to null
				this.currentTransition = null;
			} else {
				// if we want to wait, build a transition request and add it to the
				// request stack
				LOGGER.debug("A transition is currently in progress, waiting...");
				TransitionRequest tr = TransitionRequest.transitionSlide(slide);
				this.requests.push(tr);
				return;
			}
		}
		
		LOGGER.debug("Transitioning slide {}", slide);
		this.requests.clear();
		
		Slide oldSlide = this.slide.get();
		final SlideNode oldNode = this.slideNode.get();
		
		// set new slide (always set null to ensure that
		// it registers the change of value)
		this.slide.set(null);
		this.slide.set(slide);
		
		// create transition between the slides
		ParallelTransition inOutTransition = new ParallelTransition();
		SequentialTransition tx = new SequentialTransition(inOutTransition);
		
		if (oldNode != null) {
			Slide basis = slide != null ? slide : oldSlide;
			inOutTransition.getChildren().add(TransitionConverter.toJavaFX(basis.getTransition(), basis, null, oldNode, false));
		}

		if (slide != null) {
			final SlideNode newNode = this.slideNode.get();
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			inOutTransition.getChildren().add(TransitionConverter.toJavaFX(slide.getTransition(), slide, null, newNode, true));
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
			
			if (this.autoHideEnabled.get()) {
				long time = slide.getTime();
				if (time != Slide.TIME_FOREVER && time > 0) {
					PauseTransition wait = new PauseTransition(new Duration(time * 1000));
					tx.getChildren().add(wait);
					wait.setOnFinished(e -> {
						if (slide != null) {
							Slide current = this.slide.get();
							if (current == slide) {
								if (time != Slide.TIME_FOREVER && time > 0) {
									this.transitionSlide(null);
								}
							}
						}
					});
				}
			}
		}
		
		inOutTransition.setOnFinished(e -> {
			if (oldNode != null) {
				this.surface.getChildren().remove(oldNode);
				oldNode.mode.unbind();
				oldNode.dispose();
			}
		});
		
		this.currentTransition = tx;
		
		// no matter what, we always want to run something after the
		// transition ends - this gives us the ability to queue transitions
		// or do clean up after the transition finishes
		tx.setOnFinished(this::runLastPendingTransition);
		tx.play();
	}

	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void swapPlaceholders(TextStore data) {
		Slide slide = this.slide.get();
		if (slide == null) return;
		
		this.requests.clear();
		
		if (this.currentTransition != null) {
			LOGGER.debug("Transition in progress - immediately finishing it.");
			this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
		}
		
		LOGGER.debug("Swapping placeholder data");
		
		slide.setPlaceholderData(data);
	}
	
	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void transitionPlaceholders(TextStore data) {
		this.transitionPlaceholders(data, true);
	}
	
	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void transitionPlaceholders(TextStore data, boolean waitForTransition) {
		Slide slide = this.slide.get();
		if (slide == null) return;
		
		SlideNode slideNode = this.slideNode.get();
		if (slideNode == null) return;
		
		// check if there's a transition currently executing
		if (this.currentTransition != null && this.currentTransition.getStatus() != Status.STOPPED) {
			// if there is one, should we stop it where it is and start the new
			// transition or should we queue it up to play after the executing one
			if (!waitForTransition) {
				// if we don't want to wait
				LOGGER.debug("A transition is currently in progress, stopping and doing new transition");
				// clear any pending requests so we don't execute them after ending the current transition
				this.requests.clear();
				// jump to the end of the current transition
				this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
				// then stop it
				this.currentTransition.stop();
				// manually complete any finalization steps for the transition
				EventHandler<ActionEvent> eh = this.currentTransition.getOnFinished();
				if (eh != null) {
					eh.handle(null);
				}
				// and set it to null
				this.currentTransition = null;
			} else {
				// if we want to wait, build a transition request and add it to the
				// request stack
				LOGGER.debug("A transition is currently in progress, waiting...");
				TransitionRequest tr = TransitionRequest.transitionPlaceholders(data);
				this.requests.push(tr);
				return;
			}
		}
		
		this.requests.clear();
		LOGGER.debug("Transitioning placeholder data {}", data);
		
		// copy the place holder components and convert them to static text components
		// so that they don't change when we update the place holder data
		int index = 0;
		Map<Integer, SlideComponent> newComponents = new HashMap<Integer, SlideComponent>();
		List<SlideComponent> oldComponents = new ArrayList<SlideComponent>();
		for (SlideComponent sc : slide.getComponents()) {
			if (sc instanceof TextPlaceholderComponent) {
				TextPlaceholderComponent tpc = (TextPlaceholderComponent)sc;
				tpc.setTextLocked(true);
				
				newComponents.put(index, tpc.copy());
				oldComponents.add(sc);
			}
			index++;
		}
		
		// add them to the slide
		// NOTE: we need to add them right after their source component to make sure
		// the animations are correct.  To do that we need to track how many we've added
		// so far and offset the index by that amount
		int added = 0;
		for (Integer key : newComponents.keySet()) {
			SlideComponent sc = newComponents.get(key);
			slide.getComponents().add(key + added, sc);
			added++;
		}
		
		// update the placeholder data
		slide.setPlaceholderData(data);

		// setup transition
		SlideAnimation source = slide.getTransition();
		
		ParallelTransition tx = new ParallelTransition();
		for (SlideRegionNode<?> node : slideNode.getSlideComponentNodesUnmodifiable()) {
			boolean found = false;
			
			// transition out the old components
			for (SlideComponent sc : oldComponents) {
				if (node.region == sc) {
					tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, sc, node, false));
					found = true;
					break;
				}
			}
			
			if (found) continue;
			
			// transition in the new components
			for (SlideComponent sc : newComponents.values()) {
				if (node.region == sc) {
					tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, sc, node, true));
					break;
				}
			}
		}

		// no matter what, we always want to run something after the
		// transition ends - this gives us the ability to queue transitions
		// or do clean up after the transition finishes
		// when complete, remove all the asis components
		tx.setOnFinished(e -> {
			slide.getComponents().removeAll(oldComponents);
			// make sure to trigger any pending transitions
			this.runLastPendingTransition(e);
		});
		
		this.currentTransition = tx;
		
		// play the transition
		tx.play();
	}

	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void transitionContent(TextStore data) {
		this.transitionContent(data, true);
	}
	
	/**
	 * Updates the placeholder data for the currently rendered slide using the slide transition.
	 * @param data the placeholder data
	 */
	public void transitionContent(TextStore data, boolean waitForTransition) {
		Slide slide = this.slide.get();
		if (slide == null) return;
		
		SlideNode slideNode = this.slideNode.get();
		if (slideNode == null) return;
		
		// check if there's a transition currently executing
		if (this.currentTransition != null && this.currentTransition.getStatus() != Status.STOPPED) {
			// if there is one, should we stop it where it is and start the new
			// transition or should we queue it up to play after the executing one
			if (!waitForTransition) {
				// if we don't want to wait
				LOGGER.debug("A transition is currently in progress, stopping and doing new transition");
				// clear any pending requests so we don't execute them after ending the current transition
				this.requests.clear();
				// jump to the end of the current transition
				this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
				// then stop it
				this.currentTransition.stop();
				// manually complete any finalization steps for the transition
				EventHandler<ActionEvent> eh = this.currentTransition.getOnFinished();
				if (eh != null) {
					eh.handle(null);
				}
				// and set it to null
				this.currentTransition = null;
			} else {
				// if we want to wait, build a transition request and add it to the
				// request stack
				LOGGER.debug("A transition is currently in progress, waiting...");
				TransitionRequest tr = TransitionRequest.transitionContent(data);
				this.requests.push(tr);
				return;
			}
		}
		
		this.requests.clear();
		LOGGER.debug("Transitioning placeholder data {}", data);
		
		// copy all components and converting any placeholders to static text components
		// so that they don't change when we update the place holder data
		int index = 0;
		Map<Integer, SlideComponent> newComponents = new HashMap<Integer, SlideComponent>();
		List<SlideComponent> oldComponents = new ArrayList<>();
		
		for (SlideComponent sc : slide.getComponents()) {
			// lock the placeholder text so updates to the place holder data doesn't happen
			if (sc instanceof TextPlaceholderComponent) {
				TextPlaceholderComponent tpc = (TextPlaceholderComponent)sc;
				tpc.setTextLocked(true);
			}
			
			oldComponents.add(sc);
			newComponents.put(index, sc.copy());
			
			index++;
		}
		
		// add them to the slide
		// NOTE: we need to add them right after their source component to make sure
		// the animations are correct.  To do that we need to track how many we've added
		// so far and offset the index by that amount
		int added = 0;
		for (Integer key : newComponents.keySet()) {
			SlideComponent sc = newComponents.get(key);
			slide.getComponents().add(key + added, sc);
			added++;
		}
		
		// update the placeholder data
		slide.setPlaceholderData(data);

		// setup transition
		SlideAnimation source = slide.getTransition();
		
		ParallelTransition tx = new ParallelTransition();
		for (SlideRegionNode<?> node : slideNode.getSlideComponentNodesUnmodifiable()) {
			boolean found = false;
			
			// transition out the old components
			for (SlideComponent sc : oldComponents) {
				if (node.region == sc) {
					tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, sc, node, false));
					found = true;
					break;
				}
			}
			
			if (found) continue;
			
			// transition in the new components
			for (SlideComponent sc : newComponents.values()) {
				if (node.region == sc) {
					tx.getChildren().add(TransitionConverter.toJavaFX(source, slide, sc, node, true));
					break;
				}
			}
		}

		// no matter what, we always want to run something after the
		// transition ends - this gives us the ability to queue transitions
		// or do clean up after the transition finishes
		// when complete, remove all the asis components
		tx.setOnFinished(e -> {
			slide.getComponents().removeAll(oldComponents);
			// make sure to trigger any pending transitions
			this.runLastPendingTransition(e);
		});
		
		this.currentTransition = tx;
		
		// play the transition
		tx.play();
	}

	private void runLastPendingTransition(ActionEvent e) {
		LOGGER.debug("Transition complete, checking for pending transition requests");
		
		this.currentTransition = null;
		
		// NOTE: this is put in a run later because if we don't there's a flicker
		// when performing the slide/placeholder transition immediately
		Platform.runLater(() -> {
			int size = this.requests.size();
			if (size <= 0) {
				LOGGER.debug("No requests to process");
				return;
			}
			
			// get the most recent operation
			TransitionRequest request = this.requests.pop();
			if (request == null) {
				LOGGER.debug("No requests to process");
				// if it's null, then there's nothing to do
				return;
			}
			
			LOGGER.debug("{} transition requests present. Running {}", size, request.getType());
			LOGGER.debug("Clearing the transition requests stack");
			// if it's non-null, then clear the stack of all other pending
			// operations because we only want to run the most recent
			this.requests.clear();
			
			LOGGER.debug("Running most recent transition request");
			switch(request.getType()) {
				case SLIDE:
					transitionSlide(request.getSlide());
					break;
				case PLACEHOLDERS:
					transitionPlaceholders(request.getPlaceholderData());
					break;
				case CONTENT:
					transitionContent(request.getPlaceholderData());
					break;
				default:
					LOGGER.warn("Transition request type {} unknown", request.getType());
					break;
			}
		});
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
	
	public boolean isCheckeredBackgroundEnabled() {
		return this.checkeredBackgroundEnabled.get();
	}
	
	public void setCheckeredBackgroundEnabled(boolean flag) {
		this.checkeredBackgroundEnabled.set(flag);
	}
	
	public BooleanProperty checkeredBackgroundEnabledProperty() {
		return this.checkeredBackgroundEnabled;
	}
	
	public boolean isAutoHideEnabled() {
		return this.autoHideEnabled.get();
	}
	
	public void setAutoHideEnabled(boolean flag) {
		this.autoHideEnabled.set(flag);
	}
	
	public BooleanProperty autoHideEnabledProperty() {
		return this.autoHideEnabled;
	}
}
