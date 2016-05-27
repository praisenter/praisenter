package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.FontPicker;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.media.MediaPicker;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

// TODO translate

public final class SlideComponentEditor extends GridPane {

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// data
	
	final ObjectProperty<ObservableSlideComponent<?>> component = new SimpleObjectProperty<ObservableSlideComponent<?>>();
	
	// controls
	
	// base component
	final SlidePaintPicker pkrBackground;
	// TODO need a border picker
	
	// media component
	final MediaPicker pkrMedia;
	
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
		this.pkrBackground = new SlidePaintPicker(context);
		// border
		// re-ordering
		
		// media component
		// media
		this.pkrMedia = new MediaPicker(context);
		
		// text component
		// paint
		this.pkrTextPaint = new SlidePaintPicker(context, PaintType.NONE, PaintType.COLOR, PaintType.GRADIENT);
		// border
		// font
		this.pkrFont = new FontPicker(Font.font("Arial", 30), FXCollections.observableArrayList(Font.getFamilies()));
		// h-align
		ToggleButton tglLeft = new ToggleButton("l");
		ToggleButton tglRight = new ToggleButton("r");
		ToggleButton tglCenter = new ToggleButton("c");
		ToggleButton tglJustify = new ToggleButton("j");
		tglLeft.setUserData(HorizontalTextAlignment.LEFT);
		tglRight.setUserData(HorizontalTextAlignment.RIGHT);
		tglCenter.setUserData(HorizontalTextAlignment.CENTER);
		tglJustify.setUserData(HorizontalTextAlignment.JUSTIFY);
		tglLeft.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT));
		tglRight.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT));
		tglCenter.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER));
		tglJustify.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY));
		this.segHorizontalAlignment = new SegmentedButton(tglLeft, tglRight, tglCenter, tglJustify);
		// v-align
		ToggleButton tglTop = new ToggleButton("t");
		ToggleButton tglMiddle = new ToggleButton("m");
		ToggleButton tglBottom = new ToggleButton("b");
		tglTop.setUserData(VerticalTextAlignment.TOP);
		tglMiddle.setUserData(VerticalTextAlignment.CENTER);
		tglBottom.setUserData(VerticalTextAlignment.BOTTOM);
		// FIXME need vertical alignment options
		tglTop.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.LIST));
		tglMiddle.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.LIST_ALT));
		tglBottom.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.LIST_OL));
		this.segVerticalAlignment = new SegmentedButton(tglTop, tglMiddle, tglBottom);
		// font scale
		this.cmbFontScaling = new ChoiceBox<Option<FontScaleType>>(fontScaleTypes);
		// padding
		this.spnPadding = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		// line spacing
		this.spnLineSpacing = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		
		// basic text component
		// text
		this.txtText = new TextField();
		
		// date-time component
		// format
		this.cmbDateTimeFormat = new ChoiceBox<Option<SimpleDateFormat>>(dateTimeFormats);
		
		// text placeholder component
		// type
		this.cmbPlaceholderType = new ChoiceBox<Option<PlaceholderType>>(placeholderTypes);
		// variants
		this.cmbPlaceholderVariants = new CheckComboBox<Option<PlaceholderVariant>>(placeholderVariants);
		
		// TODO wire up
		
		
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
