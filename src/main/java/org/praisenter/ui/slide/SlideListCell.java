package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.utility.ClasspathLoader;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;

final class SlideListCell extends ListCell<Slide> {
	private static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/images/transparent.png");
	
	private final ImageView graphic;
	private final Pane pane;
	
	public SlideListCell() {	
		this.pane = new Pane();
		this.pane.setBackground(new Background(new BackgroundImage(TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		
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