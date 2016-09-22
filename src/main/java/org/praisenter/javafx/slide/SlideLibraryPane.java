package org.praisenter.javafx.slide;

import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.PraisenterContext;

import javafx.scene.layout.BorderPane;

public class SlideLibraryPane extends BorderPane {
	private final PraisenterContext context;
	
	public SlideLibraryPane(PraisenterContext context) {
		this.context = context;
		
		FlowListView<SlideListItem> slidesView = new FlowListView<SlideListItem>(new SlideListViewCellFactory(100));
		slidesView.itemsProperty().bindContent(this.context.getSlideLibrary().getItems());
		
		// TODO double click to edit a slide
		// TODO some nice animation or something to transition back and forth
		// TODO maybe auto-saving?
		
		this.setCenter(slidesView);
	}
}
