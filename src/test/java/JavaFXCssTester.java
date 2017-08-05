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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXCssTester extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	private Path currentCss = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox row1 = new VBox(
				new Button("button"),
				new ToggleButton("toggle"),
				new CheckBox("check"),
				new ChoiceBox<String>(FXCollections.observableArrayList("item1", "item2", "item3")),
				new ComboBox<String>(FXCollections.observableArrayList("item1", "item2", "item3")),
				new RadioButton("radio"),
				new Hyperlink("link"),
				new Label("label"),
				new MenuBar(new Menu("menu", null, new MenuItem("item1"), new SeparatorMenuItem(), new MenuItem("item2"), new Menu("sub", null, new MenuItem("sub1")))),
				new Separator(),
				new Slider(0, 100, 0),
				new Spinner<>(0, 10, 1),
				new TextField());
		
		TabPane tabs = new TabPane(new Tab("controls", row1));
		
		TextArea area = new TextArea();
		
		final Path css1 = Paths.get(System.getProperty("user.home")).resolve("temp1.css");
		final Path css2 = Paths.get(System.getProperty("user.home")).resolve("temp2.css");
		this.currentCss = css1;
		
		SplitPane split = new SplitPane(tabs, area);
		split.setOrientation(Orientation.HORIZONTAL);
		
		Button swap = new Button("Swap CSS");
		
		BorderPane root = new BorderPane();
		root.setTop(swap);
		root.setCenter(split);
		
		Scene scene = new Scene(root);
		
		swap.setOnAction(e -> {
			// save the css to the other 
			if (this.currentCss == css1) {
				this.currentCss = css2;
			} else {
				this.currentCss = css1;
			}
			try {
				Files.write(this.currentCss, area.getText().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				scene.getStylesheets().setAll(this.currentCss.toAbsolutePath().toUri().toURL().toExternalForm());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
