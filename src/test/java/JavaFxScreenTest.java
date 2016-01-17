import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class JavaFxScreenTest extends Application {
	public static void main(String[] args) {
		Application.launch(args);
		
		
		
		
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

		// detection
		ObservableList<Screen> screens = Screen.getScreens();
		for (Screen screen : screens) {
			System.out.println(screen.getBounds());
		}

		// creating a new window
		// transparent background
		Stage s1 = new Stage(StageStyle.TRANSPARENT);
		// size to fill the screen
		s1.setWidth(screens.get(1).getBounds().getWidth());
		s1.setHeight(screens.get(1).getBounds().getHeight());
		s1.setX(screens.get(1).getBounds().getMinX());
		s1.setY(screens.get(1).getBounds().getMinY());
		
		VBox layout = new VBox();
		
		// just for visuals
		Text text = new Text("Hello world");
		text.setFont(Font.font("Segoe UI Light", 30));
		layout.getChildren().add(text);
		
		// translucency testing
		FadeTransition t = new FadeTransition(Duration.millis(5000), layout);
		t.setFromValue(0);
		t.setToValue(1);
		t.setCycleCount(5);
		t.setAutoReverse(true);
		
		// transparent background
		Scene sc1 = new Scene(layout, Color.TRANSPARENT);
		
		s1.setScene(sc1);
		s1.show();
		t.play();
	}
}
