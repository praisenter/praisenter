package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.javafx.FlowListView;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.Orientation;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.easing.EasingType;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public final class AnimationPane extends BorderPane {
	/** The configured animation */
	private final ObjectProperty<SlideAnimation> animation = new SimpleObjectProperty<SlideAnimation>() {
		public void set(SlideAnimation animation) {
			if (paint instanceof LinearGradient) {
	    		LinearGradient lg = (LinearGradient)paint;
	    		
	    		final double x1 = clamp(lg.getStartX(), 0, 1);
	    		final double y1 = clamp(lg.getStartY(), 0, 1);
	    		final double x2 = clamp(lg.getEndX(), 0, 1);
	    		final double y2 = clamp(lg.getEndY(), 0, 1);
	    		
	    		type.set(0);
	    		rdoLinear.setSelected(true);
	    		cycle.set(lg.getCycleMethod());
	    		switch (lg.getCycleMethod()) {
	    			case REFLECT:
	    				rdoCycleReflect.setSelected(true);
	    				break;
	    			case REPEAT:
	    				rdoCycleRepeat.setSelected(true);
	    				break;
					default:
	    				rdoCycleNone.setSelected(true);	
	    		}
	    		handle1X.set(x1);
	    		handle1Y.set(y1);
	    		handle2X.set(x2);
	    		handle2Y.set(y2);
	    		
	    		// stops
	    		List<Stop> stops = lg.getStops();
	    		if (stops != null && stops.size() > 0) {
	    			Stop s1 = stops.get(0);
	    			stop1.set(s1);
	    			sldStop1.setValue(s1.getOffset());
	    			pkrStop1.setValue(s1.getColor());
	    			if (stops.size() > 1) {
	    				Stop s2 = stops.get(1);
	    				stop2.set(s2);
	    				sldStop2.setValue(s2.getOffset());
	    				pkrStop2.setValue(s2.getColor());
	    			}
	    		}
	    		
	    		// set the handle locations
	    		handle1.setLayoutX(x1 * preview.getWidth());
	    		handle1.setLayoutY(y1 * preview.getHeight());
	    		handle2.setLayoutX(x2 * preview.getWidth());
	    		handle2.setLayoutY(y2 * preview.getHeight());
	    	} else if (paint instanceof RadialGradient) {
	    		RadialGradient rg = (RadialGradient)paint;
	    		
	    		final double sqrt2 = Math.sqrt(2.0);
	    		final double r = clamp(rg.getRadius(), 0, 1);
	    		final double x1 = clamp(rg.getCenterX(), 0, 1);
	    		final double y1 = clamp(rg.getCenterY(), 0, 1);
	    		final double x2 = clamp(x1 + sqrt2 * r, 0, 1);
	    		final double y2 = clamp(y1 + sqrt2 * r, 0, 1);
	    		
	    		type.set(1);
	    		rdoRadial.setSelected(true);
	    		cycle.set(rg.getCycleMethod());
	    		switch (rg.getCycleMethod()) {
	    			case REFLECT:
	    				rdoCycleReflect.setSelected(true);
	    				break;
	    			case REPEAT:
	    				rdoCycleRepeat.setSelected(true);
	    				break;
    				default:
	    				rdoCycleNone.setSelected(true);	
	    		}
	    		handle1X.set(x1);
	    		handle1Y.set(y1);
	    		handle2X.set(x2);
	    		handle2Y.set(y2);
	    		
	    		// we have to recompute the radius since the radius could
	    		// be bigger than what we allow (for example if the center
	    		// is at (0.5, 0.5) the max radius is sqrt(0.5) instead of 1
	    		final double d1 = x2 - x1;
	    		final double d2 = y2 - y1;
	    		radius.set(Math.sqrt(d1 * d1 + d2 * d2));
	    		
	    		// stops
	    		List<Stop> stops = rg.getStops();
	    		if (stops != null && stops.size() > 0) {
	    			Stop s1 = stops.get(0);
	    			stop1.set(s1);
	    			sldStop1.setValue(s1.getOffset());
	    			pkrStop1.setValue(s1.getColor());
	    			if (stops.size() > 1) {
	    				Stop s2 = stops.get(1);
	    				stop2.set(s2);
	    				sldStop2.setValue(s2.getOffset());
	    				pkrStop2.setValue(s2.getColor());
	    			}
	    		}
	    		
	    		// set the handle locations
	    		handle1.setLayoutX(x1 * preview.getWidth());
	    		handle1.setLayoutY(y1 * preview.getHeight());
	    		handle2.setLayoutX(x2 * preview.getWidth());
	    		handle2.setLayoutY(y2 * preview.getHeight());
	    	}
			
			// doing this will mean that setting it to null or any other type of paint will do nothing
			// since it will just use the current observables to generate a new paint
			
			// this has the added effect of allowing us to update the paint property without having
			// it go through the conversion process above by calling: set(null);
			super.set(createPaint());
		}
	};
	
	// sub properties of the animation
	
	private final ObjectProperty<AnimationOption> animationOption = new SimpleObjectProperty<AnimationOption>();
	private final ObjectProperty<AnimationOption> easingOption = new SimpleObjectProperty<AnimationOption>();
	
	private final ObjectProperty<AnimationType> animationType = new SimpleObjectProperty<AnimationType>();
	private final ObjectProperty<EasingType> easingType = new SimpleObjectProperty<EasingType>();
	
	private final LongProperty duration = new SimpleLongProperty();
	private final LongProperty delay = new SimpleLongProperty();
	
	private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<Orientation>();
	private final ObjectProperty<Direction> direction = new SimpleObjectProperty<Direction>();
	private final ObjectProperty<ShapeType> shapeType = new SimpleObjectProperty<ShapeType>();
	private final ObjectProperty<Operation> operation = new SimpleObjectProperty<Operation>();
	
	// nodes
	
	TextField txtDuration;
	TextField txtDelay;
	ChoiceBox<AnimationType> cbAnimationType;
	ChoiceBox<EasingType> cbEasingType;
	ChoiceBox<Orientation> cbOrientation;
	ChoiceBox<Direction> cbDirection;
	ChoiceBox<ShapeType> cbShapeType;
	ChoiceBox<Operation> cbOperation;
	
	public AnimationPane() {
		// setup the animation selection
		List<AnimationOption> animationOptions = new ArrayList<AnimationOption>(Transitions.getAnimationOptions());
		List<AnimationOption> easingOptions = new ArrayList<AnimationOption>(Transitions.getEasingOptions());
		
		FlowListView<AnimationOption> aniListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		aniListPane.itemsProperty().set(FXCollections.observableArrayList(animationOptions));
		aniListPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
		
		FlowListView<AnimationOption> easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		
		// setup the animation config
		
		TextField txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		TextField txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		// TODO limit options based on what object we are configuring it for (slide or component); for a slide we only want animation type in
		// TODO limit options based on what animation we are configuring (fade/swap/etc)
		ChoiceBox<AnimationType> cbAnimationType = new ChoiceBox<>(FXCollections.observableArrayList(AnimationType.values()));
		ChoiceBox<EasingType> cbEasingType = new ChoiceBox<>(FXCollections.observableArrayList(EasingType.values()));
		ChoiceBox<Orientation> cbOrientation = new ChoiceBox<>(FXCollections.observableArrayList(Orientation.values()));
		ChoiceBox<Direction> cbDirection = new ChoiceBox<>(FXCollections.observableArrayList(Direction.values()));
		ChoiceBox<ShapeType> cbShapeType = new ChoiceBox<>(FXCollections.observableArrayList(ShapeType.values()));
		ChoiceBox<Operation> cbOperation = new ChoiceBox<>(FXCollections.observableArrayList(Operation.values()));
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(3);
		grid.setVgap(3);
		
		// animation options
		
		grid.add(new Label("Duration"), 0, 0);
		grid.add(txtDuration, 1, 0);
		
		grid.add(new Label("Delay"), 0, 1);
		grid.add(txtDelay, 1, 1);
		
		grid.add(new Label("Animation Type"), 0, 2);
		grid.add(cbAnimationType, 1, 2);
		
		grid.add(new Label("Orientation"), 0, 3);
		grid.add(cbOrientation, 1, 3);
		
		grid.add(new Label("Direction"), 0, 4);
		grid.add(cbDirection, 1, 4);
		
		grid.add(new Label("Shape Type"), 0, 5);
		grid.add(cbShapeType, 1, 5);
		
		grid.add(new Label("Operation"), 0, 6);
		grid.add(cbOperation, 1, 6);
		
		// easing options
		
		grid.add(new Label("Easing Type"), 0, 7);
		grid.add(cbEasingType, 1, 7);
		
		ScrollPane scrAnimations = new ScrollPane(aniListPane);
		scrAnimations.setFitToWidth(true);
		scrAnimations.setPrefHeight(300);
		
		ScrollPane scrEasings = new ScrollPane(easingListPane);
		scrEasings.setFitToHeight(true);
		scrEasings.setPrefHeight(115);
		
		TitledPane ttlProperties = new TitledPane("Settings", grid);
		ttlProperties.prefHeightProperty().bind(scrAnimations.heightProperty());
		
		this.setCenter(scrAnimations);
		this.setRight(ttlProperties);
		this.setBottom(scrEasings);
	}
}
