package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.omg.CosNaming.Binding;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.LongTextFormatter;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.Orientation;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.animation.Zoom;
import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.EasingType;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
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
		public void setValue(SlideAnimation animation) {
			set(animation);
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
	
	Label lblObjects;
	Label lblDuration;
	Label lblDelay;
	Label lblAnimationType;
	Label lblEasingType;
	Label lblOrientation;
	Label lblDirection;
	Label lblShapeType;
	Label lblOperation;
	
	ComboBox<AnimatedObject> cmbObjects;
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
	
	public AnimationPane(ObservableSet<AnimatedObject> objects) {
		FilteredList<Direction> directions = new FilteredList<Direction>(FXCollections.observableArrayList(Direction.values()));
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(3);
		grid.setVgap(3);
		
		ObservableList<AnimatedObject> objs = FXCollections.observableArrayList(objects);
		
		lblObjects = new Label("Object");
		cmbObjects = new ComboBox<>(objs);
		
		if (objects.size() == 0) {
			// TODO throw error, we should always at least have the slide
		}
		if (objects.size() == 1) {
			cmbObjects.setValue(objs.get(0));
			cmbObjects.setDisable(true);
		}
		
		cmbObjects.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		// setup the animation selection
		List<AnimationOption> animationOptions = new ArrayList<AnimationOption>(Transitions.getAnimationOptions());
		List<AnimationOption> easingOptions = new ArrayList<AnimationOption>(Transitions.getEasingOptions());
		
		aniListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		aniListPane.itemsProperty().set(FXCollections.observableArrayList(animationOptions));
		aniListPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
		
		aniListPane.selectionProperty().addListener((obs, ov, nv) -> {
			// remove controls
			grid.getChildren().removeAll(
					lblDirection, cbDirection,
					lblOrientation, cbOrientation,
					lblOperation, cbOperation,
					lblShapeType, cbShapeType);
			// hide show based on animation type
			if (nv != null) {
				Class<?> type = nv.getType();
				if (Blinds.class.isAssignableFrom(type)) {
					grid.add(lblOrientation, 0, 5);
					grid.add(cbOrientation, 1, 5);
				} else if (Push.class.isAssignableFrom(type)) {
					grid.add(lblDirection, 0, 5);
					grid.add(cbDirection, 1, 5);
					directions.setPredicate((f) -> {
						return f == Direction.UP || f == Direction.RIGHT || f == Direction.LEFT || f == Direction.DOWN;
					});
				} else if (Shaped.class.isAssignableFrom(type)) {
					grid.add(lblShapeType, 0, 5);
					grid.add(cbShapeType, 1, 5);
					grid.add(lblOperation, 0, 6);
					grid.add(cbOperation, 1, 6);
				} else if (Split.class.isAssignableFrom(type)) {
					grid.add(lblOrientation, 0, 5);
					grid.add(cbOrientation, 1, 5);
					grid.add(lblOperation, 0, 6);
					grid.add(cbOperation, 1, 6);
				} else if (Swipe.class.isAssignableFrom(type)) {
					grid.add(lblDirection, 0, 5);
					grid.add(cbDirection, 1, 5);
					directions.setPredicate((f) -> { return true; });
				}
				// otherwise all the options remain hidden
			}
			
			animation.set(null);
		});
		
		easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		easingListPane.selectionProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		// setup the animation config
		
		lblDuration = new Label("Duration");
		txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		txtDuration.setTextFormatter(new LongTextFormatter());
		txtDuration.textProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblDelay = new Label("Delay");
		txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		txtDelay.setTextFormatter(new LongTextFormatter());
		txtDelay.textProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblAnimationType = new Label("Animation Type");
		lblAnimationType.managedProperty().bind(lblAnimationType.visibleProperty());
		cbAnimationType = new ChoiceBox<>(FXCollections.observableArrayList(AnimationType.values()));
		cbAnimationType.managedProperty().bind(cbAnimationType.visibleProperty());
		cbAnimationType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblEasingType = new Label("Easing Type");
		cbEasingType = new ChoiceBox<>(FXCollections.observableArrayList(EasingType.values()));
		cbEasingType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
				
		lblOrientation = new Label("Orientation");
		lblOrientation.managedProperty().bind(lblOrientation.visibleProperty());
		cbOrientation = new ChoiceBox<>(FXCollections.observableArrayList(Orientation.values()));
		cbOrientation.managedProperty().bind(cbOrientation.visibleProperty());
		cbOrientation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblDirection = new Label("Direction");
		cbDirection = new ChoiceBox<>(directions);
		cbDirection.managedProperty().bind(cbDirection.visibleProperty());
		cbDirection.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblShapeType = new Label("Shape Type");
		cbShapeType = new ChoiceBox<>(FXCollections.observableArrayList(ShapeType.values()));
		cbShapeType.managedProperty().bind(cbShapeType.visibleProperty());
		cbShapeType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblOperation = new Label("Operation");
		cbOperation = new ChoiceBox<>(FXCollections.observableArrayList(Operation.values()));
		cbOperation.managedProperty().bind(cbOperation.visibleProperty());
		cbOperation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		grid.add(lblObjects, 0, 0);
		grid.add(cmbObjects, 1, 0);
		
		grid.add(lblDuration, 0, 1);
		grid.add(txtDuration, 1, 1);
		
		grid.add(lblDelay, 0, 2);
		grid.add(txtDelay, 1, 2);
		
		grid.add(lblAnimationType, 0, 3);
		grid.add(cbAnimationType, 1, 3);
		
		grid.add(lblEasingType, 0, 4);
		grid.add(cbEasingType, 1, 4);
		
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
			animation.setDelay((long)this.txtDelay.getTextFormatter().getValue());
			animation.setDuration((long)this.txtDuration.getTextFormatter().getValue());
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
			} else if (animation instanceof Swap ||
					   animation instanceof Fade ||
					   animation instanceof Zoom) {
				// Swap/Fade/Zoom don't have extra options at this time
			} else {
				// TODO log warning
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
