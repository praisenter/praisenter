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

import org.praisenter.Tag;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.themes.Styles;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.Slide;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Pane specifically for display of {@link Slide}s.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlidePropertiesPane extends VBox {
	/** The date formatter */
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withZone(ZoneId.systemDefault());
	
	// properties
	
	/** The slide */
	private final ObjectProperty<SlideListItem> slide = new SimpleObjectProperty<SlideListItem>();
	
	// the sub properties
	
	/** The name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The language */
	private final StringProperty time = new SimpleStringProperty();
	
	/** The source */
	private final StringProperty totalTime = new SimpleStringProperty();
	
	/** The last modified date */
	private final StringProperty updatedDate = new SimpleStringProperty();
	
	/** The create date */
	private final StringProperty createDate = new SimpleStringProperty();

	// nodes
	
	/** The tag view for editing tags */
	private final TagListView tagView;
	
	/**
	 * Creates a new metadata pane.
	 * @param allTags the observable list of all tags
	 */
	public SlidePropertiesPane(ObservableSet<Tag> allTags) {
		this.getStyleClass().add(Styles.SLIDE_INFO_PANE);
		
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
        Label lblNameValue = new Label();
        lblNameValue.textProperty().bind(name);
        lblNameValue.setTooltip(new Tooltip());
        lblNameValue.getTooltip().textProperty().bind(name);
        lblNameValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblName, 0, 0, 1, 1);
        grid.add(lblNameValue, 1, 0, 1, 1);
        
        Label lblLanguage = new Label(Translations.get("slide.properties.time"));
        Label lblLanguageValue = new Label();
        lblLanguageValue.textProperty().bind(time);
        lblLanguageValue.setTooltip(new Tooltip());
        lblLanguageValue.getTooltip().textProperty().bind(time);
        lblLanguageValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblLanguage, 0, 1, 1, 1);
        grid.add(lblLanguageValue, 1, 1, 1, 1);
        
        Label lblSource = new Label(Translations.get("slide.properties.totalTime"));
        Label lblSourceValue = new Label();
        lblSourceValue.textProperty().bind(totalTime);
        lblSourceValue.setTooltip(new Tooltip());
        lblSourceValue.getTooltip().textProperty().bind(totalTime);
        lblSourceValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblSource, 0, 2, 1, 1);
        grid.add(lblSourceValue, 1, 2, 1, 1);
        
        Label lblImportDate = new Label(Translations.get("slide.properties.createDate"));
        Label lblImportDateValue = new Label();
        lblImportDateValue.textProperty().bind(createDate);
        lblImportDateValue.setTooltip(new Tooltip());
        lblImportDateValue.getTooltip().textProperty().bind(createDate);
        lblImportDateValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblImportDate, 0, 3, 1, 1);
        grid.add(lblImportDateValue, 1, 3, 1, 1);
        
        Label lblUpdatedDate = new Label(Translations.get("slide.properties.updatedDate"));
        Label lblUpdatedDateValue = new Label();
        lblUpdatedDateValue.textProperty().bind(updatedDate);
        lblUpdatedDateValue.setTooltip(new Tooltip());
        lblUpdatedDateValue.getTooltip().textProperty().bind(updatedDate);
        lblUpdatedDateValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblUpdatedDate, 0, 4, 1, 1);
        grid.add(lblUpdatedDateValue, 1, 4, 1, 1);
        
        this.tagView = new TagListView(allTags);
        // handle when an action is perfomed on the tag view
        this.tagView.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				SlideListItem slide = SlidePropertiesPane.this.slide.get();
				Tag tag = event.getTag();
				// bubble up the event
				if (event.getEventType() == TagEvent.ADDED) {
					fireEvent(new SlideTagEvent(tagView, SlidePropertiesPane.this, SlideMetadataEvent.ADD_TAG, slide, tag));
				} else if (event.getEventType() == TagEvent.REMOVED) {
					fireEvent(new SlideTagEvent(tagView, SlidePropertiesPane.this, SlideMetadataEvent.REMOVE_TAG, slide, tag));
				}
			}
        });
        
        // handle when the slide is changed
        this.slide.addListener(new ChangeListener<SlideListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends SlideListItem> ob, SlideListItem oldValue, SlideListItem newValue) {
        		SlideListItem item = newValue;
        		
        		tagView.setText(null);
        		
        		if (item == null || !item.isLoaded()) {
        			name.set("");
        			time.set("");
        			totalTime.set("");
        	        updatedDate.set("");
        	        createDate.set("");
        	        tagView.tagsProperty().set(null);
        			setDisable(true);
        		} else {
        			setDisable(false);
        			Slide slide = item.getSlide();
        			
        			name.set(slide.getName());
        			time.set(slide.getTime() == Slide.TIME_FOREVER ? "" : String.valueOf(slide.getTime()));
        			long total = slide.getTotalTime();
        			totalTime.set(total == Slide.TIME_FOREVER ? "" : String.valueOf(total));
        	        updatedDate.set(slide.getLastModifiedDate() != null ? DATETIME_FORMATTER.format(slide.getLastModifiedDate()) : null);
        	        createDate.set(slide.getCreatedDate() != null ? DATETIME_FORMATTER.format(slide.getCreatedDate()) : null);
        	        
        	        tagView.tagsProperty().set(item.getTags());
        		}
        	}
		});
        
        this.getChildren().addAll(grid, tagView);
	}
	
	/**
	 * Returns the current slide.
	 * @return {@link SlideListItem} or null
	 */
	public SlideListItem getSlide() {
		return this.slide.get();
	}
	
	/**
	 * Sets the current slide.
	 * @param slide the slide
	 */
	public void setSlide(SlideListItem slide) {
		this.slide.set(slide);
	}
	
	/**
	 * Returns the current slide property.
	 * @return ObjectProperty&lt;{@link SlideListItem}&gt;
	 */
	public ObjectProperty<SlideListItem> slideProperty() {
		return this.slide;
	}
}
