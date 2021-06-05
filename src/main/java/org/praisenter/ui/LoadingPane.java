package org.praisenter.ui;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.praisenter.Constants;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BiblePersistAdapter;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaPersistAdapter;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlidePersistAdapter;
import org.praisenter.data.slide.SlideShow;
import org.praisenter.data.slide.SlideShowPersistAdapter;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPersistAdapter;
import org.praisenter.ui.slide.JavaFXSlideRenderer;
import org.praisenter.ui.translations.Translations;
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
	private static final double BAR_X_OFFSET = 75.0;
	private static final double BAR_Y_OFFSET = 50.0;
	private static final double CIRCLE_RADIUS = 50.0;
	private static final double LINE_WIDTH = 4.0;
	
	// members
	
	private final GlobalContext context;
	
	private final StringProperty message;
	private final DoubleProperty progress;
	
	private final RotateTransition circleAnimation;
	private Timeline barAnimation;
	
	public LoadingPane(GlobalContext context) {
		this.context = context;
		
		this.message = new SimpleStringProperty();
		this.progress = new SimpleDoubleProperty();
		
		// FIXME Add "Praisenter" text or logo to the loading pane
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
    						Duration.millis(300), 
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
	
	private CompletableFuture<Void> loadBibles() {
		return CompletableFuture.runAsync(() -> {
			this.message.set(Translations.get("task.loading.bible"));
		}).thenCompose((v) -> {
			return this.context.dataManager.registerPersistAdapter(Bible.class, new BiblePersistAdapter(Paths.get(Constants.BIBLES_ABSOLUTE_PATH)));
		}).thenRun(() -> {
			this.progress.set(0.2);
		});
	}

	private CompletableFuture<Void> loadSongs() {
		return CompletableFuture.runAsync(() -> {
			this.message.set(Translations.get("task.loading.song"));
		}).thenCompose((v) -> {
			return this.context.dataManager.registerPersistAdapter(Song.class, new SongPersistAdapter(Paths.get(Constants.SONGS_ABSOLUTE_PATH)));
		}).thenRun(() -> {
			this.progress.set(0.3);
		});
	}
	
	private CompletableFuture<Void> loadMedia() {
		return CompletableFuture.runAsync(() -> {
			this.message.set(Translations.get("task.loading.media"));
		}).thenCompose((v) -> {
			return this.context.dataManager.registerPersistAdapter(Media.class, new MediaPersistAdapter(Paths.get(Constants.MEDIA_ABSOLUTE_PATH), this.context.configuration));
		}).thenRun(() -> {
			this.progress.set(0.4);
		});
	}
	
	private CompletableFuture<Void> loadSlides() {
		return CompletableFuture.runAsync(() -> {
			this.message.set(Translations.get("task.loading.slide"));
		}).thenCompose((v) -> {
			return this.context.dataManager.registerPersistAdapter(Slide.class, new SlidePersistAdapter(Paths.get(Constants.SLIDES_ABSOLUTE_PATH), new JavaFXSlideRenderer(this.context), this.context.configuration));
		}).thenRun(() -> {
			this.progress.set(0.6);
		});
	}
	
	private CompletableFuture<Void> loadSlideShows() {
		return CompletableFuture.runAsync(() -> {
			this.message.set(Translations.get("task.loading.show"));
		}).thenCompose((v) -> {
			return this.context.dataManager.registerPersistAdapter(SlideShow.class, new SlideShowPersistAdapter(Paths.get(Constants.SLIDESHOWS_ABSOLUTE_PATH)));
		}).thenRun(() -> {
			this.progress.set(0.8);
		});
	}
	
	/**
	 * Starts the loading process on another thread.
	 * @throws IllegalStateException if start has already been called
	 */
	public CompletableFuture<Void> start() {
		return CompletableFuture.completedFuture(null)
		.thenCompose((v) -> {
			return this.loadBibles();
		}).thenCompose((v) -> {
			return this.loadSongs();
		}).thenCompose((v) -> {
			return this.loadMedia();
		}).thenCompose((v) -> {
			return this.loadSlides();
		}).thenCompose((v) -> {
			return this.loadSlideShows();
		});
	}
}
