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
package org.praisenter.ui.slide.controls;

import java.util.List;

import org.praisenter.data.slide.text.SlideFont;
import org.praisenter.data.slide.text.SlideFontPosture;
import org.praisenter.data.slide.text.SlideFontWeight;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.bind.ObjectConverter;
import org.praisenter.ui.controls.LastValueDoubleStringConverter;
import org.praisenter.ui.controls.TextInputFieldEventFilter;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, update this class to always show the bold/italic buttons https://bugs.openjdk.java.net/browse/JDK-8091064
// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, consider changing the bold button to a drop down of values https://bugs.openjdk.java.net/browse/JDK-8091064
// see also https://bitbucket.org/controlsfx/controlsfx/src/13e52b38df16842b71a4c9df1cadbeba6087742a/controlsfx/src/main/java/org/controlsfx/dialog/FontSelectorDialog.java?at=default&fileviewer=file-view-default

/**
 * A picker control for selecting a Java FX font.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideFontPicker extends VBox {
	private final ObservableList<String> families;
	private final Font defaultJavaFXFont;
	
	private final ObjectProperty<SlideFont> value = new SimpleObjectProperty<SlideFont>();

	private final StringProperty family;
	private final BooleanProperty bold;
	private final BooleanProperty italic;
	private final DoubleProperty size;
	private final ObjectProperty<Double> sizeAsObject;
	
	/**
	 * Full constructor.
	 * @param families the list of font families
	 */
	public SlideFontPicker() {
		// load the fonts
		this.families = FXCollections.observableArrayList(Font.getFamilies());
		
		// get the base UI font from a label
		this.defaultJavaFXFont = (new Label()).getFont();
		
		this.family = new SimpleStringProperty(this.defaultJavaFXFont.getFamily());
		this.bold = new SimpleBooleanProperty();
		this.italic = new SimpleBooleanProperty();
		this.size = new SimpleDoubleProperty();
		this.sizeAsObject = this.size.asObject();
		
		ComboBox<String> cmbFamily = new ComboBox<>(this.families);
		cmbFamily.setMinWidth(0);
		cmbFamily.valueProperty().bindBidirectional(this.family);
		
		ToggleButton tglBold = new ToggleButton("b");
		tglBold.selectedProperty().bindBidirectional(this.bold);
		
		ToggleButton tglItalic = new ToggleButton("i");
		tglItalic.selectedProperty().bindBidirectional(this.italic);
		
		Spinner<Double> spnSize = new Spinner<>(1, Double.MAX_VALUE, 20, 1);
		spnSize.setEditable(true);
		spnSize.getValueFactory().setConverter(new LastValueDoubleStringConverter((originalValueText) -> {
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
				return SlideFontPicker.this.getControlValues();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.bold, new ObjectConverter<SlideFont, Boolean>() {
			@Override
			public Boolean convertFrom(SlideFont t) {
				if (t == null) return false;
				return t.getWeight() == SlideFontWeight.BOLD;
			}
			@Override
			public SlideFont convertTo(Boolean e) {
				return SlideFontPicker.this.getControlValues();
			}
		});
		
		BindingHelper.bindBidirectional(this.value, this.italic, new ObjectConverter<SlideFont, Boolean>() {
			@Override
			public Boolean convertFrom(SlideFont t) {
				if (t == null) return false;
				return t.getPosture() == SlideFontPosture.ITALIC;
			}
			@Override
			public SlideFont convertTo(Boolean e) {
				return SlideFontPicker.this.getControlValues();
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
				return SlideFontPicker.this.getControlValues();
			}
		});
		
		/**
		 * Updates the visibility of the bold and italic buttons based on the
		 * given font family's available styles.
		 * <p>
		 * This is done because Java FX (at the time of this class being created) doesn't support
		 * deriving fonts like AWT did.  As a result, setting a font weight on a font that doesn't
		 * support that weight has no effect.  So to reduce confusion we just hide the buttons
		 * when they aren't supported.  If a later version of Java FX supports it we can remove this
		 * as the underlying data model does support it.
		 * @param family the font family
		 */
		cmbFamily.valueProperty().addListener((obs, ov, nv) -> {
			boolean hasBold = false;
			boolean hasItalic = false;
			String family = this.family.get();
			List<String> names = Font.getFontNames(family);
			for (String name : names) {
				String styles = name.replace(family, "").toUpperCase();
				hasBold |= styles.contains("BOLD");
				hasItalic |= styles.contains("ITALIC");
			}
			tglBold.setDisable(!hasBold);
			tglItalic.setDisable(!hasItalic);
		});
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				spnSize.getEditor());

		this.setSpacing(5);
		this.getChildren().addAll(
				new HBox(5, cmbFamily, new HBox(tglBold, tglItalic)), 
				spnSize);
	}
	
	/**
	 * Creates a new font using the current values of the controls.
	 * @return {@link SlideFont}
	 */
	private SlideFont getControlValues() {
		return new SlideFont(
				this.family.get(), 
				this.bold.get() ? SlideFontWeight.BOLD : SlideFontWeight.NORMAL, 
				this.italic.get() ? SlideFontPosture.ITALIC : SlideFontPosture.REGULAR, 
				this.size.get());
	}
	
	/**
	 * Returns the font property.
	 * @return ObjectProperty&lt;Font&gt;
	 */
	public ObjectProperty<SlideFont> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current font.
	 * @return Font
	 */
	public SlideFont getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current font.
	 * @param font the font
	 */
	public void setValue(SlideFont font) {
		this.value.set(font);
	}
}
