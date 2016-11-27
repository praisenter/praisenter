package org.praisenter.javafx.slide;

import java.util.Iterator;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;
import org.praisenter.utility.ClasspathLoader;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SlidePreviewPane {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/resources/transparent.png");

	private final PraisenterContext context;
	
	private final ObjectProperty<ObservableSlide<Slide>> slide = new SimpleObjectProperty<ObservableSlide<Slide>>();

	private final StackPane slidePreview;
	
	public SlidePreviewPane(PraisenterContext context) {
		this.context = context;
		
		final int padding = 0;
		
		slidePreview = new StackPane();
//		slidePreview.setPrefSize(500, 400);
		slidePreview.setPadding(new Insets(padding));
		slidePreview.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		
		// clip by the slide Preview area
		Rectangle clipRect = new Rectangle(slidePreview.getWidth(), slidePreview.getHeight());
		clipRect.heightProperty().bind(slidePreview.heightProperty());
		clipRect.widthProperty().bind(slidePreview.widthProperty());
		slidePreview.setClip(clipRect);
		
		StackPane slideBounds = new StackPane();
		slideBounds.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		
		DropShadow sdw = new DropShadow();
		sdw.setRadius(5);
		sdw.setColor(Color.rgb(0, 0, 0, 0.3));
		slideBounds.setEffect(sdw);
		
		// we resize and position canvasBack based on the target width/height 
		// and the available width height using a uniform scale factor
		DoubleBinding widthSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty());
			}
			@Override
			protected double computeValue() {
				ObservableSlide<?> s = slide.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = slidePreview.getWidth() - padding * 2;
					double th = slidePreview.getHeight() - padding * 2;
					return Math.floor(Fx.getUniformlyScaledBounds(w, h, tw, th).getWidth());
				}
				return 0;
			}
		};
		DoubleBinding heightSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty());
			}
			@Override
			protected double computeValue() {
				ObservableSlide<?> s = slide.get();
				if (s != null) {
					double w = s.getWidth();
					double h = s.getHeight();
					double tw = slidePreview.getWidth() - padding * 2;
					double th = slidePreview.getHeight() - padding * 2;
					return Math.floor(Fx.getUniformlyScaledBounds(w, h, tw, th).getHeight());
				}
				return 0;
			}
		};
		slideBounds.maxWidthProperty().bind(widthSizing);
		slideBounds.maxHeightProperty().bind(heightSizing);
		
		Pane slideCanvas = new Pane();
		slideCanvas.setMinSize(0, 0);
		slideCanvas.setSnapToPixel(true);
		slidePreview.setSnapToPixel(true);
		
		ObjectBinding<Scaling> scaleFactor = new ObjectBinding<Scaling>() {
			{
				bind(slidePreview.widthProperty(), slidePreview.heightProperty());
			}
			@Override
			protected Scaling computeValue() {
				double tw = slidePreview.getWidth() - padding * 2;
				double th = slidePreview.getHeight() - padding * 2;
				
				ObservableSlide<?> s = slide.get();
				if (s == null) {
					return new Scaling(1, 0, 0);
				}
				
				double w = s.getWidth();
				double h = s.getHeight();
				// if so, lets get the scale factors
				double sw = tw / w;
				double sh = th / h;
				// if we want to scale uniformly we need to choose
				// the smallest scale factor
				double scale = sw < sh ? sw : sh;
				
				// to scale uniformly we need to 
				// scale by the smallest factor
				if (sw < sh) {
					w = tw;
					h = (int)Math.ceil(sw * h);
				} else {
					w = (int)Math.ceil(sh * w);
					h = th;
				}

				// center the image
				double x = (tw - w) / 2.0;
				double y = (th - h) / 2.0;
				
				return new Scaling(scale, x, y);
			}
		};
		
		slidePreview.getChildren().addAll(slideBounds, slideCanvas);
		StackPane.setAlignment(slideBounds, Pos.CENTER);
		
		// setup of the editor when the slide being edited changes
		slide.addListener((obs, ov, nv) -> {
			slideCanvas.getChildren().clear();
			
			if (ov != null) {
				ov.scalingProperty().unbind();
				Iterator<ObservableSlideComponent<?>> components = ov.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					osr.scalingProperty().unbind();
				}
			}
			
			if (nv != null) {
				StackPane rootPane = nv.getDisplayPane();
				slideCanvas.getChildren().add(rootPane);
				nv.scalingProperty().bind(scaleFactor);
				
				Iterator<ObservableSlideComponent<?>> components = nv.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					osr.scalingProperty().bind(scaleFactor);
				}
			}
		});
	}
}
