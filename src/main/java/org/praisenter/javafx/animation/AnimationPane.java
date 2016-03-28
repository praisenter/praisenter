package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.praisenter.javafx.FlowListView;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.Orientation;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.EasingType;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public final class AnimationPane extends BorderPane {
	/** The configured animation */
	private final ObjectProperty<SlideAnimation> animation = new SimpleObjectProperty<SlideAnimation>() {
		public void set(SlideAnimation animation) {
			if (animation != null) {
	    		// assign all the controls their values
	    	}
			
			// when set to null, build it from the control values
			SlideAnimation ani = createAnimation();
			// check for null (we don't have enough info to build one)
			if (ani == null) return;
			// set it
			super.set(ani);
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
	
	ComboBox<AnimatableObject> cmbObjects;
	FlowListView<AnimationOption> aniListPane;
	FlowListView<AnimationOption> easingListPane;
	TextField txtDuration;
	TextField txtDelay;
	ChoiceBox<AnimationType> cbAnimationType;
	ChoiceBox<EasingType> cbEasingType;
	ChoiceBox<Orientation> cbOrientation;
	ChoiceBox<Direction> cbDirection;
	ChoiceBox<ShapeType> cbShapeType;
	ChoiceBox<Operation> cbOperation;
	
	public AnimationPane(ObservableSet<AnimatableObject> objects) {
		// TODO may not want to show this if sent only one to choose from
		cmbObjects = new ComboBox<>(FXCollections.observableArrayList(objects));
		
		// setup the animation selection
		List<AnimationOption> animationOptions = new ArrayList<AnimationOption>(Transitions.getAnimationOptions());
		List<AnimationOption> easingOptions = new ArrayList<AnimationOption>(Transitions.getEasingOptions());
		
		aniListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		aniListPane.itemsProperty().set(FXCollections.observableArrayList(animationOptions));
		aniListPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
		
		aniListPane.selectionProperty().addListener((obs, ov, nv) -> {
			// hide controls
			cbOrientation.setVisible(false);
			cbDirection.setVisible(false);
			cbShapeType.setVisible(false);
			cbOperation.setVisible(false);
			// hide show based on animation type
			if (nv != null) {
				Class<?> type = nv.getType();
				if (Blinds.class.isAssignableFrom(type)) {
					cbOrientation.setVisible(true);
				} else if (Push.class.isAssignableFrom(type)) {
					cbDirection.setVisible(true);
				} else if (Shaped.class.isAssignableFrom(type)) {
					cbOperation.setVisible(true);
					cbShapeType.setVisible(true);
				} else if (Split.class.isAssignableFrom(type)) {
					cbOperation.setVisible(true);
					cbOrientation.setVisible(true);
				} else if (Swipe.class.isAssignableFrom(type)) {
					cbDirection.setVisible(true);
				}
				// otherwise all the options remain hidden
			}
		});
		
		easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		
		// setup the animation config
		
		txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		
		txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		// TODO limit options based on what object we are configuring it for (slide or component); for a slide we only want animation type in
		// TODO limit options based on what animation we are configuring (fade/swap/etc)
		cbAnimationType = new ChoiceBox<>(FXCollections.observableArrayList(AnimationType.values()));
		cbEasingType = new ChoiceBox<>(FXCollections.observableArrayList(EasingType.values()));
		cbOrientation = new ChoiceBox<>(FXCollections.observableArrayList(Orientation.values()));
		cbDirection = new ChoiceBox<>(FXCollections.observableArrayList(Direction.values()));
		cbShapeType = new ChoiceBox<>(FXCollections.observableArrayList(ShapeType.values()));
		cbOperation = new ChoiceBox<>(FXCollections.observableArrayList(Operation.values()));
		
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
	
	private SlideAnimation createAnimation() {
		AnimationOption animationOption = this.aniListPane.selectionProperty().get();
		if (animationOption == null) {
			// TODO what do we do if the user hasn't selected an animation?
			return null;
		}
		
		AnimationOption easingOption = this.easingListPane.selectionProperty().get();
		if (easingOption == null) {
			// TODO what do we do if the user hasn't selected an easing?
			return null;
		}
		
		Class<?> animationClass = animationOption.getType();
		Class<?> easingClass = easingOption.getType();
		try {
			// animation
			SlideAnimation animation = (SlideAnimation)animationClass.newInstance();
			animation.setDelay(this.txtDelay.getText());
			animation.setDuration(this.txtDuration.getText());
			animation.setId(this.cmbObjects.getValue().getObjectId());
			animation.setType(this.cbAnimationType.getValue());
			
			// custom animation options
			if (animation instanceof Blinds) {
				Blinds a = (Blinds)animation;
				a.setOrientation(this.orientation.get());
			} else if (animation instanceof Push) {
				Push a = (Push)animation;
				a.setDirection(this.direction.get());
			} else if (animation instanceof Shaped) {
				Shaped a = (Shaped)animation;
				a.setOperation(this.operation.get());
				a.setShapeType(this.shapeType.get());
			} else if (animation instanceof Split) {
				Split a = (Split)animation;
				a.setOrientation(this.orientation.get());
				a.setOperation(this.operation.get());
			} else if (animation instanceof Swipe) {
				Swipe a = (Swipe)animation;
				a.setDirection(this.direction.get());
			} else {
				// Swap/Fade/Zoom don't have extra options at this time
			}
			
			// easing
			Easing easing = (Easing)easingClass.newInstance();
			easing.setType(this.cbEasingType.getValue());
			animation.setEasing(easing);
			
			return animation;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
