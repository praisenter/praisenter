package org.praisenter.javafx.display;

import java.util.ArrayList;

import org.praisenter.Constants;
import org.praisenter.configuration.Display;
import org.praisenter.javafx.slide.ObservableSlide;

import javafx.animation.Animation.Status;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public final class DisplayTarget {
	private final ObjectProperty<Display> display = new SimpleObjectProperty<>();
	private final boolean debugMode;
	
	private final Stage stage;
	private final Pane surface;
	
	private Pane slideSurface0;
	private Pane slideSurface1;
	
	private Transition transition;
	
	private ObservableSlide<?> slide;
	
	public DisplayTarget(Display display, boolean debug) {
		this.stage = new Stage(StageStyle.TRANSPARENT);

    	// icons
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon16x16alt.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon32x32.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon48x48.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon64x64.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon96x96.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon128x128.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon256x256.png"));
		this.stage.getIcons().add(new Image("org/praisenter/resources/logo/icon512x512.png"));
    	
		this.debugMode = debug;
		
		this.stage.initModality(Modality.NONE);
		this.stage.setResizable(false);
		
		// prevent the user from closing or hiding the window
		// JAVABUG (M) 06/30/16 [workaround] At some point we need to come up with a way for the sub windows to not be seen; JavaFX does not have a facility for this at this time https://bugs.openjdk.java.net/browse/JDK-8091566
		// JAVABUG (L) 10/31/16 [workaround] Modal dialog brings wrong stage to front when closed https://bugs.openjdk.java.net/browse/JDK-8159226
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
		this.stage.show();
		
		this.slideSurface0 = new Pane();
		this.slideSurface1 = new Pane();
		
		this.slideSurface0.setBackground(null);
		this.slideSurface1.setBackground(null);
		
		// events
		
		this.display.addListener((obs, ov, nv) -> {
			if (nv != null) {
				this.stage.setX(nv.getX());
				this.stage.setY(nv.getY());
				this.stage.setWidth(nv.getWidth());
				this.stage.setHeight(nv.getHeight());
				this.stage.setMinWidth(nv.getWidth());
				this.stage.setMinHeight(nv.getHeight());
				this.stage.setMaxWidth(nv.getWidth());
				this.stage.setMaxHeight(nv.getHeight());
				
				this.stage.setTitle(Constants.NAME + " " + nv.getName() + " (" + nv.getRole() + ")");
			}
		});
		
		this.display.set(display);
	}
	
	public Display getDisplay() {
		return this.display.get();
	}
	
	public void setDisplay(Display display) {
		this.display.set(display);
	}
	
	public ObjectProperty<Display> displayProperty() {
		return this.display;
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
