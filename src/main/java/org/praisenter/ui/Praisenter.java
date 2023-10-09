package org.praisenter.ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.stage.Stage;

// FEATURE (L-M) Evaluate detecting text language for better indexing (a field per language) in lucene; Apache Tika or LangDetect; This would also be used in the searches to know what indexed fields to use
// FEATURE (L-M) Use Apache POI to read powerpoint files
// FEATURE (M-L) Evaluate alternate JavaFX styles here https://github.com/JFXtras/jfxtras-styles
// FEATURE (H-L) Quick send to display - any place in the app when the context contains something that could be displayed offer a Quick Display button to allow the user to quickly get it shown - with configurable settings
// FEATURE (M-L) From selected media items, generate slides or slide show
// FEATURE (H-H) Auto-update feature (windows only?); Update check (connect out to github packages or something); Auto-download install (download and execute); In config store upgrade number, in app write code to convert from upgrade number to upgrade number;

// JAVABUG (L) 05/31/17 Java FX just chooses the last image in the set of stage icons rather than choosing the best bugs.openjdk.java.net/browse/JDK-8091186, bugs.openjdk.java.net/browse/JDK-8087459

public final class Praisenter extends Application {
    static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
    	// the default style of the app
    	Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
    	
    	Parameters params = this.getParameters();
    	Map<String, String> named = params.getNamed();
    	
    	// specified by --workspace=C:\path\to\workspace
    	Path path = null;
    	String workspacePathString = named.get("workspace");
    	if (workspacePathString != null) {
    		try {
    			path = Paths.get(workspacePathString);
    		} catch (Exception ex) {
    			// just ignore it if we get an exception
    		}
    	}

    	LifecycleHandler lifecycleHandler = new LifecycleHandler();
    	lifecycleHandler.start(this, stage, path);
    }
}
