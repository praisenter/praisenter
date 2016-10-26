package org.praisenter.javafx.bible;

import java.util.Collections;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
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

// JAVABUG Dragging to the edge of a scrollable window doesn't scroll it and there's no good way to scroll it manually
final class BibleEditorEventManager {
	private static final Border DRAG_TOP = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(1, 0, 0, 0)));
	private static final Border DRAG_BOTTOM = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(0, 0, 1, 0)));
	
	// drag n' drop

	// FIXME drag drop multiple
	// FIXME drag drop book number not sorted?
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
		
		cell.getTreeView().getSelectionModel().clearSelection();
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
    		
//    		if (type0 == 3 && dragged != toNode) {
//    			TreeItem<TreeData> chapterNode = toNode.getParent();
//    			reorderVerse(chapterNode, toNode);
//    		} else if (type0 == 2) {
//    			TreeItem<TreeData> bookNode = toNode.getParent();
//    			reorderChapter(bookNode, toNode);
//    		} else if (type0 == 1) {
//    			TreeItem<TreeData> bibleNode = toNode.getParent();
//    			reorderBook(bibleNode, toNode);
//    		}
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
