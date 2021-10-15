package org.praisenter.ui.bible;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.json.JsonIO;
import org.praisenter.ui.Action;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

//JAVABUG (L) 11/03/16 Dragging to the edge of a scrollable window doesn't scroll it and there's no good way to scroll it manually

public final class BibleEditor extends BorderPane implements DocumentEditor<Bible> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String BIBLE_EDITOR_CSS = "p-bible-editor";
	private static final String BIBLE_EDITOR_BULK_CSS = "p-bible-editor-bulk";
	private static final String BIBLE_EDITOR_BULK_BUTTONS_CSS = "p-bible-editor-bulk-buttons";
	
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	// data
	
	private final GlobalContext context;
	private final DocumentContext<Bible> document;

	// helpers
	
	private final Bible bible;
	private final UndoManager undoManager;
	
	// nodes
	
	private final TreeView<Object> treeView;
	
	private final StringProperty bulkEditModeValue;
	private final StringProperty bulkEditModeError;
	
	public BibleEditor(
			GlobalContext context, 
			DocumentContext<Bible> document) {
		this.getStyleClass().add(BIBLE_EDITOR_CSS);
		
		this.context = context;
		this.document = document;
		
		// set the helpers
		
		this.bible = document.getDocument();
		this.undoManager = document.getUndoManager();
		
		this.bulkEditModeValue = new SimpleStringProperty();
		this.bulkEditModeError = new SimpleStringProperty();
		
		// the tree
		
		BibleTreeItem root = new BibleTreeItem();
		root.setValue(this.bible);
		
		this.treeView = new TreeView<Object>(root);
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
			// set the selected items
			document.getSelectedItems().setAll(this.treeView
					.getSelectionModel()
					.getSelectedItems()
					.stream().filter(i -> i != null && i.getValue() != null)
					.map(i -> i.getValue())
					.collect(Collectors.toList()));
		});

		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(
				this.createMenuItem(Action.BULK_EDIT_BEGIN),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.NEW_BOOK),
				this.createMenuItem(Action.NEW_CHAPTER),
				this.createMenuItem(Action.NEW_VERSE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.COPY),
				this.createMenuItem(Action.CUT),
				this.createMenuItem(Action.PASTE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.REORDER),
				this.createMenuItem(Action.RENUMBER),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.DELETE)
			);
		this.treeView.setContextMenu(menu);
		
		// when the menu is shown, update the enabled/disable state
		menu.showingProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				// update the enable state
				for (MenuItem mnu : menu.getItems()) {
					Action action = (Action)mnu.getUserData();
					if (action != null) {
						boolean isEnabled = this.isActionEnabled(action);
						mnu.setDisable(!isEnabled);
					}
				}
			}
		});
		
		// build the bulk edit UI
		TextArea textArea = new TextArea();
		textArea.setWrapText(false);
		textArea.textProperty().bindBidirectional(this.bulkEditModeValue);
		Button btnOk = new Button(Translations.get("ok"));
		btnOk.minWidthProperty().bind(btnOk.prefWidthProperty());
		Button btnCancel = new Button(Translations.get("cancel"));
		btnCancel.minWidthProperty().bind(btnCancel.prefWidthProperty());
		Label lblError = new Label();
		lblError.getStyleClass().add("error-label");
		lblError.setMaxWidth(Double.MAX_VALUE);
		lblError.textProperty().bind(this.bulkEditModeError);
		lblError.visibleProperty().bind(this.bulkEditModeError.length().greaterThan(0));
		
		HBox bulkEditorButtons = new HBox(lblError, btnOk, btnCancel);
		bulkEditorButtons.getStyleClass().add(BIBLE_EDITOR_BULK_BUTTONS_CSS);
		HBox.setHgrow(lblError, Priority.ALWAYS);
		
		VBox bulkEditor = new VBox(
				textArea,
				bulkEditorButtons);
		bulkEditor.getStyleClass().add(BIBLE_EDITOR_BULK_CSS);
		
		VBox.setVgrow(textArea, Priority.ALWAYS);
		
		bulkEditor.visibleProperty().bind(document.bulkEditProperty());
		this.treeView.visibleProperty().bind(document.bulkEditProperty().not());
		
		StackPane editorStack = new StackPane(this.treeView, bulkEditor);
		
		btnOk.setOnAction(e -> {
			try {
				this.processBulkEdit();
				document.setBulkEdit(false);
				this.bulkEditModeValue.set(null);
				this.bulkEditModeError.set(null);
			} catch (Exception ex) {
				this.bulkEditModeError.set(ex.getMessage());
			}
		});
		
		btnCancel.setOnAction(e -> {
			document.setBulkEdit(false);
			this.bulkEditModeValue.set(null);
			this.bulkEditModeError.set(null);
		});
		
		this.setCenter(editorStack);
	}
	
	private MenuItem createMenuItem(Action action) {
		MenuItem mnu = new MenuItem(Translations.get(action.getMessageKey()));
		if (action.getGraphicSupplier() != null) {
			mnu.setGraphic(action.getGraphicSupplier().get());
		}
		// JAVABUG (L) 09/19/2021 [workaround] Multiple accelerators have odd behavior https://bugs.openjdk.java.net/browse/JDK-8088068
//		mnu.setAccelerator(action.getAccelerator());
		mnu.setOnAction(e -> this.executeAction(action));
		mnu.setUserData(action);
		return mnu;
	}
	
	@Override
	public DocumentContext<Bible> getDocumentContext() {
		return this.document;
	}
	
	@Override
	public void setDefaultFocus() {
		this.treeView.requestFocus();
	}
	
	@Override
	public CompletableFuture<Void> executeAction(Action action) {
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
			case RENUMBER:
				return this.renumber();
			case REORDER:
				return this.reorder();
			case BULK_EDIT_BEGIN:
				return this.beginBulkEdit();
			default:
				return CompletableFuture.completedFuture(null);
		}
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		DocumentContext<Bible> ctx = this.document;
		switch (action) {
			case COPY:
				return ctx.isSingleTypeSelected() && ctx.getSelectedType() != Bible.class;
			case CUT:
				return ctx.isSingleTypeSelected() && ctx.getSelectedType() != Bible.class;
			case PASTE:
				return ((ctx.getSelectedType() == Bible.class || ctx.getSelectedType() == Book.class) && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_BOOK_ARRAY)) ||
					   ((ctx.getSelectedType() == Book.class || ctx.getSelectedType() == Chapter.class) && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_CHAPTER_ARRAY)) ||
					   ((ctx.getSelectedType() == Chapter.class || ctx.getSelectedType() == Verse.class) && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_VERSE_ARRAY));
			case DELETE:
				return ctx.getSelectedCount() > 0 && ctx.getSelectedType() != Bible.class;
			case NEW_BOOK:
				return ctx.getSelectedCount() == 1 && (ctx.getSelectedType() == Bible.class || ctx.getSelectedType() == Book.class);
			case NEW_CHAPTER:
				return ctx.getSelectedCount() == 1 && (ctx.getSelectedType() == Book.class || ctx.getSelectedType() == Chapter.class);
			case NEW_VERSE:
				return ctx.getSelectedCount() == 1 && (ctx.getSelectedType() == Chapter.class || ctx.getSelectedType() == Verse.class);
			case REDO:
				return ctx.getUndoManager().isRedoAvailable();
			case UNDO:
				return ctx.getUndoManager().isUndoAvailable();
			case RENUMBER:
				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
			case REORDER:
				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
			case BULK_EDIT_BEGIN:
				return ctx.getSelectedCount() == 1 && (ctx.getSelectedType() == Book.class || ctx.getSelectedType() == Chapter.class);
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		// specifically show these actions
		switch (action) {
			case NEW_BOOK:
			case NEW_CHAPTER:
			case NEW_VERSE:
			case RENUMBER:
			case REORDER:
			case BULK_EDIT_BEGIN:
				return true;
			default:
				return false;
		}
	}
	
	// internal methods

	private CompletableFuture<Void> beginBulkEdit() {
		Object selection = this.document.getSelectedItem();
		Class<?> clazz = this.document.getSelectedType();
		
		if (clazz == Book.class) {
			BulkEditConverter<Book> tx = new BookBulkEditConverter();
			this.bulkEditModeValue.set(tx.toString((Book)selection));
			this.document.setBulkEdit(true);
		} else if (clazz == Chapter.class) {
			BulkEditConverter<Chapter> tx = new ChapterBulkEditConverter();
			this.bulkEditModeValue.set(tx.toString((Chapter)selection));
			this.document.setBulkEdit(true);
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private void processBulkEdit() throws BulkEditParseException {
		Object selection = this.document.getSelectedItem();
		Class<?> clazz = this.document.getSelectedType();
		String result = this.bulkEditModeValue.get();
		
		if (clazz == Book.class) {
			BulkEditConverter<Book> tx = new BookBulkEditConverter();
			Book book = tx.fromString(result);
			Bible bible = this.bible;
			int index = bible.getBooks().indexOf(selection);
			if (index >= 0) {
				bible.getBooks().set(index, book);
			}
		} else if (clazz == Chapter.class) {
			BulkEditConverter<Chapter> tx = new ChapterBulkEditConverter();
			Chapter chapter = tx.fromString(result);
			Bible bible = this.bible;
			for (Book book : bible.getBooks()) {
				int index = book.getChapters().indexOf(selection);
				if (index >= 0) {
					book.getChapters().set(index, chapter);
					break;
				}
			}
		}
	}
	
	private CompletableFuture<Void> delete() {
		List<TreeItem<Object>> selected = new ArrayList<>(this.treeView.getSelectionModel().getSelectedItems());
		this.treeView.getSelectionModel().clearSelection();
		this.undoManager.beginBatch("Delete");
		try {
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
		} catch (Exception ex) {
			LOGGER.error("Failed to delete the selected items", ex);
			this.undoManager.discardBatch();
			
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getClosest(TreeItem<Object> item, Class<T> clazz) {
		if (item == null) return null;
		
		Object value = item.getValue();
		if (value != null && value.getClass() == clazz) {
			return (T)value;
		}
		
		return this.getClosest(item.getParent(), clazz);
	}
	
	private CompletableFuture<Void> create(Action action) {
		TreeItem<Object> item = this.treeView.getSelectionModel().getSelectedItem();
		
		Book book = this.getClosest(item, Book.class);
		Chapter chapter = this.getClosest(item, Chapter.class);
		
		
		switch (action) {
			case NEW_BOOK:
				int number = this.bible.getMaxBookNumber() + 1;
				this.bible.getBooks().add(new Book(number, Translations.get("action.new.bible.book")));
				break;
			case NEW_CHAPTER:
				if (book != null) {
					int n = book.getMaxChapterNumber();
					book.getChapters().add(new Chapter(n));
				}
				break;
			case NEW_VERSE:
				if (chapter != null) {
					int n = chapter.getMaxVerseNumber();
					chapter.getVerses().add(new Verse(n, Translations.get("action.new.bible.verse")));
				}
				break;
			default:
				break;
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> renumber() {
		if (this.document.getSelectedCount() == 1) {
			// capture the item to be renumbered
			final TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				final Object value = selected.getValue();
				if (this.context.getWorkspaceConfiguration().isRenumberBibleWarningEnabled()) {
					Alert alert = Dialogs.confirmWithOptOut(
							this.context.getStage(), 
							Modality.WINDOW_MODAL, 
							AlertType.CONFIRMATION, 
							Translations.get("action.renumber"), 
							Translations.get("action.confirm"), 
							Translations.get("bible.editor.renumber.description"), 
							Translations.get("action.confirm.optout"), 
							(optOut) -> {
								if (optOut) {
									this.context.getWorkspaceConfiguration().setRenumberBibleWarningEnabled(false);
								}
							});
					
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {
						this.renumber(true, value);
					}
					
					return CompletableFuture.completedFuture(null);
				} else {
					// just do it
					this.renumber(true, value);
				}
			}
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private void renumber(boolean accepted, Object selected) {		
		this.treeView.requestFocus();
		if (accepted && selected != null) {
			this.undoManager.beginBatch("Renumber");
			try {
				if (selected instanceof Bible) {
					((Bible)selected).renumber();
				} else if (selected instanceof Book) {
					((Book)selected).renumber();
				} else if (selected instanceof Chapter) {
					((Chapter)selected).renumber();
				}
				this.undoManager.completeBatch();
			} catch (Exception ex) {
				LOGGER.error("Failed to renumber", ex);
				this.undoManager.discardBatch();
			}
		}
	}
	
	private CompletableFuture<Void> reorder() {
		if (this.document.getSelectedCount() == 1) {
			// capture the item to be renumbered
			final TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				final Object value = selected.getValue();
				if (this.context.getWorkspaceConfiguration().isReorderBibleWarningEnabled()) {
					Alert alert = Dialogs.confirmWithOptOut(
							this.context.getStage(), 
							Modality.WINDOW_MODAL, 
							AlertType.CONFIRMATION, 
							Translations.get("action.reorder"), 
							Translations.get("action.confirm"), 
							Translations.get("bible.editor.reorder.description"), 
							Translations.get("action.confirm.optout"),
							(optOut) -> {
								if (optOut) {
									this.context.getWorkspaceConfiguration().setReorderBibleWarningEnabled(false);
								}
							});
					
					Optional<ButtonType> result = alert.showAndWait();
					if (result.isPresent() && result.get() == ButtonType.OK) {
						this.reorder(true, value);
					}
					
					return CompletableFuture.completedFuture(null);
				} else {
					// just do it
					this.reorder(true, value);
				}
			}
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private void reorder(boolean accepted, Object selected) {		
		this.treeView.requestFocus();
		if (accepted && selected != null) {
			this.undoManager.beginBatch("Renumber");
			try {
				if (selected instanceof Bible) {
					((Bible)selected).reorder();
				} else if (selected instanceof Book) {
					((Book)selected).reorder();
				} else if (selected instanceof Chapter) {
					((Chapter)selected).reorder();
				}
				this.undoManager.completeBatch();
			} catch (Exception ex) {
				LOGGER.error("Failed to reorder", ex);
				this.undoManager.discardBatch();
			}
		}
	}
	
	private ClipboardContent getClipboardContentForSelection(boolean serializeData) throws Exception {
		List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
		List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
		
		// in the case of Drag n' Drop, we don't need to serialize it
		String data = serializeData ? JsonIO.write(objectData) : "NA";
		List<String> textData = new ArrayList<>();
		DataFormat format = null;
		
		Class<?> clazz = this.document.getSelectedType();
		if (clazz == Book.class) {
			format = DataFormats.PRAISENTER_BOOK_ARRAY;
			textData = items.stream().map(b -> ((Book)b.getValue()).getName()).collect(Collectors.toList());
		} else if (clazz == Chapter.class) {
			format = DataFormats.PRAISENTER_CHAPTER_ARRAY;
			textData = items.stream().map(c -> ((Chapter)c.getValue()).toString()).collect(Collectors.toList());
		} else if (clazz == Verse.class) {
			format = DataFormats.PRAISENTER_VERSE_ARRAY;
			textData = items.stream().map(v -> ((Verse)v.getValue()).getText()).collect(Collectors.toList());
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(String.join(Constants.NEW_LINE, textData));
		content.put(format, data);
		
		return content;
	}
	
	private CompletableFuture<Void> copy(boolean isCut) {
		Class<?> clazz = this.document.getSelectedType();
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
			} catch (Exception ex) {
				LOGGER.warn("Failed to create ClipboardContent for current selection (copy/cut)", ex);
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private CompletableFuture<Void> paste() {
		TreeItem<Object> item = this.treeView.getSelectionModel().getSelectedItem();
		if (item != null) {
			Object value = item.getValue();
			
			Book book = this.getClosest(item, Book.class);
			Chapter chapter = this.getClosest(item, Chapter.class);
			
			Clipboard clipboard = Clipboard.getSystemClipboard();
			try {
				if ((value instanceof Bible || value instanceof Book) && clipboard.hasContent(DataFormats.PRAISENTER_BOOK_ARRAY)) {
					Book[] books = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_BOOK_ARRAY), Book[].class);
					this.bible.getBooks().addAll(books);
				} else if ((value instanceof Book || value instanceof Chapter) && clipboard.hasContent(DataFormats.PRAISENTER_CHAPTER_ARRAY)) {
					Chapter[] chapters = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_CHAPTER_ARRAY), Chapter[].class);
					book.getChapters().addAll(chapters);
				} else if ((value instanceof Chapter || value instanceof Verse) && clipboard.hasContent(DataFormats.PRAISENTER_VERSE_ARRAY)) {
					Verse[] verses = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_VERSE_ARRAY), Verse[].class);
					chapter.getVerses().addAll(verses);
				}
				// TODO select the pasted elements
			} catch (Exception ex) {
				LOGGER.warn("Failed to paste clipboard content (likely due to a JSON deserialization error", ex);
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private void dragDetected(MouseEvent e) {
		if (this.document.isSingleTypeSelected()) {
			try {
				Dragboard db = ((Node)e.getSource()).startDragAndDrop(TransferMode.COPY_OR_MOVE);
				ClipboardContent content = this.getClipboardContentForSelection(false);
				db.setContent(content);
			} catch (Exception ex) {
				LOGGER.warn("Failed to create ClipboardContent for current selection (drag detected)", ex);
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

		// handle null item (happens when you drag onto blank area)
		if (item == null) {
			return;
		}
		
		// check for null data
		Object data = item.getValue();
		if (data == null) {
			return;
		}
		
		// don't allow drop onto incorrect locations
		boolean dragBooks = e.getDragboard().hasContent(DataFormats.PRAISENTER_BOOK_ARRAY);
		boolean dragChapters = e.getDragboard().hasContent(DataFormats.PRAISENTER_CHAPTER_ARRAY);
		boolean dragVerses = e.getDragboard().hasContent(DataFormats.PRAISENTER_VERSE_ARRAY);
		
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
		boolean dragBooks = e.getDragboard().hasContent(DataFormats.PRAISENTER_BOOK_ARRAY);
		boolean dragChapters = e.getDragboard().hasContent(DataFormats.PRAISENTER_CHAPTER_ARRAY);
		boolean dragVerses = e.getDragboard().hasContent(DataFormats.PRAISENTER_VERSE_ARRAY);
		
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
		int size = targetItem.getChildren().size();
		int index = isParent ? size : targetItem.getParent().getChildren().indexOf(targetItem);
		boolean after = e.getY() >= target.getHeight() * 0.75;
		if (!isParent && after) index++;
		
		if (dragBooks) {
			((Bible)parent).getBooks().addAll(index, items.stream().map(i -> (Book)i).collect(Collectors.toList()));
		} else if (dragChapters) {
			((Book)parent).getChapters().addAll(index, items.stream().map(i -> (Chapter)i).collect(Collectors.toList()));
		} else if (dragVerses) {
			((Chapter)parent).getVerses().addAll(index, items.stream().map(i -> (Verse)i).collect(Collectors.toList()));
		}
		
		int row = (isParent && size > 0 
				? this.treeView.getRow(targetItem.getChildren().get(size - 1))
				: this.treeView.getRow(targetItem)) 
				+ (!isParent && after ? 1 : -items.size());
		this.treeView.getSelectionModel().clearSelection();
		this.treeView.getSelectionModel().selectRange(row, row + items.size());
		
		this.undoManager.completeBatch();
		
		e.setDropCompleted(true);
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
}
