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
package org.praisenter.javafx.slide;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.controls.ValueLabel;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideShow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Pane specifically for display of {@link SlideShow}s.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideShowPropertiesPane extends VBox {
	/** The date formatter */
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withZone(ZoneId.systemDefault());
	
	// properties
	
	/** The slide */
	private final ObjectProperty<SlideShowListItem> show = new SimpleObjectProperty<SlideShowListItem>();
	
	// the sub properties
	
	/** The name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The time */
	private final StringProperty time = new SimpleStringProperty();
	
	/** The last modified date */
	private final StringProperty updatedDate = new SimpleStringProperty();
	
	/** The create date */
	private final StringProperty createDate = new SimpleStringProperty();

	/**
	 * Creates a new metadata pane.
	 * @param context the context
	 */
	public SlideShowPropertiesPane(PraisenterContext context) {
		this.getStyleClass().add("slide-show-properties-pane");
		
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
        
        Label lblName = new Label(Translations.get("slide.properties.name"));
        Label lblNameValue = new ValueLabel();
        lblNameValue.textProperty().bind(name);
        lblNameValue.getTooltip().textProperty().bind(name);
        grid.add(lblName, 0, 0, 1, 1);
        grid.add(lblNameValue, 1, 0, 1, 1);
        
        Label lblLanguage = new Label(Translations.get("slide.properties.time"));
        Label lblLanguageValue = new ValueLabel();
        lblLanguageValue.textProperty().bind(time);
        lblLanguageValue.getTooltip().textProperty().bind(time);
        grid.add(lblLanguage, 0, 1, 1, 1);
        grid.add(lblLanguageValue, 1, 1, 1, 1);
        
        Label lblImportDate = new Label(Translations.get("slide.properties.createDate"));
        Label lblImportDateValue = new ValueLabel();
        lblImportDateValue.textProperty().bind(createDate);
        lblImportDateValue.getTooltip().textProperty().bind(createDate);
        grid.add(lblImportDate, 0, 2, 1, 1);
        grid.add(lblImportDateValue, 1, 2, 1, 1);
        
        Label lblUpdatedDate = new Label(Translations.get("slide.properties.updatedDate"));
        Label lblUpdatedDateValue = new ValueLabel();
        lblUpdatedDateValue.textProperty().bind(updatedDate);
        lblUpdatedDateValue.getTooltip().textProperty().bind(updatedDate);
        grid.add(lblUpdatedDate, 0, 3, 1, 1);
        grid.add(lblUpdatedDateValue, 1, 3, 1, 1);
        
        // handle when the slide is changed
        this.show.addListener(new ChangeListener<SlideShowListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends SlideShowListItem> ob, SlideShowListItem oldValue, SlideShowListItem newValue) {
        		SlideShowListItem item = newValue;
        		
        		if (item == null || !item.isLoaded()) {
        			name.set("");
        			time.set("");
        	        updatedDate.set("");
        	        createDate.set("");
        			setDisable(true);
        		} else {
        			setDisable(false);
        			SlideShow show = item.getSlideShow();
        			
        			// get the total time
        			ObservableSlideLibrary library = context.getSlideLibrary();
        			long totalTime = 0;
        			for (int i = 0; i < show.getSlides().size(); i++) {
        				Slide slide = library.getSlide(show.getSlides().get(i).getSlideId());
        				if (slide != null) {
        					long tt = slide.getTotalTime();
        					if (tt == Slide.TIME_FOREVER) {
        						totalTime = tt;
        						break;
        					} else {
        						totalTime += tt;
        					}
        				}
        			}
        			
        			name.set(show.getName());
        			time.set(totalTime == Slide.TIME_FOREVER ? "" : String.valueOf(totalTime));
        	        updatedDate.set(show.getLastModifiedDate() != null ? DATETIME_FORMATTER.format(show.getLastModifiedDate()) : null);
        	        createDate.set(show.getCreatedDate() != null ? DATETIME_FORMATTER.format(show.getCreatedDate()) : null);
        		}
        	}
		});
        
        this.getChildren().addAll(grid);
	}
	
	/**
	 * Returns the current slide show.
	 * @return {@link SlideShowListItem} or null
	 */
	public SlideShowListItem getSlideShow() {
		return this.show.get();
	}
	
	/**
	 * Sets the current slide show.
	 * @param show the slide show
	 */
	public void setSlideShow(SlideShowListItem show) {
		this.show.set(show);
	}
	
	/**
	 * Returns the current slide show property.
	 * @return ObjectProperty&lt;{@link SlideShowListItem}&gt;
	 */
	public ObjectProperty<SlideShowListItem> slideShowProperty() {
		return this.show;
	}
}
