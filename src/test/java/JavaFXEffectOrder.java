import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JavaFXEffectOrder extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	private Path currentCss = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		ImageView view = new ImageView(new Image("photo.png"));
		
		//  1. ColorAdjust or SepiaTone?
		//	2. Bloom or Glow?
		
		//  3. GaussianBlur or MotionBlur
		
		//  4. InnerShadow
		//  5. DropShadow
		
		//  6. Reflection
		
//		ColorAdjust color = new ColorAdjust(0.2, 0.8, 0.5, 0.1);
//		SepiaTone sepia = new SepiaTone(0.8);
		Bloom bloom = new Bloom(0.5);
		Glow glow = new Glow(0.5);
//		GaussianBlur blur = new GaussianBlur(4);
//		MotionBlur motion = new MotionBlur(80, 5);
//		InnerShadow inner = new InnerShadow(10, 0, 0, Color.YELLOW);
//		DropShadow drop = new DropShadow(10, 0, 0, Color.BLACK);
//		Reflection reflection = new Reflection(-62, 1, 1, 1.0);
		
//		color.setInput(value);
//		glow.setInput(value);
//		motion.setInput(value);
//		bloom.setInput(sepia);
//		blur.setInput(bloom);
//		inner.setInput(blur);
//		drop.setInput(inner);
//		reflection.setInput(drop);
		
		Text text = new Text("hello");
		text.setFill(Color.RED);
		text.setFont(Font.font(null, 100));
		
		StackPane root = new StackPane(view, text);
		text.setEffect(bloom);
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
