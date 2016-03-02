package org.praisenter.javafx;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.javafx.utility.JavaFxNodeHelper;
import org.praisenter.utility.ClasspathLoader;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public final class GradientPicker extends VBox {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/javafx/resources/transparent.png");
	
	final static double WIDTH = 200;
    final static double HEIGHT = 200;
	
	private final ObjectProperty<Paint> paintProperty = new SimpleObjectProperty<Paint>() {
		public void set(Paint paint) {
			if (paint instanceof LinearGradient) {
	    		LinearGradient lg = (LinearGradient)paint;
	    		
	    		final double x1 = clamp(lg.getStartX(), 0, 1);
	    		final double y1 = clamp(lg.getStartY(), 0, 1);
	    		final double x2 = clamp(lg.getEndX(), 0, 1);
	    		final double y2 = clamp(lg.getEndY(), 0, 1);
	    		
	    		type.set(0);
	    		rdoLinear.setSelected(true);
	    		cycle.set(lg.getCycleMethod());
	    		switch (lg.getCycleMethod()) {
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
	    		List<Stop> stops = lg.getStops();
	    		if (stops != null && stops.size() > 0) {
	    			Stop s1 = stops.get(0);
	    			stop1.set(s1);
	    			sldStop1.setValue(s1.getOffset());
	    			pkrStop1.setValue(s1.getColor());
	    			if (stops.size() > 1) {
	    				Stop s2 = stops.get(1);
	    				stop2.set(s2);
	    				sldStop2.setValue(s2.getOffset());
	    				pkrStop2.setValue(s2.getColor());
	    			}
	    		}
	    		
	    		// set the handle locations
	    		handle1.setLayoutX(x1 * preview.getWidth());
	    		handle1.setLayoutY(y1 * preview.getHeight());
	    		handle2.setLayoutX(x2 * preview.getWidth());
	    		handle2.setLayoutY(y2 * preview.getHeight());
	    	} else if (paint instanceof RadialGradient) {
	    		RadialGradient rg = (RadialGradient)paint;
	    		
	    		final double sqrt2 = Math.sqrt(2.0);
	    		final double r = clamp(rg.getRadius(), 0, 1);
	    		final double x1 = clamp(rg.getCenterX(), 0, 1);
	    		final double y1 = clamp(rg.getCenterY(), 0, 1);
	    		final double x2 = clamp(x1 + sqrt2 * r, 0, 1);
	    		final double y2 = clamp(y1 + sqrt2 * r, 0, 1);
	    		
	    		type.set(1);
	    		rdoRadial.setSelected(true);
	    		cycle.set(rg.getCycleMethod());
	    		switch (rg.getCycleMethod()) {
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
	    		List<Stop> stops = rg.getStops();
	    		if (stops != null && stops.size() > 0) {
	    			Stop s1 = stops.get(0);
	    			stop1.set(s1);
	    			sldStop1.setValue(s1.getOffset());
	    			pkrStop1.setValue(s1.getColor());
	    			if (stops.size() > 1) {
	    				Stop s2 = stops.get(1);
	    				stop2.set(s2);
	    				sldStop2.setValue(s2.getOffset());
	    				pkrStop2.setValue(s2.getColor());
	    			}
	    		}
	    		
	    		// set the handle locations
	    		handle1.setLayoutX(x1 * preview.getWidth());
	    		handle1.setLayoutY(y1 * preview.getHeight());
	    		handle2.setLayoutX(x2 * preview.getWidth());
	    		handle2.setLayoutY(y2 * preview.getHeight());
	    	}
			
			// doing this will mean that setting it to null or any other type of paint will do nothing
			// since it will just use the current observables to generate a new paint
			
			// this has the added effect of allowing us to update the paint property without having
			// it go through the conversion process above
			super.set(createPaint());
		}
	};
	
	private final IntegerProperty type = new SimpleIntegerProperty(0);
	private final ObjectProperty<CycleMethod> cycle = new SimpleObjectProperty<CycleMethod>(CycleMethod.NO_CYCLE);
	
	// Stop 1
	private final ObjectProperty<Stop> stop1 = new SimpleObjectProperty<Stop>(new Stop(0, Color.WHITE));
	
	// Stop 2
	private final ObjectProperty<Stop> stop2 = new SimpleObjectProperty<Stop>(new Stop(1, Color.rgb(0, 0, 0, 0.5)));
	
	// handle positions
	private final DoubleProperty handle1X = new SimpleDoubleProperty(0);
	private final DoubleProperty handle1Y = new SimpleDoubleProperty(0);
	private final DoubleProperty handle2X = new SimpleDoubleProperty(1);
	private final DoubleProperty handle2Y = new SimpleDoubleProperty(1);
	
	// radial specific
	private final DoubleProperty radius = new SimpleDoubleProperty(1);
	
	// nodes
	
	private final RadioButton rdoLinear;
	private final RadioButton rdoRadial;
	private final RadioButton rdoCycleNone;
	private final RadioButton rdoCycleReflect;
	private final RadioButton rdoCycleRepeat;
	private final Slider sldStop1;
	private final ColorPicker pkrStop1;
	private final Slider sldStop2;
	private final ColorPicker pkrStop2;
	
	private final Pane preview;
	private final Shape handle1;
	private final Shape handle2;
	
	// TODO add code so that this operates similar to the color picker 
	// see http://stackoverflow.com/questions/27171885/display-custom-color-dialog-directly-javafx-colorpicker
    public GradientPicker() {
        // set the padding
    	setPadding(new Insets(10));
    	setSpacing(5);

    	// create the gradient type options
    	// TODO translate
        ToggleGroup grpTypes = new ToggleGroup();
        this.rdoLinear = new RadioButton("Linear");
        this.rdoLinear.setToggleGroup(grpTypes);
        this.rdoLinear.setUserData(0);
        this.rdoLinear.setSelected(true);
        this.rdoRadial = new RadioButton("Radial");
        this.rdoRadial.setToggleGroup(grpTypes);
        this.rdoRadial.setUserData(1);
        grpTypes.selectedToggleProperty().addListener((obs, ov, nv) -> {
        	type.set((Integer)nv.getUserData());
        	this.paintProperty.set(null);
        });
        HBox typeRow = new HBox();
        typeRow.setSpacing(5);
        typeRow.getChildren().addAll(this.rdoLinear, this.rdoRadial);
        
    	// create the cycle type options
    	// TODO translate
        ToggleGroup grpCycleTypes = new ToggleGroup();
        this.rdoCycleNone = new RadioButton("None");
        this.rdoCycleNone.setToggleGroup(grpCycleTypes);
        this.rdoCycleNone.setUserData(CycleMethod.NO_CYCLE);
        this.rdoCycleNone.setSelected(true);
        this.rdoCycleReflect = new RadioButton("Reflect");
        this.rdoCycleReflect.setToggleGroup(grpCycleTypes);
        this.rdoCycleReflect.setUserData(CycleMethod.REFLECT);
        this.rdoCycleRepeat = new RadioButton("Repeat");
        this.rdoCycleRepeat.setToggleGroup(grpCycleTypes);
        this.rdoCycleRepeat.setUserData(CycleMethod.REPEAT);
        grpCycleTypes.selectedToggleProperty().addListener((obs, ov, nv) -> {
        	cycle.set((CycleMethod)nv.getUserData());
        	this.paintProperty.set(null);
        });
        HBox cycleRow = new HBox();
        cycleRow.setSpacing(5);
        cycleRow.getChildren().addAll(this.rdoCycleNone, this.rdoCycleReflect, this.rdoCycleRepeat);
        
        // stop 1 offset slider
        this.sldStop1 = new Slider(0, 1, 0);
        this.sldStop1.valueProperty().addListener((obs, ov, nv) -> {
        	this.stop1.set(new Stop(nv.doubleValue(), this.stop1.get().getColor()));
        	this.paintProperty.set(null);
        });
        
        // stop 1 color
        this.pkrStop1 = new ColorPicker(Color.WHITE);
        this.pkrStop1.valueProperty().addListener((obs, ov, nv) -> {
        	this.stop1.set(new Stop(0, nv));
        	this.paintProperty.set(null);
        });
        
        // stop 2 offset slider
        this.sldStop2 = new Slider(0, 1, 0);
        this.sldStop2.valueProperty().addListener((obs, ov, nv) -> {
        	this.stop2.set(new Stop(nv.doubleValue(), this.stop2.get().getColor()));
        	this.paintProperty.set(null);
        });
        
        // stop 2 color
        this.pkrStop2 = new ColorPicker(Color.BLACK);
        this.pkrStop2.valueProperty().addListener((obs, ov, nv) -> {
        	this.stop2.set(new Stop(1, nv));
        	this.paintProperty.set(null);
        });
        
        // create the preview/configure view
        StackPane previewStack = new StackPane();
        
        // the transparent (tiled) background
        Pane bg = new Pane();
        bg.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
    	JavaFxNodeHelper.setSize(bg, WIDTH, HEIGHT);
        
        // the preview pane itself
        this.preview = new Pane();
        JavaFxNodeHelper.setSize(this.preview, WIDTH, HEIGHT);
        this.preview.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1.0, 0.0, null), null, new BorderWidths(1))));
        this.preview.setBackground(new Background(new BackgroundFill(this.paintProperty.get(), null, null)));
        this.preview.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
            	// when these change, update the paint
                bind(paintProperty);
            }

            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(paintProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
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
        	this.paintProperty.set(null);
        };

        EventHandler<MouseEvent> handle2MouseHandler = event -> {
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
        	this.paintProperty.set(null);
        };
        
        // set the handlers
        this.handle1.setOnMouseDragged(handle1MouseHandler);
        this.handle2.setOnMouseDragged(handle2MouseHandler);
        
        // add all the controls to the VBox
        getChildren().addAll(typeRow, cycleRow, this.sldStop1, this.pkrStop1, this.sldStop2, this.pkrStop2, previewRow);
        
        // set the default paint based on the default values
    	this.paintProperty.set(null);
    }

    private Paint createPaint() {
    	if (this.type.get() == 0) {
    		return new LinearGradient(
    				this.handle1X.get(), 
    				this.handle1Y.get(), 
    				this.handle2X.get(), 
    				this.handle2Y.get(), 
    				true, 
    				this.cycle.get(), 
    				this.stop1.get(), 
    				this.stop2.get());
    	} else {
    		return new RadialGradient(
    				0.0, 
    				0.0, 
    				this.handle1X.get(), 
    				this.handle1Y.get(), 
    				this.radius.get(), 
    				true, 
    				this.cycle.get(), 
    				this.stop1.get(), 
    				this.stop2.get());
    	}
    }
    
    private static double clamp(double value, double min, double max) {
        return value < min 
        		? min 
        		: value > max 
        			? max 
        			: value;
    }

    public ObjectProperty<Paint> paintProperty() {
    	return this.paintProperty;
    }
    
    public Paint getPaint() {
    	return this.paintProperty.get();
    }
    
    public void setPaint(Paint paint) {
    	this.paintProperty.set(paint);
    }
}