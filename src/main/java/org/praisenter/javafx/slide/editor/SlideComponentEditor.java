package org.praisenter.javafx.slide.editor;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.OpenIconic;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.PlaceholderVariant;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

// TODO translate
// TODO tooltip help

final class SlideComponentEditor extends GridPane {

	/** The openiconic glyph-font pack */
	private static final GlyphFont FONT_ICONIC	= GlyphFontRegistry.font("Icons");
	
	/** The fontawesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	// data
	
	// FIXME we probably want to reuse this class for the slide itself as well
	final ObjectProperty<ObservableSlideComponent<?>> component = new SimpleObjectProperty<ObservableSlideComponent<?>>();
	
	boolean updating = false;
	
	// controls
	
	// base component
	final SlidePaintPicker pkrBackground;
	final SlideStrokePicker pkrBorder;
	final Slider sldOpacity;
	final SlideShadowPicker pkrShadow;
	final SlideShadowPicker pkrGlow;
	final Button btnMoveUp;
	final Button btnMoveDown;
	
	// media component
	final SlidePaintPicker pkrMedia;
	
	// text component
	final SlidePaintPicker pkrTextPaint;
	final SlideStrokePicker pkrTextBorder;
	final SlideFontPicker pkrFont;
	final SegmentedButton segHorizontalAlignment;
	final SegmentedButton segVerticalAlignment;
	final ChoiceBox<Option<FontScaleType>> cmbFontScaling;
	final Spinner<Double> spnPadding;
	final Spinner<Double> spnLineSpacing;
	final ToggleButton tglTextWrapping;
	final SlideShadowPicker pkrTextShadow;
	final SlideShadowPicker pkrTextGlow;
	
	// basic text component
	final TextArea txtText;
	
	// date-time component
	final TextField txtDateFormat;
	final ChoiceBox<Option<SimpleDateFormat>> cmbDateTimeFormat;
	
	// countdown component
	final DateTimePicker pkrCountdownTime;
	final TextField txtCountdownFormat;
	final ChoiceBox<Option<String>> cbCountdownFormat;
	
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
		
		ObservableList<Option<String>> countdownFormats = FXCollections.observableArrayList();
		countdownFormats.add(new Option<String>("YY:MM:DD:HH:MM:SS", "%1$02d:%2$02d:%3$02d:%4$02d:%5$02d:%6$02d"));
		countdownFormats.add(new Option<String>("MM:DD:HH:MM:SS", "%2$02d:%3$02d:%4$02d:%5$02d:%6$02d"));
		countdownFormats.add(new Option<String>("DD:HH:MM:SS", "%3$02d:%4$02d:%5$02d:%6$02d"));
		countdownFormats.add(new Option<String>("HH:MM:SS", "%4$02d:%5$02d:%6$02d"));
		countdownFormats.add(new Option<String>("MM:SS", "%5$02d:%6$02d"));
		countdownFormats.add(new Option<String>("SS", "%6$02d"));
		
		// base component
		// background
		Label lblBackground = new Label("Background");
		this.pkrBackground = new SlidePaintPicker(context, 
				PaintType.NONE, 
				PaintType.COLOR, 
				PaintType.GRADIENT, 
				PaintType.IMAGE, 
				PaintType.VIDEO);
		this.pkrBackground.setValue(null);
		// border
		Label lblBorder = new Label("Border");
		this.pkrBorder = new SlideStrokePicker(context, true);
		this.pkrBorder.setValue(null);
		Label lblOpacity = new Label("Opacity");
		this.sldOpacity = new Slider(0.0, 1.0, 1.0);
		this.sldOpacity.setPrefWidth(75);
		this.sldOpacity.setMaxWidth(75);
		Label lblShadow = new Label("Shadow");
		this.pkrShadow = new SlideShadowPicker();
		Label lblGlow = new Label("Glow");
		this.pkrGlow = new SlideShadowPicker();
		
		// ordering
		this.btnMoveUp = new Button("", FONT_ICONIC.create(OpenIconic.Glyph.DATA_TRANSFER_UPLOAD));
		this.btnMoveDown = new Button("", FONT_ICONIC.create(OpenIconic.Glyph.DATA_TRANSFER_DOWNLOAD));
		
		// media component
		// media
		Label lblMedia = new Label("Media");
		this.pkrMedia = new SlidePaintPicker(context, 
				PaintType.NONE, 
				PaintType.IMAGE, 
				PaintType.VIDEO, 
				PaintType.AUDIO);
		this.pkrMedia.setValue(null);
		
		// text component
		// paint
		Label lblTextFill = new Label("Text Fill");
		this.pkrTextPaint = new SlidePaintPicker(context, 
				PaintType.COLOR, 
				PaintType.GRADIENT);
		this.pkrTextPaint.setValue(new SlideColor(0, 0, 0, 0.5));
		// border
		Label lblTextBorder = new Label("Text Border");
		this.pkrTextBorder = new SlideStrokePicker(context, false);
		this.pkrTextBorder.setValue(null);
		// font
		Label lblFont = new Label("Font");
		this.pkrFont = new SlideFontPicker(FXCollections.observableArrayList(Font.getFamilies()));
		// h-align
		ToggleButton tglLeft = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_LEFT));
		ToggleButton tglRight = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_RIGHT));
		ToggleButton tglCenter = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_CENTER));
		ToggleButton tglJustify = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ALIGN_JUSTIFY));
		tglLeft.setSelected(true);
		tglLeft.setUserData(HorizontalTextAlignment.LEFT);
		tglRight.setUserData(HorizontalTextAlignment.RIGHT);
		tglCenter.setUserData(HorizontalTextAlignment.CENTER);
		tglJustify.setUserData(HorizontalTextAlignment.JUSTIFY);
		this.segHorizontalAlignment = new SegmentedButton(tglLeft, tglCenter, tglRight, tglJustify);
		// v-align
		ToggleButton tglTop = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_TOP));
		ToggleButton tglMiddle = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_CENTER));
		ToggleButton tglBottom = new ToggleButton("", FONT_ICONIC.create(OpenIconic.Glyph.VERTICAL_ALIGN_BOTTOM));
		tglTop.setSelected(true);
		tglTop.setUserData(VerticalTextAlignment.TOP);
		tglMiddle.setUserData(VerticalTextAlignment.CENTER);
		tglBottom.setUserData(VerticalTextAlignment.BOTTOM);
		this.segVerticalAlignment = new SegmentedButton(tglTop, tglMiddle, tglBottom);
		
		// text wrapping
		this.tglTextWrapping = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.PARAGRAPH));
		this.tglTextWrapping.setSelected(true);
				
		HBox alignment = new HBox(2, this.segHorizontalAlignment, this.segVerticalAlignment, this.tglTextWrapping);
		
		// font scale
		Label lblFontScaling = new Label("Sizing");
		this.cmbFontScaling = new ChoiceBox<Option<FontScaleType>>(fontScaleTypes);
		this.cmbFontScaling.setValue(fontScaleTypes.get(0));
		// padding
		Label lblPadding = new Label("Padding");
		this.spnPadding = new Spinner<Double>(0.0, Double.MAX_VALUE, 0.0, 1.0);
		this.spnPadding.setPrefWidth(100);
		this.spnPadding.setEditable(true);
		// commit the value as the user types
		this.spnPadding.editorProperty().get().textProperty().addListener((obs, ov, nv) -> {
			spnPadding.editorProperty().get().commitValue();
		});
		// line spacing
		Label lblLineSpacing = new Label("Line Spacing");
		this.spnLineSpacing = new Spinner<Double>(-Double.MAX_VALUE, Double.MAX_VALUE, 0.0, 1.0);
		this.spnLineSpacing.setPrefWidth(100);
		this.spnLineSpacing.setEditable(true);
		// commit the value as the user types
		this.spnLineSpacing.editorProperty().get().textProperty().addListener((obs, ov, nv) -> {
			spnLineSpacing.editorProperty().get().commitValue();
		});
		
		Label lblTextShadow = new Label("Text Shadow");
		this.pkrTextShadow = new SlideShadowPicker();
		Label lblTextGlow = new Label("Text Glow");
		this.pkrTextGlow = new SlideShadowPicker();
		
		// basic text component
		// text
		Label lblText = new Label("Text");
		this.txtText = new TextArea();
		this.txtText.setMinWidth(0);
		this.txtText.setPrefWidth(100);
		this.txtText.setWrapText(true);
		this.txtText.setText("");
		
		// date-time component
		// format
		Label lblFormat = new Label("Format");
		this.txtDateFormat = new TextField(dateTimeFormats.get(0).getValue().toPattern());
		this.cmbDateTimeFormat = new ChoiceBox<Option<SimpleDateFormat>>(dateTimeFormats);
		this.cmbDateTimeFormat.setValue(dateTimeFormats.get(0));
		this.cmbDateTimeFormat.setPrefWidth(100);
		VBox hbDateFormat = new VBox(2, this.txtDateFormat, this.cmbDateTimeFormat);
		
		// countdown component
		Label lblCountdownTime = new Label("To");
		this.pkrCountdownTime = new DateTimePicker();
		Label lblCountdownFormat = new Label("Format");
		this.txtCountdownFormat = new TextField(countdownFormats.get(0).getValue());
		this.cbCountdownFormat = new ChoiceBox<Option<String>>(countdownFormats);
		this.cbCountdownFormat.setValue(countdownFormats.get(0));
		this.cbCountdownFormat.setPrefWidth(100);
		VBox hbCountdownFormat = new VBox(2, this.txtCountdownFormat, this.cbCountdownFormat);
		
		// text placeholder component
		// type
		Label lblPlaceholderType = new Label("Type");
		this.cmbPlaceholderType = new ChoiceBox<Option<PlaceholderType>>(placeholderTypes);
		this.cmbPlaceholderType.setValue(placeholderTypes.get(0));
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
			lblTextBorder,
			pkrTextBorder,
			lblFont,
			pkrFont,
			alignment,
			lblFontScaling,
			cmbFontScaling,
			lblPadding,
			spnPadding,
			lblLineSpacing,
			spnLineSpacing,
			lblTextShadow,
			pkrTextShadow,
			lblTextGlow,
			pkrTextGlow,
			// basic text
			lblText,
			txtText,
			// date-time
			lblFormat,
			hbDateFormat,
			// countdown
			lblCountdownTime,
			pkrCountdownTime,
			lblCountdownFormat,
			hbCountdownFormat,
			// placeholder
			lblPlaceholderType,
			cmbPlaceholderType,
			lblPlaceholderVariants,
			cmbPlaceholderVariants
		};
		
		// add controls
		this.setVgap(2);
		this.setHgap(2);

		Label lblDepth = new Label("Position");
		HBox hbDepth = new HBox(2, this.btnMoveUp, this.btnMoveDown);
		this.add(lblDepth, 0, 0);
		this.add(hbDepth, 1, 0);
		
		this.add(lblBackground, 0, 1);
		this.add(pkrBackground, 1, 1);
		
		this.add(lblBorder, 0, 2);
		this.add(pkrBorder, 1, 2);
		
		this.add(lblOpacity, 0, 3);
		this.add(sldOpacity, 1, 3);

		this.add(lblShadow, 0, 4);
		this.add(pkrShadow, 1, 4);
		
		this.add(lblGlow, 0, 5);
		this.add(pkrGlow, 1, 5);
		
		final int sy = 6;
		
		this.component.addListener((obs, ov, nv) -> {
			updating = true;
			this.getChildren().removeAll(all);
			pkrBackground.setValue(nv.getBackground());
			pkrBorder.setValue(nv.getBorder());
			sldOpacity.setValue(nv.getOpacity());
			pkrShadow.setValue(nv.getShadow());
			pkrGlow.setValue(nv.getGlow());
			if (nv instanceof ObservableMediaComponent) {
				ObservableMediaComponent omc = (ObservableMediaComponent)nv;
				// add controls
				this.add(lblMedia, 0, sy);
				this.add(pkrMedia, 1, sy);
				// set values
				pkrMedia.setValue(omc.getMedia());
			} else if (nv instanceof ObservableTextComponent<?>) {
				ObservableTextComponent<?> otc = (ObservableTextComponent<?>)nv;
				// add controls
				this.add(lblTextFill, 0, sy);
				this.add(pkrTextPaint, 1, sy);
				this.add(lblTextBorder, 0, sy + 1);
				this.add(pkrTextBorder, 1, sy + 1);
				this.add(lblFont, 0, sy + 2);
				this.add(pkrFont, 1, sy + 2);
				this.add(alignment, 1, sy + 3);
				this.add(lblFontScaling, 0, sy + 4);
				this.add(cmbFontScaling, 1, sy + 4);
				this.add(lblPadding, 0, sy + 5);
				this.add(spnPadding, 1, sy + 5);
				this.add(lblLineSpacing, 0, sy + 6);
				this.add(spnLineSpacing, 1, sy + 6);
				this.add(lblTextShadow, 0, sy + 7);
				this.add(pkrTextShadow, 1, sy + 7);
				this.add(lblTextGlow, 0, sy + 8);
				this.add(pkrTextGlow, 1, sy + 8);
				// set values
				this.pkrTextPaint.setValue(otc.getTextPaint());
				this.pkrTextBorder.setValue(otc.getTextBorder());
				this.pkrFont.setFont(otc.getFont());
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
				this.spnPadding.getValueFactory().setValue(otc.getPadding().getTop());
				this.spnLineSpacing.getValueFactory().setValue(otc.getLineSpacing()); 
				this.tglTextWrapping.setSelected(otc.isTextWrapping());
				this.pkrTextShadow.setValue(otc.getTextShadow());
				this.pkrTextGlow.setValue(otc.getTextGlow());
				
				if (nv instanceof ObservableDateTimeComponent) {
					ObservableDateTimeComponent odtc = (ObservableDateTimeComponent)nv;
					
					this.add(lblFormat, 0, sy + 9);
					this.add(hbDateFormat, 1, sy + 9);
					
					this.cmbDateTimeFormat.setValue(new Option<SimpleDateFormat>(null, odtc.getFormat()));
					this.txtDateFormat.setText(odtc.getFormat().toPattern());
				} else if (nv instanceof ObservableCountdownComponent) {
					ObservableCountdownComponent ocdc = (ObservableCountdownComponent)nv;
					
					this.add(lblCountdownTime, 0, sy + 9);
					this.add(pkrCountdownTime, 1, sy + 9);
					this.add(lblCountdownFormat, 0, sy + 10);
					this.add(hbCountdownFormat, 1, sy + 10);
					
					this.pkrCountdownTime.setValue(ocdc.getTarget());
					this.cbCountdownFormat.setValue(new Option<String>(null, ocdc.getFormat()));
					this.txtCountdownFormat.setText(ocdc.getFormat());
				} else if (nv instanceof ObservableTextPlaceholderComponent) {
					ObservableTextPlaceholderComponent otpc = (ObservableTextPlaceholderComponent)nv;
					
					this.add(lblPlaceholderType, 0, sy + 9);
					this.add(cmbPlaceholderType, 1, sy + 9);
					this.add(lblPlaceholderVariants, 0, sy + 10);
					this.add(cmbPlaceholderVariants, 1, sy + 10);
					
					this.cmbPlaceholderType.setValue(new Option<PlaceholderType>(null, otpc.getPlaceholderType()));
					this.cmbPlaceholderVariants.getCheckModel().clearChecks();
					for (PlaceholderVariant variant : otpc.getVariants()) {
						this.cmbPlaceholderVariants.getCheckModel().check(new Option<PlaceholderVariant>(null, variant));
					}
				} else if (nv instanceof ObservableBasicTextComponent) {
					ObservableBasicTextComponent<?> obtc = (ObservableBasicTextComponent<?>)nv;
					
					this.add(lblText, 0, sy + 9);
					this.add(txtText, 1, sy + 9);
					
					this.txtText.setText(obtc.getText());
				}
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
		
		this.pkrBorder.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				component.setBorder(nv);
			}
		});
		
		this.sldOpacity.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				component.setOpacity(nv.doubleValue());
			}
		});
		
		this.pkrShadow.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				component.setShadow(nv);
			}
		});
		
		this.pkrGlow.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				component.setGlow(nv);
			}
		});
		
		this.btnMoveUp.setOnAction((e) -> {
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				fireEvent(new SlideComponentOrderEvent(this.btnMoveUp, SlideComponentEditor.this, component, SlideComponentOrderEvent.OPERATION_FORWARD));
			}
		});
		this.btnMoveDown.setOnAction((e) -> {
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null) {
				fireEvent(new SlideComponentOrderEvent(this.btnMoveDown, SlideComponentEditor.this, component, SlideComponentOrderEvent.OPERATION_BACKWARD));
			}
		});
		
		this.pkrMedia.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableMediaComponent) {
				ObservableMediaComponent mc =(ObservableMediaComponent)component;
				if (nv == null || nv instanceof MediaObject) {
					mc.setMedia((MediaObject)nv);
				} else {
					mc.setMedia(null);
				}
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
		
		this.pkrTextBorder.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextBorder(nv);
			}
		});
		
		this.pkrFont.fontProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setFont(nv);
			}
		});
		
		this.segHorizontalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				Object value = nv != null ? nv.getUserData() : null;
				if (value == null) {
					// default to left
					value = HorizontalTextAlignment.LEFT;
				}
				if (value instanceof HorizontalTextAlignment) {
					tc.setHorizontalTextAlignment((HorizontalTextAlignment)value);
				} else {
					tc.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
				}
			}
		});
		
		this.segVerticalAlignment.getToggleGroup().selectedToggleProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				Object value = nv != null ? nv.getUserData() : null;
				if (value == null) {
					// default to left
					value = VerticalTextAlignment.TOP;
				}
				if (value instanceof VerticalTextAlignment) {
					tc.setVerticalTextAlignment((VerticalTextAlignment)value);
				} else {
					tc.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				}
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
				tc.setPadding(new SlidePadding(nv.doubleValue()));
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
		
		this.tglTextWrapping.selectedProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextWrapping(nv);
			}
		});
		
		this.txtText.setOnKeyReleased((e) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableBasicTextComponent) {
				ObservableBasicTextComponent<?> tc = (ObservableBasicTextComponent<?>)component;
				tc.setText(txtText.getText());
			}
		});
		
		this.pkrTextShadow.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextShadow(nv);
			}
		});
		
		this.pkrTextGlow.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextComponent) {
				ObservableTextComponent<?> tc =(ObservableTextComponent<?>)component;
				tc.setTextGlow(nv);
			}
		});
		
		this.cmbDateTimeFormat.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent tc = (ObservableDateTimeComponent)component;
				if (nv != null) {
					updating = true;
					txtDateFormat.setText(nv.getValue().toPattern());
					updating = false;
					tc.setFormat(nv.getValue());
				}
			}
		});

		this.txtDateFormat.textProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableDateTimeComponent) {
				ObservableDateTimeComponent tc = (ObservableDateTimeComponent)component;
				try {
					tc.setFormat(new SimpleDateFormat(nv));
				} catch (Exception ex) {
					// failed to parse the format
				}
				cmbDateTimeFormat.setValue(null);
			}
		});
		
		this.pkrCountdownTime.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent cdc =(ObservableCountdownComponent)component;
				cdc.setTarget(nv);
			}
		});
		
		this.txtCountdownFormat.textProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent cdc =(ObservableCountdownComponent)component;
				cdc.setFormat(nv);
				cbCountdownFormat.setValue(null);
			}
		});
		
		this.cbCountdownFormat.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableCountdownComponent) {
				ObservableCountdownComponent cdc =(ObservableCountdownComponent)component;
				if (nv != null) {
					updating = true;
					txtCountdownFormat.setText(nv.getValue());
					updating = false;
					cdc.setFormat(nv.getValue());
				}
			}
		});
		
		this.cmbPlaceholderType.valueProperty().addListener((obs, ov, nv) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.setPlaceholderType(nv.getValue());
			}
		});
		
		this.cmbPlaceholderVariants.checkModelProperty().get().getCheckedItems().addListener((javafx.collections.ListChangeListener.Change<? extends Option<PlaceholderVariant>> change) -> {
			if (updating) return;
			ObservableSlideComponent<?> component = this.component.get();
			if (component != null && component instanceof ObservableTextPlaceholderComponent) {
				ObservableTextPlaceholderComponent tc = (ObservableTextPlaceholderComponent)component;
				tc.getVariants().clear();
				tc.getVariants().addAll(change.getList().stream().map((o) -> o.getValue()).collect(Collectors.toList()).toArray(new PlaceholderVariant[0]));
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
