package org.praisenter.javafx.slide;

import org.praisenter.javafx.FlowListCell;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.SelectionEvent;
import org.praisenter.javafx.slide.editor.SlideEditorPane;

import javafx.scene.layout.BorderPane;

public class SlideLibraryPane extends BorderPane {
	private final PraisenterContext context;
	
	private final SlideEditorPane editor;
	private final FlowListView<SlideListItem> slides;
	
	public SlideLibraryPane(PraisenterContext context) {
		this.context = context;
		this.editor = new SlideEditorPane(context);
		
		this.slides = new FlowListView<SlideListItem>(new SlideListViewCellFactory(100));
		this.slides.itemsProperty().bindContent(this.context.getSlideLibrary().getItems());
		
		// TODO some nice animation or something to transition back and forth
		// TODO maybe auto-saving?
		
		this.slides.addEventHandler(SelectionEvent.DOUBLE_CLICK, (e) -> {
			@SuppressWarnings("unchecked")
			FlowListCell<SlideListItem> view = (FlowListCell<SlideListItem>)e.getTarget();
			SlideListItem item = view.getData();
			
			editor.setSlide(null);
			editor.setSlide(item.slide);
			
			this.setCenter(editor);
		});
		
		this.setCenter(this.slides);
	}
}
