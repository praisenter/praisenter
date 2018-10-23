package org.praisenter.ui.bible;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.async.InOrderExecutionManager;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.data.json.JsonIO;
import org.praisenter.ui.Action;
import org.praisenter.ui.ConfirmationPromptPane;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.SaveAsPromptPane;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;
import org.praisenter.ui.events.ActionPromptPaneCompleteEvent;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
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

// TODO error handling, translations, UI clean up, context menu
// TODO drag n drop scrolling

public final class BibleEditorPane extends BorderPane implements DocumentEditor<Bible> {
	private static final DataFormat BOOK_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Book.class.getName());
	private static final DataFormat CHAPTER_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Chapter.class.getName());
	private static final DataFormat VERSE_CLIPBOARD_DATA = new DataFormat("application/x-praisenter-json-list;class=" + Verse.class.getName());
	
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	// data
	
	private final GlobalContext context;
	private final DocumentContext<Bible> documentContext;

	// helpers
	
	private final Bible bible;
	private final UndoManager undoManager;
	private final InOrderExecutionManager executionManager;
	
	// nodes
	
	private final TreeView<Object> treeView;
	
	public BibleEditorPane(
			GlobalContext context, 
			DocumentContext<Bible> documentContext) {
		this.getStyleClass().add("bible-editor-pane");
		
		this.context = context;
		this.documentContext = documentContext;
		
		// set the helpers
		
		this.bible = documentContext.getDocument();
		this.undoManager = documentContext.getUndoManager();
		this.executionManager = new InOrderExecutionManager();
		
		// the tree
		
		BibleTreeItem root = new BibleTreeItem();
		root.setValue(this.bible);
		
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
			// set the selected items
			documentContext.getSelectedItems().setAll(this.treeView
					.getSelectionModel()
					.getSelectedItems()
					.stream().filter(i -> i != null && i.getValue() != null)
					.map(i -> i.getValue())
					.collect(Collectors.toList()));
		});
		
		documentContext.getUndoManager().undoCountProperty().addListener(this::onUndoStateChanged);
		documentContext.getUndoManager().redoCountProperty().addListener(this::onUndoStateChanged);
		
		ContextMenu menu = new ContextMenu();
		menu.getItems().addAll(
//				menu.createMenuItem(ApplicationAction.NEW_BOOK),
//				menu.createMenuItem(ApplicationAction.NEW_CHAPTER),
//				menu.createMenuItem(ApplicationAction.NEW_VERSE),
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
		
		this.setCenter(this.treeView);
	}
	
	private void onUndoStateChanged(ObservableValue<? extends Number> obs, Number ov, Number nv) {
		this.fireEvent(new ActionStateChangedEvent(this, this, ActionStateChangedEvent.UNDO_REDO));
	}
	
	@Override
	public DocumentContext<Bible> getDocumentContext() {
		return this.documentContext;
	}
	
	private MenuItem createMenuItem(Action action) {
		MenuItem mnu = new MenuItem(Translations.get(action.getMessageKey()));
		if (action.getGraphicSupplier() != null) {
			mnu.setGraphic(action.getGraphicSupplier().get());
		}
		// NOTE: due to bug in JavaFX, we don't apply the accelerator here
		//mnu.setAccelerator(value);
		
		mnu.setOnAction(e -> this.executeAction(action));
		mnu.setUserData(action);
		return mnu;
	}
	
	@Override
	public void setDefaultFocus() {
		this.treeView.requestFocus();
	}
	
//	@Override
//	public void cleanUp() {
//		
//	}
	
	@Override
	public CompletableFuture<Node> executeAction(Action action) {
		switch (action) {
			case COPY:
				return this.copy(false);
			case PASTE:
				return this.paste();
			case CUT:
				return this.copy(true);
			case DELETE:
				return this.delete();
//			case NEW_BOOK:
//			case NEW_CHAPTER:
//			case NEW_VERSE:
//				return this.create(action);
			case REDO:
				return this.redo();
			case UNDO:
				return this.undo();
			case RENUMBER:
				return this.renumber();
			case REORDER:
				return this.reorder();
			case SAVE:
				return this.save().thenApply((a) -> { return null; });
//			case SAVE_AS:
//				return this.saveAs();
			default:
				return CompletableFuture.completedFuture(null);
		}
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		DocumentContext<Bible> ctx = this.documentContext;
		switch (action) {
			case COPY:
				return ctx.isSingleTypeSelected() && ctx.getSelectedType() != Bible.class;
			case CUT:
				return ctx.isSingleTypeSelected() && ctx.getSelectedType() != Bible.class;
			case PASTE:
				return (ctx.getSelectedType() == Bible.class && Clipboard.getSystemClipboard().hasContent(BOOK_CLIPBOARD_DATA)) ||
					   (ctx.getSelectedType() == Book.class && Clipboard.getSystemClipboard().hasContent(CHAPTER_CLIPBOARD_DATA)) ||
					   (ctx.getSelectedType() == Chapter.class && Clipboard.getSystemClipboard().hasContent(VERSE_CLIPBOARD_DATA));
			case DELETE:
				return ctx.getSelectedCount() > 0 && ctx.getSelectedType() != Bible.class;
//			case NEW_BOOK:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Bible.class;
//			case NEW_CHAPTER:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Book.class;
//			case NEW_VERSE:
//				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() == Chapter.class;
			case REDO:
				return ctx.getUndoManager().isRedoAvailable();
			case UNDO:
				return ctx.getUndoManager().isUndoAvailable();
			case RENUMBER:
				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
			case REORDER:
				return ctx.getSelectedCount() == 1 && ctx.getSelectedType() != Verse.class;
//			case SAVE:
//				return true;
//			case SAVE_AS:
//				return true;
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		return true;
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
		List<TreeItem<Object>> selected = new ArrayList<>(this.treeView.getSelectionModel().getSelectedItems());
		this.treeView.getSelectionModel().clearSelection();
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
	
//	private CompletableFuture<Node> create(Action action) {
//		switch (action) {
//			case NEW_BOOK:
//				
//				break;
//			case NEW_CHAPTER:
//				
//				break;
//			case NEW_VERSE:
//				
//				break;
//			default:
//				break;
//		}
//		return AsyncHelper.nil();
//	}
	
	private CompletableFuture<Node> renumber() {
		if (this.documentContext.getSelectedCount() == 1) {
			// capture the item to be renumbered
			final TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				final Object value = selected.getValue();
				if (this.context.getConfiguration().isRenumberBibleWarningEnabled()) {
					ConfirmationPromptPane confirm = new ConfirmationPromptPane();
					confirm.setTitle("Renumber");
					confirm.setMessage("Are you sure you want to renumber?");
					confirm.setShowAskAgain(true);
					confirm.setAskAgain(true);
					confirm.addEventHandler(ActionPromptPaneCompleteEvent.ALL, (e) -> {
						boolean askAgain = confirm.getAskAgain();
						if (!askAgain) {
							this.context.getConfiguration().setRenumberBibleWarningEnabled(false);
							this.context.saveConfiguration();
						}
						this.renumber(e.getEventType() == ActionPromptPaneCompleteEvent.ACCEPT, value);
					});
					return CompletableFuture.completedFuture(confirm);
				} else {
					// just do it
					this.renumber(true, value);
				}
			}
		}
		return AsyncHelper.nil();
	}
	
	private void renumber(boolean accepted, Object selected) {		
		this.treeView.requestFocus();
		if (accepted && selected != null) {
			this.undoManager.beginBatch("Renumber");
			if (selected instanceof Bible) {
				((Bible)selected).renumber();
			} else if (selected instanceof Book) {
				((Book)selected).renumber();
			} else if (selected instanceof Chapter) {
				((Chapter)selected).renumber();
			}
			this.undoManager.completeBatch();
		}
	}
	
	private CompletableFuture<Node> reorder() {
		if (this.documentContext.getSelectedCount() == 1) {
			// capture the item to be renumbered
			final TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				final Object value = selected.getValue();
				if (this.context.getConfiguration().isReorderBibleWarningEnabled()) {
					ConfirmationPromptPane confirm = new ConfirmationPromptPane();
					confirm.setTitle("Renumber");
					confirm.setMessage("Are you sure you want to reorder?");
					confirm.setShowAskAgain(true);
					confirm.setAskAgain(true);
					confirm.addEventHandler(ActionPromptPaneCompleteEvent.ALL, (e) -> {
						boolean askAgain = confirm.getAskAgain();
						if (!askAgain) {
							this.context.getConfiguration().setReorderBibleWarningEnabled(false);
							this.context.saveConfiguration();
						}
						this.reorder(e.getEventType() == ActionPromptPaneCompleteEvent.ACCEPT, value);
					});
					return CompletableFuture.completedFuture(confirm);
				} else {
					// just do it
					this.reorder(true, value);
				}
			}
		}
		return AsyncHelper.nil();
	}
	
	private void reorder(boolean accepted, Object selected) {		
		this.treeView.requestFocus();
		if (accepted && selected != null) {
			this.undoManager.beginBatch("Renumber");
			if (selected instanceof Bible) {
				((Bible)selected).reorder();
			} else if (selected instanceof Book) {
				((Book)selected).reorder();
			} else if (selected instanceof Chapter) {
				((Chapter)selected).reorder();
			}
			this.undoManager.completeBatch();
		}
	}
	
	private CompletableFuture<Void> save() {
		Bible bible = this.bible;
		if (bible != null) {
			// update the modified on
			bible.setModifiedDate(Instant.now());
			// now create a copy to be saved
			final Bible copy = bible.copy();
			final Object position = this.undoManager.storePosition();
			this.executionManager.execute((o) -> {
				System.out.println("Saving " + copy.getModifiedDate());
				BackgroundTask task = new BackgroundTask();
				task.setName("Save " + this.bible.getName());
				task.setMessage("Saving...");
				this.context.addBackgroundTask(task);
				return this.context.getDataManager().update(copy).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
					this.undoManager.markPosition(position);
					task.setProgress(1);
				})).exceptionally((ex) -> {
					task.setException(ex);
					return null;
				});
			});
			
			
//// TODO need to make sure that saves don't interleave
//			this.saveBarrier.thenRun(() -> {
//				BackgroundTask task = new BackgroundTask();
//				task.setName("Save " + this.bible.getName());
//				task.setMessage("Saving...");
//				this.context.addBackgroundTask(task);
//				this.saveBarrier = this.context.getDataManager().update(bible.copy()).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
//					this.undoManager.mark();
//					task.setProgress(1);
//				})).exceptionally((ex) -> {
//					task.setException(ex);
//					return null;
//				});
//			});
			
		}
		return AsyncHelper.nil();
	}

//	private CompletableFuture<Node> saveAs() {
//		Bible bible = this.bible;
//		if (bible != null) {
//			final String name = bible.getName();
//			final UUID id = bible.getId();
//			
//			// prompt for new name
//			SaveAsPromptPane confirm = new SaveAsPromptPane();
//			confirm.setTitle("Save this bible as...");
//			confirm.setMessage("Please choose a new name for the bible");
//			confirm.setName("Copy of " + name);
//			confirm.addEventHandler(ActionPromptPaneCompleteEvent.ACCEPT, e -> {
//				String newName = confirm.getName();
//				bible.setId(UUID.randomUUID());
//				bible.setName(newName);
//				bible.setModifiedDate(Instant.now());
//				final Bible copy = bible.copy();
//				final Object position = this.undoManager.storePosition();
//				this.executionManager.execute((o) -> {
//					return this.context.getDataManager().create(copy).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
//						this.undoManager.markPosition(position);
//					})).exceptionally(t -> {
//						// revert changes
//						bible.setId(id);
//						// another option is to not allow Save As... and the user just needs to copy the document first
//						// NOTE: this will cause another undo to be placed, which kind of sucks
//						// but I don't see a way around this right now, this causes two undos to occur...
//						// make sure the name hasn't changed since we attempted to save
//						// if it hasn't, then we can revert the name
//						if (Objects.equals(bible.getName(), newName)) {
//							bible.setName(name);
//						}
//						return null;
//					}).thenCompose(n -> {
//						return null;
//					});
//				});
//			});
//			return CompletableFuture.completedFuture(confirm);
//		}
//		return AsyncHelper.nil();
//	}
//	
	private ClipboardContent getClipboardContentForSelection(boolean serializeData) throws JsonProcessingException {
		List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
		List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
		
		String data = serializeData ? JsonIO.write(objectData) : "NA";
		List<String> textData = new ArrayList<>();
		DataFormat format = null;
		
		Class<?> clazz = this.documentContext.getSelectedType();
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
		Class<?> clazz = this.documentContext.getSelectedType();
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
		if (this.documentContext.getSelectedCount() == 1) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			try {
				if (selected.getValue() instanceof Bible && clipboard.hasContent(BOOK_CLIPBOARD_DATA)) {
					Book[] books = JsonIO.read((String)clipboard.getContent(BOOK_CLIPBOARD_DATA), Book[].class);
					this.bible.getBooks().addAll(books);
				} else if (selected.getValue() instanceof Book && clipboard.hasContent(CHAPTER_CLIPBOARD_DATA)) {
					Chapter[] chapters = JsonIO.read((String)clipboard.getContent(CHAPTER_CLIPBOARD_DATA), Chapter[].class);
					((Book)selected.getValue()).getChapters().addAll(chapters);
				} else if (selected.getValue() instanceof Chapter && clipboard.hasContent(VERSE_CLIPBOARD_DATA)) {
					Verse[] verses = JsonIO.read((String)clipboard.getContent(VERSE_CLIPBOARD_DATA), Verse[].class);
					((Chapter)selected.getValue()).getVerses().addAll(verses);
				}
				// TODO select the pasted elements
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return AsyncHelper.nil();
	}
	
	private void dragDetected(MouseEvent e) {
		if (this.documentContext.isSingleTypeSelected()) {
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
