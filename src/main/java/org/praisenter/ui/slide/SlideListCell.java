package org.praisenter.ui.slide;

import org.praisenter.data.TextItem;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

final class SlideListCell extends ListCell<Slide> {
	private final SlideView view;
	
	public SlideListCell(GlobalContext context, double w, double h) {
		this.view = new SlideView(context);
		this.view.setAutoHideEnabled(false);
		this.view.setCheckeredBackgroundEnabled(true);
		this.view.setFitToWidthEnabled(true);
		this.view.setFitToHeightEnabled(true);
		// TODO i don't really like this as now it's not auto-sizing based on css
		this.view.setPrefHeight(150);
		this.view.setPrefWidth(270);
		this.view.setViewMode(SlideMode.VIEW);
		this.view.setViewScaleAlignCenter(true);
		
		this.setText(null);
		this.setGraphic(null);
		this.setContentDisplay(ContentDisplay.BOTTOM);
		
		this.itemProperty().addListener((obs, oldItem, newItem) -> {
	        if (newItem != null) {
	        	String name = newItem.getName();
	        	if (newItem.hasPlaceholders()) {
	        		TextItem txt = newItem.getPlaceholderData().get(TextVariant.PRIMARY, TextType.TITLE);
	        		if (txt != null) {
	        			name = txt.getText();
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