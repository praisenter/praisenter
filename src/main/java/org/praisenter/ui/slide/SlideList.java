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

public final class SlideList extends ListView<Slide> {
	private static final String SLIDE_LIST_CLASS = "p-slide-list";
	
//	private final ObservableList<Slide> slides;
	private final ObservableList<Slide> selected;
	private final ObservableList<Slide> selectedUnmodifiable;
	private final ObjectProperty<Slide> selection;
	
	public SlideList(GlobalContext context) {
//		this.slides = FXCollections.observableArrayList();
		this.selected = FXCollections.observableArrayList();
		this.selection = new SimpleObjectProperty<>();
		
		this.selectedUnmodifiable = FXCollections.unmodifiableObservableList(this.selected);
		
//		ListView<Slide> view = new ListView<>(this.slides);
		Bindings.bindContent(this.selected, this.getSelectionModel().getSelectedItems());
		
		this.selection.bind(Bindings.valueAt(this.getSelectionModel().getSelectedItems(), 0));
		
		this.setCellFactory(s -> {
			SlideListCell cell = new SlideListCell(context);
//			cell.maxWidthProperty().bind(this.widthProperty());
			return cell;
		});
		this.getStyleClass().add(SLIDE_LIST_CLASS);
	}
	
//	public ObservableList<Slide> getSlides() {
//		return this.slides;
//	}
	
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
