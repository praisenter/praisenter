package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public final class SlideList extends BorderPane {
	private static final String SLIDE_LIST_CLASS = "p-slide-list";
	
	private final ObservableList<Slide> slides;
	private final ObservableList<Slide> selected;
	private final ObservableList<Slide> selectedUnmodifiable;
	private final ObjectProperty<Slide> selection;
	
	public SlideList(GlobalContext context, double w, double h) {
		this.slides = FXCollections.observableArrayList();
		this.selected = FXCollections.observableArrayList();
		this.selection = new SimpleObjectProperty<>();
		
		this.selectedUnmodifiable = FXCollections.unmodifiableObservableList(this.selected);
		
		ListView<Slide> view = new ListView<>(this.slides);
		Bindings.bindContent(this.selected, view.getSelectionModel().getSelectedItems());
		
		this.selection.bind(Bindings.valueAt(this.selected, 0));
		
		view.setCellFactory(s -> new SlideListCell(context, w, h));
		view.getStyleClass().add(SLIDE_LIST_CLASS);
		
		this.setCenter(view);
	}
	
	public ObservableList<Slide> getSlides() {
		return this.slides;
	}
	
	public ObservableList<Slide> getSelected() {
		return this.selectedUnmodifiable;
	}
	
	public Slide getSelection() {
		return this.selection.get();
	}
	
	public ReadOnlyObjectProperty<Slide> selectionProperty() {
		return this.selection;
	}
}
