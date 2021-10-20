package org.praisenter.ui;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.ui.slide.JavaFXSlideRenderer;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.upgrade.InstallUpgradeHandler;
import org.praisenter.utility.RuntimeProperties;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

// FEATURE (L-L) Replace the current loading background image

final class LoadingPane extends Pane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final double BAR_X_OFFSET = 75.0;
	private static final double BAR_Y_OFFSET = 50.0;
	private static final double CIRCLE_RADIUS = 50.0;
	private static final double LINE_WIDTH = 4.0;
	private static final long ANIMATION_DURATION = 300;
	
	// members
	
	private final GlobalContext context;
	private final InstallUpgradeHandler upgradeHandler;
	
	private final StringProperty message;
	private final DoubleProperty progress;
	
	private final RotateTransition circleAnimation;
	private Timeline barAnimation;
	
	public LoadingPane(GlobalContext context, InstallUpgradeHandler upgradeHandler) {
		this.context = context;
		this.upgradeHandler = upgradeHandler;
		
		this.message = new SimpleStringProperty();
		this.progress = new SimpleDoubleProperty();
		
		// set the background image
    	setBackground(new Background(
    			new BackgroundImage(
    					new Image(LoadingPane.class.getResourceAsStream("/org/praisenter/images/splash.jpg")), 
    					BackgroundRepeat.NO_REPEAT, 
    					BackgroundRepeat.NO_REPEAT, 
    					null, 
    					new BackgroundSize(1, 1, true, true, false, true))));
    	
    	// loading bar background
    	final Line barbg = new Line();
    	barbg.setStartX(BAR_X_OFFSET);
    	barbg.startYProperty().bind(this.heightProperty().subtract(BAR_Y_OFFSET));
    	barbg.endYProperty().bind(this.heightProperty().subtract(BAR_Y_OFFSET));
    	barbg.endXProperty().bind(this.widthProperty().subtract(BAR_X_OFFSET));
    	barbg.setStroke(new Color(0, 0, 0, 0.3));
    	barbg.setStrokeWidth(LINE_WIDTH);
    	
    	// the loading bar
    	final Line barfg = new Line();
    	barfg.setStroke(new Color(1, 1, 1, 1));
    	barfg.setStrokeWidth(LINE_WIDTH);
    	barfg.setStartX(BAR_X_OFFSET);
    	barfg.startYProperty().bind(this.heightProperty().subtract(BAR_Y_OFFSET));
    	barfg.setEndX(BAR_X_OFFSET + 1);
    	barfg.endYProperty().bind(this.heightProperty().subtract(BAR_Y_OFFSET));
    	
    	// loading... text
    	Text loadingText = new Text(Translations.get("task.loading"));
    	loadingText.setFont(this.getFont(80));
    	loadingText.setFill(Color.WHITE);
    	loadingText.setX(BAR_X_OFFSET - 5);
    	loadingText.yProperty().bind(this.heightProperty().add(-BAR_Y_OFFSET - CIRCLE_RADIUS * 0.75));
    	
    	// the current loading action
    	Text currentAction = new Text();
    	currentAction.textProperty().bind(this.message);
    	currentAction.setFont(this.getFont(15));
    	currentAction.setFill(Color.WHITE);
    	currentAction.setX(BAR_X_OFFSET);
    	currentAction.yProperty().bind(this.heightProperty().add(-BAR_Y_OFFSET - LINE_WIDTH - CIRCLE_RADIUS * 0.25));
    	
    	final Path path = new Path();
    	path.setFill(null);
    	path.setStroke(Color.WHITE);
    	path.setStrokeWidth(LINE_WIDTH);
    	path.setStrokeLineCap(StrokeLineCap.BUTT);
    	
    	final DoubleBinding ccx = Bindings.createDoubleBinding(() -> {
    		return this.getWidth() - BAR_X_OFFSET - CIRCLE_RADIUS;
    	}, this.widthProperty());
    	final DoubleBinding ccy = Bindings.createDoubleBinding(() -> {
    		return this.getHeight() - BAR_Y_OFFSET - LINE_WIDTH - CIRCLE_RADIUS * 0.5 - CIRCLE_RADIUS;
    	}, this.heightProperty());
    	
    	for (int i = 0; i < 3; i++) {
    		double angle = i * 120;
    		MoveTo move = new MoveTo();
        	move.xProperty().bind(ccx.add(CIRCLE_RADIUS * Math.cos(Math.toRadians(angle))));
        	move.yProperty().bind(ccy.add(CIRCLE_RADIUS * Math.sin(Math.toRadians(angle))));
        	ArcTo arc = new ArcTo();
        	arc.setRadiusX(CIRCLE_RADIUS);
        	arc.setRadiusY(CIRCLE_RADIUS);
        	arc.setXAxisRotation(0);
        	arc.setLargeArcFlag(false);
        	arc.setSweepFlag(true);
        	arc.xProperty().bind(ccx.add(CIRCLE_RADIUS * Math.cos(Math.toRadians(angle + 100))));
        	arc.yProperty().bind(ccy.add(CIRCLE_RADIUS * Math.sin(Math.toRadians(angle + 100))));
        	path.getElements().addAll(move, arc);
    	}
    	
    	// the circle animation
    	this.circleAnimation = new RotateTransition(Duration.millis(2000), path);
    	this.circleAnimation.setByAngle(-360);
    	this.circleAnimation.setInterpolator(Interpolator.LINEAR);
    	this.circleAnimation.setCycleCount(Animation.INDEFINITE);
    	this.circleAnimation.play();
    	
    	// handle progress updates
    	this.progress.addListener((obs, ov, nv) -> {
    		// when the progress changes, we could just go ahead and set the new
    		// end x of the loading bar, but it looks choppy.
    		
    		// instead we will animate the end x of the bar to the correct position
    		// using a timeline.
    		
    		// if the current timeline isn't finished by the time a new target end
    		// x is reached, it is stopped wherever it is and a new timeline animates
    		// it to the new end x.
    		
    		// create a new timeline to animate the end x property of the loading bar
    		Timeline tn = new Timeline(
    				new KeyFrame(
    						Duration.millis(ANIMATION_DURATION), 
    						new KeyValue(
    								barfg.endXProperty(), 
    								BAR_X_OFFSET + nv.doubleValue() * (LoadingPane.this.getWidth() - BAR_X_OFFSET * 2), 
    								Interpolator.EASE_IN)));
    		
    		// stop the current one (if needed)
    		if (barAnimation != null) {
    			barAnimation.stop();
    		}
    		
    		// assign the new animation
    		barAnimation = tn;
    		
    		// play the new animation
    		barAnimation.play();
    	});
    	
    	// finally add all the nodes to this pane
    	this.getChildren().addAll(barbg, barfg, path, loadingText, currentAction);
	}
	
	private Font getFont(double size) {
    	// TODO test fonts on various platforms
    	return Font.font(
			RuntimeProperties.IS_WINDOWS_OS 
			? "Segoe UI Light" //"Sans Serif" //"Lucida Grande" //"Segoe UI Light" 
			: RuntimeProperties.IS_MAC_OS 
				? "Lucida Grande"
				: "Sans Serif", size);
	}

	private CompletableFuture<Void> performUpgrade() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.upgrade"));
		}).apply(null).thenCompose((v) -> {
			LOGGER.info("Performing any upgrade steps");
			return this.upgradeHandler.performUpgradeSteps();
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.progress.set(0.1);
		}));
	}
	
	private CompletableFuture<Void> loadBibles() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.bible"));
		}).apply(null).thenCompose((v) -> {
			LOGGER.info("Loading bibles");
			return this.context.workspaceManager.registerBiblePersistAdapter();
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.progress.set(0.3);
		}));
	}

	private CompletableFuture<Void> loadSongs() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.song"));
		}).apply(null).thenCompose((v) -> {
			LOGGER.info("Loading songs");
			return this.context.workspaceManager.registerSongPersistAdapter();
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.progress.set(0.5);
		}));
	}
	
	private CompletableFuture<Void> loadMedia() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.media"));
		}).apply(null).thenCompose((v) -> {
			LOGGER.info("Loading media");
			return this.context.workspaceManager.registerMediaPersistAdapter();
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.progress.set(0.7);
		}));
	}
	
	private CompletableFuture<Void> loadSlides() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.slide"));
		}).apply(null).thenCompose((v) -> {
			LOGGER.info("Loading slides");
			return this.context.workspaceManager.registerSlidePersistAdapter(new JavaFXSlideRenderer(this.context));
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.progress.set(0.8);
		}));
	}

	private CompletableFuture<Void> loadDisplayManager() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.displays"));
			// setup the display manager
			LOGGER.info("Initializing the screen manager.");
			this.context.getDisplayManager().initialize();
			this.progress.set(0.85);
		}).apply(null).thenRunAsync(() -> {
			this.waitForAnimation();
		});
	}
	
	private CompletableFuture<Void> loadFonts() {
		return AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.message.set(Translations.get("task.loading.fonts"));
			LOGGER.info("Loading fonts.");
			List<String> families = Font.getFamilies();
			Font.getFontNames();
			// to improve performance of font pickers, we need to preload
			// the fonts by creating a font for each one
			for (String family : families) {
				Font.font(family);
			}
			LOGGER.info("Fonts loaded.");
			this.progress.set(0.9);
		}).apply(null).thenRunAsync(() -> {
			this.waitForAnimation();
		});
	}
	
	private CompletableFuture<Node> loadMainUI() {
		return AsyncHelper.onJavaFXThreadAndWait((v) -> {
			this.message.set(Translations.get("task.loading.ui"));
			LOGGER.info("Building UI");
			PraisenterPane main = new PraisenterPane(this.context);
			this.progress.set(1.0);
			return main;
		}).apply(null).thenApplyAsync((ui) -> {
			this.waitForAnimation();
			return ui;
		});
	}
	
	private void waitForAnimation() {
		try {
			Thread.sleep(ANIMATION_DURATION);
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Starts the loading process on another thread.
	 */
	public CompletableFuture<Node> start() {
		return CompletableFuture.completedFuture(null)
		.thenCompose((v) -> {
			return this.performUpgrade();
		}).thenCompose((v) -> {
			return this.loadBibles();
		}).thenCompose((v) -> {
			return this.loadSongs();
		}).thenCompose((v) -> {
			return this.loadMedia();
		}).thenCompose((v) -> {
			return this.loadSlides();
		}).thenCompose((v) -> {
			return this.loadDisplayManager();
		}).thenCompose((v) -> {
			return this.loadFonts();
		}).thenCompose((v) -> {
			return this.loadMainUI();
		});
	}
}
