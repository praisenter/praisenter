package org.praisenter.ui.slide;

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
		this.view.setViewMode(SlideMode.VIEW);
		this.view.setViewScaleAlignCenter(true);
		// TODO css - needs to be slightly smaller than the listview width
		
		// TODO i don't really like this as now it's not auto-sizing based on css
//		double tw = 200;
//		double th = (tw / w) * h;
//		this.view.setMaxWidth(tw);
//		
////		this.setMinSize(tw, th);
//		this.setPrefSize(tw+10, th+10);
		
		// this doesn't work either because of the scrollbars that show up that we can't control
		this.setPrefHeight(150);
		
		this.setGraphic(null);
		this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		
		this.itemProperty().addListener((obs, oldItem, newItem) -> {
	        if (newItem != null) {
	        	this.view.setSlide(newItem);
	        }
	    });
		
	    this.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
	        if (isEmpty) {
	            this.setGraphic(null);
	        } else {
	            this.setGraphic(this.view);
	        }
	    });
	}
}