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
package org.praisenter.javafx.bible;

import java.text.DateFormat;

import org.praisenter.bible.Bible;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Pane specifically for display of {@link Bible}s.
 * @author William Bittle
 * @version 3.0.0
 */
final class BibleMetadataPane extends VBox {
	/** A simple bottom border for displaying on labels */
	private static final Border VALUE_BORDER = new Border(new BorderStroke(Color.color(0.7, 0.7, 0.7), BorderStrokeStyle.DASHED, null, new BorderWidths(0, 0, 1, 0)));
	
	/** The date formatter */
	private static final DateFormat DATE_FORMATTER = DateFormat.getDateInstance(DateFormat.LONG);
	
	// properties
	
	/** The bible */
	private final ObjectProperty<BibleListItem> bible = new SimpleObjectProperty<BibleListItem>();
	
	// the sub properties
	
	/** The name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The language */
	private final StringProperty language = new SimpleStringProperty();
	
	/** The source */
	private final StringProperty source = new SimpleStringProperty();
	
	/** The import date */
	private final StringProperty importDate = new SimpleStringProperty();
	
	/** Has copyright */
	private final StringProperty copyright = new SimpleStringProperty();
	
	/** The verse count */
	private final StringProperty verseCount = new SimpleStringProperty();
	
	/** Has apocrypha */
	private final StringProperty hasApocrypha = new SimpleStringProperty();
	
	/** Had import errors */
	private final StringProperty hadImportErrors = new SimpleStringProperty();
	
	/**
	 * Creates a new metadata pane.
	 */
	public BibleMetadataPane() {
		this.setPadding(new Insets(0, 5, 10, 5));
		this.setDisable(true);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(5);
		grid.setPadding(new Insets(5));
		
        ColumnConstraints labels = new ColumnConstraints();
        labels.setHgrow(Priority.NEVER);
        labels.setMinWidth(ConstraintsBase.CONSTRAIN_TO_PREF);
        grid.getColumnConstraints().add(labels);
        grid.setAlignment(Pos.BASELINE_LEFT);
        
        // for debugging
        //this.setGridLinesVisible(true);
        
        Label lblName = new Label(Translations.get("bible.properties.name"));
        Label lblNameValue = new Label();
        lblNameValue.textProperty().bind(name);
        lblNameValue.setTooltip(new Tooltip());
        lblNameValue.getTooltip().textProperty().bind(name);
        lblNameValue.setBorder(VALUE_BORDER);
        grid.add(lblName, 0, 0, 1, 1);
        grid.add(lblNameValue, 1, 0, 1, 1);
        
        Label lblLanguage = new Label(Translations.get("bible.properties.language"));
        Label lblLanguageValue = new Label();
        lblLanguageValue.textProperty().bind(language);
        lblLanguageValue.setTooltip(new Tooltip());
        lblLanguageValue.getTooltip().textProperty().bind(language);
        lblLanguageValue.setBorder(VALUE_BORDER);
        grid.add(lblLanguage, 0, 1, 1, 1);
        grid.add(lblLanguageValue, 1, 1, 1, 1);
        
        Label lblSource = new Label(Translations.get("bible.properties.source"));
        Label lblSourceValue = new Label();
        lblSourceValue.textProperty().bind(source);
        lblSourceValue.setTooltip(new Tooltip());
        lblSourceValue.getTooltip().textProperty().bind(source);
        lblSourceValue.setBorder(VALUE_BORDER);
        grid.add(lblSource, 0, 2, 1, 1);
        grid.add(lblSourceValue, 1, 2, 1, 1);
        
        Label lblImportDate = new Label(Translations.get("bible.properties.importDate"));
        Label lblImportDateValue = new Label();
        lblImportDateValue.textProperty().bind(importDate);
        lblImportDateValue.setTooltip(new Tooltip());
        lblImportDateValue.getTooltip().textProperty().bind(importDate);
        lblImportDateValue.setBorder(VALUE_BORDER);
        grid.add(lblImportDate, 0, 3, 1, 1);
        grid.add(lblImportDateValue, 1, 3, 1, 1);
        
        Label lblCopyright = new Label(Translations.get("bible.properties.copyright"));
        Label lblCopyrightValue = new Label();
        lblCopyrightValue.textProperty().bind(copyright);
        lblCopyrightValue.setTooltip(new Tooltip());
        lblCopyrightValue.getTooltip().textProperty().bind(copyright);
        lblCopyrightValue.setBorder(VALUE_BORDER);
        grid.add(lblCopyright, 0, 4, 1, 1);
        grid.add(lblCopyrightValue, 1, 4, 1, 1);
        
        Label lblVerseCount = new Label(Translations.get("bible.properties.verseCount"));
        Label lblVerseCountValue = new Label();
        lblVerseCountValue.textProperty().bind(verseCount);
        lblVerseCountValue.setTooltip(new Tooltip());
        lblVerseCountValue.getTooltip().textProperty().bind(verseCount);
        lblVerseCountValue.setBorder(VALUE_BORDER);
        grid.add(lblVerseCount, 0, 5, 1, 1);
        grid.add(lblVerseCountValue, 1, 5, 1, 1);
        
        Label lblHasApocrypha = new Label(Translations.get("bible.properties.apocrypha"));
        Label lblHasApocryphaValue = new Label();
        lblHasApocryphaValue.textProperty().bind(hasApocrypha);
        lblHasApocryphaValue.setTooltip(new Tooltip());
        lblHasApocryphaValue.getTooltip().setText(Translations.get("bible.properties.apocrypha.tooltip"));
        lblHasApocryphaValue.setBorder(VALUE_BORDER);
        grid.add(lblHasApocrypha, 0, 6, 1, 1);
        grid.add(lblHasApocryphaValue, 1, 6, 1, 1);
        
        Label lblImportErrors = new Label(Translations.get("bible.properties.importWarnings"));
        Label lblImportErrorsValue = new Label();
        lblImportErrorsValue.textProperty().bind(hadImportErrors);
        lblImportErrorsValue.setTooltip(new Tooltip());
        lblImportErrorsValue.getTooltip().setText(Translations.get("bible.properties.importWarnings.tooltip"));
        lblImportErrorsValue.setBorder(VALUE_BORDER);
        grid.add(lblImportErrors, 0, 7, 1, 1);
        grid.add(lblImportErrorsValue, 1, 7, 1, 1);
        
        // handle when the media is changed
        this.bible.addListener(new ChangeListener<BibleListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends BibleListItem> ob, BibleListItem oldValue, BibleListItem newValue) {
        		BibleListItem item = newValue;
        		
        		if (item == null || !item.loaded) {
        			name.set("");
        			language.set("");
        			source.set("");
        	        importDate.set("");
        	        copyright.set("");
        	        verseCount.set("");
        	        hasApocrypha.set("");
        	        hadImportErrors.set("");
        			setDisable(true);
        		} else {
        			setDisable(false);
        			String yes = Translations.get("yes");
        			String no = Translations.get("no");
        			String unknown = Translations.get("unknown");
        			
        			name.set(item.bible.getName());
        			language.set(item.bible.getLanguage());
        			source.set(item.bible.getSource());
        	        importDate.set(DATE_FORMATTER.format(item.bible.getImportDate()));
        			String copy = item.bible.getCopyright();
        	        copyright.set(copy != null && copy.length() > 0 ? copy : unknown);
        	        verseCount.set(String.valueOf(item.bible.getVerseCount()));
        	        hasApocrypha.set(item.bible.hasApocrypha() ? yes : no);
        	        hadImportErrors.set(item.bible.hadImportWarning() ? yes : no);
        		}
        	}
		});
        
        this.getChildren().addAll(grid);
	}
	
	/**
	 * Returns the current bible.
	 * @return {@link BibleListItem} or null
	 */
	public BibleListItem getBible() {
		return this.bible.get();
	}
	
	/**
	 * Sets the current bible.
	 * @param bible the bible
	 */
	public void setBible(BibleListItem bible) {
		this.bible.set(bible);
	}
	
	/**
	 * Returns the current bible property.
	 * @return ObjectProperty&lt;{@link BibleListItem}&gt;
	 */
	public ObjectProperty<BibleListItem> bibleProperty() {
		return this.bible;
	}
}
