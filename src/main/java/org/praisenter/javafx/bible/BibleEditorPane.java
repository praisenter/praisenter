package org.praisenter.javafx.bible;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.Constants;
import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.ApplicationAction;
import org.praisenter.javafx.ApplicationContextMenu;
import org.praisenter.javafx.ApplicationEvent;
import org.praisenter.javafx.ApplicationPane;
import org.praisenter.javafx.ApplicationPaneEvent;
import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

// TODO translate
public final class BibleEditorPane extends BorderPane implements ApplicationPane {
	private final PraisenterContext context;

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
//	private final ScrollPane scroller;
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<>();
	
	private final TreeView<TreeData> bibleTree;
	
	public BibleEditorPane(PraisenterContext context) {
		this.context = context;
		
		// TODO saving
		// TODO updating nodes with changes in fields
		
		// bible
		TextField txtName = new TextField();
		TextField txtLanguage = new TextField();
		TextField txtSource = new TextField();
		TextField txtCopyright = new TextField();
		TextArea txtNotes = new TextArea();
		TextField txtBookName = new TextField();
		
		GridPane bibleDetail = new GridPane();
		bibleDetail.setPadding(new Insets(10));
		bibleDetail.setVgap(5);
		bibleDetail.setHgap(5);
		bibleDetail.add(new Label("Name"), 0, 0);
		bibleDetail.add(txtName, 1, 0);
		bibleDetail.add(new Label("Language"), 0, 1);
		bibleDetail.add(txtLanguage, 1, 1);
		bibleDetail.add(new Label("Source"), 0, 2);
		bibleDetail.add(txtSource, 1, 2);
		bibleDetail.add(new Label("Copyright"), 0, 3);
		bibleDetail.add(txtCopyright, 1, 3);
		bibleDetail.add(new Label("Notes"), 0, 4);
		bibleDetail.add(txtNotes, 0, 5, 2, 1);
		
		GridPane bookDetail = new GridPane();
		bookDetail.setPadding(new Insets(10));
		bookDetail.setVgap(5);
		bookDetail.setHgap(5);
		bookDetail.add(new Label("Name"), 0, 0);
		bookDetail.add(txtBookName, 1, 0);
		
		TitledPane ttlBible = new TitledPane("Bible Properties", bibleDetail);
		ttlBible.setCollapsible(false);
		TitledPane ttlBook = new TitledPane("Book Properties", bookDetail);
		ttlBook.setCollapsible(false);
		VBox properties = new VBox(ttlBible, ttlBook);
		
		this.setCenter(properties);
		
		BibleEditorEventManager manager = new BibleEditorEventManager();
		
		this.bibleTree = new TreeView<TreeData>();
		this.bibleTree.setEditable(true);
		this.bibleTree.setShowRoot(true);
		// allow multiple selection
		this.bibleTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.bibleTree.setCellFactory(new Callback<TreeView<TreeData>, TreeCell<TreeData>>(){
            @Override
            public TreeCell<TreeData> call(TreeView<TreeData> itm) {
            	BibleTreeCell cell = new BibleTreeCell();
            	// wire up events
            	cell.setOnDragDetected(e -> {
        			manager.dragDetected(cell, e);
        		});
            	cell.setOnDragExited(e -> {
        			manager.dragExited(cell, e);
        		});
            	cell.setOnDragEntered(e -> {
        			manager.dragEntered(cell, e);
        		});
            	cell.setOnDragOver(e -> {
        			manager.dragOver(cell, e);
        		});
            	cell.setOnDragDropped(e -> {
        			manager.dragDropped(cell, e);
        		});
            	return cell;
            }
        });
		
		// context menu
		ApplicationContextMenu menu = new ApplicationContextMenu(this);
		menu.getItems().addAll(
				menu.createMenuItem(ApplicationAction.NEW_BOOK),
				menu.createMenuItem(ApplicationAction.NEW_CHAPTER),
				menu.createMenuItem(ApplicationAction.NEW_VERSE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.COPY),
				menu.createMenuItem(ApplicationAction.CUT),
				menu.createMenuItem(ApplicationAction.PASTE),
				new SeparatorMenuItem(),
				menu.createMenuItem(ApplicationAction.RENUMBER),
				menu.createMenuItem(ApplicationAction.DELETE));
		this.bibleTree.setContextMenu(menu);
		
		// EVENTS & BINDINGS
		
		this.bible.addListener((obs, ov, nv) -> {
			if (nv != null) {
				// create the root node
				TreeItem<TreeData> root = this.forBible(nv);
				root.setExpanded(true);
				
				// set the tree
				bibleTree.setRoot(root);
				
				// set the editor fields
				txtName.setText(nv.getName());
				txtLanguage.setText(nv.getLanguage());
				txtSource.setText(nv.getSource());
				txtCopyright.setText(nv.getCopyright());
				txtNotes.setText(nv.getNotes());
			} else {
				bibleTree.setRoot(null);
				
				txtName.setText(null);
				txtLanguage.setText(null);
				txtSource.setText(null);
				txtCopyright.setText(null);
				txtNotes.setText(null);
			}
		});
		
		this.bibleTree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<TreeData>>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TreeItem<TreeData>> change) {
				// update state
				stateChanged();
			}
		});
		
		this.setLeft(this.bibleTree);
		
		// listen for application events
		this.addEventHandler(ApplicationEvent.ALL, this::onApplicationEvent);
	}

	// NODE GENERATION
	
	private TreeItem<TreeData> forBible(Bible bible) {
		TreeItem<TreeData> root = new TreeItem<TreeData>(new BibleTreeData(bible));
		for (Book book : bible.getBooks()) {
			if (book != null) {
				TreeItem<TreeData> bi = this.forBook(bible, book);
				root.getChildren().add(bi);
			}
		}
		return root;
	}
	
	private TreeItem<TreeData> forBook(Bible bible, Book book) {
		TreeItem<TreeData> bi = new TreeItem<TreeData>(new BookTreeData(bible, book));
		for (Chapter chapter : book.getChapters()) {
			TreeItem<TreeData> ch = this.forChapter(bible, book, chapter);
			bi.getChildren().add(ch);
		}
		return bi;
	}
	
	private TreeItem<TreeData> forChapter(Bible bible, Book book, Chapter chapter) {
		TreeItem<TreeData> ch = new TreeItem<TreeData>(new ChapterTreeData(bible, book, chapter));
		for (Verse verse : chapter.getVerses()) {
			TreeItem<TreeData> vi = this.forVerse(bible, book, chapter, verse);
			ch.getChildren().add(vi);
		}
		return ch;
	}
	
	private TreeItem<TreeData> forVerse(Bible bible, Book book, Chapter chapter, Verse verse) {
		return new TreeItem<TreeData>(new VerseTreeData(bible, book, chapter, verse));
	}
	
	// METHODS
	
	private void copy(boolean cut) {
		// get the selection(s)
		List<TreeItem<TreeData>> items = new ArrayList<TreeItem<TreeData>>(this.bibleTree.getSelectionModel().getSelectedItems());
		
		if (items.size() > 0) {
			DataFormat format = null;
			StringBuilder text = new StringBuilder();
			
			List<Object> data = new ArrayList<Object>();
			for (TreeItem<TreeData> item : items) {
				TreeData td = item.getValue();
				if (td instanceof BookTreeData) {
					BookTreeData btd = (BookTreeData)td;
					Book book = btd.book;
					data.add(book);
					format = DataFormats.BOOKS;
					text.append(book.getName()).append(Constants.NEW_LINE);
					if (cut) {
						btd.bible.getBooks().remove(book);
					}
				} else if (td instanceof ChapterTreeData) {
					ChapterTreeData ctd = (ChapterTreeData)td;
					Chapter chapter = ctd.chapter;
					data.add(chapter);
					format = DataFormats.CHAPTERS;
					text.append(chapter.getNumber()).append(Constants.NEW_LINE);
					if (cut) {
						ctd.book.getChapters().remove(chapter);
					}
				} else if (td instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)td;
					Verse verse = vtd.verse;
					data.add(verse);
					format = DataFormats.VERSES;
					text.append(verse.getText()).append(Constants.NEW_LINE);
					if (cut) {
						vtd.chapter.getVerses().remove(verse);
					}
				}
			}
			
			for (TreeItem<TreeData> item : items) {
				if (cut) {
					// remove from the tree
					item.getParent().getChildren().remove(item);
				}
			}
			
			Clipboard cb = Clipboard.getSystemClipboard();
			ClipboardContent cc = new ClipboardContent();
			cc.put(DataFormat.PLAIN_TEXT, text.toString());
			cc.put(format, data);
			cb.setContent(cc);
			
			// notify we changed
			this.stateChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void paste() {
		// get the selection(s)
		ObservableList<TreeItem<TreeData>> items = this.bibleTree.getSelectionModel().getSelectedItems();
		
		Clipboard cb = Clipboard.getSystemClipboard();
		if (items.size() == 1) {
			TreeItem<TreeData> node = items.get(0);
			Class<?> type = node.getValue().getClass();
			if (type.equals(BibleTreeData.class)) {
				Bible bible = ((BibleTreeData)node.getValue()).bible;
				// then we can paste books
				Object data = cb.getContent(DataFormats.BOOKS);
				if (data != null && data instanceof List) {
					List<Book> books = (List<Book>)data;
					short max = bible.getMaxBookNumber();
					for (Book book : books) {
						Book copy = book.copy();
						copy.setNumber(++max);
						// add the book to the bible
						bible.getBooks().add(copy);
						// add the tree node
						node.getChildren().add(this.forBook(bible, copy));
					}
				}
			} else if (type.equals(BookTreeData.class)) {
				BookTreeData td = (BookTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				// then we can paste books
				Object data = cb.getContent(DataFormats.CHAPTERS);
				if (data != null && data instanceof List) {
					List<Chapter> chapters = (List<Chapter>)data;
					for (Chapter chapter : chapters) {
						Chapter copy = chapter.copy();
						// add the book to the bible
						book.getChapters().add(copy);
						// add the tree node
						node.getChildren().add(this.forChapter(bible, book, copy));
					}
				}
			} else if (type.equals(ChapterTreeData.class)) {
				ChapterTreeData td = (ChapterTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				Chapter chapter = td.chapter;
				// then we can paste books
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<Verse> verses = (List<Verse>)data;
					for (Verse verse : verses) {
						Verse copy = verse.copy();
						// add the book to the bible
						chapter.getVerses().add(copy);
						// add the tree node
						node.getChildren().add(this.forVerse(bible, book, chapter, copy));
					}
				}
			} else if (type.equals(VerseTreeData.class)) {
				VerseTreeData td = (VerseTreeData)node.getValue();
				Bible bible = td.bible;
				Book book = td.book;
				Chapter chapter = td.chapter;
				// then we can paste books
				Object data = cb.getContent(DataFormats.VERSES);
				if (data != null && data instanceof List) {
					List<Verse> verses = (List<Verse>)data;
					for (Verse verse : verses) {
						Verse copy = verse.copy();
						// add the book to the bible
						chapter.getVerses().add(copy);
						// add the tree node
						node.getParent().getChildren().add(this.forVerse(bible, book, chapter, copy));
					}
				}
			}
		}
	}
	
	private void delete() {
		List<TreeItem<TreeData>> items = new ArrayList<TreeItem<TreeData>>(this.bibleTree.getSelectionModel().getSelectedItems());
		
		if (items.size() > 0) {
			for (TreeItem<TreeData> item : items) {
				// remove the data
				TreeData td = item.getValue();
				if (td instanceof BookTreeData) {
					BookTreeData btd = (BookTreeData)td;
					btd.bible.getBooks().remove(btd.book);
				} else if (td instanceof ChapterTreeData) {
					ChapterTreeData ctd = (ChapterTreeData)td;
					ctd.book.getChapters().remove(ctd.chapter);
				} else if (td instanceof VerseTreeData) {
					VerseTreeData vtd = (VerseTreeData)td;
					vtd.chapter.getVerses().remove(vtd.verse);
				}
				// remove the node
				item.getParent().getChildren().remove(item);
			}
		}
	}
	
	private void add() {
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		
		if (item == null || item.getValue() == null) return;
		Class<?> type = item.getValue().getClass();
		if (VerseTreeData.class.equals(type)) {
			// add new verse
			VerseTreeData vd = (VerseTreeData)item.getValue();
			short number = vd.chapter.getMaxVerseNumber();
			Verse verse = new Verse(++number, "New verse");
			// add to data
			vd.chapter.getVerses().add(verse);
			// add to view
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new VerseTreeData(vd.bible, vd.book, vd.chapter, verse));
			item.getParent().getChildren().add(newItem);
		} else if (ChapterTreeData.class.equals(type)) {
			// add new verse
			ChapterTreeData cd = (ChapterTreeData)item.getValue();
			short number = cd.chapter.getMaxVerseNumber();
			Verse verse = new Verse(++number, "New verse");
			// add to data
			cd.chapter.getVerses().add(verse);
			// add to view
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new VerseTreeData(cd.bible, cd.book, cd.chapter, verse));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (BookTreeData.class.equals(type)) {
			// add new chapter
			BookTreeData bd = (BookTreeData)item.getValue();
			short number = bd.book.getMaxChapterNumber();
			Chapter chapter = new Chapter(++number);
			// add to data
			bd.book.getChapters().add(chapter);
			// add to view
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new ChapterTreeData(bd.bible, bd.book, chapter));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (BibleTreeData.class.equals(type)) {
			// add new book
			BibleTreeData bd = (BibleTreeData)item.getValue();
			short number = bd.bible.getMaxBookNumber();
			Book book = new Book("New book", ++number);
			// add to data
			bd.bible.getBooks().add(book);
			// add to view
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new BookTreeData(bd.bible, book));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		}
	}
	
	private void save() {
		this.context.getBibleLibrary().save(this.getBible(), b -> {
			
		}, (b, ex) -> {
			ex.printStackTrace();
		});
	}
	
	private void renumber() {
		// need to determine what is selected
		// renumber depth first
		TreeItem<TreeData> item = this.bibleTree.getSelectionModel().getSelectedItem();
		
		if (item != null) {
			// remove the data
			TreeData td = item.getValue();
			if (td instanceof BibleTreeData) {
				this.renumberBible(item);
			} else if (td instanceof BookTreeData) {
				this.renumberBook(item);
			} else if (td instanceof ChapterTreeData) {
				this.renumberChapter(item);
			} else if (td instanceof VerseTreeData) {
				this.renumberChapter(item.getParent());
			}
		}
	}
	
	private void renumberBible(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			BookTreeData td = (BookTreeData)item.getValue();
			// update the data
			td.book.setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberBook(item);
		}
		// make sure the data is sorted the same way
		Collections.sort(((BibleTreeData)node.getValue()).bible.getBooks());
	}
	
	private void renumberBook(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			ChapterTreeData td = (ChapterTreeData)item.getValue();
			// update the data
			td.chapter.setNumber(i++);
			// update the label
			td.update();
			// update children
			renumberChapter(item);
		}
		// make sure the data is sorted the same way
		Collections.sort(((BookTreeData)node.getValue()).book.getChapters());
	}
	
	private void renumberChapter(TreeItem<TreeData> node) {
		short i = 1;
		for (TreeItem<TreeData> item : node.getChildren()) {
			VerseTreeData td = (VerseTreeData)item.getValue();
			// update the data
			td.verse.setNumber(i++);
			// update the label
			td.update();
		}
		// make sure the data is sorted the same way
		Collections.sort(((ChapterTreeData)node.getValue()).chapter.getVerses());
	}

    /**
     * Event handler for application events.
     * @param event the event
     */
    private final void onApplicationEvent(ApplicationEvent event) {
    	ApplicationAction action = event.getAction();
    	switch (action) {
	    	case NEW_BOOK:
	    		add();
	    		break;
			case NEW_CHAPTER:
				add();
				break;
			case NEW_VERSE:
				add();
				break;
			case COPY:
				this.copy(false);
				break;
			case CUT:
				this.copy(true);
				break;
			case PASTE:
				this.paste();
				break;
			case DELETE:
				this.delete();
				break;
			case SAVE:
				this.save();
				break;
			case SAVE_AS:
				// FIXME save as...
				break;
			case RENUMBER:
				this.renumber();
				break;
    		default:
    			break;
    	}
    }
    
    /**
     * Called when the state of this pane changes.
     */
    private final void stateChanged() {
    	fireEvent(new ApplicationPaneEvent(this.bibleTree, BibleEditorPane.this, ApplicationPaneEvent.STATE_CHANGED, BibleEditorPane.this));
    }
    
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
		// how much is selected?
		ObservableList<TreeItem<TreeData>> items = this.bibleTree.getSelectionModel().getSelectedItems();
		int count = items.size();
		
		// are all the selections the same type?
		boolean sameType = true;
		Class<?> type = null;
		if (count > 0) {
			TreeItem<TreeData> first = items.get(0);
			if (first != null) {
				type = first.getValue().getClass();
				for (TreeItem<TreeData> item : items) {
					if (!item.getValue().getClass().equals(type)) {
						sameType = false;
						break;
					}
				}
			}
		}
		
    	switch (action) {
    		case NEW_BOOK:
    			return count == 1 && BibleTreeData.class.equals(type);
    		case NEW_CHAPTER:
    			return count == 1 && BookTreeData.class.equals(type);
    		case NEW_VERSE:
    			return count == 1 && (ChapterTreeData.class.equals(type) || VerseTreeData.class.equals(type));
			case COPY:
			case CUT:
				// must be the same type and something selected
				return sameType && count > 0;
			case PASTE:
				Clipboard cb = Clipboard.getSystemClipboard();
				if (sameType && count == 1) {
					if (BibleTreeData.class.equals(type)) {
						// then we can paste books
						return cb.hasContent(DataFormats.BOOKS);
					} else if (BookTreeData.class.equals(type)) {
						return cb.hasContent(DataFormats.CHAPTERS);
					} else if (ChapterTreeData.class.equals(type)) {
						return cb.hasContent(DataFormats.VERSES);
					} else if (VerseTreeData.class.equals(type)) {
						return cb.hasContent(DataFormats.VERSES);
					}
				}
				return false;
			case DELETE:
				return count > 0;
			case RENUMBER:
				return count == 1;
			case SAVE:
			case SAVE_AS:
				return true;
			default:
				break;
		}
    	return false;
	}
	
	@Override
	public boolean isApplicationActionVisible(ApplicationAction action) {
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
}
