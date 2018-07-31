package org.praisenter.ui.bible;

import java.util.concurrent.CompletableFuture;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.Action;
import org.praisenter.ui.DocumentPane;
import org.praisenter.ui.ReadOnlyPraisenterContext;
import org.praisenter.ui.SelectionInfo;
import org.praisenter.ui.undo.UndoManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class BibleEditorPane extends BorderPane implements DocumentPane {
	private final ReadOnlyPraisenterContext context;
	
	private final ObjectProperty<Bible> bible;
	private final BooleanProperty hasUnsavedChanges;
	private final StringProperty documentName;
	private final ObjectProperty<EventHandler<Event>> onActionStateChanged;
	
	// nodes
	
	private final TreeView<Object> treeView;
	
	// helpers
	
	private final SelectionInfo<TreeItem<Object>> selectionInfo;
	private final UndoManager undoManager = new UndoManager();
	
	public BibleEditorPane(ReadOnlyPraisenterContext context) {
		this.context = context;
		
		this.bible = new SimpleObjectProperty<>();
		this.hasUnsavedChanges = new SimpleBooleanProperty();
		this.documentName = new SimpleStringProperty();
		this.onActionStateChanged = new SimpleObjectProperty<>();
		
		this.bible.addListener((obs, ov, nv) -> {
			this.documentName.unbind();
			if (nv != null) {
				this.documentName.bind(nv.nameProperty());
			}
		});
		
		BibleTreeItem root = new BibleTreeItem();
		root.valueProperty().bind(this.bible);
		
		this.treeView = new TreeView<Object>();
		this.treeView.setRoot(root);
		this.treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.treeView.setCellFactory((view) -> {
			TreeCell<Object> cell = new BibleTreeCell();
//			// wire up events
//        	cell.setOnDragDetected(e -> {
//    			manager.dragDetected(cell, e);
//    		});
//        	cell.setOnDragExited(e -> {
//    			manager.dragExited(cell, e);
//    		});
//        	cell.setOnDragEntered(e -> {
//    			manager.dragEntered(cell, e);
//    		});
//        	cell.setOnDragOver(e -> {
//    			manager.dragOver(cell, e);
//    		});
//        	cell.setOnDragDropped(e -> {
//    			EditCommand command = manager.dragDropped(cell, e);
//    			applyCommand(command);
//    		});
//        	cell.setOnDragDone(e -> {
//        		manager.dragDone(cell, e);
//        	});
        	return cell;
		});
		
		this.setCenter(this.treeView);

		this.treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<Object>> change) -> {
			EventHandler<Event> eh = this.onActionStateChanged.get();
			if (eh != null) {
				eh.handle(null);
			}
		});
		
		this.selectionInfo = new SelectionInfo<TreeItem<Object>>(this.treeView.getSelectionModel(), (item) -> {
			return item.getValue().getClass();
		});
		
		this.undoManager.addWatchDefinition(Verse.class, (verse, manager) -> {
			manager.register("Verse Number", verse, verse.numberProperty());
			manager.register("Verse Text", verse, verse.textProperty());
		});
		
		this.undoManager.addWatchDefinition(Chapter.class, (chapter, manager) -> {
			manager.register("Chapter Number", chapter, chapter.numberProperty());
			manager.register("Verses", chapter, chapter.getVerses());
		});
		
		this.undoManager.addWatchDefinition(Book.class, (book, manager) -> {
			manager.register("Book Name", book, book.nameProperty());
			manager.register("Book Number", book, book.numberProperty());
			manager.register("Chapters", book, book.getChapters());
		});
		
		this.undoManager.addWatchDefinition(Bible.class, (bible, manager) -> {
			manager.register("Copyright", bible, bible.copyrightProperty());
			manager.register("Language", bible, bible.languageProperty());
			manager.register("Name", bible, bible.nameProperty());
			manager.register("Notes", bible, bible.notesProperty());
			manager.register("Source", bible, bible.sourceProperty());
			manager.register("Books", bible, bible.getBooks());
		});
		
		this.undoManager.targetProperty().bind(this.bible);
	}
	
	@Override
	public EventHandler<Event> getOnActionStateChanged() {
		return this.onActionStateChanged.get();
	}
	
	@Override
	public ObjectProperty<EventHandler<Event>> onActionStateChangedProperty() {
		return this.onActionStateChanged;
	}
	
	@Override
	public void setOnActionStateChanged(EventHandler<Event> handler) {
		this.onActionStateChanged.set(handler);
	}
	
	@Override
	public String getDocumentName() {
		return this.documentName.get();
	}
	
	@Override
	public ReadOnlyStringProperty documentNameProperty() {
		return this.documentName;
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		return this.hasUnsavedChanges.get();
	}
	
	@Override
	public ReadOnlyBooleanProperty unsavedChangesProperty() {
		return this.hasUnsavedChanges;
	}
	
	@Override
	public void setDefaultFocus() {
		this.treeView.requestFocus();
	}
	
	@Override
	public void cleanUp() {
		this.bible.set(null);
	}
	
	@Override
	public CompletableFuture<Void> saveDocument() {
		Bible bible = this.bible.get();
		return this.context.getDataManager().update(bible);
	}
	
	@Override
	public CompletableFuture<Void> performAction(Action action) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		switch (action) {
			case COPY:
				return this.selectionInfo.isSingleTypeSelected();
			case PASTE:
				return this.selectionInfo.isSingleTypeSelected();
			case CUT:
				return this.selectionInfo.isSingleTypeSelected();
			case DELETE:
				return this.selectionInfo.getSelectedCount() > 0;
			case NEW_BOOK:
				return this.selectionInfo.getSelectedType() == Bible.class;
			case NEW_CHAPTER:
				return this.selectionInfo.getSelectedType() == Book.class;
			case NEW_VERSE:
				return this.selectionInfo.getSelectedType() == Chapter.class;
			case REDO:
			case UNDO:
			case RENUMBER:
				return this.selectionInfo.getSelectedType() != Verse.class;
			case REORDER:
				return this.selectionInfo.getSelectedType() != Verse.class;
			default:
				return false;
		}
	}

	public Bible getBible() {
		return this.bible.get();
	}
	
	public void setBible(Bible bible) {
		this.bible.set(bible);
	}
	
	public ObjectProperty<Bible> bibleProperty() {
		return this.bible;
	}
}
