package org.praisenter.ui.slide;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
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
import org.praisenter.data.workspace.Resolution;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Icons;
import org.praisenter.ui.Option;
import org.praisenter.ui.ScreenHelper;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.DateTimePicker;
import org.praisenter.ui.controls.EditorDivider;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.EditorTitledPane;
import org.praisenter.ui.controls.IntegerSliderField;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.LongSpinnerValueFactory;
import org.praisenter.ui.controls.SimpleDateFormatConverter;
import org.praisenter.ui.controls.WidthHeightPicker;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.controls.TimeStringConverter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.slide.controls.MediaObjectPicker;
import org.praisenter.ui.slide.controls.SlideAnimationPicker;
import org.praisenter.ui.slide.controls.SlideFontPicker;
import org.praisenter.ui.slide.controls.SlidePaddingPicker;
import org.praisenter.ui.slide.controls.SlidePaintPicker;
import org.praisenter.ui.slide.controls.SlideShadowPicker;
import org.praisenter.ui.slide.controls.SlideStrokePicker;
import org.praisenter.ui.slide.convert.TimeFormatConverter;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
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
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public final class SlideSelectionEditor extends VBox implements DocumentSelectionEditor<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SELECTION_EDITOR_CSS = "p-slide-selection-editor";
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Slide>> documentContext;
	
	private final ObjectProperty<Slide> slide;
	private final ObjectProperty<Object> selectedItem;

	// slide
	private final StringProperty name;
	private final DoubleProperty width;
	private final DoubleProperty height;
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
	private final BooleanBinding textComponentSelected;
	
	// helpers
	
	public SlideSelectionEditor(GlobalContext context) {
		this.getStyleClass().add(SELECTION_EDITOR_CSS);
		
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		
		this.slide = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();
		this.time = new SimpleLongProperty();
		this.timeAsObject = this.time.asObject();
		this.transition = new SimpleObjectProperty<>();
		this.background = new SimpleObjectProperty<>();
		this.border = new SimpleObjectProperty<>();
		this.opacity = new SimpleDoubleProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		
		ObservableList<Resolution> resolutions = FXCollections.observableArrayList();
		resolutions.addAll(context.getWorkspaceConfiguration().getResolutions());
		this.updateScreenResolutions(resolutions);
		this.updateSlideResolutions(resolutions);
		
		Screen.getScreens().addListener((InvalidationListener)(obs -> this.updateScreenResolutions(resolutions)));
		this.context.getWorkspaceManager().getItemsUnmodifiable(Slide.class).addListener((InvalidationListener)obs -> this.updateSlideResolutions(resolutions));
		
		this.resolutions = new MappedList<Option<Resolution>, Resolution>(resolutions.sorted(), r -> {
			Option<Resolution> option = new Option<>(null, r);
			option.nameProperty().bind(Bindings.createStringBinding(() -> {
				boolean isNative = this.isNativeResolution(r);
				return isNative 
						? Translations.get("resolution.native", r.getWidth(), r.getHeight())
						: Translations.get("resolution", r.getWidth(), r.getHeight());
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
		this.textComponentSelected = this.selectedTextComponent.isNotNull();
		
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
		
		WidthHeightPicker pkrSlideSize = new WidthHeightPicker();
		pkrSlideSize.selectedWidthProperty().bindBidirectional(this.width);
		pkrSlideSize.selectedHeightProperty().bindBidirectional(this.height);
		
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
		
		IntegerSliderField pkrOpacity = new IntegerSliderField(0, 1, 1, 100);
		pkrOpacity.valueProperty().bindBidirectional(this.opacity);
		
		SlidePaintPicker pkrBackground = new SlidePaintPicker(context, true, true, true, true, true, Translations.get("slide.background"));
		pkrBackground.valueProperty().bindBidirectional(this.background);
		
		SlideStrokePicker pkrBorder = new SlideStrokePicker(SlideStrokeType.INSIDE, Translations.get("slide.border"), true);
		pkrBorder.valueProperty().bindBidirectional(this.border);
		
		SlideAnimationPicker pkrTransition = new SlideAnimationPicker();
		pkrTransition.valueProperty().bindBidirectional(this.transition);
		
		TagListView viewTags = new TagListView(this.context.getWorkspaceManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		// generic component
		
		SlidePaintPicker pkrComponentBackground = new SlidePaintPicker(context, true, true, true, true, true, Translations.get("slide.background"));
		pkrComponentBackground.valueProperty().bindBidirectional(this.componentBackground);
		
		SlideStrokePicker pkrComponentBorder = new SlideStrokePicker(SlideStrokeType.CENTERED, Translations.get("slide.border"), true);
		pkrComponentBorder.valueProperty().bindBidirectional(this.componentBorder);
		
		IntegerSliderField sldComponentOpacity = new IntegerSliderField(0, 1, 1, 100);
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
		
		ToggleButton tglHAlignLeft = new ToggleButton("", Icons.getIcon(Icons.HORIZONTAL_ALIGN_LEFT));
		ToggleButton tglHAlignRight = new ToggleButton("", Icons.getIcon(Icons.HORIZONTAL_ALIGN_RIGHT));
		ToggleButton tglHAlignCenter = new ToggleButton("", Icons.getIcon(Icons.HORIZONTAL_ALIGN_CENTER));
		ToggleButton tglHAlignJustify = new ToggleButton("", Icons.getIcon(Icons.HORIZONTAL_ALIGN_JUSTIFY));
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignLeft.selectedProperty(), HorizontalTextAlignment.LEFT);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignRight.selectedProperty(), HorizontalTextAlignment.RIGHT);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignCenter.selectedProperty(), HorizontalTextAlignment.CENTER);
		BindingHelper.bindBidirectional(this.componentHorizontalTextAlignment, tglHAlignJustify.selectedProperty(), HorizontalTextAlignment.JUSTIFY);
		tglHAlignLeft.getStyleClass().add(Styles.LEFT_PILL);
		tglHAlignCenter.getStyleClass().add(Styles.CENTER_PILL);
		tglHAlignRight.getStyleClass().add(Styles.CENTER_PILL);
		tglHAlignJustify.getStyleClass().add(Styles.RIGHT_PILL);
		HBox segComponentHAlignment = new HBox(tglHAlignLeft, tglHAlignCenter, tglHAlignRight, tglHAlignJustify);
		segComponentHAlignment.setAlignment(Pos.CENTER_RIGHT);

		ToggleButton tglVAlignTop = new ToggleButton("", Icons.getIcon(Icons.VERTICAL_ALIGN_TOP));
		ToggleButton tglVAlignCenter = new ToggleButton("", Icons.getIcon(Icons.VERTICAL_ALIGN_CENTER));
		ToggleButton tglVAlignBottom = new ToggleButton("", Icons.getIcon(Icons.VERTICAL_ALIGN_BOTTOM));
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignTop.selectedProperty(), VerticalTextAlignment.TOP);
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignCenter.selectedProperty(), VerticalTextAlignment.CENTER);
		BindingHelper.bindBidirectional(this.componentVerticalTextAlignment, tglVAlignBottom.selectedProperty(), VerticalTextAlignment.BOTTOM);
		tglVAlignTop.getStyleClass().add(Styles.LEFT_PILL);
		tglVAlignCenter.getStyleClass().add(Styles.CENTER_PILL);
		tglVAlignBottom.getStyleClass().add(Styles.RIGHT_PILL);
		HBox segComponentVAlignment = new HBox(tglVAlignTop, tglVAlignCenter, tglVAlignBottom);
		segComponentVAlignment.setAlignment(Pos.CENTER_RIGHT);
		
		TextField txtLineSpacing = new TextField();
		TextFormatter<Double> tfLineSpacing = new TextFormatter<Double>(LastValueNumberStringConverter.forDouble(originalValueText -> {
			Platform.runLater(() -> {
				txtLineSpacing.setText(originalValueText);
			});
		}));
		txtLineSpacing.setTextFormatter(tfLineSpacing);
		tfLineSpacing.valueProperty().bindBidirectional(this.componentLineSpacingAsObject);
		Label lblLineSpacingAdder = new Label(Translations.get("slide.measure.pixels.abbreviation"));
		lblLineSpacingAdder.setAlignment(Pos.CENTER);
		InputGroup grpLineSpacing = new InputGroup(txtLineSpacing, lblLineSpacingAdder);
		HBox.setHgrow(txtLineSpacing, Priority.ALWAYS);

		ToggleSwitch tglTextWrapping = new ToggleSwitch();
		tglTextWrapping.selectedProperty().bindBidirectional(this.componentTextWrapping);
		HBox boxTextWrapping = new HBox(tglTextWrapping);
		boxTextWrapping.setAlignment(Pos.CENTER_RIGHT);

		SlidePaddingPicker pkrComponentPadding = new SlidePaddingPicker();
		pkrComponentPadding.valueProperty().bindBidirectional(this.componentPadding);
		
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
		
		ToggleSwitch tglComponentCountdownTimeOnly = new ToggleSwitch();
		tglComponentCountdownTimeOnly.selectedProperty().bindBidirectional(this.componentCountdownTimeOnly);
		HBox boxComponentCountdownTimeOnly = new HBox(tglComponentCountdownTimeOnly);
		boxComponentCountdownTimeOnly.setAlignment(Pos.CENTER_RIGHT);
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				txtName,
				spnTime.getEditor(),
				txtComponentText,
				txtLineSpacing);
		
		// Layout
		
		EditorField fldName = new EditorField(Translations.get("slide.name"), txtName);
		EditorField fldTarget = new EditorField(Translations.get("slide.target"), Translations.get("slide.target.description"), cmbTargetSize);
		EditorField fldSize = new EditorField(Translations.get("slide.size"), pkrSlideSize);
		EditorField fldTime = new EditorField(Translations.get("slide.time"), Translations.get("slide.time.description"), spnTime);
		EditorField fldOpacity = new EditorField(Translations.get("slide.opacity"), Translations.get("slide.opacity.description"), pkrOpacity);
		EditorField fldTags = new EditorField(Translations.get("tags"), viewTags, EditorField.LAYOUT_VERTICAL);
		
		EditorFieldGroup grpSlide = new EditorFieldGroup(
				fldName, 
				fldTime, 
				fldOpacity, 
				new EditorDivider(Translations.get("slide.size")),
				fldSize,
				fldTarget, 
				fldTags);
		
		EditorTitledPane ttlSlide = new EditorTitledPane(Translations.get("slide"), grpSlide);
		EditorTitledPane ttlSlideBackground = new EditorTitledPane(Translations.get("slide.slide.background"), pkrBackground);
		EditorTitledPane ttlSlideBorder = new EditorTitledPane(Translations.get("slide.slide.border"), pkrBorder);
		EditorTitledPane ttlSlideTransition = new EditorTitledPane(Translations.get("slide.slide.transition"), pkrTransition);

		EditorFieldGroup grpTextComponentGeneral = new EditorFieldGroup(
				new EditorField(pkrComponentFont),
				new EditorField(Translations.get("slide.font.scale"), Translations.get("slide.font.scale.description"), cbComponentFontScaleType),
				new EditorDivider(Translations.get("slide.text.multiline")),
				new EditorField(Translations.get("slide.text.wrapping"), boxTextWrapping),
				new EditorField(Translations.get("slide.text.linespacing"), grpLineSpacing),
				new EditorDivider(Translations.get("slide.text.alignment")),
				new EditorField(Translations.get("slide.text.halignment"), segComponentHAlignment),
				new EditorField(Translations.get("slide.text.valignment"), segComponentVAlignment));
		
		EditorField fldComponentOpacity = new EditorField(Translations.get("slide.opacity"), sldComponentOpacity);
		EditorField fldText = new EditorField(Translations.get("slide.text"), txtComponentText, EditorField.LAYOUT_VERTICAL);
		EditorField fldPlaceholderType = new EditorField(Translations.get("slide.placeholder.type"), Translations.get("slide.placeholder.type.description"), cbComponentTextType);
		EditorField fldPlaceholderVariant = new EditorField(Translations.get("slide.placeholder.variant"), Translations.get("slide.placeholder.variant.description"), cbComponentTextVariant);
		EditorField fldDateTimeFormat = new EditorField(Translations.get("slide.datetime.format"), cmbComponentDateTimeFormat);
//		EditorField fldCountdownTarget = new EditorField(pkrComponentCountdownTarget);
		EditorField fldCountdownFormat = new EditorField(Translations.get("slide.countdown.format"), cmbComponentCountdownFormat);
		EditorField fldCountdownTimeOnly = new EditorField(Translations.get("slide.countdown.timeonly"), Translations.get("slide.countdown.timeonly.description"), boxComponentCountdownTimeOnly);
		
		fldText.managedProperty().bind(fldText.visibleProperty());
		fldPlaceholderType.managedProperty().bind(fldPlaceholderType.visibleProperty());
		fldPlaceholderVariant.managedProperty().bind(fldPlaceholderVariant.visibleProperty());
		fldDateTimeFormat.managedProperty().bind(fldDateTimeFormat.visibleProperty());
		pkrComponentCountdownTarget.managedProperty().bind(pkrComponentCountdownTarget.visibleProperty());
		fldCountdownFormat.managedProperty().bind(fldCountdownFormat.visibleProperty());
		fldCountdownTimeOnly.managedProperty().bind(fldCountdownTimeOnly.visibleProperty());
		pkrComponentMedia.managedProperty().bind(pkrComponentMedia.visibleProperty());
		
		EditorFieldGroup grpComponent = new EditorFieldGroup(
				fldComponentOpacity,
				fldText,
				fldPlaceholderType,
				fldPlaceholderVariant,
				fldDateTimeFormat,
				pkrComponentCountdownTarget,
				fldCountdownFormat,
				fldCountdownTimeOnly,
				pkrComponentMedia);
		
		Node[] componentTypeFields = new Node[] {
			fldText,
			fldPlaceholderType,
			fldPlaceholderVariant,
			fldDateTimeFormat,
			pkrComponentCountdownTarget,
			fldCountdownFormat,
			fldCountdownTimeOnly,
			pkrComponentMedia
		};
		
		this.selectedComponent.addListener((obs, ov, nv) -> {
			for (Node field : componentTypeFields) {
				field.setVisible(false);
			}
			
			if (nv != null) {
				if (nv instanceof TextPlaceholderComponent) {
					fldPlaceholderType.setVisible(true);
					fldPlaceholderVariant.setVisible(true);
				} else if (nv instanceof DateTimeComponent) {
					fldDateTimeFormat.setVisible(true);
				} else if (nv instanceof CountdownComponent) {
					pkrComponentCountdownTarget.setVisible(true);
					fldCountdownFormat.setVisible(true);
					fldCountdownTimeOnly.setVisible(true);
				} else if (nv instanceof TextComponent) {
					fldText.setVisible(true);
				} else if (nv instanceof MediaComponent) {
					pkrComponentMedia.setVisible(true);
				} else {
					LOGGER.warn("Unsupported component type: '" + nv.getClass().getName() + "'");
				}
			}
		});
		
		EditorTitledPane ttlComponent = new EditorTitledPane(Translations.get("slide.component"), grpComponent);
		EditorTitledPane ttlComponentBackground = new EditorTitledPane(Translations.get("slide.component.background"), pkrComponentBackground);
		EditorTitledPane ttlComponentBorder = new EditorTitledPane(Translations.get("slide.component.border"), pkrComponentBorder);
		EditorTitledPane ttlComponentShadow = new EditorTitledPane(Translations.get("slide.component.shadow"), pkrComponentShadow);
		EditorTitledPane ttlComponentGlow = new EditorTitledPane(Translations.get("slide.component.glow"), pkrComponentGlow);
		
		EditorTitledPane ttlComponentTextFont = new EditorTitledPane(Translations.get("slide.component.text.font"), grpTextComponentGeneral);
		EditorTitledPane ttlComponentTextPadding = new EditorTitledPane(Translations.get("slide.component.text.padding"), pkrComponentPadding);
		EditorTitledPane ttlComponentTextFill = new EditorTitledPane(Translations.get("slide.component.text.fill"), pkrComponentTextPaint);
		EditorTitledPane ttlComponentTextBorder = new EditorTitledPane(Translations.get("slide.component.text.border"), pkrComponentTextBorder);
		EditorTitledPane ttlComponentTextShadow = new EditorTitledPane(Translations.get("slide.component.text.shadow"), pkrComponentTextShadow);
		EditorTitledPane ttlComponentTextGlow = new EditorTitledPane(Translations.get("slide.component.text.glow"), pkrComponentTextGlow);
		
		ttlComponent.textProperty().bind(Bindings.createStringBinding(() -> {
			SlideComponent sc = this.selectedComponent.get();
			if (sc == null) return null;
			if (sc instanceof TextPlaceholderComponent) {
				return Translations.get("slide.component.placeholder");
			} else if (sc instanceof DateTimeComponent) {
				return Translations.get("slide.component.datetime");
			} else if (sc instanceof CountdownComponent) {
				return Translations.get("slide.component.countdown");
			} else if (sc instanceof TextComponent) {
				return Translations.get("slide.component.text");
			} else if (sc instanceof MediaComponent) {
				return Translations.get("slide.component.media");
			} else {
				LOGGER.warn("Unsupported component type: '" + sc.getClass().getName() + "'");
				return Translations.get("slide.component");
			}
		}, this.selectedComponent));
		
		EditorTitledPane[] componentGroups = new EditorTitledPane[] {
			ttlComponent,
			ttlComponentBackground,
			ttlComponentBorder,
			ttlComponentShadow,
			ttlComponentGlow
		};
		
		EditorTitledPane[] textComponentGroups = new EditorTitledPane[] {
			ttlComponentTextFont,
			ttlComponentTextPadding,
			ttlComponentTextFill,
			ttlComponentTextBorder,
			ttlComponentTextShadow,
			ttlComponentTextGlow
		};
		
		for (EditorTitledPane group : componentGroups) {
			group.visibleProperty().bind(componentSelected);
			group.managedProperty().bind(group.visibleProperty());
		}
		
		for (EditorTitledPane group : textComponentGroups) {
			group.visibleProperty().bind(textComponentSelected);
			group.managedProperty().bind(group.visibleProperty());
		}
		
		EditorTitledPane[] collapsedByDefault = new EditorTitledPane[] {
			ttlSlideBackground,
			ttlSlideBorder,
			ttlSlideTransition,
			ttlComponentBackground,
			ttlComponentBorder,
			ttlComponentShadow,
			ttlComponentGlow,
			ttlComponentTextFont,
			ttlComponentTextPadding,
			ttlComponentTextFill,
			ttlComponentTextBorder,
			ttlComponentTextShadow,
			ttlComponentTextGlow
		};
		
		for (EditorTitledPane group : collapsedByDefault) {
			group.setExpanded(false);
		}
		
		this.getChildren().addAll(
				ttlSlide,
				ttlSlideBackground,
				ttlSlideBorder,
				ttlSlideTransition,
				ttlComponent,
				ttlComponentBackground,
				ttlComponentBorder,
				ttlComponentShadow,
				ttlComponentGlow,
				ttlComponentTextFont,
				ttlComponentTextPadding,
				ttlComponentTextFill,
				ttlComponentTextBorder,
				ttlComponentTextShadow,
				ttlComponentTextGlow);
	}
	
	private void updateSlideResolutions(ObservableList<Resolution> resolutions) {
		// add any screen sizes based on the current set of slides
		List<Slide> slides = this.context.getWorkspaceManager().getItemsUnmodifiable(Slide.class);
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
			Resolution r = ScreenHelper.getResolution(screen);
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
			Rectangle2D bounds = ScreenHelper.getScaledScreenBounds(screen);
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
