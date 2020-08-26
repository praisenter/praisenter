
import org.praisenter.ui.TextMeasurer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;


public class TestFontMeasuring extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		final Font font = Font.font("Segoe UI Light", FontWeight.NORMAL, FontPosture.ITALIC, 10.0);
		final String text = "Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.";
		final double tww = 800.0;
		final double ls = 5.0;
		final TextBoundsType tbt = TextBoundsType.VISUAL;
		
		final int th = 400;
		
		System.out.println(font.getFamily());
		System.out.println(font.getStyle());
		System.out.println(font.getName());
		System.out.println(font.getSize());
		System.out.println();
		
		Font nf = TextMeasurer.getFittingFontForParagraph(text, font, font.getSize(), tww, th, ls, tbt);
		
		System.out.println(nf.getFamily());
		System.out.println(nf.getStyle());
		System.out.println(nf.getName());
		System.out.println(nf.getSize());
		System.out.println();
		
		nf = TextMeasurer.getFittingFontForLine(text, font, font.getSize(), tww, tbt);
		
		System.out.println(nf.getFamily());
		System.out.println(nf.getStyle());
		System.out.println(nf.getName());
		System.out.println(nf.getSize());
		System.out.println();
		
		BorderPane layout = new BorderPane();
		
		Text txt = new Text(text);
		txt.setWrappingWidth(tww);
		txt.setBoundsType(tbt);
		txt.setFont(nf);
		txt.setLineSpacing(ls);
		
		layout.setCenter(txt);
		
		Scene sc1 = new Scene(layout, Color.TRANSPARENT);

		stage.setScene(sc1);
		stage.show();
		
		System.out.println("[" + txt.getLayoutBounds().getWidth() + ", " + txt.getLayoutBounds().getHeight() + "]");
	}
}
