

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.Constants;
import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlidePersistAdapter;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.graphics.SlideStrokeCap;
import org.praisenter.data.slide.graphics.SlideStrokeJoin;
import org.praisenter.data.slide.graphics.SlideStrokeStyle;
import org.praisenter.data.slide.graphics.SlideStrokeType;
import org.praisenter.data.slide.text.FontScaleType;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.SlideFontPosture;
import org.praisenter.data.slide.text.SlideFontWeight;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.slide.JavaFXSlideRenderer;
import org.praisenter.ui.slide.SlideEditor;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloWorldJavaFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
    	int spacing = 5;
    	
    	Directory d = FSDirectory.open(Paths.get(Constants.SEARCH_INDEX_ABSOLUTE_PATH));
    	Analyzer a = new StandardAnalyzer();
    	SearchIndex si = new SearchIndex(d, a);
    	DataManager dm = new DataManager(si);
    	Configuration c = new Configuration();
    	GlobalContext gc = new GlobalContext(this, primaryStage, dm, c);
    	
    	dm.registerPersistAdapter(Slide.class, new SlidePersistAdapter(Paths.get(Constants.SLIDES_ABSOLUTE_PATH), new JavaFXSlideRenderer(gc), c));
    	
    	VBox root = new VBox();
    	root.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
    	root.setAlignment(Pos.TOP_LEFT);
    	
    	SlideView sv = new SlideView(gc);
    	//sv.setPrefSize(512, 384);
    	sv.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	sv.setMinSize(0, 0);
    	sv.setViewMode(SlideMode.VIEW);
    	sv.setViewScalingEnabled(true);
    	
    	Slide s = new Slide();
    	s.setBackground(new SlideColor(0.5,0.5,0.5,0.5));
    	//s.setBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
    	s.setCreatedDate(Instant.now());
    	s.setHeight(768);
    	s.setModifiedDate(Instant.now());
    	s.setName("test");
    	s.setOpacity(1);
    	s.setWidth(1024);
    	
    	TextComponent tc = new TextComponent();
    	tc.setBackground(new SlideColor(0, 1, 0, 1));
    	tc.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut mauris purus, eleifend ac urna nec, venenatis placerat lectus. Vestibulum at sagittis nulla, sed euismod diam. Vivamus vel odio rhoncus turpis ultrices tristique sed feugiat nunc. Nulla condimentum magna in neque sagittis, vel pharetra diam pellentesque. Aliquam erat volutpat. Cras consequat lectus eu odio rhoncus, ut faucibus nisl congue. Curabitur sed elit a neque aliquam rutrum. Vivamus venenatis consectetur malesuada.");
    	tc.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
    	tc.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
    	tc.setPadding(new SlidePadding(10));
    	tc.setTextBorder(new SlideStroke(new SlideColor(1, 0, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
    	tc.setTextPaint(new SlideColor(0, 0, 1, 1));
    	tc.setWidth(300);
    	tc.setHeight(100);
    	tc.setX(200);
    	tc.setY(200);
    	
    	TextComponent tc2 = new TextComponent();
    	tc2.setBackground(new SlideColor(0, 0, 1, 1));
    	tc2.setBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
    	tc2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut mauris purus, eleifend ac urna nec, venenatis placerat lectus. Vestibulum at sagittis nulla, sed euismod diam. Vivamus vel odio rhoncus turpis ultrices tristique sed feugiat nunc. Nulla condimentum magna in neque sagittis, vel pharetra diam pellentesque. Aliquam erat volutpat. Cras consequat lectus eu odio rhoncus, ut faucibus nisl congue. Curabitur sed elit a neque aliquam rutrum. Vivamus venenatis consectetur malesuada.");
    	tc2.setFontScaleType(FontScaleType.NONE);
    	tc2.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
    	tc2.setPadding(new SlidePadding(10));
//    	tc2.setTextBorder(new SlideStroke(new SlideColor(1, 0, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
    	tc2.setTextPaint(new SlideColor(0, 0, 0, 1));
    	tc2.setWidth(200);
    	tc2.setHeight(100);
    	tc2.setX(900);
    	tc2.setY(500);
    	
    	s.getComponents().add(tc);
    	s.getComponents().add(tc2);
    	
    	sv.setSlide(s);
    	
    	SlideEditor se = new SlideEditor(gc, new DocumentContext<Slide>(s));
    	se.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	se.setMinSize(0, 0);
    	
    	Button btnSave = new Button("save new");
    	btnSave.setOnAction(e -> {
    		gc.getDataManager().create(s);
    	});
    	
    	root.getChildren().addAll(se, sv, btnSave);
//    	VBox.setVgrow(sv, Priority.ALWAYS);
    	VBox.setVgrow(se, Priority.ALWAYS);
        
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
    }
}