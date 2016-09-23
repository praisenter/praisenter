import java.util.List;

import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.slide.editor.FontBorderRibbonTab;
import org.praisenter.javafx.slide.editor.FontRibbonTab;
import org.praisenter.javafx.slide.editor.ParagraphRibbonTab;
import org.praisenter.resources.OpenIconic;

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
		
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/resources/open-iconic.ttf")));
		
		BorderPane root = new BorderPane();
		
		HBox ribbon = new HBox();
		root.setTop(ribbon);
		
		ribbon.getChildren().addAll(
				new FontRibbonTab(),
				new ParagraphRibbonTab(),
				new FontBorderRibbonTab());
		
		primaryStage.setScene(new Scene(root));
		//primaryStage.getScene().getStylesheets().add(RibbonTesting.class.getResource("/org/praisenter/javafx/styles/default.css").toExternalForm());
		primaryStage.show();
	}
}
