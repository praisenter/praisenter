package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.easing.EasingType;
import org.praisenter.javafx.easing.Easings;
import org.praisenter.javafx.transition.CustomTransition;
import org.praisenter.javafx.transition.TransitionType;
import org.praisenter.javafx.transition.Transitions;
import org.praisenter.javafx.utility.JavaFxNodeHelper;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideTransition;
import org.praisenter.slide.text.TextComponent;

// TODO need to address editing and resizing.  The building of the JavaFX UI is highly dependent on a number of things so we may end up needing to update a bunch of stuff (width/height in particluar)

public class FxSlide extends FxSlideRegion<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final Pane contentNode;
	
	List<FxSlideComponent> children;
	
	// players for audio/video
	List<MediaPlayer> players;
	
	// the slide transition
	Transition transition;
	
	// any other playable animations
	ParallelTransition animation;
	
	public FxSlide(PraisenterContext context, Slide component, SlideMode mode) {
		super(context, component, mode);
		
		this.contentNode = new Pane();
		this.children = new ArrayList<>();
		this.players = new ArrayList<MediaPlayer>();
		
		// width/height
		int w = this.component.getWidth();
		int h = this.component.getHeight();
		JavaFxNodeHelper.setSize(this.contentNode, w, h);
		
		// this assumes the components are in the correct order
		Iterator<SlideComponent> it = this.component.getComponentIterator();
		while (it.hasNext()) {
			SlideComponent sc = it.next();
			FxSlideComponent<?> comp = null;
			if (sc instanceof MediaComponent) {
				comp = new FxSlideMediaComponent(this.context, (MediaComponent)sc, this.mode);
			} else if (sc instanceof TextComponent) {
				comp = new FxSlideTextComponent(this.context, (TextComponent)sc, this.mode);
			}
			if (comp != null) {
				// add the node to the foreground
				this.contentNode.getChildren().add(comp.contentNode);
				this.children.add(comp);
			}
		}

		// set the slide transition
		// TODO the exiting slide will also need to be transitioned, maybe add a method to get a transition to exit the current slide
		SlideTransition stx = this.component.getTransition(this.component.getId());
		if (stx != null) {
			CustomTransition ctx = Transitions.getTransition(stx.getTransitionId());
			// TODO not sure if IN is correct here or not
			ctx.setInterpolator(Easings.getEasing(stx.getEasingId(), EasingType.IN));
			ctx.setAutoReverse(false);
			ctx.setCycleCount(0);
			ctx.setDelay(Duration.ZERO);
			ctx.setDuration(Duration.millis(stx.getDuration()));
			// TODO this depends on the outgoing and incoming slides
			// ctx.setNode(node);
			ctx.setType(TransitionType.IN);
			this.transition = ctx;
		}
		
		// TODO other animations		
		
	}

	public Node getContentNode() {
		return this.contentNode;
	}
	
	public List<MediaPlayer> getMediaPlayers() {
		List<MediaPlayer> players = new ArrayList<>();
		
		players.addAll(super.getMediaPlayers());
		for (FxSlideComponent<?> wrapper : this.children) {
			players.addAll(wrapper.getMediaPlayers());
		}
		
		return players;
	}
}