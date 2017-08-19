

import java.util.List;

import org.praisenter.song.Verse;
import org.praisenter.song.VerseBreak;
import org.praisenter.song.VerseFragment;
import org.praisenter.song.VerseText;
import org.praisenter.utility.StringManipulator;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloWorldJavaFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	int spacing = 5;
    	
    	VBox root = new VBox();
    	root.setSpacing(spacing * 4);
        
        Verse verse = new Verse();
        verse.setFontSize(50);
        verse.setName("v", 1, null);
        
        // line 0
        verse.getFragments().add(new VerseText(null, "C#", "fortissimo"));
        verse.getFragments().add(new VerseText(null, "Db", null));
        verse.getFragments().add(new VerseText(null, "A", null));
        verse.getFragments().add(new VerseBreak());
        
        // line 1
        verse.getFragments().add(new VerseText(null, null, "we start on v1"));
        verse.getFragments().add(new VerseText("This is the ", "C#", "rapid"));
        verse.getFragments().add(new VerseText("world of man ", "Db", null));
        verse.getFragments().add(new VerseText("and we do not belog", "A", null));
        verse.getFragments().add(new VerseBreak());
        
        // line 2
        verse.getFragments().add(new VerseText("My hope is ", "C#", "slur"));
        verse.getFragments().add(new VerseText("in you lord ", "Db", "Here is a really long comment that should be wrapped and could be super long too"));
        verse.getFragments().add(new VerseText("but even if you don't", null, null));
        verse.getFragments().add(new VerseBreak());
        
        root.getChildren().add(process(verse.getFragments()));
        
        
        
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
    
    private static final VBox process(List<VerseFragment> fragments) {
    	VBox lines = new VBox();
    	lines.setSpacing(20);
    	
    	GridPane grid = new GridPane();
    	grid.setVgap(5);
    	grid.setHgap(5);
    	lines.getChildren().add(grid);
    	
    	int column = 0;
    	for (VerseFragment fragment : fragments) {
    		if (fragment instanceof VerseBreak) {
    			grid = new GridPane();
    	    	grid.setVgap(5);
    	    	grid.setHgap(5);
    	    	lines.getChildren().add(grid);
    			continue;
    		} else if (fragment instanceof VerseText) {    			
    			VerseText vt = (VerseText)fragment;
    			if (!StringManipulator.isNullOrEmpty(vt.getChord())) {
    				Label label = new Label();
        			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
    				label.setText(vt.getChord());
    				grid.add(label, column, 0);
    			} 
    			if (!StringManipulator.isNullOrEmpty(vt.getText())) {
    				Label label = new Label();
        			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
    				label.setText(vt.getText());
    				grid.add(label, column, 1);
    			} 
    			if (!StringManipulator.isNullOrEmpty(vt.getComment())) {
    				Label label = new Label();
        			label.setMinSize(0, Label.USE_PREF_SIZE);
    				label.setText(vt.getComment());
    				label.setMaxWidth(100);
    				label.setWrapText(true);
    				label.setTextFill(Color.GREY);
    				grid.add(label, column, 2);
    				GridPane.setValignment(label, VPos.TOP);
    			}
    			column++;
    		}
    	}
    	
    	return lines;
    }
}