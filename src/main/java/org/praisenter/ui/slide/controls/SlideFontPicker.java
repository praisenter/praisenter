package org.praisenter.ui.slide.controls;

import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.SlideFontPosture;
import org.praisenter.data.slide.text.SlideFontWeight;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.EditorDivider;
import org.praisenter.ui.controls.EditorField;
import org.praisenter.ui.controls.EditorFieldGroup;
import org.praisenter.ui.controls.LastValueNumberStringConverter;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.text.Font;
import javafx.util.Callback;

// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, update this class to always show the bold/italic buttons https://bugs.openjdk.java.net/browse/JDK-8091064
// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, consider changing the bold button to a drop down of values https://bugs.openjdk.java.net/browse/JDK-8091064
// see also https://bitbucket.org/controlsfx/controlsfx/src/13e52b38df16842b71a4c9df1cadbeba6087742a/controlsfx/src/main/java/org/controlsfx/dialog/FontSelectorDialog.java?at=default&fileviewer=file-view-default

public final class SlideFontPicker extends EditorFieldGroup {
	private final ObservableList<String> families;
	private final Font defaultJavaFXFont;
	
	private final ObjectProperty<SlideFont> value = new SimpleObjectProperty<SlideFont>();

	private final StringProperty family;
	private final ObjectProperty<SlideFontWeight> weight;
	private final ObjectProperty<SlideFontPosture> posture;
	private final DoubleProperty size;
	private final ObjectProperty<Double> sizeAsObject;
	
	public SlideFontPicker(String label) {
		// load the fonts
		this.families = FXCollections.observableArrayList(Font.getFamilies());
		
		// get the base UI font from a label
		this.defaultJavaFXFont = (new Label()).getFont();
		
		this.family = new SimpleStringProperty(this.defaultJavaFXFont.getFamily());
		this.weight = new SimpleObjectProperty<>();
		this.posture = new SimpleObjectProperty<>();
		this.size = new SimpleDoubleProperty();
		this.sizeAsObject = this.size.asObject();
		
		ComboBox<String> cmbFamily = new ComboBox<>(this.families);
		cmbFamily.setMaxWidth(Double.MAX_VALUE);
		cmbFamily.valueProperty().bindBidirectional(this.family);
		
		ObservableList<Option<SlideFontWeight>> weights = FXCollections.observableArrayList();
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.BLACK), SlideFontWeight.BLACK));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.EXTRA_BOLD), SlideFontWeight.EXTRA_BOLD));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.BOLD), SlideFontWeight.BOLD));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.SEMI_BOLD), SlideFontWeight.SEMI_BOLD));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.MEDIUM), SlideFontWeight.MEDIUM));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.NORMAL), SlideFontWeight.NORMAL));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.LIGHT), SlideFontWeight.LIGHT));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.EXTRA_LIGHT), SlideFontWeight.EXTRA_LIGHT));
		weights.add(new Option<>(Translations.get("slide.font.weight." + SlideFontWeight.THIN), SlideFontWeight.THIN));
		ComboBox<Option<SlideFontWeight>> cmbWeight = new ComboBox<>(weights);
		cmbWeight.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbWeight.valueProperty(), this.weight);
		
		ObservableList<Option<SlideFontPosture>> postures = FXCollections.observableArrayList();
		postures.add(new Option<>(Translations.get("slide.font.posture." + SlideFontPosture.REGULAR), SlideFontPosture.REGULAR));
		postures.add(new Option<>(Translations.get("slide.font.posture." + SlideFontPosture.ITALIC), SlideFontPosture.ITALIC));
		ComboBox<Option<SlideFontPosture>> cmbPosture = new ComboBox<>(postures);
		cmbPosture.setMaxWidth(Double.MAX_VALUE);
		BindingHelper.bindBidirectional(cmbPosture.valueProperty(), this.posture);

		Spinner<Double> spnSize = new Spinner<>(1, Double.MAX_VALUE, 20, 1);
		spnSize.setEditable(true);
		spnSize.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		spnSize.getValueFactory().setConverter(LastValueNumberStringConverter.forDouble((originalValueText) -> {
			Platform.runLater(() -> {
				spnSize.getEditor().setText(originalValueText);
			});
		}));
		spnSize.getValueFactory().valueProperty().bindBidirectional(this.sizeAsObject);
		
		// set the cell factory to use the font to display the font name
		cmbFamily.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> listview) {
                return new ListCell<String>() {
                    @Override protected void updateItem(String family, boolean empty) {
                        super.updateItem(family, empty);
                        if (!empty) {
                            setFont(Font.font(family));
                            setText(family);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
		
		BindingHelper.bindBidirectional(this.value, this.family, new ObjectConverter<SlideFont, String>() {
			@Override
			public String convertFrom(SlideFont t) {
				if (t == null) return defaultJavaFXFont.getFamily();
				return t.getFamily();
			}
			@Override
			public SlideFont convertTo(String e) {
				return SlideFontPicker.this.getCurrentValue();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.weight, new ObjectConverter<SlideFont, SlideFontWeight>() {
			@Override
			public SlideFontWeight convertFrom(SlideFont t) {
				if (t == null) return SlideFontWeight.NORMAL;
				return t.getWeight();
			}
			@Override
			public SlideFont convertTo(SlideFontWeight e) {
				return SlideFontPicker.this.getCurrentValue();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.posture, new ObjectConverter<SlideFont, SlideFontPosture>() {
			@Override
			public SlideFontPosture convertFrom(SlideFont t) {
				if (t == null) return SlideFontPosture.REGULAR;
				return t.getPosture();
			}
			@Override
			public SlideFont convertTo(SlideFontPosture e) {
				return SlideFontPicker.this.getCurrentValue();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.sizeAsObject, new ObjectConverter<SlideFont, Double>() {
			@Override
			public Double convertFrom(SlideFont t) {
				if (t == null) return defaultJavaFXFont.getSize();
				return t.getSize();
			}
			@Override
			public SlideFont convertTo(Double e) {
				return SlideFontPicker.this.getCurrentValue();
			}
		});
		
//		/**
//		 * Updates the visibility of the bold and italic buttons based on the
//		 * given font family's available styles.
//		 * <p>
//		 * This is done because Java FX (at the time of this class being created) doesn't support
//		 * deriving fonts like AWT did.  As a result, setting a font weight on a font that doesn't
//		 * support that weight has no effect.  So to reduce confusion we just hide the buttons
//		 * when they aren't supported.  If a later version of Java FX supports it we can remove this
//		 * as the underlying data model does support it.
//		 * @param family the font family
//		 */
//		cmbFamily.valueProperty().addListener((obs, ov, nv) -> {
//			boolean hasBold = false;
//			boolean hasItalic = false;
//			String family = this.family.get();
//			List<String> names = Font.getFontNames(family);
//			for (String name : names) {
//				String styles = name.replace(family, "").toUpperCase();
//				hasBold |= styles.contains("BOLD");
//				hasItalic |= styles.contains("ITALIC");
//			}
//			tglBold.setDisable(!hasBold);
//			tglItalic.setDisable(!hasItalic);
//		});
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(spnSize.getEditor());
		
		EditorField fldFontFamily = new EditorField(label, cmbFamily);
		EditorField fldFontWeight = new EditorField(Translations.get("slide.font.weight"), cmbWeight);
		EditorField fldFontPosture = new EditorField(Translations.get("slide.font.style"), cmbPosture);
		EditorField fldFontSize = new EditorField(Translations.get("slide.font.size"), spnSize);
		
		this.getChildren().addAll(
				fldFontFamily,
				fldFontWeight,
				fldFontPosture,
				new EditorDivider(Translations.get("slide.font.size")),
				fldFontSize);
	}
	
	private SlideFont getCurrentValue() {
		return new SlideFont(
				this.family.get(), 
				this.weight.get(), 
				this.posture.get(), 
				this.size.get());
	}
	
	public ObjectProperty<SlideFont> valueProperty() {
		return this.value;
	}
	
	public SlideFont getValue() {
		return this.value.get();
	}
	
	public void setValue(SlideFont font) {
		this.value.set(font);
	}
}
