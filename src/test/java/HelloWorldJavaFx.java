

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloWorldJavaFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	int spacing = 5;
    	
    	VBox root = new VBox();
    	
        
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}