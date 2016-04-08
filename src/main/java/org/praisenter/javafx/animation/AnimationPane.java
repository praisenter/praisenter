package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.IntegerTextFormatter;
import org.praisenter.javafx.LongTextFormatter;
import org.praisenter.javafx.utility.FxFactory;
import org.praisenter.javafx.utility.JavaFxNodeHelper;
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
import org.praisenter.slide.easing.Linear;

import javafx.animation.Transition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public final class AnimationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final double PREVIEW_WIDTH = 214;
	private static final double PREVIEW_HEIGHT = 120;
	
	/** The configured animation */
	private final ObjectProperty<SlideAnimation> animation = new SimpleObjectProperty<SlideAnimation>() {
		public void set(SlideAnimation animation) {
			if (animation != null) {
	    		// assign all the controls their values
				// FIXME we don't know the animated object type at this time
				cmbObjects.setValue(new AnimatedObject(animation.getId(), AnimatedObjectType.COMPONENT, "test"));
				animationListPane.selectionProperty().set(new AnimationOption(animation.getClass(), 0));
				easingListPane.selectionProperty().set(new AnimationOption(animation.getEasing().getClass(), 0));
				txtDuration.setText(String.valueOf(animation.getDuration()));
				txtDelay.setText(String.valueOf(animation.getDelay()));
				cbAnimationType.setValue(animation.getType());
				cbEasingType.setValue(animation.getEasing().getType());
				// specific animation settings
				// custom animation options
				if (animation instanceof Blinds) {
					Blinds a = (Blinds)animation;
					cbOrientation.setValue(a.getOrientation());
					txtBlindCount.setText(String.valueOf(a.getBlindCount()));
				} else if (animation instanceof Push) {
					Push a = (Push)animation;
					cbDirection.setValue(a.getDirection());
				} else if (animation instanceof Shaped) {
					Shaped a = (Shaped)animation;
					cbOperation.setValue(a.getOperation());
					cbShapeType.setValue(a.getShapeType());
				} else if (animation instanceof Split) {
					Split a = (Split)animation;
					cbOrientation.setValue(a.getOrientation());
					cbOperation.setValue(a.getOperation());
				} else if (animation instanceof Swipe) {
					Swipe a = (Swipe)animation;
					cbDirection.setValue(a.getDirection());
				} else if (animation instanceof Swap ||
						   animation instanceof Fade ||
						   animation instanceof Zoom) {
					// Swap/Fade/Zoom don't have extra options at this time
				} else {
					LOGGER.warn("Unhandled SlideAnimation type " + animation.getClass().getName());
				}
					
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
	
	private final IntegerProperty blindCount = new SimpleIntegerProperty();
	
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
	Label lblBlindCount;
	
	ComboBox<AnimatedObject> cmbObjects;
	FlowListView<AnimationOption> animationListPane;
	FlowListView<AnimationOption> easingListPane;
	TextField txtDuration;
	TextField txtDelay;
	ChoiceBox<AnimationType> cbAnimationType;
	ChoiceBox<EasingType> cbEasingType;
	ChoiceBox<Orientation> cbOrientation;
	ChoiceBox<Direction> cbDirection;
	ChoiceBox<ShapeType> cbShapeType;
	ChoiceBox<Operation> cbOperation;
	TextField txtBlindCount;
	
	// preview
	
	Pane panePreview;
	Pane pane1;
	Pane pane2;
	Transition transition;
	
	public AnimationPane(ObservableSet<AnimatedObject> objects) {
		FilteredList<Direction> directions = new FilteredList<Direction>(FXCollections.observableArrayList(Direction.values()));
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(3);
		grid.setVgap(3);
		
		ObservableList<AnimatedObject> objs = FXCollections.observableArrayList(objects);
		
		lblObjects = new Label("For");
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
		
		animationListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		animationListPane.itemsProperty().set(FXCollections.observableArrayList(animationOptions));
		animationListPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
		animationListPane.selectionProperty().set(new AnimationOption(Swap.class, 0));
		animationOption.bind(animationListPane.selectionProperty());
		animationListPane.selectionProperty().addListener((obs, ov, nv) -> {
			// remove controls
			grid.getChildren().removeAll(
					lblDirection, cbDirection,
					lblOrientation, cbOrientation,
					lblOperation, cbOperation,
					lblShapeType, cbShapeType,
					lblBlindCount, txtBlindCount);
			// hide show based on animation type
			if (nv != null) {
				Class<?> type = nv.getType();
				if (Blinds.class.isAssignableFrom(type)) {
					grid.add(lblOrientation, 0, 5);
					grid.add(cbOrientation, 1, 5);
					grid.add(lblBlindCount, 0, 6);
					grid.add(txtBlindCount, 1, 6);
				} else if (Push.class.isAssignableFrom(type)) {
					grid.add(lblDirection, 0, 5);
					grid.add(cbDirection, 1, 5);
					directions.setPredicate((f) -> {
						return f == Direction.UP || f == Direction.RIGHT || f == Direction.LEFT || f == Direction.DOWN;
					});
					cbDirection.setValue(Direction.UP);
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
					cbDirection.setValue(Direction.UP);
				}
				// otherwise all the options remain hidden
			}
			
			animation.set(null);
		});
		
		easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		easingListPane.selectionProperty().set(new AnimationOption(Linear.class, 0));
		easingOption.bind(easingListPane.selectionProperty());
		easingListPane.selectionProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		// setup the animation config
		
		lblDuration = new Label("Duration");
		txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		LongTextFormatter durationFormatter = new LongTextFormatter();
		duration.bind(durationFormatter.valueProperty());
		txtDuration.setTextFormatter(durationFormatter);
		durationFormatter.setValue(500l);
		durationFormatter.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblDelay = new Label("Delay");
		txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		LongTextFormatter delayFormatter = new LongTextFormatter();
		delay.bind(delayFormatter.valueProperty());
		txtDelay.setTextFormatter(delayFormatter);
		delayFormatter.setValue(0l);
		delayFormatter.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblAnimationType = new Label("Animation Type");
		cbAnimationType = new ChoiceBox<>(FXCollections.observableArrayList(AnimationType.values()));
		cbAnimationType.setValue(AnimationType.IN);
		animationType.bind(cbAnimationType.valueProperty());
		cbAnimationType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblEasingType = new Label("Easing Type");
		cbEasingType = new ChoiceBox<>(FXCollections.observableArrayList(EasingType.values()));
		cbEasingType.setValue(EasingType.IN);
		easingType.bind(cbEasingType.valueProperty());
		cbEasingType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
				
		lblOrientation = new Label("Orientation");
		cbOrientation = new ChoiceBox<>(FXCollections.observableArrayList(Orientation.values()));
		cbOrientation.setValue(Orientation.HORIZONTAL);
		orientation.bind(cbOrientation.valueProperty());
		cbOrientation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblDirection = new Label("Direction");
		cbDirection = new ChoiceBox<>(directions);
		cbDirection.setValue(Direction.UP);
		cbDirection.setPrefWidth(100);
		direction.bind(cbDirection.valueProperty());
		cbDirection.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblShapeType = new Label("Shape Type");
		cbShapeType = new ChoiceBox<>(FXCollections.observableArrayList(ShapeType.values()));
		cbShapeType.setValue(ShapeType.CIRCLE);
		shapeType.bind(cbShapeType.valueProperty());
		cbShapeType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblOperation = new Label("Operation");
		cbOperation = new ChoiceBox<>(FXCollections.observableArrayList(Operation.values()));
		cbOperation.setValue(Operation.COLLAPSE);
		operation.bind(cbOperation.valueProperty());
		cbOperation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		lblBlindCount = new Label("Blind Count");
		txtBlindCount = new TextField();
		txtBlindCount.setPromptText("number of blinds");
		IntegerTextFormatter blindCountFormatter = new IntegerTextFormatter();
		blindCount.bind(blindCountFormatter.valueProperty());
		txtBlindCount.setTextFormatter(blindCountFormatter);
		blindCountFormatter.setValue(12);
		blindCountFormatter.valueProperty().addListener((obs, ov, nv) -> {
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
		
		panePreview = new Pane();
		JavaFxNodeHelper.setSize(panePreview, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		panePreview.setBorder(FxFactory.newBorder(Color.BLACK));
		
		pane1 = new Pane();
		pane1.setBorder(FxFactory.newBorder(Color.BLACK));
		pane1.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255, 0.5), null, null)));
		JavaFxNodeHelper.setSize(pane1, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		pane2 = new StackPane();
		pane2.setBorder(FxFactory.newBorder(Color.BLACK));
		pane2.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)));
		Text content = new Text("Content");
		content.setFill(Color.WHITE);
		pane2.getChildren().add(content);
		JavaFxNodeHelper.setSize(pane2, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		panePreview.getChildren().addAll(pane1, pane2);
		
		ScrollPane scrAnimations = new ScrollPane(animationListPane);
		scrAnimations.setFitToWidth(true);
		scrAnimations.setPrefHeight(300);
		
		ScrollPane scrEasings = new ScrollPane(easingListPane);
		scrEasings.setFitToHeight(true);
		scrEasings.setPrefHeight(115);

		BorderPane left = new BorderPane();
		left.setCenter(scrAnimations);
		left.setBottom(scrEasings);
		
		VBox boxPreview = new VBox();
		Button btnPreview = new Button("Preview");
		btnPreview.setOnAction((e) -> {
			SlideAnimation animation = this.animation.get();
			CustomTransition<?> ct = Transitions.createCustomTransition(animation);
			ct.setNode(pane2);
			if (transition != null) {
				transition.stop();
			}
			transition = ct;
			ct.play();
		});
		boxPreview.getChildren().addAll(btnPreview, panePreview);
		
		TitledPane ttlProperties = new TitledPane("Settings", grid);
		TitledPane ttlPreview = new TitledPane("Preview", boxPreview);
		VBox right = new VBox();
		right.getChildren().addAll(ttlProperties, ttlPreview);
		
		this.setCenter(left);
		this.setRight(right);
	}
	
	private SlideAnimation createAnimation() {
		AnimationOption animationOption = this.animationListPane.selectionProperty().get();
		if (animationOption == null) {
			// return null if an animation type hasn't been selected
			return null;
		}
		
		AnimationOption easingOption = this.easingListPane.selectionProperty().get();
		if (easingOption == null) {
			// return null if an easing hasn't been selected
			return null;
		}
		
		Class<?> animationClass = animationOption.getType();
		Class<?> easingClass = easingOption.getType();
		try {
			// animation
			SlideAnimation animation = (SlideAnimation)animationClass.newInstance();
			animation.setDelay(this.delay.get());
			animation.setDuration(this.duration.get());
			animation.setId(this.cmbObjects.getValue().getObjectId());
			animation.setType(this.cbAnimationType.getValue());
			
			// custom animation options
			if (animation instanceof Blinds) {
				Blinds a = (Blinds)animation;
				a.setOrientation(this.orientation.get());
				a.setBlindCount(this.blindCount.get());
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
				LOGGER.warn("Unhandled SlideAnimation type " + animation.getClass().getName());
			}
			
			// easing
			Easing easing = (Easing)easingClass.newInstance();
			easing.setType(this.cbEasingType.getValue());
			animation.setEasing(easing);
			
			return animation;
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.warn("Failed to create a new animation/easing of type " + animationClass.getName() + "/" + easingClass.getName() + ". The class must have a zero-argument constructor.");
		}
		return null;
	}

	public ObjectProperty<SlideAnimation> animationProperty() {
		return this.animation;
	}
	
	public SlideAnimation getAnimation() {
		return this.animation.get();
	}
	
	public void setAnimation(SlideAnimation animation) {
		this.animation.set(animation);
	}
}
