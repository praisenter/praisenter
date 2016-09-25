import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Praisenter;
import org.praisenter.javafx.slide.editor.BackgroundRibbonTab;
import org.praisenter.javafx.slide.editor.BorderRibbonTab;
import org.praisenter.javafx.slide.editor.CountdownRibbonTab;
import org.praisenter.javafx.slide.editor.DateTimeRibbonTab;
import org.praisenter.javafx.slide.editor.FontBorderRibbonTab;
import org.praisenter.javafx.slide.editor.FontGlowRibbonTab;
import org.praisenter.javafx.slide.editor.FontRibbonTab;
import org.praisenter.javafx.slide.editor.FontShadowRibbonTab;
import org.praisenter.javafx.slide.editor.GeneralRibbonTab;
import org.praisenter.javafx.slide.editor.GlowRibbonTab;
import org.praisenter.javafx.slide.editor.ParagraphRibbonTab;
import org.praisenter.javafx.slide.editor.PlaceholderRibbonTab;
import org.praisenter.javafx.slide.editor.ShadowRibbonTab;
import org.praisenter.resources.OpenIconic;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RibbonTesting extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/resources/open-iconic.ttf")));
		
		BorderPane root = new BorderPane();
		
		HBox ribBox = new HBox(new BackgroundRibbonTab(null), new BorderRibbonTab(), new GeneralRibbonTab(), new GlowRibbonTab(), new ShadowRibbonTab());
		ribBox.setPadding(new Insets(0, 0, 0, 2));
		Tab tabBox = new Tab(" General ", ribBox);
		tabBox.setClosable(false);
		
		HBox ribText = new HBox(new FontRibbonTab(), new ParagraphRibbonTab(), new FontBorderRibbonTab(), new FontShadowRibbonTab(), new FontGlowRibbonTab());
		ribText.setPadding(new Insets(0, 0, 0, 2));
		Tab tabText = new Tab(" Text ", ribText);
		tabText.setClosable(false);
		
		Tab tabInsert = new Tab(" Insert ");
		tabInsert.setClosable(false);
		
		HBox ribFormat = new HBox(new DateTimeRibbonTab(), new CountdownRibbonTab(), new PlaceholderRibbonTab());
		ribFormat.setPadding(new Insets(0, 0, 0, 2));
		Tab tabFormat = new Tab(" Format ", ribFormat);
		tabFormat.setClosable(false);
		
		TabPane tabs = new TabPane(tabInsert, tabBox, tabText, tabFormat);
		
		root.setTop(tabs);
		
		primaryStage.setScene(new Scene(root));
		primaryStage.getScene().getStylesheets().add(RibbonTesting.class.getResource("/org/praisenter/javafx/styles/default.css").toExternalForm());
		primaryStage.show();
	}
}
