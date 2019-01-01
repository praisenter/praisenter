package org.praisenter.ui.slide;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;
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
import org.praisenter.ui.controls.LongSpinnerValueFactory;
import org.praisenter.ui.controls.LastValueDoubleStringConverter;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TimeStringConverter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

// TODO paint selection control
// TODO border selection control
// TODO effect (shadow/glow) selection control
// TODO padding selection control
// TODO font selection control
// TODO date format control
// TODO countdown format control
// TODO date+time control (date already exists) for time we might be able to just do a specialized converter see https://stackoverflow.com/questions/32613619/how-to-make-a-timespinner-in-javafx

public final class SlideSelectionEditor extends VBox implements DocumentSelectionEditor<Slide> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Slide>> documentContext;
	
	private final ObjectProperty<Slide> slide;
	private final ObjectProperty<Object> selectedItem;

	// slide
	private final StringProperty name;
	private final LongProperty time;
	private final ObjectProperty<Long> timeAsObject;
	private final ObjectProperty<SlidePaint> background;
	private final ObjectProperty<SlideStroke> border;
	private final DoubleProperty opacity;
	private final ObjectProperty<Double> opacityAsObject;
	private final DoubleProperty width;
	private final DoubleProperty height;
	private final ObservableSet<Tag> tags;
	private final ObservableList<SlideAnimation> animations;
	
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
	private final BooleanBinding placeholderComponentSelected;
	private final BooleanBinding dateTimeComponentSelected;
	private final BooleanBinding countdownComponentSelected;
	
	public SlideSelectionEditor(GlobalContext context) {
		this.getStyleClass().add("p-selection-properties");
		
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		
		this.slide = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.time = new SimpleLongProperty();
		this.timeAsObject = this.time.asObject();
		this.background = new SimpleObjectProperty<>();
		this.border = new SimpleObjectProperty<>();
		this.opacity = new SimpleDoubleProperty();
		this.opacityAsObject = this.opacity.asObject();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.animations = FXCollections.observableArrayList();
		
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
		this.placeholderComponentSelected = this.selectedTextPlaceholderComponent.isNotNull();
		this.dateTimeComponentSelected = this.selectedDateTimeComponent.isNotNull();
		this.countdownComponentSelected = this.selectedCountdownComponent.isNotNull();
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.slide.unbind();
			this.selectedItem.unbind();
			if (nv != null) {
				this.slide.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
			} else {
				this.slide.set(null);
				this.selectedItem.set(null);
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			if (nv != null && nv instanceof SlideComponent) {
				this.selectedComponent.set((SlideComponent)nv);
			} else {
				this.selectedComponent.set(null);
			}
		});
		
		this.selectedComponent.addListener((obs, ov, nv) -> {
			if (nv != null) {
				if (nv instanceof TextComponent) {
					this.selectedMediaComponent.set(null);
					this.selectedTextComponent.set((TextComponent)nv);
				} else {
					this.selectedTextComponent.set(null);
					this.selectedCountdownComponent.set(null);
					this.selectedDateTimeComponent.set(null);
					this.selectedTextPlaceholderComponent.set(null);
				}
				
				if (nv instanceof MediaComponent) {
					this.selectedMediaComponent.set((MediaComponent)nv);
				} else if (nv instanceof TextPlaceholderComponent) {
					this.selectedTextPlaceholderComponent.set((TextPlaceholderComponent)nv);
					this.selectedCountdownComponent.set(null);
					this.selectedDateTimeComponent.set(null);
				} else if (nv instanceof DateTimeComponent) {
					this.selectedDateTimeComponent.set((DateTimeComponent)nv);
					this.selectedCountdownComponent.set(null);
					this.selectedTextPlaceholderComponent.set(null);
				} else if (nv instanceof CountdownComponent) {
					this.selectedCountdownComponent.set((CountdownComponent)nv);
					this.selectedDateTimeComponent.set(null);
					this.selectedTextPlaceholderComponent.set(null);
				}
			}
		});
		
		this.slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.time.unbindBidirectional(ov.timeProperty());
				this.background.unbindBidirectional(ov.backgroundProperty());
				this.border.unbindBidirectional(ov.borderProperty());
				this.opacity.unbindBidirectional(ov.opacityProperty());
				this.width.unbindBidirectional(ov.widthProperty());
				this.height.unbindBidirectional(ov.heightProperty());
				
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				Bindings.unbindContentBidirectional(this.animations, ov.getAnimations());
				
				this.tags.clear();
				this.animations.clear();
			}
			
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.time.bindBidirectional(nv.timeProperty());
				this.background.bindBidirectional(nv.backgroundProperty());
				this.border.bindBidirectional(nv.borderProperty());
				this.opacity.bindBidirectional(nv.opacityProperty());
				this.width.bindBidirectional(nv.widthProperty());
				this.height.bindBidirectional(nv.heightProperty());
				
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
				Bindings.bindContentBidirectional(this.animations, nv.getAnimations());
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
		
		Label lblName = new Label(Translations.get("slide.name"));
		TextField txtName = new TextField();
		txtName.textProperty().bindBidirectional(this.name);
		
		Label lblTime = new Label(Translations.get("slide.time"));
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
		
		Label lblOpacity = new Label(Translations.get("slide.opacity"));
		Spinner<Double> spnOpacity = new Spinner<>(0, 1, 1, 0.1);
		spnOpacity.setEditable(true);
		spnOpacity.getValueFactory().setConverter(new LastValueDoubleStringConverter((originalValueText) -> {
			Platform.runLater(() -> {
				spnOpacity.getEditor().setText(originalValueText);
			});
		}));
		spnOpacity.getValueFactory().valueProperty().bindBidirectional(this.opacityAsObject);
		
//		Label lblBibleName = new Label(Translations.get("bible.name"));
//		TextField txtBibleName = new TextField();
//		txtBibleName.textProperty().bindBidirectional(this.name);
//
//		Label lblBibleLanguage = new Label(Translations.get("bible.language"));
//		TextField txtBibleLanguage = new TextField();
//		txtBibleLanguage.textProperty().bindBidirectional(this.language);
//		
//		Label lblBibleSource = new Label(Translations.get("bible.source"));
//		TextField txtBibleSource = new TextField();
//		txtBibleSource.textProperty().bindBidirectional(this.source);
//		
//		Label lblBibleCopyright = new Label(Translations.get("bible.copyright"));
//		TextField txtBibleCopyright = new TextField();
//		txtBibleCopyright.textProperty().bindBidirectional(this.copyright);
//
//		Label lblBibleNotes = new Label(Translations.get("bible.notes"));
//		TextArea txtBibleNotes = new TextArea();
//		txtBibleNotes.textProperty().bindBidirectional(this.notes);
//		txtBibleNotes.setWrapText(true);
		
		TagListView viewTags = new TagListView(this.context.getDataManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
//		Label lblBookName = new Label(Translations.get("bible.name"));
//		TextField txtBookName = new TextField();
//		txtBookName.textProperty().bindBidirectional(this.bookName);
//		
//		Label lblBookNumber = new Label(Translations.get("bible.number"));
//		Spinner<Integer> spnBookNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
//		spnBookNumber.setEditable(true);
//		spnBookNumber.getValueFactory().valueProperty().bindBidirectional(this.bookNumber2);
//		
//		Label lblChapterNumber = new Label(Translations.get("bible.number"));
//		Spinner<Integer> spnChapterNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
//		spnChapterNumber.setEditable(true);
//		spnChapterNumber.getValueFactory().valueProperty().bindBidirectional(this.chapterNumber2);
//		
//		Label lblVerseText = new Label(Translations.get("bible.text"));
//		TextArea txtVerseText = new TextArea();
//		txtVerseText.textProperty().bindBidirectional(this.verseText);
//		txtVerseText.setWrapText(true);
//		
//		Label lblVerseNumber = new Label(Translations.get("bible.number"));
//		Spinner<Integer> spnVerseNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
//		spnVerseNumber.setEditable(true);
//		spnVerseNumber.getValueFactory().valueProperty().bindBidirectional(this.verseNumber2);
//		
//		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
//				txtBibleName,
//				txtBibleLanguage,
//				txtBibleSource,
//				txtBibleCopyright,
//				txtBibleNotes,
//				txtBookName,
//				spnBookNumber.getEditor(),
//				spnChapterNumber.getEditor(),
//				txtVerseText,
//				spnVerseNumber.getEditor());
//		
//		TitledPane ttlBible = new TitledPane(Translations.get("bible"), new VBox(
//				lblBibleName, txtBibleName,
//				lblBibleLanguage, txtBibleLanguage,
//				lblBibleSource, txtBibleSource,
//				lblBibleCopyright, txtBibleCopyright,
//				lblBibleNotes, txtBibleNotes,
//				viewTags));
//		
//		TitledPane ttlBook = new TitledPane(Translations.get("bible.book"), new VBox(
//				lblBookNumber, spnBookNumber,
//				lblBookName, txtBookName));
//
//		TitledPane ttlChapter = new TitledPane(Translations.get("bible.chapter"), new VBox(
//				lblChapterNumber, spnChapterNumber));
//		
//		TitledPane ttlVerse = new TitledPane(Translations.get("bible.verse"), new VBox(
//				lblVerseNumber, spnVerseNumber,
//				lblVerseText, txtVerseText));
//		
		this.getChildren().addAll(
				lblName,
				txtName,
				lblTime,
				spnTime,
				lblOpacity,
				spnOpacity,
				viewTags);
		
		// hide/show
		
//		BooleanBinding bibleSelected = this.documentContext.isNotNull();
//		ttlBible.visibleProperty().bind(bibleSelected);
//		ttlBible.managedProperty().bind(bibleSelected);
//		
//		BooleanBinding bookSelected = this.selectedBook.isNotNull();
//		ttlBook.visibleProperty().bind(bookSelected);
//		ttlBook.managedProperty().bind(bookSelected);
//		
//		BooleanBinding chapterSelected = this.selectedChapter.isNotNull();
//		ttlChapter.visibleProperty().bind(chapterSelected);
//		ttlChapter.managedProperty().bind(chapterSelected);
//		
//		BooleanBinding verseSelected = this.selectedVerse.isNotNull();
//		ttlVerse.visibleProperty().bind(verseSelected);
//		ttlVerse.managedProperty().bind(verseSelected);
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
