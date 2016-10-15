/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.List;

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

import javafx.animation.Transition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

// FIXME translate

/**
 * Represents a control to configure animations.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class AnimationPickerPane extends BorderPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The animation preview width */
	private static final double PREVIEW_WIDTH = 214;
	
	/** The animation preview height */
	private static final double PREVIEW_HEIGHT = 120;
	
	// options
	
	/** The animation types (in/out) */
	private static final FilteredList<Option<AnimationType>> ANIMATION_TYPE_OPTIONS = getOptions(AnimationType.class);
	
	/** The easing types (in/out/both) */
	private static final FilteredList<Option<EasingType>> EASING_TYPE_OPTIONS = getOptions(EasingType.class);
	
	/** The orientation options */
	private static final FilteredList<Option<Orientation>> ORIENTATION_OPTIONS = getOptions(Orientation.class);
	
	/** The operation options */
	private static final FilteredList<Option<Operation>> OPERATION_OPTIONS = getOptions(Operation.class);
	
	/** The shape type options */
	private static final FilteredList<Option<ShapeType>> SHAPE_TYPE_OPTIONS = getOptions(ShapeType.class);
	
	/** The direction options */
	private static final FilteredList<Option<Direction>> DIRECTION_OPTIONS = getOptions(Direction.class);
	
	/**
	 * Returns a filtered observable list for the given enum.
	 * @param clazz the class
	 * @return FilteredList
	 */
	private static <T extends Enum<T>> FilteredList<Option<T>> getOptions(Class<T> clazz) {
		List<Option<T>> options = new ArrayList<Option<T>>();
		T[] values = clazz.getEnumConstants();
		for (T value : values) {
			options.add(new Option<T>(Translations.get(clazz.getName() + "." + value.name()), value));
		}
		return new FilteredList<Option<T>>(FXCollections.observableArrayList(options));
	}
	
	/**
	 * Returns an option for the given value.
	 * @param options the list of options
	 * @param value the value
	 * @return Option&lt;T&gt;
	 */
	private static <T> Option<T> getOption(FilteredList<Option<T>> options, T value) {
		for (Option<T> option : options) {
			if (option.getValue() == value) {
				return option;
			}
		}
		return options.get(0);
	}
	
	// the output
	
	/** The configured animation */
	private final ObjectProperty<SlideAnimation> value = new SimpleObjectProperty<SlideAnimation>();

	/** True if the value is being changed */
	private boolean mutating = false;
	
	// nodes
	
	/** The objects (slide/components) selector */
	private final ComboBox<AnimatedObject> cmbObjects;
	
	/** The animation selector */
	private final FlowListView<AnimationOption> animationListPane;
	
	/** The easing selector */
	private final FlowListView<AnimationOption> easingListPane;
	
	/** The duration */
	private final TextField txtDuration;
	
	/** The delay */
	private final TextField txtDelay;
	
	/** The animation type selector (in/out) */
	private final ChoiceBox<Option<AnimationType>> cbAnimationType;
	
	/** The easing type selector (in/out/both) */
	private final ChoiceBox<Option<EasingType>> cbEasingType;
	
	/** The orientation selector */
	private final ChoiceBox<Option<Orientation>> cbOrientation;
	
	/** The direction selector */
	private final ChoiceBox<Option<Direction>> cbDirection;
	
	/** The shape type selector */
	private final ChoiceBox<Option<ShapeType>> cbShapeType;
	
	/** The operation selector */
	private final ChoiceBox<Option<Operation>> cbOperation;
	
	/** The blind count */
	private final TextField txtBlindCount;
	
	// preview
	
	/** The preview transition */
	Transition transition;
	
	/**
	 * Creates a new animation picker pane.
	 * @param objects the list of objects the animation can be applied to
	 */
	public AnimationPickerPane(ObservableSet<AnimatedObject> objects) {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(3);
		grid.setVgap(3);
		
		ObservableList<AnimatedObject> objs = FXCollections.observableArrayList(objects);
		
		Label lblObjects = new Label("For");
		cmbObjects = new ComboBox<>(objs);
		
		if (objects.size() == 0) {
			// TODO throw error, we should always at least have the slide
		}
		if (objects.size() == 1) {
			cmbObjects.setValue(objs.get(0));
			cmbObjects.setDisable(true);
		}
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				mutating = true;
				value.set(getControlValues());
				mutating = false;
			}
		};
		
		// setup the animation selection
		List<AnimationOption> animationOptions = new ArrayList<AnimationOption>(Transitions.getAnimationOptions());
		List<AnimationOption> easingOptions = new ArrayList<AnimationOption>(Transitions.getEasingOptions());
		
		animationListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		animationListPane.itemsProperty().set(FXCollections.observableArrayList(animationOptions));
		animationListPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
		animationListPane.setSelection(new AnimationOption(Swap.class, 0));
				
		easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
		easingListPane.setSelection(new AnimationOption(Linear.class, 0));
		
		// setup the animation config
		
		Label lblDuration = new Label("Duration");
		txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		LongTextFormatter durationFormatter = new LongTextFormatter();
		txtDuration.setTextFormatter(durationFormatter);
		durationFormatter.setValue(500l);
		
		Label lblDelay = new Label("Delay");
		txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		LongTextFormatter delayFormatter = new LongTextFormatter();
		txtDelay.setTextFormatter(delayFormatter);
		delayFormatter.setValue(0l);
		
		Label lblAnimationType = new Label("Animation Type");
		cbAnimationType = new ChoiceBox<>(ANIMATION_TYPE_OPTIONS);
		cbAnimationType.setValue(ANIMATION_TYPE_OPTIONS.get(0));
		
		Label lblEasingType = new Label("Easing Type");
		cbEasingType = new ChoiceBox<>(EASING_TYPE_OPTIONS);
		cbEasingType.setValue(EASING_TYPE_OPTIONS.get(0));
						
		Label lblOrientation = new Label("Orientation");
		cbOrientation = new ChoiceBox<>(ORIENTATION_OPTIONS);
		cbOrientation.setValue(ORIENTATION_OPTIONS.get(0));
		
		Label lblDirection = new Label("Direction");
		cbDirection = new ChoiceBox<>(DIRECTION_OPTIONS);
		cbDirection.setValue(DIRECTION_OPTIONS.get(0));
		cbDirection.setPrefWidth(100);
		
		Label lblShapeType = new Label("Shape Type");
		cbShapeType = new ChoiceBox<>(SHAPE_TYPE_OPTIONS);
		cbShapeType.setValue(SHAPE_TYPE_OPTIONS.get(0));
				
		Label lblOperation = new Label("Operation");
		cbOperation = new ChoiceBox<>(OPERATION_OPTIONS);
		cbOperation.setValue(OPERATION_OPTIONS.get(0));
		
		Label lblBlindCount = new Label("Blind Count");
		txtBlindCount = new TextField();
		txtBlindCount.setPromptText("number of blinds");
		IntegerTextFormatter blindCountFormatter = new IntegerTextFormatter();
		txtBlindCount.setTextFormatter(blindCountFormatter);
		blindCountFormatter.setValue(12);
		
		// value bindings
		
		easingListPane.selectionProperty().addListener(listener);
		txtDuration.textProperty().addListener(listener);
		txtDelay.textProperty().addListener(listener);
		cbAnimationType.valueProperty().addListener(listener);
		cbDirection.valueProperty().addListener(listener);
		cbEasingType.valueProperty().addListener(listener);
		cbOperation.valueProperty().addListener(listener);
		cbOrientation.valueProperty().addListener(listener);
		cbShapeType.valueProperty().addListener(listener);
		txtBlindCount.textProperty().addListener(listener);
		cmbObjects.valueProperty().addListener(listener);
		
		// hide/show logic
		
		animationListPane.selectionProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			mutating = true;
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
			value.set(getControlValues());
			mutating = false;
		});
		
		// set the default
		this.mutating = true;
		this.value.set(getControlValues());
		this.mutating = false;
		
		// listen for changes directly to the animation
		this.value.addListener((obs, ov, nv) -> {
			if (mutating) return;
			mutating = true;
			setControlValues(nv);
			mutating = false;
		});
		
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
		
		Pane panePreview = new Pane();
		Fx.setSize(panePreview, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		panePreview.setBorder(Fx.newBorder(Color.BLACK));
		panePreview.setClip(new Rectangle(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT));
		
		Pane pane1 = new Pane();
		pane1.setBorder(Fx.newBorder(Color.BLACK));
		pane1.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255, 0.5), null, null)));
		Fx.setSize(pane1, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		
		Pane pane2 = new StackPane();
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
			SlideAnimation animation = this.value.get();
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
		this.setMinHeight(420);
	}
	
	/**
	 * Sets the values of the controls based on the given animation.
	 * @param animation the animation
	 */
	private void setControlValues(SlideAnimation animation) {
		if (animation != null) {
    		// assign all the controls their values
			// FIXME we don't know the animated object type at this time
			cmbObjects.setValue(new AnimatedObject(animation.getId(), AnimatedObjectType.COMPONENT, "test"));
			animationListPane.setSelection(new AnimationOption(animation.getClass(), 0));
			easingListPane.setSelection(new AnimationOption(animation.getEasing().getClass(), 0));
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
	}
	
	/**
	 * Returns an animation using the current values of the controls.
	 * @return {@link SlideAnimation}
	 */
	private SlideAnimation getControlValues() {
		AnimationOption animationOption = this.animationListPane.selectionProperty().get();
		if (animationOption == null) {
			// return null if an animation type hasn't been selected
			animationOption = new AnimationOption(Swap.class, 10);
		}
		
		AnimationOption easingOption = this.easingListPane.selectionProperty().get();
		if (easingOption == null) {
			// return null if an easing hasn't been selected
			easingOption = new AnimationOption(Linear.class, 0);
		}
		
		StringConverter<Integer> intConverter = new IntegerStringConverter();
		StringConverter<Long> longConverter = new LongStringConverter();
		
		Class<?> animationClass = animationOption.getType();
		Class<?> easingClass = easingOption.getType();
		try {
			Long delay = longConverter.fromString(this.txtDelay.getText());
			Long duration = longConverter.fromString(this.txtDuration.getText());
			Integer blinds = intConverter.fromString(this.txtBlindCount.getText());
			
			// animation
			SlideAnimation animation = (SlideAnimation)animationClass.newInstance();
			animation.setDelay(delay != null ? delay : 0);
			animation.setDuration(duration != null ? duration : 500);
			animation.setId(this.cmbObjects.getValue().getObjectId());
			animation.setType(this.cbAnimationType.getValue().getValue());
			
			// custom animation options
			if (animation instanceof Blinds) {
				Blinds a = (Blinds)animation;
				a.setOrientation(this.cbOrientation.getValue().getValue());
				a.setBlindCount(blinds != null ? blinds : 10);
			} else if (animation instanceof Push) {
				Push a = (Push)animation;
				a.setDirection(this.cbDirection.getValue().getValue());
			} else if (animation instanceof Shaped) {
				Shaped a = (Shaped)animation;
				a.setOperation(this.cbOperation.getValue().getValue());
				a.setShapeType(this.cbShapeType.getValue().getValue());
			} else if (animation instanceof Split) {
				Split a = (Split)animation;
				a.setOrientation(this.cbOrientation.getValue().getValue());
				a.setOperation(this.cbOperation.getValue().getValue());
			} else if (animation instanceof Swipe) {
				Swipe a = (Swipe)animation;
				a.setDirection(this.cbDirection.getValue().getValue());
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
			// easings don't have extra options at this time
			animation.setEasing(easing);
			
			return animation;
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.warn("Failed to create a new animation/easing of type " + animationClass.getName() + "/" + easingClass.getName() + ". The class must have a zero-argument constructor.");
		}
		
		// last ditch effort
		return new Swap();
	}

	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;{@link SlideAnimation}&gt;
	 */
	public ObjectProperty<SlideAnimation> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current value.
	 * @return {@link SlideAnimation}
	 */
	public SlideAnimation getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value.
	 * @param animation the animation
	 */
	public void setValue(SlideAnimation animation) {
		this.value.set(animation);
	}
}
