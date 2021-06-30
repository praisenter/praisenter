package org.praisenter.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.DataManager;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.configuration.ConfigurationPersistAdapter;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.fonts.OpenIconic;
import org.praisenter.ui.themes.Theme;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.RuntimeProperties;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

// TODO fix the manifest
// TODO explore deployment options
// TODO testing on High DPI screens
// TODO fix dark theme

// FEATURE (L-M) Evaluate detecting text language for better indexing (a field per language) in lucene; Apache Tika or LangDetect; This would also be used in the searches to know what indexed fields to use
// FEATURE (L-M) Use Apache POI to read powerpoint files
// FEATURE (M-L) Evaluate alternate JavaFX styles here https://github.com/JFXtras/jfxtras-styles
// FEATURE (H-L) Quick send to display - any place in the app when the context contains something that could be displayed offer a Quick Display button to allow the user to quickly get it shown - with configurable settings
// FEATURE (M-L) From selected media items, generate slides or slide show
// FEATURE (H-H) Auto-update feature (windows only?); Update check (connect out to github packages or something); Auto-download install (download and execute); In config store upgrade number, in app write code to convert from upgrade number to upgrade number;

// a. Generate a self-signed public/private key pair
// b. Generate a signature for the version-check.json and install.jar files

// Upon start up
// 0. Check for install.jar and install.jar.sig
// 0a. If present perform verification again
// 0b. If verified execute install.jar
// 0c. Either way delete all update files
// 0c. When complete continue startup

// After startup and in the background
// 1. Download {url}/v/{current-version}/version-check.json
// 2. Download {url}/v/{current-version}/version-check.json.sig https://docs.oracle.com/javase/tutorial/security/apisign/step3.html
// 3. Verify the signature of the version-check.json with the version-check.json.sig and the public key
// 4. If it does not pass
//		a. Ignore update and notify user
//		b. Clean up files
// 5. If it passes
//		a. Check for a new version
//			i.  If we are on the current version then stop
//			ii. Otherwise
// 				1. Download {url}/v/{current-version}/install.jar
// 				2. Download {url}/v/{current-version}/install.jar.sig
// 				3. Verify the signature of the install.jar with the install.jar.sig and the public key
//				4. If it does not pass
//					a. Ignore update and notify user
//					b. clean up files
//				5. If it passes
//					a. Notify the user of the update with option to restart

// JAVABUG (L) 05/31/17 Java FX just chooses the last image in the set of stage icons rather than choosing the best bugs.openjdk.java.net/browse/JDK-8091186, bugs.openjdk.java.net/browse/JDK-8087459

public final class Praisenter extends Application {
	private static final Logger LOGGER;
	
	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		try {
			Files.createDirectories(Paths.get(Constants.ROOT_PATH));
		} catch (IOException e1) {
			// attempt to write to something
			e1.printStackTrace();
			throw new RuntimeException("Failed to create the praisenter root path: '" + Constants.ROOT_PATH + "'", e1);
		}
		
		// set the log4j configuration file path
		Path log4jPath = Paths.get(Constants.ROOT_PATH, "log4j2.xml");
		if (!Files.exists(log4jPath)) {
			// read the file from 
			try {
				ClasspathLoader.copy("/org/praisenter/config/log4j2.xml", log4jPath);
			} catch (Exception e) {
				// attempt to write to something
				e.printStackTrace();
				throw new RuntimeException("Failed to copy log4j2.xml configuration file from classpath to Praisenter root path", e);
			}
		}
		
		// copy the default languages files
		Path englishTranslationPath = Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH, "messages.properties");
		if (!Files.exists(englishTranslationPath)) {
			// read the file from 
			try {
				Files.createDirectories(Paths.get(Constants.LOCALES_ABSOLUTE_FILE_PATH));
				ClasspathLoader.copy("/org/praisenter/translations/messages.properties", englishTranslationPath);
			} catch (Exception e) {
				// attempt to write to something
				e.printStackTrace();
				throw new RuntimeException("Failed to copy messages.properties file from classpath to the locales folder", e);
			}
		}
		
		// copy the default theme files
		Path defaultThemePath = Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH, "default.css");
		if (!Files.exists(defaultThemePath)) {
			// read the file from 
			try {
				Files.createDirectories(Paths.get(Constants.THEMES_ABSOLUTE_FILE_PATH));
				ClasspathLoader.copy("/org/praisenter/themes/default.css", defaultThemePath);
			} catch (Exception e) {
				// attempt to write to something
				e.printStackTrace();
				throw new RuntimeException("Failed to copy default.css file from classpath to the themes folder", e);
			}
		}
		
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, log4jPath.toAbsolutePath().toString());
		
		// create a logger for this class after the log4j has been initialized
		LOGGER = LogManager.getLogger();
    		
		// log some system info
    	LOGGER.info(Constants.NAME + " v" + Version.STRING);
    	LOGGER.info("OS:        " + (RuntimeProperties.IS_WINDOWS_OS ? "[Windows] " : RuntimeProperties.IS_MAC_OS ? "[MacOS] " : RuntimeProperties.IS_LINUX_OS ? "[Linux] " : "[Other] ") + RuntimeProperties.OPERATING_SYSTEM + " " + RuntimeProperties.ARCHITECTURE);
    	LOGGER.info("Java:      " + RuntimeProperties.JAVA_VERSION + " " + RuntimeProperties.JAVA_VENDOR);
    	LOGGER.info("JVM Args:  " + RuntimeProperties.JVM_ARGUMENTS);
    	LOGGER.info("Java Home: " + RuntimeProperties.JAVA_HOME);
    	LOGGER.info("User Home: " + RuntimeProperties.USER_HOME);
    	LOGGER.info("Locale:    " + Locale.getDefault().toLanguageTag());
	}

	// FEATURE (L-H) We should look at making some of the Java FX features "optional" instead of required
	/** The array of Java FX features that Praisenter uses */
	private static final ConditionalFeature[] REQUIRED_JAVAFX_FEATURES = new ConditionalFeature[] {
		ConditionalFeature.TRANSPARENT_WINDOW,
		ConditionalFeature.GRAPHICS,
		ConditionalFeature.SHAPE_CLIP,
		ConditionalFeature.CONTROLS,
		ConditionalFeature.MEDIA,
		ConditionalFeature.EFFECT
	};
	
	private static final int MIN_WIDTH = 1000;
	private static final int MIN_HEIGHT = 700;
	
    static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
    	long startTime = System.nanoTime();
    	
    	// log supported features
    	LOGGER.info("Java FX Feature Support:");
    	for (ConditionalFeature feature : ConditionalFeature.values()) {
    		LOGGER.info(feature.name() + "=" + Platform.isSupported(feature));
    	}
    	
    	// verify features
    	LOGGER.info("Verifying required Java FX features.");
    	for (ConditionalFeature feature : REQUIRED_JAVAFX_FEATURES) {
    		if (!Platform.isSupported(feature)) {
    			// not supported, attempt to show the user an error message
    			Alert a = Alerts.exception(
    					stage.getOwner(),
    					Translations.get("init.feature.missing.title"), 
    					Translations.get("init.feature.missing.header"), 
    					feature.name());
    			a.showAndWait();
    			LOGGER.info("User closed exception dialog. Exiting application.");
    			Platform.exit();
    		}
    	}
    	LOGGER.info("Required features present.");
    	
    	// title
    	stage.setTitle(Constants.NAME + " " + Version.STRING);
    	
    	// icons
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon16x16alt.png"), 16, 16, true, true));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon32x32.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon48x48.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon64x64.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon96x96.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon128x128.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon256x256.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon512x512.png")));
    	
		// load fonts
    	LOGGER.info("Loading glyph fonts.");
    	GlyphFontRegistry.register(new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/fontawesome-webfont.ttf")));
		GlyphFontRegistry.register(new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/open-iconic.ttf")));

    	// initialize lucene
		final FSDirectory directory = FSDirectory.open(Paths.get(Constants.SEARCH_INDEX_ABSOLUTE_PATH));
		final Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		
		// initialize the data manager
    	final SearchIndex index = new SearchIndex(directory, analyzer);
    	DataManager dataManager = new DataManager(index);
    	
    	// Step 1: load configuration or default it
    	this.loadConfiguration(dataManager).thenApply((configuration) -> {
    		// finally build the context
    		return new GlobalContext(
    				this, 
    				stage, 
    				dataManager, 
    				configuration);
    	}).thenApply((context) -> {
    		// set the language if in the config
        	String languageTag = context.configuration.getLanguageTag();
        	if (languageTag != null) {
        		Locale locale = Locale.forLanguageTag(languageTag);
        		if (locale != null) {
        			LOGGER.info("Setting the default language to '" + locale + "'.");
    				Locale.setDefault(locale);
        		}
        	}
        	
    		// check for debug mode
    		if (context.configuration.isDebugModeEnabled()) {
    			// if enabled, change the log level to TRACE
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
    			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
    			loggerConfig.setLevel(Level.TRACE);
    			ctx.updateLoggers();
    		}
    		
    		return context;
    	}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((context) -> {    		
    		// set the widow size and position
    		// set minimum size
        	stage.setMinWidth(MIN_WIDTH);
        	stage.setMinHeight(MIN_HEIGHT);
        	
        	// read stored position/size
        	double x = context.configuration.getApplicationX();
    		double y = context.configuration.getApplicationY();
    		double w = context.configuration.getApplicationWidth();
    		double h = context.configuration.getApplicationHeight();
        	
    		// clamp the size
    		if (w < MIN_WIDTH) w = MIN_WIDTH;
    		if (h < MIN_HEIGHT) h = MIN_HEIGHT;
    		
    		// set the size
    		stage.setWidth(w);
        	stage.setHeight(h);
    		
    		// set position, but only if
    		// we are in the bounds of a screen
    		// if not, we'll rely on Java FX to place
    		// the window a the default location
    		if (this.isInScreenBounds(x, y, w, h)) {
    			stage.setX(x);
    			stage.setY(y);
    		}
        	
    		// check for maximized
    		if (context.configuration.isApplicationMaximized()) {
    			stage.setMaximized(true);
    		}
    		
    		stage.setOnCloseRequest((e) -> {
    			LOGGER.debug("Request to close the stage received.");
    			e.consume();
    			LOGGER.debug("Attempt to save configuration before exit.");
    			this.onCloseRequest(context).thenRun(() -> {
    				LOGGER.debug("Closing application");
    				Platform.runLater(() -> {
    					stage.close();
    				});
    			});
    		});
    		stage.setOnHiding((e) -> {
    			Platform.exit();
    		});
    		
    		// add bindings
    		context.configuration.applicationMaximizedProperty().bind(stage.maximizedProperty());
    		
    		// only bind the x, y, width, height when the window is not maximized
    		stage.maximizedProperty().addListener((obs, ov, nv) -> {
    			if (nv) {
    				context.configuration.applicationXProperty().unbind();
            		context.configuration.applicationYProperty().unbind();
            		context.configuration.applicationWidthProperty().unbind();
            		context.configuration.applicationHeightProperty().unbind();
    			} else {
    				context.configuration.applicationXProperty().bind(stage.xProperty());
            		context.configuration.applicationYProperty().bind(stage.yProperty());
            		context.configuration.applicationWidthProperty().bind(stage.widthProperty());
            		context.configuration.applicationHeightProperty().bind(stage.heightProperty());
    			}
    		});
    		
    		// when debug mode changes, update the log level
    		context.configuration.debugModeEnabledProperty().addListener((obs, ov, nv) -> {
    			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
    			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    			if (nv) {
	    			loggerConfig.setLevel(Level.TRACE);
				} else {
					loggerConfig.setLevel(Level.INFO);
				}
    			ctx.updateLoggers();
    		});
    		    		
    		// build the loading UI
    		LoadingPane loadingPane = new LoadingPane(context);
    		StackPane layout = new StackPane(loadingPane);
    		
    		Scene scene = new Scene(layout);
    		scene.getStylesheets().add(Theme.getTheme(context.configuration.getThemeName()).getCss());
    		stage.setScene(scene);
    		stage.show();
    		
    		LOGGER.info("Starting load");
    		loadingPane.start().thenRun(() -> {
    			Platform.runLater(() -> {
    				try {
	    				// setup the display manager
	    				LOGGER.info("Initializing the screen manager.");
	    				context.getDisplayManager().initialize();
	    				
	    				// load fonts
	    				LOGGER.info("Loading fonts.");
	    				List<String> families = Font.getFamilies();
	    				Font.getFontNames();
	    				// to improve performance of font pickers, we need to preload
	    				// the fonts by creating a font for each one
	    				for (String family : families) {
	    					Font.font(family);
	    				}
	    				LOGGER.info("Fonts loaded.");
	    				
	    				LOGGER.info("Building UI");
	    	    		PraisenterPane main = new PraisenterPane(context);
	    	    		layout.getChildren().add(0, main);
	    	    		
	    				// fade out the loader
	    				FadeTransition fade = new FadeTransition(Duration.millis(600), loadingPane);
	    				fade.setFromValue(1.0);
	    				fade.setToValue(0.0);
	    				
	    				// we'll pause for a moment to let the user see it completed
	    				SequentialTransition seq = new SequentialTransition(new PauseTransition(Duration.millis(1500)), fade);
	    				seq.setAutoReverse(false);
	    				seq.setCycleCount(1);
	    				
	    				// when the fade out is complete
	    				seq.statusProperty().addListener((fadeStatus, oldFadeStatus, newFadeStatus) -> {
	    					if (newFadeStatus == Animation.Status.STOPPED) {
	    						LOGGER.info("Fade out of loading screen complete.");
	    						layout.getChildren().remove(loadingPane);
	    						LOGGER.info("Loading scene removed from the scene graph. {} node(s) remaining.", layout.getChildren().size());
	    						final long endTime = System.nanoTime();
	    						LOGGER.info("Loading completed in {} seconds.", (endTime - startTime) / 1e9);
	    					}
	    				});
	    				
	    				// play the fade out
	    				seq.play();
    				} catch (Exception ex) {
    					LOGGER.error(ex.getMessage(), ex);
    					this.showExecptionAlertThenExit(ex, stage);
    				}
    			});
    		}).exceptionally((ex) -> {
    			this.showExecptionAlertThenExit(ex, stage);
    			return null;
    		});
    	})).exceptionally((ex) -> {
    		this.showExecptionAlertThenExit(ex, stage);
    		return null;
    	});
    }
    
    private void showExecptionAlertThenExit(Throwable ex, Window owner) {
		Platform.runLater(() -> {
			// and show the error
			Alert a = Alerts.exception(owner, ex);
			a.showAndWait();
			
			LOGGER.info("User closed exception dialog. Exiting application.");
			
			// then exit the app
			Platform.exit();
		});
	}
    
    private CompletableFuture<Configuration> loadConfiguration(DataManager dataManager) {
    	return dataManager.registerPersistAdapter(Configuration.class, new ConfigurationPersistAdapter(Paths.get(Constants.CONFIG_ABSOLUTE_PATH))).exceptionally((ex) -> {
    		LOGGER.error("Failed to load configuration: ", ex);
    		return null;
    	}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((a) -> {
    		LOGGER.info("Loading configuration.");
    		List<Configuration> configs = dataManager.getItemsUnmodifiable(Configuration.class);
    		if (configs.isEmpty()) {
    			return null;
    		}
    		return configs.get(0);
    	})).exceptionally((ex) -> {
    		LOGGER.error("", ex);
    		return null;
    	}).thenCompose((configuration) -> {
    		// just return the config if it's not null
        	CompletableFuture<Configuration> future = CompletableFuture.completedFuture(configuration);
        	if (configuration == null) {
        		// if it is null, then create a new default configuration
        		LOGGER.info("Configuration not found, creating a new one with default settings.");
        		final Configuration newConfiguration = new Configuration();
        		future = dataManager.create(newConfiguration).thenApply((o) -> newConfiguration);
        	}
        	
        	LOGGER.info("Configuration loaded.");
        	return future;
    	});
    }

    private boolean isInScreenBounds(double x, double y, double w, double h) {
    	Rectangle2D bounds = new Rectangle2D(x, y, w, h);
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D sb = screen.getBounds();
            if (bounds.intersects(sb)) {
            	return true;
            }
        }
        
        return false;
    }
    
    private CompletableFuture<Void> onCloseRequest(GlobalContext context) {
    	// close the presentation screens
		context.getDisplayManager().release();
		
    	// wait for any pending async tasks
    	// NOTE: the assumption here is that all asynchronous processing is being performed on the ForkJoinPool commonPool
    	// (the default for CompletableFuture)
    	if (!ForkJoinPool.commonPool().awaitQuiescence(60, TimeUnit.SECONDS)) {
    		LOGGER.warn("Waited 60 seconds for tasks to complete, but they didn't.");
    		// TODO need to prompt user to wait longer or just exit
    	}
    	
		// save some application info
		LOGGER.debug("Saving application location and size: ({}, {}) {}x{} isMaximized={}", 
				context.getConfiguration().getApplicationX(), 
				context.getConfiguration().getApplicationY(), 
				context.getConfiguration().getApplicationWidth(), 
				context.getConfiguration().getApplicationHeight(),
				context.getConfiguration().isApplicationMaximized());
		
		return context.saveConfiguration().exceptionally((ex) -> {
			LOGGER.warn("Failed to save application x,y and width,height when the application closed.", ex);
			return null;
		});
    }
}
