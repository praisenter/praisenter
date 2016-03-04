package org.praisenter.javafx.media;

import org.praisenter.javafx.FlowListItemView;
import org.praisenter.media.MediaType;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

final class MediaListViewCellFactory implements Callback<MediaListItem, FlowListItemView<MediaListItem>> {
	@Override
	public FlowListItemView<MediaListItem> call(MediaListItem item) {
		FlowListItemView<MediaListItem> cell = new FlowListItemView<MediaListItem>(item);
		
		cell.setPrefSize(130, 130);
		
		String name = null;
		
		if (item.loaded) {
			name = item.name;
	    	// setup the thumbnail image
	    	final ImageView thumb = new ImageView(SwingFXUtils.toFXImage(item.media.getThumbnail(), null));
	    	// only show a drop shadow effect on images that aren't using the default thumbnail
	    	if (item.media.getMetadata().getType() == MediaType.IMAGE) {
	    		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
	    	}
	    	cell.getChildren().add(thumb);
		} else {
			name = item.name;
			// setup an indeterminant progress bar
			ProgressIndicator progress = new ProgressIndicator();
			cell.getChildren().add(progress);
		}
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.setText(name);
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	cell.getChildren().addAll(label);
    	
		return cell;
	}
}
