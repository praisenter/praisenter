

import java.util.UUID;

import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.animation.AnimatedObject;
import org.praisenter.javafx.animation.AnimatedObjectType;
import org.praisenter.javafx.animation.AnimationPane;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.easing.Linear;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
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
        
        ObservableSet<AnimatedObject> objects = FXCollections.observableSet(new AnimatedObject(id, AnimatedObjectType.COMPONENT, "test"));
        
        AnimationPane root = new AnimationPane(objects);
        
//        Blinds animation = new Blinds();
//        Fade animation = new Fade();
        Swipe animation = new Swipe();
        animation.setId(id);
        animation.setDelay(100);
        animation.setDuration(300);
        animation.setEasing(new Linear());
//        animation.setEasing(new Back());
        animation.setDirection(Direction.UP);
//        animation.setOrientation(Orientation.HORIZONTAL);
        animation.setType(AnimationType.IN);
        root.setAnimation(animation);
        
        Scene scene = new Scene(root);
//        scene.getStylesheets().add(Praisenter.THEME_CSS);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}