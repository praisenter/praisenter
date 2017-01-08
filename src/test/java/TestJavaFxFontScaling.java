

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TestJavaFxFontScaling extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        String lorem = "<span style='color: blue; font-family: Segoe UI Light'>Lorem</span> ipsum dolor sit amet, consectetur adipiscing elit. Quisque sed feugiat diam. Sed non erat sit amet nibh consectetur tincidunt sed vel sem. Vestibulum varius volutpat luctus. Donec ac rhoncus erat, at placerat erat. Aliquam viverra vestibulum diam auctor dictum. Suspendisse est lectus, aliquet in tristique at, maximus mattis ante. Cras venenatis metus eget ex pulvinar placerat. Fusce eget ante ipsum. Praesent elementum consectetur enim, non congue quam rhoncus sed. Sed euismod convallis tellus, et semper tellus dictum id. Nunc eleifend viverra ligula, vitae ullamcorper risus. In placerat neque semper condimentum feugiat. Nunc eget pulvinar nisl. Duis ornare tempus odio, vel malesuada risus faucibus sit amet.Aenean lacus mauris, aliquam a blandit sed, rutrum et metus. Integer eu volutpat justo, nec viverra diam. Nam ac cursus sapien. Integer id mattis mi. Maecenas dignissim nunc eu mauris dictum, nec pharetra nibh porta. Maecenas mauris mauris, interdum non lectus a, dictum iaculis quam. Donec venenatis arcu vel hendrerit feugiat. Sed mattis molestie posuere. Fusce nec nisi sem.Vivamus vitae nibh gravida, dictum ante non, consectetur magna. Phasellus tortor elit, commodo quis ultricies sit amet, facilisis vitae turpis. Ut vel rutrum massa. Donec vehicula eros quis arcu rutrum, faucibus sollicitudin nibh aliquam. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Phasellus sagittis tellus vel varius tempor. Phasellus venenatis justo lorem, vitae convallis ipsum consectetur quis. Sed nunc nulla, vestibulum vel nulla nec, mattis consectetur dui. Mauris in ultrices risus, in commodo arcu. Quisque varius suscipit nisi eget ultricies. Etiam ut eros bibendum, sagittis sem cursus, maximus neque.Suspendisse cursus venenatis nibh, a dapibus purus auctor nec. Fusce tempor lorem nisl, non tincidunt tortor mollis sit amet. Morbi viverra ullamcorper neque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi faucibus, leo a laoreet rhoncus, elit risus accumsan libero, id tempus tortor est eu sem. Morbi elementum tempor mi, vitae aliquam tellus dictum ac. Maecenas ultricies finibus augue, et imperdiet odio aliquet sed.Duis in hendrerit magna. Suspendisse nec erat non purus semper eleifend et sed dui. Suspendisse tincidunt odio eu sodales cursus. Suspendisse nisl erat, ultrices feugiat tellus sit amet, tincidunt elementum massa. Sed posuere euismod ultrices. Nam pretium congue mi nec fermentum. Duis et nisi et urna pharetra gravida quis id mi. Fusce sagittis, tortor ut mollis sagittis, velit urna ullamcorper velit, vel feugiat ex quam non libero. Donec tortor est, venenatis eget lacus non, ultricies pharetra mauris. Phasellus pretium varius sapien quis facilisis.";
        
        Text text = new Text(lorem);

        TextFlow flow = new TextFlow();
        flow.getChildren().add(text);

        HTMLEditor editor = new HTMLEditor();
        editor.setHtmlText(lorem);
        
        WebView view = new WebView();
        view.getEngine().loadContent(lorem);
       
        editor.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        
        BorderPane root = new BorderPane();
        root.setCenter(editor);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}