package org.praisenter.javafx.bible;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.Constants;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

// JAVABUG Dragging to the edge of a scrollable window doesn't scroll it and there's no good way to scroll it manually
final class BibleEditorDragDropManager {
	// FIXME make this css
	private static final Border DRAG_TOP = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(1, 0, 0, 0)));
	private static final Border DRAG_BOTTOM = new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 2, 0, null), null, new BorderWidths(0, 0, 1, 0)));
	private static final Background DRAG_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTBLUE, null, null));
	
	private static final PseudoClass DRAG_OVER_PARENT = PseudoClass.getPseudoClass("drag-over-parent");
	private static final PseudoClass DRAG_OVER_SIBLING_TOP = PseudoClass.getPseudoClass("drag-over-sibling-top");
	private static final PseudoClass DRAG_OVER_SIBLING_BOTTOM = PseudoClass.getPseudoClass("drag-over-sibling-bottom");
	
	private final List<TreeItem<TreeData>> selected;
	private Class<?> selectedType;
	
	// TODO test ordering and saving to ensure that they are saved as listed in the tree
	
	public BibleEditorDragDropManager() {
		this.selected = new ArrayList<TreeItem<TreeData>>();
	}
	
	public void dragDetected(TreeCell<TreeData> cell, MouseEvent e) {
		if (!cell.isEmpty()) {
			TreeView<TreeData> view = cell.getTreeView();
			
			// how much is selected?
			this.selected.addAll(view.getSelectionModel().getSelectedItems());
			int count = this.selected.size();
			
			// are all the selections the same type?
			boolean sameType = true;
			if (count > 0) {
				TreeItem<TreeData> first = this.selected.get(0);
				if (first != null) {
					this.selectedType = first.getValue().getClass();
					for (TreeItem<TreeData> item : this.selected) {
						if (!item.getValue().getClass().equals(this.selectedType)) {
							sameType = false;
							break;
						}
					}
				}
			}
			
			// must be the same type and must have at least one selected
			if (sameType && count > 0) {
				StringBuilder text = new StringBuilder();
				for (TreeItem<TreeData> item : this.selected) {
					TreeData td = item.getValue();
					if (td instanceof BookTreeData) {
						BookTreeData btd = (BookTreeData)td;
						Book book = btd.book;
						text.append(book.getName()).append(Constants.NEW_LINE);
					} else if (td instanceof ChapterTreeData) {
						ChapterTreeData ctd = (ChapterTreeData)td;
						Chapter chapter = ctd.chapter;
						text.append(chapter.getNumber()).append(Constants.NEW_LINE);
					} else if (td instanceof VerseTreeData) {
						VerseTreeData vtd = (VerseTreeData)td;
						Verse verse = vtd.verse;
						text.append(verse.getText()).append(Constants.NEW_LINE);
					}
				}
				
				Dragboard db = cell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
				ClipboardContent cc = new ClipboardContent();
				// we have to put something in there to make sure the d&d works
				cc.put(DataFormat.PLAIN_TEXT, text.toString().trim());
				db.setContent(cc);
				
				Label label = new Label(cell.getText());
		        new Scene(label);
		        db.setDragView(label.snapshot(null, null));
			}
		}
	}
	
	public void dragExited(TreeCell<TreeData> cell, DragEvent e) {
		cell.pseudoClassStateChanged(DRAG_OVER_PARENT, false);
		cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
		cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
	}
	
	public void dragEntered(TreeCell<TreeData> cell, DragEvent e) {
		// nothing to do here
	}
	
	public void dragOver(TreeCell<TreeData> cell, DragEvent e) {
		if (!cell.isEmpty()) {
			TreeItem<TreeData> item = cell.getTreeItem();
			TreeData data = cell.getItem();
			boolean allowed = false;
			
			if (!this.selected.contains(item)) {
				boolean parent = false;
				if (data instanceof ChapterTreeData && VerseTreeData.class.equals(this.selectedType)) {
					allowed = true;
					parent = true;
				} else if (data instanceof VerseTreeData && VerseTreeData.class.equals(this.selectedType)) {
					allowed = true;
				} else if (data instanceof BookTreeData && ChapterTreeData.class.equals(this.selectedType)) {
					allowed = true;
					parent = true;
				} else if (data instanceof ChapterTreeData && ChapterTreeData.class.equals(this.selectedType)) {
					allowed = true;
				} else if (data instanceof BibleTreeData && BookTreeData.class.equals(this.selectedType)) { 
					allowed = true;
					parent = true;
				} else if (data instanceof BookTreeData && BookTreeData.class.equals(this.selectedType)) {
					allowed = true;
				}
				
				if (allowed) {
					e.acceptTransferModes(TransferMode.MOVE);
					
					if (parent) {
//						cell.setBackground(DRAG_BACKGROUND);
						cell.pseudoClassStateChanged(DRAG_OVER_PARENT, true);
					} else {
						if (e.getY() < cell.getHeight() * 0.75) {
//							cell.setBorder(DRAG_TOP);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, true);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, false);
						} else {
//							cell.setBorder(DRAG_BOTTOM);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_BOTTOM, true);
							cell.pseudoClassStateChanged(DRAG_OVER_SIBLING_TOP, false);
						}
					}
				}
			}
		}
	}
	
	public void dragDropped(TreeCell<TreeData> cell, DragEvent e) {
		TreeItem<TreeData> toNode = cell.getTreeItem();
		
		// make sure we don't paste onto ourself
		if (this.selected.contains(toNode)) {
			return;
		}
		
		TreeData data = toNode.getValue();
		boolean after = e.getY() >= cell.getHeight() * 0.75;
		
		// remove all the nodes first so that the indexes don't get jacked
		for (TreeItem<TreeData> item : this.selected) {
			// remove it from the tree
			item.getParent().getChildren().remove(item);
			// remove it from the bible
			TreeData td = item.getValue();
			if (td instanceof VerseTreeData) {
				VerseTreeData vtd = (VerseTreeData)td;
				vtd.chapter.getVerses().remove(vtd.verse);
			} else if (td instanceof ChapterTreeData) {
				ChapterTreeData ctd = (ChapterTreeData)td;
				ctd.book.getChapters().remove(ctd.chapter);
			} else if (td instanceof BookTreeData) {
				BookTreeData btd = (BookTreeData)td;
				btd.bible.getBooks().remove(btd.book);
			}
		}
		
		// compute the index of the target node
		int index = toNode.getParent().getChildren().indexOf(toNode);
		if (after) {
			index++;
		}
		
		// if we drag it to the parent type, then always add the nodes/data to the end
		if (data instanceof ChapterTreeData && VerseTreeData.class.equals(this.selectedType) ||
			data instanceof BookTreeData && ChapterTreeData.class.equals(this.selectedType) ||
			data instanceof BibleTreeData && BookTreeData.class.equals(this.selectedType)) {
			index = -1;
		} else {
			// if we drag it to the same type, then get the parent
			toNode = toNode.getParent();
		}
		
		for (TreeItem<TreeData> item : this.selected) {
			// add the node at the target
			if (index < 0) {
				toNode.getChildren().add(item);
			} else {
				toNode.getChildren().add(index, item);
			}
			
			// add the data
			if (VerseTreeData.class.equals(this.selectedType)) {
				// moving verses onto a chapter
				ChapterTreeData ctd = (ChapterTreeData)toNode.getValue();
				Verse verse = ((VerseTreeData)item.getValue()).verse;
				if (index < 0) {
					ctd.chapter.getVerses().add(verse);
				} else {
					ctd.chapter.getVerses().add(index, verse);
				}
			} else if (ChapterTreeData.class.equals(this.selectedType)) {
				// moving chapters onto a book
				BookTreeData btd = (BookTreeData)toNode.getValue();
				Chapter chapter = ((ChapterTreeData)item.getValue()).chapter;
				if (index < 0) {
					btd.book.getChapters().add(chapter);
				} else {
					btd.book.getChapters().add(index, chapter);
				}
			} else if (BookTreeData.class.equals(this.selectedType)) {
				// moving books onto a bible
				BibleTreeData btd = (BibleTreeData)toNode.getValue();
				Book book = ((BookTreeData)item.getValue()).book;
				if (index < 0) {
					btd.bible.getBooks().add(book);
				} else {
					btd.bible.getBooks().add(index, book);
				}
			}
			
			if (index >= 0) {
				index++;
			}
		}
		
		e.setDropCompleted(true);
	}
	
	public void dragDone(TreeCell<TreeData> cell, DragEvent e) {
		// regardless if the transfer was successful
		// clear the local state
		this.selected.clear();
		this.selectedType = null;
		
		// this indicates the transfer was successful
		if (e.getTransferMode() != null) {
			cell.getTreeView().getSelectionModel().clearSelection();
		}
	}
}
