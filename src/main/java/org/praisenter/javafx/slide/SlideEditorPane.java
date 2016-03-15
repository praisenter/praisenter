package org.praisenter.javafx.slide;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import org.controlsfx.dialog.FontSelectorDialog;
import org.praisenter.javafx.GradientPicker;
import org.praisenter.javafx.Resolution;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.media.MediaType;
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
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
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
	static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/javafx/resources/transparent.png");
	
	/** The component hover line width */
	private static final double LINE_WIDTH = 1.0;
	
	/** The component hover dash length */
	private static final double DASH_LENGTH = 1.0f;
	
	/** The space between the dashes */
	private static final double DASH_SPACE_LENGTH = DASH_LENGTH * 3.0f; 
	
	// we use two borders each with a different color to allow it to
	// be visible regardless of the color of the background of the component
	
	/** The component hover border color 1 */
	private static final Color BORDER_COLOR_1 = Color.BLACK;
	
	/** The component hover border color 2 */
	private static final Color BORDER_COLOR_2 = Color.WHITE;
	
	/** The size of the resize prongs (should be an odd number to center on corners and sides well) */
	static final int RESIZE_PRONG_SIZE = 9;
	
	// for selection and grid
	
	/** The border 1 stroke */
	static final BorderStroke BORDER_STROKE_1 = new BorderStroke(BORDER_COLOR_1, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, 0.0, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
	
	/** The border 2 stroke */
	static final BorderStroke BORDER_STROKE_2 = new BorderStroke(BORDER_COLOR_2, new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, Double.MAX_VALUE, DASH_LENGTH * 2.0, Arrays.stream(new Double[] { DASH_LENGTH, DASH_SPACE_LENGTH }).collect(Collectors.toList())), null, new BorderWidths(LINE_WIDTH));
	
	// TODO move this out of its own javafx app
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	// TODO translate
	@Override
	public void start(Stage stage) throws Exception {
		
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
//		
//		Slide slide = createTestSlide();
//		PraisenterContext context = new PraisenterContext(library, null, null, null);
//		FxSlide fxSlide = new FxSlide(context, slide, SlideMode.PRESENT);
		
		BorderPane root = new BorderPane();
		
		// TODO see http://fxexperience.com/2014/05/resizable-grid-using-canvas/ for a grid example
		// TODO we'll need to scale the node's width/height and x/y for the current size of the canvas
		Pane canvas = new Pane();
		canvas.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		canvas.setPrefSize(400, 400);
		canvas.setEffect(new DropShadow(5, Color.BLACK));
		
//		canvas.getChildren().addAll(fxSlide.getBackgroundNode(), fxSlide.getContentNode(), fxSlide.getBorderNode());
		
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
			
			// size
			
			Label lblResolution = new Label("Size");
			ComboBox<Resolution> cmbResolutions = new ComboBox<Resolution>(FXCollections.observableArrayList(Resolution.DEFAULT_RESOLUTIONS));
			TextField txtWidth = new TextField();
			txtWidth.setPrefWidth(75);
			txtWidth.setPromptText("width");
			TextField txtHeight = new TextField();
			txtHeight.setPromptText("height");
			txtHeight.setPrefWidth(75);
			Label lblOr = new Label("or");
			lblOr.setPadding(new Insets(0, 5, 0, 5));
			HBox wh = new HBox();
			wh.setSpacing(2);
			wh.setAlignment(Pos.BASELINE_LEFT);
			wh.getChildren().addAll(txtWidth, txtHeight, lblOr, cmbResolutions);
			grid.add(lblResolution, 0, 0);
			grid.add(wh, 1, 0);

			// position
			
			Label lblPosition = new Label("Position");
			TextField txtX = new TextField();
			txtX.setPrefWidth(75);
			txtX.setPromptText("x");
			TextField txtY = new TextField();
			txtY.setPrefWidth(75);
			txtY.setPromptText("y");
			grid.add(lblPosition, 0, 1);
			HBox xy = new HBox();
			xy.getChildren().addAll(txtX, txtY);
			xy.setSpacing(2);
			grid.add(xy, 1, 1);
			
			TitledPane ttlSlide = new TitledPane("Size & Position", grid);
			propertiesPane.getChildren().add(ttlSlide);
		}
		
		{
			GridPane grid = new GridPane();
			grid.setHgap(5);
			grid.setVgap(3);
	//		grid.setGridLinesVisible(true);
			
			// background
			
			Label lblBackground = new Label("Background");
			ChoiceBox<PaintType> cbTypes = new ChoiceBox<PaintType>(FXCollections.observableArrayList(PaintType.values()));
			ColorPicker clrPicker = new ColorPicker();
			clrPicker.managedProperty().bind(clrPicker.visibleProperty());
			GradientPicker pkrGradient = new GradientPicker(null);
			pkrGradient.managedProperty().bind(pkrGradient.visibleProperty());
			MediaPicker pkrImage = new MediaPicker(library, FXCollections.observableSet(), MediaType.IMAGE);
			pkrImage.managedProperty().bind(pkrImage.visibleProperty());
			MediaPicker pkrVideo = new MediaPicker(library, FXCollections.observableSet(), MediaType.VIDEO);
			pkrVideo.managedProperty().bind(pkrVideo.visibleProperty());
			HBox bg = new HBox();
			bg.setSpacing(2);
			bg.getChildren().addAll(cbTypes, clrPicker, pkrGradient, pkrImage, pkrVideo);
			grid.add(lblBackground, 0, 0);
			grid.add(bg, 1, 0);
			
			Label lblScaling = new Label("Scaling");
			ChoiceBox<ScaleType> cbScaling = new ChoiceBox<ScaleType>(FXCollections.observableArrayList(ScaleType.values()));
			Label lblLoop = new Label("Loop");
			CheckBox chkLoop = new CheckBox();
			Label lblMute = new Label("Mute");
			CheckBox chkMute = new CheckBox();
			
			clrPicker.setVisible(false);
			pkrGradient.setVisible(false);
			pkrImage.setVisible(false);
			pkrVideo.setVisible(false);
			cbTypes.valueProperty().addListener((obs, ov, nv) -> {
				switch (nv) {
					case COLOR:
						clrPicker.setVisible(true);
						pkrGradient.setVisible(false);
						pkrImage.setVisible(false);
						pkrVideo.setVisible(false);
						grid.getChildren().removeAll(lblScaling, cbScaling, lblLoop, chkLoop, lblMute, chkMute);
						break;
					case GRADIENT:
						clrPicker.setVisible(false);
						pkrGradient.setVisible(true);
						pkrImage.setVisible(false);
						pkrVideo.setVisible(false);
						grid.getChildren().removeAll(lblScaling, cbScaling, lblLoop, chkLoop, lblMute, chkMute);
						break;
					case IMAGE:
						clrPicker.setVisible(false);
						pkrGradient.setVisible(false);
						pkrImage.setVisible(true);
						pkrVideo.setVisible(false);
						grid.getChildren().removeAll(lblScaling, cbScaling, lblLoop, chkLoop, lblMute, chkMute);
						grid.add(lblScaling, 0, 1);
						grid.add(cbScaling, 1, 1);
						break;
					case VIDEO:
						clrPicker.setVisible(false);
						pkrGradient.setVisible(false);
						pkrImage.setVisible(false);
						pkrVideo.setVisible(true);
						grid.getChildren().removeAll(lblScaling, cbScaling, lblLoop, chkLoop, lblMute, chkMute);
						grid.add(lblScaling, 0, 1);
						grid.add(cbScaling, 1, 1);
						grid.add(lblLoop, 0, 2);
						grid.add(chkLoop, 1, 2);
						grid.add(lblMute, 0, 3);
						grid.add(chkMute, 1, 3);
						break;
					case NONE:
					default:
						// hide all the controls
						clrPicker.setVisible(false);
						pkrGradient.setVisible(false);
						pkrImage.setVisible(false);
						pkrVideo.setVisible(false);
						grid.getChildren().removeAll(lblScaling, cbScaling, lblLoop, chkLoop, lblMute, chkMute);
						break;
				}
			});
			
			TitledPane ttlSlide = new TitledPane("Slide Background", grid);
			propertiesPane.getChildren().add(ttlSlide);
			
			Button btnFont = new Button("Font...");
			btnFont.setOnAction((e) -> {
				// FIXME this won't work, we need to be able to select an arbitrary font size
				// reference source code here
				// https://bitbucket.org/controlsfx/controlsfx/src/13e52b38df16842b71a4c9df1cadbeba6087742a/controlsfx/src/main/java/org/controlsfx/dialog/FontSelectorDialog.java?at=default&fileviewer=file-view-default
				FontSelectorDialog dialog = new FontSelectorDialog(null);
				dialog.show();
			});
			propertiesPane.getChildren().add(btnFont);
		}
		
		// the canvas will go in the center
		VBox wrapper = new VBox();
		wrapper.setPadding(new Insets(20));
		wrapper.getChildren().add(canvas);
		VBox.setVgrow(canvas, Priority.ALWAYS);
		root.setCenter(wrapper);
		root.setRight(propertiesPane);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
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
		txt.setFontName("Impact");
		txt.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		txt.setFontSize(10);
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
//				UUID.fromString("b179387f-ab1d-40ba-a246-40226f375e8b"),
				UUID.fromString("3a455fd7-c8f0-4c81-955b-0bcb3e4c47ef"),
				ScaleType.UNIFORM,
				false,
				true);
		txt.setBackground(img);
		
		MediaObject vid = new MediaObject(
//				UUID.fromString("758e2ed0-88ba-4107-8462-a4ac44670875"),
				UUID.fromString("76fec243-0feb-4a1d-8a56-e57f9193a5cd"),
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
