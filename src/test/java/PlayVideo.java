


import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlayVideo extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("It's a video!");
        
        Media media = new Media(new File("path/to/file").toURI().toString());
        MediaException ex = media.getError();
        if (ex != null) {
        	ex.printStackTrace();
        	return;
        }
        
        MediaPlayer mp = new MediaPlayer(media);
        ex = mp.getError();
        if (ex != null) {
        	ex.printStackTrace();
        	return;
        }
        
        // attempt to set volume
        Platform.runLater(() -> {
        	mp.setVolume(0.7);
        });
        
        MediaView mv = new MediaView(mp);
        mv.setFitWidth(800);
        mv.setFitHeight(600);
        
        Text text = new Text("Trying to test volume control");
        text.setFill(Color.BLACK);
        
        StackPane root = new StackPane();
        
        root.getChildren().addAll(mv, text);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        
        mp.play();
        
        ex = mp.getError();
        if (ex != null) {
        	ex.printStackTrace();
        }
    }
}