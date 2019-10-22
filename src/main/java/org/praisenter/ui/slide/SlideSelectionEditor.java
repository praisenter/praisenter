package org.praisenter.ui.slide;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.praisenter.data.Tag;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.configuration.Resolution;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.data.slide.graphics.SlideStrokeType;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.data.slide.text.CountdownComponent;
import org.praisenter.data.slide.text.DateTimeComponent;
import org.praisenter.data.slide.text.FontScaleType;
import org.praisenter.data.slide.text.HorizontalTextAlignment;
import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.data.slide.text.TextPlaceholderComponent;
import org.praisenter.data.slide.text.VerticalTextAlignment;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Glyphs;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.DateTimePicker;
import org.praisenter.ui.controls.EditGridPane;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.LongSpinnerValueFactory;
import org.praisenter.ui.controls.SimpleDateFormatConverter;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.controls.TimeStringConverter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.slide.controls.MediaObjectPicker;
import org.praisenter.ui.slide.controls.SlideFontPicker;
import org.praisenter.ui.slide.controls.SlidePaddingPicker;
import org.praisenter.ui.slide.controls.SlidePaintPicker;
import org.praisenter.ui.slide.controls.SlideShadowPicker;
import org.praisenter.ui.slide.controls.SlideStrokePicker;
import org.praisenter.ui.slide.controls.SlideAnimationPicker;
import org.praisenter.ui.slide.convert.TimeFormatConverter;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public final class SlideSelectionEditor extends VBox implements DocumentSelectionEditor<Slide> {
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Slide>> documentContext;
	
	private final ObjectProperty<Slide> slide;
	private final ObjectProperty<Object> selectedItem;

	// slide
	private final StringProperty name;
	private final DoubleProperty width;
	private final DoubleProperty height;
	private final ObjectProperty<Double> widthAsObject;
	private final ObjectProperty<Double> heightAsObject;
	private final LongProperty time;
	private final ObjectProperty<SlideAnimation> transition;
	private final ObjectProperty<Long> timeAsObject;
	private final ObjectProperty<SlidePaint> background;
	private final ObjectProperty<SlideStroke> border;
	private final DoubleProperty opacity;
	private final ObservableSet<Tag> tags;
	
	private final ObservableList<Option<Resolution>> resolutions;
	
	// general slide component
	private final ObjectProperty<SlideComponent> selectedComponent;
	private final ObjectProperty<SlidePaint> componentBackground;
	private final ObjectProperty<SlideStroke> componentBorder;
	private final DoubleProperty componentOpacity;
	private final ObjectProperty<SlideShadow> componentShadow;
	private final ObjectProperty<SlideShadow> componentGlow;
	
	// media component
	private final ObjectProperty<MediaComponent> selectedMediaComponent;
	private final ObjectProperty<MediaObject> componentMedia;
	
	// generic/basic text component
	private final ObjectProperty<TextComponent> selectedTextComponent;
	private final StringProperty componentText;
	private final ObjectProperty<SlidePaint> componentTextPaint;
	private final ObjectProperty<SlideStroke> componentTextBorder;
	private final ObjectProperty<SlideFont> componentFont;
	private final ObjectProperty<FontScaleType> componentFontScaleType;
	private final ObjectProperty<VerticalTextAlignment> componentVerticalTextAlignment;
	private final ObjectProperty<HorizontalTextAlignment> componentHorizontalTextAlignment;
	private final ObjectProperty<SlidePadding> componentPadding;
	private final DoubleProperty componentLineSpacing;
	private final ObjectProperty<Double> componentLineSpacingAsObject;
	private final BooleanProperty componentTextWrapping;
	private final ObjectProperty<SlideShadow> componentTextShadow;
	private final ObjectProperty<SlideShadow> componentTextGlow;
	
	// text placeholder component
	private final ObjectProperty<TextPlaceholderComponent> selectedTextPlaceholderComponent;
	private final ObjectProperty<TextType> componentPlaceholderType;
	private final ObjectProperty<TextVariant> componentPlaceholderVariant;
	
	// date/time component
	private final ObjectProperty<DateTimeComponent> selectedDateTimeComponent;
	private final ObjectProperty<SimpleDateFormat> componentDateTimeFormat;
	
	// countdown component
	private final ObjectProperty<CountdownComponent> selectedCountdownComponent;
	private final ObjectProperty<LocalDateTime> componentCountdownTarget;
	private final BooleanProperty componentCountdownTimeOnly;
	private final StringProperty componentCountdownFormat;
	
	// visible bindings
	private final BooleanBinding componentSelected;
	private final BooleanBinding mediaComponentSelected;
	private final BooleanBinding textComponentSelected;
	private final BooleanBinding basicTextComponentSelected;
	private final BooleanBinding placeholderComponentSelected;
	private final BooleanBinding dateTimeComponentSelected;
	private final BooleanBinding countdownComponentSelected;
	
	// helpers
	
	public SlideSelectionEditor(GlobalContext context) {
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		
		this.slide = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();
		this.widthAsObject = this.width.asObject();
		this.heightAsObject = this.height.asObject();
		this.time = new SimpleLongProperty();
		this.timeAsObject = this.time.asObject();
		this.transition = new SimpleObjectProperty<>();
		this.background = new SimpleObjectProperty<>();
		this.border = new SimpleObjectProperty<>();
		this.opacity = new SimpleDoubleProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
		resolutions.addAll(context.getConfiguration().getResolutions());
		this.updateScreenResolutions(resolutions);
		this.updateSlideResolutions(resolutions);
		
		Screen.getScreens().addListener((InvalidationListener)(obs -> this.updateScreenResolutions(resolutions)));
		this.context.getDataManager().getItemsUnmodifiable(Slide.class).addListener((InvalidationListener)obs -> this.updateSlideResolutions(resolutions));
		
		this.resolutions = new MappedList<Option<Resolution>, Resolution>(resolutions.sorted(), r -> {
			Option<Resolution> option = new Option<>(null, r);
			option.nameProperty().bind(Bindings.createStringBinding(() -> {
				boolean isNative = this.isNativeResolution(r);
				// TODO lookup the screen assignment so we can show something other than "Native" - would be nice to show "Present Screen" or "Teleprompter" stuff like that
				return Translations.get("slide.resolution", r.getWidth(), r.getHeight(), isNative ? " (Native)" : "");
			}, Screen.getScreens()));
			return option;
		});
		
		this.selectedComponent = new SimpleObjectProperty<>();
		this.componentBackground = new SimpleObjectProperty<>();
		this.componentBorder = new SimpleObjectProperty<>();
		this.componentOpacity = new SimpleDoubleProperty();
		this.componentShadow = new SimpleObjectProperty<>();
		this.componentGlow = new SimpleObjectProperty<>();
		
		this.selectedMediaComponent = new SimpleObjectProperty<>();
		this.componentMedia = new SimpleObjectProperty<>();
		
		this.selectedTextComponent = new SimpleObjectProperty<>();
		this.componentText = new SimpleStringProperty();
		this.componentTextPaint = new SimpleObjectProperty<>();
		this.componentTextBorder = new SimpleObjectProperty<>();
		this.componentFont = new SimpleObjectProperty<>();
		this.componentFontScaleType = new SimpleObjectProperty<>();
		this.componentHorizontalTextAlignment = new SimpleObjectProperty<>();
		this.componentVerticalTextAlignment = new SimpleObjectProperty<>();
		this.componentPadding = new SimpleObjectProperty<>();
		this.componentLineSpacing = new SimpleDoubleProperty();
		this.componentLineSpacingAsObject = this.componentLineSpacing.asObject();
		this.componentTextWrapping = new SimpleBooleanProperty();
		this.componentTextShadow = new SimpleObjectProperty<>();
		this.componentTextGlow = new SimpleObjectProperty<>();
		
		this.selectedTextPlaceholderComponent = new SimpleObjectProperty<>();
		this.componentPlaceholderType = new SimpleObjectProperty<>();
		this.componentPlaceholderVariant = new SimpleObjectProperty<>();
		
		this.selectedDateTimeComponent = new SimpleObjectProperty<>();
		this.componentDateTimeFormat = new SimpleObjectProperty<>();
		
		this.selectedCountdownComponent = new SimpleObjectProperty<>();
		this.componentCountdownFormat = new SimpleStringProperty();
		this.componentCountdownTarget = new SimpleObjectProperty<>();
		this.componentCountdownTimeOnly = new SimpleBooleanProperty();
		
		this.componentSelected = this.selectedComponent.isNotNull();
		this.mediaComponentSelected = this.selectedMediaComponent.isNotNull();
		this.textComponentSelected = this.selectedTextComponent.isNotNull();
		this.basicTextComponentSelected = Bindings.createBooleanBinding(() -> {
			SlideComponent selected = this.selectedComponent.get();
			return selected != null && selected.getClass() == TextComponent.class;
		} , this.selectedComponent);
		this.placeholderComponentSelected = this.selectedTextPlaceholderComponent.isNotNull();
		this.dateTimeComponentSelected = this.selectedDateTimeComponent.isNotNull();
		this.countdownComponentSelected = this.selectedCountdownComponent.isNotNull();
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.slide.unbind();
			this.selectedItem.unbind();
			
			this.slide.set(null);
			this.selectedItem.set(null);
			
			if (nv != null) {
				this.slide.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			// it was realy important to clear the selection first
			// to make sure everything was unbound before applying a
			// new value
			this.selectedComponent.set(null);
			if (nv != null && nv instanceof EditNode) {
				EditNode node = (EditNode)nv;
				this.selectedComponent.set(node.getComponent());
			}
		});
		
		this.selectedComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.componentBackground.unbindBidirectional(ov.backgroundProperty());
				this.componentBorder.unbindBidirectional(ov.borderProperty());
				this.componentGlow.unbindBidirectional(ov.glowProperty());
				this.componentOpacity.unbindBidirectional(ov.opacityProperty());
				this.componentShadow.unbindBidirectional(ov.shadowProperty());
			}
			
			if (nv != null) {
				this.componentBackground.bindBidirectional(nv.backgroundProperty());
				this.componentBorder.bindBidirectional(nv.borderProperty());
				this.componentGlow.bindBidirectional(nv.glowProperty());
				this.componentOpacity.bindBidirectional(nv.opacityProperty());
				this.componentShadow.bindBidirectional(nv.shadowProperty());
			}
			
			this.selectedMediaComponent.set(null);
			this.selectedTextComponent.set(null);
			this.selectedCountdownComponent.set(null);
			this.selectedDateTimeComponent.set(null);
			this.selectedTextPlaceholderComponent.set(null);
			
			if (nv != null) {
				if (nv instanceof TextComponent) {
					this.selectedTextComponent.set((TextComponent)nv);
				}
				
				if (nv instanceof MediaComponent) {
					this.selectedMediaComponent.set((MediaComponent)nv);
				} else if (nv instanceof TextPlaceholderComponent) {
					this.selectedTextPlaceholderComponent.set((TextPlaceholderComponent)nv);
				} else if (nv instanceof DateTimeComponent) {
					this.selectedDateTimeComponent.set((DateTimeComponent)nv);
				} else if (nv instanceof CountdownComponent) {
					this.selectedCountdownComponent.set((CountdownComponent)nv);
				}
			}
		});
		
		this.slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.time.unbindBidirectional(ov.timeProperty());
				this.width.unbindBidirectional(ov.widthProperty());
				this.height.unbindBidirectional(ov.heightProperty());
				this.background.unbindBidirectional(ov.backgroundProperty());
				this.border.unbindBidirectional(ov.borderProperty());
				this.opacity.unbindBidirectional(ov.opacityProperty());
				this.transition.unbindBidirectional(ov.transitionProperty());
				
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				
				this.tags.clear();
			}
			
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.time.bindBidirectional(nv.timeProperty());
				this.width.bindBidirectional(nv.widthProperty());
				this.height.bindBidirectional(nv.heightProperty());
				this.background.bindBidirectional(nv.backgroundProperty());
				this.border.bindBidirectional(nv.borderProperty());
				this.opacity.bindBidirectional(nv.opacityProperty());
				this.transition.bindBidirectional(nv.transitionProperty());
				
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
			}
		});
		
		this.selectedMediaComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.componentMedia.unbindBidirectional(ov.mediaProperty());
			}
			if (nv != null) {
				this.componentMedia.bindBidirectional(nv.mediaProperty());
			}
		});
		
		this.selectedTextComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				if (ov.getClass() == TextComponent.class) {
					this.componentText.unbindBidirectional(ov.textProperty());
				}
				this.componentTextPaint.unbindBidirectional(ov.textPaintProperty());
				this.componentTextBorder.unbindBidirectional(ov.textBorderProperty());
				this.componentFont.unbindBidirectional(ov.fontProperty());
				this.componentFontScaleType.unbindBidirectional(ov.fontScaleTypeProperty());
				this.componentHorizontalTextAlignment.unbindBidirectional(ov.horizontalTextAlignmentProperty());
				this.componentVerticalTextAlignment.unbindBidirectional(ov.verticalTextAlignmentProperty());
				this.componentLineSpacing.unbindBidirectional(ov.lineSpacingProperty());
				this.componentTextWrapping.unbindBidirectional(ov.textWrappingEnabledProperty());
				this.componentPadding.unbindBidirectional(ov.paddingProperty());
				this.componentLineSpacing.unbindBidirectional(ov.lineSpacingProperty());
				this.componentTextGlow.unbindBidirectional(ov.textGlowProperty());
				this.componentTextShadow.unbindBidirectional(ov.textShadowProperty());
			}
			if (nv != null) {
				if (nv.getClass() == TextComponent.class) {
					this.componentText.bindBidirectional(nv.textProperty());
				}
				this.componentTextPaint.bindBidirectional(nv.textPaintProperty());
				this.componentTextBorder.bindBidirectional(nv.textBorderProperty());
				this.componentFont.bindBidirectional(nv.fontProperty());
				this.componentFontScaleType.bindBidirectional(nv.fontScaleTypeProperty());
				this.componentHorizontalTextAlignment.bindBidirectional(nv.horizontalTextAlignmentProperty());
				this.componentVerticalTextAlignment.bindBidirectional(nv.verticalTextAlignmentProperty());
				this.componentLineSpacing.bindBidirectional(nv.lineSpacingProperty());
				this.componentTextWrapping.bindBidirectional(nv.textWrappingEnabledProperty());
				this.componentPadding.bindBidirectional(nv.paddingProperty());
				this.componentLineSpacing.bindBidirectional(nv.lineSpacingProperty());
				this.componentTextGlow.bindBidirectional(nv.textGlowProperty());
				this.componentTextShadow.bindBidirectional(nv.textShadowProperty());
			}
		});
		
		this.selectedTextPlaceholderComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.componentPlaceholderType.unbindBidirectional(ov.placeholderTypeProperty());
				this.componentPlaceholderVariant.unbindBidirectional(ov.placeholderVariantProperty());
			}
			if (nv != null) {
				this.componentPlaceholderType.bindBidirectional(nv.placeholderTypeProperty());
				this.componentPlaceholderVariant.bindBidirectional(nv.placeholderVariantProperty());
			}
		});
		
		this.selectedDateTimeComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.componentDateTimeFormat.unbindBidirectional(ov.dateTimeFormatProperty());
			}
			if (nv != null) {
				this.componentDateTimeFormat.bindBidirectional(nv.dateTimeFormatProperty());
			}
		});
		
		this.selectedCountdownComponent.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.componentCountdownFormat.unbindBidirectional(ov.countdownFormatProperty());
				this.componentCountdownTarget.unbindBidirectional(ov.countdownTargetProperty());
				this.componentCountdownTimeOnly.unbindBidirectional(ov.countdownTimeOnlyProperty());
			}
			if (nv != null) {
				this.componentCountdownFormat.bindBidirectional(nv.countdownFormatProperty());
				this.componentCountdownTarget.bindBidirectional(nv.countdownTargetProperty());
				this.componentCountdownTimeOnly.bindBidirectional(nv.countdownTimeOnlyProperty());
			}
		});
		
		// UI
		TextField txtName = new TextField();
		txtName.textProperty().bindBidirectional(this.name);
		
		ComboBox<Option<Resolution>> cmbTargetSize = new ComboBox<>(this.resolutions);
		cmbTargetSize.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				Resolution r = nv.getValue();
				if (r != null) {
					DocumentContext<Slide> document = this.documentContext.get();
					if (document != null) {
						Slide slide = document.getDocument();
						if (slide != null) {
							double cw = slide.getWidth();
							double ch = slide.getHeight();
							double tw = r.getWidth();
							double th = r.getHeight();
							
							if (cw != tw || ch != th) {
								UndoManager um = document.getUndoManager();
								// add the undo record
								um.addEdit(new SlideTargetSizeEdit(
										cw, ch, tw, th,
										(w, h) -> {
											slide.fit(w, h);
										}));
								// since this will call the listeners below, just start
								// a batch and discard it
								um.beginBatch("discard");
								slide.fit(tw, th);
								um.discardBatch();
							}
						}
					}
				}
			}
		});
		
		// we need to track and add to undo manager when the width/height changes for the slide (For components this is handled separately via mouse click-drag)
		
		this.width.addListener((obs, ov, nv) -> {
			DocumentContext<Slide> document = this.documentContext.get();
			if (document != null) {
				Slide slide = document.getDocument();
				if (slide != null) {
					double cw = slide.getWidth();
					double ch = slide.getHeight();
					double tw = nv != null ? nv.doubleValue() : cw;
					if (cw != tw) {
						UndoManager um = document.getUndoManager();
						um.addEdit(new SlideTargetSizeEdit(cw, ch, tw, ch, (w, h) -> {
							slide.fit(w, h);
						}));
						slide.fit(tw, ch);
					}
				}
			}
		});
		
		this.height.addListener((obs, ov, nv) -> {
			DocumentContext<Slide> document = this.documentContext.get();
			if (document != null) {
				Slide slide = document.getDocument();
				if (slide != null) {
					double cw = slide.getWidth();
					double ch = slide.getHeight();
					double th = nv != null ? nv.doubleValue() : ch;
					if (ch != th) {
						UndoManager um = document.getUndoManager();
						um.addEdit(new SlideTargetSizeEdit(cw, ch, cw, th, (w, h) -> {
							slide.fit(w, h);
						}));
						slide.fit(cw, th);
					}
				}
			}
		});
		
		ObjectConverter<Integer, Double> intToDoubleConverter = new ObjectConverter<Integer, Double>() {
			@Override
			public Double convertFrom(Integer t) {
				if (t == null) return 0.0;
				return t.doubleValue();
			}
			@Override
			public Integer convertTo(Double e) {
				if (e == null) return 0;
				return e.intValue();
			}
		};
		
		TextField txtWidth = new TextField();
		TextFormatter<Integer> tfWidth = new TextFormatter<Integer>(LastValueNumberStringConverter.forInteger(originalValueText -> {
			Platform.runLater(() -> {
				txtWidth.setText(originalValueText);
			});
		}));
		txtWidth.setTextFormatter(tfWidth);
		BindingHelper.bindBidirectional(tfWidth.valueProperty(), this.widthAsObject, intToDoubleConverter);

		TextField txtHeight = new TextField();
		TextFormatter<Integer> tfHeight = new TextFormatter<Integer>(LastValueNumberStringConverter.forInteger(originalValueText -> {
			Platform.runLater(() -> {
				txtHeight.setText(originalValueText);
			});
		}));
		txtHeight.setTextFormatter(tfHeight);
		BindingHelper.bindBidirectional(tfHeight.valueProperty(), this.heightAsObject, intToDoubleConverter);
		
		Spinner<Long> spnTime = new Spinner<>(0, Long.MAX_VALUE, 0, 5);
		spnTime.setEditable(true);
		LongSpinnerValueFactory spnTimeVF = new LongSpinnerValueFactory(0, Long.MAX_VALUE, 0, 5);
		spnTimeVF.setConverter(new TimeStringConverter((originalValueText) -> {
			Platform.runLater(() -> {
				spnTime.getEditor().setText(originalValueText);
			});
		}));
		spnTime.setValueFactory(spnTimeVF);
		spnTime.getValueFactory().valueProperty().bindBidirectional(this.timeAsObject);
		
		Slider sldOpacity = new Slider(0, 1, 1);
		sldOpacity.valueProperty().bindBidirectional(this.opacity);
		
		SlidePaintPicker pkrBackground = new SlidePaintPicker(context, true, true, true, true, true, Translations.get("slide.background"));
		pkrBackground.valueProperty().bindBidirectional(this.background);
		
		SlideStrokePicker pkrBorder = new SlideStrokePicker(SlideStrokeType.INSIDE, Translations.get("slide.border"), true);
		pkrBorder.valueProperty().bindBidirectional(this.border);
		
		SlideAnimationPicker pkrTransition = new SlideAnimationPicker();
		pkrTransition.valueProperty().bindBidirectional(this.transition);
		
		TagListView viewTags = new TagListView(this.context.getDataManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		// generic component
		
		SlidePaintPicker pkrComponentBackground = new SlidePaintPicker(context, true, true, true, true, true, Translations.get("slide.background"));
		pkrComponentBackground.valueProperty().bindBidirectional(this.componentBackground);
		
		SlideStrokePicker pkrComponentBorder = new SlideStrokePicker(SlideStrokeType.CENTERED, Translations.get("slide.border"), true);
		pkrComponentBorder.valueProperty().bindBidirectional(this.componentBorder);
		
		Label lblComponentOpacity = new Label(Translations.get("slide.opacity"));
		Slider sldComponentOpacity = new Slider(0, 1, 1);
		sldComponentOpacity.valueProperty().bindBidirectional(this.componentOpacity);
		
		SlideShadowPicker pkrComponentShadow = new SlideShadowPicker(Translations.get("slide.shadow"));
		pkrComponentShadow.valueProperty().bindBidirectional(this.componentShadow);

		SlideShadowPicker pkrComponentGlow = new SlideShadowPicker(Translations.get("slide.glow"));
		pkrComponentGlow.valueProperty().bindBidirectional(this.componentGlow);
		
		// media component
		
		MediaObjectPicker pkrComponentMedia = new MediaObjectPicker(context, MediaType.AUDIO, MediaType.VIDEO, MediaType.IMAGE);
		pkrComponentMedia.valueProperty().bindBidirectional(this.componentMedia);
		
		// basic text component
		
		TextArea txtComponentText = new TextArea();
		txtComponentText.setWrapText(true);
		txtComponentText.textProperty().bindBidirectional(this.componentText);
		
		SlidePaintPicker pkrComponentTextPaint = new SlidePaintPicker(context, false, true, true, false, false, Translations.get("slide.text.paint"));
		pkrComponentTextPaint.valueProperty().bindBidirectional(this.componentTextPaint);

		// NOTE: for performance, the text border HAS to be of type CENTERED
		SlideStrokePicker pkrComponentTextBorder = new SlideStrokePicker(SlideStrokeType.CENTERED, Translations.get("slide.text.border"), false);
		pkrComponentTextBorder.valueProperty().bindBidirectional(this.componentTextBorder);
		
		SlideFontPicker pkrComponentFont = new SlideFontPicker(Translations.get("slide.font"));
		pkrComponentFont.valueProperty().bindBidirectional(this.componentFont);
		
		ObservableList<Option<FontScaleType>> fontScaleOptions = FXCollections.observableArrayList();
		for (FontScaleType type : FontScaleType.values()) {
			fontScaleOptions.add(new Option<FontScaleType>(Translations.get("slide.font.scale." + type), type));
		}
		ChoiceBox<Option<FontScaleType>> cbComponentFontScaleType = new ChoiceBox<>(fontScaleOptions);
		cbComponentFontScaleType.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cbComponentFontScaleType.valueProperty(), this.componentFontScaleType);
		
		ToggleButton tglHAlignLeft = new ToggleButton("", Glyphs.HALIGN_LEFT.duplicate());
		ToggleButton tglHAlignRight = new ToggleButton("", Glyphs.HALIGN_RIGHT.duplicate());
		ToggleButton tglHAlignCenter = new ToggleButton("", Glyphs.HALIGN_CENTER.duplicate());
		ToggleButton tglHAlignJustify = new ToggleButton("", Glyphs.HALIGN_JUSTIFY.duplicate());
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignLeft.selectedProperty(), HorizontalTextAlignment.LEFT);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignRight.selectedProperty(), HorizontalTextAlignment.RIGHT);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignCenter.selectedProperty(), HorizontalTextAlignment.CENTER);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignJustify.selectedProperty(), HorizontalTextAlignment.JUSTIFY);
		// NOTE: SegmentedButton has an issue when running on Java 9+, the Controls FX 9.0.0 version printed a nasty error about reflecting on com.sun code (but worked)
//		SegmentedButton segComponentHAlignment = new SegmentedButton(tglHAlignLeft, tglHAlignCenter, tglHAlignRight, tglHAlignJustify);
		HBox segComponentHAlignment = new HBox(1, tglHAlignLeft, tglHAlignCenter, tglHAlignRight, tglHAlignJustify);

		ToggleButton tglVAlignTop = new ToggleButton("", Glyphs.VALIGN_TOP.duplicate());
		ToggleButton tglVAlignCenter = new ToggleButton("", Glyphs.VALIGN_CENTER.duplicate());
		ToggleButton tglVAlignBottom = new ToggleButton("", Glyphs.VALIGN_BOTTOM.duplicate());
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignTop.selectedProperty(), VerticalTextAlignment.TOP);
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignCenter.selectedProperty(), VerticalTextAlignment.CENTER);
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignBottom.selectedProperty(), VerticalTextAlignment.BOTTOM);
		HBox segComponentVAlignment = new HBox(1, tglVAlignTop, tglVAlignCenter, tglVAlignBottom);
		
		SlidePaddingPicker pkrComponentPadding = new SlidePaddingPicker();
		pkrComponentPadding.valueProperty().bindBidirectional(this.componentPadding);
		
		TextField txtLineSpacing = new TextField();
		TextFormatter<Double> tfLineSpacing = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble(originalValueText -> {
			Platform.runLater(() -> {
				txtLineSpacing.setText(originalValueText);
			});
		}));
		txtLineSpacing.setTextFormatter(tfLineSpacing);
		tfLineSpacing.valueProperty().bindBidirectional(this.componentLineSpacingAsObject);

		CheckBox chkComponentTextWrapping = new CheckBox();
		chkComponentTextWrapping.selectedProperty().bindBidirectional(this.componentTextWrapping);
		
		SlideShadowPicker pkrComponentTextShadow = new SlideShadowPicker(Translations.get("slide.text.shadow"));
		pkrComponentTextShadow.valueProperty().bindBidirectional(this.componentTextShadow);

		SlideShadowPicker pkrComponentTextGlow = new SlideShadowPicker(Translations.get("slide.text.glow"));
		pkrComponentTextGlow.valueProperty().bindBidirectional(this.componentTextGlow);
		
		// placeholder component
		
		ObservableList<Option<TextVariant>> placeholderVariantOptions = FXCollections.observableArrayList();
		for (TextVariant variant : TextVariant.values()) {
			placeholderVariantOptions.add(new Option<TextVariant>(Translations.get("slide.text.variant." + variant), variant));
		}
		ChoiceBox<Option<TextVariant>> cbComponentTextVariant = new ChoiceBox<>(placeholderVariantOptions);
		BindingHelper.bindBidirectional(cbComponentTextVariant.valueProperty(), this.componentPlaceholderVariant);
		
		ObservableList<Option<TextType>> placeholderTypeOptions = FXCollections.observableArrayList();
		for (TextType textType : TextType.values()) {
			placeholderTypeOptions.add(new Option<TextType>(Translations.get("slide.text.type." + textType), textType));
		}
		ChoiceBox<Option<TextType>> cbComponentTextType = new ChoiceBox<>(placeholderTypeOptions);
		BindingHelper.bindBidirectional(cbComponentTextType.valueProperty(), this.componentPlaceholderType);

		// datetime component
		
		ObservableList<SimpleDateFormat> dateTimeFormatOptions = FXCollections.observableArrayList();
		dateTimeFormatOptions.add(new SimpleDateFormat("EEEE MMMM, d yyyy"));
		dateTimeFormatOptions.add(new SimpleDateFormat("EEEE MMMM, d yyyy h:mm a"));
		dateTimeFormatOptions.add(new SimpleDateFormat("EEEE MMMM, d yyyy h:mm a z"));
		dateTimeFormatOptions.add(new SimpleDateFormat("EEE MMM, d yyyy"));
		dateTimeFormatOptions.add(new SimpleDateFormat("EEE MMM, d yyyy h:mm a"));
		dateTimeFormatOptions.add(new SimpleDateFormat("EEE MMM, d yyyy h:mm a z"));
		dateTimeFormatOptions.add(new SimpleDateFormat("M/d/yyyy"));
		dateTimeFormatOptions.add(new SimpleDateFormat("M/d/yyyy h:mm a"));
		dateTimeFormatOptions.add(new SimpleDateFormat("M/d/yyyy h:mm a z"));
		ComboBox<SimpleDateFormat> cmbComponentDateTimeFormat = new ComboBox<>(dateTimeFormatOptions);
		cmbComponentDateTimeFormat.setConverter(new SimpleDateFormatConverter());
		cmbComponentDateTimeFormat.setEditable(true);
		cmbComponentDateTimeFormat.valueProperty().bindBidirectional(this.componentDateTimeFormat);
		
		// countdown component
		
		DateTimePicker pkrComponentCountdownTarget = new DateTimePicker();
		pkrComponentCountdownTarget.valueProperty().bindBidirectional(this.componentCountdownTarget);

		ObservableList<String> countdownFormatOptions = FXCollections.observableArrayList();
		countdownFormatOptions.add("YY:MM:DD:hh:mm:ss");
		countdownFormatOptions.add("MM:DD:hh:mm:ss");
		countdownFormatOptions.add("DD:hh:mm:ss");
		countdownFormatOptions.add("hh:mm:ss");
		countdownFormatOptions.add("mm:ss");
		countdownFormatOptions.add("ss");
		ComboBox<String> cmbComponentCountdownFormat = new ComboBox<>(countdownFormatOptions);
		cmbComponentCountdownFormat.setEditable(true);
		BindingHelper.bindBidirectional(cmbComponentCountdownFormat.valueProperty(), this.componentCountdownFormat, new ObjectConverter<String, String>() {
			final TimeFormatConverter tfc = new TimeFormatConverter();
			@Override
			public String convertFrom(String t) {
				return tfc.fromString(t);
			}
			@Override
			public String convertTo(String e) {
				return tfc.toString(e);
			}
		});
		
		CheckBox chkComponentCountdownTimeOnly = new CheckBox();
		chkComponentCountdownTimeOnly.selectedProperty().bindBidirectional(this.componentCountdownTimeOnly);
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				txtName,
				spnTime.getEditor(),
				txtComponentText,
				txtLineSpacing);
		
		int r = 0;
		EditGridPane grid1 = new EditGridPane();
		grid1.addRow(r++, new Label(Translations.get("item.name")), txtName);
		grid1.addRow(r++, new Label(Translations.get("slide.target")), cmbTargetSize);
		grid1.addRow(r++, new Label(Translations.get("slide.width")), txtWidth);
		grid1.addRow(r++, new Label(Translations.get("slide.height")), txtHeight);
		grid1.addRow(r++, new Label(Translations.get("slide.time")), spnTime);
		grid1.addRow(r++, new Label(Translations.get("slide.opacity")), sldOpacity);
		grid1.addRow(r++, new Label(Translations.get("tags")), viewTags);
		
		VBox slideControls = new VBox(
			3,
			grid1,
			pkrBackground,
			pkrBorder,
			pkrTransition);
		
		TitledPane ttlSlide = new TitledPane(Translations.get("slide"), slideControls);
		
		VBox layoutText = new VBox(txtComponentText);
		
		EditGridPane layoutFont = new EditGridPane();
		layoutFont.addRow(0, new Label(Translations.get("slide.font.scale")), cbComponentFontScaleType);
		layoutFont.addRow(1, new Label(Translations.get("slide.text.halignment")), segComponentHAlignment);
		layoutFont.addRow(2, new Label(Translations.get("slide.text.valignment")), segComponentVAlignment);
		layoutFont.addRow(3, new Label(Translations.get("slide.text.linespacing")), txtLineSpacing);
		layoutFont.addRow(4, new Label(Translations.get("slide.text.wrapping")), chkComponentTextWrapping);
		
		VBox layoutTextComponent = new VBox(
				3,
				pkrComponentPadding,
				pkrComponentTextPaint,
				pkrComponentTextBorder,
				pkrComponentFont,
				layoutFont,
				pkrComponentTextShadow,
				pkrComponentTextGlow,
				layoutText);
		pkrComponentPadding.setAlignment(Pos.CENTER);
		layoutText.visibleProperty().bind(this.basicTextComponentSelected);
		layoutText.managedProperty().bind(layoutText.visibleProperty());
		layoutTextComponent.visibleProperty().bind(this.textComponentSelected);
		layoutTextComponent.managedProperty().bind(layoutTextComponent.visibleProperty());
		
		EditGridPane layoutPlaceholderComponent = new EditGridPane();
		layoutPlaceholderComponent.addRow(0, new Label(Translations.get("slide.placeholder.type")), cbComponentTextType);
		layoutPlaceholderComponent.addRow(1, new Label(Translations.get("slide.placeholder.variant")), cbComponentTextVariant);
		layoutPlaceholderComponent.visibleProperty().bind(this.placeholderComponentSelected);
		layoutPlaceholderComponent.managedProperty().bind(layoutPlaceholderComponent.visibleProperty());
		
		EditGridPane layoutDateTimeComponent = new EditGridPane();
		layoutDateTimeComponent.addRow(0, new Label(Translations.get("slide.datetime.format")), cmbComponentDateTimeFormat);
		layoutDateTimeComponent.visibleProperty().bind(this.dateTimeComponentSelected);
		layoutDateTimeComponent.managedProperty().bind(layoutDateTimeComponent.visibleProperty());
		
		EditGridPane layoutCountdownComponent = new EditGridPane();
		layoutCountdownComponent.addRow(0, new Label(Translations.get("slide.countdown.target")), pkrComponentCountdownTarget);
		layoutCountdownComponent.addRow(1, new Label(Translations.get("slide.countdown.format")), cmbComponentCountdownFormat);
		layoutCountdownComponent.addRow(2, new Label(Translations.get("slide.countdown.timeonly")), chkComponentCountdownTimeOnly);
		layoutCountdownComponent.visibleProperty().bind(this.countdownComponentSelected);
		layoutCountdownComponent.managedProperty().bind(layoutCountdownComponent.visibleProperty());
		
		VBox layoutMediaComponent = new VBox(pkrComponentMedia);
		layoutMediaComponent.visibleProperty().bind(this.mediaComponentSelected);
		layoutMediaComponent.managedProperty().bind(layoutMediaComponent.visibleProperty());
		
		EditGridPane grid4 = new EditGridPane();
		grid4.addRow(0, lblComponentOpacity, sldComponentOpacity);
		
		TitledPane ttlComponent = new TitledPane(Translations.get("slide.component"), new VBox(
				3,
				pkrComponentBackground,
				pkrComponentBorder,
				grid4,
				pkrComponentShadow,
				pkrComponentGlow,
				layoutMediaComponent,
				layoutTextComponent,
				layoutPlaceholderComponent,
				layoutDateTimeComponent,
				layoutCountdownComponent));
		
		ScrollPane scroller = new ScrollPane(new VBox(
				ttlSlide,
				ttlComponent));
		scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroller.setFitToWidth(true);
		
		this.getChildren().addAll(scroller);
		
		// hide/show
		
		ttlComponent.visibleProperty().bind(this.componentSelected);
		ttlComponent.managedProperty().bind(ttlComponent.visibleProperty());
	}
	
	private void updateSlideResolutions(ObservableList<Resolution> resolutions) {
		// add any screen sizes based on the current set of slides
		List<Slide> slides = this.context.getDataManager().getItemsUnmodifiable(Slide.class);
		List<Resolution> nr = new ArrayList<>();
		for (Slide slide : slides) {
			Resolution r = new Resolution((int)Math.ceil(slide.getWidth()), (int)Math.ceil(slide.getHeight()));
			int index = resolutions.indexOf(r);
			if (index < 0) {
				// doesn't exist
				nr.add(r);
			}
		}
		resolutions.addAll(nr);
	}
	
	private void updateScreenResolutions(ObservableList<Resolution> resolutions) {
		// add any screen sizes based on the current displays
		List<Resolution> nr = new ArrayList<>();
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			Resolution r = new Resolution((int)Math.ceil(bounds.getWidth()), (int)Math.ceil(bounds.getHeight()));
			int index = resolutions.indexOf(r);
			if (index < 0) {
				// doesn't exist
				nr.add(r);
			}
		}
		resolutions.addAll(nr);
	}
	
	private boolean isNativeResolution(Resolution r) {
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			if ((int)bounds.getWidth() == r.getWidth() && (int)bounds.getHeight() == r.getHeight()) {
				return true;
			}
		}
		return false;
	}
	
	public DocumentContext<Slide> getDocumentContext() {
		return this.documentContext.get();
	}
	
	public void setDocumentContext(DocumentContext<Slide> ctx) {
		this.documentContext.set(ctx);
	}
	
	public ObjectProperty<DocumentContext<Slide>> documentContextProperty() {
		return this.documentContext;
	}
}
