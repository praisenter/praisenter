package org.praisenter.ui.slide;

import org.praisenter.data.TextItem;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

final class SlideListCell extends ListCell<Slide> {
	private static final String SLIDE_LIST_CELL_CLASS = "p-slide-list-cell";
	private static final String SLIDE_LIST_CELL_SLIDE_VIEW_CLASS = "p-slide-list-cell-slide-view";
	
	private final SlideView view;
	
	public SlideListCell(GlobalContext context) {
		this.getStyleClass().add(SLIDE_LIST_CELL_CLASS);
		
		this.view = new SlideView(context);
		this.view.setAutoHideEnabled(false);
		this.view.setCheckeredBackgroundEnabled(true);
		this.view.setFitToWidthEnabled(true);
		this.view.setFitToHeightEnabled(true);
		this.view.setViewMode(SlideMode.VIEW);
		this.view.setViewScaleAlignCenter(true);
		this.view.getStyleClass().add(SLIDE_LIST_CELL_SLIDE_VIEW_CLASS);
		
		this.setText(null);
		this.setGraphic(null);
		this.setContentDisplay(ContentDisplay.BOTTOM);
		
		this.itemProperty().addListener((obs, oldItem, newItem) -> {
	        if (newItem != null) {
	        	String name = newItem.getName();
	        	if (newItem.hasPlaceholders()) {
	        		TextItem txt = newItem.getPlaceholderData().get(TextVariant.PRIMARY, TextType.TITLE);
	        		if (txt != null) {
	        			String replacedName = txt.getText();
	        			if (replacedName != null && !replacedName.isBlank()) {
	        				name = replacedName;
	        			}
	        		}
	        	}
	        	this.setText(name);
	        	this.view.setSlide(newItem);
	        }
	    });
		
	    this.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
	        if (isEmpty) {
	        	this.setText(null);
	            this.setGraphic(null);
	        } else {
	            this.setGraphic(this.view);
	        }
	    });
	}
}