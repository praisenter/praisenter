import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class TestTask extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.start();
		
		Thread.sleep(1000);
		
		task.onSucceededProperty().addListener((e) -> {
			System.out.println("test");
		});
	}
}
