import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TestTextArea extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		
		TextArea text = new TextArea();
		
		text.setFont(Font.font("Segoe UI Light", 20));
		text.setBackground(null);
		text.setBorder(null);
		text.setText("Lorem Ipsum");
		text.setEffect(null);
		
		root.setCenter(text);
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
