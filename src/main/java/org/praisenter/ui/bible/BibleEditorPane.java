package org.praisenter.ui.bible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.json.JsonIO;
import org.praisenter.ui.Action;
import org.praisenter.ui.DocumentPane;
import org.praisenter.ui.ReadOnlyPraisenterContext;
import org.praisenter.ui.SelectionInfo;
import org.praisenter.ui.undo.UndoManager;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BibleEditorPane extends BorderPane implements DocumentPane {
	private static final DataFormat BOOK_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Book.class.getName());
	private static final DataFormat CHAPTER_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Chapter.class.getName());
	private static final DataFormat VERSE_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Verse.class.getName());
	
	private final ReadOnlyPraisenterContext context;
	
	private final ObjectProperty<Bible> bible;
	private final BooleanProperty hasUnsavedChanges;
	private final StringProperty documentName;
	private final ObjectProperty<EventHandler<Event>> onActionStateChanged;
	
	// helpers
	
	private final StringProperty name;
	private final StringProperty language;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty notes;
	
	private final ObjectProperty<Book> selectedBook;
	private final StringProperty bookName;
	private final IntegerProperty bookNumber;
	private final ObjectProperty<Integer> bookNumber2;
	
	private final ObjectProperty<Chapter> selectedChapter;
	private final IntegerProperty chapterNumber;
	private final ObjectProperty<Integer> chapterNumber2;
	
	private final ObjectProperty<Verse> selectedVerse;
	private final StringProperty verseText;
	private final IntegerProperty verseNumber;
	private final ObjectProperty<Integer> verseNumber2;

	private final SelectionInfo<TreeItem<Object>> selectionInfo;
	private final UndoManager undoManager = new UndoManager();
	
	// nodes
	
	private final TreeView<Object> treeView;
	
	public BibleEditorPane(ReadOnlyPraisenterContext context) {
		this.context = context;
		
		this.bible = new SimpleObjectProperty<>();
		this.hasUnsavedChanges = new SimpleBooleanProperty();
		this.documentName = new SimpleStringProperty();
		this.onActionStateChanged = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.language = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		
		this.bible.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.documentName.unbind();
				this.name.unbindBidirectional(ov.nameProperty());
				this.language.unbindBidirectional(ov.languageProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
			}
			if (nv != null) {
				this.documentName.bind(nv.nameProperty());
				this.name.bindBidirectional(nv.nameProperty());
				this.language.bindBidirectional(nv.languageProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.notes.bindBidirectional(nv.notesProperty());
			}
		});
		
		this.bookNumber = new SimpleIntegerProperty();
		this.bookName = new SimpleStringProperty();
		this.bookNumber2 = this.bookNumber.asObject();
		
		this.selectedBook = new SimpleObjectProperty<>();
		this.selectedBook.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.bookName.unbindBidirectional(ov.nameProperty());
				this.bookNumber.unbindBidirectional(ov.numberProperty());
			}
			if (nv != null) {
				this.bookName.bindBidirectional(nv.nameProperty());
				this.bookNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.chapterNumber = new SimpleIntegerProperty();
		this.chapterNumber2 = this.chapterNumber.asObject();
		
		this.selectedChapter = new SimpleObjectProperty<>();
		this.selectedChapter.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.chapterNumber.unbindBidirectional(ov.numberProperty());
			}
			if (nv != null) {
				this.chapterNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		this.verseNumber = new SimpleIntegerProperty();
		this.verseText = new SimpleStringProperty();
		this.verseNumber2 = this.verseNumber.asObject();
		
		this.selectedVerse = new SimpleObjectProperty<>();
		this.selectedVerse.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.verseText.unbindBidirectional(ov.textProperty());
				this.verseNumber.unbindBidirectional(ov.numberProperty());
			}
			if (nv != null) {
				this.verseText.bindBidirectional(nv.textProperty());
				this.verseNumber.bindBidirectional(nv.numberProperty());
			}
		});
		
		// UI
		
		Label lblBibleName = new Label("Name");
		TextField txtBibleName = new TextField();
		txtBibleName.textProperty().bindBidirectional(this.name);

		Label lblBibleLanguage = new Label("Language");
		TextField txtBibleLanguage = new TextField();
		txtBibleLanguage.textProperty().bindBidirectional(this.language);
		
		Label lblBibleSource = new Label("Source");
		TextField txtBibleSource = new TextField();
		txtBibleSource.textProperty().bindBidirectional(this.source);
		
		Label lblBibleCopyright = new Label("Copyright");
		TextField txtBibleCopyright = new TextField();
		txtBibleCopyright.textProperty().bindBidirectional(this.copyright);

		Label lblBibleNotes = new Label("Notes");
		TextArea txtBibleNotes = new TextArea();
		txtBibleNotes.textProperty().bind(this.notes);
		txtBibleNotes.setWrapText(true);
		
		Label lblBookName = new Label("Name");
		TextField txtBookName = new TextField();
		txtBookName.textProperty().bindBidirectional(this.bookName);
		
		Label lblBookNumber = new Label("Number");
		Spinner<Integer> spnBookNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnBookNumber.setEditable(true);
		spnBookNumber.getValueFactory().valueProperty().bindBidirectional(this.bookNumber2);
		
		Label lblChapterNumber = new Label("Number");
		Spinner<Integer> spnChapterNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnChapterNumber.setEditable(true);
		spnChapterNumber.getValueFactory().valueProperty().bindBidirectional(this.chapterNumber2);
		
		Label lblVerseText = new Label("Text");
		TextArea txtVerseText = new TextArea();
		txtVerseText.textProperty().bindBidirectional(this.verseText);
		txtVerseText.setWrapText(true);
		
		Label lblVerseNumber = new Label("Number");
		Spinner<Integer> spnVerseNumber = new Spinner<>(1, Integer.MAX_VALUE, 1);
		spnVerseNumber.setEditable(true);
		spnVerseNumber.getValueFactory().valueProperty().bindBidirectional(this.verseNumber2);
		
		VBox fields = new VBox(
				lblBibleName, txtBibleName,
				lblBibleLanguage, txtBibleLanguage,
				lblBibleSource, txtBibleSource,
				lblBibleCopyright, txtBibleCopyright,
				lblBibleNotes, txtBibleNotes,
				lblBookName, txtBookName, 
				lblBookNumber, spnBookNumber,
				lblChapterNumber, spnChapterNumber,
				lblVerseText, txtVerseText,
				lblVerseNumber, spnVerseNumber);
		
		this.setRight(fields);
		
		// hide/show
		
		BooleanBinding bookSelected = this.selectedBook.isNotNull();
		lblBookName.visibleProperty().bind(bookSelected);
		lblBookName.managedProperty().bind(bookSelected);
		txtBookName.visibleProperty().bind(bookSelected);
		txtBookName.managedProperty().bind(bookSelected);
		lblBookNumber.visibleProperty().bind(bookSelected);
		lblBookNumber.managedProperty().bind(bookSelected);
		spnBookNumber.visibleProperty().bind(bookSelected);
		spnBookNumber.managedProperty().bind(bookSelected);
		
		BooleanBinding chapterSelected = this.selectedChapter.isNotNull();
		lblChapterNumber.visibleProperty().bind(chapterSelected);
		lblChapterNumber.managedProperty().bind(chapterSelected);
		spnChapterNumber.visibleProperty().bind(chapterSelected);
		spnChapterNumber.managedProperty().bind(chapterSelected);
		
		BooleanBinding verseSelected = this.selectedVerse.isNotNull();
		lblVerseText.visibleProperty().bind(verseSelected);
		lblVerseText.managedProperty().bind(verseSelected);
		txtVerseText.visibleProperty().bind(verseSelected);
		txtVerseText.managedProperty().bind(verseSelected);
		lblVerseNumber.visibleProperty().bind(verseSelected);
		lblVerseNumber.managedProperty().bind(verseSelected);
		spnVerseNumber.visibleProperty().bind(verseSelected);
		spnVerseNumber.managedProperty().bind(verseSelected);
		
		// the tree
		
		BibleTreeItem root = new BibleTreeItem();
		root.valueProperty().bind(this.bible);
		
		this.treeView = new TreeView<Object>(root);
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
			// handle the selection state changing
			EventHandler<Event> eh = this.onActionStateChanged.get();
			if (eh != null) {
				eh.handle(null);
			}
			
			// handle selection of a tree item
			this.selectedBook.set(null);
			this.selectedChapter.set(null);
			this.selectedVerse.set(null);
			if (change.getList().size() == 1) {
				Object value = change.getList().get(0).getValue();
				if (value instanceof Book) {
					this.selectedBook.set((Book)value);
				} else if (value instanceof Chapter) {
					this.selectedChapter.set((Chapter)value);
				} else if (value instanceof Verse) {
					this.selectedVerse.set((Verse)value);
				}
			}
		});
		
		this.selectionInfo = new SelectionInfo<TreeItem<Object>>(this.treeView.getSelectionModel(), (item) -> {
			return item.getValue().getClass();
		});
		
		this.undoManager.targetProperty().bind(this.bible);
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
		this.undoManager.reset();
	}
	
	@Override
	public CompletableFuture<Void> saveDocument() {
		Bible bible = this.bible.get();
		return this.context.getDataManager().update(bible);
	}
	
	@Override
	public void undo() {
		this.undoManager.undo();
	}
	
	@Override
	public void redo() {
		this.undoManager.redo();
	}
	
	@Override
	public CompletableFuture<Void> performAction(Action action) {
		switch (action) {
			case COPY:
				return this.copy(false);
			case PASTE:
				return this.paste();
			case CUT:
				return this.copy(true);
//			case DELETE:
//				return this.selectionInfo.getSelectedCount() > 0;
//			case NEW_BOOK:
//				return this.selectionInfo.getSelectedType() == Bible.class;
//			case NEW_CHAPTER:
//				return this.selectionInfo.getSelectedType() == Book.class;
//			case NEW_VERSE:
//				return this.selectionInfo.getSelectedType() == Chapter.class;
//			case REDO:
//			case UNDO:
//			case RENUMBER:
//				return this.selectionInfo.getSelectedType() != Verse.class;
//			case REORDER:
//				return this.selectionInfo.getSelectedType() != Verse.class;
//			default:
//				return false;
		}
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
	
	public Bible getBible() {
		return this.bible.get();
	}
	
	public void setBible(Bible bible) {
		this.bible.set(bible);
	}
	
	public ObjectProperty<Bible> bibleProperty() {
		return this.bible;
	}
	
	// internal methods
	
	private CompletableFuture<Void> copy(boolean isCut) {
		if (this.selectionInfo.isSingleTypeSelected() && this.selectionInfo.getSelectedType() != Bible.class) {
			List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
			List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
			String data;
			try {
				data = JsonIO.write(objectData);
				
				List<String> textData = new ArrayList<>();
				
				DataFormat format = null;
				
				Class<?> clazz = this.selectionInfo.getSelectedType();
				if (clazz == Book.class) {
					textData = items.stream().map(b -> ((Book)b.getValue()).getName()).collect(Collectors.toList());
					if (isCut) {
						this.bible.get().getBooks().removeAll(objectData);
					}
					format = BOOK_CLIPBOARD_DATA;
				} else if (clazz == Chapter.class) {
					textData = items.stream().map(c -> ((Chapter)c.getValue()).toString()).collect(Collectors.toList());
					if (isCut) {
						Book book = (Book)items.get(0).getParent().getValue();
						book.getChapters().removeAll(objectData);
					}
					format = CHAPTER_CLIPBOARD_DATA;
				} else if (clazz == Verse.class) {
					textData = items.stream().map(v -> ((Verse)v.getValue()).getText()).collect(Collectors.toList());
					if (isCut) {
						Chapter chapter = (Chapter)items.get(0).getParent().getValue();
						chapter.getVerses().removeAll(objectData);
					}
					format = VERSE_CLIPBOARD_DATA;
				}
				
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(String.join(", ", textData));
				content.put(format, data);
				clipboard.setContent(content);
				
				// handle the selection state changing
				EventHandler<Event> eh = this.onActionStateChanged.get();
				if (eh != null) {
					eh.handle(null);
				}
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> paste() {
		if (this.selectionInfo.getSelectedCount() == 1) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			if (selected.getValue() instanceof Bible && clipboard.hasContent(BOOK_CLIPBOARD_DATA)) {
				try {
					List<Book> books = Arrays.asList(JsonIO.read((String)clipboard.getContent(BOOK_CLIPBOARD_DATA), Book[].class));
					this.bible.get().getBooks().addAll(books);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (selected.getValue() instanceof Book && clipboard.hasContent(CHAPTER_CLIPBOARD_DATA)) {
				try {
					List<Chapter> chapters = Arrays.asList(JsonIO.read((String)clipboard.getContent(CHAPTER_CLIPBOARD_DATA), Chapter[].class));
					((Book)selected.getValue()).getChapters().addAll(chapters);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (selected.getValue() instanceof Chapter && clipboard.hasContent(VERSE_CLIPBOARD_DATA)) {
				try {
					List<Verse> verses = Arrays.asList(JsonIO.read((String)clipboard.getContent(VERSE_CLIPBOARD_DATA), Verse[].class));
					((Chapter)selected.getValue()).getVerses().addAll(verses);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return CompletableFuture.completedFuture(null);
	}
}
