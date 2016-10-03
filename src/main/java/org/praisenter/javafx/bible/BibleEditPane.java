package org.praisenter.javafx.bible;

import java.util.List;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Verse;
import org.praisenter.javafx.PraisenterContext;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Callback;

public class BibleEditPane extends BorderPane {
	private final PraisenterContext context;
	
	private final ObjectProperty<Bible> bible = new SimpleObjectProperty<>();
	
	public BibleEditPane(PraisenterContext context) {
		this.context = context;
		
		// bible
		TextField txtName = new TextField();
		TextField txtLanguage = new TextField();
		TextField txtSource = new TextField();
		TextField txtCopyright = new TextField();
		TextArea txtNotes = new TextArea();
		
		// book
		TextField txtBookName = new TextField();
		
		// verse
		TextField txtText = new TextField();
		
		TreeView<ObservableBibleItem> bibleEditor = new TreeView<ObservableBibleItem>();
		bibleEditor.setEditable(true);
		bibleEditor.setShowRoot(false);
		bibleEditor.setCellFactory(new Callback<TreeView<ObservableBibleItem>, TreeCell<ObservableBibleItem>>(){
            @Override
            public TreeCell<ObservableBibleItem> call(TreeView<ObservableBibleItem> itm) {
            	return new ObservableBibleItemTreeCell();
            }
        });
		
		this.bible.addListener((obs, ov, nv) -> {
			if (nv != null && nv.getBookCount() > 0) {
				// create the root
				TreeItem<ObservableBibleItem> root = new TreeItem<ObservableBibleItem>(new ObservableBibleItem(nv, null, 0, null));
				root.setExpanded(true);
				
				for (Book book : nv.getBooks()) {
					if (book != null) {
						TreeItem<ObservableBibleItem> bi = new TreeItem<ObservableBibleItem>(new ObservableBibleItem(nv, book, 0, null));
						root.getChildren().add(bi);
						int chapter = -1;
						TreeItem<ObservableBibleItem> ch = null;
						for (Verse verse : book.getVerses()) {
							if (chapter != verse.getChapter()) {
								ch = new TreeItem<ObservableBibleItem>(new ObservableBibleItem(null, null, verse.getChapter(), null));
								bi.getChildren().add(ch);
								chapter = verse.getChapter();
							}
							TreeItem<ObservableBibleItem> vi = new TreeItem<ObservableBibleItem>(new ObservableBibleItem(nv, book, verse.getChapter(), verse));
							ch.getChildren().add(vi);
						}
					}
				}
				
				bibleEditor.setRoot(root);
			}
		});
		
		this.setLeft(bibleEditor);
	}
	
	private TreeItem<ObservableBibleItem> dragTarget;
	private Border border = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(2, 0, 0, 0)));
	private class ObservableBibleItemTreeCell extends TreeCell<ObservableBibleItem> {
		
		ObservableBibleItem current;
		
		{
			setOnDragDetected(e -> {
				if (!isEmpty() && current != null && current.verse != null) {
					dragTarget = getTreeItem();
					Dragboard db = startDragAndDrop(TransferMode.MOVE);
					ClipboardContent cc = new ClipboardContent();
					cc.put(DataFormat.PLAIN_TEXT, current.verse.getText());
					db.setContent(cc);
					Label label = new Label(getText());
                    new Scene(label);
                    db.setDragView(label.snapshot(null, null));
				}
			});
			
			setOnDragExited(e -> {
				// remove the tree node
//				getTreeItem().getParent().getChildren().remove(dragTarget);
				setBorder(null);
			});
			
			setOnDragEntered(e -> {
				// insert the node before
				setBorder(border);
//				if (!isEmpty() && current != null && current.verse != null) {
//					if (getTreeItem() != dragTarget) {
//						int index = getTreeItem().getParent().getChildren().indexOf(getTreeItem());
//						getTreeItem().getParent().getChildren().add(index, dragTarget);
//					}
//				}
			});
			
			setOnDragOver(e -> {
				if (!isEmpty() && current != null && current.verse != null) {
					Dragboard db = e.getDragboard();
					if (db.hasString()) {
						e.acceptTransferModes(TransferMode.MOVE);
					} else {
						e.consume();
					}
				}
			});
			
			setOnDragDropped(e -> {
//				// get the dragboard
//				Dragboard db = e.getDragboard();
//				String text = db.getString();
//				TreeItem<ObservableBibleItem> me = getTreeItem();
//				ObservableBibleItem itm = me.getValue();
//				TreeItem<ObservableBibleItem> parent = me.getParent();
//				int pos = parent.getChildren().indexOf(me);
//				parent.getChildren().add(pos, new TreeItem<ObservableBibleItem>(
//						new ObservableBibleItem(itm.bible, itm.book, itm.chapter.get(), new Verse(itm.chapter.get(), itm.verse.getVerse(), -1, itm.verse.getOrder(), text))));
//				e.setDropCompleted(true);
			});
		}
		
        @Override
        //by using Number we don't have to parse a String
        protected void updateItem(ObservableBibleItem item, boolean empty) {
            super.updateItem(item, empty);
            current = item;
            if (!empty && item != null) {
	            this.textProperty().unbind();
	            this.textProperty().bind(item.label);
            } else {
            	this.textProperty().unbind();
            	this.setText(null);
            }
        }
    };
	
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
