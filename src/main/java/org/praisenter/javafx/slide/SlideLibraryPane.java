package org.praisenter.javafx.slide;

import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.FlowListCell;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.SelectionEvent;
import org.praisenter.javafx.slide.editor.SlideEditorPane;

import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class SlideLibraryPane extends BorderPane {
	private final PraisenterContext context;
	
	private final SlideEditorPane editor;
	private final FlowListView<SlideListItem> slides;
	
	public SlideLibraryPane(PraisenterContext context) {
		this.context = context;
		this.editor = new SlideEditorPane(context);
		
		this.slides = new FlowListView<SlideListItem>(Orientation.HORIZONTAL, new Callback<SlideListItem, FlowListCell<SlideListItem>>() {
        	@Override
        	public FlowListCell<SlideListItem> call(SlideListItem item) {
				return new SlideListCell(item, 100);
			}
        });
		this.slides.itemsProperty().bindContent(this.context.getSlideLibrary().getItems());
		
		// TODO menus are not available when focused on slide editor (only when focused on ribbon)
		
		// TODO: undo/redo might be easier here since the size the XML documents is much smaller than bibles
		
		this.slides.addEventHandler(SelectionEvent.DOUBLE_CLICK, (e) -> {
			@SuppressWarnings("unchecked")
			FlowListCell<SlideListItem> view = (FlowListCell<SlideListItem>)e.getTarget();
			SlideListItem item = view.getData();
			if (item.isLoaded()) {
	    		fireEvent(new ApplicationEvent(e.getSource(), e.getTarget(), ApplicationEvent.ALL, ApplicationAction.EDIT, item.getSlide()));
	    	}
		});
		
		this.setCenter(this.slides);
	}
}
