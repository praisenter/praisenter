package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.animation.AnimationDirection;
import org.praisenter.data.slide.animation.AnimationOperation;
import org.praisenter.data.slide.animation.AnimationOrientation;
import org.praisenter.data.slide.animation.AnimationShapeType;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.animation.AnimationFunction;
import org.praisenter.data.slide.animation.AnimationEasingFunction;
import org.praisenter.data.slide.animation.AnimationEasingType;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditGridPane;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.LongSpinnerValueFactory;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;

// TODO expand to allow edit of animation fields, allow toggle of Transition vs. Animation fields

public final class SlideAnimationPicker extends VBox {
	
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
	
	public SlideAnimationPicker() {
		this.animationFunction = new SimpleObjectProperty<AnimationFunction>();
		this.duration = new SimpleObjectProperty<Long>(0l);
		this.easingFunction = new SimpleObjectProperty<AnimationEasingFunction>();
		this.easingType = new SimpleObjectProperty<AnimationEasingType>();
		this.orientation = new SimpleObjectProperty<AnimationOrientation>();
		this.operation = new SimpleObjectProperty<AnimationOperation>();
		this.direction = new SimpleObjectProperty<AnimationDirection>();
		this.shapeType = new SimpleObjectProperty<AnimationShapeType>();
		this.blindCount = new SimpleObjectProperty<Integer>(0);
		
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
		spnDuration.setValueFactory(new LongSpinnerValueFactory(0, Long.MAX_VALUE));
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
		spnBlindCount.getValueFactory().setConverter(LastValueNumberStringConverter.forInteger((originalValueText) -> {
			Platform.runLater(() -> {
				spnBlindCount.getEditor().setText(originalValueText);
			});
		}));
		spnBlindCount.getValueFactory().valueProperty().bindBidirectional(this.blindCount);
		
		// layout
		
		int r = 0;
        EditGridPane grid = new EditGridPane();
        grid.addRow(r++, new Label(Translations.get("slide.transition.type")), cmbAnimationFunction);
        grid.addRow(r++, new Label(Translations.get("slide.transition.duration")), spnDuration);
        grid.addRow(r++, new Label(Translations.get("slide.transition.easing.function")), cmbEasingFunction);
        grid.addRow(r++, new Label(Translations.get("slide.transition.easing.type")), cmbEasingType);
        grid.addRow(r++, new Label(Translations.get("slide.transition.direction")), cmbDirections);
        grid.addRow(r++, new Label(Translations.get("slide.transition.operation")), cmbOperations);
        grid.addRow(r++, new Label(Translations.get("slide.transition.orientation")), cmbOrientations);
        grid.addRow(r++, new Label(Translations.get("slide.transition.shape.type")), cmbShapeTypes);
        grid.addRow(r++, new Label(Translations.get("slide.transition.blind.count")), spnBlindCount);
        grid.showRowsOnly(0);
        
        this.getChildren().addAll(grid);
        
        this.animationFunction.addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		grid.showRowsOnly(0);
        		return;
        	}
        	
        	switch (nv) {
        		case BLINDS: 
        			grid.showRowsOnly(0,1,2,3,6,8); 
        			break;
        		case FADE: 
    				grid.showRowsOnly(0,1,2,3); 
    				break;
        		case PUSH: 
        			grid.showRowsOnly(0,1,2,3,4);
        			filteredDirections.setPredicate(d -> 
        				d.getValue() == AnimationDirection.UP || 
    					d.getValue() == AnimationDirection.DOWN || 
    					d.getValue() == AnimationDirection.LEFT || 
    					d.getValue() == AnimationDirection.RIGHT);
        			break;
        		case SHAPE: 
        			grid.showRowsOnly(0,1,2,3,5,7); 
        			break;
        		case SPLIT: 
        			grid.showRowsOnly(0,1,2,3,5,6); 
        			break;
        		case SWAP: 
        			grid.showRowsOnly(0); 
        			break;
        		case SWIPE: 
        			grid.showRowsOnly(0,1,2,3,4);
        			filteredDirections.setPredicate(d -> true);
        			break;
        		case ZOOM: 
        			grid.showRowsOnly(0,1,2,3); 
        			break;
        		default: 
        			grid.showRowsOnly(0); 
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
