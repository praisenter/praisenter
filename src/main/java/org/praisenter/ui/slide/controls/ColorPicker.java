package org.praisenter.ui.slide.controls;

import java.util.Locale;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

// TODO custom color picker if the OOB one doesn't get fixed

public final class ColorPicker extends VBox {
	// https://palettolithic.com/
	private static final String[][] PALETTE = new String[][] { 
		// gray
	    { "#f2f2f3", "#e5e6e7", "#cacdce", "#b0b3b5", "#959a9d", "#7b8184", "#62676a", "#4a4d50", "#313435", "#181a1b" },
	    // blue
	    { "#e5f4ff", "#cceaff", "#99d4ff", "#66bfff", "#33aaff", "#0095ff", "#0077cc", "#005999", "#003b66", "#001e33" },
	    // indigo
	    { "#e5e8ff", "#ccd0ff", "#99a2ff", "#6673ff", "#3344ff", "#0015ff", "#0011cc", "#000d99", "#000866", "#000433" },
	    //violet
	    { "#f0e5ff", "#e1ccff", "#c399ff", "#a666ff", "#8833ff", "#6a00ff", "#5500cc", "#400099", "#2a0066", "#150033" },
	    // purple
	    { "#fde5ff", "#fbccff", "#f699ff", "#f266ff", "#ee33ff", "#ea00ff", "#bb00cc", "#8c0099", "#5d0066", "#2f0033" },
	    // pink
	    { "#ffe5f4", "#ffccea", "#ff99d5", "#ff66bf", "#ff33aa", "#ff0095", "#cc0077", "#990059", "#66003c", "#33001e" },
	    // red: 
	    { "#ffe5e8", "#ffccd0", "#ff99a1", "#ff6673", "#ff3344", "#ff0015", "#cc0011", "#99000d", "#660008", "#330004" },
	    // orange
	    { "#fff0e5", "#ffe1cc", "#ffc499", "#ffa666", "#ff8833", "#ff6a00", "#cc5500", "#994000", "#662b00", "#331500" },
	    // yellow
	    { "#fffde5", "#fffbcc", "#fff699", "#fff266", "#ffee33", "#ffea00", "#ccbb00", "#998c00", "#665e00", "#332f00" },
	    // lime
	    { "#f4ffe5", "#eaffcc", "#d5ff99", "#bfff66", "#aaff33", "#95ff00", "#77cc00", "#599900", "#3c6600", "#1e3300" },
	    // green
	    { "#e8ffe5", "#d0ffcc", "#a2ff99", "#73ff66", "#44ff33", "#15ff00", "#11cc00", "#0d9900", "#096600", "#043300" },
	    // teal
	    { "#e5fff0", "#ccffe1", "#99ffc4", "#66ffa6", "#33ff88", "#00ff6a", "#00cc55", "#009940", "#00662b", "#003315" },
	    // cyan
	    { "#e5fffd", "#ccfffb", "#99fff7", "#66fff2", "#33ffee", "#00ffea", "#00ccbb", "#00998c", "#00665e", "#00332f" }
	};
	
	
	private final ObjectProperty<Color> value;
	private final ObjectProperty<Color> currentValue;
    
	private ObjectProperty<ColorType> type;
	
	private DoubleProperty hue;
    private DoubleProperty saturation;
    private DoubleProperty brightness;
    
    private IntegerProperty red;
    private IntegerProperty green;
    private IntegerProperty blue;
    
    private IntegerProperty alpha;
    
    private StringProperty web;
    
    private boolean changing = false;
    
	public ColorPicker() {
		this.value = new SimpleObjectProperty<>(Color.BLACK);
		this.currentValue = new SimpleObjectProperty<Color>(Color.BLACK);
		
		// all set-able manually
		this.type = new SimpleObjectProperty<>(ColorType.HSB);
		this.hue = new SimpleDoubleProperty(0);
		this.saturation = new SimpleDoubleProperty(0);
		this.brightness = new SimpleDoubleProperty(0);
		this.red = new SimpleIntegerProperty(0);
		this.green = new SimpleIntegerProperty(0);
		this.blue = new SimpleIntegerProperty(0);
		this.alpha = new SimpleIntegerProperty(100);
		this.web = new SimpleStringProperty("000000");
		
		// build color swatch grid
		
		final int n = PALETTE.length;
		final int m = PALETTE[0].length;
		
		// use css instead
		final double paletteColorSize = 16.0;
		
		GridPane paletteGrid = new GridPane();
		
		// use css instead
		paletteGrid.setVgap(1.0);
		paletteGrid.setHgap(1.0);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				final Color color = Color.web(PALETTE[i][j]);
				Pane pane = new Pane();
				pane.setPrefSize(paletteColorSize, paletteColorSize);
				pane.setMinSize(paletteColorSize, paletteColorSize);
				pane.setMaxSize(paletteColorSize, paletteColorSize);
				pane.setBackground(new Background(new BackgroundFill(color, null, null)));
				pane.setUserData(pane);
				pane.setOnMouseClicked(e -> {
					this.red.set((int)clamp(color.getRed() * 100, 0, 255));
					this.green.set((int)clamp(color.getGreen() * 100, 0, 255));
					this.blue.set((int)clamp(color.getBlue() * 100, 0, 255));
					this.alpha.set(100);
					this.onRGBChanged();
				});
				paletteGrid.add(pane, i, j);
			}
		}
		
		
        // build color rect
		
		
		
		// build hue selector
		
		// build RGB/HSB/Web sliders & text fields
		
		// build opacity slider & text field
		
		this.getChildren().addAll(paletteGrid);
		
		
		
		
		
		
		
//		Dialog<Media> dlgMedia = new Dialog<>();
//		dlgMedia.setTitle(Translations.get("media"));
//		dlgMedia.getDialogPane().setContent(lstMedia);
//		dlgMedia.setResultConverter((button) -> {
//			if (button == ButtonType.OK) {
//				List<?> selected = lstMedia.getSelectedItems();
//				if (selected.size() > 0) {
//					return (Media)selected.get(0);
//				}
//			}
//			// return the original value
//			return this.originalMedia;
//		});
//		dlgMedia.resultProperty().bindBidirectional(this.media);
//		dlgMedia.initOwner(context.getStage());
//		dlgMedia.initStyle(StageStyle.UTILITY);
//		dlgMedia.initModality(Modality.WINDOW_MODAL);
//		dlgMedia.setResizable(true);
//	    dlgMedia.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
//		
//		Button btnMedia = new Button(Translations.get("slide.media.choose"));
//		btnMedia.setAlignment(Pos.BASELINE_LEFT);
//		btnMedia.setMaxWidth(Double.MAX_VALUE);
//		btnMedia.setOnAction(e -> {
//			this.originalMedia = this.media.get();
//			dlgMedia.show();
//			WindowHelper.centerOnParent(this.getScene().getWindow(), dlgMedia);
//		});
		
	}
	
	private void onRGBChanged() {
		Color color = this.getCurrentColor(ColorType.RGB);
		this.alpha.set((int)(color.getOpacity() * 100));
		this.brightness.set(color.getBrightness() * 100);
		this.hue.set(color.getHue() * 100);
		this.saturation.set(color.getSaturation() * 100);
		this.web.set(convertToWeb(color));
	}
	
	private String convertToWeb(Color c) {
		return String.format((Locale) null, "#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
	}
	
	private Color getCurrentColor(ColorType type) {
		double alf = clamp(this.alpha.get() / 100.0, 0.0, 1.0);
		
		switch (type) {
			case HSB:
				double hue = this.hue.get();
				double sat = clamp(this.saturation.get() / 100.0, 0.0, 1.0);
				double bri = clamp(this.brightness.get() / 100.0, 0.0, 1.0);
				return Color.hsb(hue, sat, bri, alf);
			case RGB:
				int r = this.red.get();
				int g = this.green.get();
				int b = this.blue.get();
				return Color.rgb(r, g, b, alf);
			case WEB:
				return Color.web(this.web.get(), alf);
			default:
				// error???
				break;
		}
		
		return null;
	}
	
	private static double clamp(double value, double min, double max) {
        return value < min ? min : value > max ? max : value;
    }
	
    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (double)((1.0 / 255) * x);
            int h = (int)((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }
	
	public Color getValue() {
		return this.value.get();
	}

	public void setValue(Color color) {
		this.value.set(color);
	}
	
	public ObjectProperty<Color> valueProperty() {
		return this.value;
	}
	
	public Color getCurrentValue() {
		return this.currentValue.get();
	}
	
	public void setCurrentValue(Color color) {
		this.currentValue.set(color);
	}
	
	public ObjectProperty<Color> currentValueProperty() {
		return this.currentValue;
	}
}
