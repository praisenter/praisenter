package org.praisenter.javafx.screen;

import java.util.ArrayList;

import org.praisenter.Constants;
import org.praisenter.javafx.slide.ObservableSlide;

import javafx.animation.Animation.Status;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public final class DisplayScreen {
	private final int id;
	private final ScreenRole role;
	private final Screen screen;
	private final boolean debugMode;
	
	private final Stage stage;
	private final Pane surface;
	
	private Pane slideSurface0;
	private Pane slideSurface1;
	
	private Transition transition;
	
	private ObservableSlide<?> slide;
	
	public DisplayScreen(int id, ScreenRole role, Screen screen, boolean debug) {
		this.id = id;
		this.role = role;
		this.screen = screen;
		this.stage = new Stage(StageStyle.TRANSPARENT);
		this.debugMode = debug;
		
		Rectangle2D bounds = screen.getBounds();
		this.stage.initModality(Modality.NONE);
		this.stage.setX(bounds.getMinX());
		this.stage.setY(bounds.getMinY());
		this.stage.setWidth(bounds.getWidth());
		this.stage.setHeight(bounds.getHeight());
		this.stage.setMinWidth(bounds.getWidth());
		this.stage.setMinHeight(bounds.getHeight());
		this.stage.setMaxWidth(bounds.getWidth());
		this.stage.setMaxHeight(bounds.getHeight());
		this.stage.setResizable(false);
		
		// prevent the user from closing or hiding the window
		// JAVABUG 06/30/16 MEDIUM [workaround] At some point we need to come up with a way for the sub windows to not be seen; JavaFX does not have a facility for this at this time https://bugs.openjdk.java.net/browse/JDK-8091566
		// JAVABUG 10/31/16 LOW [workaround] Modal dialog brings wrong stage to front when closed https://bugs.openjdk.java.net/browse/JDK-8159226
		// for now we have to create these with no owner because once another window/stage owns these
		// focusing the owner also brings these windows to the foreground...
		EventHandler<WindowEvent> block = (WindowEvent e) -> {
			e.consume();
		};
		this.stage.setOnCloseRequest(block);
		this.stage.setOnHiding(block);
		
		this.surface = new Pane();
		this.surface.setBackground(null);
		if (this.debugMode) {
			this.surface.setBorder(new Border(new BorderStroke(
					Color.RED, 
					new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, new ArrayList<Double>()), 
					null, 
					new BorderWidths(10))));
		}
		this.stage.setScene(new Scene(this.surface, Color.TRANSPARENT));
		this.stage.setTitle(Constants.NAME + " (" + role + ")");
		this.stage.show();
		
		this.slideSurface0 = new Pane();
		this.slideSurface1 = new Pane();
		
		this.slideSurface0.setBackground(null);
		this.slideSurface1.setBackground(null);
	}
	
	public synchronized void send(final ObservableSlide<?> slide) {
		// an item was placed on the queue
		// whats the status of the current transition?
		if (transition != null) {
			if (transition.getStatus() == Status.RUNNING) {
				transition.setOnFinished((e) -> {
					this.display(slide);
				});
			} else {
				// otherwise display it immediately
				this.display(slide);
			}
		} else {
			// no transition is playing so just display immediately
			this.display(slide);
		}
	}
	
	private synchronized void display(final ObservableSlide<?> slide) {
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
			this.slide.stop();
			this.slide.dispose();
			// 2. remove slide0 from the surface
			this.surface.getChildren().remove(this.slideSurface0);
			// 3. remove all of slide0's children
			this.slideSurface0.getChildren().clear();
			// 4. reassign slide1 to slide0
			Pane temp = this.slideSurface0;
			this.slideSurface0 = this.slideSurface1;
			this.slideSurface1 = temp;
			// 5. set the current slide
			this.slide = slide;
		});
		
		// add the new slide to the surface
		this.slideSurface1.getChildren().add(slide.getDisplayPane());
		this.surface.getChildren().add(slideSurface1);
		
		this.transition = master;

		this.stage.toFront();
		
		// start the media players for this slide (if any)
		this.slide.play();
		
		this.transition.play();
	}

	public synchronized void release() {
		if (this.transition != null) {
			this.transition.stop();
		}
		
		if (this.slide != null) {
			this.slide.stop();
			this.slide.dispose();
		}
		
		this.stage.setOnHiding(null);
		this.stage.setOnCloseRequest(null);
		this.stage.close();
	}
}
