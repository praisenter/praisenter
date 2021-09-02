package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public final class SlideList extends BorderPane {
	private final ObservableList<Slide> slides;
	
	private final ObservableList<Slide> selected;
	
	public SlideList(GlobalContext context, double w, double h) {
		this.slides = FXCollections.observableArrayList();
		this.selected = FXCollections.observableArrayList();
		
		ListView<Slide> view = new ListView<>(this.slides);
		Bindings.bindContent(this.selected, view.getSelectionModel().getSelectedItems());
		
		view.setCellFactory(s -> new SlideListCell(context, w, h));
		view.setPrefWidth(300);
		
		this.setCenter(view);
	}
	
	public ObservableList<Slide> getSlides() {
		return this.slides;
	}
	
	public ObservableList<Slide> getSelected() {
		return this.selected;
	}
}
