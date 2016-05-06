package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;

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
import javafx.geometry.Rectangle2D;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.IntegerTextFormatter;
import org.praisenter.javafx.LongTextFormatter;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
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

public final class AnimationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final double PREVIEW_WIDTH = 214;
	private static final double PREVIEW_HEIGHT = 120;
	
	private final FilteredList<Option<AnimationType>> ANIMATION_TYPE_OPTIONS = getOptions(AnimationType.class);
	private final FilteredList<Option<EasingType>> EASING_TYPE_OPTIONS = getOptions(EasingType.class);
	private final FilteredList<Option<Orientation>> ORIENTATION_OPTIONS = getOptions(Orientation.class);
	private final FilteredList<Option<Operation>> OPERATION_OPTIONS = getOptions(Operation.class);
	private final FilteredList<Option<ShapeType>> SHAPE_TYPE_OPTIONS = getOptions(ShapeType.class);
	private final FilteredList<Option<Direction>> DIRECTION_OPTIONS = getOptions(Direction.class);
	
	private static <T extends Enum<T>> FilteredList<Option<T>> getOptions(Class<T> clazz) {
		List<Option<T>> options = new ArrayList<Option<T>>();
		T[] values = clazz.getEnumConstants();
		for (T value : values) {
			options.add(new Option<T>(Translations.get(clazz.getName() + "." + value.name()), value));
		}
		return new FilteredList<Option<T>>(FXCollections.observableArrayList(options));
	}
	
	private static <T> Option<T> getOption(FilteredList<Option<T>> options, T value) {
		for (Option<T> option : options) {
			if (option.getValue() == value) {
				return option;
			}
		}
		return options.get(0);
	}
	
	private boolean setting = false;
	
	/** The configured animation */
	private final ObjectProperty<SlideAnimation> animation = new SimpleObjectProperty<SlideAnimation>() {
		public void set(SlideAnimation animation) {
			if (setting) return;
			setting = true;
			if (animation != null) {
	    		// assign all the controls their values
				// FIXME we don't know the animated object type at this time
				cmbObjects.setValue(new AnimatedObject(animation.getId(), AnimatedObjectType.COMPONENT, "test"));
				animationListPane.selectionProperty().set(new AnimationOption(animation.getClass(), 0));
				easingListPane.selectionProperty().set(new AnimationOption(animation.getEasing().getClass(), 0));
				txtDuration.setText(String.valueOf(animation.getDuration()));
				txtDelay.setText(String.valueOf(animation.getDelay()));
				cbAnimationType.setValue(getOption(ANIMATION_TYPE_OPTIONS, animation.getType()));
				cbEasingType.setValue(getOption(EASING_TYPE_OPTIONS, animation.getEasing().getType()));
				// specific animation settings
				// custom animation options
				if (animation instanceof Blinds) {
					Blinds a = (Blinds)animation;
					cbOrientation.setValue(getOption(ORIENTATION_OPTIONS, a.getOrientation()));
					txtBlindCount.setText(String.valueOf(a.getBlindCount()));
				} else if (animation instanceof Push) {
					Push a = (Push)animation;
					cbDirection.setValue(getOption(DIRECTION_OPTIONS, a.getDirection()));
				} else if (animation instanceof Shaped) {
					Shaped a = (Shaped)animation;
					cbOperation.setValue(getOption(OPERATION_OPTIONS, a.getOperation()));
					cbShapeType.setValue(getOption(SHAPE_TYPE_OPTIONS, a.getShapeType()));
				} else if (animation instanceof Split) {
					Split a = (Split)animation;
					cbOrientation.setValue(getOption(ORIENTATION_OPTIONS, a.getOrientation()));
					cbOperation.setValue(getOption(OPERATION_OPTIONS, a.getOperation()));
				} else if (animation instanceof Swipe) {
					Swipe a = (Swipe)animation;
					cbDirection.setValue(getOption(DIRECTION_OPTIONS, a.getDirection()));
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
			setting = false;
		}
		public void setValue(SlideAnimation animation) {
			set(animation);
		}
	};
	
	// sub properties of the animation
	
	private final ObjectProperty<AnimationOption> animationOption = new SimpleObjectProperty<AnimationOption>();
	private final ObjectProperty<AnimationOption> easingOption = new SimpleObjectProperty<AnimationOption>();
	
	private final ObjectProperty<Option<AnimationType>> animationType = new SimpleObjectProperty<Option<AnimationType>>();
	private final ObjectProperty<Option<EasingType>> easingType = new SimpleObjectProperty<Option<EasingType>>();
	
	private final LongProperty duration = new SimpleLongProperty();
	private final LongProperty delay = new SimpleLongProperty();
	
	private final ObjectProperty<Option<Orientation>> orientation = new SimpleObjectProperty<Option<Orientation>>();
	private final ObjectProperty<Option<Direction>> direction = new SimpleObjectProperty<Option<Direction>>();
	private final ObjectProperty<Option<ShapeType>> shapeType = new SimpleObjectProperty<Option<ShapeType>>();
	private final ObjectProperty<Option<Operation>> operation = new SimpleObjectProperty<Option<Operation>>();
	
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
	ChoiceBox<Option<AnimationType>> cbAnimationType;
	ChoiceBox<Option<EasingType>> cbEasingType;
	ChoiceBox<Option<Orientation>> cbOrientation;
	ChoiceBox<Option<Direction>> cbDirection;
	ChoiceBox<Option<ShapeType>> cbShapeType;
	ChoiceBox<Option<Operation>> cbOperation;
	TextField txtBlindCount;
	
	// preview
	
	Pane panePreview;
	Pane pane1;
	Pane pane2;
	Transition transition;
	
	public AnimationPane(ObservableSet<AnimatedObject> objects) {
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
				
		easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		easingListPane.selectionProperty().set(new AnimationOption(Linear.class, 0));
		
		// setup the animation config
		
		lblDuration = new Label("Duration");
		txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		LongTextFormatter durationFormatter = new LongTextFormatter();
		txtDuration.setTextFormatter(durationFormatter);
		durationFormatter.setValue(500l);
		
		lblDelay = new Label("Delay");
		txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		LongTextFormatter delayFormatter = new LongTextFormatter();
		txtDelay.setTextFormatter(delayFormatter);
		delayFormatter.setValue(0l);
		
		lblAnimationType = new Label("Animation Type");
		cbAnimationType = new ChoiceBox<>(ANIMATION_TYPE_OPTIONS);
		cbAnimationType.setValue(ANIMATION_TYPE_OPTIONS.get(0));
		
		lblEasingType = new Label("Easing Type");
		cbEasingType = new ChoiceBox<>(EASING_TYPE_OPTIONS);
		cbEasingType.setValue(EASING_TYPE_OPTIONS.get(0));
						
		lblOrientation = new Label("Orientation");
		cbOrientation = new ChoiceBox<>(ORIENTATION_OPTIONS);
		cbOrientation.setValue(ORIENTATION_OPTIONS.get(0));
		
		lblDirection = new Label("Direction");
		cbDirection = new ChoiceBox<>(DIRECTION_OPTIONS);
		cbDirection.setValue(DIRECTION_OPTIONS.get(0));
		cbDirection.setPrefWidth(100);
		
		lblShapeType = new Label("Shape Type");
		cbShapeType = new ChoiceBox<>(SHAPE_TYPE_OPTIONS);
		cbShapeType.setValue(SHAPE_TYPE_OPTIONS.get(0));
				
		lblOperation = new Label("Operation");
		cbOperation = new ChoiceBox<>(OPERATION_OPTIONS);
		cbOperation.setValue(OPERATION_OPTIONS.get(0));
		
		lblBlindCount = new Label("Blind Count");
		txtBlindCount = new TextField();
		txtBlindCount.setPromptText("number of blinds");
		IntegerTextFormatter blindCountFormatter = new IntegerTextFormatter();
		txtBlindCount.setTextFormatter(blindCountFormatter);
		blindCountFormatter.setValue(12);
		
		// Bindings
		
		animationOption.bind(animationListPane.selectionProperty());
		animationListPane.selectionProperty().addListener((obs, ov, nv) -> {
			setting = true;
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
					DIRECTION_OPTIONS.setPredicate((f) -> {
						return f.getValue() == Direction.UP || 
							   f.getValue() == Direction.RIGHT || 
							   f.getValue() == Direction.LEFT || 
							   f.getValue() == Direction.DOWN;
					});
					cbDirection.setValue(DIRECTION_OPTIONS.get(0));
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
					DIRECTION_OPTIONS.setPredicate((f) -> { return true; });
					cbDirection.setValue(DIRECTION_OPTIONS.get(0));
				} else {
					// otherwise all the options remain hidden
					LOGGER.warn("Unhandled animation type " + type.getName() + " in " + getClass().getName());
				}
			}
			setting = false;
			animation.set(null);
		});
		
		easingOption.bind(easingListPane.selectionProperty());
		easingListPane.selectionProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		duration.bind(durationFormatter.valueProperty());
		durationFormatter.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		delay.bind(delayFormatter.valueProperty());
		delayFormatter.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		animationType.bind(cbAnimationType.valueProperty());
		cbAnimationType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		easingType.bind(cbEasingType.valueProperty());
		cbEasingType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		orientation.bind(cbOrientation.valueProperty());
		cbOrientation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		direction.bind(cbDirection.valueProperty());
		cbDirection.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		shapeType.bind(cbShapeType.valueProperty());
		cbShapeType.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		operation.bind(cbOperation.valueProperty());
		cbOperation.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		blindCount.bind(blindCountFormatter.valueProperty());
		blindCountFormatter.valueProperty().addListener((obs, ov, nv) -> {
			animation.set(null);
		});
		
		// Set the default value
		
		this.animation.set(null);
		
		// UI
		
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
		Fx.setSize(panePreview, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		panePreview.setBorder(Fx.newBorder(Color.BLACK));
		panePreview.setClip(new Rectangle(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT));
		
		pane1 = new Pane();
		pane1.setBorder(Fx.newBorder(Color.BLACK));
		pane1.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255, 0.5), null, null)));
		Fx.setSize(pane1, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		pane2 = new StackPane();
		pane2.setBorder(Fx.newBorder(Color.BLACK));
		pane2.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)));
		Text content = new Text("Content");
		content.setFill(Color.WHITE);
		pane2.getChildren().add(content);
		Fx.setSize(pane2, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
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
			animation.setType(this.cbAnimationType.getValue().getValue());
			
			// custom animation options
			if (animation instanceof Blinds) {
				Blinds a = (Blinds)animation;
				a.setOrientation(this.orientation.get().getValue());
				a.setBlindCount(this.blindCount.get());
			} else if (animation instanceof Push) {
				Push a = (Push)animation;
				a.setDirection(this.direction.get().getValue());
			} else if (animation instanceof Shaped) {
				Shaped a = (Shaped)animation;
				a.setOperation(this.operation.get().getValue());
				a.setShapeType(this.shapeType.get().getValue());
			} else if (animation instanceof Split) {
				Split a = (Split)animation;
				a.setOrientation(this.orientation.get().getValue());
				a.setOperation(this.operation.get().getValue());
			} else if (animation instanceof Swipe) {
				Swipe a = (Swipe)animation;
				a.setDirection(this.direction.get().getValue());
			} else if (animation instanceof Swap ||
					   animation instanceof Fade ||
					   animation instanceof Zoom) {
				// Swap/Fade/Zoom don't have extra options at this time
			} else {
				LOGGER.warn("Unhandled SlideAnimation type " + animation.getClass().getName());
			}
			
			// easing
			Easing easing = (Easing)easingClass.newInstance();
			easing.setType(this.cbEasingType.getValue().getValue());
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
