package org.praisenter.ui.display;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.TextStore;
import org.praisenter.data.configuration.Display;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;

import javafx.beans.property.ReadOnlyObjectProperty;
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

// TODO support for notifications probably needs another SlideView stacked on top of the main one

public final class DisplayTarget {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final double FOREGROUND = 2;
	private static final double BACKGROUND = 1;
	
	private final GlobalContext context;
	private final Display display;
	
	private final Stage stage;
	private final Pane container;
	
	private SlideView slideView;
	
	public DisplayTarget(GlobalContext context, Display display) {
		this.context = context;
		this.display = display;
		
		this.stage = new Stage(StageStyle.TRANSPARENT);

    	// icons
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon16x16alt.png", 16, 16, true, true));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon32x32.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon48x48.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon64x64.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon96x96.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon128x128.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon256x256.png"));
		this.stage.getIcons().add(new Image("org/praisenter/logo/icon512x512.png"));
    	
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

		this.container = new Pane();
		this.container.setBackground(null);
		
		this.stage.setScene(new Scene(this.container, Color.TRANSPARENT));
		this.stage.show();
		
		// setup debug mode notification
		this.context.getConfiguration().debugModeEnabledProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				this.container.setBorder(new Border(new BorderStroke(
					Color.RED, 
					new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, new ArrayList<Double>()), 
					null, 
					new BorderWidths(10))));
			} else {
				this.container.setBorder(null);
			}
		});
		
		this.slideView = new SlideView(context);
		this.slideView.setClipEnabled(false);
		this.slideView.setFitToHeightEnabled(false);
		this.slideView.setFitToWidthEnabled(false);
		this.slideView.setViewMode(SlideMode.PRESENT);
		
		this.container.getChildren().addAll(this.slideView);
		
		// events
		
		this.stage.setX(display.getX());
		this.stage.setY(display.getY());
		this.stage.setWidth(display.getWidth());
		this.stage.setHeight(display.getHeight());
		this.stage.setMinWidth(display.getWidth());
		this.stage.setMinHeight(display.getHeight());
		this.stage.setMaxWidth(display.getWidth());
		this.stage.setMaxHeight(display.getHeight());
		
		this.stage.setTitle(Constants.NAME + " " + display.toString());
	}
	
	@Override
	public String toString() {
		return this.display.toString();
	}
	
	// the display
	
	public Display getDisplay() {
		return this.display;
	}
	
	public void display(final TextStore data) {
		this.slideView.transitionPlaceholders(data.copy());
		
		this.stage.toFront();
	}
	
	public void display(final Slide slide) {
		if (slide == null) {
			this.slideView.transitionSlide(null);
			return;
		}
		
		Slide copy = slide.copy();

		double w = this.display.getWidth();
		double h = this.display.getHeight();
		
		copy.fit(w, h);
		
		this.slideView.transitionSlide(copy);
		
		this.stage.toFront();
	}
	
	public void release() {
		this.slideView.dispose();
		
		this.container.getChildren().clear();
		
		this.stage.setOnHiding(null);
		this.stage.setOnCloseRequest(null);
		this.stage.close();
	}
	
	public Slide getSlide() {
		return this.slideView.getSlide();
	}
	
	public ReadOnlyObjectProperty<Slide> slideProperty() {
		return this.slideView.slideProperty();
	}
}
