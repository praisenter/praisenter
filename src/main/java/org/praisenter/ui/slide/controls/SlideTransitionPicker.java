package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.effects.Direction;
import org.praisenter.data.slide.effects.Operation;
import org.praisenter.data.slide.effects.Orientation;
import org.praisenter.data.slide.effects.ShapeType;
import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;
import org.praisenter.data.slide.effects.transition.SlideTransition;
import org.praisenter.data.slide.effects.transition.TransitionType;
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

public final class SlideTransitionPicker extends VBox {
	
	private final ObjectProperty<TransitionType> transitionType;
	private final ObjectProperty<Long> duration;
	private final ObjectProperty<EasingFunction> easingFunction;
	private final ObjectProperty<EasingType> easingType;
	private final ObjectProperty<Orientation> orientation;
	private final ObjectProperty<Operation> operation;
	private final ObjectProperty<Direction> direction;
	private final ObjectProperty<ShapeType> shapeType;
	private final ObjectProperty<Integer> blindCount;
	
	private final ObjectProperty<SlideTransition> value;
	
	public SlideTransitionPicker() {
		this.transitionType = new SimpleObjectProperty<TransitionType>();
		this.duration = new SimpleObjectProperty<Long>(0l);
		this.easingFunction = new SimpleObjectProperty<EasingFunction>();
		this.easingType = new SimpleObjectProperty<EasingType>();
		this.orientation = new SimpleObjectProperty<Orientation>();
		this.operation = new SimpleObjectProperty<Operation>();
		this.direction = new SimpleObjectProperty<Direction>();
		this.shapeType = new SimpleObjectProperty<ShapeType>();
		this.blindCount = new SimpleObjectProperty<Integer>(0);
		
		this.value = new SimpleObjectProperty<SlideTransition>();
		
		// controls

		ObservableList<Option<TransitionType>> transitionTypes = FXCollections.observableArrayList();
		for (TransitionType transitionType : TransitionType.values()) {
			transitionTypes.add(new Option<>(Translations.get("slide.transition.type." + transitionType), transitionType));
		}
		ComboBox<Option<TransitionType>> cmbTransitionType = new ComboBox<>(transitionTypes);
		cmbTransitionType.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbTransitionType.valueProperty(), this.transitionType);
		
		Spinner<Long> spnDuration = new Spinner<>(0, Long.MAX_VALUE, 400, 100);
		spnDuration.setEditable(true);
		spnDuration.setValueFactory(new LongSpinnerValueFactory(0, Long.MAX_VALUE));
		spnDuration.getValueFactory().setConverter(LastValueNumberStringConverter.forLong((originalValueText) -> {
			Platform.runLater(() -> {
				spnDuration.getEditor().setText(originalValueText);
			});
		}));
		spnDuration.getValueFactory().valueProperty().bindBidirectional(this.duration);
		
		ObservableList<Option<EasingFunction>> easingFunctions = FXCollections.observableArrayList();
		for (EasingFunction function : EasingFunction.values()) {
			easingFunctions.add(new Option<>(Translations.get("slide.easing.function." + function), function));
		}
		ComboBox<Option<EasingFunction>> cmbEasingFunction = new ComboBox<>(easingFunctions);
		cmbEasingFunction.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbEasingFunction.valueProperty(), this.easingFunction);

		ObservableList<Option<EasingType>> easingTypes = FXCollections.observableArrayList();
		for (EasingType type : EasingType.values()) {
			easingTypes.add(new Option<>(Translations.get("slide.easing.type." + type), type));
		}
		ComboBox<Option<EasingType>> cmbEasingType = new ComboBox<>(easingTypes);
		cmbEasingType.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbEasingType.valueProperty(), this.easingType);
		
		ObservableList<Option<Orientation>> orientations = FXCollections.observableArrayList();
		for (Orientation orientation : Orientation.values()) {
			orientations.add(new Option<>(Translations.get("slide.effect.orientation." + orientation), orientation));
		}
		ComboBox<Option<Orientation>> cmbOrientations = new ComboBox<>(orientations);
		cmbOrientations.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbOrientations.valueProperty(), this.orientation);
		
		ObservableList<Option<Operation>> operations = FXCollections.observableArrayList();
		for (Operation operation : Operation.values()) {
			operations.add(new Option<>(Translations.get("slide.effect.operation." + operation), operation));
		}
		ComboBox<Option<Operation>> cmbOperations = new ComboBox<>(operations);
		cmbOperations.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbOperations.valueProperty(), this.operation);
		
		ObservableList<Option<Direction>> directions = FXCollections.observableArrayList();
		for (Direction direction : Direction.values()) {
			directions.add(new Option<>(Translations.get("slide.effect.direction." + direction), direction));
		}
		FilteredList<Option<Direction>> filteredDirections = directions.filtered(d -> true);
		ComboBox<Option<Direction>> cmbDirections = new ComboBox<>(filteredDirections);
		cmbDirections.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbDirections.valueProperty(), this.direction);
		
		ObservableList<Option<ShapeType>> shapeTypes = FXCollections.observableArrayList();
		for (ShapeType shapeType : ShapeType.values()) {
			shapeTypes.add(new Option<>(Translations.get("slide.effect.shape." + shapeType), shapeType));
		}
		ComboBox<Option<ShapeType>> cmbShapeTypes = new ComboBox<>(shapeTypes);
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
        grid.addRow(r++, new Label(Translations.get("slide.transition.type")), cmbTransitionType);
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
        
        this.transitionType.addListener((obs, ov, nv) -> {
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
        				d.getValue() == Direction.UP || 
    					d.getValue() == Direction.DOWN || 
    					d.getValue() == Direction.LEFT || 
    					d.getValue() == Direction.RIGHT);
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
		
		BindingHelper.bindBidirectional(this.transitionType, this.value, new ObjectConverter<TransitionType, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(TransitionType t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public TransitionType convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getTransitionType();
			}
		});
		
		BindingHelper.bindBidirectional(this.duration, this.value, new ObjectConverter<Long, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(Long t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public Long convertTo(SlideTransition e) {
				if (e == null) return 0l;
				return e.getDuration();
			}
		});
		
		BindingHelper.bindBidirectional(this.easingFunction, this.value, new ObjectConverter<EasingFunction, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(EasingFunction t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public EasingFunction convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getEasingFunction();
			}
		});
		
		BindingHelper.bindBidirectional(this.easingType, this.value, new ObjectConverter<EasingType, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(EasingType t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public EasingType convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getEasingType();
			}
		});
		
		BindingHelper.bindBidirectional(this.orientation, this.value, new ObjectConverter<Orientation, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(Orientation t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public Orientation convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getOrientation();
			}
		});
		
		BindingHelper.bindBidirectional(this.operation, this.value, new ObjectConverter<Operation, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(Operation t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public Operation convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getOperation();
			}
		});
		
		BindingHelper.bindBidirectional(this.direction, this.value, new ObjectConverter<Direction, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(Direction t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public Direction convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getDirection();
			}
		});
		
		BindingHelper.bindBidirectional(this.shapeType, this.value, new ObjectConverter<ShapeType, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(ShapeType t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public ShapeType convertTo(SlideTransition e) {
				if (e == null) return null;
				return e.getShapeType();
			}
		});
		
		BindingHelper.bindBidirectional(this.blindCount, this.value, new ObjectConverter<Integer, SlideTransition>() {
			@Override
			public SlideTransition convertFrom(Integer t) {
				return SlideTransitionPicker.this.getCurrentValue();
			}
			@Override
			public Integer convertTo(SlideTransition e) {
				if (e == null) return 1;
				return e.getBlindCount();
			}
		});
	}
	
	private SlideTransition getCurrentValue() {
		TransitionType type = this.transitionType.get();
		
		if (type == null) return null;
		
		long duration = this.duration.get();
		EasingFunction easingFunction = this.easingFunction.get();
		EasingType easingType = this.easingType.get();
		Orientation orientation = this.orientation.get();
		Operation operation = this.operation.get();
		Direction direction = this.direction.get();
		ShapeType shapeType = this.shapeType.get();
		int blindCount = this.blindCount.get();
		
		SlideTransition st = new SlideTransition();
		st.setBlindCount(blindCount);
		st.setDirection(direction);
		st.setDuration(duration);
		st.setEasingFunction(easingFunction);
		st.setEasingType(easingType);
		st.setOperation(operation);
		st.setOrientation(orientation);
		st.setShapeType(shapeType);
		st.setTransitionType(type);
		
		return st;
	}
	
	public SlideTransition getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideTransition value) {
		this.value.set(value);
	}
	
	public ObjectProperty<SlideTransition> valueProperty() {
		return this.value;
	}
}
