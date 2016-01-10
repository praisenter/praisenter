package org.praisenter.javafx.media;

import java.io.IOException;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.xml.bind.JAXBException;

import org.praisenter.Tag;
import org.praisenter.javafx.TagView;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaMetadata;
import org.praisenter.media.MediaType;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.Formatter;

final class MediaMetadataView extends GridPane {
	private static final String NOT_APPLICABLE = "-";
	
	private final ObjectProperty<MediaListItem> media = new SimpleObjectProperty<MediaListItem>();
	
	private final StringProperty width = new SimpleStringProperty();
	private final StringProperty height = new SimpleStringProperty();
	private final StringProperty length = new SimpleStringProperty();
	private final StringProperty audio = new SimpleStringProperty();
	private final StringProperty format = new SimpleStringProperty();
	
	private final TagView tagView;
	
	public MediaMetadataView(MediaLibrary library, ObservableSet<Tag> allTags) {
		this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(5));
        this.setDisable(true);
        
        ColumnConstraints labels = new ColumnConstraints();
        labels.setHgrow(Priority.NEVER);
        labels.setMinWidth(ConstraintsBase.CONSTRAIN_TO_PREF);
        this.getColumnConstraints().add(labels);
        
        // for debugging
        //this.setGridLinesVisible(true);
        
        Label lblWidth = new Label(Translations.getTranslation("media.metadata.width"));
        Label lblWidthValue = new Label();
        lblWidthValue.textProperty().bind(width);
        lblWidthValue.setTooltip(new Tooltip());
        lblWidthValue.getTooltip().textProperty().bind(width);
        this.add(lblWidth, 0, 0, 1, 1);
        this.add(lblWidthValue, 1, 0, 1, 1);
        
        Label lblHeight = new Label(Translations.getTranslation("media.metadata.height"));
        Label lblHeightValue = new Label();
        lblHeightValue.textProperty().bind(height);
        lblHeightValue.setTooltip(new Tooltip());
        lblHeightValue.getTooltip().textProperty().bind(height);
        this.add(lblHeight, 0, 1, 1, 1);
        this.add(lblHeightValue, 1, 1, 1, 1);
        
        Label lblLength = new Label(Translations.getTranslation("media.metadata.length"));
        Label lblLengthValue = new Label();
        lblLengthValue.textProperty().bind(length);
        lblLengthValue.setTooltip(new Tooltip());
        lblLengthValue.getTooltip().textProperty().bind(length);
        this.add(lblLength, 0, 2, 1, 1);
        this.add(lblLengthValue, 1, 2, 1, 1);
        
        Label lblSound = new Label(Translations.getTranslation("media.metadata.sound"));
        Label lblSoundValue = new Label();
        lblSoundValue.textProperty().bind(audio);
        lblSoundValue.setTooltip(new Tooltip());
        lblSoundValue.getTooltip().textProperty().bind(audio);
        this.add(lblSound, 0, 3, 1, 1);
        this.add(lblSoundValue, 1, 3, 1, 1);
        
        Label lblFormat = new Label(Translations.getTranslation("media.metadata.format"));
        Label lblFormatValue = new Label();
        lblFormatValue.textProperty().bind(format);
        lblFormatValue.setTooltip(new Tooltip());
        lblFormatValue.getTooltip().textProperty().bind(format);
        this.add(lblFormat, 0, 4, 1, 1);
        this.add(lblFormatValue, 1, 4, 1, 1);
        
        this.tagView = new TagView(allTags);
        // handle when an action is perfomed on the tag view
        this.tagView.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Media media = MediaMetadataView.this.media.get().media;
				Set<Tag> tags = tagView.getTags();
				try {
					library.setTags(media, tags);
					allTags.addAll(tags);
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        this.add(tagView, 0, 5, 2, 1);
        
        // handle when the media is changed
        this.media.addListener(new ChangeListener<MediaListItem>() {
        	@Override
        	public void changed(ObservableValue<? extends MediaListItem> ob, MediaListItem oldValue, MediaListItem newValue) {
        		MediaListItem item = newValue;
        		
        		tagView.setText(null);
        		
        		if (item == null || !item.loaded) {
        			width.set("");
        	        height.set("");
        	        length.set("");
        	        audio.set("");
        	        format.set("");
        	        tagView.getTags().clear();
        			setDisable(true);
        		} else {
        			setDisable(false);
        			Media media = item.media;
        			MediaType type = media.getMetadata().getType();
        			String unknown = Translations.getTranslation("media.metadata.unknown");
        			
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
        				audio.set(media.getMetadata().hasAudio() ? Translations.getTranslation("yes") : Translations.getTranslation("no"));
        			} else {
        				audio.set(NOT_APPLICABLE);
        			}
        			
        			format.set(media.getMetadata().getFormat().toString());
        			
        			tagView.getTags().addAll(media.getMetadata().getTags());
        	        tagView.getTags().retainAll(media.getMetadata().getTags());
        		}
        	}
		});
	}
	
	public MediaListItem getMedia() {
		return this.media.get();
	}
	
	public void setMedia(MediaListItem media) {
		this.media.set(media);
	}
	
	public ObjectProperty<MediaListItem> mediaProperty() {
		return this.media;
	}
}
