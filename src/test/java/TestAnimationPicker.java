

import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.animation.AnimationPane;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestAnimationPicker extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animation Picker");
        
        AnimationPane root = new AnimationPane();
        
        Scene scene = new Scene(root, 770, 400);
        scene.getStylesheets().add(Praisenter.THEME_CSS);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}