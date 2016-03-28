

import java.util.UUID;

import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.animation.AnimatableObject;
import org.praisenter.javafx.animation.AnimationPane;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class TestAnimationPicker extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animation Picker");
        
        ObservableSet<AnimatableObject> objects = FXCollections.observableSet(new AnimatableObject(UUID.randomUUID(), "test"));
        
        AnimationPane root = new AnimationPane(objects);
        
        Scene scene = new Scene(root, 770, 400);
        scene.getStylesheets().add(Praisenter.THEME_CSS);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}