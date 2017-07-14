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
package org.praisenter.javafx.slide.editor.controls;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, update this class to always show the bold/italic buttons https://bugs.openjdk.java.net/browse/JDK-8091064
// JAVABUG (L) 03/15/16 Java FX does not have a facility to derive fonts; If this is added in the future, consider changing the bold button to a drop down of values https://bugs.openjdk.java.net/browse/JDK-8091064
// see also https://bitbucket.org/controlsfx/controlsfx/src/13e52b38df16842b71a4c9df1cadbeba6087742a/controlsfx/src/main/java/org/controlsfx/dialog/FontSelectorDialog.java?at=default&fileviewer=file-view-default

/**
 * A picker control for selecting a Java FX font.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideFontPicker extends HBox {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The base UI font (from a label) */
	private final Font base;
	
	/** The list of font families */
	private final ObservableList<String> families;
	
	/** The configured font */
	private final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>() {
		public void set(SlideFont font) {
			if (mutating) return;
			if (font != null) {
				mutating = true;
				setControlValues(font);
				mutating = false;
			}
			super.set(getControlValues());
		}
		public void setValue(SlideFont font) {
			set(font);
		}
	};

	/** True if the control values are being set */
	private boolean mutating = false;
	
	// nodes
	
	/** The font family drop down */
	private final ComboBox<String> cmbFamily;
	
	/** The bold button */
	private final ToggleButton tglBold;
	
	/** The italic button */
	private final ToggleButton tglItalic;
	
	/** The font size spinner */
	private final Spinner<Double> spnSize;
	
	/**
	 * Full constructor.
	 * @param families the list of font families
	 */
	public SlideFontPicker(ObservableList<String> families) {
		this.families = families;
		
		// get the base UI font
		this.base = (new Label()).getFont();
		
		this.cmbFamily = new ComboBox<>(this.families);
		this.cmbFamily.setPrefWidth(100);
		this.cmbFamily.setValue(base.getFamily());
		this.cmbFamily.setTooltip(new Tooltip("The text font"));
		
		this.tglBold = new ToggleButton("b");
		Font def = this.tglBold.getFont();
		this.tglBold.setFont(Font.font(def.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, def.getSize()));
		this.tglBold.managedProperty().bind(this.tglBold.visibleProperty());
		this.tglBold.setTooltip(new Tooltip("Bold"));
		
		this.tglItalic = new ToggleButton("i");
		def = this.tglItalic.getFont();
		this.tglItalic.setFont(Font.font(def.getFamily(), FontWeight.NORMAL, FontPosture.ITALIC, def.getSize()));
		this.tglItalic.managedProperty().bind(this.tglItalic.visibleProperty());
		this.tglItalic.setTooltip(new Tooltip("Italic"));
		
		this.spnSize = new Spinner<>(1, Double.MAX_VALUE, 20, 1);
		this.spnSize.setPrefWidth(65);
		// allow manual typing
		this.spnSize.setEditable(true);
		// commit the value as the user types
		this.spnSize.editorProperty().get().textProperty().addListener((obs, ov, nv) -> {
			spnSize.editorProperty().get().commitValue();
		});
		this.spnSize.setTooltip(new Tooltip("Font Size"));
		this.spnSize.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, new PreventUndoRedoEventFilter(this));
		
		// set the cell factory to use the font to display the font name
		this.cmbFamily.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
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
		
		// setup listeners
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				// update the font
				SlideFontPicker.this.font.set(null);
			}
		};
		
		this.cmbFamily.valueProperty().addListener(listener);
		this.cmbFamily.valueProperty().addListener((obs, ov, nv) -> {
			updateAvailableFontStyles(nv);
		});
		this.tglBold.selectedProperty().addListener(listener);
		this.tglItalic.selectedProperty().addListener(listener);
		this.spnSize.valueProperty().addListener(listener);

		this.setSpacing(2);
		this.getChildren().addAll(this.cmbFamily, this.tglBold, this.tglItalic, this.spnSize);
	}
	
	/**
	 * Creates a new font using the current values of the controls.
	 * @return {@link SlideFont}
	 */
	private SlideFont getControlValues() {
		return new SlideFont(
				this.cmbFamily.getValue(), 
				this.tglBold.isSelected() ? SlideFontWeight.BOLD : SlideFontWeight.NORMAL, 
				this.tglItalic.isSelected() ? SlideFontPosture.ITALIC : SlideFontPosture.REGULAR, 
				this.spnSize.getValue());
	}
	
	/**
	 * Sets the values of the controls to the given font values.
	 * @param font the font
	 */
	private void setControlValues(SlideFont font) {
		String family = font.getFamily();
		// check if the family is available on this system
		if (!families.contains(family)) {
			LOGGER.warn("Font family '{}' not found.", family);
			// set the default font
			this.cmbFamily.setValue(base.getFamily());
		} else {
			this.cmbFamily.setValue(font.getFamily());
		}
		
		// get the font weight and posture from the style
		updateAvailableFontStyles(family);
		boolean isBold = font.getWeight() == SlideFontWeight.BOLD;
		boolean isItalic = font.getPosture() == SlideFontPosture.ITALIC;
		if (isBold) {
			this.tglBold.setSelected(true);
		} else {
			this.tglBold.setSelected(false);
		}
		if (isItalic) {
			this.tglItalic.setSelected(true);
		} else {
			this.tglItalic.setSelected(false);
		}
		
		// set the size
		this.spnSize.getValueFactory().setValue(font.getSize());
	}
	
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
	private void updateAvailableFontStyles(String family) {
		boolean hasBold = false;
		boolean hasItalic = false;
		List<String> names = Font.getFontNames(family);
		for (String name : names) {
			String styles = name.replace(family, "").toUpperCase();
			hasBold |= styles.contains("BOLD");
			hasItalic |= styles.contains("ITALIC");
		}
		this.tglBold.setDisable(!hasBold);
		this.tglItalic.setDisable(!hasItalic);
	}
	
	/**
	 * Returns the font property.
	 * @return ObjectProperty&lt;Font&gt;
	 */
	public ObjectProperty<SlideFont> fontProperty() {
		return this.font;
	}
	
	/**
	 * Returns the current font.
	 * @return Font
	 */
	public SlideFont getFont() {
		return this.font.get();
	}
	
	/**
	 * Sets the current font.
	 * @param font the font
	 */
	public void setFont(SlideFont font) {
		this.font.set(font);
	}
}
