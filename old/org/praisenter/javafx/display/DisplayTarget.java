package org.praisenter.javafx.display;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.configuration.Display;
import org.praisenter.configuration.Setting;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.animation.Animations;

import javafx.animation.Animation.Status;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
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

// FIXME we need a solution to retain the currently animating background in the video background case...

public final class DisplayTarget {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableConfiguration configuration;
	
	private final ObjectProperty<Display> display = new SimpleObjectProperty<>();
	
	private final Stage stage;
	private final Pane surface;
	
	private Pane slideSurface0;
	private Pane slideSurface1;
	
	private Transition transition;
	
	private ObservableSlide<?> slide;
	
	public DisplayTarget(ObservableConfiguration configuration, Display display) {
		this.configuration = configuration;
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
		this.stage.setScene(new Scene(this.surface, Color.TRANSPARENT));
		this.stage.show();
		
		// setup debug mode notification
		this.configuration.getSettings().addListener((MapChangeListener.Change<? extends Setting, ? extends Object> c) -> {
			if (c.getKey() == Setting.APP_DEBUG_MODE) {
				Object value = c.getValueAdded();
				if (c.wasAdded() && value != null) {
					boolean on = (boolean)value;
					if (on) {
						this.surface.setBorder(new Border(new BorderStroke(
								Color.RED, 
								new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, new ArrayList<Double>()), 
								null, 
								new BorderWidths(10))));
						return;
					}
				}
				this.surface.setBorder(null);
			}
		});
		
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
	
	@Override
	public String toString() {
		return this.display.get().getName();
	}
	
	// the display
	
	public Display getDisplay() {
		return this.display.get();
	}
	
	public void setDisplay(Display display) {
		this.display.set(display);
	}
	
	public ObjectProperty<Display> displayProperty() {
		return this.display;
	}
	
	// helpers
	
	public synchronized void send(final ObservableSlide<?> slide) {
		// an item was placed on the queue
		// whats the status of the current transition?
		if (transition != null) {
			if (transition.getStatus() == Status.RUNNING && this.configuration.getBoolean(Setting.PRESENT_WAIT_FOR_TRANSITIONS_TO_COMPLETE, true)) {
				transition.setOnFinished((e) -> {
					// perform clean up first
					this.cleanup(this.slide);
					LOGGER.debug("Transition complete. Showing next slide.");
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
		Transition master = Animations.buildSlideTransition(this.slide, slide);
		
		// this transition will contain all the transitions for the slide including
		// the transition for the slide itself and all its components
//		ParallelTransition incoming = new ParallelTransition();
//		
//		master.getChildren().add(incoming);
		master.setOnFinished((e) -> {
			this.cleanup(slide);
		});
		
		// add the new slide to the surface
		this.slideSurface1.getChildren().add(slide.getDisplayPane());
		this.surface.getChildren().add(slideSurface1);
		
		this.transition = master;

		this.stage.toFront();
		
		// start the media players for this slide (if any)
		slide.play();
		
		this.transition.play();
	}
	
	private void cleanup(ObservableSlide<?> slide) {
		LOGGER.debug("Transition complete. Performing house cleaning to prepare for next slide.");
		
		// when this transition is done we need to:
		// 1. stop all of slide0's media players
		if (this.slide != null) {
			this.slide.stop();
			this.slide.dispose();
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
		this.slide = slide;
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
