package org.praisenter.javafx.bible;

import java.util.Collections;
import java.util.Optional;

import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

final class BibleEditorEventManager {
	private static final Border DRAG_TOP = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(1, 0, 0, 0)));
	private static final Border DRAG_BOTTOM = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(0, 0, 1, 0)));
	
	// context menu

	private TreeItem<TreeData> copyCutTarget;
	
	public void add(TreeItem<TreeData> item) {
		int type = getType(item.getValue());
		if (type == 3) {
			// add new verse
			VerseTreeData vd = (VerseTreeData)item.getValue();
			short number = vd.chapter.getMaxVerseNumber();
			Verse verse = new Verse(++number, "New verse");
			vd.chapter.getVerses().add(verse);
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new VerseTreeData(vd.bible, vd.book, vd.chapter, verse));
			item.getParent().getChildren().add(newItem);
		} else if (type == 2) {
			// add new verse
			ChapterTreeData cd = (ChapterTreeData)item.getValue();
			short number = cd.chapter.getMaxVerseNumber();
			Verse verse = new Verse(++number, "New verse");
			cd.chapter.getVerses().add(verse);
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new VerseTreeData(cd.bible, cd.book, cd.chapter, verse));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (type == 1) {
			// add new chapter
			BookTreeData bd = (BookTreeData)item.getValue();
			short number = bd.book.getMaxChapterNumber();
			Chapter chapter = new Chapter(++number);
			bd.book.getChapters().add(chapter);
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new ChapterTreeData(bd.bible, bd.book, chapter));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		} else if (type == 0) {
			// add new book
			BibleTreeData bd = (BibleTreeData)item.getValue();
			short number = bd.bible.getMaxBookNumber();
			Book book = new Book("New book", ++number);
			bd.bible.getBooks().add(book);
			TreeItem<TreeData> newItem = new TreeItem<TreeData>(new BookTreeData(bd.bible, book));
			item.getChildren().add(newItem);
			item.setExpanded(true);
		}
	}
	
	public void delete(TreeItem<TreeData> item) {
		int type = getType(item.getValue());
		if (type != 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this?", ButtonType.OK, ButtonType.CANCEL);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// delete the node
				item.getParent().getChildren().remove(item);
				// update the data model
				if (type == 3) {
					VerseTreeData verse = (VerseTreeData)item.getValue();
					verse.chapter.getVerses().remove(verse.verse);
				} else if (type == 2) {
					ChapterTreeData chapter = (ChapterTreeData)item.getValue();
					chapter.book.getChapters().remove(chapter.chapter);
				} else if (type == 1) {
					BookTreeData book = (BookTreeData)item.getValue();
					book.bible.getBooks().remove(book.book);
				}
			}
		}
	}
	
	public void copy(TreeItem<TreeData> item) {
		if (item == null) return;
		
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, item.getValue().label.get());
		clipboard.setContent(content);
		
		this.copyCutTarget = item;
	}
	
	public void cut(TreeItem<TreeData> item) {
		this.copyCutTarget = item;
		
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, item.getValue().label.get());
		clipboard.setContent(content);
		
		item.getParent().getChildren().remove(item);
	}
	
	public void paste(TreeItem<TreeData> item) {
		int type0 = getType(item.getValue());
		if (copyCutTarget != null) {
			int type1 = getType(copyCutTarget.getValue());
			if (type0 == 0 && type1 == 1) {
				// paste a copy of the book
				Book book = ((BookTreeData)this.copyCutTarget.getValue()).book.copy();
				BibleTreeData bible = (BibleTreeData)item.getValue();
				item.getChildren().add(new TreeItem<TreeData>(new BookTreeData(bible.bible, book)));
			} else if (type0 == 1 && type1 == 2) {
				// paste a copy of the chapter
				Chapter chapter = ((ChapterTreeData)this.copyCutTarget.getValue()).chapter.copy();
				BookTreeData book = (BookTreeData)item.getValue();
				item.getChildren().add(new TreeItem<TreeData>(new ChapterTreeData(book.bible, book.book, chapter)));
			} else if (type0 == 2 && type1 == 3) {
				// paste a copy of the verse
				Verse verse = ((VerseTreeData)this.copyCutTarget.getValue()).verse.copy();
				ChapterTreeData chapter = (ChapterTreeData)item.getValue();
				item.getChildren().add(new TreeItem<TreeData>(new VerseTreeData(chapter.bible, chapter.book, chapter.chapter, verse)));
			}
		}
	}
	
	public boolean canPaste(TreeItem<TreeData> item) {
		if (this.copyCutTarget == null) {
			return false;
		}
		int type0 = getType(item.getValue());
		int type1 = getType(copyCutTarget.getValue());
		if (type0 == 0 && type1 == 1) {
			return true;
		} else if (type0 == 1 && type1 == 2) {
			return true;
		} else if (type0 == 2 && type1 == 3) {
			return true;
		}
		return false;
	}
	
	// drag n' drop

	private TreeItem<TreeData> dragTarget;
	private int dragTargetType;
	private Border border;
	
	public void dragDetected(TreeCell<TreeData> cell, MouseEvent e) {
		if (!cell.isEmpty()) {
			dragTarget = cell.getTreeItem();
			dragTargetType = getType(cell.getItem());
			Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent cc = new ClipboardContent();
			cc.put(DataFormat.PLAIN_TEXT, dragTarget.getValue().label.get());
			db.setContent(cc);
			Label label = new Label(cell.getText());
	        new Scene(label);
	        db.setDragView(label.snapshot(null, null));
		}
	}
	
	public void dragExited(TreeCell<TreeData> cell, DragEvent e) {
		cell.setBorder(border);
	}
	
	public void dragEntered(TreeCell<TreeData> cell, DragEvent e) {
		border = cell.getBorder();
	}
	
	public void dragOver(TreeCell<TreeData> cell, DragEvent e) {
		if (!cell.isEmpty()) {
			int type = getType(cell.getItem());
			if (type == this.dragTargetType) {
				Dragboard db = e.getDragboard();
				if (db.hasString()) {
					e.acceptTransferModes(TransferMode.MOVE);
				} else {
					e.consume();
				}
				
				if (e.getY() < cell.getHeight() * 0.75) {
					cell.setBorder(DRAG_TOP);
				} else {
					cell.setBorder(DRAG_BOTTOM);
				}
			}
		}
	}
	
	public void dragDropped(TreeCell<TreeData> cell, DragEvent e) {
		TreeItem<TreeData> toNode = cell.getTreeItem();
		TreeItem<TreeData> node = this.dragTarget;
		
		determineDragAction(node, toNode, e.getY() >= cell.getHeight() * 0.75);
		
		cell.getTreeView().getSelectionModel().select(node);
	}
	
	// helpers
	
    static int getType(TreeData item) {
    	if (item == null) return -1;
    	
    	if (item instanceof BookTreeData) {
			return 1;
		} else if (item instanceof ChapterTreeData) {
			return 2;
		} else if (item instanceof VerseTreeData) {
			return 3;
		} else {
			return 0;
		}
    }
    
    private void determineDragAction(TreeItem<TreeData> dragged, TreeItem<TreeData> toNode, boolean after) {
    	int type0 = getType(dragged.getValue());
    	int type1 = getType(toNode.getValue());
    	
    	if (type0 == type1 && dragged != toNode) {
        	// update the nodes
        	// remove it from where it is
        	dragged.getParent().getChildren().remove(dragged);
        	// add it to where it should be
        	int index = toNode.getParent().getChildren().indexOf(toNode);
        	if (after) {
        		index++;
        	}
        	toNode.getParent().getChildren().add(index, dragged);
    		
    		if (type0 == 3 && dragged != toNode) {
    			TreeItem<TreeData> chapterNode = toNode.getParent();
    			reorderVerse(chapterNode, toNode);
    		} else if (type0 == 2) {
    			TreeItem<TreeData> bookNode = toNode.getParent();
    			reorderChapter(bookNode, toNode);
    		} else if (type0 == 1) {
    			TreeItem<TreeData> bibleNode = toNode.getParent();
    			reorderBook(bibleNode, toNode);
    		}
    	}
    }
    
    private void reorderVerse(TreeItem<TreeData> chapterNode, TreeItem<TreeData> to) {
    	ChapterTreeData chapter = (ChapterTreeData)chapterNode.getValue();
    	
    	// update the data
    	// update the verse numbers
    	for (short i = 1; i < to.getParent().getChildren().size() + 1; i++) {
    		TreeItem<TreeData> node = to.getParent().getChildren().get(i - 1);
    		VerseTreeData data = (VerseTreeData)node.getValue();
    		data.verse.setNumber(i);
    		node.getValue().update();
    	}
    	
    	// sort
    	Collections.sort(chapter.chapter.getVerses());
    }
    
    private void reorderChapter(TreeItem<TreeData> bookNode, TreeItem<TreeData> to) {
    	BookTreeData book = (BookTreeData)bookNode.getValue();
    	
    	// update the data
    	// update the verse numbers
    	for (short i = 1; i < to.getParent().getChildren().size() + 1; i++) {
    		TreeItem<TreeData> node = to.getParent().getChildren().get(i - 1);
    		ChapterTreeData data = (ChapterTreeData)node.getValue();
    		data.chapter.setNumber(i);
    		node.getValue().update();
    	}
    	
    	// sort
    	Collections.sort(book.book.getChapters());
    }
    
    private void reorderBook(TreeItem<TreeData> bibleNode, TreeItem<TreeData> to) {
    	BibleTreeData bible = (BibleTreeData)bibleNode.getValue();
    	
    	// update the data
    	// update the verse numbers
    	for (short i = 1; i < to.getParent().getChildren().size() + 1; i++) {
    		TreeItem<TreeData> node = to.getParent().getChildren().get(i - 1);
    		BookTreeData data = (BookTreeData)node.getValue();
    		data.book.setNumber(i);
    		node.getValue().update();
    	}
    	
    	// sort
    	Collections.sort(bible.bible.getBooks());
    }
}
