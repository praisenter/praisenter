package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.SlideAssignment;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;

final class SlideAssignmentListCell extends ListCell<SlideAssignment> {
	private final PraisenterContext context;
	private final ImageView graphic;
	private final Pane pane;
	
	public SlideAssignmentListCell(PraisenterContext context) {
		this.context = context;
		
		this.pane = new Pane();
		this.pane.setBackground(new Background(new BackgroundImage(Fx.TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		
		this.graphic = new ImageView();
		this.graphic.setFitWidth(100);
		
		this.pane.getChildren().add(this.graphic);
		
		setGraphic(this.pane);
		setWrapText(true);
	}
	
	@Override
	protected void updateItem(SlideAssignment item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item == null || empty) {
			this.graphic.setImage(null);
			this.pane.setVisible(false);
			setText(null);
		} else {
			// use the context to get the appropriate SlideListItem for this assignment
			SlideListItem sli = this.context.getSlideLibrary().getListItem(item.getSlideId());
			if (sli != null) {
				this.graphic.setImage(SwingFXUtils.toFXImage(sli.getSlide().getThumbnail(), null));
				this.pane.setVisible(true);
				setText(sli.getName());
			} else {
				this.graphic.setImage(null);
				this.pane.setVisible(false);
				this.setText(null);
			}
		}
	}
}