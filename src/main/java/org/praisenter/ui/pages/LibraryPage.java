package org.praisenter.ui.pages;

import org.praisenter.data.Persistable;
import org.praisenter.ui.ActionPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;

public final class LibraryPage extends BorderPane implements Page {
	private static final String LIBRARY_PAGE_CLASS = "p-library-page";
	
	private final ActionPane actionPane;
	private final ObservableList<Persistable> items;
	
	public LibraryPage(GlobalContext context) {
		this.items = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(), (i) -> {
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.values());
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		this.actionPane = itemListing;
		
		this.setCenter(itemListing);
		this.getStyleClass().add(LIBRARY_PAGE_CLASS);
		
		itemListing.focusedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				itemListing.requestFocus();
			}
		});
	}

	@Override
	public void setDefaultFocus() {
		this.actionPane.setDefaultFocus();
	}
}
