import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

import org.praisenter.javafx.configuration.ScreenMapping;
import org.praisenter.javafx.configuration.ScreenRole;
import org.praisenter.javafx.screen.ScreenManager;


public class DisplayScreenTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		List<ScreenMapping> mapping = new ArrayList<ScreenMapping>();
		mapping.add(new ScreenMapping("\\Display0", ScreenRole.NONE));
		mapping.add(new ScreenMapping("\\Display1", ScreenRole.PRESENTATION));
		
		ScreenManager sm = new ScreenManager();
		sm.setup(mapping);
	}
}
