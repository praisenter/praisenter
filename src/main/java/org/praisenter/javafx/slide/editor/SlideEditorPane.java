package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Collectors;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.Scaling;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utility.ClasspathLoader;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

// FEATURE grouping of components
// JAVABUG 06/30/16 text border really slows when the stroke style is INSIDE or OUTSIDE - may just want to not offer this option

// TODO slide presentation:
//		user selects slide
//		if slide has placeholders
//			clear all placeholders
//		endif
//		if user is selecting a bible verse or song verse
//			set placeholders based on languages
//		endif
//		present thumbnail of slide

//		user presents slide
//		the transition+easing+duration is used to generate a ParallelTransition for the current slide and the incoming slide
//		the new slide is added to the scene graph
//		the transition is played
public final class SlideEditorPane extends BorderPane {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/resources/transparent.png");
	
	/** The component hover line width */
	private static final double LINE_WIDTH = 1.0;
	
	/** The component hover dash length */
	private static final double DASH_LENGTH = 2.0f;
	
	/** The space between the dashes */
	private static final double DASH_SPACE_LENGTH = DASH_LENGTH * 2; 
	
	// we use two borders each with a different color to allow it to
	// be visible regardless of the color of the background of the component
	
	/** The component hover border color 1 */
	private static final Color BORDER_COLOR_1 = Color.BLACK;
	
	/** The component hover border color 2 */
	private static final Color BORDER_COLOR_2 = Color.WHITE;
	
	/** The size of the resize prongs (should be an odd number to center on corners and sides well) */
	static final int RESIZE_PRONG_SIZE = 9;
	
	// for selection and grid
	// TODO convert to CSS
	/** The border 1 stroke */
	static final BorderStroke BORDER_STROKE_1 = new BorderStroke(BORDER_COLOR_1, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, 0.0, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
	
	/** The border 2 stroke */
	static final BorderStroke BORDER_STROKE_2 = new BorderStroke(BORDER_COLOR_2, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, DASH_LENGTH, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
	
	
	private final PraisenterContext context;
	
	private Resolution resolution = null;
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>();
	private final ObjectProperty<ObservableSlideRegion<?>> selected = new SimpleObjectProperty<ObservableSlideRegion<?>>();

	private final StackPane slidePreview;
	
	public SlideEditorPane(PraisenterContext context) {
		
		this.context = context;
		
		EventHandler<MouseEvent> entered = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Border newBorder = new Border(BORDER_STROKE_1, BORDER_STROKE_2);
				Region region = (Region)e.getSource();
				region.setBorder(newBorder);
				e.consume();
			}
		};
		EventHandler<MouseEvent> exited = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Region region = (Region)e.getSource();
				if (selected.get() == null || selected.get().getDisplayPane() != region) {
					region.setBorder(null);
				}
				getScene().setCursor(Cursor.DEFAULT);
			}
		};
		EventHandler<MouseEvent> hover = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Region region = (Region)e.getSource();
				double x = e.getX();
				double y = e.getY();
				double w = region.getWidth();
				double h = region.getHeight();
				
				Cursor cursor = CursorPosition.getCursorForPosition(x, y, w, h);
				getScene().setCursor(cursor);
				e.consume();
			}
		};
		
		// create the ribbon
		
		SlideEditorRibbon ribbon = new SlideEditorRibbon(context);
		VBox top = new VBox(ribbon);
		top.setBorder(new Border(new BorderStroke(null, null, Color.GRAY, null, null, null, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, null, new BorderWidths(0, 0, 1, 0), null)));
		this.setTop(top);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Slide Preview Node hierarchy
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// BorderPane											This node ensures the size of slidePreview is maximized
		//	- Pane (slidePreview)								This node is the wrapper for the transparency background and slide node
		//		- StackPane (slideBounds)						This node shows the (non-scaled) tiled transparency image.
		//		- Pane (slideCanvas)							Container for slide (allows scaling to work properly)
		//			- StackPane (slide editable node)			This observable slide's node
		//				- Pane (componentCanvas)				This node houses all the component nodes and is used to offset the x/y positions of the components by the slide's x/y position
		//					- Component 1
		//					- Component 2
		//					- Component 3
		//					...
		
		final double padding = 10;
		
		slidePreview = new StackPane();
		slidePreview.setPrefSize(500, 400);
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
					return getUniformlyScaledBounds(w, h, tw, th).getWidth();
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
					return getUniformlyScaledBounds(w, h, tw, th).getHeight();
				}
				return 0;
			}
		};
		slideBounds.maxWidthProperty().bind(widthSizing);
		slideBounds.maxHeightProperty().bind(heightSizing);
		
		Pane slideCanvas = new Pane();
		slideCanvas.setMinSize(0, 0);
		
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
		
		slide.addListener((obs, ov, nv) -> {
			slideCanvas.getChildren().clear();
			if (ov != null) {
				ov.scalingProperty().unbind();
				Iterator<ObservableSlideComponent<?>> components = nv.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					osr.scalingProperty().unbind();
				}
			}
			
			if (nv != null) {
				resolution = new Resolution(nv.getWidth(), nv.getHeight());
				StackPane rootEditPane = nv.getDisplayPane();
				slideCanvas.getChildren().add(rootEditPane);
				nv.scalingProperty().bind(scaleFactor);
				rootEditPane.setOnMouseEntered(entered);
				rootEditPane.setOnMouseExited(exited);
				rootEditPane.setOnMouseMoved(hover);
				SlideRegionDraggedEventHandler slideDragHandler = new SlideRegionDraggedEventHandler(nv);
				rootEditPane.setOnMouseReleased(slideDragHandler);
				rootEditPane.setOnMouseDragged(slideDragHandler);
				rootEditPane.addEventHandler(MouseEvent.MOUSE_PRESSED, slideDragHandler);
				rootEditPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
					ObservableSlideRegion<?> temp = selected.get();
					if (temp != slide.get() && temp != null) {
						temp.getDisplayPane().setBorder(null);
					}
					selected.set(nv);
					e.consume();
				});
				
				// FIXME note, this will need to be done when we add components
				Iterator<ObservableSlideComponent<?>> components = nv.componentIterator();
				while (components.hasNext()) {
					ObservableSlideComponent<?> osr = components.next();
					SlideRegionDraggedEventHandler dragHandler = new SlideRegionDraggedEventHandler(osr);
					StackPane pane = osr.getDisplayPane();
					osr.scalingProperty().bind(scaleFactor);
					pane.setOnMouseEntered(entered);
					pane.setOnMouseExited(exited);
					pane.setOnMouseReleased(dragHandler);
					pane.setOnMouseDragged(dragHandler);
					pane.setOnMouseMoved(hover);
					pane.addEventHandler(MouseEvent.MOUSE_PRESSED, dragHandler);
					pane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
						ObservableSlideRegion<?> temp = selected.get();
						if (temp != osr && temp != null) {
							temp.getDisplayPane().setBorder(null);
						}
						selected.set(osr);
						e.consume();
					});
				}
			}
		});
		
		// for testing
//		slidePreview.setBorder(Fx.newBorder(Color.ORANGE));
//		slideBounds.setBorder(Fx.newBorder(Color.RED));
//		slideCanvas.setBorder(Fx.newBorder(Color.YELLOW));
		
		// events
		
		ribbon.slideProperty().bind(slide);
		ribbon.componentProperty().bind(selected);
		
		ribbon.addEventHandler(SlideEditorEvent.ORDER, (e) -> {
			ObservableSlideComponent<?> component = e.component;
			if (e.operation == SlideComponentOrderEvent.OPERATION_BACK) {
				slide.get().moveComponentBack(component);
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_FRONT) {
				slide.get().moveComponentFront(component);			
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_BACKWARD) {
				slide.get().moveComponentDown(component);
			} else if (e.operation == SlideComponentOrderEvent.OPERATION_FORWARD) {
				slide.get().moveComponentUp(component);
			}
		});
		
		ribbon.addEventHandler(SlideEditorEvent.TARGET_RESOLUTION, (e) -> {
			resolution = e.resolution;
			scaleFactor.invalidate();
			widthSizing.invalidate();
			heightSizing.invalidate();
		});

		ribbon.addEventHandler(SlideEditorEvent.ADD_COMPONENT, (e) -> {
			ObservableSlideComponent<?> component = e.getComponent();
			slide.get().addComponent(component);
			
			SlideRegionDraggedEventHandler dragHandler = new SlideRegionDraggedEventHandler(component);
			StackPane pane = component.getDisplayPane();
			component.scalingProperty().bind(scaleFactor);
			pane.setOnMouseEntered(entered);
			pane.setOnMouseExited(exited);
			pane.setOnMouseReleased(dragHandler);
			pane.setOnMouseDragged(dragHandler);
			pane.setOnMouseMoved(hover);
			pane.addEventHandler(MouseEvent.MOUSE_PRESSED, dragHandler);
			pane.addEventHandler(MouseEvent.MOUSE_PRESSED, (me) -> {
				ObservableSlideRegion<?> temp = selected.get();
				if (temp != component && temp != null) {
					temp.getDisplayPane().setBorder(null);
				}
				selected.set(component);
				me.consume();
			});
		});
		
		// set values
			
		Slide s = createTestSlide();
		slide.set(new ObservableSlide<Slide>(s, context, SlideMode.EDIT));
			
		this.setCenter(slidePreview);
	}
	
	private static final Rectangle2D getUniformlyScaledBounds(double w, double h, double tw, double th) {
		// compute the scale factors
		double sw = tw / w;
		double sh = th / h;

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
		
		return new Rectangle2D(x, y, Math.max(w, 0), Math.max(h, 0));
	}
	
	private static final Slide createTestSlide() {
		BasicSlide slide = new BasicSlide();
		slide.setWidth(800);
		slide.setHeight(600);
		
		SlideColor color = new SlideColor(0, 0, 0.8, 0.7);
		
		SlideLinearGradient gradient = new SlideLinearGradient(
				0, 0, 1, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 1, 0, 1)),
				new SlideGradientStop(1, new SlideColor(0, 0, 1, 1)));
		
		SlideStroke stroke = new SlideStroke(
				gradient, 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
				1, 
				0);
		
		SlideStroke thick = new SlideStroke(
				new SlideColor(0.5, 0, 0, 1), 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, DashPattern.DASH.getDashes()), 
				5, 
				5);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 0.707, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 0, 0, 0.8)),
				new SlideGradientStop(1, new SlideColor(0, 1, 1, 0.8)));
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFont(new SlideFont("Impact", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
		txt.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		txt.setWidth(400);
		txt.setHeight(400);
		txt.setX(20);
		txt.setY(100);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		txt.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		txt.setPadding(new SlidePadding(10));
		txt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		txt.setBorder(thick);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setLineSpacing(10);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		txt.setShadow(new SlideShadow(ShadowType.OUTER, new SlideColor(0, 0, 0, 1), 0, 0, 10, 0.0));
		
		MediaObject img = new MediaObject(
				UUID.fromString("f6668fb0-3a40-4590-99a4-1ba474315dca"),
//				UUID.fromString("245d1e2a-9b82-431d-8dd9-bac0ed0a7aca"),
				ScaleType.UNIFORM,
				false,
				true);
		txt.setBackground(img);
		
		MediaObject vid = new MediaObject(
				UUID.fromString("a5d7dab1-8c59-4103-87cf-a13db23152f3"),
//				UUID.fromString("abe57410-81b9-4226-a15f-95f0bedcea89"),
				ScaleType.NONUNIFORM,
				false,
				true);
		
		MediaComponent mc = new MediaComponent();
		mc.setBackground(img);
		mc.setBorder(thick);
		mc.setWidth(100);
		mc.setHeight(100);
		mc.setX(100);
		mc.setY(200);
		mc.setMedia(vid);
		
		DateTimeComponent dt = new DateTimeComponent();
		dt.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
		dt.setFontScaleType(FontScaleType.NONE);
		dt.setWidth(600);
		dt.setHeight(200);
		dt.setX(0);
		dt.setY(0);
		dt.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		dt.setVerticalTextAlignment(VerticalTextAlignment.TOP);
		dt.setPadding(new SlidePadding(20));
		dt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		dt.setTextPaint(new SlideColor(1.0, 0, 0, 1));
		dt.setFormat(new SimpleDateFormat("M/d/yyyy h:mm a z"));
		
		TextPlaceholderComponent tp = new TextPlaceholderComponent();
		tp.setFont(new SlideFont("Verdana", SlideFontWeight.NORMAL, SlideFontPosture.ITALIC, 5));
		tp.setFontScaleType(FontScaleType.BEST_FIT);
		tp.setWidth(200);
		tp.setHeight(400);
		tp.setX(200);
		tp.setY(0);
		tp.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		tp.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
		tp.setPadding(new SlidePadding(10));
		//tp.setBackground(new SlideColor(0.5, 0, 0.5, 0.5));
		tp.setTextPaint(gradient);
		tp.setTextBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 0));
		tp.setLineSpacing(2);
		tp.setType(PlaceholderType.TITLE);
		//tp.setVariants(variants);
		
		CountdownComponent cd = new CountdownComponent();
		cd.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 100));
		cd.setFontScaleType(FontScaleType.BEST_FIT);
		cd.setWidth(400);
		cd.setHeight(100);
		cd.setX(200);
		cd.setY(0);
		cd.setTextWrapping(false);
		cd.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		cd.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		cd.setPadding(new SlidePadding(10));
		//tp.setBackground(new SlideColor(0.5, 0, 0.5, 0.5));
		cd.setTextPaint(gradient);
		cd.setTextBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, new Double[] { 10.0, 10.0, 5.0 }), 1, 0));
//		cd.setLineSpacing(2);
		cd.setTarget(LocalDateTime.now().plusYears(1).plusMonths(2).plusDays(3).plusHours(4).plusMinutes(5).plusSeconds(6));
		//tp.setVariants(variants);
		
		slide.addComponent(txt);
		slide.addComponent(mc);
		slide.addComponent(dt);
		slide.addComponent(tp);
		slide.addComponent(cd);

//		slide.setBackground(vid);
		slide.setBackground(new SlideColor(0, 0, 1.0, 0.5));
		//slide.setBorder(thick);
		
		
		return slide;
	}
}
