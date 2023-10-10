package org.praisenter.ui.pages;

import org.praisenter.data.Persistable;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;

public class LibraryPage extends BorderPane {
//	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String LIBRARY_PAGE_CLASS = "p-library-page";
	
	private final ObservableList<Persistable> items;
	
	public LibraryPage(GlobalContext context) {
		this.items = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(), (i) -> {
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.values());
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		this.setCenter(itemListing);
		this.getStyleClass().add(LIBRARY_PAGE_CLASS);
	}
}
