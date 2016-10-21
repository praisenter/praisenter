package org.praisenter.javafx.bible;

import org.praisenter.javafx.FlowListCell;

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

public final class BibleListCell extends FlowListCell<BibleListItem> {
	private static final Image ICON = new Image("/org/praisenter/resources/bible-icon.png");
	
	public BibleListCell(BibleListItem item) {
		super(item);
		
		this.setPrefWidth(110);
		this.setAlignment(Pos.TOP_CENTER);
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView(ICON);
    	
    	thumb.setFitHeight(100);
    	thumb.setPreserveRatio(true);
		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
		
		// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(thumb);
    	wrapper.setAlignment(Pos.BOTTOM_CENTER);
    	wrapper.setPrefHeight(100);
    	wrapper.setMaxHeight(100);
    	wrapper.setMinHeight(100);
    	wrapper.managedProperty().bind(wrapper.visibleProperty());
    	this.getChildren().add(wrapper);
	
		// setup an indeterminant progress bar
		ProgressIndicator progress = new ProgressIndicator();
		progress.managedProperty().bind(progress.visibleProperty());
		this.getChildren().add(progress);
		
		wrapper.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
    	
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
