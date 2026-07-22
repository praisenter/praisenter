package org.praisenter.ui.display;

import java.util.ArrayList;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public final class ScreenDisplayTarget extends Stage implements DisplayTarget {
	@SuppressWarnings("unused")
	private final GlobalContext context;
	private final DisplayConfiguration configuration;
	
	private final Pane container;
	
	private final SlideView slideView;
	private final SlideView notificationView;
	
	private final ChangeListener<? super Boolean> activeListener;
	
	public ScreenDisplayTarget(GlobalContext context, DisplayConfiguration configuration) {
		super(StageStyle.TRANSPARENT);
		
		this.context = context;
		this.configuration = configuration;

    	// icons
		WindowHelper.setIcons(this);
    	
		this.initModality(Modality.NONE);
		this.setResizable(false);
		
		// prevent the user from closing or hiding the window
		// JAVABUG (M) 06/30/16 [workaround] At some point we need to come up with a way for the sub windows to not be seen; JavaFX does not have a facility for this at this time https://bugs.openjdk.java.net/browse/JDK-8091566
		// JAVABUG (L) 10/31/16 [workaround] Modal dialog brings wrong stage to front when closed https://bugs.openjdk.java.net/browse/JDK-8159226
		// for now we have to create these with no owner because once another window/stage owns these
		// focusing the owner also brings these windows to the foreground...
		EventHandler<WindowEvent> block = (WindowEvent e) -> {
			e.consume();
		};
		this.setOnCloseRequest(block);
		this.setOnHiding(block);
		
		this.container = new StackPane();
		this.container.setBackground(null);
		
		this.setScene(new Scene(this.container, Color.TRANSPARENT));
		
		// setup debug mode notification
		if (context.getWorkspaceConfiguration().isDebugModeEnabled()) {
			this.container.setBorder(new Border(new BorderStroke(
					Color.RED, 
					new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, new ArrayList<Double>()), 
					null, 
					new BorderWidths(10))));
		}
		
		this.slideView = new SlideView(context);
		this.slideView.setClipEnabled(false);
		this.slideView.setFitToHeightEnabled(false);
		this.slideView.setFitToWidthEnabled(false);
		this.slideView.setCheckeredBackgroundEnabled(false);
		this.slideView.setViewMode(SlideMode.PRESENT);
		
		this.notificationView = new SlideView(context);
		this.notificationView.setClipEnabled(false);
		this.notificationView.setFitToHeightEnabled(false);
		this.notificationView.setFitToWidthEnabled(false);
		this.notificationView.setCheckeredBackgroundEnabled(false);
		this.notificationView.setViewMode(SlideMode.PRESENT);
		this.notificationView.setAutoHideEnabled(true);
		
		this.container.getChildren().addAll(this.slideView, this.notificationView);
		
		// cache hints
		this.container.setCache(true);
		this.container.setCacheHint(CacheHint.SPEED);
		
		// events
		
		// JAVABUG (H) 07/20/26 [workaround] output scale is not properly accounted for
		// When we create a Stage, it defaults to the primary screen. If that screen has an output scaling
		// other than 1.0, then the new Stage will inherit that scaling even after being moved to a screen
		// with a different output scaling
		// https://bugs.openjdk.org/browse/JDK-8251912
		// https://bugs.openjdk.org/browse/JDK-8281058
		
		// OUTPUTSCALE-WORKAROUND
		// To work around the issue, we set the (x,y) location of the Stage so that it gets the correct 
		// output scale settings for the screen it's on. Then, when we show the stage, we set the width
		// to something arbitrary, then set the width to the real value.
		this.setX(configuration.getX());
		this.setY(configuration.getY());
		
		this.titleProperty().bind(configuration.labelProperty());
		
		if (configuration.isActive()) {
			// OUTPUTSCALE-WORKAROUND
			// To ensure the width/height actually change, we need to change
			// the width to something else, then back to the value we want
			this.setWidth(100);
			this.setHeight(100);
			this.setMinWidth(100);
			this.setMinHeight(100);
			this.setMaxWidth(100);
			this.setMaxHeight(100);
			
			this.show();
			
			// OUTPUTSCALE-WORKAROUND
			this.setWidth(configuration.getWidth());
			this.setHeight(configuration.getHeight());
			this.setMinWidth(configuration.getWidth());
			this.setMinHeight(configuration.getHeight());
			this.setMaxWidth(configuration.getWidth());
			this.setMaxHeight(configuration.getHeight());
		}
		
		this.activeListener = (obs, ov, nv) -> {
			if (!nv) {
				// clear the content
				this.slideView.render(null, null, false);
				this.notificationView.render(null, null, false);
				// hide the window
				this.hide();
			} else if (!this.isShowing()) {
				// OUTPUTSCALE-WORKAROUND
				// To ensure the width/height actually change, we need to change
				// the width to something else, then back to the value we want
				this.setWidth(100);
				this.setHeight(100);
				this.setMinWidth(100);
				this.setMinHeight(100);
				this.setMaxWidth(100);
				this.setMaxHeight(100);
				
				this.show();
				
				// OUTPUTSCALE-WORKAROUND
				this.setWidth(configuration.getWidth());
				this.setHeight(configuration.getHeight());
				this.setMinWidth(configuration.getWidth());
				this.setMinHeight(configuration.getHeight());
				this.setMaxWidth(configuration.getWidth());
				this.setMaxHeight(configuration.getHeight());
			}
		};
		configuration.activeProperty().addListener(this.activeListener);
		
	}
	
	@Override
	public String toString() {
		return this.configuration.getLabel();
	}
	
	public void dispose() {
		this.configuration.activeProperty().removeListener(this.activeListener);
		
		this.slideView.dispose();
		
		this.container.getChildren().clear();
		
		this.setOnHiding(null);
		this.setOnCloseRequest(null);
		this.close();
	}
	
	@Override
	public void displaySlide(Slide slide, TextStore data) {
		this.displaySlide(slide, data, true);
	}

	public void displaySlide(final Slide slide, final TextStore data, boolean transtion) {
		if (slide == null) {
			this.slideView.render(null, null, transtion);
			return;
		}
		
		Slide copy = slide.copy();

		if (data != null) {
			copy.setPlaceholderData(data.copy());
		}
		
		double w = this.configuration.getWidth();
		double h = this.configuration.getHeight();
		
		copy.fit(w, h);
		
		this.slideView.render(copy, copy.getPlaceholderData(), transtion);
		
		this.toFront();
	}
	
	@Override
	public void displayNotification(Slide slide, TextStore data) {
		this.displayNotification(slide, data, true);
	}

	public void displayNotification(final Slide slide, final TextStore data, boolean transtion) {
		if (slide == null) {
			this.notificationView.render(null, null, transtion);
			return;
		}
		
		Slide copy = slide.copy();

		if (data != null) {
			copy.setPlaceholderData(data.copy());
		}
		
		double w = this.configuration.getWidth();
		double h = this.configuration.getHeight();
		
		copy.fit(w, h);

		this.notificationView.render(copy, copy.getPlaceholderData(), transtion);
		
		this.toFront();
	}
	
	@Override
	public void clear() {
		this.clear(true);
	}
	
	public void clear(boolean transition) {
		this.slideView.render(null, null, transition);
		this.notificationView.render(null, null, transition);
	}
	
	public DisplayConfiguration getDisplayConfiguration() {
		return this.configuration;
	}
	
//	public Slide getSlide() {
//		return this.slideView.getSlide();
//	}
//	
//	public ReadOnlyObjectProperty<Slide> slideProperty() {
//		return this.slideView.slideProperty();
//	}
//	
//	public Slide getNotificationSlide() {
//		return this.notificationView.getSlide();
//	}
//	
//	public ReadOnlyObjectProperty<Slide> notificationSlideProperty() {
//		return this.notificationView.slideProperty();
//	}
}
