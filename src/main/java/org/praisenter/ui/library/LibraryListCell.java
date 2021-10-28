package org.praisenter.ui.library;

import org.praisenter.data.Persistable;
import org.praisenter.data.bible.ReadOnlyBible;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.media.ReadOnlyMedia;
import org.praisenter.data.slide.ReadOnlySlide;
import org.praisenter.data.song.ReadOnlySong;
import org.praisenter.ui.controls.FlowListCell;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

final class LibraryListCell extends FlowListCell<Persistable> {
	private static final String LIBRARY_LIST_CELL_CSS = "p-library-list-cell";
	private static final String LIBRARY_LIST_CELL_GRAPHIC_CSS = "p-library-list-cell-graphic";
	private static final String LIBRARY_LIST_CELL_THUMBNAIL_CSS = "p-library-list-cell-thumbnail";
	private static final String LIBRARY_LIST_CELL_IMAGE_CSS = "p-library-list-cell-image";
	private static final String LIBRARY_LIST_CELL_AUDIO_CSS = "p-library-list-cell-audio";
	private static final String LIBRARY_LIST_CELL_VIDEO_CSS = "p-library-list-cell-video";
	private static final String LIBRARY_LIST_CELL_BIBLE_CSS = "p-library-list-cell-bible";
	private static final String LIBRARY_LIST_CELL_SONG_CSS = "p-library-list-cell-song";
	private static final String LIBRARY_LIST_CELL_SLIDE_CSS = "p-library-list-cell-slide";
	private static final String LIBRARY_LIST_CELL_LABEL_CSS = "p-library-list-cell-label";
	
	public LibraryListCell(Persistable data) {
		super(data);
		
		this.getStyleClass().add(LIBRARY_LIST_CELL_CSS);
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView();
    	final VBox underlay = new VBox(thumb);
    	final VBox graphic = new VBox(underlay);
    	final Label label = new Label();
    	
    	thumb.getStyleClass().add(LIBRARY_LIST_CELL_THUMBNAIL_CSS);
    	graphic.getStyleClass().add(LIBRARY_LIST_CELL_GRAPHIC_CSS);
    	label.getStyleClass().add(LIBRARY_LIST_CELL_LABEL_CSS);
    	
    	thumb.setPreserveRatio(true);
    	underlay.maxWidthProperty().bind(thumb.fitWidthProperty());
    	underlay.maxHeightProperty().bind(thumb.fitHeightProperty());
    	
    	if (data instanceof ReadOnlyMedia) {
    		final ReadOnlyMedia media = (ReadOnlyMedia)data;
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(media.getMediaThumbnailPath().toUri().toURL().toExternalForm());
    		}, media.mediaThumbnailPathProperty()));
    		if (media.getMediaType() != MediaType.VIDEO) underlay.getStyleClass().add(LIBRARY_LIST_CELL_VIDEO_CSS);
    		else if (media.getMediaType() != MediaType.AUDIO) underlay.getStyleClass().add(LIBRARY_LIST_CELL_AUDIO_CSS);
    		else if (media.getMediaType() != MediaType.IMAGE) underlay.getStyleClass().add(LIBRARY_LIST_CELL_IMAGE_CSS);
//    		if (media.getMediaType() != MediaType.VIDEO) {
//    			underlay.getStyleClass().add("dropshadow-underlay");
//    		}
    		label.textProperty().bind(media.nameProperty());
    	} else if (data instanceof ReadOnlySlide) {
    		final ReadOnlySlide slide = (ReadOnlySlide)data;
    		underlay.getStyleClass().add(LIBRARY_LIST_CELL_SLIDE_CSS);
//			underlay.getStyleClass().addAll("transparent-underlay", "dropshadow-underlay");
    		thumb.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(slide.getThumbnailPath().toUri().toURL().toExternalForm());
    		}, slide.thumbnailPathProperty()));
    		label.textProperty().bind(slide.nameProperty());
    	} else if (data instanceof ReadOnlyBible) {
    		final ReadOnlyBible bible = (ReadOnlyBible)data;
//    		underlay.getStyleClass().add("dropshadow-underlay");
    		underlay.getStyleClass().add(LIBRARY_LIST_CELL_BIBLE_CSS);
//    		thumb.getStyleClass().add("bible");
    		label.textProperty().bind(bible.nameProperty());
    	} else if (data instanceof ReadOnlySong) {
    		final ReadOnlySong song = (ReadOnlySong)data;
    		underlay.getStyleClass().add(LIBRARY_LIST_CELL_SONG_CSS);
//    		underlay.getStyleClass().add("dropshadow-underlay");
//    		thumb.getStyleClass().add("song");
    		label.textProperty().bind(song.nameProperty());
    	} else {
    		// TODO log a warning
    	}
    	
    	// add the image and label to the cell
    	this.getChildren().addAll(graphic, label);
	}
	
}
