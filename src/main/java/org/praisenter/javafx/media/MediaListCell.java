package org.praisenter.javafx.media;

import org.praisenter.javafx.FlowListCell;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public final class MediaListCell extends FlowListCell<MediaListItem> {
	private final ObjectProperty<Media> media = new SimpleObjectProperty<Media>(null);
	
	public MediaListCell(DefaultThumbnails thumbs, MediaListItem item, int maxHeight) {
		super(item);
		
		this.setPrefWidth(110);
		this.setAlignment(Pos.TOP_CENTER);
		
    	final ImageView thumb = new ImageView();
    	// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(thumb);
    	wrapper.setAlignment(Pos.BOTTOM_CENTER);
    	wrapper.setPrefHeight(maxHeight);
    	wrapper.setMaxHeight(maxHeight);
    	wrapper.setMinHeight(maxHeight);
    	wrapper.managedProperty().bind(wrapper.visibleProperty());
    	this.getChildren().add(wrapper);

		// setup an indeterminant progress bar
		ProgressIndicator progress = new ProgressIndicator();
		progress.managedProperty().bind(progress.visibleProperty());
		this.getChildren().add(progress);
		
		this.media.addListener((obs, ov, nv) -> {
			// setup the thumbnail image
			Image image = null;
			if (nv != null) {
				if (nv.getThumbnail() == null) {
					if (nv.getType() == MediaType.IMAGE) {
						image = thumbs.getDefaultImageThumbnail();
					} else if (nv.getType() == MediaType.VIDEO) {
						image = thumbs.getDefaultVideoThumbnail();
					} else if (nv.getType() == MediaType.AUDIO) {
						image = thumbs.getDefaultAudioThumbnail();
					}
				} else {
					image = SwingFXUtils.toFXImage(nv.getThumbnail(), null);
				}
			}
			
			thumb.setImage(image);
			
			if (nv != null && nv.getType() == MediaType.IMAGE) {
	    		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
	    	} else {
	    		thumb.setEffect(null);
	    	}
		});
		
		wrapper.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
		this.media.bind(item.mediaProperty());
		
    	// setup the media name label
    	final Label label = new Label();
    	label.textProperty().bind(item.nameProperty());
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
