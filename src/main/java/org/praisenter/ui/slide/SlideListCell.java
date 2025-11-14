package org.praisenter.ui.slide;

import java.util.UUID;

import org.praisenter.data.Persistable;
import org.praisenter.data.TextItem;
import org.praisenter.data.TextStore;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideReference;
import org.praisenter.ui.GlobalContext;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

final class SlideListCell extends ListCell<SlideReference> {
	private static final String SLIDE_LIST_CELL_CLASS = "p-slide-list-cell";
	private static final String SLIDE_LIST_CELL_SLIDE_VIEW_CLASS = "p-slide-list-cell-slide-view";
	
	private final GlobalContext context;
	private final SlideView view;
	
	private final DoubleProperty slideWidth;
	private final DoubleProperty slideHeight;
	
	public SlideListCell(GlobalContext context) {
		this.getStyleClass().add(SLIDE_LIST_CELL_CLASS);

		this.context = context;
		
		this.view = new SlideView(context);
		this.view.setAutoHideEnabled(false);
		this.view.setCheckeredBackgroundEnabled(true);
		this.view.setFitToWidthEnabled(true);
		this.view.setFitToHeightEnabled(true);
		this.view.setViewMode(SlideMode.VIEW);
		this.view.setViewScaleAlignCenter(true);
		this.view.getStyleClass().add(SLIDE_LIST_CELL_SLIDE_VIEW_CLASS);

		this.slideWidth = new SimpleDoubleProperty();
		this.slideHeight = new SimpleDoubleProperty();
		
		this.setText(null);
		this.setGraphic(null);
		this.setContentDisplay(ContentDisplay.BOTTOM);
		
	    context.getWorkspaceManager().getItemsUnmodifiable().addListener((Change<? extends Persistable> c) -> {
	    	SlideReference sr = this.getItem();
	    	if (sr == null)
	    		return;
	    	
	    	TextStore placeholderData = sr.getPlaceholderData();
	    	
	    	while (c.next()) {
	    		if (c.wasAdded()) {
	    			for (var item : c.getAddedSubList()) {
	    				if (item.getId().equals(sr.getSlideId())) {
	    					// it was updated
	    					this.updateSlideView((Slide)item, placeholderData);
	    					return;
	    				}
	    			}
	    		}
	    		
	    		// removed is handled outside of the cell

	    		if (c.wasUpdated()) {
	    			for (int i = c.getFrom(); i < c.getTo(); i++) {
	    				var item = c.getList().get(i);
	    				if (item.getId().equals(sr.getSlideId())) {
	    					// it was changed
	    					this.updateSlideView((Slide)item, placeholderData);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    });
	    
	    this.slideWidth.addListener((obs, ov, nv) -> {
	    	if (nv != null) {
	    		this.handleResize(nv.doubleValue(), this.slideHeight.get());
	    	}
	    });
	    
	    this.slideHeight.addListener((obs, ov, nv) -> {
	    	if (nv != null) {
	    		this.handleResize(this.slideWidth.get(), nv.doubleValue());
	    	}
	    });
	}
	
	@Override
	protected void updateItem(SlideReference item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			this.clearSlideView();
			return;
		}
		
        if (item != null) {
        	UUID slideId = item.getSlideId();
        	TextStore placeholderData = item.getPlaceholderData();
        	
        	// try to find the slide
        	Persistable persistable = context.getWorkspaceManager().getPersistableById(slideId);
			if (persistable != null && persistable instanceof Slide) {
				// if found, then render it
				this.updateSlideView((Slide)persistable, placeholderData);
			} else {
				// otherwise clear
				this.clearSlideView();
			}
        } else {
        	this.clearSlideView();
        }
	}
	
	private void handleResize(double width, double height) {
		SlideReference sr = this.getItem();
		if (sr != null) {
			UUID slideId = sr.getSlideId();
	    	TextStore placeholderData = sr.getPlaceholderData();
	    	
	    	// try to find the slide
	    	Persistable item = context.getWorkspaceManager().getPersistableById(slideId);
			if (item != null && item instanceof Slide) {
				// if found, then render it
				this.updateSlideView((Slide)item, placeholderData);
			} else {
				// otherwise clear
				this.clearSlideView();
			}
		}
	}

	private void clearSlideView() {
		this.setText(null);
		this.setGraphic(null);
	}
	
	private void updateSlideView(Slide slide, TextStore placeholderData) {
		slide = slide.copy();
		slide.setPlaceholderData(placeholderData);
		slide.fit(this.slideWidth.get(), this.slideHeight.get());
		
		String name = slide.getName();
		if (slide.hasPlaceholders()) {
			TextItem txt = placeholderData.get(TextVariant.PRIMARY, TextType.TITLE);
    		if (txt != null) {
    			String replacedName = txt.getText();
    			if (replacedName != null && !replacedName.isBlank()) {
    				name = replacedName;
    			}
    		}
		}
		
		this.setText(name);
    	this.view.render(slide, placeholderData, false);
    	if (this.getGraphic() == null) {
    		this.setGraphic(this.view);
    	}
	}
	
	public double getSlideWidth() {
		return this.slideWidth.get();
	}
	
	public void setSlideWidth(double width) {
		this.slideWidth.set(width);
	}
	
	public DoubleProperty slideWidthProperty() {
		return this.slideWidth;
	}
	
	public double getSlideHeight() {
		return this.slideHeight.get();
	}
	
	public void setSlideHeight(double height) {
		this.slideHeight.set(height);
	}
	
	public DoubleProperty slideHeightProperty() {
		return this.slideHeight;
	}
}