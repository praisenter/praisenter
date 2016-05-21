package org.praisenter.javafx.slide;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.dialog.FontSelectorDialog;
import org.praisenter.javafx.FontPicker;
import org.praisenter.javafx.GradientPicker;
import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.Resolution;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.animation.AnimationPane;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.media.MediaType;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.graphics.Rectangle;
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
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utility.ClasspathLoader;

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
	
//	/** The component hover line width */
//	private static final double LINE_WIDTH = 1.0;
//	
//	/** The component hover dash length */
//	private static final double DASH_LENGTH = 1.0f;
//	
//	/** The space between the dashes */
//	private static final double DASH_SPACE_LENGTH = DASH_LENGTH * 3.0f; 
//	
//	// we use two borders each with a different color to allow it to
//	// be visible regardless of the color of the background of the component
//	
//	/** The component hover border color 1 */
//	private static final Color BORDER_COLOR_1 = Color.BLACK;
//	
//	/** The component hover border color 2 */
//	private static final Color BORDER_COLOR_2 = Color.WHITE;
//	
//	/** The size of the resize prongs (should be an odd number to center on corners and sides well) */
//	static final int RESIZE_PRONG_SIZE = 9;
//	
//	// for selection and grid
//	
//	/** The border 1 stroke */
//	static final BorderStroke BORDER_STROKE_1 = new BorderStroke(BORDER_COLOR_1, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, 0.0, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
//	
//	/** The border 2 stroke */
//	static final BorderStroke BORDER_STROKE_2 = new BorderStroke(BORDER_COLOR_2, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, DASH_LENGTH * 2.0, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
	
	
	Slide slide;
//	FxSlide fxSlide;
	
	ObjectProperty<Resolution> targetResolution = new SimpleObjectProperty<>();
	
	// TODO move this out of its own javafx app
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	// TODO translate
	@Override
	public void start(Stage stage) throws Exception {
		
//		Path path = Paths.get("D:\\Personal\\Praisenter\\testmedialibrary");
    	Path path = Paths.get("C:\\Users\\William\\Desktop\\test\\media");
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
		
		Slide slide = createTestSlide();
		PraisenterContext context = new PraisenterContext(this, stage, null, null, library, null, null, null);
//		FxSlide fxSlide = new FxSlide(context, slide, SlideMode.EDIT);
		ObservableSlide<Slide> oSlide = new ObservableSlide<Slide>(slide, context, SlideMode.EDIT);
		
		targetResolution.set(new Resolution(slide.getWidth(), slide.getHeight()));
		
		// Slide Preview Node hierarchy
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// BorderPane							This node ensures the size of the wrapper node is resized.
		//	- Pane (slidePreview)				This node is the wrapper for the slide area.
		//		- StackPane (slideBounds)		This node is sized based on the available bounds and renders the transparency pattern.
		//			- StackPane (slideCanvas)	This node has the scaling transformation applied to scale the slide to the available bounds.
		//				- FxSlide nodes...
		
		
		Pane slidePreview = new Pane();
//		slidePreview.setBorder(FxFactory.newBorder(Color.ORANGE));
		
		// TODO see http://fxexperience.com/2014/05/resizable-grid-using-canvas/ for a grid example
		StackPane slideBounds = new StackPane();
//		slideBounds.setBorder(FxFactory.newBorder(Color.RED));
		slideBounds.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
//		slideBounds.setEffect(new DropShadow(10, Color.BLACK));
		
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
		DoubleBinding xPositioning = new DoubleBinding() {
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
				return getUniformlyScaledBounds(r.getWidth(), r.getHeight(), tw, th).getMinX();
			}
		};
		DoubleBinding yPositioning = new DoubleBinding() {
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
				return getUniformlyScaledBounds(r.getWidth(), r.getHeight(), tw, th).getMinY();
			}
		};
		slideBounds.prefWidthProperty().bind(widthSizing);
		slideBounds.prefHeightProperty().bind(heightSizing);
		slideBounds.layoutXProperty().bind(xPositioning);
		slideBounds.layoutYProperty().bind(yPositioning);
		
		StackPane slideCanvas = new StackPane();
		slideCanvas.setMinSize(0, 0);
//		slideCanvas.setBorder(FxFactory.newBorder(Color.BLUE));
//		slideCanvas.getChildren().addAll(fxSlide.getBackgroundNode(), fxSlide.getContentNode(), fxSlide.getBorderNode());
		slideCanvas.getChildren().add(oSlide.getSlideNode());
		slideCanvas.setPrefSize(500, 500);
		
		DoubleBinding scaleFactor = new DoubleBinding() {
			{
				bind(slideBounds.widthProperty(), slideBounds.heightProperty());
			}
			@Override
			protected double computeValue() {
				double tw = slideBounds.getWidth();
				double th = slideBounds.getHeight();
				// if so, lets get the scale factors
				double sw = Math.min(1.0, (double)tw / (double)slide.getWidth());
				double sh = Math.min(1.0, (double)th / (double)slide.getHeight());
				// if we want to scale uniformly we need to choose
				// the smallest scale factor
				if (sw < sh) {
					return sw;
				} else {
					return sh;
				}
			}
		};
		slideCanvas.scaleXProperty().bind(scaleFactor);
		slideCanvas.scaleYProperty().bind(scaleFactor);
		
		slideBounds.getChildren().add(slideCanvas);
		slidePreview.getChildren().add(slideBounds);
		
		// properties pane
		// TODO Scrolling
		// TODO hide/show based on other inputs
		// TODO slide component contents controls
		VBox propertiesPane = new VBox();
		
		// slide properties
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
	//		grid.setGridLinesVisible(true);
			
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
			
			Label lblTags = new Label("");
			TagListView lstTags = new TagListView(FXCollections.observableSet());
			grid.add(lblTags, 0, 2);
			grid.add(lstTags, 1, 2);
			
			TitledPane ttlSlide = new TitledPane("Slide Properties", grid);
			propertiesPane.getChildren().add(ttlSlide);
		}
		
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
	//		grid.setGridLinesVisible(true);
			
			// target (controls the screen size)
			
			Label lblResolution = new Label("Target");
			ComboBox<Resolution> cmbResolutions = new ComboBox<Resolution>(FXCollections.observableArrayList(Resolution.DEFAULT_RESOLUTIONS));
			cmbResolutions.valueProperty().bindBidirectional(targetResolution);
			cmbResolutions.valueProperty().addListener((obs, ov, nv) -> {
				// when this changes we need to adjust all the sizes of the controls in the slide
				slide.fit(nv.getWidth(), nv.getHeight());
				// then we need to update all the Java FX nodes
				
			});
			Button btnNewResolution = new Button("Add");
			HBox tRes = new HBox();
			tRes.setSpacing(2);
			tRes.getChildren().addAll(cmbResolutions, btnNewResolution);
			grid.add(lblResolution, 0, 0);
			grid.add(tRes, 1, 0);
			
			// size (controls the slide size)
			
			Label lblSize = new Label("Size");
			TextField txtWidth = new TextField();
			txtWidth.setPrefWidth(75);
			txtWidth.setPromptText("width");
			TextField txtHeight = new TextField();
			txtHeight.setPromptText("height");
			txtHeight.setPrefWidth(75);
			HBox wh = new HBox();
			wh.setSpacing(2);
			wh.setAlignment(Pos.BASELINE_LEFT);
			wh.getChildren().addAll(txtWidth, txtHeight);
			grid.add(lblSize, 0, 1);
			grid.add(wh, 1, 1);

			// position
			
			Label lblPosition = new Label("Position");
			TextField txtX = new TextField();
			txtX.setPrefWidth(75);
			txtX.setPromptText("x");
			TextField txtY = new TextField();
			txtY.setPrefWidth(75);
			txtY.setPromptText("y");
			grid.add(lblPosition, 0, 2);
			HBox xy = new HBox();
			xy.getChildren().addAll(txtX, txtY);
			xy.setSpacing(2);
			grid.add(xy, 1, 2);
			
			TitledPane ttlSlide = new TitledPane("Size & Position", grid);
			propertiesPane.getChildren().add(ttlSlide);
		}
		
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
	//		grid.setGridLinesVisible(true);
			
			// background
			SlidePaintPicker pkrBackground = new SlidePaintPicker(context, PaintType.COLOR, PaintType.GRADIENT);
			
			TitledPane ttlSlide = new TitledPane("Slide Background", pkrBackground);
			propertiesPane.getChildren().add(ttlSlide);
			
//			FontPicker pkrFont = new FontPicker(Font.font("Segoe UI Black", 30), FXCollections.observableArrayList(Font.getFamilies()));
//			propertiesPane.getChildren().add(pkrFont);
		}
		
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
	//		grid.setGridLinesVisible(true);
			
			
			
			TitledPane aniPane = new TitledPane("Transition & Animations", grid);
			propertiesPane.getChildren().add(aniPane);
		}
		
		BorderPane bdr = new BorderPane();
//		bdr.setBorder(FxFactory.newBorder(Color.BLACK));
		bdr.setPadding(new Insets(20));
		bdr.setCenter(slidePreview);
		
		ScrollPane propertyScroller = new ScrollPane(propertiesPane);
		propertyScroller.setFitToWidth(true);
		
		SplitPane split = new SplitPane();
		split.getItems().addAll(bdr, propertyScroller);
		split.setDividerPositions(0.55);
		SplitPane.setResizableWithParent(propertyScroller, false);
		
		Scene scene = new Scene(split);
//		scene.getStylesheets().add();
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
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
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
		txt.setOrder(0);
		txt.setPadding(10);
		txt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		txt.setBorder(thick);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		
		slide.addComponent(txt);
		
		MediaObject img = new MediaObject(
				UUID.fromString("912f0224-dfdd-4055-a471-32b7c371eb05"),
//				UUID.fromString("3df0f8ef-faa5-4d2c-820d-2d8dc55216b9"),
				ScaleType.UNIFORM,
				false,
				true);
		txt.setBackground(img);
		
		MediaObject vid = new MediaObject(
				UUID.fromString("e7e3b3c8-0c46-4507-b277-a18113078e75"),
//				UUID.fromString("00c9ac93-6923-4f4c-a3c2-6bcd9572b170"),
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
		
		slide.addComponent(mc);
		

		slide.setBackground(vid);
		slide.setBorder(thick);
		
		
		return slide;
	}
}
