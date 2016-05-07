package org.praisenter.javafx.screen;

import java.util.List;
import java.util.Queue;

import org.praisenter.javafx.configuration.ScreenMapping;
import org.praisenter.javafx.slide.FxSlide;

import javafx.animation.Animation.Status;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public final class DisplayScreen {
	private final ScreenMapping mapping;
	
	private final Stage stage;
	private final Pane surface;
	
	private Pane slideSurface0;
	private Pane slideSurface1;
	
	private Transition transition;
	
	private FxSlide slide0;
	private FxSlide slide1;
	
	public synchronized void send(FxSlide slide) {
		this.slide1 = slide;
		
		// an item was placed on the queue
		// whats the status of the current transition?
		if (transition != null) {
			if (transition.getStatus() == Status.RUNNING) {
				transition.setOnFinished((e) -> {
					this.display();
				});
			} else {
				// otherwise display it immediately
				this.display();
			}
		} else {
			// no transition is playing so just display immediately
			this.display();
		}
	}
	
	private synchronized void display() {
		FxSlide slide = this.slide1;
		// when it's time to display, we need to determine the transitions that
		// need to play.  
		
		// the master transition will hold the transitions for both
		// slides, the out-going and the in-coming slides.
		ParallelTransition master = new ParallelTransition();
		
		// this transition will contain all the transitions for the slide including
		// the transition for the slide itself and all its components
		ParallelTransition incoming = new ParallelTransition();
		
		master.getChildren().add(incoming);
		master.setOnFinished((e) -> {
			// when this transition is done we need to:
			// 1. stop all of slide0's media players
			List<MediaPlayer> players = this.slide0.getMediaPlayers();
			for (MediaPlayer mp : players) {
				mp.stop();
			}
			// 2. remove slide0 from the surface
			this.surface.getChildren().remove(this.slideSurface0);
			// 3. remove all of slide0's children
			this.slideSurface0.getChildren().clear();
			// 4. reassign slide1 to slide0
			Pane temp = this.slideSurface0;
			this.slideSurface0 = this.slideSurface1;
			this.slideSurface1 = temp;
			// 5. set the current slide
			this.slide0 = slide;
		});
		
		// add the new slide to the surface
		this.slideSurface1.getChildren().addAll(slide.getBackgroundNode(), slide.getContentNode(), slide.getBorderNode());
		this.surface.getChildren().add(slideSurface1);
		
		this.transition = master;

		// start the media players for this slide (if any)
		List<MediaPlayer> players = slide.getMediaPlayers();
		for (MediaPlayer mp : players) {
			mp.play();
		}
		
		this.transition.play();
	}
}
