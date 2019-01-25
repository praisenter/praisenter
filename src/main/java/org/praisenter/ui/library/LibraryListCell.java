package org.praisenter.ui.library;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.ReadOnlyBible;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.media.ReadOnlyMedia;
import org.praisenter.data.slide.ReadOnlySlide;
import org.praisenter.data.slide.ReadOnlySlideShow;
import org.praisenter.ui.controls.FlowListCell;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

final class LibraryListCell extends FlowListCell<Persistable> {
	public LibraryListCell(Persistable data) {
		super(data);
		
		this.getStyleClass().add("p-library-list-cell");
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView();
    	final VBox underlay = new VBox(thumb);
    	final VBox wrapper = new VBox(underlay);
    	final Label label = new Label();
    	
    	thumb.getStyleClass().addAll("thumb");
    	underlay.getStyleClass().addAll("image-underlay");
    	wrapper.getStyleClass().addAll("image-wrapper");
    	
    	thumb.setPreserveRatio(true);
    	underlay.maxWidthProperty().bind(thumb.fitWidthProperty());
    	underlay.maxHeightProperty().bind(thumb.fitHeightProperty());
    	
    	if (data instanceof ReadOnlyMedia) {
    		final ReadOnlyMedia media = (ReadOnlyMedia)data;
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(media.getMediaThumbnailPath().toUri().toURL().toExternalForm());
    		}, media.mediaThumbnailPathProperty()));
    		if (media.getMediaType() != MediaType.VIDEO) {
    			underlay.getStyleClass().add("dropshadow-underlay");
    		}
    		label.textProperty().bind(media.nameProperty());
    	} else if (data instanceof ReadOnlySlide) {
    		final ReadOnlySlide slide = (ReadOnlySlide)data;
			underlay.getStyleClass().addAll("transparent-underlay", "dropshadow-underlay");
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(slide.getThumbnailPath().toUri().toURL().toExternalForm());
    		}, slide.thumbnailPathProperty()));
    		label.textProperty().bind(slide.nameProperty());
    	} else if (data instanceof ReadOnlySlideShow) {
    		final ReadOnlySlideShow show = (ReadOnlySlideShow)data;
    		// TODO we would need to load a stacked version of the first 3 slides in the show
    		label.textProperty().bind(show.nameProperty());
    	} else if (data instanceof ReadOnlyBible) {
    		final ReadOnlyBible bible = (ReadOnlyBible)data;
    		underlay.getStyleClass().add("dropshadow-underlay");
    		thumb.getStyleClass().add("bible");
    		label.textProperty().bind(bible.nameProperty());
    	}
    	
    	// TODO cases for songs and anything else
    	
    	// add the image and label to the cell
    	this.getChildren().addAll(wrapper, label);
	}
	
}
