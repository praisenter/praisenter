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
package org.praisenter.javafx.slide.editor.controls;

import java.util.List;

import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.graphics.SlideGradient;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlideRadialGradient;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
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
final class SlideGradientPickerPane extends HBox {
	/** The preview pane width */
	private static final double WIDTH = 200;
	
	/** The preview pane height */
	private static final double HEIGHT = 200;
	
    // the current gradient
    
    /** The configured gradient paint */
	private final ObjectProperty<SlideGradient> gradient = new SimpleObjectProperty<SlideGradient>();
	private final ObjectProperty<SlideGradient> temp = new SimpleObjectProperty<SlideGradient>();

	/** True if the controls are being set */
	private boolean mutating = false;
	
	// properties that build the gradient

	/** The first point's x coordinate (start/center x) */
	private final DoubleProperty handle1X = new SimpleDoubleProperty(0);
	
	/** The first point's y coordinate (start/center y) */
	private final DoubleProperty handle1Y = new SimpleDoubleProperty(0);
	
	/** The second point's x coordinate (end x) */
	private final DoubleProperty handle2X = new SimpleDoubleProperty(1);
	
	/** The second point's x coordinate (end y) */
	private final DoubleProperty handle2Y = new SimpleDoubleProperty(1);
	
	/** The radius */
	private final DoubleProperty radius = new SimpleDoubleProperty(1);
	
	// nodes
	
	/** The toggle group for the gradient type */
	private final ToggleGroup grpTypes;
	
	/** The linear gradient type radio button */
	private final RadioButton rdoLinear;
	
	/** The radial gradient type radio button */
	private final RadioButton rdoRadial;
	
	/** The toggle group for the cycle type */
	private final ToggleGroup grpCycleTypes;
	
	/** The cycle none type radio button */
	private final RadioButton rdoCycleNone;
	
	/** The cycle reflect type radio button */
	private final RadioButton rdoCycleReflect;
	
	/** The cycle repeat type radio button */
	private final RadioButton rdoCycleRepeat;
	
	/** The slider for the first stop's offset */
	private final Slider sldStop1;
	
	/** The color picker for the first stop */
	private final ColorPicker pkrStop1;
	
	/** The slider for the second stop's offset */
	private final Slider sldStop2;
	
	/** The color picker for the second stop */
	private final ColorPicker pkrStop2;
	
	/** The preview pane */
	private final Pane preview;
	
	/** The first point's handle shape */
	private final Shape handle1;
	
	/** The second point's handle shape */
	private final Shape handle2;
	
	/**
	 * Default constructor.
	 */
    public SlideGradientPickerPane() {
        // set the padding
    	setPadding(new Insets(10));
    	setSpacing(7);

    	this.gradient.addListener((obs, ov, nv) -> {
    		if (mutating) return;
    		mutating = true;
    		setControlValues(nv);
    		mutating = false;
    	});
    	
    	InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				mutating = true;
				gradient.set(getControlValues());
				temp.set(getControlValues());
				mutating = false;
			}
		};
    	
    	// create the gradient type options
		this.grpTypes = new ToggleGroup();
        this.rdoLinear = new RadioButton(Translations.get("gradient.type.linear"));
        this.rdoLinear.setToggleGroup(this.grpTypes);
        this.rdoLinear.setUserData(0);
        this.rdoLinear.setSelected(true);
        this.rdoRadial = new RadioButton(Translations.get("gradient.type.radial"));
        this.rdoRadial.setToggleGroup(this.grpTypes);
        this.rdoRadial.setUserData(1);
        this.grpTypes.selectedToggleProperty().addListener(listener);
        HBox typeRow = new HBox();
        typeRow.setSpacing(5);
        typeRow.getChildren().addAll(this.rdoLinear, this.rdoRadial);
        
    	// create the cycle type options
        this.grpCycleTypes = new ToggleGroup();
        this.rdoCycleNone = new RadioButton(Translations.get("gradient.cycle.none"));
        this.rdoCycleNone.setToggleGroup(this.grpCycleTypes);
        this.rdoCycleNone.setUserData(SlideGradientCycleType.NONE);
        this.rdoCycleNone.setSelected(true);
        this.rdoCycleReflect = new RadioButton(Translations.get("gradient.cycle.reflect"));
        this.rdoCycleReflect.setToggleGroup(this.grpCycleTypes);
        this.rdoCycleReflect.setUserData(SlideGradientCycleType.REFLECT);
        this.rdoCycleRepeat = new RadioButton(Translations.get("gradient.cycle.repeat"));
        this.rdoCycleRepeat.setToggleGroup(this.grpCycleTypes);
        this.rdoCycleRepeat.setUserData(SlideGradientCycleType.REPEAT);
        this.grpCycleTypes.selectedToggleProperty().addListener(listener);
        HBox cycleRow = new HBox();
        cycleRow.setSpacing(5);
        cycleRow.getChildren().addAll(this.rdoCycleNone, this.rdoCycleReflect, this.rdoCycleRepeat);
        
        // stop 1 offset slider
        this.sldStop1 = new Slider(0, 1, 0);
        this.sldStop1.setPrefWidth(50);
        this.sldStop1.valueProperty().addListener((obs, ov, nv) -> {
        	if (!this.sldStop1.isValueChanging()) {
        		listener.invalidated(obs);
        	} else {
            	if (mutating) return;
    			mutating = true;
    			temp.set(getControlValues());
    			mutating = false;
        	}
        });
        this.sldStop1.valueChangingProperty().addListener((obs, ov, nv) -> {
        	if (!nv) {
        		listener.invalidated(obs);
        	}
        });
        
        // stop 1 color
        this.pkrStop1 = new ColorPicker(Color.BLACK);
        this.pkrStop1.valueProperty().addListener(listener);
        
        // stop 2 offset slider
        this.sldStop2 = new Slider(0, 1, 1);
        this.sldStop2.setPrefWidth(50);
        this.sldStop2.valueProperty().addListener((obs, ov, nv) -> {
        	if (!this.sldStop2.isValueChanging()) {
        		listener.invalidated(obs);
        	} else {
            	if (mutating) return;
    			mutating = true;
    			temp.set(getControlValues());
    			mutating = false;
        	}
        });
        this.sldStop2.valueChangingProperty().addListener((obs, ov, nv) -> {
        	if (!nv) {
        		listener.invalidated(obs);
        	}
        });
        
        // stop 2 color
        this.pkrStop2 = new ColorPicker(Color.WHITE);
        this.pkrStop2.valueProperty().addListener(listener);
        
        // create the preview/configure view
        StackPane previewStack = new StackPane();
        
        // the transparent (tiled) background
        Pane bg = new Pane();
        bg.setBackground(new Background(new BackgroundImage(Fx.TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
    	Fx.setSize(bg, WIDTH, HEIGHT);
        
        // the preview pane itself
        this.preview = new Pane();
        Fx.setSize(this.preview, WIDTH, HEIGHT);
        this.preview.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(1))));
        this.preview.setBackground(new Background(new BackgroundFill(PaintConverter.toJavaFX(this.temp.get()), null, null)));
        this.preview.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
            	// when the paint changes, update the background
                bind(temp);
            }
            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(PaintConverter.toJavaFX(temp.get()), CornerRadii.EMPTY, Insets.EMPTY));
            }
        }); 
        
        // the configuration handles
        this.handle1 = new Circle(5);
        this.handle1.setStroke(Color.WHITE);
        this.handle1.setStrokeWidth(2);
        this.handle1.setManaged(false);
        this.handle1.setFill(Color.BLACK);
        this.handle2 = new Circle(5);
        this.handle2.setStroke(Color.BLACK);
        this.handle2.setFill(Color.WHITE);
        this.handle2.setStrokeWidth(2);
        this.handle2.setManaged(false);
        this.handle2.setLayoutX(WIDTH);
        this.handle2.setLayoutY(HEIGHT);
        
        // add them all to the preview stack
        previewStack.getChildren().addAll(bg, this.preview, this.handle1, this.handle2);
        
        // put the preview stack into an HBox so it doesn't try to center it when resized
        HBox previewRow = new HBox();
        previewRow.setAlignment(Pos.TOP_LEFT);
        previewRow.getChildren().add(previewStack);
        
        // wire up the mouse handlers for the handles
        EventHandler<MouseEvent> handle1MouseHandler = event -> {
        	mutating = true;
        	// get the change in x and y
            final double x = event.getX();
            final double y = event.getY();
            // compute the new x/y between 0 and 1
            final double px = clamp(handle1X.get() + x / WIDTH, 0, 1);
            final double py = clamp(handle1Y.get() + y / HEIGHT, 0, 1);
        	// set the start x/y
        	handle1X.set(px);
        	handle1Y.set(py);
        	// compute a new radius
        	final double cx = handle2X.get();
            final double cy = handle2Y.get();
            final double dx = px - cx;
            final double dy = py - cy;
            radius.set(Math.sqrt(dx * dx + dy * dy));
        	// move the handle
        	handle1.setLayoutX(clamp(handle1.getLayoutX() + x, 0, WIDTH));
        	handle1.setLayoutY(clamp(handle1.getLayoutY() + y, 0, HEIGHT));
        	// set the temp value so the user can see the change
        	this.temp.set(getControlValues());
        	mutating = false;
        };

        EventHandler<MouseEvent> handle2MouseHandler = event -> {
        	mutating = true;
        	// get the change in x and y
            final double x = event.getX();
            final double y = event.getY();
        	// compute the new x/y between 0 and 1
            final double px = clamp(handle2X.get() + x / WIDTH, 0, 1);
            final double py = clamp(handle2Y.get() + y / HEIGHT, 0, 1);
            // set the end x/y
            handle2X.set(px);
            handle2Y.set(py);
            // compute a new radius
            final double cx = handle1X.get();
            final double cy = handle1Y.get();
            final double dx = px - cx;
            final double dy = py - cy;
            radius.set(Math.sqrt(dx * dx + dy * dy));
            // move the handle
            handle2.setLayoutX(clamp(handle2.getLayoutX() + x, 0, WIDTH));
        	handle2.setLayoutY(clamp(handle2.getLayoutY() + y, 0, HEIGHT));
        	// set the temp value so the user can see the change
        	this.temp.set(getControlValues());
        	mutating = false;
        };
        
        EventHandler<MouseEvent> handleEnter = event -> {
        	getScene().setCursor(Cursor.HAND);
        };
    	EventHandler<MouseEvent> handleExit = event -> {
    		getScene().setCursor(Cursor.DEFAULT);
        };
        
        // set the handlers
        this.handle1.setOnMouseDragged(handle1MouseHandler);
        this.handle2.setOnMouseDragged(handle2MouseHandler);
        // when the mouse pointer is released, then change the primary value of the control
        this.handle1.setOnMouseReleased(e -> { this.gradient.set(this.getControlValues()); });
        this.handle2.setOnMouseReleased(e -> { this.gradient.set(this.getControlValues()); });
        // change the cursor based on hover over the handles
        this.handle1.setOnMouseEntered(handleEnter);
        this.handle1.setOnMouseExited(handleExit);
        this.handle2.setOnMouseEntered(handleEnter);
        this.handle2.setOnMouseExited(handleExit);
        
        // add all the controls
        VBox left = new VBox();
        left.setSpacing(7);
        left.getChildren().addAll(typeRow, this.pkrStop1, this.sldStop1, this.pkrStop2, this.sldStop2, cycleRow);
        getChildren().addAll(left, previewRow);
    }

    /**
     * Returns a new gradient given the current input.
     * @return SlideGradient
     */
    private SlideGradient getControlValues() {
    	if (this.rdoLinear.isSelected()) {
    		return new SlideLinearGradient(
    				this.handle1X.get(), 
    				this.handle1Y.get(), 
    				this.handle2X.get(), 
    				this.handle2Y.get(), 
    				(SlideGradientCycleType)this.grpCycleTypes.getSelectedToggle().getUserData(), 
    				new SlideGradientStop(this.sldStop1.getValue(), PaintConverter.fromJavaFX(this.pkrStop1.getValue())), 
    				new SlideGradientStop(this.sldStop2.getValue(), PaintConverter.fromJavaFX(this.pkrStop2.getValue())));
    	} else {
    		return new SlideRadialGradient(
    				this.handle1X.get(), 
    				this.handle1Y.get(), 
    				this.radius.get(), 
    				(SlideGradientCycleType)this.grpCycleTypes.getSelectedToggle().getUserData(), 
    				new SlideGradientStop(this.sldStop1.getValue(), PaintConverter.fromJavaFX(this.pkrStop1.getValue())), 
    				new SlideGradientStop(this.sldStop2.getValue(), PaintConverter.fromJavaFX(this.pkrStop2.getValue())));
    	}
    }
    
    /**
	 * Sets the values of the controls to the given gradient values.
	 * @param gradient the gradient
	 */
    private void setControlValues(SlideGradient gradient) {
    	if (gradient instanceof SlideLinearGradient) {
    		SlideLinearGradient lg = (SlideLinearGradient)gradient;
    		
    		final double x1 = clamp(lg.getStartX(), 0, 1);
    		final double y1 = clamp(lg.getStartY(), 0, 1);
    		final double x2 = clamp(lg.getEndX(), 0, 1);
    		final double y2 = clamp(lg.getEndY(), 0, 1);
    		
    		rdoLinear.setSelected(true);
    		switch (lg.getCycleType()) {
    			case REFLECT:
    				rdoCycleReflect.setSelected(true);
    				break;
    			case REPEAT:
    				rdoCycleRepeat.setSelected(true);
    				break;
				default:
    				rdoCycleNone.setSelected(true);	
    		}
    		handle1X.set(x1);
    		handle1Y.set(y1);
    		handle2X.set(x2);
    		handle2Y.set(y2);
    		
    		// stops
    		List<SlideGradientStop> stops = lg.getStops();
    		if (stops != null && stops.size() > 0) {
    			SlideGradientStop s1 = stops.get(0);
    			sldStop1.setValue(s1.getOffset());
    			pkrStop1.setValue(PaintConverter.toJavaFX(s1.getColor()));
    			if (stops.size() > 1) {
    				SlideGradientStop s2 = stops.get(1);
    				sldStop2.setValue(s2.getOffset());
    				pkrStop2.setValue(PaintConverter.toJavaFX(s2.getColor()));
    			}
    		}
    		
    		// set the handle locations
    		handle1.setLayoutX(x1 * WIDTH);
    		handle1.setLayoutY(y1 * HEIGHT);
    		handle2.setLayoutX(x2 * WIDTH);
    		handle2.setLayoutY(y2 * HEIGHT);
    	} else if (gradient instanceof SlideRadialGradient) {
    		SlideRadialGradient rg = (SlideRadialGradient)gradient;
    		
    		final double sqrt2 = Math.sqrt(2.0);
    		final double r = clamp(rg.getRadius(), 0, 1);
    		final double x1 = clamp(rg.getCenterX(), 0, 1);
    		final double y1 = clamp(rg.getCenterY(), 0, 1);
    		final double x2 = clamp(x1 + sqrt2 * r, 0, 1);
    		final double y2 = clamp(y1 + sqrt2 * r, 0, 1);
    		
    		rdoRadial.setSelected(true);
    		switch (rg.getCycleType()) {
    			case REFLECT:
    				rdoCycleReflect.setSelected(true);
    				break;
    			case REPEAT:
    				rdoCycleRepeat.setSelected(true);
    				break;
				default:
    				rdoCycleNone.setSelected(true);	
    		}
    		handle1X.set(x1);
    		handle1Y.set(y1);
    		handle2X.set(x2);
    		handle2Y.set(y2);
    		
    		// we have to recompute the radius since the radius could
    		// be bigger than what we allow (for example if the center
    		// is at (0.5, 0.5) the max radius is sqrt(0.5) instead of 1
    		final double d1 = x2 - x1;
    		final double d2 = y2 - y1;
    		radius.set(Math.sqrt(d1 * d1 + d2 * d2));
    		
    		// stops
    		List<SlideGradientStop> stops = rg.getStops();
    		if (stops != null && stops.size() > 0) {
    			SlideGradientStop s1 = stops.get(0);
    			sldStop1.setValue(s1.getOffset());
    			pkrStop1.setValue(PaintConverter.toJavaFX(s1.getColor()));
    			if (stops.size() > 1) {
    				SlideGradientStop s2 = stops.get(1);
    				sldStop2.setValue(s2.getOffset());
    				pkrStop2.setValue(PaintConverter.toJavaFX(s2.getColor()));
    			}
    		}
    		
    		// set the handle locations
    		handle1.setLayoutX(x1 * WIDTH);
    		handle1.setLayoutY(y1 * HEIGHT);
    		handle2.setLayoutX(x2 * WIDTH);
    		handle2.setLayoutY(y2 * HEIGHT);
    	}
    	
    	this.temp.set(this.gradient.get());
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
    public ObjectProperty<SlideGradient> gradientProperty() {
    	return this.gradient;
    }
    
    /**
     * Returns the current gradient.
     * @return {@link SlideGradient}
     */
    public SlideGradient getGradient() {
    	return this.gradient.get();
    }
    
    /**
     * Sets the current gradient.
     * @param gradient the gradient
     */
    public void setGradient(SlideGradient gradient) {
    	this.gradient.set(gradient);
    }
}