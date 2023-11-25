package org.praisenter.ui.slide;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.TextStore;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.text.TextPlaceholderComponent;
import org.praisenter.data.workspace.PlaceholderTransitionBehavior;
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
	
	private final ObjectProperty<PreparedSlide> slide;

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
	
	private final Queue<PendingPreparedSlide> requests;
	
	private Transition currentTransition;
	
	public SlideView(GlobalContext context) {
		this.context = context;
		
		this.slide = new SimpleObjectProperty<>();

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

		this.currentTransition = null;
		
		this.setSnapToPixel(true);
		
		Pane viewBackground = new Pane();
		Pane scaleContainer = new Pane();
		this.surface = scaleContainer;
		this.requests = new PriorityQueue<>();
		
		this.slide.addListener((obs, ov, nv) -> {
			this.slideHeight.unbind();
			this.slideWidth.unbind();
			
			if (nv != null) {
				Slide slide = nv.getSlide();
				if (slide != null) {
					this.slideWidth.bind(slide.widthProperty());
					this.slideHeight.bind(slide.heightProperty());
				}
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
			return Math.floor(scaling.width);
		}, this.viewScale));
		
		viewBackground.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			Scaling scaling = this.viewScale.get();
			return Math.floor(scaling.height);
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
			return Math.ceil(scaling.x);
		}, this.viewScale, this.viewScaleAlignCenter));
		
		viewBackground.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
			if (!this.viewScaleAlignCenter.get()) return 0.0;
			Scaling scaling = this.viewScale.get();
			return Math.ceil(scaling.y);
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
		PreparedSlide ps = this.slide.get();
		if (ps == null) {
			return;
		}
		
		SlideNode slideNode = ps.getNode();
		if (slideNode != null) {
			slideNode.play();
		}
	}

	@Override
	public void pause() {
		PreparedSlide ps = this.slide.get();
		if (ps == null) {
			return;
		}
		
		SlideNode slideNode = ps.getNode();
		if (slideNode != null) {
			slideNode.pause();
		}
	}

	@Override
	public void stop() {
		PreparedSlide ps = this.slide.get();
		if (ps == null) {
			return;
		}
		
		SlideNode slideNode = ps.getNode();
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
		
		PreparedSlide ps = this.slide.get();
		if (ps == null) {
			return;
		}
		
		SlideNode slideNode = ps.getNode();
		if (slideNode != null) {
			slideNode.dispose();
			slideNode = null;
		}
		
		this.slide.set(null);
		this.requests.clear();
	}
	
	/**
	 * Asynchronously renders the given slide in this {@link SlideView}.
	 * <p>
	 * The given slide and data will be used as-is.  If you don't want this SlideView
	 * from updating based on changes to the given slide, make sure to copy both the
	 * slide and the data before calling this method.
	 * <p>
	 * Use the transition parameter to control whether to transition the slide based
	 * on it's or the previous slide's transition, or to swap it immediately.
	 * @param slide
	 * @param data
	 * @param transition
	 * @return
	 */
	public CompletableFuture<Void> render(Slide slide, TextStore data, boolean transition) {
		LOGGER.debug("Rendering slide '{}'", slide);
		
		if (slide == null) {
			// this indicates a clear/hide action
			
			// when it's a clear action, there's no slide to prepare so we can
			// go ahead and attempt to "render" it (clear the current slide)
			PreparedSlide clear = new PreparedSlide(null, null, null, Instant.now());
			this.renderPreparedSlide(clear, transition);
			return CompletableFuture.completedFuture(null);
		} else {
			// we're presenting something new
			return this.prepareThenRender(slide, data, transition);
		}
	}
	
	/**
	 * Asynchronously prepares the slide and then attempts to render it when done.
	 * <p>
	 * Preparing a slide has three major steps:
	 * <ul>
	 * <li>Pre-loading images into memory</li>
	 * <li>Converting the slide to a {@link SlideNode}</li>
	 * <li>Waiting for Java FX MediaPlayers to be ready</li>
	 * </ul1>
	 * @param slide the slide
	 * @param data the data
	 * @param transition whether to transition or swap
	 * @return
	 */
	// sequenced helpers
	
	private CompletableFuture<Void> prepareThenRender(Slide slide, TextStore data, boolean transition) {
		// handle new display
		return this.prepare(slide, data).thenAccept((prepared) -> {
			// at this point we need to know whether this one is old
			// before attempting to present it
			PreparedSlide ps = this.slide.get();
			if (ps != null && ps.getTime().isAfter(prepared.getTime())) {
				SlideNode node = ps.getNode();
				node.dispose();
				LOGGER.debug("SKIPPING");
				// skip this action
				return;
			}
			
			// if it's not old, then we need to check if there's a
			// currently transitioning slide (the transition is in
			// progress)
			this.renderPreparedSlide(prepared, transition);
		});
	}

	/**
	 * Renders the given prepared slide.
	 * <p>
	 * This method will either:
	 * <ul>
	 * <li>Transition to the given slide</li>
	 * <li>Swap to the given slide</li>
	 * <li>Queue the given slide for rendering after the current transition completes</li>
	 * </ul>
	 * @param prepared
	 * @param transition
	 */
	private void renderPreparedSlide(PreparedSlide prepared, boolean transition) {
		boolean waitForTransition = this.context.getWorkspaceConfiguration().isWaitForTransitionsToCompleteEnabled();
		boolean transitionInProgress = this.isTransitionInProgress();
		
		// if we're supposed to wait for transitions and there is a transition in progress
		// when we need to queue up the given prepared slide for render after the transition
		// has completed
		if (waitForTransition && transitionInProgress) {
			LOGGER.debug("Queuing render of slide '{}'", prepared.getSlide());
			PendingPreparedSlide request = new PendingPreparedSlide(prepared, transition);
			this.requests.add(request);
			return;
		}
		
		// we got here because either we don't want to wait for transitions to complete
		// OR there's not one in progress, so if there is one in progress then we just
		// need to stop it
		if (transitionInProgress) {
			this.stopCurrentTransition();
		}

		// since we don't wait or there's not one in progress, so we
		// can just proceed to rendering it
		if (transition) {
			this.transitionPreparedSlide(prepared);
		} else {
			this.swapPreparedSlide(prepared);
		}
	}

	/**
	 * Returns true if there's a transition currently in progress.
	 * @return boolean
	 */
	private boolean isTransitionInProgress() {
		return this.currentTransition != null && this.currentTransition.getStatus() != Status.STOPPED;
	}
	
	/**
	 * Stops the current transition and makes sure there's no pending renders in the queue.
	 */
	private void stopCurrentTransition() {
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
	}

	/**
	 * Returns true if transitioning from one slide to another can transition the
	 * placeholders only or not.
	 * @param oldSlide the current slide
	 * @param newSlide the new slide
	 * @return boolean
	 */
	private boolean isPlaceholderTransitionOnly(Slide oldSlide, Slide newSlide) {
		// they must be the same slide (by identity)
		if (oldSlide == null && newSlide != null) return false;
		if (oldSlide != null && newSlide == null) return false;
		if (oldSlide == null && newSlide == null) return false;
		
		if (oldSlide.identityEquals(newSlide)) {
			// they must both be non-null
			if (oldSlide != null && newSlide != null) {
				// they must have the same modified date
				if (newSlide.getModifiedDate().equals(oldSlide.getModifiedDate())) {
					return true;
				}
			}
		}
		return false;
	}
	
	// internal swap handling
	
	// internal swap handling
	
	private void swapPreparedSlide(PreparedSlide prepared) {
		// get the current prepared slide
		final PreparedSlide oldPreparedSlide = this.slide.get();
		final Slide oldSlide = oldPreparedSlide == null ? null : oldPreparedSlide.getSlide();
		final SlideNode oldNode = oldPreparedSlide == null ? null : oldPreparedSlide.getNode();
		final TextStore oldData = oldPreparedSlide == null ? null : oldPreparedSlide.getData();

		// get the newly prepared slide
		final Slide newSlide = prepared.getSlide();
		final SlideNode newNode = prepared.getNode();
		final TextStore newData = prepared.getData();

		// if both are null, there's nothing to do
		if (oldSlide == null && newSlide == null) {
			return;
		}
		
		// check if the current transition is not complete
		if (this.currentTransition != null) {
			LOGGER.debug("Transition in progress - immediately finishing it.");
			this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
		}
		
		// figure out what to do with it
		if (this.isPlaceholderTransitionOnly(oldSlide, newSlide)) {
			PreparedSlide oldPrepared = new PreparedSlide(oldSlide, oldData, oldNode, prepared.getTime());
			this.swapPlaceholders(newData);
			this.slide.set(oldPrepared);
		} else {
			this.swapSlide(oldSlide, oldNode, newSlide, newNode);
			this.slide.set(prepared);
		}
	}
	
	private void swapSlide(Slide oldSlide, SlideNode oldNode, Slide newSlide, SlideNode newNode) {
		LOGGER.debug("Swapping slide: {} with {}", oldSlide, newSlide);
		
		// clean up the old node (if there was one)
		if (oldNode != null) {
			this.surface.getChildren().remove(oldNode);
			oldNode.mode.unbind();
			oldNode.dispose();
		}
		
		// if the new one is not null (not a clear)
		if (newSlide != null) {
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
			
			this.setupAutoHide(newSlide, false);
		}
	}

	private void swapPlaceholders(TextStore data) {
		PreparedSlide ps = this.slide.get();
		if (ps == null)
			return;
		
		Slide slide = ps.getSlide();
		if (slide == null) 
			return;
		
		if (this.currentTransition != null) {
			LOGGER.debug("Transition in progress - immediately finishing it.");
			this.currentTransition.jumpTo(this.currentTransition.getCycleDuration());
		}
		
		LOGGER.debug("Swapping placeholder data");
		
		slide.setPlaceholderData(data);
	}
	
	// internal transition handling
	
	private void transitionPreparedSlide(PreparedSlide prepared) {
		PlaceholderTransitionBehavior behavior = this.context.getWorkspaceConfiguration().getPlaceholderTransitionBehavior();
		
		final PreparedSlide oldPreparedSlide = this.slide.get();
		final Slide oldSlide = oldPreparedSlide == null ? null : oldPreparedSlide.getSlide();
		final SlideNode oldNode = oldPreparedSlide == null ? null : oldPreparedSlide.getNode();
		final TextStore oldData = oldPreparedSlide == null ? null : oldPreparedSlide.getData();
		
		final Slide newSlide = prepared.getSlide();
		final SlideNode newNode = prepared.getNode();
		final TextStore newData = prepared.getData();
		
		// figure out what to do with it
		if (this.isPlaceholderTransitionOnly(oldSlide, newSlide)) {
			PreparedSlide oldPrepared = new PreparedSlide(oldSlide, oldData, oldNode, prepared.getTime());
			// do placeholders only
			if (behavior == null || behavior == PlaceholderTransitionBehavior.PLACEHOLDERS) {
				this.transitionPlaceholders(newData);
				this.slide.set(oldPrepared);
			} else if (behavior == PlaceholderTransitionBehavior.CONTENT) {
				this.transitionContent(newData);
				this.slide.set(oldPrepared);
			} else {
				this.transitionSlide(oldSlide, oldNode, newSlide, newNode);
				this.slide.set(prepared);
			}
		} else {
			this.transitionSlide(oldSlide, oldNode, newSlide, newNode);
			this.slide.set(prepared);
		}
	}
	
	private void transitionSlide(Slide oldSlide, SlideNode oldNode, Slide newSlide, SlideNode newNode) {
		LOGGER.debug("Transitioning slide from '{}' to '{}'", oldSlide, newSlide);
		
		// create transition between the slides
		ParallelTransition inOutTransition = new ParallelTransition();
		SequentialTransition tx = new SequentialTransition(inOutTransition);
		
		if (oldNode != null) {
			Slide basis = newSlide != null ? newSlide : oldSlide;
			inOutTransition.getChildren().add(TransitionConverter.toJavaFX(basis.getTransition(), basis, null, oldNode, false));
		}

		if (newSlide != null) {
			newNode.mode.bind(this.mode);
			this.surface.getChildren().add(newNode);
			inOutTransition.getChildren().add(TransitionConverter.toJavaFX(newSlide.getTransition(), newSlide, null, newNode, true));
			
			if (this.mode.get() == SlideMode.PRESENT) {
				newNode.play();
			}
			
			Transition wait = this.setupAutoHide(newSlide, true);
			if (wait != null) {
				tx.getChildren().add(wait);
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

	private void transitionPlaceholders(TextStore data) {
		PreparedSlide ps = this.slide.get();
		if (ps == null)
			return;
		
		Slide slide = ps.getSlide();
		if (slide == null) 
			return;
		
		SlideNode slideNode = ps.getNode();
		if (slideNode == null) 
			return;
		
		LOGGER.debug("Transitioning placeholders for '{}'", slide);
		
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

	private void transitionContent(TextStore data) {
		PreparedSlide ps = this.slide.get();
		if (ps == null)
			return;
		
		Slide slide = ps.getSlide();
		if (slide == null) 
			return;
		
		SlideNode slideNode = ps.getNode();
		if (slideNode == null) 
			return;
		
		LOGGER.debug("Transitioning content for '{}'", slide);
		
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

	// transition wait trigger
	
	private PauseTransition setupAutoHide(final Slide slide, boolean transition) {
		// even if we're doing a swap, we still need to check if
		// we should hide the slide after the auto-hide time
		if (this.autoHideEnabled.get()) {
			long time = slide.getTime();
			if (time != Slide.TIME_FOREVER && time > 0) {
				PauseTransition wait = new PauseTransition(new Duration(time * 1000));
				wait.setOnFinished(e -> {
					LOGGER.debug("Slide '{}' duration ended, clearing the slide", slide);
					
					// get the current prepared slide (it could be different than what it was
					// when we triggered this wait)
					PreparedSlide ps = this.slide.get();
					Slide current = ps == null ? null : ps.getSlide();
					
					// we only clear the slide if the current slide is the same as the slide
					// we had presented at the time of triggering this wait
					if (current == slide) {
						PreparedSlide clear = new PreparedSlide(null, null, null, Instant.now());
						if (transition) {
							this.transitionPreparedSlide(clear);
						} else {
							this.swapPreparedSlide(clear);
						}
					}
				});
				wait.play();
				return wait;
			}
		}
		return null;
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
			PendingPreparedSlide request = this.requests.poll();
			if (request == null) {
				LOGGER.debug("No requests to process");
				// if it's null, then there's nothing to do
				return;
			}
			
			LOGGER.trace("Clearing the transition requests stack");
			// if it's non-null, then clear the stack of all other pending
			// operations because we only want to run the most recent
			this.requests.clear();
			
			LOGGER.debug("Running most recent transition request");
			PreparedSlide slide = request.getSlide();
			if (request.isTransition()) {
				this.transitionPreparedSlide(slide);
			} else {
				this.swapPreparedSlide(slide);
			}
		});
	}

	// prepare methods
	
	// slide preparation (preloading)
	
	private CompletableFuture<PreparedSlide> prepare(Slide slide, TextStore data) {
		Instant time = Instant.now();
		LOGGER.trace("Preparing slide '{}' for view", slide);
		Map<UUID, Path> mediaToLoad = this.getImagesToPreLoad(slide);
		return CompletableFuture.runAsync(() -> {
			// then on the same thread, load the images
			this.preLoadImages(mediaToLoad);
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((v) -> {
			// then on the JavaFX thread build the SlideNode
			// and trigger the async setup of media
			SlideNode slideNode = this.buildSlideNode(slide);
			
			// the wait for media-ready code has been commented out because it
			// didn't seem to effect the outcome - I was trying to wait until
			// they were ready so there were no visual artifacts when you hit
			// send - in particular laggy slide transitions that were choppy
			
			// it almost seemed like a spin up time to get the underlying
			// Java FX player ready for the first time (GStreamer)
			
			return slideNode;
		})).thenApplyAsync((slideNode) -> {
			// then on a threadpool thread, wait for the media to be ready
			this.waitForMediaReady(slideNode);
			return slideNode;
		}).thenComposeAsync(AsyncHelper.onJavaFXThreadAndWait((slideNode) -> {
			
			// then on the JavaFX thread, add to the prepared SlideNode to the
			// prepared queue and signal it's ready
			LOGGER.trace("Slide '{}' is ready for view", slide);
			PreparedSlide ptr = new PreparedSlide(slide, data, slideNode, time);
			return ptr;
		})).exceptionally(t -> {
			LOGGER.error("Failed to prepare slide: ", t);
			return null;
		});
	}
	
	
	private Map<UUID, Path> getImagesToPreLoad(Slide slide) {
		final Set<UUID> mediaIds = slide.getReferencedMedia();
		final Map<UUID, Path> mediaToLoad = new HashMap<>();
		
		LOGGER.trace("Checking for media that hasn't been loaded");
		for (UUID mediaId : mediaIds) {
			Media media = this.context.getWorkspaceManager().getItem(Media.class, mediaId);
			if (media != null) {
				if (media.getMediaType() == MediaType.IMAGE) {
					if (!this.context.getImageCache().isImageCached(mediaId)) {
						LOGGER.trace("Image media '{}' has not been loaded yet", media.getName());
						mediaToLoad.put(media.getId(), media.getMediaPath());
					}
				} else if (media.getMediaType() == MediaType.VIDEO && this.mode.get() != SlideMode.PRESENT) {
					if (!this.context.getImageCache().isImageCached(mediaId)) {
						LOGGER.trace("Video media image '{}' has not been loaded yet", media.getName());
						mediaToLoad.put(media.getId(), media.getMediaImagePath());
					}
				}
			}
		}
		
		return mediaToLoad;
	}
	
	
	private void preLoadImages(Map<UUID, Path> mediaToLoad) {
		if (mediaToLoad.size() > 0) {
			LOGGER.trace("Loading {} images", mediaToLoad.size());
			for (var entry : mediaToLoad.entrySet()) {
				LOGGER.trace("Loading media '{}'", entry.getValue());
				this.context.getImageCache().getOrLoadImage(entry.getKey(), entry.getValue());
			}
		} else {
			LOGGER.trace("No media to load, returning");
		}
	}
	
	
	private SlideNode buildSlideNode(Slide slide) {
		LOGGER.trace("Creating JavaFX nodes");
		SlideNode node = new SlideNode(this.context, slide);
		
		LOGGER.trace("Chaning mode to: {}", this.mode.get());
		node.setMode(this.mode.get());
		node.isReady();
		
		LOGGER.trace("JavaFX nodes ready");
		return node;
	}
	
	
	private void waitForMediaReady(SlideNode node) {
		int waitTimeInMS = 0;
		final int waitTimeIncrementInMS = 100;
		final int maxWaitTimeInMS = 5 * 1000;
		
		LOGGER.trace("Staring wait for media players");
		while (!node.isReady()) {
			try {
				Thread.sleep(waitTimeIncrementInMS);
				waitTimeInMS += waitTimeIncrementInMS;
			} catch (InterruptedException e) {
				LOGGER.error("The thread waiting for media players to get ready was interrupted", e);
				break;
			}
			
			if (waitTimeInMS >= maxWaitTimeInMS) {
				LOGGER.warn("Waiting for media to get ready took too long {} ms", waitTimeInMS);
				break;
			}
		}
			
		LOGGER.trace("Media players are ready!");
	}
	
	
	// properties
		

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
