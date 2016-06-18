package org.praisenter.javafx.slide.editor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.Scaling;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.resources.OpenIconic;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
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

// FEATURE grouping of components

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
public final class SlideEditorPane extends Application {
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
	
	
	Slide slide;
	ObservableSlide<?> oSlide;
	
	ObjectProperty<Resolution> targetResolution = new SimpleObjectProperty<>();
	
	ObjectProperty<ObservableSlideComponent<?>> selected = new SimpleObjectProperty<ObservableSlideComponent<?>>();

	StackPane slidePreview;
	
	// TODO move this out of its own javafx app
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	// TODO translate
	@Override
	public void start(Stage stage) throws Exception {
		Font.getFamilies();
		Font.getFontNames();
		
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/resources/open-iconic.ttf")));
		
		Path path = Paths.get("D:\\Personal\\Praisenter\\testmedialibrary");
//    	Path path = Paths.get("C:\\Users\\William\\Desktop\\test\\media");
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				100, 100,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, new JavaFXMediaImportFilter(path), settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PraisenterContext context = new PraisenterContext(this, stage, null, null, library, null, null, null);
		
		slide = createTestSlide();
		oSlide = new ObservableSlide<Slide>(slide, context, SlideMode.EDIT);
		targetResolution.set(new Resolution(slide.getWidth(), slide.getHeight()));
		
		EventHandler<MouseEvent> entered = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Border newBorder = new Border(BORDER_STROKE_1, BORDER_STROKE_2);
				Region region = (Region)e.getSource();
				region.setBorder(newBorder);
			}
		};
		EventHandler<MouseEvent> exited = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Region region = (Region)e.getSource();
				if (selected.get() == null || selected.get().getEditPane() != region) {
					region.setBorder(null);
				}
				stage.getScene().setCursor(Cursor.DEFAULT);
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
				stage.getScene().setCursor(cursor);
			}
		};
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Slide Preview Node hierarchy
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// BorderPane											This node ensures the size of the wrapper node is resized
		//	- Pane (slidePreview)								This node is the wrapper for the transparency background and slide node
		//		- StackPane (slideBounds)						This node shows the tiled transparency image.
		//		- Pane (slideCanvas)							?
		//			- StackPane Editable Node					This node is for mouse selection and editing capability.
		//				- Pane Container						This node applies the scaling transformation
		//					- Hierarchy of component nodes		All the nodes for the component
		
		
		slidePreview = new StackPane();
		slidePreview.setPrefSize(500, 400);
//		slidePreview.setBorder(Fx.newBorder(Color.ORANGE));
		
		StackPane slideBounds = new StackPane();
//		slideBounds.setBorder(Fx.newBorder(Color.RED));
		slideBounds.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		
		// we resize and position canvasBack based on the target width/height 
		// and the available width height using a uniform scale factor
		DoubleBinding widthSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty(),
					 targetResolution);
			}
			@Override
			protected double computeValue() {
				Resolution r = targetResolution.get();
				double tw = slidePreview.getWidth();
				double th = slidePreview.getHeight();
				return getUniformlyScaledBounds(r.getWidth(), r.getHeight(), tw, th).getWidth();
			}
		};
		DoubleBinding heightSizing = new DoubleBinding() {
			{
				bind(slidePreview.widthProperty(), 
					 slidePreview.heightProperty(),
					 targetResolution);
			}
			@Override
			protected double computeValue() {
				Resolution r = targetResolution.get();
				double tw = slidePreview.getWidth();
				double th = slidePreview.getHeight();
				return getUniformlyScaledBounds(r.getWidth(), r.getHeight(), tw, th).getHeight();
			}
		};
		slideBounds.maxWidthProperty().bind(widthSizing);
		slideBounds.maxHeightProperty().bind(heightSizing);
		
		Pane slideCanvas = new Pane();
		slideCanvas.setMinSize(0, 0);
//		slideCanvas.setBorder(Fx.newBorder(Color.YELLOW));
		
		ObjectBinding<Scaling> scaleFactor = new ObjectBinding<Scaling>() {
			{
				bind(slidePreview.widthProperty(), slidePreview.heightProperty());
			}
			@Override
			protected Scaling computeValue() {
				double tw = slidePreview.getWidth();
				double th = slidePreview.getHeight();
				double w = slide.getWidth();
				double h = slide.getHeight();
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
		
		Pane rootEditPane = oSlide.getEditPane();
		slideCanvas.getChildren().add(rootEditPane);
		oSlide.scalingProperty().bind(scaleFactor);
		rootEditPane.setOnMouseEntered(entered);
		rootEditPane.setOnMouseExited(exited);
		// TODO dragging and resizing for the slide itself (when its a notification slide)
		// FIXME note, this will need to be done when we add components
		for (ObservableSlideComponent<?> osr : oSlide.getObservableComponents()) {
			SlideRegionDraggedEventHandler dragHandler = new SlideRegionDraggedEventHandler(osr);
			Pane pane = osr.getEditPane();
			slideCanvas.getChildren().add(pane);
			osr.scalingProperty().bind(scaleFactor);
			pane.setOnMouseEntered(entered);
			pane.setOnMouseExited(exited);
//			pane.setOnMousePressed(dragHandler);
			pane.setOnMouseReleased(dragHandler);
			pane.setOnMouseDragged(dragHandler);
			pane.setOnMouseMoved(hover);
			pane.addEventHandler(MouseEvent.MOUSE_PRESSED, dragHandler);
			pane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
				ObservableSlideComponent<?> temp = selected.get();
				if (temp != osr && temp != null) {
					temp.getEditPane().setBorder(null);
				}
				selected.set(osr);
			});
		}
		
		slidePreview.getChildren().addAll(slideBounds, slideCanvas);
		StackPane.setAlignment(slideBounds, Pos.CENTER);
		
		// properties pane
		VBox propertiesPane = new VBox();
		
		// slide properties
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
//			grid.setGridLinesVisible(true);
			
			// slide name
			Label lblName = new Label("Name");
			TextField txtName = new TextField();
			txtName.setPromptText("Name");
			grid.add(lblName, 0, 0);
			grid.add(txtName, 1, 0);
			
			Label lblTime = new Label("Time");
			TextField txtTime = new TextField();
			txtTime.setPromptText("00:00");
			grid.add(lblTime, 0, 1);
			grid.add(txtTime, 1, 1);
			
			// target resolution
			// TODO replace with PraisenterContext configuration
			Configuration conf = Configuration.createDefaultConfiguration();
			Label lblResolution = new Label("Target");
			ComboBox<Resolution> cmbResolutions = new ComboBox<Resolution>(conf.resolutionsProperty());
			cmbResolutions.valueProperty().bindBidirectional(targetResolution);
			cmbResolutions.valueProperty().addListener((obs, ov, nv) -> {
				// when this changes we need to adjust all the sizes of the controls in the slide
				oSlide.fit(nv.getWidth(), nv.getHeight());
				// then we need to update all the Java FX nodes
				scaleFactor.invalidate();
			});
			Button btnNewResolution = new Button("Add");
			HBox tRes = new HBox();
			tRes.setSpacing(2);
			tRes.getChildren().addAll(cmbResolutions, btnNewResolution);
			grid.add(lblResolution, 0, 2);
			grid.add(tRes, 1, 2);
			
			TagListView lstTags = new TagListView(FXCollections.observableSet());
			grid.add(lstTags, 0, 3, 2, 1);
			
			TitledPane ttlSlide = new TitledPane("Slide Properties", grid);
			propertiesPane.getChildren().add(ttlSlide);
		}
		
		// slide component editing
		{
			SlideComponentEditor se = new SlideComponentEditor(context);
			se.component.bind(selected);
			
			se.addEventHandler(SlideComponentEvent.ORDER, (e) -> {
				ObservableSlideComponent<?> component = e.component;
				if (e.operation == SlideComponentOrderEvent.OPERATION_BACK) {
					// FIXME handle
				} else if (e.operation == SlideComponentOrderEvent.OPERATION_FRONT) {
					// FIXME handle					
				} else if (e.operation == SlideComponentOrderEvent.OPERATION_BACKWARD) {
					oSlide.moveComponentDown(component);
				} else if (e.operation == SlideComponentOrderEvent.OPERATION_FORWARD) {
					oSlide.moveComponentUp(component);
				}
				slideCanvas.getChildren().removeAll(oSlide.getEditPanes());
				slideCanvas.getChildren().addAll(oSlide.getEditPanes());
			});
			
			TitledPane ttlComponent = new TitledPane("Component Properties", se);
			propertiesPane.getChildren().add(ttlComponent);
		}
		
		BorderPane bdr = new BorderPane();
		bdr.setPadding(new Insets(20));
		bdr.setCenter(slidePreview);
		
		ScrollPane propertyScroller = new ScrollPane(propertiesPane);
		propertyScroller.setFitToWidth(true);
		
		SplitPane split = new SplitPane();
		split.getItems().addAll(bdr, propertyScroller);
		split.setDividerPositions(0.8);
		SplitPane.setResizableWithParent(propertyScroller, false);
		
		Scene scene = new Scene(split);
		stage.setScene(scene);
		stage.show();
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
		
		return new Rectangle2D(x, y, w, h);
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
				new SlideStrokeStyle(SlideStrokeType.INSIDE, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
				5, 
				5);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1.5, 
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
		txt.setPadding(10);
		txt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		txt.setBorder(thick);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setLineSpacing(10);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		
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
		dt.setPadding(20);
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
		tp.setPadding(10);
		tp.setBackground(new SlideColor(0.5, 0, 0.5, 0.5));
		tp.setTextPaint(gradient);
		// FIXME text border really slows when the stroke style is INSIDE or OUTSIDE - may just want to not offer this option
		tp.setTextBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 0));
		tp.setLineSpacing(2);
		tp.setType(PlaceholderType.TITLE);
		//tp.setVariants(variants);
		
		slide.addComponent(txt);
		slide.addComponent(mc);
		slide.addComponent(dt);
		slide.addComponent(tp);

//		slide.setBackground(vid);
		slide.setBackground(new SlideColor(0, 0, 1.0, 0.5));
		//slide.setBorder(thick);
		
		
		return slide;
	}
}
