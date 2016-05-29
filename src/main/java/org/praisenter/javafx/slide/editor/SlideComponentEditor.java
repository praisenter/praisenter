package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

// TODO translate

public final class SlideComponentEditor extends GridPane {

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// data
	
	final ObjectProperty<ObservableSlideComponent<?>> component = new SimpleObjectProperty<ObservableSlideComponent<?>>();
	
	boolean updating = false;
	
	// controls
	
	// base component
	final SlidePaintPicker pkrBackground;
	// TODO need a border picker
	
	// media component
	final SlidePaintPicker pkrMedia;
	
	// text component
	final SlidePaintPicker pkrTextPaint;
	// TODO text border
	final FontPicker pkrFont;
	final SegmentedButton segHorizontalAlignment;
	final SegmentedButton segVerticalAlignment;
	final ChoiceBox<Option<FontScaleType>> cmbFontScaling;
	final Spinner<Double> spnPadding;
	final Spinner<Double> spnLineSpacing;
	
	// basic text component
	final TextField txtText;
	
	// date-time component
	final ChoiceBox<Option<SimpleDateFormat>> cmbDateTimeFormat;
	
	// text placeholder component
	final ChoiceBox<Option<PlaceholderType>> cmbPlaceholderType;
	final CheckComboBox<Option<PlaceholderVariant>> cmbPlaceholderVariants;
	
	public SlideComponentEditor(PraisenterContext context) {
		ObservableList<Option<FontScaleType>> fontScaleTypes = FXCollections.observableArrayList();
		fontScaleTypes.add(new Option<FontScaleType>("None", FontScaleType.NONE));
		fontScaleTypes.add(new Option<FontScaleType>("Reduce Size Only", FontScaleType.REDUCE_SIZE_ONLY));
		fontScaleTypes.add(new Option<FontScaleType>("Fill", FontScaleType.BEST_FIT));
		
		ObservableList<Option<SimpleDateFormat>> dateTimeFormats = FXCollections.observableArrayList();
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sunday January, 1 2013", new SimpleDateFormat("EEEE MMMM, d yyyy")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sunday January, 1 2013 4:23 PM", new SimpleDateFormat("EEEE MMMM, d yyyy h:mm a")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sunday January, 1 2013 4:23 PM EST", new SimpleDateFormat("EEEE MMMM, d yyyy h:mm a z")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sun Jan, 1 2013", new SimpleDateFormat("EEE MMM, d yyyy")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sun Jan, 1 2013 4:23 PM", new SimpleDateFormat("EEE MMM, d yyyy h:mm a")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("Sun Jan, 1 2013 4:23 PM EST", new SimpleDateFormat("EEE MMM, d yyyy h:mm a z")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("1/1/2013", new SimpleDateFormat("M/d/yyyy")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("1/1/2013 4:23 PM", new SimpleDateFormat("M/d/yyyy h:mm a")));
		dateTimeFormats.add(new Option<SimpleDateFormat>("1/1/2013 4:23 PM EST", new SimpleDateFormat("M/d/yyyy h:mm a z")));

		ObservableList<Option<PlaceholderType>> placeholderTypes = FXCollections.observableArrayList();
		placeholderTypes.add(new Option<PlaceholderType>("Text", PlaceholderType.TEXT));
		placeholderTypes.add(new Option<PlaceholderType>("Title", PlaceholderType.TITLE));
		
		// FEATURE add more variants
		ObservableList<Option<PlaceholderVariant>> placeholderVariants = FXCollections.observableArrayList();
		placeholderVariants.add(new Option<PlaceholderVariant>("Primary", PlaceholderVariant.PRIMARY));
		placeholderVariants.add(new Option<PlaceholderVariant>("Secondary", PlaceholderVariant.SECONDARY));
		
		// base component
		// background
		Label lblBackground = new Label("Background");
		this.pkrBackground = new SlidePaintPicker(context, 
				PaintType.NONE, 
				PaintType.COLOR, 
				PaintType.GRADIENT, 
				PaintType.IMAGE, 
				PaintType.VIDEO);
		// border
		// re-ordering
		
		// media component
		// media
		Label lblMedia = new Label("Media");
		this.pkrMedia = new SlidePaintPicker(context, 
				PaintType.NONE, 
				PaintType.IMAGE, 
				PaintType.VIDEO, 
				PaintType.AUDIO);
		
		// text component
		// paint
		Label lblTextFill = new Label("Text Fill");
		this.pkrTextPaint = new SlidePaintPicker(context, 
				PaintType.NONE, 
				PaintType.COLOR, 
				PaintType.GRADIENT);
		// border
		// font
		Label lblFont = new Label("Font");
		this.pkrFont = new FontPicker(Font.font("Arial", 30), FXCollections.observableArrayList(Font.getFamilies()));
		// h-align
		ToggleButton tglLeft = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT));
		ToggleButton tglRight = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT));
		ToggleButton tglCenter = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER));
		ToggleButton tglJustify = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY));
		tglLeft.setUserData(HorizontalTextAlignment.LEFT);
		tglRight.setUserData(HorizontalTextAlignment.RIGHT);
		tglCenter.setUserData(HorizontalTextAlignment.CENTER);
		tglJustify.setUserData(HorizontalTextAlignment.JUSTIFY);
		this.segHorizontalAlignment = new SegmentedButton(tglLeft, tglRight, tglCenter, tglJustify);
		// v-align
		// FIXME vertical align icons
		ToggleButton tglTop = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.LIST));
		ToggleButton tglMiddle = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.LIST_ALT));
		ToggleButton tglBottom = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.LIST_OL));
		tglTop.setUserData(VerticalTextAlignment.TOP);
		tglMiddle.setUserData(VerticalTextAlignment.CENTER);
		tglBottom.setUserData(VerticalTextAlignment.BOTTOM);
		this.segVerticalAlignment = new SegmentedButton(tglTop, tglMiddle, tglBottom);
		// font scale
		Label lblFontScaling = new Label("Sizing");
		this.cmbFontScaling = new ChoiceBox<Option<FontScaleType>>(fontScaleTypes);
		// padding
		Label lblPadding = new Label("Padding");
		this.spnPadding = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		// line spacing
		Label lblLineSpacing = new Label("Line Spacing");
		this.spnLineSpacing = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		
		// basic text component
		// text
		Label lblText = new Label("Text");
		this.txtText = new TextField();
		
		// date-time component
		// format
		Label lblFormat = new Label("Format");
		this.cmbDateTimeFormat = new ChoiceBox<Option<SimpleDateFormat>>(dateTimeFormats);
		
		// text placeholder component
		// type
		Label lblPlaceholderType = new Label("Type");
		this.cmbPlaceholderType = new ChoiceBox<Option<PlaceholderType>>(placeholderTypes);
		// variants
		Label lblPlaceholderVariants = new Label("Variants");
		this.cmbPlaceholderVariants = new CheckComboBox<Option<PlaceholderVariant>>(placeholderVariants);
		
		// create a node array of all the nodes we need to
		// remove from the layout when the type of the component changes
		Node[] all = new Node[] {
			// media
			lblMedia,
			pkrMedia,
			// text
			lblTextFill,
			pkrTextPaint,
			lblFont,
			pkrFont,
			segHorizontalAlignment,
			segVerticalAlignment,
			lblFontScaling,
			cmbFontScaling,
			lblPadding,
			spnPadding,
			lblLineSpacing,
			spnLineSpacing,
			// basic text
			lblText,
			txtText,
			// date-time
			lblFormat,
			cmbDateTimeFormat,
			// placeholder
			lblPlaceholderType,
			cmbPlaceholderType,
			lblPlaceholderVariants,
			cmbPlaceholderVariants
		};
		
		// add controls
		this.setVgap(5);
		this.setHgap(5);
		this.add(lblBackground, 0, 0);
		this.add(pkrBackground, 1, 0);
		
		// TODO wire up
		this.component.addListener((obs, ov, nv) -> {
			updating = true;
			this.getChildren().removeAll(all);
			pkrBackground.setValue(nv.getBackground());
			if (nv instanceof ObservableMediaComponent) {
				ObservableMediaComponent omc = (ObservableMediaComponent)nv;
				// add controls
				this.add(lblMedia, 0, 1);
				this.add(pkrMedia, 1, 1);
				// set values
				pkrMedia.setValue(omc.getMedia());
			} else if (nv instanceof ObservableTextComponent<?>) {
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				// add controls
				this.add(lblTextFill, 0, 1);
				this.add(pkrTextPaint, 1, 1);
				this.add(lblFont, 0, 2);
				this.add(pkrFont, 1, 2);
				this.add(segHorizontalAlignment, 1, 3);
				this.add(segVerticalAlignment, 1, 4);
				this.add(lblFontScaling, 0, 5);
				this.add(cmbFontScaling, 1, 5);
				this.add(lblPadding, 0, 6);
				this.add(spnPadding, 1, 6);
				this.add(lblLineSpacing, 0, 7);
				this.add(spnLineSpacing, 1, 7);
				// set values
				this.pkrTextPaint.setValue(otc.getTextPaint());
				this.pkrFont.setFont(JavaFXTypeConverter.toJavaFX(otc.getFont()));
				switch (otc.getHorizontalTextAlignment()) {
					case LEFT:
						tglLeft.setSelected(true);
						break;
					case CENTER:
						tglCenter.setSelected(true);
						break;
					case RIGHT:
						tglRight.setSelected(true);
						break;
					case JUSTIFY:
						tglJustify.setSelected(true);
						break;
				}
				switch (otc.getVerticalTextAlignment()) {
					case TOP:
						tglTop.setSelected(true);
						break;
					case CENTER:
						tglMiddle.setSelected(true);
						break;
					case BOTTOM:
						tglBottom.setSelected(true);
						break;
				}
				this.cmbFontScaling.setValue(new Option<FontScaleType>(null, otc.getFontScaleType()));
				this.spnPadding.getValueFactory().setValue(otc.getPadding());
				this.spnLineSpacing.getValueFactory().setValue(otc.getLineSpacing());
			}
			updating = false;
		});
		
		// wiring up
		
		this.pkrBackground.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				component.setBackground(nv);
			}
		});
		
		this.pkrTextPaint.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextPaint(nv);
			}
		});
		
		this.pkrFont.fontProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setFont(JavaFXTypeConverter.fromJavaFX(nv));
			}
		});
		
		this.segHorizontalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setHorizontalTextAlignment((HorizontalTextAlignment)nv.getUserData());
			}
		});
		
		this.segVerticalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setVerticalTextAlignment((VerticalTextAlignment)nv.getUserData());
			}
		});
		
		this.cmbFontScaling.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setFontScaleType(nv.getValue());
			}
		});
		
		this.spnPadding.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setPadding(nv);
			}
		});
		
		this.spnLineSpacing.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setLineSpacing(nv);
			}
		});
	}
	
	public ObservableSlideComponent<?> getComponent() {
		return this.component.get();
	}
	
	public void setComponent(ObservableSlideComponent<?> component) {
		this.component.set(component);
	}
	
	public ObjectProperty<ObservableSlideComponent<?>> componentProperty() {
		return this.component;
	}
}
