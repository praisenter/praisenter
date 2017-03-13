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
package org.praisenter.javafx.media;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.Tag;
import org.praisenter.javafx.Styles;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.Formatter;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Pane specifically for display and edit of {@link Media}.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaInfoPane extends VBox {
	/** The value used for non-applicable fields (like the length for an image) */
	private static final String NOT_APPLICABLE = "";
	
	/** A formatter for instance fields */
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withZone(ZoneId.systemDefault());
	
	// properties
	
	/** The media */
	private final ObjectProperty<MediaListItem> media = new SimpleObjectProperty<MediaListItem>();
	
	// the sub properties
	
	/** The name */
	private final StringProperty name = new SimpleStringProperty();
	
	/** The width */
	private final StringProperty width = new SimpleStringProperty();
	
	/** The height */
	private final StringProperty height = new SimpleStringProperty();
	
	/** The length */
	private final StringProperty length = new SimpleStringProperty();
	
	/** Has audio */
	private final StringProperty audio = new SimpleStringProperty();
	
	/** The format */
	private final StringProperty format = new SimpleStringProperty();
	
	/** The date added */
	private final StringProperty dateAdded = new SimpleStringProperty();
	
	// nodes
	
	/** The tag view for editing tags */
	private final TagListView tagView;
	
	/**
	 * Creates a new media metadata pane.
	 * @param allTags the set of all tags
	 */
	public MediaInfoPane(ObservableSet<Tag> allTags) {
		this.getStyleClass().add(Styles.MEDIA_INFO_PANE);
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
        
        Label lblName = new Label(Translations.get("media.properties.name"));
        Label lblNameValue = new Label();
        lblNameValue.textProperty().bind(name);
        lblNameValue.setTooltip(new Tooltip());
        lblNameValue.getTooltip().textProperty().bind(name);
        lblNameValue.getStyleClass().add(Styles.VALUE_LABEL);
        HBox nameRow = new HBox();
        nameRow.setAlignment(Pos.BASELINE_LEFT);
        nameRow.getChildren().addAll(lblNameValue);
        grid.add(lblName, 0, 0, 1, 1);
        grid.add(nameRow, 1, 0, 1, 1);
        
        Label lblWidth = new Label(Translations.get("media.properties.width"));
        Label lblWidthValue = new Label();
        lblWidthValue.textProperty().bind(width);
        lblWidthValue.setTooltip(new Tooltip());
        lblWidthValue.getTooltip().textProperty().bind(width);
        lblWidthValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblWidth, 0, 1, 1, 1);
        grid.add(lblWidthValue, 1, 1, 1, 1);
        
        Label lblHeight = new Label(Translations.get("media.properties.height"));
        Label lblHeightValue = new Label();
        lblHeightValue.textProperty().bind(height);
        lblHeightValue.setTooltip(new Tooltip());
        lblHeightValue.getTooltip().textProperty().bind(height);
        lblHeightValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblHeight, 0, 2, 1, 1);
        grid.add(lblHeightValue, 1, 2, 1, 1);
        
        Label lblLength = new Label(Translations.get("media.properties.length"));
        Label lblLengthValue = new Label();
        lblLengthValue.textProperty().bind(length);
        lblLengthValue.setTooltip(new Tooltip());
        lblLengthValue.getTooltip().textProperty().bind(length);
        lblLengthValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblLength, 0, 3, 1, 1);
        grid.add(lblLengthValue, 1, 3, 1, 1);
        
        Label lblSound = new Label(Translations.get("media.properties.sound"));
        Label lblSoundValue = new Label();
        lblSoundValue.textProperty().bind(audio);
        lblSoundValue.setTooltip(new Tooltip());
        lblSoundValue.getTooltip().textProperty().bind(audio);
        lblSoundValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblSound, 0, 4, 1, 1);
        grid.add(lblSoundValue, 1, 4, 1, 1);
        
        Label lblFormat = new Label(Translations.get("media.properties.format"));
        Label lblFormatValue = new Label();
        lblFormatValue.textProperty().bind(format);
        lblFormatValue.setTooltip(new Tooltip());
        lblFormatValue.getTooltip().textProperty().bind(format);
        lblFormatValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblFormat, 0, 5, 1, 1);
        grid.add(lblFormatValue, 1, 5, 1, 1);
        
        Label lblDateAdded = new Label(Translations.get("media.properties.dateAdded"));
        Label lblDateAddedValue = new Label();
        lblDateAddedValue.textProperty().bind(dateAdded);
        lblDateAddedValue.setTooltip(new Tooltip());
        lblDateAddedValue.getTooltip().textProperty().bind(dateAdded);
        lblDateAddedValue.getStyleClass().add(Styles.VALUE_LABEL);
        grid.add(lblDateAdded, 0, 6, 1, 1);
        grid.add(lblDateAddedValue, 1, 6, 1, 1);
        
        this.tagView = new TagListView(allTags);
        // handle when an action is perfomed on the tag view
        this.tagView.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				MediaListItem media = MediaInfoPane.this.media.get();
				Tag tag = event.getTag();
				// bubble up the event
				if (event.getEventType() == TagEvent.ADDED) {
					fireEvent(new MediaTagEvent(tagView, MediaInfoPane.this, MediaMetadataEvent.ADD_TAG, media, tag));
				} else if (event.getEventType() == TagEvent.REMOVED) {
					fireEvent(new MediaTagEvent(tagView, MediaInfoPane.this, MediaMetadataEvent.REMOVE_TAG, media, tag));
				}
			}
        });
        
        // handle when the media is changed
        this.media.addListener(new ChangeListener<MediaListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends MediaListItem> ob, MediaListItem oldValue, MediaListItem newValue) {
        		MediaListItem item = newValue;
        		
        		tagView.setText(null);
        		
        		if (item == null || !item.isLoaded()) {
        			name.set("");
        			width.set("");
        	        height.set("");
        	        length.set("");
        	        audio.set("");
        	        format.set("");
        	        dateAdded.set("");
        	        tagView.tagsProperty().set(null);
        			setDisable(true);
        		} else {
        			setDisable(false);
        			Media media = item.getMedia();
        			MediaType type = media.getType();
        			String unknown = Translations.get("unknown");
        			
        			name.set(media.getName());
        			
        			// width/height
        			if (type == MediaType.IMAGE || type == MediaType.VIDEO) {
        				int w = media.getWidth();
        				int h = media.getHeight();
        				width.set(w == Media.UNKNOWN ? unknown : String.valueOf(w));
        				height.set(h == Media.UNKNOWN ? unknown : String.valueOf(h));
        			} else {
        				width.set(NOT_APPLICABLE);
        				height.set(NOT_APPLICABLE);
        			}
        			
        			// length
        			if (type == MediaType.AUDIO || type == MediaType.VIDEO) {
        				long l = media.getLength();
        				length.set(l == Media.UNKNOWN ? unknown : Formatter.getSecondsFormattedString(l));
        			} else {
        				length.set(NOT_APPLICABLE);
        			}
        			
        			// has sound?
        			if (media.getType() == MediaType.VIDEO) {
        				audio.set(media.hasAudio() ? Translations.get("yes") : Translations.get("no"));
        			} else if (media.getType() == MediaType.AUDIO) {
        				audio.set(Translations.get("yes"));
        			} else {
        				audio.set(NOT_APPLICABLE);
        			}
        			
        			format.set(media.getFormat().toString());
        			dateAdded.set(DATETIME_FORMATTER.format(media.getDateAdded()));
        			
        			tagView.tagsProperty().set(newValue.getTags());
        		}
        	}
		});
        
        this.getChildren().addAll(grid, tagView);
	}
	
	/**
	 * Returns the current media.
	 * @return {@link MediaListItem} or null
	 */
	public MediaListItem getMedia() {
		return this.media.get();
	}
	
	/**
	 * Sets the current media.
	 * @param media the media
	 */
	public void setMedia(MediaListItem media) {
		this.media.set(media);
	}
	
	/**
	 * Returns the current media property.
	 * @return ObjectProperty&lt;{@link MediaListItem}&gt;
	 */
	public ObjectProperty<MediaListItem> mediaProperty() {
		return this.media;
	}
}
