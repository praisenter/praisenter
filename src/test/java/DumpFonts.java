import java.util.List;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class DumpFonts extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		List<String> fonts = Font.getFontNames();
		for (String font : fonts) {
			System.out.println(font);
		}
		System.out.println("==================================================================");
		System.out.println("==================================================================");
		List<String> families = Font.getFamilies();
		for (String family : families) {
			System.out.println(family);
		}
	}
}
