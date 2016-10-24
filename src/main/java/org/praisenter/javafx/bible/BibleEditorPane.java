package org.praisenter.javafx.bible;

import java.util.Collections;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.PraisenterContext;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
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
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;
import javafx.util.Duration;

// TODO translate
final class BibleEditorPane extends BorderPane {
	private final PraisenterContext context;

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
//	private final ScrollPane scroller;
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<>();
	
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
		
		TreeView<TreeData> bibleEditor = new TreeView<TreeData>();
		bibleEditor.setEditable(true);
		bibleEditor.setShowRoot(true);
		bibleEditor.setCellFactory(new Callback<TreeView<TreeData>, TreeCell<TreeData>>(){
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
		
		MenuItem mnuCopy = new MenuItem("Copy", FONT_AWESOME.create(FontAwesome.Glyph.COPY));
		MenuItem mnuCut = new MenuItem("Cut", FONT_AWESOME.create(FontAwesome.Glyph.CUT));
		MenuItem mnuPaste = new MenuItem("Paste", FONT_AWESOME.create(FontAwesome.Glyph.PASTE));
		mnuPaste.setDisable(true);
		
		mnuCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
		mnuCut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		mnuPaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
		
		MenuItem mnuDelete = new MenuItem("Delete", FONT_AWESOME.create(FontAwesome.Glyph.REMOVE));
		MenuItem mnuNew = new MenuItem("New", FONT_AWESOME.create(FontAwesome.Glyph.PLUS));
		
		ContextMenu contextMenu = new ContextMenu(mnuNew, mnuDelete, new SeparatorMenuItem(), mnuCopy, mnuCut, mnuPaste);
		bibleEditor.setContextMenu(contextMenu);
		
		this.bible.addListener((obs, ov, nv) -> {
			if (nv != null) {
				// create the root
				TreeItem<TreeData> root = new TreeItem<TreeData>(new BibleTreeData(nv));
				root.setExpanded(true);
				
				for (Book book : nv.getBooks()) {
					if (book != null) {
						TreeItem<TreeData> bi = new TreeItem<TreeData>(new BookTreeData(nv, book));
						root.getChildren().add(bi);
						for (Chapter chapter : book.getChapters()) {
							TreeItem<TreeData> ch = new TreeItem<TreeData>(new ChapterTreeData(nv, book, chapter));
							bi.getChildren().add(ch);
							for (Verse verse : chapter.getVerses()) {
								TreeItem<TreeData> vi = new TreeItem<TreeData>(new VerseTreeData(nv, book, chapter, verse));
								ch.getChildren().add(vi);
							}
						}
					}
				}
				
				bibleEditor.setRoot(root);
				
				txtName.setText(nv.getName());
				txtLanguage.setText(nv.getLanguage());
				txtSource.setText(nv.getSource());
				txtCopyright.setText(nv.getCopyright());
				txtNotes.setText(nv.getNotes());
			} else {
				bibleEditor.setRoot(null);
				
				txtName.setText(null);
				txtLanguage.setText(null);
				txtSource.setText(null);
				txtCopyright.setText(null);
				txtNotes.setText(null);
			}
		});
		
		contextMenu.setOnShowing(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			int type = BibleEditorEventManager.getType(item.getValue());
			
			if (type == 0) {
				mnuDelete.setVisible(false);
				mnuCopy.setVisible(false);
				mnuCut.setVisible(false);
			} else {
				mnuDelete.setVisible(true);
				mnuCopy.setVisible(true);
				mnuCut.setVisible(true);
			}
			
			if (type == 0) {
				mnuNew.setText("New Book");
			} else if (type == 1) {
				mnuNew.setText("New Chapter");
			} else if (type == 2) {
				mnuNew.setText("New Verse");
			} else {
				mnuNew.setText("New Verse");
			}
			
			if (manager.canPaste(item)) {
				mnuPaste.setDisable(false);
			} else {
				mnuPaste.setDisable(true);
			}
		});
		
		mnuCopy.setOnAction(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			manager.copy(item);
		});
		mnuCut.setOnAction(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			manager.cut(item);
		});
		mnuPaste.setOnAction(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			if (manager.canPaste(item)) {
				manager.paste(item);
			}
		});
		
		mnuDelete.setOnAction(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			manager.delete(item);
		});
		mnuNew.setOnAction(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			manager.add(item);
		});
		
		// key events
		
		bibleEditor.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				int index = bibleEditor.getSelectionModel().getSelectedIndex();
				TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
				manager.delete(item);
			}
		});
		
		KeyCombination copy = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
		KeyCombination cut = new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN);
		KeyCombination paste = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
		bibleEditor.setOnKeyPressed(e -> {
			int index = bibleEditor.getSelectionModel().getSelectedIndex();
			TreeItem<TreeData> item = bibleEditor.getTreeItem(index);
			if (copy.match(e)) {
				manager.copy(item);
			} else if (cut.match(e)) {
				manager.cut(item);
			} else if (paste.match(e)) {
				if (manager.canPaste(item)) {
					manager.paste(item);
				}
			}
		});
		
		this.setLeft(bibleEditor);
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
