


import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayVideo extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("It's a video!");
        
        Media media = new Media(new File("D:\\Personal\\Praisenter\\20051210-w50s.flv").toURI().toString());
//        Media media = new Media(new File("C:\\Users\\William\\Desktop\\test\\033_JumpBack.avi.mp4").toURI().toString());
        MediaException ex = media.getError();
        if (ex != null) ex.printStackTrace();
        System.out.println(media.getDuration());
        System.out.println(media.getHeight());
        System.out.println(media.getWidth());
        MediaPlayer mp = new MediaPlayer(media);
        ex = mp.getError();
        if (ex != null) ex.printStackTrace();
        MediaView mv = new MediaView(mp);
//        mp.setCycleCount(Integer.MAX_VALUE);
        mv.setFitWidth(800);
        mv.setFitHeight(600);
//        mv.setPreserveRatio(true);
        
//        Media media2 = new Media(new File("C:\\Users\\William\\Desktop\\test\\trailer_1080p.ogg.mp4").toURI().toString());
//        MediaPlayer mp2 = new MediaPlayer(media2);
//        MediaView mv2 = new MediaView(mp2);
//        mv2.setFitWidth(300);
//        mv2.setFitHeight(300);
        
        Text text = new Text("Hey this is some text on top of a video.");
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Segoe UI Light", 20));
        
        StackPane root = new StackPane();
        
        root.getChildren().addAll(mv, text);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        
        mp.play();
        ex = mp.getError();
        if (ex != null) ex.printStackTrace();
//        mp2.play();
    }
}