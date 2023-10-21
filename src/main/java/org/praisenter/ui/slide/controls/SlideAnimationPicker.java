package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.animation.AnimationDirection;
import org.praisenter.data.slide.animation.AnimationEasingFunction;
import org.praisenter.data.slide.animation.AnimationEasingType;
import org.praisenter.data.slide.animation.AnimationFunction;
import org.praisenter.data.slide.animation.AnimationOperation;
import org.praisenter.data.slide.animation.AnimationOrientation;
import org.praisenter.data.slide.animation.AnimationShapeType;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.LongSpinnerValueFactory;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

// FEATURE (L-H) expand to allow edit of animation fields, allow toggle of Transition vs. Animation fields

public final class SlideAnimationPicker extends EditorFieldGroup {
	
	private final ObjectProperty<AnimationFunction> animationFunction;
	private final ObjectProperty<Long> duration;
	private final ObjectProperty<AnimationEasingFunction> easingFunction;
	private final ObjectProperty<AnimationEasingType> easingType;
	private final ObjectProperty<AnimationOrientation> orientation;
	private final ObjectProperty<AnimationOperation> operation;
	private final ObjectProperty<AnimationDirection> direction;
	private final ObjectProperty<AnimationShapeType> shapeType;
	private final ObjectProperty<Integer> blindCount;
	
	private final ObjectProperty<SlideAnimation> value;
	
	private final BooleanBinding showDurationAndEasing;
	private final BooleanBinding showDirection;
	private final BooleanBinding showOperation;
	private final BooleanBinding showOrientation;
	private final BooleanBinding showShapeType;
	private final BooleanBinding showBlindCount;
	
	public SlideAnimationPicker() {
		this.animationFunction = new SimpleObjectProperty<AnimationFunction>(AnimationFunction.SWAP);
		this.duration = new SimpleObjectProperty<Long>(300l);
		this.easingFunction = new SimpleObjectProperty<AnimationEasingFunction>(AnimationEasingFunction.LINEAR);
		this.easingType = new SimpleObjectProperty<AnimationEasingType>(AnimationEasingType.IN);
		this.orientation = new SimpleObjectProperty<AnimationOrientation>(AnimationOrientation.VERTICAL);
		this.operation = new SimpleObjectProperty<AnimationOperation>(AnimationOperation.EXPAND);
		this.direction = new SimpleObjectProperty<AnimationDirection>(AnimationDirection.LEFT);
		this.shapeType = new SimpleObjectProperty<AnimationShapeType>(AnimationShapeType.CIRCLE);
		this.blindCount = new SimpleObjectProperty<Integer>(10);
		
		this.value = new SimpleObjectProperty<SlideAnimation>();
		
		// controls

		ObservableList<Option<AnimationFunction>> animationFunctions = FXCollections.observableArrayList();
		for (AnimationFunction animationFunction : AnimationFunction.values()) {
			animationFunctions.add(new Option<>(Translations.get("slide.animation.function." + animationFunction), animationFunction));
		}
		ComboBox<Option<AnimationFunction>> cmbAnimationFunction = new ComboBox<>(animationFunctions);
		cmbAnimationFunction.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbAnimationFunction.valueProperty(), this.animationFunction);
		
		Spinner<Long> spnDuration = new Spinner<>(0, Long.MAX_VALUE, 400, 100);
		spnDuration.setEditable(true);
		spnDuration.setValueFactory(new LongSpinnerValueFactory(0, Long.MAX_VALUE, 400, 100));
		spnDuration.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spnDuration.getValueFactory().setConverter(LastValueNumberStringConverter.forLong((originalValueText) -> {
			Platform.runLater(() -> {
				spnDuration.getEditor().setText(originalValueText);
			});
		}));
		spnDuration.getValueFactory().valueProperty().bindBidirectional(this.duration);
		
		ObservableList<Option<AnimationEasingFunction>> easingFunctions = FXCollections.observableArrayList();
		for (AnimationEasingFunction function : AnimationEasingFunction.values()) {
			easingFunctions.add(new Option<>(Translations.get("slide.easing.function." + function), function));
		}
		ComboBox<Option<AnimationEasingFunction>> cmbEasingFunction = new ComboBox<>(easingFunctions);
		cmbEasingFunction.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbEasingFunction.valueProperty(), this.easingFunction);

		ObservableList<Option<AnimationEasingType>> easingTypes = FXCollections.observableArrayList();
		for (AnimationEasingType type : AnimationEasingType.values()) {
			easingTypes.add(new Option<>(Translations.get("slide.easing.type." + type), type));
		}
		ComboBox<Option<AnimationEasingType>> cmbEasingType = new ComboBox<>(easingTypes);
		cmbEasingType.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbEasingType.valueProperty(), this.easingType);
		
		ObservableList<Option<AnimationOrientation>> orientations = FXCollections.observableArrayList();
		for (AnimationOrientation orientation : AnimationOrientation.values()) {
			orientations.add(new Option<>(Translations.get("slide.effect.orientation." + orientation), orientation));
		}
		ComboBox<Option<AnimationOrientation>> cmbOrientations = new ComboBox<>(orientations);
		cmbOrientations.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbOrientations.valueProperty(), this.orientation);
		
		ObservableList<Option<AnimationOperation>> operations = FXCollections.observableArrayList();
		for (AnimationOperation operation : AnimationOperation.values()) {
			operations.add(new Option<>(Translations.get("slide.effect.operation." + operation), operation));
		}
		ComboBox<Option<AnimationOperation>> cmbOperations = new ComboBox<>(operations);
		cmbOperations.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbOperations.valueProperty(), this.operation);
		
		ObservableList<Option<AnimationDirection>> directions = FXCollections.observableArrayList();
		for (AnimationDirection direction : AnimationDirection.values()) {
			directions.add(new Option<>(Translations.get("slide.effect.direction." + direction), direction));
		}
		FilteredList<Option<AnimationDirection>> filteredDirections = directions.filtered(d -> true);
		ComboBox<Option<AnimationDirection>> cmbDirections = new ComboBox<>(filteredDirections);
		cmbDirections.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbDirections.valueProperty(), this.direction);
		
		ObservableList<Option<AnimationShapeType>> shapeTypes = FXCollections.observableArrayList();
		for (AnimationShapeType shapeType : AnimationShapeType.values()) {
			shapeTypes.add(new Option<>(Translations.get("slide.effect.shape." + shapeType), shapeType));
		}
		ComboBox<Option<AnimationShapeType>> cmbShapeTypes = new ComboBox<>(shapeTypes);
		cmbShapeTypes.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbShapeTypes.valueProperty(), this.shapeType);
		
		Spinner<Integer> spnBlindCount = new Spinner<>(1, Integer.MAX_VALUE, 5, 1);
		spnBlindCount.setEditable(true);
		spnBlindCount.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spnBlindCount.getValueFactory().setConverter(LastValueNumberStringConverter.forInteger((originalValueText) -> {
			Platform.runLater(() -> {
				spnBlindCount.getEditor().setText(originalValueText);
			});
		}));
		spnBlindCount.getValueFactory().valueProperty().bindBidirectional(this.blindCount);
		
		this.showDurationAndEasing = Bindings.createBooleanBinding(() -> {
			if (this.animationFunction.get() == AnimationFunction.SWAP) {
				return false;
			}
			return true;
		}, this.animationFunction);
		
		this.showDirection = Bindings.createBooleanBinding(() -> {
			AnimationFunction fn = this.animationFunction.get();
			if (fn == AnimationFunction.SWIPE || fn == AnimationFunction.PUSH) {
				return true;
			}
			return false;
		}, this.animationFunction);
		
		this.showOrientation = Bindings.createBooleanBinding(() -> {
			AnimationFunction fn = this.animationFunction.get();
			if (fn == AnimationFunction.SPLIT || fn == AnimationFunction.BLINDS) {
				return true;
			}
			return false;
		}, this.animationFunction);
		
		this.showOperation = Bindings.createBooleanBinding(() -> {
			AnimationFunction fn = this.animationFunction.get();
			if (fn == AnimationFunction.SHAPE || fn == AnimationFunction.SPLIT) {
				return true;
			}
			return false;
		}, this.animationFunction);
		
		this.showShapeType = Bindings.createBooleanBinding(() -> {
			AnimationFunction fn = this.animationFunction.get();
			if (fn == AnimationFunction.SHAPE) {
				return true;
			}
			return false;
		}, this.animationFunction);
		
		this.showBlindCount = Bindings.createBooleanBinding(() -> {
			AnimationFunction fn = this.animationFunction.get();
			if (fn == AnimationFunction.BLINDS) {
				return true;
			}
			return false;
		}, this.animationFunction);
		
		// layout
		
		EditorField fldAnimation = new EditorField(Translations.get("slide.transition.type"), Translations.get("slide.transition.type.description"), cmbAnimationFunction);
		EditorField fldDuration = new EditorField(Translations.get("slide.transition.duration"), Translations.get("slide.transition.duration.description"), spnDuration);
		EditorField fldEasing = new EditorField(Translations.get("slide.transition.easing.function"), Translations.get("slide.transition.easing.function.description"), cmbEasingFunction);
		EditorField fldEasingType = new EditorField(Translations.get("slide.transition.easing.type"), Translations.get("slide.transition.easing.type.description"), cmbEasingType);
		EditorField fldDirection = new EditorField(Translations.get("slide.transition.direction"), cmbDirections);
		EditorField fldOperation = new EditorField(Translations.get("slide.transition.operation"), cmbOperations);
		EditorField fldOrientation = new EditorField(Translations.get("slide.transition.orientation"), cmbOrientations);
		EditorField fldShapeType = new EditorField(Translations.get("slide.transition.shape.type"), cmbShapeTypes);
		EditorField fldBlindCount = new EditorField(Translations.get("slide.transition.blind.count"), Translations.get("slide.transition.blind.count.description"), spnBlindCount);

		fldDuration.visibleProperty().bind(this.showDurationAndEasing);
		fldDuration.managedProperty().bind(fldDuration.visibleProperty());
		fldEasing.visibleProperty().bind(this.showDurationAndEasing);
		fldEasing.managedProperty().bind(fldEasing.visibleProperty());
		fldEasingType.visibleProperty().bind(this.showDurationAndEasing);
		fldEasingType.managedProperty().bind(fldEasingType.visibleProperty());
		fldDirection.visibleProperty().bind(this.showDirection);
		fldDirection.managedProperty().bind(fldDirection.visibleProperty());
		fldOperation.visibleProperty().bind(this.showOperation);
		fldOperation.managedProperty().bind(fldOperation.visibleProperty());
		fldOrientation.visibleProperty().bind(this.showOrientation);
		fldOrientation.managedProperty().bind(fldOrientation.visibleProperty());
		fldShapeType.visibleProperty().bind(this.showShapeType);
		fldShapeType.managedProperty().bind(fldShapeType.visibleProperty());
		fldBlindCount.visibleProperty().bind(this.showBlindCount);
		fldBlindCount.managedProperty().bind(fldBlindCount.visibleProperty());
		
		this.getChildren().addAll(
				fldAnimation,
				fldDuration,
				fldEasing,
				fldEasingType,
				fldDirection,
				fldOperation,
				fldOrientation,
				fldShapeType,
				fldBlindCount);
        
        this.animationFunction.addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		return;
        	}
        	
        	switch (nv) {
        		case BLINDS: 
        			break;
        		case FADE: 
    				break;
        		case PUSH: 
        			filteredDirections.setPredicate(d -> 
        				d.getValue() == AnimationDirection.UP || 
    					d.getValue() == AnimationDirection.DOWN || 
    					d.getValue() == AnimationDirection.LEFT || 
    					d.getValue() == AnimationDirection.RIGHT);
        			break;
        		case SHAPE: 
        			break;
        		case SPLIT: 
        			break;
        		case SWAP: 
        			break;
        		case SWIPE: 
        			filteredDirections.setPredicate(d -> true);
        			break;
        		case ZOOM: 
        			break;
        		default: 
        			break;	
        	}
        });
		
		// bind to value
		
		BindingHelper.bindBidirectional(this.animationFunction, this.value, new ObjectConverter<AnimationFunction, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationFunction t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationFunction convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getAnimationFunction();
			}
		});
		
		BindingHelper.bindBidirectional(this.duration, this.value, new ObjectConverter<Long, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(Long t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public Long convertTo(SlideAnimation e) {
				if (e == null) return 0l;
				return e.getDuration();
			}
		});
		
		BindingHelper.bindBidirectional(this.easingFunction, this.value, new ObjectConverter<AnimationEasingFunction, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationEasingFunction t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationEasingFunction convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getEasingFunction();
			}
		});
		
		BindingHelper.bindBidirectional(this.easingType, this.value, new ObjectConverter<AnimationEasingType, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationEasingType t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationEasingType convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getEasingType();
			}
		});
		
		BindingHelper.bindBidirectional(this.orientation, this.value, new ObjectConverter<AnimationOrientation, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationOrientation t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationOrientation convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getOrientation();
			}
		});
		
		BindingHelper.bindBidirectional(this.operation, this.value, new ObjectConverter<AnimationOperation, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationOperation t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationOperation convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getOperation();
			}
		});
		
		BindingHelper.bindBidirectional(this.direction, this.value, new ObjectConverter<AnimationDirection, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationDirection t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationDirection convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getDirection();
			}
		});
		
		BindingHelper.bindBidirectional(this.shapeType, this.value, new ObjectConverter<AnimationShapeType, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(AnimationShapeType t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public AnimationShapeType convertTo(SlideAnimation e) {
				if (e == null) return null;
				return e.getShapeType();
			}
		});
		
		BindingHelper.bindBidirectional(this.blindCount, this.value, new ObjectConverter<Integer, SlideAnimation>() {
			@Override
			public SlideAnimation convertFrom(Integer t) {
				return SlideAnimationPicker.this.getCurrentValue();
			}
			@Override
			public Integer convertTo(SlideAnimation e) {
				if (e == null) return 1;
				return e.getBlindCount();
			}
		});
	}
	
	private SlideAnimation getCurrentValue() {
		AnimationFunction type = this.animationFunction.get();
		
		if (type == null) return null;
		
		long duration = this.duration.get();
		AnimationEasingFunction easingFunction = this.easingFunction.get();
		AnimationEasingType easingType = this.easingType.get();
		AnimationOrientation orientation = this.orientation.get();
		AnimationOperation operation = this.operation.get();
		AnimationDirection direction = this.direction.get();
		AnimationShapeType shapeType = this.shapeType.get();
		int blindCount = this.blindCount.get();
		
		SlideAnimation st = new SlideAnimation();
		st.setAnimationFunction(type);
		st.setBlindCount(blindCount);
		st.setDirection(direction);
		st.setDuration(duration);
		st.setEasingFunction(easingFunction);
		st.setEasingType(easingType);
		st.setOperation(operation);
		st.setOrientation(orientation);
		st.setShapeType(shapeType);
		
		return st;
	}
	
	public SlideAnimation getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideAnimation value) {
		this.value.set(value);
	}
	
	public ObjectProperty<SlideAnimation> valueProperty() {
		return this.value;
	}
}
