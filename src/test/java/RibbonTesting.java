import java.util.List;

import org.praisenter.javafx.slide.editor.FontRibbonTab;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RibbonTesting extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		
		HBox ribbon = new HBox();
		root.setTop(ribbon);
		
		ribbon.getChildren().add(new FontRibbonTab());
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
