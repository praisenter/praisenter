import org.praisenter.javafx.GradientPicker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class GradientPickerTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		GradientPicker gp = new GradientPicker();
		Scene scene = new Scene(gp);
		primaryStage.setScene(scene);
		primaryStage.show();
		
//		gp.setPaint(new RadialGradient(0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.BLACK)));
		gp.setPaint(new LinearGradient(0, 0, 2, 2, true, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.PINK)));
//		gp.paintProperty().set(new RadialGradient(0, 0, 0.5, 0.5, 2, true, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.BLACK)));
		
		gp.paintProperty().addListener((obs, ov, nv) -> {
			System.out.println(nv);
		});
	}
}
