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
package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientType;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.slide.convert.PaintConverter;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.ClasspathLoader;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

// FEATURE (L-L) Add ability to configure any number of gradient stops
// JAVABUG (L) 06/14/17 [workaround] Color picker in context menu (or other dialog) doesn't work if you click the "Custom Color" link https://bugs.openjdk.java.net/browse/JDK-8175803

/**
 * Pane for configuring a gradient pattern.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideGradientPicker extends VBox {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/images/transparent.png");
	private static final double WIDTH = 200;
	private static final double HEIGHT = 200;
	
    // the current gradient
    
	private final ObjectProperty<SlideGradient> value;

	// properties that build the gradient

	private final BooleanProperty isLinear;
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
    	this.value = new SimpleObjectProperty<SlideGradient>();
    	this.isLinear = new SimpleBooleanProperty(true);
    	this.cycleType = new SimpleObjectProperty<>(SlideGradientCycleType.NONE);
    	this.stop1Color = new SimpleObjectProperty<>(Color.BLACK);
    	this.stop2Color = new SimpleObjectProperty<>(Color.WHITE);
    	this.stop1Offset = new SimpleDoubleProperty(0);
    	this.stop2Offset = new SimpleDoubleProperty(1);
    	this.handle1X = new SimpleDoubleProperty(0);
    	this.handle1Y = new SimpleDoubleProperty(0);
    	this.handle2X = new SimpleDoubleProperty(1);
    	this.handle2Y = new SimpleDoubleProperty(1);
    	
    	this.value.set(this.getControlValues());
    	
    	// create the gradient type options
		ToggleGroup grpTypes = new ToggleGroup();
        RadioButton rdoLinear = new RadioButton(Translations.get("slide.gradient.type." + SlideGradientType.LINEAR));
        rdoLinear.setToggleGroup(grpTypes);
        rdoLinear.setUserData(0);
        rdoLinear.setSelected(true);
        
        RadioButton rdoRadial = new RadioButton(Translations.get("slide.gradient.type." + SlideGradientType.RADIAL));
        rdoRadial.setToggleGroup(grpTypes);
        rdoRadial.setUserData(1);
        
    	// create the cycle type options
        ToggleGroup grpCycleTypes = new ToggleGroup();
        RadioButton rdoCycleNone = new RadioButton(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.NONE));
        rdoCycleNone.setToggleGroup(grpCycleTypes);
        rdoCycleNone.setUserData(SlideGradientCycleType.NONE);
        rdoCycleNone.setSelected(true);
        
        RadioButton rdoCycleReflect = new RadioButton(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.REFLECT));
        rdoCycleReflect.setToggleGroup(grpCycleTypes);
        rdoCycleReflect.setUserData(SlideGradientCycleType.REFLECT);
        
        RadioButton rdoCycleRepeat = new RadioButton(Translations.get("slide.gradient.cycle." + SlideGradientCycleType.REPEAT));
        rdoCycleRepeat.setToggleGroup(grpCycleTypes);
        rdoCycleRepeat.setUserData(SlideGradientCycleType.REPEAT);
        
        // stop 1 offset slider
        Slider sldStop1 = new Slider(0, 1, 0);
        sldStop1.valueProperty().bindBidirectional(this.stop1Offset);
        
        // stop 1 color
        ColorPicker pkrStop1 = new ColorPicker(Color.BLACK);
        pkrStop1.setStyle("-fx-color-label-visible: false;");
        pkrStop1.valueProperty().bindBidirectional(this.stop1Color);
        
        // stop 2 offset slider
        Slider sldStop2 = new Slider(0, 1, 1);
        sldStop2.valueProperty().bindBidirectional(this.stop2Offset);
        
        // stop 2 color
        ColorPicker pkrStop2 = new ColorPicker(Color.WHITE);
        pkrStop2.setStyle("-fx-color-label-visible: false;");
        pkrStop2.valueProperty().bindBidirectional(this.stop2Color);
        
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
        bg.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
        
        // the preview pane itself
        Pane preview = new Pane();
        preview.setPrefSize(WIDTH, HEIGHT);
        preview.setMaxSize(WIDTH, HEIGHT);
        preview.setMinSize(WIDTH, HEIGHT);
        preview.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(1))));
        preview.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
        	return new Background(new BackgroundFill(PaintConverter.toJavaFX(this.value.get()), CornerRadii.EMPTY, Insets.EMPTY));
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
            final double px = clamp(this.handle1X.get() + x / WIDTH, 0, 1);
            final double py = clamp(this.handle1Y.get() + y / HEIGHT, 0, 1);
        	
            // set the start x/y
        	this.handle1X.set(px);
        	this.handle1Y.set(py);
        };
        
        handle1.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
        	double x1 = clamp(this.handle1X.get(), 0, 1);
            return x1 * WIDTH;
        }, this.handle1X));

        handle1.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
        	double y1 = clamp(this.handle1Y.get(), 0, 1);
            return y1 * HEIGHT;
        }, this.handle1Y));
        
        EventHandler<MouseEvent> handle2MouseHandler = event -> {
        	// get the change in x and y
            final double x = event.getX();
            final double y = event.getY();
        	
            // compute the new x/y between 0 and 1
            final double px = clamp(this.handle2X.get() + x / WIDTH, 0, 1);
            final double py = clamp(this.handle2Y.get() + y / HEIGHT, 0, 1);
            
            // set the end x/y
            this.handle2X.set(px);
            this.handle2Y.set(py);
        };
        
        handle2.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
        	double x2 = clamp(this.handle2X.get(), 0, 1);
            return x2 * WIDTH;
        }, this.handle2X));

        handle2.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
        	double y2 = clamp(this.handle2Y.get(), 0, 1);
            return y2 * HEIGHT;
        }, this.handle2Y));
        
        // set the handlers
        handle1.setOnMouseDragged(handle1MouseHandler);
        handle2.setOnMouseDragged(handle2MouseHandler);
        handle2.setCursor(Cursor.HAND);
        handle1.setCursor(Cursor.HAND);
        
        HBox pp = new HBox(previewStack);
        pp.setAlignment(Pos.CENTER);
        
        HBox stop1 = new HBox(5, sldStop1, pkrStop1);
        stop1.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sldStop1, Priority.ALWAYS);
        
        HBox stop2 = new HBox(5, sldStop2, pkrStop2);
        stop2.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sldStop2, Priority.ALWAYS);
        
        // build the layout
        this.setSpacing(5);
        this.getChildren().addAll(
        		pp,
        		new HBox(5, rdoLinear, rdoRadial),
        		stop1,
        		stop2,
        		new HBox(5, rdoCycleNone, rdoCycleReflect, rdoCycleRepeat));
        
		// bindings
		
        rdoLinear.setOnAction(e -> {
        	this.isLinear.set(true);
        });
        rdoRadial.setOnAction(e -> {
        	this.isLinear.set(false);
        });
        this.isLinear.addListener((obs, ov, nv) -> {
        	if (!nv) rdoRadial.setSelected(true);
        	else rdoLinear.setSelected(true);
        });
        
        rdoCycleNone.setOnAction(e -> {
        	this.cycleType.set(SlideGradientCycleType.NONE);
        });
        rdoCycleReflect.setOnAction(e -> {
        	this.cycleType.set(SlideGradientCycleType.REFLECT);
        });
        rdoCycleRepeat.setOnAction(e -> {
        	this.cycleType.set(SlideGradientCycleType.REPEAT);
        });
        this.cycleType.addListener((obs, ov, nv) -> {
        	if (nv == SlideGradientCycleType.REPEAT) rdoCycleRepeat.setSelected(true);
        	else if (nv == SlideGradientCycleType.REFLECT) rdoCycleReflect.setSelected(true);
        	else rdoCycleNone.setSelected(true);
        });
        
		BindingHelper.bindBidirectional(this.isLinear, this.value, new ObjectConverter<Boolean, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Boolean t) {
				return SlideGradientPicker.this.getControlValues();
			}
			@Override
			public Boolean convertTo(SlideGradient e) {
				if (e == null) return false;
				return e.getType() == SlideGradientType.LINEAR;
			}
		});
		
		BindingHelper.bindBidirectional(this.cycleType, this.value, new ObjectConverter<SlideGradientCycleType, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(SlideGradientCycleType t) {
				return SlideGradientPicker.this.getControlValues();
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
				return SlideGradientPicker.this.getControlValues();
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
				return SlideGradientPicker.this.getControlValues();
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
				return SlideGradientPicker.this.getControlValues();
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
				return SlideGradientPicker.this.getControlValues();
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
				return SlideGradientPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle1X.get();
				return clamp(e.getStartX(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle1Y, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle1Y.get();
				return clamp(e.getStartY(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle2X, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle2X.get();
				return clamp(e.getEndX(), 0, 1);
			}
		});
		
		BindingHelper.bindBidirectional(this.handle2Y, this.value, new ObjectConverter<Number, SlideGradient>() {
			@Override
			public SlideGradient convertFrom(Number t) {
				return SlideGradientPicker.this.getControlValues();
			}
			@Override
			public Number convertTo(SlideGradient e) {
				if (e == null) return handle2Y.get();
				return clamp(e.getEndY(), 0, 1);
			}
		});
    }

    /**
     * Returns a new gradient given the current input.
     * @return SlideGradient
     */
    private SlideGradient getControlValues() {
		SlideGradient sg = new SlideGradient();
		sg.setType(!this.isLinear.get() ? SlideGradientType.RADIAL : SlideGradientType.LINEAR);
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
    
    /**
     * Clamps the given value between min and max.
     * @param value the value
     * @param min the min value
     * @param max the max value
     * @return double
     */
    private static double clamp(double value, double min, double max) {
        return value < min 
        		? min 
        		: value > max 
        			? max 
        			: value;
    }

    /**
     * Returns the gradient property.
     * @return ObjectProperty&lt;{@link SlideGradient}&gt;
     */
    public ObjectProperty<SlideGradient> valueProperty() {
    	return this.value;
    }
    
    /**
     * Returns the current gradient.
     * @return {@link SlideGradient}
     */
    public SlideGradient getValue() {
    	return this.value.get();
    }
    
    /**
     * Sets the current gradient.
     * @param gradient the gradient
     */
    public void setValue(SlideGradient gradient) {
    	this.value.set(gradient);
    }
}