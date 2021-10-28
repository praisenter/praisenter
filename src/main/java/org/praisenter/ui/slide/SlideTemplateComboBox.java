package org.praisenter.ui.slide;

import org.praisenter.data.PersistableComparator;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class SlideTemplateComboBox extends ComboBox<Slide> {
	public SlideTemplateComboBox(GlobalContext context) {
		ObservableList<Slide> allSlides = context.getWorkspaceManager().getItemsUnmodifiable(Slide.class);
        FilteredList<Slide> filtered = allSlides.filtered(s -> s.hasPlaceholders());
        SortedList<Slide> sorted = filtered.sorted(new PersistableComparator<Slide>());
        this.setItems(sorted);
        
        // JAVABUG (L) 09/16/17 [workaround] The combobox's drop down goes off screen - I've mitigated by reducing the number of items visible at one time
        this.setVisibleRowCount(6);
		this.setCellFactory((view) -> {
			return new SlideTemplateListCell();
		});
		this.setButtonCell(new ListCell<Slide>() {
			@Override
			protected void updateItem(Slide item, boolean empty) {
				super.updateItem(item, empty);
				
				if (item == null || empty) {
					this.textProperty().unbind();
				} else {
					this.textProperty().bind(item.nameProperty());
				}
			}
		});
	}
}
