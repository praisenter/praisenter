package org.praisenter.javafx.slide;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.praisenter.utility.ClasspathLoader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

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
public final class SlideEditor extends Application {
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
		
		BorderPane root = new BorderPane();
		
		// menu at the top
		MenuBar bar = new MenuBar();
		
		Menu menuFile = new Menu("File");
		MenuItem itemNew = new MenuItem("New");
		MenuItem itemSave = new MenuItem("Save");
		MenuItem itemSaveAs = new MenuItem("Save As...");
		MenuItem itemCancel = new MenuItem("Cancel");
		menuFile.getItems().addAll(itemNew, new SeparatorMenuItem(), itemSave, itemSaveAs, new SeparatorMenuItem(), itemCancel);
		
		Menu menuEdit = new Menu("Edit");
		Menu menuInsert = new Menu("Insert");
		Menu menuMedia = new Menu("Media");
		Menu menuHelp = new Menu("Help");
		
		bar.getMenus().addAll(menuFile, menuEdit, menuInsert, menuMedia, menuHelp);
		
		// TODO see http://fxexperience.com/2014/05/resizable-grid-using-canvas/ for a grid example
		// TODO we'll need to scale the node's width/height and x/y for the current size of the canvas
		Pane canvas = new Pane();
		canvas.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		canvas.setPrefSize(400, 400);
		canvas.setEffect(new DropShadow(5, Color.BLACK));
		
		// menu at the top
		root.setTop(bar);
		// the canvas will go in the center
		VBox wrapper = new VBox();
		wrapper.setPadding(new Insets(20));
		wrapper.getChildren().add(canvas);
		VBox.setVgrow(canvas, Priority.ALWAYS);
		root.setCenter(wrapper);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
