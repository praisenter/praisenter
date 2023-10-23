package org.praisenter.ui.song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongBook;
import org.praisenter.ui.Action;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.ui.DataFormats;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;
import org.praisenter.ui.events.ActionStateChangedEvent;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.undo.UndoManager;
import org.praisenter.utility.MimeType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.stage.FileChooser;

//FEATURE (L-L) Implement import from PDF for song lyrics
//JAVABUG (L) 11/03/16 Dragging to the edge of a scrollable window doesn't scroll it and there's no good way to scroll it manually

public final class SongEditor extends BorderPane implements DocumentEditor<Song> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SONG_EDITOR_CSS = "p-song-editor";
	private static final String SONG_EDITOR_BULK_CSS = "p-song-editor-bulk";
	private static final String SONG_EDITOR_BULK_BUTTONS_CSS = "p-song-editor-bulk-buttons";
	
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	// data
	
	private final GlobalContext context;
	private final DocumentContext<Song> document;

	// helpers
	
	private final Song song;
	private final UndoManager undoManager;
	
	// nodes
	
	private final TreeView<Object> treeView;
	
	private final StringProperty bulkEditModeValue;
	private final StringProperty bulkEditModeError;
	
	public SongEditor(
			GlobalContext context, 
			DocumentContext<Song> document) {
		this.getStyleClass().add(SONG_EDITOR_CSS);
		
		this.context = context;
		this.document = document;
		
		// set the helpers
		
		this.song = document.getDocument();
		this.undoManager = document.getUndoManager();
		
		this.bulkEditModeValue = new SimpleStringProperty();
		this.bulkEditModeError = new SimpleStringProperty();
		
		// the tree
		
		SongTreeItem root = new SongTreeItem();
		root.setValue(this.song);
		
		this.treeView = new TreeView<Object>(root);
		this.treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.treeView.setCellFactory((view) -> {
			SongTreeCell cell = new SongTreeCell();
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
				this.createMenuItem(Action.NEW_LYRICS),
				this.createMenuItem(Action.NEW_AUTHOR),
				this.createMenuItem(Action.NEW_SONGBOOK),
				this.createMenuItem(Action.NEW_SECTION),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.COPY),
				this.createMenuItem(Action.CUT),
				this.createMenuItem(Action.PASTE),
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
		lblError.getStyleClass().add("p-error-label");
		lblError.setMaxWidth(Double.MAX_VALUE);
		lblError.textProperty().bind(this.bulkEditModeError);
		lblError.visibleProperty().bind(this.bulkEditModeError.length().greaterThan(0));
		
		HBox bulkEditorButtons = new HBox(lblError, btnOk, btnCancel);
		bulkEditorButtons.getStyleClass().add(SONG_EDITOR_BULK_BUTTONS_CSS);
		HBox.setHgrow(lblError, Priority.ALWAYS);
		
		VBox bulkEditor = new VBox(
				textArea,
				bulkEditorButtons);
		bulkEditor.getStyleClass().add(SONG_EDITOR_BULK_CSS);
		
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
		// NOTE: due to bug in JavaFX, we don't apply the accelerator here
		//mnu.setAccelerator(value);
		mnu.setOnAction(e -> this.executeAction(action));
		mnu.setUserData(action);
		return mnu;
	}
	
	@Override
	public DocumentContext<Song> getDocumentContext() {
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
			case NEW_AUTHOR:
			case NEW_LYRICS:
			case NEW_SECTION:
			case NEW_SONGBOOK:
				return this.create(action);
			case BULK_EDIT_BEGIN:
				return this.beginBulkEdit();
			default:
				return CompletableFuture.completedFuture(null);
		}
	}
	
	@Override
	public boolean isActionEnabled(Action action) {
		DocumentContext<Song> ctx = this.document;
		final Object selected = ctx.getSelectedItem();
		Class<?> selectedType = ctx.getSelectedType();
		Class<?> containerType = null;
		if (selected != null && selectedType == Container.class) {
			containerType = ((Container) selected).getType();
		}
		
		switch (action) {
			case COPY:
				return ctx.isSingleTypeSelected() && selectedType != Song.class && containerType == null;
			case CUT:
				return ctx.isSingleTypeSelected() && selectedType != Song.class && containerType == null;
			case PASTE:
				return (selectedType == Song.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_LYRICS_ARRAY)) ||
					   (selectedType == Lyrics.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_LYRICS_ARRAY)) ||
					   (selectedType == Section.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SECTION_ARRAY)) ||
					   (selectedType == Author.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY)) ||
					   (selectedType == SongBook.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY)) ||
					   (selectedType == Lyrics.class && (
							   Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY) || 
							   Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY) ||
							   Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SECTION_ARRAY))) ||
					   ((containerType == Author.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY)) ||
						(containerType == Section.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SECTION_ARRAY)) ||
						(containerType == SongBook.class && Clipboard.getSystemClipboard().hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY)));
			case DELETE:
				return ctx.getSelectedCount() > 0 && selectedType != Song.class && containerType == null;
			case NEW_LYRICS:
				return ctx.getSelectedCount() == 1 && (selectedType == Song.class || selectedType == Lyrics.class);
			case NEW_AUTHOR:
				return ctx.getSelectedCount() == 1 && (selectedType == Lyrics.class || selectedType == Author.class || containerType == Author.class);
			case NEW_SECTION:
				return ctx.getSelectedCount() == 1 && (selectedType == Lyrics.class || selectedType == Section.class || (containerType == Section.class));
			case NEW_SONGBOOK:
				return ctx.getSelectedCount() == 1 && (selectedType == Lyrics.class || selectedType == SongBook.class || (containerType == SongBook.class));
			case SAVE:
				return ctx.hasUnsavedChanges();
			case REDO:
				return ctx.getUndoManager().isRedoAvailable();
			case UNDO:
				return ctx.getUndoManager().isUndoAvailable();
			case BULK_EDIT_BEGIN:
				return ctx.getSelectedCount() == 1 && (selectedType == Lyrics.class || (containerType == Section.class));
			default:
				return false;
		}
	}
	
	@Override
	public boolean isActionVisible(Action action) {
		// specifically show these actions
		switch (action) {
			case NEW_LYRICS:
			case NEW_SECTION:
			case NEW_SONGBOOK:
			case NEW_AUTHOR:
			case BULK_EDIT_BEGIN:
				return true;
			default:
				return false;
		}
	}
	
	// internal methods

	private CompletableFuture<Void> beginBulkEdit() {
		DocumentContext<Song> ctx = this.document;
		final Object selected = ctx.getSelectedItem();
		Class<?> selectedType = ctx.getSelectedType();
		Class<?> containerType = null;
		if (selected != null && selectedType == Container.class) {
			containerType = ((Container) selected).getType();
		}
		
		if (selectedType == Lyrics.class) {
			BulkEditConverter<Lyrics> tx = new LyricsBulkEditConverter();
			this.bulkEditModeValue.set(tx.toString((Lyrics)selected));
			this.document.setBulkEdit(true);
		} else if (containerType == Section.class) {
			Object lyrics = this.treeView.getSelectionModel().getSelectedItem().getParent().getValue();
			BulkEditConverter<Lyrics> tx = new LyricsBulkEditConverter();
			this.bulkEditModeValue.set(tx.toString((Lyrics)lyrics));
			this.document.setBulkEdit(true);
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private void processBulkEdit() throws BulkEditParseException {
		DocumentContext<Song> ctx = this.document;
		final Object selected = ctx.getSelectedItem();
		Class<?> selectedType = ctx.getSelectedType();
		Class<?> containerType = null;
		if (selected != null && selectedType == Container.class) {
			containerType = ((Container) selected).getType();
		}
		
		Lyrics lyrics = null;
		if (selectedType == Lyrics.class) {
			lyrics = (Lyrics)selected;
		} else if (containerType == Section.class) {
			lyrics = (Lyrics)this.treeView.getSelectionModel().getSelectedItem().getParent().getValue();
		}
		
		if (lyrics != null) {
			String result = this.bulkEditModeValue.get();
			
			BulkEditConverter<Lyrics> converter = new LyricsBulkEditConverter();
			Lyrics edits = converter.fromString(result);
			
			UndoManager um = this.document.getUndoManager();
			um.beginBatch("LyricsBulkEdit");
			lyrics.setTitle(edits.getTitle());
			lyrics.getSections().setAll(edits.getSections());
			um.completeBatch();
		} else {
			LOGGER.error("Bulk edit failed to apply because the selected item wasn't what we expected (an instance of Lyrics)");
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
						if (parent instanceof Lyrics) {
							// do nothing
						} else if (parent instanceof Container) {
							Lyrics lyrics = ((Lyrics) parentItem.getParent().getValue());
							if (value instanceof Author) {
								lyrics.getAuthors().remove(value);
							} else if (value instanceof SongBook) {
								lyrics.getSongBooks().remove(value);
							} else if (value instanceof Section) {
								lyrics.getSections().remove(value);
							}
						} else if (parent instanceof Song) {
							((Song) parent).getLyrics().remove(value);
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
	
	private CompletableFuture<Void> create(Action action) {
		TreeItem<Object> item = this.treeView.getSelectionModel().getSelectedItem();
		Lyrics lyrics = this.findClosestLyrics(item);
		
		switch (action) {
			case NEW_LYRICS:
				Lyrics newLyrics = new Lyrics();
				newLyrics.setLanguage(Locale.getDefault().toLanguageTag());
				newLyrics.setOriginal(true);
				newLyrics.setTitle(this.song.getName());
				newLyrics.getAuthors().add(new Author(System.getProperty("user.name"), Author.TYPE_LYRICS));
				newLyrics.getSections().add(new Section(Translations.get("song.lyrics.section.name.default"), Translations.get("song.lyrics.section.text.default")));
				this.song.getLyrics().add(newLyrics);
				break;
			case NEW_AUTHOR:
				if (this.document.getSelectedCount() == 1 && lyrics != null) {
					lyrics.getAuthors().add(new Author(System.getProperty("user.name"), Author.TYPE_LYRICS));
				}
				break;
			case NEW_SONGBOOK:
				if (this.document.getSelectedCount() == 1 && lyrics != null) {
					lyrics.getSongBooks().add(new SongBook());
				}
				break;
			case NEW_SECTION:
				if (this.document.getSelectedCount() == 1 && lyrics != null) {
					lyrics.getSections().add(new Section(Translations.get("song.lyrics.section.name.default"), Translations.get("song.lyrics.section.text.default")));
				}
				break;
			default:
				break;
		}
		return CompletableFuture.completedFuture(null);
	}
	
	private Lyrics findClosestLyrics(TreeItem<Object> item) {
		if (item == null) return null;
		
		Object value = item.getValue();
		if (value != null && value instanceof Lyrics) {
			return (Lyrics)value;
		}
		
		return findClosestLyrics(item.getParent());
	}
	
	private ClipboardContent getClipboardContentForSelection(boolean serializeData) throws Exception {
		List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
		List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
		
		// in the case of Drag n' Drop, we don't need to serialize it
		String data = serializeData ? JsonIO.write(objectData) : "NA";
		List<String> textData = new ArrayList<>();
		DataFormat format = null;
		
		Class<?> clazz = this.document.getSelectedType();
		if (clazz == Author.class) {
			format = DataFormats.PRAISENTER_AUTHOR_ARRAY;
			textData = items.stream().map(b -> ((Author)b.getValue()).getName()).collect(Collectors.toList());
		} else if (clazz == SongBook.class) {
			format = DataFormats.PRAISENTER_SONGBOOK_ARRAY;
			textData = items.stream().map(c -> ((SongBook)c.getValue()).toString()).collect(Collectors.toList());
		} else if (clazz == Section.class) {
			format = DataFormats.PRAISENTER_SECTION_ARRAY;
			textData = items.stream().map(v -> ((Section)v.getValue()).getText()).collect(Collectors.toList());
		} else if (clazz == Lyrics.class) {
			format = DataFormats.PRAISENTER_LYRICS_ARRAY;
			textData = items.stream().map(v -> ((Lyrics)v.getValue()).getTitle()).collect(Collectors.toList());
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(String.join(Constants.NEW_LINE, textData));
		content.put(format, data);
		
		return content;
	}
	
	private CompletableFuture<Void> copy(boolean isCut) {
		Class<?> clazz = this.document.getSelectedType();
		if (clazz != null && clazz != Song.class) {
			List<TreeItem<Object>> items = this.treeView.getSelectionModel().getSelectedItems();
			List<Object> objectData = items.stream().map(i -> i.getValue()).collect(Collectors.toList());
			try {
				ClipboardContent content = this.getClipboardContentForSelection(true);
				Clipboard clipboard = Clipboard.getSystemClipboard();
				clipboard.setContent(content);
				
				if (isCut) {
					if (clazz == Lyrics.class) {
						Object parent = items.get(0).getParent().getValue();
						((Song)parent).getLyrics().removeAll(objectData);
					} else if (clazz == Author.class) {
						Object parent = items.get(0).getParent().getParent().getValue();
						((Lyrics)parent).getAuthors().removeAll(objectData);
					} else if (clazz == SongBook.class) {
						Object parent = items.get(0).getParent().getParent().getValue();
						((Lyrics)parent).getSongBooks().removeAll(objectData);
					} else if (clazz == Section.class) {
						Object parent = items.get(0).getParent().getParent().getValue();
						((Lyrics)parent).getSections().removeAll(objectData);
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
		if (this.document.getSelectedCount() == 1) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			TreeItem<Object> selected = this.treeView.getSelectionModel().getSelectedItem();
			final Object value = selected.getValue();
			Class<?> selectedType = value.getClass();
			
			TreeItem<Object> item = this.treeView.getSelectionModel().getSelectedItem();
			Lyrics lyrics = this.findClosestLyrics(item);
			
			try {
				if ((selectedType == Song.class || selectedType == Lyrics.class) && clipboard.hasContent(DataFormats.PRAISENTER_LYRICS_ARRAY)) {
					Lyrics[] newLyrics = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_LYRICS_ARRAY), Lyrics[].class);
					this.song.getLyrics().addAll(newLyrics);
				} else if (lyrics != null && clipboard.hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY)) {
					Author[] authors = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_AUTHOR_ARRAY), Author[].class);
					lyrics.getAuthors().addAll(authors);
				} else if (lyrics != null && clipboard.hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY)) {
					SongBook[] songbooks = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY), SongBook[].class);
					lyrics.getSongBooks().addAll(songbooks);
				} else if (lyrics != null && clipboard.hasContent(DataFormats.PRAISENTER_SECTION_ARRAY)) {
					Section[] sections = JsonIO.read((String)clipboard.getContent(DataFormats.PRAISENTER_SECTION_ARRAY), Section[].class);
					lyrics.getSections().addAll(sections);
				}
				// TODO select the pasted elements
			} catch (Exception ex) {
				LOGGER.warn("Failed to paste clipboard content (likely due to a JSON deserialization error", ex);
			}
		}
		
		return CompletableFuture.completedFuture(null);
	}
	
	private void dragDetected(MouseEvent e) {
		if (this.document.isSingleTypeSelected() && this.document.getSelectedType() != Container.class) {
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
		if (e.getSource() instanceof SongTreeCell) {
			SongTreeCell cell = (SongTreeCell)e.getSource();
			cell.pseudoClassStateChanged(DRAG_OVER_PARENT, false);
			cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
			cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
		}
	}
	
	private void dragEntered(DragEvent e) {
		// nothing to do here
	}
	
	private void dragOver(DragEvent e) {
		if (!(e.getSource() instanceof SongTreeCell)) {
			return;
		}
		
		// don't allow drop onto itself
		SongTreeCell cell = (SongTreeCell)e.getSource();
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
		boolean dragAuthors = e.getDragboard().hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY);
		boolean dragLyrics = e.getDragboard().hasContent(DataFormats.PRAISENTER_LYRICS_ARRAY);
		boolean dragSections = e.getDragboard().hasContent(DataFormats.PRAISENTER_SECTION_ARRAY);
		boolean dragSongBooks = e.getDragboard().hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY);
		
		boolean targetIsSong = data instanceof Song;
		boolean targetIsLyrics = data instanceof Lyrics;
		boolean targetIsContainer = data instanceof Container;
		boolean targetIsAuthor = data instanceof Author;
		boolean targetIsSongBook = data instanceof SongBook;
		boolean targetIsSection = data instanceof Section;
		boolean targetIsAuthorContainer = targetIsContainer && ((Container) data).getType() == Author.class;
		boolean targetIsSongBookContainer = targetIsContainer && ((Container) data).getType() == SongBook.class;
		boolean targetIsSectionContainer = targetIsContainer && ((Container) data).getType() == Section.class;
		
		boolean isAllowed = 
				(dragAuthors && targetIsLyrics) ||
				(dragAuthors && targetIsAuthor) ||
				(dragAuthors && targetIsAuthorContainer) ||
				(dragLyrics && targetIsSong) ||
				(dragLyrics && targetIsLyrics) ||
				(dragSections && targetIsLyrics) ||
				(dragSections && targetIsSection) ||
				(dragSections && targetIsSectionContainer) ||
				(dragSongBooks && targetIsLyrics) ||
				(dragSongBooks && targetIsSongBook) ||
				(dragSongBooks && targetIsSongBookContainer);
		
		if (!isAllowed) {
			return;
		}
		
		// allow the transfer
		e.acceptTransferModes(TransferMode.MOVE);
		
		boolean isParent = 
				(dragAuthors && targetIsLyrics) ||
				(dragAuthors && targetIsAuthorContainer) ||
				(dragLyrics && targetIsSong) ||
				(dragSections && targetIsLyrics) ||
				(dragSections && targetIsSectionContainer) ||
				(dragSongBooks && targetIsLyrics) ||
				(dragSongBooks && targetIsSongBookContainer);

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
		if (!(e.getGestureTarget() instanceof SongTreeCell)) {
			return;
		}
		
		// copy the selected items
		List<TreeItem<Object>> selected = new ArrayList<>(this.treeView.getSelectionModel().getSelectedItems());

		// check for null data
		SongTreeCell target = (SongTreeCell)e.getGestureTarget();
		TreeItem<Object> targetItem = target.getTreeItem();
		Object targetValue = targetItem.getValue();
		
		// are we dragging to a parent node?
		boolean dragAuthors = e.getDragboard().hasContent(DataFormats.PRAISENTER_AUTHOR_ARRAY);
		boolean dragLyrics = e.getDragboard().hasContent(DataFormats.PRAISENTER_LYRICS_ARRAY);
		boolean dragSections = e.getDragboard().hasContent(DataFormats.PRAISENTER_SECTION_ARRAY);
		boolean dragSongBooks = e.getDragboard().hasContent(DataFormats.PRAISENTER_SONGBOOK_ARRAY);
		
		boolean targetIsSong = targetValue instanceof Song;
		boolean targetIsLyrics = targetValue instanceof Lyrics;
		boolean targetIsContainer = targetValue instanceof Container;
		boolean targetIsAuthorContainer = targetIsContainer && ((Container) targetValue).getType() == Author.class;
		boolean targetIsSongBookContainer = targetIsContainer && ((Container) targetValue).getType() == SongBook.class;
		boolean targetIsSectionContainer = targetIsContainer && ((Container) targetValue).getType() == Section.class;
		
		boolean isParent = 
				(dragAuthors && targetIsLyrics) ||
				(dragAuthors && targetIsAuthorContainer) ||
				(dragLyrics && targetIsSong) ||
				(dragSections && targetIsLyrics) ||
				(dragSections && targetIsSectionContainer) ||
				(dragSongBooks && targetIsLyrics) ||
				(dragSongBooks && targetIsSongBookContainer);
		
		this.undoManager.beginBatch("DragDrop");
		
		// remove the data from its previous location
		List<Object> items = new ArrayList<>();
		for (TreeItem<Object> item : selected) {
			Object child = item.getValue();
			Object parent = item.getParent().getValue();
			if (parent instanceof Container) {
				parent = item.getParent().getParent().getValue();
			}
			
			if (child instanceof Lyrics) {
				((Song)parent).getLyrics().remove(child);
			} else if (child instanceof Author) {
				((Lyrics)parent).getAuthors().remove(child);
			} else if (child instanceof Section) {
				((Lyrics)parent).getSections().remove(child);
			} else if (child instanceof SongBook) {
				((Lyrics)parent).getSongBooks().remove(child);
			}
			items.add(child);
		}
		
		// now add the data
		Object parent = isParent ? targetValue : targetItem.getParent().getValue();
		int size = targetItem.getChildren().size();
		if (targetIsLyrics) {
			if (dragAuthors) size = ((Lyrics)parent).getAuthors().size();
			if (dragSongBooks) size = ((Lyrics)parent).getSongBooks().size();
			if (dragSections) size = ((Lyrics)parent).getSections().size();
		}
		int index = isParent ? size : targetItem.getParent().getChildren().indexOf(targetItem);
		boolean after = e.getY() >= target.getHeight() * 0.75;
		if (!isParent && after) index++;
		
		if (parent instanceof Container) {
			TreeItem<?> item = targetItem.getParent();
			if (!isParent) item = item.getParent();
			parent = item.getValue();
		}
		
		if (dragLyrics) {
			((Song)parent).getLyrics().addAll(index, items.stream().map(i -> (Lyrics)i).collect(Collectors.toList()));
		} else if (dragSections) {
			((Lyrics)parent).getSections().addAll(index, items.stream().map(i -> (Section)i).collect(Collectors.toList()));
		} else if (dragSongBooks) {
			((Lyrics)parent).getSongBooks().addAll(index, items.stream().map(i -> (SongBook)i).collect(Collectors.toList()));
		} else if (dragAuthors) {
			((Lyrics)parent).getAuthors().addAll(index, items.stream().map(i -> (Author)i).collect(Collectors.toList()));
		}
		
		// TODO the selection doesn't seem consistent (probably because of the containers)
//		int row = (isParent && size > 0 
//				? this.treeView.getRow(targetItem.getChildren().get(size - 1))
//				: this.treeView.getRow(targetItem)) 
//				+ (!isParent && after ? 1 : -items.size());
		this.treeView.getSelectionModel().clearSelection();
//		this.treeView.getSelectionModel().selectRange(row, row + items.size());
		
		this.undoManager.completeBatch();
		
		e.setDropCompleted(true);
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
}
