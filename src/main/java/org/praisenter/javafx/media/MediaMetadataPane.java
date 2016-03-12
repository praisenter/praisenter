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

import java.text.MessageFormat;
import java.util.Optional;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagView;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaMetadata;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.Formatter;

/**
 * Pane specifically for display and edit of {@link Media}.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaMetadataPane extends VBox {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The value used for non-applicable fields (like the length for an image) */
	private static final String NOT_APPLICABLE = "";
	
	/** A simple bottom border for displaying on labels */
	private static final Border VALUE_BORDER = new Border(new BorderStroke(Color.color(0.7, 0.7, 0.7), BorderStrokeStyle.DASHED, null, new BorderWidths(0, 0, 1, 0)));
	
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
	
	// nodes
	
	/** The tag view for editing tags */
	private final TagView tagView;
	
	/**
	 * Creates a new metadata pane.
	 * @param library the media library
	 * @param allTags the set of all tags
	 */
	public MediaMetadataPane(MediaLibrary library, ObservableSet<Tag> allTags) {
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
        
        Label lblName = new Label(Translations.get("media.metadata.name"));
        Label lblNameValue = new Label();
        lblNameValue.textProperty().bind(name);
        lblNameValue.setTooltip(new Tooltip());
        lblNameValue.getTooltip().textProperty().bind(name);
        lblNameValue.setBorder(VALUE_BORDER);
        Hyperlink btnRename = new Hyperlink(Translations.get("media.metadata.rename"));
        btnRename.setTooltip(new Tooltip(Translations.get("media.metadata.rename")));
        btnRename.setVisible(false);
        btnRename.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        HBox nameRow = new HBox();
        nameRow.setAlignment(Pos.BASELINE_LEFT);
        nameRow.getChildren().addAll(lblNameValue, btnRename);
        grid.add(lblName, 0, 0, 1, 1);
        grid.add(nameRow, 1, 0, 1, 1);
        
        btnRename.setOnAction((e) -> {
	    	TextInputDialog prompt = new TextInputDialog(name.get());
	    	prompt.setTitle(Translations.get("media.metadata.rename.title"));
	    	prompt.setHeaderText(Translations.get("media.metadata.rename.header"));
	    	prompt.setContentText(Translations.get("media.metadata.rename.content"));
	    	Optional<String> result = prompt.showAndWait();
	    	// check for the "OK" button
	    	if (result.isPresent()) {
	    		// update the media's name
	    		try {
					Media nm = library.rename(media.get().media, result.get());
					fireEvent(new MediaRenamedEvent(btnRename, MediaMetadataPane.this, media.get().media, nm));
				} catch (Exception ex) {
					// log the error
					LOGGER.error("Failed to rename media from '{}' to '{}': {}", name.get(), result.get(), ex.getMessage());
					// show an error to the user
					Alert alert = Alerts.exception(null, null, MessageFormat.format(Translations.get("media.metadata.rename.error"), name.get(), result.get()), ex);
					alert.show();
				}
	    	}
	    });
        
        Label lblWidth = new Label(Translations.get("media.metadata.width"));
        Label lblWidthValue = new Label();
        lblWidthValue.textProperty().bind(width);
        lblWidthValue.setTooltip(new Tooltip());
        lblWidthValue.getTooltip().textProperty().bind(width);
        lblWidthValue.setBorder(VALUE_BORDER);
        grid.add(lblWidth, 0, 1, 1, 1);
        grid.add(lblWidthValue, 1, 1, 1, 1);
        
        Label lblHeight = new Label(Translations.get("media.metadata.height"));
        Label lblHeightValue = new Label();
        lblHeightValue.textProperty().bind(height);
        lblHeightValue.setTooltip(new Tooltip());
        lblHeightValue.getTooltip().textProperty().bind(height);
        lblHeightValue.setBorder(VALUE_BORDER);
        grid.add(lblHeight, 0, 2, 1, 1);
        grid.add(lblHeightValue, 1, 2, 1, 1);
        
        Label lblLength = new Label(Translations.get("media.metadata.length"));
        Label lblLengthValue = new Label();
        lblLengthValue.textProperty().bind(length);
        lblLengthValue.setTooltip(new Tooltip());
        lblLengthValue.getTooltip().textProperty().bind(length);
        lblLengthValue.setBorder(VALUE_BORDER);
        grid.add(lblLength, 0, 3, 1, 1);
        grid.add(lblLengthValue, 1, 3, 1, 1);
        
        Label lblSound = new Label(Translations.get("media.metadata.sound"));
        Label lblSoundValue = new Label();
        lblSoundValue.textProperty().bind(audio);
        lblSoundValue.setTooltip(new Tooltip());
        lblSoundValue.getTooltip().textProperty().bind(audio);
        lblSoundValue.setBorder(VALUE_BORDER);
        grid.add(lblSound, 0, 4, 1, 1);
        grid.add(lblSoundValue, 1, 4, 1, 1);
        
        Label lblFormat = new Label(Translations.get("media.metadata.format"));
        Label lblFormatValue = new Label();
        lblFormatValue.textProperty().bind(format);
        lblFormatValue.setTooltip(new Tooltip());
        lblFormatValue.getTooltip().textProperty().bind(format);
        lblFormatValue.setBorder(VALUE_BORDER);
        grid.add(lblFormat, 0, 5, 1, 1);
        grid.add(lblFormatValue, 1, 5, 1, 1);
        
        this.tagView = new TagView(allTags);
        // handle when an action is perfomed on the tag view
        this.tagView.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				Media media = MediaMetadataPane.this.media.get().media;
				Tag tag = event.getTag();
				if (event.getEventType() == TagEvent.ADDED) {
					try {
						library.addTag(media, tag);
						allTags.add(tag);
					} catch (Exception e) {
						// remove it from the tags
						tagView.getTags().remove(tag);
						// log the error
						LOGGER.error("Failed to add tag '{}' for '{}': {}", tag.getName(), media.getMetadata().getPath().toAbsolutePath().toString(), e.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(null, null, MessageFormat.format(Translations.get("tags.add.error"), tag.getName()), e);
						alert.show();
					}
				} else if (event.getEventType() == TagEvent.REMOVED) {
					try {
						library.removeTag(media, tag);
					} catch (Exception e) {
						// add it back
						tagView.getTags().add(tag);
						// log the error
						LOGGER.error("Failed to remove tag '{}' for '{}': {}", tag.getName(), media.getMetadata().getPath().toAbsolutePath().toString(), e.getMessage());
						// show an error to the user
						Alert alert = Alerts.exception(null, null, MessageFormat.format(Translations.get("tags.remove.error"), tag.getName()), e);
						alert.show();
					}
				}
			}
        });
        
        // handle when the media is changed
        this.media.addListener(new ChangeListener<MediaListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends MediaListItem> ob, MediaListItem oldValue, MediaListItem newValue) {
        		MediaListItem item = newValue;
        		
//        		tagView.setValue(null);
        		tagView.setText(null);
        		
        		if (item == null || !item.loaded) {
        			name.set("");
        			btnRename.setVisible(false);
        			width.set("");
        	        height.set("");
        	        length.set("");
        	        audio.set("");
        	        format.set("");
        	        tagView.getTags().clear();
        			setDisable(true);
        		} else {
        			setDisable(false);
        			btnRename.setVisible(true);
        			Media media = item.media;
        			MediaType type = media.getMetadata().getType();
        			String unknown = Translations.get("media.metadata.unknown");
        			
        			name.set(media.getMetadata().getName());
        			
        			// width/height
        			if (type == MediaType.IMAGE || type == MediaType.VIDEO) {
        				int w = media.getMetadata().getWidth();
        				int h = media.getMetadata().getHeight();
        				width.set(w == MediaMetadata.UNKNOWN ? unknown : String.valueOf(w));
        				height.set(h == MediaMetadata.UNKNOWN ? unknown : String.valueOf(h));
        			} else {
        				width.set(NOT_APPLICABLE);
        				height.set(NOT_APPLICABLE);
        			}
        			
        			// length
        			if (type == MediaType.AUDIO || type == MediaType.VIDEO) {
        				long l = media.getMetadata().getLength();
        				length.set(l == MediaMetadata.UNKNOWN ? unknown : Formatter.getLengthFormattedString(l));
        			} else {
        				length.set(NOT_APPLICABLE);
        			}
        			
        			// has sound?
        			if (media.getMetadata().getType() == MediaType.VIDEO) {
        				audio.set(media.getMetadata().hasAudio() ? Translations.get("yes") : Translations.get("no"));
        			} else {
        				audio.set(NOT_APPLICABLE);
        			}
        			
        			format.set(media.getMetadata().getFormat().toString());
        			
        			tagView.getTags().addAll(media.getMetadata().getTags());
        	        tagView.getTags().retainAll(media.getMetadata().getTags());
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
