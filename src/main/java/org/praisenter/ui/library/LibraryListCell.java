package org.praisenter.ui.library;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.ReadOnlyBible;
import org.praisenter.data.media.ReadOnlyMedia;
import org.praisenter.data.slide.ReadOnlySlide;
import org.praisenter.data.slide.ReadOnlySlideShow;
import org.praisenter.ui.controls.FlowListCell;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LibraryListCell extends FlowListCell<Persistable> {
	public LibraryListCell(Persistable data) {
		super(data);
		
		this.getStyleClass().add("library-list-cell");
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView();
    	final VBox wrapper = new VBox(thumb);
    	final Label label = new Label();
    	
    	wrapper.getStyleClass().add("image-wrapper");
    	
    	if (data instanceof ReadOnlyMedia) {
    		final ReadOnlyMedia media = (ReadOnlyMedia)data;
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(media.getMediaThumbnailPath().toString());
    		}, media.mediaThumbnailPathProperty()));
    		label.textProperty().bind(media.nameProperty());
    	} else if (data instanceof ReadOnlySlide) {
    		final ReadOnlySlide slide = (ReadOnlySlide)data;
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(slide.getThumbnailPath().toString());
    		}, slide.thumbnailPathProperty()));
    		label.textProperty().bind(slide.nameProperty());
    	} else if (data instanceof ReadOnlySlideShow) {
    		final ReadOnlySlideShow show = (ReadOnlySlideShow)data;
    		// TODO we would need to load a stacked version of the first 3 slides in the show
    		label.textProperty().bind(show.nameProperty());
    	} else if (data instanceof ReadOnlyBible) {
    		final ReadOnlyBible bible = (ReadOnlyBible)data;
    		thumb.getStyleClass().add("bible");
    		label.textProperty().bind(bible.nameProperty());
    	}
    	
    	// TODO cases for songs and anything else
    	
//    	thumb.getStyleClass().add("bible-list-cell-thumbnail");
//    	thumb.setFitHeight(100);
//    	thumb.setPreserveRatio(true);
		//thumb.managedProperty().bind(thumb.visibleProperty());
		
		// setup an indeterminant progress bar
//		ProgressIndicator progress = new ProgressIndicator();
//		progress.getStyleClass().add("bible-list-cell-progress");
//		progress.managedProperty().bind(progress.visibleProperty());
		
		// place it in a VBox for good positioning
//    	final VBox wrapper = new VBox(thumb);
    	
//		thumb.visibleProperty().bind(item.loadedProperty());
//		progress.visibleProperty().bind(item.loadedProperty().not());
    	
    	// setup the media name label
//    	final Label label = new Label();
//    	if (data instanceof ReadOnlyMedia) {
//    		label.textProperty().bind(((ReadOnlyMedia)data).nameProperty());
//    	} else if (data instanceof ReadOnlySlide) {
//    		label.textProperty().bind(((ReadOnlySlide)data).nameProperty());
//    	} else if (data instanceof ReadOnlySlideShow) {
//    		label.textProperty().bind(((ReadOnlySlideShow)data).nameProperty());
//    	} else if (data instanceof ReadOnlyBible) {
//    		label.textProperty().bind(((ReadOnlyBible)data).nameProperty());
//    	}
    	
    	// add the image and label to the cell
    	this.getChildren().addAll(wrapper, label);
	}
	
}
