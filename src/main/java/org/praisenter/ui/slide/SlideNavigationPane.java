package org.praisenter.ui.slide;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;

public final class SlideNavigationPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ObservableList<Slide> slideList;
	
	// value
	
	private final ObjectProperty<Slide> value;
	
	private boolean isSelecting = false;
	
	public SlideNavigationPane(GlobalContext context) {
		this.value = new SimpleObjectProperty<Slide>(null);
		
		LibraryList slides = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.SLIDE);
		slides.setMultiSelectEnabled(false);
		slides.setTypeFilterVisible(false);
		slides.setDetailsPaneVisible(false);
		slides.setPrefWidth(400);
		
		FilteredList<Slide> filtered = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(Slide.class));
		filtered.setPredicate(s -> {
			return !s.hasPlaceholders();
		});
		this.slideList = filtered;
		Bindings.bindContent(slides.getItems(), this.slideList); 
		
		slides.getSelectedItems().addListener((Change<?> c) -> {
			this.isSelecting = true;
			List<?> selected = c.getList();
			if (selected.size() > 0) {
				this.value.set((Slide)selected.get(0));
			} else {
				this.value.set(null);
			}
			this.isSelecting = false;
		});
		
		this.value.addListener((obs, ov, nv) -> {
			if (this.isSelecting) {
				return;
			}
			if (nv != null) {
				slides.getSelectionModel().select(nv);
			}
		});
		
		this.setCenter(slides);

	}
	
	public Slide getValue() {
		return this.value.get();
	}
	
	public void setValue(Slide value) {
		this.value.set(value);
	}
	
	public ObjectProperty<Slide> valueProperty() {
		return this.value;
	}
}
