import org.controlsfx.glyphfont.FontAwesome;
import org.praisenter.ui.Praisenter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class TestFontIcons extends Application {
	public static void main(String[] args) {
//		System.setProperty("prism.lcdtext", "false");
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Font.loadFont(Praisenter.class.getResourceAsStream("/org/praisenter/resources/fontawesome-webfont.ttf"), 10);
//		GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/resources/fontawesome-webfont.ttf")));
//		GlyphFont font = GlyphFontRegistry.font("FontAwesome");
		
		VBox root = new VBox();
		
		
		Scene scene = new Scene(root);
//		scene.getStylesheets().add(TestFontIcons.class.getResource("/org/praisenter/javafx/styles/default.css").toExternalForm());
		
		root.getChildren().add(new Label("Hello world"));
		
		for (int i = 10; i < 100; i+=10) {
			Text text = new Text(String.valueOf(FontAwesome.Glyph.MUSIC.getChar()));
			text.setFontSmoothingType(FontSmoothingType.GRAY);
			text.setFont(Font.font("FontAwesome", i));
			//Glyph g = font.create(FontAwesome.Glyph.MUSIC).size(i);
//			g.setStyle("-fx-font-smoothing-type: lcd;");
			root.getChildren().add(text);
		}
		
		stage.setScene(scene);
		stage.show();
	}
}
