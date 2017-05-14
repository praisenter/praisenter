

import java.util.UUID;

import org.praisenter.javafx.slide.animation.AnimationPickerPane;
import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.easing.Linear;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestAnimationPicker extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animation Picker");
        UUID id = UUID.randomUUID();

        AnimationPickerPane root = new AnimationPickerPane();
        
        Swipe animation = new Swipe(AnimationType.IN,
        		300,
        		100,
        		Animation.DEFAULT_REPEAT_COUNT,
        		Animation.DEFAULT_AUTO_REVERSE,
        		new Linear(),
        		Direction.UP);
        root.setValue(animation);
        
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}