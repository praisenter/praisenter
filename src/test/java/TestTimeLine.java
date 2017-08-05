import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.easing.Bounce;
import org.praisenter.slide.easing.Circular;
import org.praisenter.slide.easing.Cubic;
import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.EasingType;
import org.praisenter.slide.easing.Exponential;
import org.praisenter.slide.easing.Quintic;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestTimeLine extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		Pane root = new Pane();
		
		Pane box = new Pane();
		box.setMinHeight(100);
		box.setMinWidth(100);
		box.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		
		root.getChildren().add(box);
		
		primaryStage.setScene(new Scene(root));
		primaryStage.setMinHeight(200);
		primaryStage.setMinWidth(200);
		primaryStage.show();
		
		Timeline t1 = new Timeline(
				new KeyFrame(Duration.millis(2000), new KeyValue(box.translateXProperty(), 50)));
		
//		Timeline t2 = new Timeline(
//				// at t-1000 I want the opacity to be the current value
//				new KeyFrame(Duration.millis(1000), new KeyValue(box.opacityProperty(), 1)),
//				// at t-1000 I want the opacity to be 0 (immediately changed)
//				new KeyFrame(Duration.millis(1000), new KeyValue(box.opacityProperty(), 0)),
//				// from t-1000 to t-2000 the opacity should change from 0 to 1
//				new KeyFrame(Duration.millis(2000), new KeyValue(box.opacityProperty(), 1)));
		
		Timeline t2 = new Timeline(
				// at t-1000 I want the opacity to be the current value
//				new KeyFrame(Duration.millis(500), new KeyValue(box.blendModeProperty(), box.getBlendMode())),
				// at t-1000 I want the opacity to be 0 (immediately changed)
				//new KeyFrame(Duration.millis(500), new KeyValue(box.clipProperty(), new Rectangle(0,0,0,0))),
				// from t-1000 to t-2000 the opacity should change from 0 to 1
				new KeyFrame(Duration.millis(500), new KeyValue(box.clipProperty(), box.getClip(), new Interpolator() {
					Easing e = new Bounce(EasingType.OUT);
					
					@Override
					protected double curve(double t) {
						return t;
					}
					
					@Override
					public Object interpolate(Object startValue, Object endValue, double fraction) {
						return getBlinds(box.getWidth(), box.getHeight(), e.curve(fraction));
					}
					
					private Shape getBlinds(double w, double h, double frac) {
						Shape clip = null;
//						if (this.animation.getType() == AnimationType.IN) {
							clip = new Rectangle(0, 0, w, h);
//						} else {
//							clip = new Rectangle();
//						}
						
						// compute the number of blinds
						final int blinds = 4;//this.animation.getBlindCount();
						double x = 0;
						// compute the blind width
						double bw = w / blinds;
						// compute the area that needs to be painted by either removing
						// vertical bars or adding vertical bars
						for (int i = 0; i < blinds; i++) {
							Rectangle blind = new Rectangle(x + bw * frac, 0, bw * (1.0 - frac), h);
//							if (this.animation.getType() == AnimationType.IN) {
								clip = Shape.subtract(clip, blind);
//							} else {
//								clip = Shape.union(clip, blind);
//							}
							x += bw;
						}
						System.out.println(clip);
						return clip;
					}
				})));
		
//		t2.setAutoReverse(true);
//		t2.setCycleCount(9);
		
//		PauseTransition pt = new PauseTransition(Duration.millis(500));
//		pt.setDelay(Duration.millis(500));
		//t2.setDelay(Duration.millis(500));
		
		// SOLUTION
		// basically the only solution for delay that I can find is to place the timeline of the animation
		// inside a sequential transition with a delay on it
		SequentialTransition sq = new SequentialTransition(t2);
		sq.setDelay(Duration.millis(500));
		
		Transition animation = new SequentialTransition(
				new PauseTransition(Duration.millis(1000)), 
				new ParallelTransition(t1, sq));
		
		animation.play();
	}
}
