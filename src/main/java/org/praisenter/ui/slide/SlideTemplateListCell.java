package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

final class SlideTemplateListCell extends ListCell<Slide> {
	private static final String SLIDE_THUMBNAIL_LIST_CELL_CSS = "p-slide-template-list-cell";
	private static final String SLIDE_THUMBNAIL_LIST_CELL_GRAPHIC_CSS = "p-slide-template-list-cell-graphic";
	
	private final ImageView graphic;
	private final Pane pane;
	
	public SlideTemplateListCell() {
		this.getStyleClass().add(SLIDE_THUMBNAIL_LIST_CELL_CSS);
		
		this.pane = new Pane();
		this.pane.getStyleClass().add(SLIDE_THUMBNAIL_LIST_CELL_GRAPHIC_CSS);
		
		this.graphic = new ImageView();
		this.graphic.setFitWidth(100);
		
		this.pane.getChildren().add(this.graphic);
		
		this.setGraphic(this.pane);
		this.setWrapText(true);
	}
	
	@Override
	protected void updateItem(Slide item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.textProperty().unbind();
			this.graphic.imageProperty().unbind();
			this.graphic.setImage(null);
			this.pane.setVisible(false);
		} else {
			this.textProperty().bind(item.nameProperty());
			this.graphic.imageProperty().bind(Bindings.createObjectBinding(() -> {
    			return new Image(item.getThumbnailPath().toUri().toURL().toExternalForm());
    		}, item.thumbnailPathProperty()));
			this.pane.setVisible(true);
		}
	}
}