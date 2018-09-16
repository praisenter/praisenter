package org.praisenter.ui.bible;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.json.JsonIO;
import org.praisenter.ui.Action;
import org.praisenter.ui.ConfirmationPromptPane;
import org.praisenter.ui.DocumentPane;
import org.praisenter.ui.ReadOnlyPraisenterContext;
import org.praisenter.ui.SelectionInfo;
import org.praisenter.ui.TextInputFieldFieldEventFilter;
import org.praisenter.ui.events.ActionPromptPaneCompleteEvent;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.undo.UndoManager;

import com.fasterxml.jackson.core.JsonProcessingException;

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
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

// TODO error handling, translations, UI clean up, context menu

public class BibleEditorPane extends BorderPane implements DocumentPane {
	private static final DataFormat BOOK_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Book.class.getName());
	private static final DataFormat CHAPTER_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Chapter.class.getName());
	private static final DataFormat VERSE_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Verse.class.getName());
	
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	// data
	
	private final ReadOnlyPraisenterContext context;
	
	private final ObjectProperty<Bible> bible;
	private final BooleanProperty hasUnsavedChanges;
	private final StringProperty documentName;
	
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
		this.getStyleClass().add("bible-editor-pane");
		
		this.context = context;
		
		this.bible = new SimpleObjectProperty<>();
		this.hasUnsavedChanges = new SimpleBooleanProperty();
		this.documentName = new SimpleStringProperty();
		
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
		txtBibleNotes.textProperty().bindBidirectional(this.notes);
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
		
		TextInputFieldFieldEventFilter.applyTextInputFieldEventFilter(
				txtBibleName,
				txtBibleLanguage,
				txtBibleSource,
				txtBibleCopyright,
				txtBibleNotes,
				txtBookName,
				spnBookNumber.getEditor(),
				spnChapterNumber.getEditor(),
				txtVerseText,
				spnVerseNumber.getEditor());
		
		VBox fields = new VBox(
				lblBibleName, txtBibleName,
				lblBibleLanguage, txtBibleLanguage,
				lblBibleSource, txtBibleSource,
				lblBibleCopyright, txtBibleCopyright,
				lblBibleNotes, txtBibleNotes, 
				lblBookNumber, spnBookNumber,
				lblBookName, txtBookName,
				lblChapterNumber, spnChapterNumber,
				lblVerseNumber, spnVerseNumber,
				lblVerseText, txtVerseText);
		fields.setPadding(new Insets(10));
		
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
		this.treeView.getStyleClass().add("bible-editor-pane-tree-view");
		this.treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.treeView.setCellFactory((view) -> {
			BibleTreeCell cell = new BibleTreeCell();
			cell.setOnDragDetected(this::dragDetected);
			cell.setOnDragExited(this::dragExited);
			cell.setOnDragEntered(this::dragEntered);
			cell.setOnDragOver(this::dragOver);
			cell.setOnDragDropped(this::dragDropped);
			cell.setOnDragDone(this::dragDone);
        	return cell;
		});
		
		this.treeView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<Object>> change) -> {
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
			
			// handle the selection state changing
			this.fireEvent(new ActionStateChangedEvent(this, this.treeView, ActionStateChangedEvent.SELECTION));
		});
		
		this.selectionInfo = new SelectionInfo<TreeItem<Object>>(this.treeView.getSelectionModel(), (item) -> {
			Object value = item.getValue();
			return value != null ? value.getClass() : null;
		});
		
		this.undoManager.targetProperty().bind(this.bible);
		this.undoManager.undoCountProperty().addListener(this::onUndoStateChanged);
		this.undoManager.redoCountProperty().addListener(this::onUndoStateChanged);
		
		SplitPane split = new SplitPane(this.treeView, fields);
		split.setDividerPositions(0.75);
		
		this.setCenter(split);
		
	}
	
	private void onUndoStateChanged(ObservableValue<? extends Number> obs, Number ov, Number nv) {
		this.fireEvent(new ActionStateChangedEvent(this, this, ActionStateChangedEvent.UNDO_REDO));
	}
	
	public void printState() {
		this.undoManager.print();
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
	public CompletableFuture<Node> performAction(Action action) {
		switch (action) {
			case COPY:
				return this.copy(false);
			case PASTE:
				return this.paste();
			case CUT:
				return this.copy(true);
			case DELETE:
				return this.delete();
			case NEW_BOOK:
			case NEW_CHAPTER:
			case NEW_VERSE:
				return this.create(action);
			case REDO:
				return this.redo();
			case UNDO:
				return this.undo();
			case RENUMBER:
				return this.renumber();
			case REORDER:
				return this.reorder();
			case SAVE:
				return this.save();
			case SAVE_AS:
				return this.saveAs();
			default:
				return CompletableFuture.completedFuture(null);
		}
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		switch (action) {
			case COPY:
				return this.selectionInfo.isSingleTypeSelected() && this.selectionInfo.getSelectedType() != Bible.class;
			case CUT:
				return this.selectionInfo.isSingleTypeSelected() && this.selectionInfo.getSelectedType() != Bible.class;
			case PASTE:
				return (this.selectionInfo.getSelectedType() == Bible.class && Clipboard.getSystemClipboard().hasContent(BOOK_CLIPBOARD_DATA)) ||
					   (this.selectionInfo.getSelectedType() == Book.class && Clipboard.getSystemClipboard().hasContent(CHAPTER_CLIPBOARD_DATA)) ||
					   (this.selectionInfo.getSelectedType() == Chapter.class && Clipboard.getSystemClipboard().hasContent(VERSE_CLIPBOARD_DATA));
			case DELETE:
				return this.selectionInfo.getSelectedCount() > 0 && this.selectionInfo.getSelectedType() != Bible.class;
			case NEW_BOOK:
				return this.selectionInfo.getSelectedCount() == 1 && this.selectionInfo.getSelectedType() == Bible.class;
			case NEW_CHAPTER:
				return this.selectionInfo.getSelectedCount() == 1 && this.selectionInfo.getSelectedType() == Book.class;
			case NEW_VERSE:
				return this.selectionInfo.getSelectedCount() == 1 && this.selectionInfo.getSelectedType() == Chapter.class;
			case REDO:
				return this.undoManager.isRedoAvailable();
			case UNDO:
				return this.undoManager.isUndoAvailable();
			case RENUMBER:
				return this.selectionInfo.getSelectedCount() == 1 && this.selectionInfo.getSelectedType() != Verse.class;
			case REORDER:
				return this.selectionInfo.getSelectedCount() == 1 && this.selectionInfo.getSelectedType() != Verse.class;
			case SAVE:
				return true;
			case SAVE_AS:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return true;
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

	private CompletableFuture<Node> undo() {
		this.undoManager.undo();
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> redo() {
		this.undoManager.redo();
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> delete() {
		List<TreeItem<Object>> selected = this.treeView.getSelectionModel().getSelectedItems();
		this.undoManager.beginBatch("Delete");
		for (TreeItem<Object> item : selected) {
			Object value = item.getValue();
			TreeItem<Object> parentItem = item.getParent();
			if (parentItem != null) {
				Object parent = parentItem.getValue();
				if (parent != null) {
					if (parent instanceof Bible) {
						((Bible)parent).getBooks().remove(value);
					} else if (parent instanceof Book) {
						((Book)parent).getChapters().remove(value);
					} else if (parent instanceof Chapter) {
						((Chapter)parent).getVerses().remove(value);
					}
				}
			}
		}
		this.undoManager.completeBatch();
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> create(Action action) {
		switch (action) {
			case NEW_BOOK:
				
				break;
			case NEW_CHAPTER:
				
				break;
			case NEW_VERSE:
				
				break;
			default:
				break;
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> renumber() {
		if (this.selectionInfo.getSelectedCount() == 1) {
			// capture the item to be renumbered
			final TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			// TODO check if we need to ask based on config value
			ConfirmationPromptPane confirm = new ConfirmationPromptPane();
			confirm.setTitle("Renumber");
			confirm.setMessage("Are you sure you want to renumber?");
			confirm.addEventHandler(ActionPromptPaneCompleteEvent.ALL, (e) -> {
				if (confirm.getAskAgain()) {
					// TODO update config based on askagain value
				}
				this.treeView.requestFocus();
				if (e.getEventType() == ActionPromptPaneCompleteEvent.ACCEPT) {
					if (selected != null) {
						Object value = selected.getValue();
						if (value != null) {
							this.undoManager.beginBatch("Renumber");
							if (value instanceof Bible) {
								((Bible)value).renumber();
							} else if (value instanceof Book) {
								((Book)value).renumber();
							} else if (value instanceof Chapter) {
								((Chapter)value).renumber();
							}
							this.undoManager.completeBatch();
						}
					}
				}
			});
			return CompletableFuture.completedFuture(confirm);
			
			
//			Alert dialog = new Alert(AlertType.CONFIRMATION);
//			dialog.setTitle("Renumber");
//			dialog.setHeaderText("Reunumber this item");
//			dialog.setContentText("Are you sure you want to renumber?");
//			dialog.initOwner(this.getScene().getWindow());
//			dialog.initModality(Modality.WINDOW_MODAL);
//			
//			Optional<ButtonType> result = dialog.showAndWait();
//			if (result.isPresent() && result.get() == ButtonType.OK) {
//				TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
//				if (selected != null) {
//					Object value = selected.getValue();
//					if (value != null) {
//						this.undoManager.beginBatch("Renumber");
//						if (value instanceof Bible) {
//							((Bible)value).renumber();;
//						} else if (value instanceof Book) {
//							((Book)value).renumber();;
//						} else if (value instanceof Chapter) {
//							((Chapter)value).renumber();;
//						}
//						this.undoManager.completeBatch();
//					}
//				}
//			}
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> reorder() {
		if (this.selectionInfo.getSelectedCount() == 1) {
			Alert dialog = new Alert(AlertType.CONFIRMATION);
			dialog.setTitle("Reorder");
			dialog.setHeaderText("Reorder this item");
			dialog.setContentText("Are you sure you want to reorder?");
			dialog.initOwner(this.getScene().getWindow());
			dialog.initModality(Modality.WINDOW_MODAL);
			
			Optional<ButtonType> result = dialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
				if (selected != null) {
					Object value = selected.getValue();
					if (value != null) {
						this.undoManager.beginBatch("Reorder");
						if (value instanceof Bible) {
							((Bible)value).reorder();
						} else if (value instanceof Book) {
							((Book)value).reorder();
						} else if (value instanceof Chapter) {
							((Chapter)value).reorder();
						}
						this.undoManager.completeBatch();
					}
				}
			}
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> save() {
		Bible bible = this.bible.get();
		if (bible != null) {
			return this.context.getDataManager().update(bible.copy()).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
				this.undoManager.mark();
			})).thenCompose(n -> {
				return null;
			});
		}
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> saveAs() {
		Bible bible = this.bible.get();
		if (bible != null) {
			// prompt for new name
			TextInputDialog dialog = new TextInputDialog("Copy of " + bible.getName());
			dialog.setContentText("Name");
			dialog.setHeaderText("Please supply a name for the bible");
			dialog.setTitle("Save As");
			dialog.initOwner(this.getScene().getWindow());
			dialog.initModality(Modality.WINDOW_MODAL);
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String name = result.get();
				bible.setId(UUID.randomUUID());
				bible.setName(name);
				return this.context.getDataManager().create(bible.copy()).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
					this.undoManager.mark();
				})).thenCompose(n -> {
					return null;
				});
			}
		}
		return AsyncHelper.nil();
	}
	
	private ClipboardContent getClipboardContentForSelection(boolean serializeData) throws JsonProcessingException {
		List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
		List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
		
		String data = serializeData ? JsonIO.write(objectData) : "NA";
		List<String> textData = new ArrayList<>();
		DataFormat format = null;
		
		Class<?> clazz = this.selectionInfo.getSelectedType();
		if (clazz == Book.class) {
			format = BOOK_CLIPBOARD_DATA;
			textData = items.stream().map(b -> ((Book)b.getValue()).getName()).collect(Collectors.toList());
		} else if (clazz == Chapter.class) {
			format = CHAPTER_CLIPBOARD_DATA;
			textData = items.stream().map(c -> ((Chapter)c.getValue()).toString()).collect(Collectors.toList());
		} else if (clazz == Verse.class) {
			format = VERSE_CLIPBOARD_DATA;
			textData = items.stream().map(v -> ((Verse)v.getValue()).getText()).collect(Collectors.toList());
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(String.join(Constants.NEW_LINE, textData));
		content.put(format, data);
		
		return content;
	}
	
	private CompletableFuture<Node> copy(boolean isCut) {
		Class<?> clazz = this.selectionInfo.getSelectedType();
		if (clazz != null && clazz != Bible.class) {
			List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
			List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
			try {
				ClipboardContent content = this.getClipboardContentForSelection(true);
				Clipboard clipboard = Clipboard.getSystemClipboard();
				clipboard.setContent(content);
				
				if (isCut) {
					Object parent = items.get(0).getParent().getValue();
					if (clazz == Book.class) {
						((Bible)parent).getBooks().removeAll(objectData);
					} else if (clazz == Chapter.class) {
						((Book)parent).getChapters().removeAll(objectData);
					} else if (clazz == Verse.class) {
						((Chapter)parent).getVerses().removeAll(objectData);
					}
				}
				
				// handle the selection state changing
				this.fireEvent(new ActionStateChangedEvent(this, this.treeView, ActionStateChangedEvent.CLIPBOARD));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return AsyncHelper.nil();
	}
	
	private CompletableFuture<Node> paste() {
		if (this.selectionInfo.getSelectedCount() == 1) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			try {
				if (selected.getValue() instanceof Bible && clipboard.hasContent(BOOK_CLIPBOARD_DATA)) {
					Book[] books = JsonIO.read((String)clipboard.getContent(BOOK_CLIPBOARD_DATA), Book[].class);
					this.bible.get().getBooks().addAll(books);
				} else if (selected.getValue() instanceof Book && clipboard.hasContent(CHAPTER_CLIPBOARD_DATA)) {
					Chapter[] chapters = JsonIO.read((String)clipboard.getContent(CHAPTER_CLIPBOARD_DATA), Chapter[].class);
					((Book)selected.getValue()).getChapters().addAll(chapters);
				} else if (selected.getValue() instanceof Chapter && clipboard.hasContent(VERSE_CLIPBOARD_DATA)) {
					Verse[] verses = JsonIO.read((String)clipboard.getContent(VERSE_CLIPBOARD_DATA), Verse[].class);
					((Chapter)selected.getValue()).getVerses().addAll(verses);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return AsyncHelper.nil();
	}
	
	private void dragDetected(MouseEvent e) {
		if (this.selectionInfo.isSingleTypeSelected()) {
			try {
				Dragboard db = ((Node)e.getSource()).startDragAndDrop(TransferMode.COPY_OR_MOVE);
				ClipboardContent content = this.getClipboardContentForSelection(false);
				db.setContent(content);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void dragExited(DragEvent e) {
		if (e.getSource() instanceof BibleTreeCell) {
			BibleTreeCell cell = (BibleTreeCell)e.getSource();
			cell.pseudoClassStateChanged(DRAG_OVER_PARENT, false);
			cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
			cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
		}
	}
	
	private void dragEntered(DragEvent e) {
		// nothing to do here
	}
	
	private void dragOver(DragEvent e) {
		if (!(e.getSource() instanceof BibleTreeCell)) {
			return;
		}
		
		// don't allow drop onto itself
		BibleTreeCell cell = (BibleTreeCell)e.getSource();
		TreeItem<Object> item = cell.getTreeItem();
		if (this.treeView.getSelectionModel().getSelectedItems().contains(item)) {
			return;
		}
		
		// check for null data
		Object data = item.getValue();
		if (data == null) {
			return;
		}
		
		// don't allow drop onto incorrect locations
		boolean dragBooks = e.getDragboard().hasContent(BOOK_CLIPBOARD_DATA);
		boolean dragChapters = e.getDragboard().hasContent(CHAPTER_CLIPBOARD_DATA);
		boolean dragVerses = e.getDragboard().hasContent(VERSE_CLIPBOARD_DATA);
		
		boolean targetIsBible = data instanceof Bible;
		boolean targetIsBook = data instanceof Book;
		boolean targetIsChapter = data instanceof Chapter;
		boolean targetIsVerse = data instanceof Verse;
		
		boolean isAllowed = 
				(dragBooks && targetIsBible) ||
				(dragBooks && targetIsBook) ||
				(dragChapters && targetIsBook) ||
				(dragChapters && targetIsChapter) ||
				(dragVerses && targetIsChapter) ||
				(dragVerses && targetIsVerse);
		
		if (!isAllowed) {
			return;
		}
		
		// allow the transfer
		e.acceptTransferModes(TransferMode.MOVE);
		
		boolean isParent = 
				(dragBooks && targetIsBible) ||
				(dragChapters && targetIsBook) ||
				(dragVerses && targetIsChapter);

		if (isParent) {
			cell.pseudoClassStateChanged(DRAG_OVER_PARENT, true);
		} else {
			if (e.getY() < cell.getHeight() * 0.75) {
				cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, true);
				cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
			} else {
				cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, true);
				cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
			}
		}
	}
	
	private void dragDropped(DragEvent e) {
		// make sure the target is a valid target
		if (!(e.getGestureTarget() instanceof BibleTreeCell)) {
			return;
		}
		
		// copy the selected items
		List<TreeItem<Object>> selected = new ArrayList<>(this.treeView.getSelectionModel().getSelectedItems());

		// check for null data
		BibleTreeCell target = (BibleTreeCell)e.getGestureTarget();
		TreeItem<Object> targetItem = target.getTreeItem();
		Object targetValue = targetItem.getValue();
		
		// are we dragging to a parent node?
		boolean dragBooks = e.getDragboard().hasContent(BOOK_CLIPBOARD_DATA);
		boolean dragChapters = e.getDragboard().hasContent(CHAPTER_CLIPBOARD_DATA);
		boolean dragVerses = e.getDragboard().hasContent(VERSE_CLIPBOARD_DATA);
		
		boolean targetIsBible = targetValue instanceof Bible;
		boolean targetIsBook = targetValue instanceof Book;
		boolean targetIsChapter = targetValue instanceof Chapter;
		
		boolean isParent = 
				(dragBooks && targetIsBible) ||
				(dragChapters && targetIsBook) ||
				(dragVerses && targetIsChapter);

		this.undoManager.beginBatch("DragDrop");
		
		// remove the data from its previous location
		List<Object> items = new ArrayList<>();
		for (TreeItem<Object> item : selected) {
			Object child = item.getValue();
			Object parent = item.getParent().getValue();
			if (child instanceof Verse) {
				((Chapter)parent).getVerses().remove(child);
			} else if (child instanceof Chapter) {
				((Book)parent).getChapters().remove(child);
			} else if (child instanceof Book) {
				((Bible)parent).getBooks().remove(child);
			}
			items.add(child);
		}
		
		// now add the data
		Object parent = isParent ? targetValue : targetItem.getParent().getValue();
		int index = isParent ? targetItem.getChildren().size() : targetItem.getParent().getChildren().indexOf(targetItem);
		boolean after = e.getY() >= target.getHeight() * 0.75;
		if (!isParent && after) index++;
		
		if (dragBooks) {
			((Bible)parent).getBooks().addAll(index, items.stream().map(i -> (Book)i).collect(Collectors.toList()));
		} else if (dragChapters) {
			((Book)parent).getChapters().addAll(index, items.stream().map(i -> (Chapter)i).collect(Collectors.toList()));
		} else if (dragVerses) {
			((Chapter)parent).getVerses().addAll(index, items.stream().map(i -> (Verse)i).collect(Collectors.toList()));
		}
		
		this.undoManager.completeBatch();
		
		e.setDropCompleted(true);
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
}
