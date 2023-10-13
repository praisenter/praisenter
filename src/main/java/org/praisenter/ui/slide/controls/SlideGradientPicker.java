package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientType;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.FormFieldSection;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.Numbers;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

// FEATURE (L-L) Add ability to configure any number of gradient stops
// JAVABUG (L) 06/14/17 [workaround] Color picker in context menu (or other dialog) doesn't work if you click the "Custom Color" link https://bugs.openjdk.java.net/browse/JDK-8175803

// FIXME replace color pickers with custom one
public final class SlideGradientPicker extends FormFieldSection {
	private static final String GRADIENT_PREVIEW_CSS = "p-gradient-preview";
	private static final String GRADIENT_PREVIEW_BACKGROUND_CSS = "p-gradient-preview-background";
	
	private static final double WIDTH = 125;
	private static final double HEIGHT = 125;
	
    // the current gradient
    
	private final ObjectProperty<SlideGradient> value;

	// properties that build the gradient

	private final ObjectProperty<SlideGradientType> type;
	private final ObjectProperty<SlideGradientCycleType> cycleType;
	private final ObjectProperty<Color> stop1Color;
	private final ObjectProperty<Color> stop2Color;
	private final DoubleProperty stop1Offset;
	private final DoubleProperty stop2Offset;
	private final DoubleProperty handle1X;
	private final DoubleProperty handle1Y;
	private final DoubleProperty handle2X;
	private final DoubleProperty handle2Y;
	
    public SlideGradientPicker() {
    	this(null);
    }
	
    public SlideGradientPicker(String label) {
    	super(label);
    	
    	this.value = new SimpleObjectProperty<SlideGradient>();
    	this.type = new SimpleObjectProperty<>(SlideGradientType.LINEAR);
    	this.cycleType = new SimpleObjectProperty<>(SlideGradientCycleType.NONE);
    	this.stop1Color = new SimpleObjectProperty<>(Color.BLACK);
    	this.stop2Color = new SimpleObjectProperty<>(Color.WHITE);
    	this.stop1Offset = new SimpleDoubleProperty(0);
    	this.stop2Offset = new SimpleDoubleProperty(1);
    	this.handle1X = new SimpleDoubleProperty(0);
    	this.handle1Y = new SimpleDoubleProperty(0);
    	this.handle2X = new SimpleDoubleProperty(1);
    	this.handle2Y = new SimpleDoubleProperty(1);
    	
    	// create the gradient type options
        ObservableList<Option<SlideGradientType>> types = FXCollections.observableArrayList();
        types.add(new Option<SlideGradientType>(Translations.get("slide.gradient.type." + SlideGradientType.LINEAR), SlideGradientType.LINEAR));
        types.add(new Option<SlideGradientType>(Translations.get("slide.gradient.type." + SlideGradientType.RADIAL), SlideGradientType.RADIAL));
        ChoiceBox<Option<SlideGradientType>> cbType = new ChoiceBox<>(types);
        cbType.setMaxWidth(Double.MAX_VALUE);
        
    	// create the cycle type options
        ObservableList<Option<SlideGradientCycleType>> cycleTypes = FXCollections.observableArrayList();
        cycleTypes.add(new Option<SlideGradientCycleType>(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.NONE), SlideGradientCycleType.NONE));
        cycleTypes.add(new Option<SlideGradientCycleType>(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.REFLECT), SlideGradientCycleType.REFLECT));
        cycleTypes.add(new Option<SlideGradientCycleType>(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.REPEAT), SlideGradientCycleType.REPEAT));
        ChoiceBox<Option<SlideGradientCycleType>> cbCycleType = new ChoiceBox<>(cycleTypes);
        cbCycleType.setMaxWidth(Double.MAX_VALUE);
        
        // stop 1 offset slider
        Slider sldStop1 = new Slider(0, 1, 0);
        sldStop1.getStyleClass().add(Styles.SMALL);
        sldStop1.setSkin(new ProgressSliderSkin(sldStop1));
        sldStop1.valueProperty().bindBidirectional(this.stop1Offset);
        
        // stop 1 color
        ColorPicker pkrStop1 = new ColorPicker(Color.BLACK);
        pkrStop1.valueProperty().bindBidirectional(this.stop1Color);
        pkrStop1.setMaxWidth(Double.MAX_VALUE);
        
        // stop 2 offset slider
        Slider sldStop2 = new Slider(0, 1, 1);
        sldStop2.getStyleClass().add(Styles.SMALL);
        sldStop2.setSkin(new ProgressSliderSkin(sldStop2));
        sldStop2.valueProperty().bindBidirectional(this.stop2Offset);
        
        // stop 2 color
        ColorPicker pkrStop2 = new ColorPicker(Color.WHITE);
        pkrStop2.valueProperty().bindBidirectional(this.stop2Color);
        pkrStop2.setMaxWidth(Double.MAX_VALUE);
        
        // create the preview/configure view
        StackPane previewStack = new StackPane();
        previewStack.setPrefSize(WIDTH + 2, HEIGHT + 2);
        previewStack.setMaxSize(WIDTH + 2, HEIGHT + 2);
        previewStack.setMinSize(WIDTH + 2, HEIGHT + 2);
        
        // the transparent (tiled) background
        Pane bg = new Pane();
        bg.setPrefSize(WIDTH, HEIGHT);
        bg.setMaxSize(WIDTH, HEIGHT);
        bg.setMinSize(WIDTH, HEIGHT);
        bg.getStyleClass().add(GRADIENT_PREVIEW_BACKGROUND_CSS);
        
        // the preview pane itself
        Pane preview = new Pane();
        preview.setPrefSize(WIDTH, HEIGHT);
        preview.setMaxSize(WIDTH, HEIGHT);
        preview.setMinSize(WIDTH, HEIGHT);
        preview.getStyleClass().add(GRADIENT_PREVIEW_CSS);
        preview.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
        	Paint paint = PaintConverter.toJavaFX(this.value.get());
        	return new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY));
        }, this.value));
        
        // the configuration handles
        Shape handle1 = new Circle(5);
        handle1.setStroke(Color.WHITE);
        handle1.setStrokeWidth(2);
        handle1.setManaged(false);
        handle1.setFill(Color.BLACK);
        
        Shape handle2 = new Circle(5);
        handle2.setStroke(Color.BLACK);
        handle2.setFill(Color.WHITE);
        handle2.setStrokeWidth(2);
        handle2.setManaged(false);
        handle2.setLayoutX(WIDTH);
        handle2.setLayoutY(HEIGHT);
        
        // add them all to the preview stack
        previewStack.getChildren().addAll(bg, preview, handle1, handle2);
        
        // wire up the mouse handlers for the handles
        EventHandler<MouseEvent> handle1MouseHandler = event -> {
        	// get the change in x and y
            final double x = event.getX();
            final double y = event.getY();
            
            // compute the new x/y between 0 and 1
            final double px = Numbers.clamp(this.handle1X.get() + x / WIDTH, 0, 1);
            final double py = Numbers.clamp(this.handle1Y.get() + y / HEIGHT, 0, 1);
        	
            // set the start x/y
        	this.handle1X.set(px);
        	this.handle1Y.set(py);
        };
        
        handle1.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
        	double x1 = Numbers.clamp(this.handle1X.get(), 0, 1);
            return x1 * WIDTH;
        }, this.handle1X));

        handle1.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
        	double y1 = Numbers.clamp(this.handle1Y.get(), 0, 1);
            return y1 * HEIGHT;
        }, this.handle1Y));
        
        EventHandler<MouseEvent> handle2MouseHandler = event -> {
        	// get the change in x and y
            final double x = event.getX();
            final double y = event.getY();
        	
            // compute the new x/y between 0 and 1
            final double px = Numbers.clamp(this.handle2X.get() + x / WIDTH, 0, 1);
            final double py = Numbers.clamp(this.handle2Y.get() + y / HEIGHT, 0, 1);
            
            // set the end x/y
            this.handle2X.set(px);
            this.handle2Y.set(py);
        };
        
        handle2.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
        	double x2 = Numbers.clamp(this.handle2X.get(), 0, 1);
            return x2 * WIDTH;
        }, this.handle2X));

        handle2.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
        	double y2 = Numbers.clamp(this.handle2Y.get(), 0, 1);
            return y2 * HEIGHT;
        }, this.handle2Y));
        
        // set the handlers
        handle1.setOnMouseDragged(handle1MouseHandler);
        handle2.setOnMouseDragged(handle2MouseHandler);
        handle2.setCursor(Cursor.HAND);
        handle1.setCursor(Cursor.HAND);
        
        HBox pp = new HBox(previewStack);
        pp.setAlignment(Pos.CENTER);
        pp.setPadding(new Insets(10, 0, 10, 0));
        
        // build the layout

	    int fIndex = this.addField(null, pp);
	    this.addField(Translations.get("slide.gradient.type"), cbType);
	    this.addField(Translations.get("slide.gradient.stop1.color"), pkrStop1);
	    this.addField(Translations.get("slide.gradient.stop1.offset"), sldStop1);
	    this.addField(Translations.get("slide.gradient.stop2.color"), pkrStop2);
	    this.addField(Translations.get("slide.gradient.stop2.offset"), sldStop2);
	    this.addField(Translations.get("slide.gradient.cycle"), cbCycleType);
        this.hideRow(fIndex);
        this.showRow(fIndex);
        
		// bindings
		
        BindingHelper.bindBidirectional(cbType.valueProperty(), this.type);
        BindingHelper.bindBidirectional(cbCycleType.valueProperty(), this.cycleType);
        
		BindingHelper.bindBidirectional(this.type, this.value, new ObjectConverter<SlideGradientType, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(SlideGradientType t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public SlideGradientType convertTo(SlideGradient e) {
				if (e == null) return SlideGradientType.LINEAR;
				return e.getType();
			}
		});
		
		BindingHelper.bindBidirectional(this.cycleType, this.value, new ObjectConverter<SlideGradientCycleType, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(SlideGradientCycleType t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public SlideGradientCycleType convertTo(SlideGradient e) {
				if (e == null) return SlideGradientCycleType.NONE;
				return e.getCycleType();
			}
		});
		
		BindingHelper.bindBidirectional(this.stop1Offset, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return sldStop1.getValue();
				return e.getStops().get(0).getOffset();
			}
		});
        
		BindingHelper.bindBidirectional(this.stop2Offset, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return sldStop2.getValue();
				return e.getStops().get(1).getOffset();
			}
		});
		
		BindingHelper.bindBidirectional(this.stop1Color, this.value, new ObjectConverter<Color, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Color t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Color convertTo(SlideGradient e) {
				if (e == null) return pkrStop1.getValue();
				return PaintConverter.toJavaFX(e.getStops().get(0).getColor());
			}
		});
		
		BindingHelper.bindBidirectional(this.stop2Color, this.value, new ObjectConverter<Color, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Color t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Color convertTo(SlideGradient e) {
				if (e == null) return pkrStop2.getValue();
				return PaintConverter.toJavaFX(e.getStops().get(1).getColor());
			}
		});
		
		BindingHelper.bindBidirectional(this.handle1X, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle1X.get();
				return Numbers.clamp(e.getStartX(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle1Y, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle1Y.get();
				return Numbers.clamp(e.getStartY(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle2X, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle2X.get();
				return Numbers.clamp(e.getEndX(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle2Y, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getCurrentValue();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle2Y.get();
				return Numbers.clamp(e.getEndY(), 0, 1);
			}
		});
    }

    private SlideGradient getCurrentValue() {
		SlideGradient sg = new SlideGradient();
		sg.setType(this.type.get());
		sg.setStartX(this.handle1X.get());
		sg.setStartY(this.handle1Y.get());
		sg.setEndX(this.handle2X.get());
		sg.setEndY(this.handle2Y.get());
		sg.setCycleType(this.cycleType.get());
		sg.getStops().get(0).setColor(PaintConverter.fromJavaFX(this.stop1Color.get()));
		sg.getStops().get(0).setOffset(this.stop1Offset.get());
		sg.getStops().get(1).setColor(PaintConverter.fromJavaFX(this.stop2Color.get()));
		sg.getStops().get(1).setOffset(this.stop2Offset.get());
		return sg;
    }
    
    public ObjectProperty<SlideGradient> valueProperty() {
    	return this.value;
    }
    
    public SlideGradient getValue() {
    	return this.value.get();
    }
    
    public void setValue(SlideGradient gradient) {
    	this.value.set(gradient);
    }
}